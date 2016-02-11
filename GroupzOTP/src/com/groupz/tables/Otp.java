package com.groupz.tables;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gpzhibernate.BaseDatabaseObject;
import com.gpzhibernate.DontPersistWhenSerializing;

@Entity
@Table(name = "otp")
public class Otp extends BaseDatabaseObject implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	@DontPersistWhenSerializing
	private long id;

	@Column(name = "CountryCode")
	private String countrycode;

	@Column(name = "Mobile")
	private String mobile;

	@Column(name = "Otp")
	private String otp;

	@Column(name = "CreatedTime")
	private Date createdTime;

	@Column(name = "LapseTime")
	private Date lapsetime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCountrycode() {
		return countrycode;
	}

	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getLapsetime() {
		return lapsetime;
	}

	public void setLapsetime(Date lapsetime) {
		this.lapsetime = lapsetime;
	}

}
