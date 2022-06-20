package com.zoom.zoomvideoconference.dbhelper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "guest_user")
public class GuestUser {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;
	
	@Column(nullable = false)
	private String guestEmail;
	
	@Column(nullable = false)
	private String guestPassword;
	
	@Column(nullable = false)
	private String isHost;
	
	private String mobileNo;
	
	private String otp;
	
	public GuestUser(String guestEmail, String guestPassword, String isHost, String mobileNo) {
		super();
		this.guestEmail = guestEmail;
		this.guestPassword = guestPassword;
		this.isHost = isHost;
		this.mobileNo = mobileNo;
	}

	public String getGuestEmail() {
		return guestEmail;
	}

	public void setGuestEmail(String guestEmail) {
		this.guestEmail = guestEmail;
	}

	public String getGuestPassword() {
		return guestPassword;
	}

	public void setGuestPassword(String guestPassword) {
		this.guestPassword = guestPassword;
	}

	public String getIsHost() {
		return isHost;
	}

	public void setIsHost(String isHost) {
		this.isHost = isHost;
	}
	
	public void setMobile(String mobile) {
		this.mobileNo = mobile;
	}
	
	public String getMobile() {
		return this.mobileNo;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
	
	
}
