package com.adms.web.bean.nav;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class NavigatorBean {

	public String mainMenu() throws Exception {
		return "/main?faces-redirect=true";
	}
	
	public String customerEnquiryMenu() throws Exception {
		return "/cs/customer-enquiry?faces-redirect=true";
	}
	
	public String confirmationMenu() throws Exception {
		return "/confirmation?faces-redirect=true";
	}
	
	public String expiredPage() throws Exception {
		return "/errors/expired?faces-redirect=true";
	}
}
