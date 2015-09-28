package com.adms.web.base.confirmation.download;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.StreamedContent;

import com.adms.cs.service.ConfirmationRecordService;
import com.adms.entity.cs.ConfirmationRecord;
import com.adms.util.FileTransfer;
import com.adms.util.MessageUtils;
import com.adms.utils.DateUtil;
import com.adms.web.base.bean.BaseBean;

@ManagedBean
@ViewScoped
public class FileDownloadView extends BaseBean {

	private static final long serialVersionUID = -259661052802018706L;
	
	@ManagedProperty(value="#{confirmationRecordService}")
	private ConfirmationRecordService confirmationRecordService;
	
	private final String MS_SQL_DATE_PATTERN = "yyyy-MM-dd";
	private final String DISPLAY_DATE_PATTERN = "dd-MMM-yyyy";
	private final String SIMPLE_DATE_PATTERN = "yyyyMMdd";
	
	private final String EXCEL_NAME_PATTERN = "ADAMS-TVD Confirmation Call Report as Settle Date on #fdd-MMM-yyyy to #tdd-MMM-yyyy";
	
	private final String TEMPLATE_EXCEL_CONF_REPORT_PATH = "/template/template-conf-call-report.xls";
	private final String FWD_COMP_NAME = "FWD LIFE INSURANCE PUBLIC COMPANY LIMITED.";
	private final String PARAM_COMPANY = "#company";
	private final String PARAM_ABBR_NAME = "#abbrName";
	private final String PARAM_CYCLE_FROM = "#cycleFrom";
	private final String PARAM_CYCLE_TO = "#cycleTo";
	
	private List<SelectItem> selectionCycleFrom;
	private List<SelectItem> selectionCycleTo;
	private List<String> cycleTos;

	private String selectedDateFrom;
	private String selectedDateTo;
	
	private Date maxCycleFrom;
	private Date maxCycleTo;
	
	private StreamedContent file;
	
	@PostConstruct
	private void init() {
		selectedDateFrom = null;
		selectedDateTo = null;
		try {
			prepareDateRange();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doDownload() {
		if(StringUtils.isBlank(selectedDateFrom) || StringUtils.isBlank(selectedDateTo)) {
			MessageUtils.getInstance().addErrorMessage("msgDialogDL", "Please select date");
			return;
		}
		
		if(selectedDateFrom.compareTo(selectedDateTo) < 0) {
			try {
				Date cycleFrom = DateUtil.convStringToDate(SIMPLE_DATE_PATTERN, selectedDateFrom);
				Date cycleTo = DateUtil.convStringToDate(SIMPLE_DATE_PATTERN, selectedDateTo);
				
				DetachedCriteria criteria = DetachedCriteria.forClass(ConfirmationRecord.class);
				criteria.add(Restrictions.between("cycleFrom", cycleFrom, cycleTo));
				criteria.add(Restrictions.between("cycleTo", cycleFrom, cycleTo));
				criteria.addOrder(Order.asc("cycleFrom"));
				criteria.addOrder(Order.asc("id"));
				List<ConfirmationRecord> list = confirmationRecordService.findByCriteria(criteria);
				
				Workbook wb = createExcel(list);
				
				if(wb != null) {
					wb.removeSheetAt(0);
					FileTransfer ft = new FileTransfer();
					String fileName = EXCEL_NAME_PATTERN + TEMPLATE_EXCEL_CONF_REPORT_PATH.substring(TEMPLATE_EXCEL_CONF_REPORT_PATH.lastIndexOf("."), TEMPLATE_EXCEL_CONF_REPORT_PATH.length());
					fileName = fileName.replaceAll("#fdd-MMM-yyyy", DateUtil.convDateToString(DISPLAY_DATE_PATTERN, maxCycleFrom));
					fileName = fileName.replaceAll("#tdd-MMM-yyyy", DateUtil.convDateToString(DISPLAY_DATE_PATTERN, maxCycleTo));
					System.out.println("File Name: " + fileName);
					ft.fileDownload(fileName, wb);
				}
				
				init();
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			MessageUtils.getInstance().addErrorMessage("msgDialogDL", "Invalid date");
		}
	}
	
	private Workbook createExcel(List<ConfirmationRecord> dataList) {
		InputStream templateExcelConf = null;
		Workbook wb = null;
		int contentRow = 5;
		try {
			Date currentCycle = null;
			templateExcelConf = Thread.currentThread().getContextClassLoader().getResourceAsStream(TEMPLATE_EXCEL_CONF_REPORT_PATH);
			wb = WorkbookFactory.create(templateExcelConf);
			
			Sheet tempSheet = wb.getSheetAt(0);
			Sheet nSheet = null;
			
//			Excel Processing below here
			for(ConfirmationRecord conf : dataList) {
				if(maxCycleFrom == null || maxCycleFrom.compareTo(conf.getCycleFrom()) < 0) maxCycleFrom = conf.getCycleFrom();
				if(maxCycleTo == null || maxCycleTo.compareTo(conf.getCycleTo()) < 0) maxCycleTo = conf.getCycleTo();
				
				if(currentCycle == null || currentCycle.compareTo(conf.getCycleFrom()) != 0) {
					currentCycle = conf.getCycleFrom();
					nSheet = wb.createSheet(getSheetName(conf.getCycleFrom(), conf.getCycleTo()));
					headerProcess(tempSheet, nSheet, conf.getCycleFrom(), conf.getCycleTo());
					contentRow = 5;
				}
				
				contentProcess(tempSheet, nSheet, contentRow, conf);
				contentRow++;
			}
			return wb;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void headerProcess(Sheet tSheet, Sheet nSheet, Date cycleFrom, Date cycleTo) throws Exception {
		Cell tCell = tSheet.getRow(0).getCell(0, Row.CREATE_NULL_AS_BLANK);
		Cell nCell = nSheet.createRow(0).createCell(0, tCell.getCellType());
		nCell.setCellValue(tCell.getStringCellValue().replaceAll(PARAM_COMPANY, FWD_COMP_NAME));
		nCell.setCellStyle(tCell.getCellStyle());
		
		tCell = tSheet.getRow(1).getCell(0, Row.CREATE_NULL_AS_BLANK);
		nCell = nSheet.createRow(1).createCell(0, tCell.getCellType());
		nCell.setCellValue(tCell.getStringCellValue().replaceAll(PARAM_ABBR_NAME, "ADAMS-TVD")
				.replaceAll(PARAM_CYCLE_FROM, DateUtil.convDateToString(DISPLAY_DATE_PATTERN, cycleFrom))
				.replaceAll(PARAM_CYCLE_TO, DateUtil.convDateToString(DISPLAY_DATE_PATTERN, cycleTo)));
		nCell.setCellStyle(tCell.getCellStyle());
		
		Row tRow = tSheet.getRow(4);
		Row nRow = nSheet.createRow(4);
		
		for(int c = 0; c < tRow.getLastCellNum(); c++) {
			tCell = tRow.getCell(c, Row.CREATE_NULL_AS_BLANK);
			nCell = nRow.createCell(c, tCell.getCellType());
			nCell.setCellValue(tCell.getStringCellValue());
			nCell.setCellStyle(tCell.getCellStyle());
		}
		
		for(int i = 0; i < tSheet.getNumMergedRegions(); i++) {
			CellRangeAddress mergedRegion = tSheet.getMergedRegion(i);
			nSheet.addMergedRegion(mergedRegion);
		}
	}
	
	private void contentProcess(Sheet tSheet, Sheet nSheet, int contentRow, ConfirmationRecord data) throws Exception {
		int tempRow = -1;
		if(data.getAction() == null) {
			tempRow = 7;
		} else if(data.getAction().getParamKey().equals("CONFIRMATION_LOG_ACTION_ACCEPT")) {
			tempRow = 5;
 		} else {
 			tempRow = 6;
 		}
		Row tRow = tSheet.getRow(tempRow);
		Row row = nSheet.createRow(contentRow);
		for(int c = 0; c < tRow.getLastCellNum(); c++) {
			Cell tCell = tRow.getCell(c);
			Cell cell = row.createCell(c, tCell.getCellType());
			setValue(cell, data);
			cell.setCellStyle(tCell.getCellStyle());
			cell.getRow().getSheet().setColumnWidth(c, tRow.getSheet().getColumnWidth(c));
		}
		
	}

//	Setup column data here
	private void setValue(Cell cell, ConfirmationRecord data) {
		switch(cell.getColumnIndex()) {
		case 0 : cell.setCellValue(cell.getRowIndex() - 4); break;
		case 1 : cell.setCellValue(DateUtil.convDateToString("yyyy", data.getApplicationDate())); break;
		case 2 : cell.setCellValue(DateUtil.convDateToString("M", data.getApplicationDate())); break;
		case 3 : cell.setCellValue(DateUtil.convDateToString("d", data.getApplicationDate())); break;
		case 4 : cell.setCellValue(DateUtil.convDateToString("yyyy", data.getSettleDate())); break;
		case 5 : cell.setCellValue(DateUtil.convDateToString("M", data.getSettleDate())); break;
		case 6 : cell.setCellValue(DateUtil.convDateToString("d", data.getSettleDate())); break;
		case 7 : cell.setCellValue(data.getPolicyNo()); break;
		case 8 : cell.setCellValue(data.getPlanCode()); break;
		case 9 : cell.setCellValue(data.getInsuredName()); break;
		case 10 : cell.setCellValue(data.getModalPremium().doubleValue()); break;
		case 11 : cell.setCellValue(data.getApe().doubleValue()); break;
		case 12 : cell.setCellValue(data.getSumInsured().doubleValue()); break;
		case 13 : cell.setCellValue(data.getMode()); break;
		case 14 : cell.setCellValue(data.getStatus()); break;
		case 15 : cell.setCellValue(data.getUnitCode()); break;
		case 16 : cell.setCellValue(data.getBankCard()); break;
		case 17 : cell.setCellValue(data.getCreditCardNo()); break;
		case 18 : cell.setCellValue(data.getExpiredDate()); break;
		case 19 : cell.setCellValue(data.getBirthDate() != null ? DateUtil.convDateToString("yyyy", data.getBirthDate()) : null); break;
		case 20 : cell.setCellValue(data.getBirthDate() != null ? DateUtil.convDateToString("M", data.getBirthDate()) : null); break;
		case 21 : cell.setCellValue(data.getBirthDate() != null ? DateUtil.convDateToString("d", data.getBirthDate()) : null); break;
		case 22 : cell.setCellValue(data.getCitizenId()); break;
		case 23 : cell.setCellValue(data.getAddress1()); break;
		case 24 : cell.setCellValue(data.getAddress2()); break;
		case 25 : cell.setCellValue(data.getAddress3()); break;
		case 26 : cell.setCellValue(data.getProvince()); break;
		case 27 : cell.setCellValue(data.getPostCode()); break;
		case 28 : cell.setCellValue(data.getTel1()); break;
		case 29 : cell.setCellValue(data.getTel2()); break;
		case 30 : cell.setCellValue(data.getTel3()); break;
		case 31 : cell.setCellValue(data.getMobile1()); break;
		case 32 : cell.setCellValue(data.getMobile2()); break;
		case 33 : cell.setCellValue(data.getTsrName()); break;
		case 34 : cell.setCellValue(data.getAction() != null ? data.getAction().getParamValue() : null); break;
		case 35 : cell.setCellValue(data.getRemark()); break;
		default: break;
		}
	}
	
	private String getSheetName(Date cycleFrom, Date cycleTo) {
		String sheetName = "";
		Calendar c1 = Calendar.getInstance();
		c1.setTime(cycleFrom);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(cycleTo);
		
		if(c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)) {
			sheetName = DateUtil.convDateToString("dd", cycleFrom);
		} else if(c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
			sheetName = DateUtil.convDateToString("dd MMM", cycleFrom);
		} else {
			sheetName = DateUtil.convDateToString("dd MMM yyyy", cycleFrom);
		}
		sheetName += "-" + DateUtil.convDateToString("dd MMM yyyy", cycleTo);
		return sheetName;
	}
	
	public void cycleListener() throws ParseException {
		selectionCycleTo = new ArrayList<>();
		if(!StringUtils.isBlank(selectedDateFrom)) {
			selectionCycleTo.add(new SelectItem(null, "Please Select"));
			for(String cycleTo : cycleTos) {
				if(cycleTo.compareTo(selectedDateFrom) > 0) {
					selectionCycleTo.add(new SelectItem(cycleTo, DateUtil.convDateToString(DISPLAY_DATE_PATTERN, DateUtil.convStringToDate(SIMPLE_DATE_PATTERN, cycleTo))));
				}
			}
		}
	}
	
	public void prepareDateRange() throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(ConfirmationRecord.class);
		ProjectionList pjl = Projections.projectionList();
		pjl.add(Projections.property("cycleFrom"));
		pjl.add(Projections.property("cycleTo"));
		criteria.setProjection(Projections.distinct(pjl));
		
		List<?> list = confirmationRecordService.findByCriteria(criteria);
		Object[] objs = list.toArray();
		selectionCycleFrom = new ArrayList<>();
		selectionCycleFrom.add(new SelectItem(null, "Please Select"));
		cycleTos = new ArrayList<>();
//		selectionCycleTo = new ArrayList<>();
//		selectionCycleTo.add(new SelectItem(null, "Please Select"));
		
		for(Object obj : objs) {
			if(obj instanceof Object[]) {
				Object[] innerObjs = (Object[]) obj;
				if(innerObjs.length == 2) {
					Date dFrom = DateUtil.convStringToDate(MS_SQL_DATE_PATTERN, innerObjs[0].toString());
					Date dTo = DateUtil.convStringToDate(MS_SQL_DATE_PATTERN, innerObjs[1].toString());
					selectionCycleFrom.add(new SelectItem(DateUtil.convDateToString(SIMPLE_DATE_PATTERN, dFrom), DateUtil.convDateToString(DISPLAY_DATE_PATTERN, dFrom)));
					cycleTos.add(DateUtil.convDateToString(SIMPLE_DATE_PATTERN, dTo));
//					selectionCycleTo.add(new SelectItem(DateUtil.convDateToString(SIMPLE_DATE_PATTERN, dTo), DateUtil.convDateToString(DISPLAY_DATE_PATTERN, dTo)));
				} else {
					System.err.println("ERR: obj[] length not eq to 2");
				}
			}
		}
	}
	
	public void setConfirmationRecordService(ConfirmationRecordService confirmationRecordService) {
		this.confirmationRecordService = confirmationRecordService;
	}

	public String getSelectedDateFrom() {
		return selectedDateFrom;
	}

	public void setSelectedDateFrom(String selectedDateFrom) {
		this.selectedDateFrom = selectedDateFrom;
	}

	public String getSelectedDateTo() {
		return selectedDateTo;
	}

	public void setSelectedDateTo(String selectedDateTo) {
		this.selectedDateTo = selectedDateTo;
	}

	public List<SelectItem> getSelectionCycleFrom() {
		return selectionCycleFrom;
	}

	public void setSelectionCycleFrom(List<SelectItem> selectionCycleFrom) {
		this.selectionCycleFrom = selectionCycleFrom;
	}

	public List<SelectItem> getSelectionCycleTo() {
		return selectionCycleTo;
	}

	public void setSelectionCycleTo(List<SelectItem> selectionCycleTo) {
		this.selectionCycleTo = selectionCycleTo;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}
	
}
