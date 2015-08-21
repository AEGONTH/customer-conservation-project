package com.adms.web.bean.login;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.adms.web.base.bean.BaseBean;

@ManagedBean
@ViewScoped
public class LoginView extends BaseBean {

	private static final long serialVersionUID = -7276944451892430995L;

	private String username;
	
//	reg = ^(?=.*\d).{4,8}$
	private String password;
	
//	private final PropertyConfig cfg = PropertyConfig.getInstance();
	
	public String doLogin() {
		boolean flag = false;
		try {
			flag = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(flag) {
			return "page/helloworld?faces-redirect=true";
		} else {
			return null;
		}
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
}
