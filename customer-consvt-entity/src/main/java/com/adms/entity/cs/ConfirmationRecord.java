package com.adms.entity.cs;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.adms.common.domain.BaseAuditDomain;

@Entity
@Table(name="CONFIRMATION_RECORD")
public class ConfirmationRecord extends BaseAuditDomain {

	private static final long serialVersionUID = -2940032028999895145L;

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="CYCLE_FROM")
	@Temporal(TemporalType.DATE)
	private Date cycleFrom;

	@Column(name="CYCLE_TO")
	@Temporal(TemporalType.DATE)
	private Date cycleTo;
	
	@Column(name="APPLICATION_DATE")
	@Temporal(TemporalType.DATE)
	private Date applicationDate;
	
	@Column(name="SETTLE_DATE")
	@Temporal(TemporalType.DATE)
	private Date settleDate;
	
	@Column(name="X_REFERENCE")
	private String xReference;

	@Column(name="POLICY_NO")
	private String policyNo;
	
	@Column(name="PLAN_CODE")
	private String planCode;

	@Column(name="INSURED_NAME")
	private String insuredName;

	@Column(name="MODAL_PREMIUM", scale=4)
	private BigDecimal modalPremium;
	
	@Column(name="APE", scale=4)
	private BigDecimal ape;
	
	@Column(name="SUM_INSURED", scale=4)
	private BigDecimal sumInsured;
	
	@Column(name="MODE")
	private String mode;

	@Column(name="STATUS")
	private String status;

	@Column(name="UNIT_CODE")
	private String unitCode;

	@Column(name="BANK_CARD")
	private String bankCard;

	@Column(name="CREDIT_CARD_NO")
	private String creditCardNo;

	@Column(name="EXPIRED_DATE")
	private String expiredDate;
	
	@Column(name="BIRTH_DATE")
	@Temporal(TemporalType.DATE)
	private Date birthDate;
	
	@Column(name="CITIZEN_ID")
	private String citizenId;
	
	@Column(name="ADDRESS_1")
	private String address1;
	
	@Column(name="ADDRESS_2")
	private String address2;

	@Column(name="ADDRESS_3")
	private String address3;

	@Column(name="PROVINCE")
	private String province;

	@Column(name="POST_CODE")
	private String postCode;

	@Column(name="TEL_1")
	private String tel1;

	@Column(name="TEL_2")
	private String tel2;

	@Column(name="TEL_3")
	private String tel3;

	@Column(name="MOBILE_1")
	private String mobile1;
	
	@Column(name="MOBILE_2")
	private String mobile2;

	@Column(name="TSR_NAME")
	private String tsrName;

	@ManyToOne
	@JoinColumn(name="ACTION", referencedColumnName="PARAM_KEY")
	private ParamConfig action;

	@Column(name="REMARK")
	private String remark;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCycleFrom() {
		return cycleFrom;
	}

	public void setCycleFrom(Date cycleFrom) {
		this.cycleFrom = cycleFrom;
	}

	public Date getCycleTo() {
		return cycleTo;
	}

	public void setCycleTo(Date cycleTo) {
		this.cycleTo = cycleTo;
	}

	public String getxReference() {
		return xReference;
	}

	public void setxReference(String xReference) {
		this.xReference = xReference;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public String getInsuredName() {
		return insuredName;
	}

	public void setInsuredName(String insuredName) {
		this.insuredName = insuredName;
	}

	public BigDecimal getModalPremium() {
		return modalPremium;
	}

	public void setModalPremium(BigDecimal modalPremium) {
		this.modalPremium = modalPremium;
	}

	public BigDecimal getApe() {
		return ape;
	}

	public void setApe(BigDecimal ape) {
		this.ape = ape;
	}

	public BigDecimal getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(BigDecimal sumInsured) {
		this.sumInsured = sumInsured;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public String getCreditCardNo() {
		return creditCardNo;
	}

	public void setCreditCardNo(String creditCardNo) {
		this.creditCardNo = creditCardNo;
	}

	public String getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getCitizenId() {
		return citizenId;
	}

	public void setCitizenId(String citizenId) {
		this.citizenId = citizenId;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getTel1() {
		return tel1;
	}

	public void setTel1(String tel1) {
		this.tel1 = tel1;
	}

	public String getTel2() {
		return tel2;
	}

	public void setTel2(String tel2) {
		this.tel2 = tel2;
	}

	public String getTel3() {
		return tel3;
	}

	public void setTel3(String tel3) {
		this.tel3 = tel3;
	}

	public String getMobile1() {
		return mobile1;
	}

	public void setMobile1(String mobile1) {
		this.mobile1 = mobile1;
	}

	public String getTsrName() {
		return tsrName;
	}

	public void setTsrName(String tsrName) {
		this.tsrName = tsrName;
	}

	public ParamConfig getAction() {
		return action;
	}

	public void setAction(ParamConfig action) {
		this.action = action;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}

	public Date getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(Date settleDate) {
		this.settleDate = settleDate;
	}

	public String getMobile2() {
		return mobile2;
	}

	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}
	
}
