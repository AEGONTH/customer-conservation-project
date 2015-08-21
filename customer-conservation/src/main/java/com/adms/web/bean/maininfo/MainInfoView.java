package com.adms.web.bean.maininfo;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.adms.web.base.bean.BaseBean;

@ManagedBean
@ViewScoped
public class MainInfoView extends BaseBean {

	private static final long serialVersionUID = 3084335211454087608L;
	
	private MainInfoModel model;
	
	@PostConstruct
	private void init() {
		if(model == null) {
			setModel(new MainInfoModel());
		}
	}
	
	public MainInfoModel getModel() {
		return model;
	}

	public void setModel(MainInfoModel model) {
		this.model = model;
	}
}
