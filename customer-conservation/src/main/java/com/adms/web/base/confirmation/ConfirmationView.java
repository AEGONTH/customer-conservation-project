package com.adms.web.base.confirmation;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.adms.web.base.bean.BaseBean;

@ManagedBean
@ViewScoped
public class ConfirmationView extends BaseBean {

	private static final long serialVersionUID = 4597975186245178609L;
	private final String REDIRECT_FILE_UPLOAD = "/confirmation/upload?faces-redirect=true";
	private final String REDIRECT_ADD_LOG = "/confirmation/addlog?faces-redirect=true";
	private final String REDIRECT_FILE_DOWNLOAD = "/confirmation/download?faces-redirect=true";
	
	public String toFileUploadPage() {
		return REDIRECT_FILE_UPLOAD;
	}
	
	public String toAddLogPage() {
		return REDIRECT_ADD_LOG;
	}
	
	public String toFileDownloadPage() {
		return REDIRECT_FILE_DOWNLOAD;
	}
}
