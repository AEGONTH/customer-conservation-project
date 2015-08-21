package com.adms.web.bean.maininfo;

import java.io.Serializable;

public class MainInfoModel implements Serializable {

	private static final long serialVersionUID = -1560703817502618555L;

	private String campaignCode;
	private String policyNo;

	public String getCampaignCode() {
		return campaignCode;
	}

	public void setCampaignCode(String campaignCode) {
		this.campaignCode = campaignCode;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}
	
	
}
