package com.adms.entity.cs;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Formula;

import com.adms.common.domain.BaseAuditDomain;

@Entity
@Table(name="CUSTOMER")
public class Customer extends BaseAuditDomain {

	private static final long serialVersionUID = 4524373584512075532L;

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="TITLE")
	private String title;
	
	@Column(name="FIRST_NAME")
	private String firstName;
	
	@Column(name="LAST_NAME")
	private String lastName;
	
	@Formula(value = " CONCAT("
			+ "		UPPER(LEFT(FIRST_NAME, 1)), LOWER(SUBSTRING(FIRST_NAME, 2, LEN(FIRST_NAME))) "
			+ "		, ' ' "
			+ "		, UPPER(LEFT(LAST_NAME, 1)), LOWER(SUBSTRING(LAST_NAME, 2, LEN(LAST_NAME)))) ")
	private String fullName;
	
	@Column(name="CITIZEN_ID")
	private String citizenId;
	
	@Column(name="PASSPORT_ID")
	private String passportId;
	
	@Column(name="GENDER")
	private String gender;
	
	@Column(name="DOB")
	@Temporal(TemporalType.DATE)
	private Date dob;
	
	@Column(name="NATIONALITY")
	private String nationality;
	
	@Column(name="CITIZENSHIP")
	private String citizenship;
	
	@Column(name="MARITAL")
	private String marital;
	
	@Column(name="HOME_NO")
	private String homeNo;
	
	@Column(name="MOBILE_NO")
	private String mobileNo;
	
	@Column(name="OTHER_NO")
	private String otherNo;
	
	@Column(name="OFFICE_NO")
	private String officeNo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCitizenId() {
		return citizenId;
	}

	public void setCitizenId(String citizenId) {
		this.citizenId = citizenId;
	}

	public String getPassportId() {
		return passportId;
	}

	public void setPassportId(String passportId) {
		this.passportId = passportId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getMarital() {
		return marital;
	}

	public void setMarital(String marital) {
		this.marital = marital;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getHomeNo() {
		return homeNo;
	}

	public void setHomeNo(String homeNo) {
		this.homeNo = homeNo;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo1) {
		this.mobileNo = mobileNo1;
	}

	public String getOtherNo() {
		return otherNo;
	}

	public void setOtherNo(String mobileNo2) {
		this.otherNo = mobileNo2;
	}

	public String getOfficeNo() {
		return officeNo;
	}

	public void setOfficeNo(String officeNo) {
		this.officeNo = officeNo;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", title=" + title + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", fullName=" + fullName + ", citizenId=" + citizenId + ", passportId=" + passportId + ", gender="
				+ gender + ", dob=" + dob + ", marital=" + marital + ", homeNo=" + homeNo + ", mobileNo1=" + mobileNo
				+ ", mobileNo2=" + otherNo + ", officeNo=" + officeNo + "]";
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getCitizenship() {
		return citizenship;
	}

	public void setCitizenship(String citizenship) {
		this.citizenship = citizenship;
	}
	
}
