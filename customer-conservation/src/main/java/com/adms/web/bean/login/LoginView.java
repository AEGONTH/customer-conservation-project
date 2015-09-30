package com.adms.web.bean.login;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import com.adms.util.MessageUtils;
import com.adms.web.base.bean.BaseBean;

@ManagedBean
@ViewScoped
public class LoginView extends BaseBean {

	private static final long serialVersionUID = -7276944451892430995L;

	@ManagedProperty(value="#{loginSession}")
	private LoginSession loginSession;
	
	private String username;
	private String password;
	
	public String doLogin() {
		if(authService()) {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			ec.getSession(true);
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", this.username);
			
			loginSession.username(username);
			
			return "main?faces-redirect=true";
		} else {
			return null;
		}
	}
	
	private boolean authService() {
		boolean flag = false;
		
		if(!StringUtils.isBlank(username) && !StringUtils.isBlank(password)) {
			//TODO: query
			flag = true;
		} else {
			if(StringUtils.isBlank(username)) ((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmLogin:username")).setValid(false);
			if(StringUtils.isBlank(password)) ((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmLogin:pwd")).setValid(false);
			MessageUtils.getInstance().addErrorMessage("msgLogin", "Please fill all the required");
		}
		
		return flag;
	}

	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}

	public void setLoginSession(LoginSession loginSession) {
		this.loginSession = loginSession;
	}

}
