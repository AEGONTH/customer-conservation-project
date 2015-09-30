package com.adms.web.bean.login;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.adms.web.base.bean.BaseBean;

@ManagedBean
@SessionScoped
public class LoginSession extends BaseBean {

	private static final long serialVersionUID = 3528950428438273568L;

	private String username;
	private String role;
	
	public LoginSession() {
		
	}

	public void signOut() throws IOException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.invalidateSession();
		ec.redirect(ec.getRequestContextPath() + "/login");
	}

	public LoginSession(String username, String role) {
		this.username = username;
		this.role = role;
	}
	
	public LoginSession username(String username) {
		this.username = username;
		return this;
	}
	
	public LoginSession role(String role) {
		this.role = role;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public String getRole() {
		return role;
	}
	
}
