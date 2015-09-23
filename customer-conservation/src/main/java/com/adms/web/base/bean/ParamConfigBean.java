package com.adms.web.base.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.NoneScoped;
import javax.faces.model.SelectItem;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.adms.cs.service.ParamConfigService;
import com.adms.entity.cs.ParamConfig;

@ManagedBean
@NoneScoped
public class ParamConfigBean extends BaseBean {

	private static final long serialVersionUID = -5160403587612494991L;

	@ManagedProperty(value="#{paramConfigService}")
	private ParamConfigService paramConfigService;
	
	public List<SelectItem> getSelectItemsByGroup(String paramGroup) throws Exception {
		try {
			List<SelectItem> resutls = new ArrayList<>();
			List<ParamConfig> list = getParamConfigByGroup(paramGroup);
			for(ParamConfig p : list) {
				resutls.add(new SelectItem(p.getParamKey(), p.getParamValue()));
			}
			return resutls;
		} catch(Exception e) {
			throw e;
		}
	}
	
	public String getParamValueFromParamKey(String paramKey) throws Exception {
		return getParamConfigFromParamKey(paramKey).getParamValue();
	}
	
	public ParamConfig getParamConfigFromParamKey(String paramKey) throws Exception {
		ParamConfig p = new ParamConfig();
		p.setParamKey(paramKey);
		List<ParamConfig> o = paramConfigService.find(p);
		if(!o.isEmpty()) {
			return o.get(0);
		}
		throw new Exception("PARAM_KEY NOT FOUND: " + paramKey);
	}
	
	private List<ParamConfig> getParamConfigByGroup(String paramGroup) throws Exception {
		try {
			DetachedCriteria criteria = DetachedCriteria.forClass(ParamConfig.class);
			criteria.add(Restrictions.eq("paramGroup", paramGroup));
			return paramConfigService.findByCriteria(criteria);
		} catch(Exception e) {
			throw e;
		}
	}

	public void setParamConfigService(ParamConfigService paramConfigService) {
		this.paramConfigService = paramConfigService;
	}
	
}
