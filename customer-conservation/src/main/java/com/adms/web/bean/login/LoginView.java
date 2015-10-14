package com.adms.web.bean.login;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.adms.common.entity.UserLogin;
import com.adms.util.MessageUtils;
import com.adms.utils.EncryptionUtil;
import com.adms.web.base.bean.BaseBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@ManagedBean
@ViewScoped
public class LoginView extends BaseBean {

	private static final long serialVersionUID = -7276944451892430995L;

	private final String AUTH_URL = "http://127.0.0.1:8080/authen-ws/rest/authservice";
	private final String AUTH_PATH = "auth";
	
	@ManagedProperty(value="#{loginSession}")
	private LoginSession loginSession;
	
	private String username;
	private String password;
	
	@PostConstruct
	private void init() throws Throwable {
		
	}
	
	public String doLogin() {
		if(!StringUtils.isBlank(username) && !StringUtils.isBlank(password)) {
			if(authService()) {
				return "main?faces-redirect=true";
			} else {
				MessageUtils.getInstance().addErrorMessage("msgLogin", "Invalid Username or Password.");
			}
		} else {
			if(StringUtils.isBlank(username)) ((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmLogin:username")).setValid(false);
			if(StringUtils.isBlank(password)) ((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmLogin:pwd")).setValid(false);
			MessageUtils.getInstance().addErrorMessage("msgLogin", "Please fill all the required");
		}
		return null;
	}
	
	private boolean authService() {
		boolean flag = false;
		
		if(!StringUtils.isBlank(username) && !StringUtils.isBlank(password)) {
			//TODO: Authentication
			try {
				String salt = "$AlT*P@$$w0Rd#";
				String encryptedPwd = EncryptionUtil.getInstance().md5(password, salt);
				UserLogin userLogin = new UserLogin(username, encryptedPwd);
				Gson gson = new GsonBuilder().create();
//				String encryptedObject = EncryptionUtil.getInstance().aes(gson.toJson(userLogin), aesKey) ;
				Response resp =  ClientBuilder.newClient()
						.target(AUTH_URL)
						.path(AUTH_PATH)
						.request()
						.header("val", gson.toJson(userLogin))
						.get();
				
				String respStr = resp.readEntity(String.class);
				if(respStr.contains("Not Found")) {
					System.err.println("Service Not Found: " + AUTH_URL);
				} else {
					gson = new GsonBuilder().create();
					userLogin = gson.fromJson(respStr, UserLogin.class);

					flag = userLogin.getLoginSuccess();
					
					if(flag) {
						List<String> privs = new ArrayList<>();
						for(String key : userLogin.getRolePrivileges().keySet()) {
							privs.addAll(userLogin.getRolePrivileges().get(key));
						}
						List<String> distinctPrivs = privs.stream()
													.sorted((p1, p2) -> p1.compareTo(p2))
													.distinct()
													.collect(Collectors.toList());
						loginSession.roles(userLogin.getRolePrivileges().keySet())
									.rolePrivileges(userLogin.getRolePrivileges())
									.distinctPrivileges(distinctPrivs);
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
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
