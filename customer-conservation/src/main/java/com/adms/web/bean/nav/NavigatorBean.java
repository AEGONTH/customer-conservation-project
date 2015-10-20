package com.adms.web.bean.nav;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class NavigatorBean {
	
	private final String REDIRECT_PARAM = "?faces-redirect=true";
	
	//Main Page URL
	private final String TO_MAIN_PAGE = "/main";
	private final String TO_CUSTOMER_ENQUIRY_PAGE = "/cs/customer-enquiry";
	private final String TO_CONFIRMATION_PAGE = "/confirmation";

	//Confirmation URL
	private final String TO_FILE_UPLOAD_PAGE = "/confirmation/upload";
	private final String TO_ADD_LOG_PAGE = "/confirmation/addlog";
	private final String TO_FILE_DOWNLOAD_PAGE = "/confirmation/download";

	public String mainMenu() throws Exception {
		return TO_MAIN_PAGE + REDIRECT_PARAM;
	}
	
	public String customerEnquiryMenu() throws Exception {
		return TO_CUSTOMER_ENQUIRY_PAGE + REDIRECT_PARAM;
	}
	
	public String confirmationMenu() throws Exception {
		return TO_CONFIRMATION_PAGE + REDIRECT_PARAM;
	}
	
	public String expiredPage() throws Exception {
		return "/errors/expired" + REDIRECT_PARAM;
	}
	
	public String toFileUploadPage() throws Throwable {
		return TO_FILE_UPLOAD_PAGE + REDIRECT_PARAM;
	}
	
	public String toAddLogPage() throws Throwable {
		return TO_ADD_LOG_PAGE + REDIRECT_PARAM;
	}
	
	public String toFileDownloadPage() throws Throwable {
		return TO_FILE_DOWNLOAD_PAGE + REDIRECT_PARAM;
	}
}
