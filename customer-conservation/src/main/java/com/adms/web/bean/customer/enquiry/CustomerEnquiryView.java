package com.adms.web.bean.customer.enquiry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.adms.cs.service.CallLogService;
import com.adms.cs.service.CustomerService;
import com.adms.cs.service.CustomerYesRecordService;
import com.adms.cs.service.LogDetailCategoryService;
import com.adms.cs.service.LogStatusGroupService;
import com.adms.entity.cs.CallLog;
import com.adms.entity.cs.Customer;
import com.adms.entity.cs.CustomerYesRecord;
import com.adms.entity.cs.LogDetailCategory;
import com.adms.entity.cs.LogStatusGroup;
import com.adms.entity.cs.LogValue;
import com.adms.entity.sale.Sales;
import com.adms.sales.service.SalesService;
import com.adms.util.MessageUtils;
import com.adms.utils.DateUtil;
import com.adms.web.base.bean.BaseBean;
import com.adms.web.base.bean.LanguageBean;

@ManagedBean
@ViewScoped
public class CustomerEnquiryView extends BaseBean {

	private static final long serialVersionUID = -7189621707509848797L;
	
	@ManagedProperty(value="#{language}")
	private LanguageBean language;
	
	@ManagedProperty(value="#{customerService}")
	private CustomerService customerService;
	
	@ManagedProperty(value="#{customerYesRecordService}")
	private CustomerYesRecordService customerYesRecordService;
	
	@ManagedProperty(value="#{callLogService}")
	private CallLogService callLogService;
	
	@ManagedProperty(value="#{logDetailCategoryService}")
	private LogDetailCategoryService logDetailCategoryService;
	
	@ManagedProperty(value="#{logStatusGroupService}")
	private LogStatusGroupService logStatusGroupService;
	
	@ManagedProperty(value="#{salesService}")
	private SalesService salesService;
	
	private ResourceBundle csMsg;
	private CustomerEnquiryModel model;
	
	private String shCitizenId;
	private String shFirstName;
	private String shLastName;
	private Date shDOB;
	
//	Add Case Parameters
	private static final String PLEASE_SELECT = "Please Select";
	private static final Long DEFAULT_SELECT_ONE_MENU_VALUE = -1L;
	
	private boolean updateLog;
	private CallLog selectedCallLog;
	
//	GROUP
	private final String CHANNEL = "CHANNEL";
	private final String CALL_NATURE = "CALL_NATURE";
	private final String CALL_CATEGORY = "CALL_CATEGORY";
	private final String CALL_RESULT = "CALL_RESULT";
	private final String CANCEL_REASON = "CANCEL_REASON";
	private final String CALL_RESOURCE = "CALL_RESOURCE";
	private final String COMPLAINT_STATUS = "COMPLAINT_STATUS";
	private final String COMPLAINT_RESULT = "COMPLAINT_RESULT";
	
	private Map<String, List<LogStatusGroup>> callLogGrupStatMap;
	private Map<Long, Map<Long, List<LogValue>>> detailCatMap;
	
	public CustomerEnquiryView() {
	
	}
	
	public void onSelectRow(SelectEvent event) {
		System.out.println("select: " + event.getObject());
	}
	
	@PostConstruct
	private void init() {
		csMsg = ResourceBundle.getBundle("com.adms.msg.cs.csMsg", new Locale(language.getLocaleCode()));
		clearModel();
		clearSh();
		try {
			initGroupStatus();
			initDetailCategory();
		} catch(Exception e) {
			e.printStackTrace();
		}
//		String renderKitId = FacesContext.getCurrentInstance().getViewRoot().getRenderKitId(); 
//		System.out.println("renderKitId: " + renderKitId);
	}
	
	private void initGroupStatus() throws Exception {
		List<LogStatusGroup> list = logStatusGroupService.findAll();
		callLogGrupStatMap = new HashMap<>();
		for(LogStatusGroup logGroup : list) {
			List<LogStatusGroup> stats = callLogGrupStatMap.get(logGroup.getLogGroup());
			if(stats == null) {
				stats = new ArrayList<>();
				callLogGrupStatMap.put(logGroup.getLogGroup(), stats);
			}
			stats.add(logGroup);
		}
	}

	private void initDetailCategory() throws Exception {
		List<LogDetailCategory> list = logDetailCategoryService.findAll();
		detailCatMap = new HashMap<>();
		
		for(LogDetailCategory d : list) {
			Map<Long, List<LogValue>> catMap = detailCatMap.get(d.getLevelCallNature().getId());
			if(catMap == null) {
				catMap = new HashMap<>();
				detailCatMap.put(d.getLevelCallNature().getId(), catMap);
			}
			List<LogValue> details = catMap.get(d.getLevelCallCategory().getId());
			if(details == null) {
				details = new ArrayList<>();
				catMap.put(d.getLevelCallCategory().getId(), details);
			}
			details.add(d.getLogValue());
		}
	}
	
	private void initCallSubCategory() {
		if(model.getSelectedCallNature() != null && model.getSelectedCallNature() != DEFAULT_SELECT_ONE_MENU_VALUE
				&& model.getSelectedCategory() != null && model.getSelectedCategory() != DEFAULT_SELECT_ONE_MENU_VALUE) {
			
			model.setSubCategorySelection(new ArrayList<>());
			model.getSubCategorySelection().add(new SelectItem(DEFAULT_SELECT_ONE_MENU_VALUE, PLEASE_SELECT));
			
			List<LogValue> details = detailCatMap.get(model.getSelectedCallNature()).get(model.getSelectedCategory());
			
			for(LogValue detail : details) {
				model.getSubCategorySelection().add(new SelectItem(detail.getId(), detail.getValue()));
			}
		} else {
			model.setSubCategorySelection(null);
		}
	}
	
	public void initAllDropdownMenu() {
		model.setSourceSelection(createSelectItems(CALL_RESOURCE));
		model.setSelectedSource(DEFAULT_SELECT_ONE_MENU_VALUE);
		
		model.setChannelSelection(createSelectItems(CHANNEL));
		model.setSelectedChannel(DEFAULT_SELECT_ONE_MENU_VALUE);
		
		model.setNatureSelection(createSelectItems(CALL_NATURE));
		model.setSelectedCallNature(DEFAULT_SELECT_ONE_MENU_VALUE);
		
		model.setCategorySelection(createSelectItems(CALL_CATEGORY));
		model.setSelectedCategory(DEFAULT_SELECT_ONE_MENU_VALUE);
		
		model.setComplaintStatusSelection(createSelectItems(COMPLAINT_STATUS));
		model.setSelectedComplaintStatus(DEFAULT_SELECT_ONE_MENU_VALUE);
		
		model.setComplaintResultSelection(createSelectItems(COMPLAINT_RESULT));
		model.setSelectedComplaintResult(DEFAULT_SELECT_ONE_MENU_VALUE);
		
		//TODO: other initial selection add here
		
		initCallSubCategory();
	}

	public void initDialogAddLog() {
		model.setLogDate(DateUtil.getCurrentDate());
	}

	public void subCategoryListener() {
		initCallSubCategory();
	}

	public void clearModel() {
		model = new CustomerEnquiryModel();
	}
	
	public void clearSh() {
		shCitizenId = null;
		shDOB = null;
		shFirstName = null;
		shLastName = null;
	}
	
	public void doSearch() throws Exception {
		RequestContext rc = RequestContext.getCurrentInstance();
		if(StringUtils.isBlank(shCitizenId) 
				&& StringUtils.isBlank(shFirstName) 
				&& StringUtils.isBlank(shLastName)
				&& shDOB == null) {
			rc.execute("PF('searchDlg').jq.effect('shake', {times:5}, 100);");
			
			MessageUtils.getInstance().addErrorMessage("msgGrowl", "Please fills at least 1 field.");
			rc.update("frmMain:msgGrowl");
		} else {
			if(shDOB != null && shDOB.compareTo(DateUtil.getCurrentDate()) > 0) {
				MessageUtils.getInstance().addErrorMessage("msgGrowl", "DOB must not exeed today");
				rc.update("frmMain:msgGrowl");
				return;
			}
			
			List<Customer> list = findCustomer();
			
			if(list.isEmpty()) {
				MessageUtils.getInstance().addErrorMessage("msgGrowl", "Customer not Found.");
				rc.update("frmMain:msgGrowl");
			} else if(list.size() == 1) {
				model.setCustomer(list.get(0));
				clearSh();
				logicPolicyByCus();
				rc.execute("PF('searchDlg').hide();");
				rc.update("frmMain");
			} else {
				model.setCustomerFounds(list);
				rc.update("frmMain:selectCustomerDlg");
				rc.execute("PF('selectCustomerDlg').show();");
			}
			
		}
	}
	
	public void doVisibleLogHistory(CustomerYesRecord obj) throws Exception {
		model.setPolicy(obj);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(CallLog.class);
		DetachedCriteria customerYes = criteria.createCriteria("customerYesRecord", JoinType.INNER_JOIN);
		customerYes.add(Restrictions.eq("sales.xReference", obj.getSales().getxReference()));
		
		criteria.addOrder(Order.desc("id"));
		
		List<CallLog> list = callLogService.findByCriteria(criteria);
		if(list != null && !list.isEmpty()) {
			this.model.setPolicyCallLogs(list);
		} else {
			this.model.setPolicyCallLogs(new ArrayList<>());
		}
		
		initAllDropdownMenu();
	}
	
	public void doInvisibleLogHistory() {
		this.model.setPolicyCallLogs(null);
	}
	
	public void doInitLog(CallLog callLog) {
		clearAddCaseLog();
		model.setLogDate(DateUtil.getCurrentDate());
		if(callLog == null) {
			updateLog = false;
			selectedCallLog = null;
		} else {
			updateLog = true;
			selectedCallLog = callLog;
			
			model.setSelectedSource(callLog.getSource().getId());
			model.setSelectedChannel(callLog.getChannel().getId());
			model.setSelectedCallNature(callLog.getCallLogDetail().getLevelCallNature().getId());
			model.setSelectedCategory(callLog.getCallLogDetail().getLevelCallCategory().getId());
			initCallSubCategory();
			model.setSelectedSubCategory(callLog.getCallLogDetail().getLogValue().getId());
			model.setLogDetail(callLog.getDetail());
			model.setSelectedComplaintStatus(callLog.getLogCurrentStatus().getId());
			model.setSelectedComplaintResult(callLog.getLogResult().getId());
			model.setResultDetail(callLog.getLogResultDetail());
			model.setCorrectiveAction(callLog.getCorrectiveAction());
			model.setSuggestDetail(callLog.getSuggestDetail());
			model.setLogRemark(callLog.getRemark());
		}
	}
	
	public void onRowSelectLog(SelectEvent event) {
		doInitLog((CallLog) event.getObject());
    }
	
	public void doAddCallLog() throws Exception {
		//TODO validate input
		if(model == null) throw new Exception("ERROR, please refresh page");
		RequestContext rc = RequestContext.getCurrentInstance();
		
		boolean flag = validateAddCaseLog();
		if(!flag) {
			MessageUtils.getInstance().addErrorMessage("msgAddCase", "Please enter data in the required fields.");
			rc.update("frmMain:panelLogDetail");
		} else {
			CallLog callLog = saveCallLog(
					updateLog
					, model.getPolicy()
					, model.getLogDate()
					, model.getSelectedSource()
					, model.getSelectedChannel()
					, model.getSelectedSubCategory()
					, model.getLogDetail()
					
					, model.getSelectedComplaintStatus()
					, model.getSelectedComplaintResult()
					, model.getResultDetail()
					, model.getCorrectiveAction()
					, model.getSuggestDetail()
					
					, model.getLogRemark());
				doVisibleLogHistory(model.getPolicy());
			if(!updateLog) {
				model.getPolicyCallLogs().add(0, callLog);
			}
			clearAddCaseLog();
			selectedCallLog = null;
			rc.update("frmMain:panelLogHistTbl");
			rc.execute("PF('addCaseDlg').hide();");
		}
	}
	
	public void doTest() {
		try {
			DetachedCriteria criteria = DetachedCriteria.forClass(Sales.class);
			criteria.add(Restrictions.eq("xReference", "58256220621"));
			
			List<Sales> list = salesService.findByCriteria(criteria);
			System.out.println(list.size());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clearAddCaseLog() {
		initAllDropdownMenu();
		model.setLogDetail(null);
		model.setResultDetail(null);
		model.setCorrectiveAction(null);
		model.setSuggestDetail(null);
		model.setLogRemark(null);
		selectedCallLog = null;
	}
	
	private boolean validateAddCaseLog() {
		//Message ID: frmMain:msgAddCase
		//frmMain:logSourceName
		//channel, callNature, callCategory, callSubcategory
		boolean flag = true;
		
		if(model.getSelectedSource() == DEFAULT_SELECT_ONE_MENU_VALUE) {
			((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmMain:logSourceName")).setValid(false);
			flag = false;
		}
		
		if(model.getSelectedChannel() == DEFAULT_SELECT_ONE_MENU_VALUE) {
			((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmMain:channel")).setValid(false);
//			MessageUtils.getInstance().addErrorMessage("msgAddCase", "Channel is Required.");
			flag = false;
		}
		
		if(model.getSelectedCallNature() == DEFAULT_SELECT_ONE_MENU_VALUE) {
			((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmMain:callNature")).setValid(false);
//			MessageUtils.getInstance().addErrorMessage("msgAddCase", "Call Nature is Required.");
			flag = false;
		}
		
		if(model.getSelectedCategory() == DEFAULT_SELECT_ONE_MENU_VALUE) {
			((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmMain:callCategory")).setValid(false);
//			MessageUtils.getInstance().addErrorMessage("msgAddCase", "Call Category is Required.");
			flag = false;
		}
		
		if(model.getSelectedSubCategory() == DEFAULT_SELECT_ONE_MENU_VALUE) {
			((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmMain:callSubcategory")).setValid(false);
//			MessageUtils.getInstance().addErrorMessage("msgAddCase", "Call Sub-category is Required.");
			flag = false;
		}
		
		return flag;
	}

	private CallLog saveCallLog(boolean updateLog, CustomerYesRecord policy, Date logDate, Long source, Long channel, Long callLogDetail, String detail
			, Long complaintStatus, Long complaintResult, String resultDetail, String correctiveAction, String suggestDetail, String remark) throws Exception {
		CallLog example = new CallLog();
		example.setCustomerYesRecord(policy);
		example.setCallDate(logDate);
		example.setSource(logStatusGroupService.find(source));
		example.setChannel(logStatusGroupService.find(channel));
		
		DetachedCriteria logDetailCriteria = DetachedCriteria.forClass(LogDetailCategory.class);
		logDetailCriteria.add(Restrictions.eq("logValue.id", callLogDetail));
		example.setCallLogDetail(logDetailCategoryService.findByCriteria(logDetailCriteria).get(0));
		if(!StringUtils.isBlank(detail)) example.setDetail(detail);
		
		LogStatusGroup lsgComplaint = logStatusGroupService.find(complaintStatus);
		example.setLogCurrentStatus(lsgComplaint);
		if(lsgComplaint.getParam().equals("COMPLAINT_STATUS_CLOSE_CANNOT_CONTACT") 
				|| lsgComplaint.getParam().equals("COMPLAINT_STATUS_CLOSE_CAN_CONTACT_CUSTOMER")) {
			example.setClosedDate(DateUtil.getCurrentDate());
		} else {
			example.setClosedDate(null);
		}
		
		example.setLogResult(logStatusGroupService.find(complaintResult));
		example.setLogResultDetail(resultDetail);
		example.setCorrectiveAction(correctiveAction);
		example.setSuggestDetail(suggestDetail);
		
		if(!StringUtils.isBlank(detail)) example.setRemark(remark);
		
		if(updateLog) {
			example.setId(selectedCallLog.getId());
			example.setCreateBy(selectedCallLog.getCreateBy());
			example.setCreateDate(selectedCallLog.getCreateDate());
			
			return callLogService.update(example, super.DEFAULT_SYSTEM_LOG_NAME);
		} else {
			return callLogService.add(example, super.DEFAULT_SYSTEM_LOG_NAME);
		}
	}
	
	public void onSelectCustomer(SelectEvent event) {
		clearSh();
		model.setCustomerFounds(null);
		model.setCustomerYRs(null);
		model.setPolicyCallLogs(null);
		logicPolicyByCus();
	}

	private void logicPolicyByCus() {
		List<CustomerYesRecord> list = findPolicyByCustomer();
		if(list != null && !list.isEmpty()) {
			model.setCustomerYRs(list);
		} else {
			model.setCustomerYRs(new ArrayList<>());
		}
	}
	
	private List<Customer> findCustomer() {
		try {
			DetachedCriteria criteria = DetachedCriteria.forClass(Customer.class);
			if(!StringUtils.isBlank(shCitizenId)) criteria.add(Restrictions.eq("citizenId", shCitizenId));
			if(!StringUtils.isBlank(shFirstName)) criteria.add(Restrictions.like("firstName", "%" + shFirstName.trim().toUpperCase() + "%"));
			if(!StringUtils.isBlank(shLastName)) criteria.add(Restrictions.like("lastName", "%" + shLastName.trim().toUpperCase() + "%"));
			if(shDOB != null) criteria.add(Restrictions.eq("dob", shDOB));
			
			return customerService.findByCriteria(criteria);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<CustomerYesRecord> findPolicyByCustomer() {
		try {
			DetachedCriteria criteria = DetachedCriteria.forClass(CustomerYesRecord.class);
			criteria.add(Restrictions.eq("customer.citizenId", this.model.getCustomer().getCitizenId()));
			return customerYesRecordService.findByCriteria(criteria);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<SelectItem> createSelectItems(String groupParam) {
		List<LogStatusGroup> list = callLogGrupStatMap.get(groupParam);
		List<SelectItem> selectItems = new ArrayList<>();
		
		selectItems.add(new SelectItem(DEFAULT_SELECT_ONE_MENU_VALUE, PLEASE_SELECT));
		for(LogStatusGroup s : list) {
			selectItems.add(new SelectItem(s.getId(), s.getValue()));
		}
		return selectItems;
	}

	public CustomerEnquiryModel getModel() {
		return model;
	}

	public void setModel(CustomerEnquiryModel model) {
		this.model = model;
	}

	public void setLanguage(LanguageBean language) {
		this.language = language;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public String getShCitizenId() {
		return shCitizenId;
	}

	public void setShCitizenId(String shCitizenId) {
		this.shCitizenId = shCitizenId;
	}

	public String getShFirstName() {
		return shFirstName;
	}

	public void setShFirstName(String shFirstName) {
		this.shFirstName = shFirstName;
	}

	public String getShLastName() {
		return shLastName;
	}

	public void setShLastName(String shLastName) {
		this.shLastName = shLastName;
	}

	public Date getShDOB() {
		return shDOB;
	}

	public void setShDOB(Date shDOB) {
		this.shDOB = shDOB;
	}

	public void setCallLogService(CallLogService callLogService) {
		this.callLogService = callLogService;
	}

	public void setCustomerYesRecordService(CustomerYesRecordService customerYesRecordService) {
		this.customerYesRecordService = customerYesRecordService;
	}

	public void setLogDetailCategoryService(LogDetailCategoryService logDetailCategoryService) {
		this.logDetailCategoryService = logDetailCategoryService;
	}

	public void setLogStatusGroupService(LogStatusGroupService logStatusGroupService) {
		this.logStatusGroupService = logStatusGroupService;
	}

	public void setSalesService(SalesService salesService) {
		this.salesService = salesService;
	}

	public boolean isUpdateLog() {
		return updateLog;
	}

	public void setUpdateLog(boolean updateLog) {
		this.updateLog = updateLog;
	}

	public CallLog getSelectedCallLog() {
		return selectedCallLog;
	}

	public void setSelectedCallLog(CallLog selectedCallLog) {
		this.selectedCallLog = selectedCallLog;
	}

}