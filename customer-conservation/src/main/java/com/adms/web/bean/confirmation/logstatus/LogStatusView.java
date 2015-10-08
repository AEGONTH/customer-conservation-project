package com.adms.web.bean.confirmation.logstatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.primefaces.context.RequestContext;

import com.adms.cs.service.ConfirmationRecordService;
import com.adms.cs.service.ParamConfigService;
import com.adms.entity.cs.ConfirmationRecord;
import com.adms.util.MessageUtils;
import com.adms.utils.DateUtil;
import com.adms.web.base.bean.BaseBean;
import com.adms.web.base.bean.ParamConfigBean;

@ManagedBean
@ViewScoped
public class LogStatusView extends BaseBean {

	private static final long serialVersionUID = -6886291977758237407L;

	private final String USER_LOGIN = "System Admin";
	
	@ManagedProperty(value="#{confirmationRecordService}")
	private ConfirmationRecordService confirmationRecordService;
	
	@ManagedProperty(value="#{paramConfigService}")
	private ParamConfigService paramConfigService;
	
	@ManagedProperty(value="#{paramConfigBean}")
	private ParamConfigBean paramConfigBean;
	
	private final String MS_SQL_DATE_PATTERN = "yyyy-MM-dd";
	private final String DISPLAY_DATE_PATTERN = "dd-MMM-yyyy";
	private final String SIMPLE_DATE_PATTERN = "yyyyMMdd";

	private List<SelectItem> selectionCycleFrom;
	private List<SelectItem> selectionCycleTo;
	private List<String> cycleTos;

	private String selectedDateFrom;
	private String selectedDateTo;
	private Date applicationDate;
	private Date settleDate;
	private String policyNo;
	private String insuredName;
	private String citizenId;
	private String mobile;
	
	private int dtRowPerPage;
	
	private List<ConfirmationRecord> logStatusModel;
	private ConfirmationRecord modLogStatus;
	
	private List<SelectItem> selectionAction;
	private String selectedAction;
	private String inRemark;
	
	@PostConstruct
	private void init() {
		selectedDateFrom = null;
		selectedDateTo = null;
		applicationDate = null;
		settleDate = null;
		logStatusModel = null;
		modLogStatus = null;
		inRemark = null;
		dtRowPerPage = 5;
		try {
			prepareCycleDateSelection();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void search() throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(ConfirmationRecord.class);
		
		if(!StringUtils.isBlank(selectedDateFrom) && !StringUtils.isBlank(selectedDateTo)) {
			if(selectedDateFrom.compareTo(selectedDateTo) > 0) {
				MessageUtils.getInstance().addErrorMessage("growlLogStatus", "Invalid cycle date to.");
				return;
			}
			
			criteria.add(Restrictions.between("cycleFrom", DateUtil.convStringToDate(SIMPLE_DATE_PATTERN, selectedDateFrom), DateUtil.convStringToDate(SIMPLE_DATE_PATTERN, selectedDateTo)));
			criteria.add(Restrictions.between("cycleTo", DateUtil.convStringToDate(SIMPLE_DATE_PATTERN, selectedDateFrom), DateUtil.convStringToDate(SIMPLE_DATE_PATTERN, selectedDateTo)));
		} else if(!StringUtils.isBlank(selectedDateFrom) && StringUtils.isBlank(selectedDateTo)) {
			criteria.add(Restrictions.eq("cycleFrom", DateUtil.convStringToDate(SIMPLE_DATE_PATTERN, selectedDateFrom)));
		} else if(StringUtils.isBlank(selectedDateFrom) && !StringUtils.isBlank(selectedDateTo)) {
			criteria.add(Restrictions.eq("cycleTo", DateUtil.convStringToDate(SIMPLE_DATE_PATTERN, selectedDateTo)));
		}
		
		if(applicationDate != null) {
			criteria.add(Restrictions.eq("applicationDate", applicationDate));
		}
		if(settleDate != null) {
			criteria.add(Restrictions.eq("settleDate", settleDate));
		}
		if(!StringUtils.isBlank(policyNo)) {
			criteria.add(Restrictions.eq("policyNo", policyNo));
		}
		if(!StringUtils.isBlank(insuredName)) {
			criteria.add(Restrictions.like("insuredName", insuredName, MatchMode.ANYWHERE));
		}
		if(!StringUtils.isBlank(citizenId)) {
			criteria.add(Restrictions.eq("citizenId", citizenId));
		}
		if(!StringUtils.isBlank(mobile)) {
			Disjunction d = Restrictions.disjunction();
			d.add(Restrictions.eq("mobile1", mobile));
			d.add(Restrictions.eq("mobile2", mobile));
			criteria.add(d);
		}
		criteria.addOrder(Order.asc("cycleFrom"));
		
		logStatusModel = confirmationRecordService.findByCriteria(criteria);
	}
	
	public void clear() {
		init();
	}
	
	public void doModifyLogStatus(ConfirmationRecord modData) {
		RequestContext rc = RequestContext.getCurrentInstance();
		selectionAction = null;
		selectedAction = null;
		try {
			modLogStatus = modData;
			prepareAddLogStatus();
			if(modLogStatus.getAction() != null) {
				selectedAction = modLogStatus.getAction().getParamKey();
			}
			if(!StringUtils.isBlank(modLogStatus.getRemark())) {
				inRemark = modLogStatus.getRemark();
			}
			
			rc.execute("PF('addActionDlg').show();");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void submitLog() throws Exception {
		try {
			if(StringUtils.isBlank(selectedAction)) {
				modLogStatus.setAction(null);
			} else {
				modLogStatus.setAction(paramConfigBean.getParamConfigFromParamKey(selectedAction));
			}
			modLogStatus.setRemark(inRemark);
			confirmationRecordService.update(modLogStatus, USER_LOGIN);

			search();
			selectedAction = null;
			inRemark = null;
			
			RequestContext rc = RequestContext.getCurrentInstance();
			rc.update("frmLogStatus");
			rc.execute("PF('addActionDlg').hide();");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void prepareAddLogStatus() throws Exception {
		selectionAction = paramConfigBean.getSelectItemsByGroup("CONFIRMATION_LOG_ACTION");
		selectionAction.add(0, new SelectItem(null, "Please Select"));
	}
	
	private void prepareCycleDateSelection() throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(ConfirmationRecord.class);
		ProjectionList pjl = Projections.projectionList();
		pjl.add(Projections.property("cycleFrom"));
		pjl.add(Projections.property("cycleTo"));
		criteria.setProjection(Projections.distinct(pjl));
		criteria.addOrder(Order.asc("cycleFrom"));
		
		List<?> list = confirmationRecordService.findByCriteria(criteria);
		Object[] objs = list.toArray();
		selectionCycleFrom = new ArrayList<>();
		selectionCycleFrom.add(new SelectItem(null, "Please Select"));
		selectionCycleTo = new ArrayList<>();
		selectionCycleTo.add(new SelectItem(null, "Please Select"));
		
		for(Object obj : objs) {
			if(obj instanceof Object[]) {
				Object[] innerObjs = (Object[]) obj;
				if(innerObjs.length == 2) {
					Date dFrom = DateUtil.convStringToDate(MS_SQL_DATE_PATTERN, innerObjs[0].toString());
					Date dTo = DateUtil.convStringToDate(MS_SQL_DATE_PATTERN, innerObjs[1].toString());
					selectionCycleFrom.add(new SelectItem(DateUtil.convDateToString(SIMPLE_DATE_PATTERN, dFrom), DateUtil.convDateToString(DISPLAY_DATE_PATTERN, dFrom)));
					selectionCycleTo.add(new SelectItem(DateUtil.convDateToString(SIMPLE_DATE_PATTERN, dTo), DateUtil.convDateToString(DISPLAY_DATE_PATTERN, dTo)));
				} else {
					System.out.println("ERR: obj[] length not eq to 2");
				}
			}
		}
	}

	public void setConfirmationRecordService(ConfirmationRecordService confirmationRecordService) {
		this.confirmationRecordService = confirmationRecordService;
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

	public List<String> getCycleTos() {
		return cycleTos;
	}

	public void setCycleTos(List<String> cycleTos) {
		this.cycleTos = cycleTos;
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

	public Date getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}

	public Date getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(Date settleDate) {
		this.settleDate = settleDate;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getInsuredName() {
		return insuredName;
	}

	public void setInsuredName(String insuredName) {
		this.insuredName = insuredName;
	}

	public String getCitizenId() {
		return citizenId;
	}

	public void setCitizenId(String citizenId) {
		this.citizenId = citizenId;
	}

	public List<ConfirmationRecord> getLogStatusModel() {
		return logStatusModel;
	}

	public void setLogStatusModel(List<ConfirmationRecord> logStatusModel) {
		this.logStatusModel = logStatusModel;
	}

	public int getDtRowPerPage() {
		return dtRowPerPage;
	}

	public void setDtRowPerPage(int dtRowPerPage) {
		this.dtRowPerPage = dtRowPerPage;
	}

	public ConfirmationRecord getModLogStatus() {
		return modLogStatus;
	}

	public void setModLogStatus(ConfirmationRecord modLogStatus) {
		this.modLogStatus = modLogStatus;
	}

	public List<SelectItem> getSelectionAction() {
		return selectionAction;
	}

	public void setSelectionAction(List<SelectItem> selectionAction) {
		this.selectionAction = selectionAction;
	}

	public String getSelectedAction() {
		return selectedAction;
	}

	public void setSelectedAction(String selectedAction) {
		this.selectedAction = selectedAction;
	}
	
	public void setParamConfigService(ParamConfigService paramConfigService) {
		this.paramConfigService = paramConfigService;
	}

	public void setParamConfigBean(ParamConfigBean paramConfigBean) {
		this.paramConfigBean = paramConfigBean;
	}

	public String getInRemark() {
		return inRemark;
	}

	public void setInRemark(String inRemark) {
		this.inRemark = inRemark;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
}
