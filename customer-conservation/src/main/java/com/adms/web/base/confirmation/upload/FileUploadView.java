package com.adms.web.base.confirmation.upload;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.PhaseId;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.adms.cs.service.ConfirmationRecordService;
import com.adms.entity.cs.ConfirmationRecord;
import com.adms.imex.excelformat.DataHolder;
import com.adms.imex.excelformat.ExcelFormat;
import com.adms.util.MessageUtils;
import com.adms.util.PropertyConfig;
import com.adms.utils.DateUtil;
import com.adms.web.base.bean.BaseBean;

@ManagedBean
@ViewScoped
public class FileUploadView extends BaseBean {

	private static final long serialVersionUID = 7287640969458666947L;
	private String userLogin = "System Admin";
	
	private final String FILE_FORMAT_PATH = "fileformat/fwd-confirmation-format.xml";

	private final String FILE_DATE_PATTERN = "yyyyMd";

	@ManagedProperty(value="#{confirmationRecordService}")
	private ConfirmationRecordService confirmationRecordService;
	
	private UploadedFile uploadedFile;
	private String excelPwd;
	private boolean needPwd;
	
	@PostConstruct
	private void init() {
		uploadedFile = null;
		needPwd = false;
	}
	
	public void handleFileUpload(FileUploadEvent event) throws Exception {
		try {
			if (!PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
				event.setPhaseId(PhaseId.INVOKE_APPLICATION);
				event.queue();
			} else {
				if (event.getFile() != null) {
					uploadedFile = event.getFile();
					doImportToDB();
					init();
				}
			}
		} catch(Exception e) {
			MessageUtils.getInstance().addErrorMessage(null, "Please, contract Administrator. >> " + e.getMessage());
			RequestContext.getCurrentInstance().update("frmMain");
			throw e;
		}
	}
	
	private void doImportToDB() {
		//TODO: Import file processing here
		InputStream fileFormatStream = null;
		InputStream wbStream = null;
		ExcelFormat ef = null;
		int count = 0;
		
		try {
			fileFormatStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE_FORMAT_PATH);
			if(needPwd) {
				if(StringUtils.isBlank(excelPwd)) {
					MessageUtils.getInstance().addErrorMessage("msgUpload", "Password is empty.");
					return;
				}
				
				Workbook wb = null;
				try {
					if(uploadedFile.getFileName().endsWith(".xlsx")) {
						wb = xlsxDecryptor(uploadedFile.getInputstream());
					} else {
						wb = xlsDecryptor(uploadedFile.getInputstream());
					}
				} catch(Exception e) {
					MessageUtils.getInstance().addErrorMessage("msgUpload", "Invalid Password for Decryption");
				}
				if(wb != null) wb.close();
			}
			wbStream = uploadedFile.getInputstream();
			ef = new ExcelFormat(fileFormatStream);
			DataHolder wbHolder = null;
			try {
			wbHolder = ef.readExcel(wbStream);
			} catch(Exception e) {
				MessageUtils.getInstance().addErrorMessage("msgUpload", "'" + uploadedFile.getFileName() + "' cannot opened");
				return;
			}
			DataHolder sheetHolder = wbHolder.get(wbHolder.getSheetNameByIndex(0));
			
//			String companyName = sheetHolder.get("companyName").getStringValue();
			String periodTxt = sheetHolder.get("periodTxt").getStringValue();
			List<String> periodDates = getDateFromString(periodTxt);
			
			if(periodDates.isEmpty()) {
				MessageUtils.getInstance().addWarnMessage("msgUpload", "Period date not found on sheet");
				return;
			}
			
//			/* Check is these period already uploaded */
			if(validatePeriodData(DateUtil.convStringToDate("dd-MMM-yyyy", periodDates.get(0)), DateUtil.convStringToDate("dd-MMM-yyyy", periodDates.get(1)))) {
				List<DataHolder> dataList = sheetHolder.getDataList("dataList");
				for(DataHolder data : dataList) {
					saveData(data, periodDates.get(0), periodDates.get(1));
					count++;
				}
				
				MessageUtils.getInstance().addInfoMessage("msgUpload", "" + count + " records have been uploaded.");
			} else {
				MessageUtils.getInstance().addWarnMessage("msgUpload", "These datas already uploaded");
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try { fileFormatStream.close();} catch(Exception e) {}
			try { wbStream.close();} catch(Exception e) {}
		}
	}
	
	private boolean validatePeriodData(Date cycleFrom, Date cycleTo) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(ConfirmationRecord.class);
		criteria.add(Restrictions.eq("cycleFrom", cycleFrom));
		criteria.add(Restrictions.eq("cycleTo", cycleTo));
		List<?> list = confirmationRecordService.findByCriteria(criteria);
		if(list.isEmpty()) {
			return true;
		}
		return false;
	}
	
	private Workbook xlsDecryptor(InputStream workbookStream) throws IOException {
		BufferedInputStream bufferInput = new BufferedInputStream(workbookStream);            
		POIFSFileSystem poiFileSystem = new POIFSFileSystem(bufferInput);            
		Biff8EncryptionKey.setCurrentUserPassword(this.excelPwd); 
		return new HSSFWorkbook(poiFileSystem, true);
	}
	
	private Workbook xlsxDecryptor(InputStream workbookStream) throws GeneralSecurityException, IOException {
		POIFSFileSystem fs = new POIFSFileSystem(workbookStream);
	    EncryptionInfo info = new EncryptionInfo(fs);
	    Decryptor d = Decryptor.getInstance(info);
	    d.verifyPassword(this.excelPwd);
	    return new XSSFWorkbook(d.getDataStream(fs));
	}
	
	private ConfirmationRecord saveData(DataHolder data, String strCycleFrom, String strCycleTo) throws Exception {
		ConfirmationRecord cmr = null;
		
		Date cycleFrom = DateUtil.convStringToDate("dd-MMM-yyyy", strCycleFrom);
		Date cycleTo = DateUtil.convStringToDate("dd-MMM-yyyy", strCycleTo);
		Date appDate = DateUtil.convStringToDate(FILE_DATE_PATTERN
				, data.get("appDateYear").getStringValue() 
				+ data.get("appDateMonth").getStringValue() 
				+ data.get("appDateDay").getStringValue());
		Date settleDate = DateUtil.convStringToDate(FILE_DATE_PATTERN
				, data.get("settleYear").getStringValue()
				+ data.get("settleMonth").getStringValue()
				+ data.get("settleDay").getStringValue());
		String policyNo = data.get("policyNo").getStringValue();
		String planCode = data.get("planCode").getStringValue();
		String insuredName = data.get("insuredName").getStringValue();
		BigDecimal modalPremium = data.get("modalPremium").getDecimalValue();
		BigDecimal ape = data.get("ape").getDecimalValue();
		BigDecimal sumInsured = data.get("sumInsured").getDecimalValue();
		String mode = data.get("mode").getStringValue();
		String status = data.get("status").getStringValue();
		String unitCode = data.get("unitCode").getValue() == null ? null : data.get("unitCode").getStringValue();
		String bankCard = data.get("bankCard").getValue() == null ? null : data.get("bankCard").getStringValue();
		String creditCardNo = data.get("creditCardNo").getValue() != null && !StringUtils.isBlank(data.get("creditCardNo").getStringValue()) ? data.get("creditCardNo").getStringValue() : null;
		String expiredDate = data.get("expiredDate").getValue() == null ? null : data.get("expiredDate").getStringValue();
		Date birthDate = data.get("birthdayYear").getValue() == null ? null : DateUtil.convStringToDate(FILE_DATE_PATTERN
				, data.get("birthdayYear").getStringValue() 
				+ data.get("birthdayMonth").getStringValue() 
				+ data.get("birthdayDay").getStringValue());
		String citizenId = data.get("citizenId").getStringValue();
		String address1 = data.get("address1").getValue() == null ? null : data.get("address1").getStringValue();
		String address2 = data.get("address2").getValue() == null ? null : data.get("address2").getStringValue();
		String address3 = data.get("address1").getValue() == null ? null : data.get("address3").getStringValue();
		String province = data.get("province").getValue() == null ? null : data.get("province").getStringValue();
		String postCode = data.get("postCode").getValue() == null ? null : data.get("postCode").getStringValue();
		String tel1 = data.get("tel1").getValue() != null && !StringUtils.isBlank(data.get("tel1").getStringValue()) ? data.get("tel1").getStringValue() : null;
		String tel2 = data.get("tel2").getValue() != null && !StringUtils.isBlank(data.get("tel2").getStringValue()) ? data.get("tel2").getStringValue() : null;
		String tel3 = data.get("tel3").getValue() != null && !StringUtils.isBlank(data.get("tel3").getStringValue()) ? data.get("tel3").getStringValue() : null;
		String mobile1 = data.get("mobile1").getValue() != null && !StringUtils.isBlank(data.get("mobile1").getStringValue()) ? data.get("mobile1").getStringValue() : null;
		String mobile2 = data.get("mobile2").getValue() != null && !StringUtils.isBlank(data.get("mobile2").getStringValue()) ? data.get("mobile2").getStringValue() : null;
		String tsrName = data.get("tsrName").getValue() != null && !StringUtils.isBlank(data.get("tsrName").getStringValue()) ? data.get("tsrName").getStringValue() : null;
		
		cmr = new ConfirmationRecord();
		cmr.setCycleFrom(cycleFrom);
		cmr.setCycleTo(cycleTo);
		cmr.setApplicationDate(appDate);
		cmr.setSettleDate(settleDate);
		cmr.setxReference(null);
		cmr.setPolicyNo(policyNo);
		cmr.setPlanCode(planCode);
		cmr.setInsuredName(insuredName);
		cmr.setModalPremium(modalPremium);
		cmr.setApe(ape);
		cmr.setSumInsured(sumInsured);
		cmr.setMode(mode);
		cmr.setStatus(status);
		cmr.setUnitCode(unitCode);
		cmr.setBankCard(bankCard);
		cmr.setCreditCardNo(creditCardNo);
		cmr.setExpiredDate(expiredDate);
		cmr.setBirthDate(birthDate);
		cmr.setCitizenId(citizenId);
		cmr.setAddress1(address1);
		cmr.setAddress2(address2);
		cmr.setAddress3(address3);
		cmr.setProvince(province);
		cmr.setPostCode(postCode);
		cmr.setTel1(tel1);
		cmr.setTel2(tel2);
		cmr.setTel3(tel3);
		cmr.setMobile1(mobile1);
		cmr.setMobile2(mobile2);
		cmr.setTsrName(tsrName);
		
//		<!-- Check data -->
		DetachedCriteria criteria = DetachedCriteria.forClass(ConfirmationRecord.class);
		criteria.add(Restrictions.eq("cycleFrom", cycleFrom));
		criteria.add(Restrictions.eq("cycleTo", cycleTo));
		criteria.add(Restrictions.eq("policyNo", policyNo));
		List<ConfirmationRecord> confirmationList = confirmationRecordService.findByCriteria(criteria);
		
		if(confirmationList.isEmpty()) {
			cmr = confirmationRecordService.add(cmr, userLogin);
		} 
//		/* Must not re-upload able */
//		else {
//			if(confirmationList.size() == 1) {
//				cmr.setId(confirmationList.get(0).getId());
//				cmr.setCreateBy(confirmationList.get(0).getCreateBy());
//				cmr.setCreateDate(confirmationList.get(0).getCreateDate());
//				cmr = confirmationRecordService.update(cmr, userLogin);
//			} else {
//				throw new Exception("Found confirmation record for policy: '" + policyNo + "' more than 1");
//			}
//		}
		return cmr;
	}
	
	private List<String> getDateFromString(String str) throws Exception {
		List<String> allMatches = new ArrayList<>();
		//more info about regex: https://www.debuggex.com/r/6_DMybgsG3ub_T9Z
		Matcher m = Pattern.compile(PropertyConfig.getInstance().getValue("cfg.regex.date.pattern")).matcher(str);
		while(m.find()) {
			allMatches.add(m.group());
		}
		return allMatches;
	}

	public void setConfirmationRecordService(ConfirmationRecordService confirmationRecordService) {
		this.confirmationRecordService = confirmationRecordService;
	}

	public String getExcelPwd() {
		return excelPwd;
	}

	public void setExcelPwd(String excelPwd) {
		this.excelPwd = excelPwd;
	}

	public boolean isNeedPwd() {
		return needPwd;
	}

	public void setNeedPwd(boolean needPwd) {
		this.needPwd = needPwd;
	}
	
}
