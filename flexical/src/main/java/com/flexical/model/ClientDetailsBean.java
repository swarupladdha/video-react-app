package com.flexical.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClientDetailsBean {

	@NotBlank(message = "orgName is Mandatory")
	private String orgName;
	
	@NotBlank(message = "address is Mandatory")
	private String address;

	@NotBlank(message = "contact is Mandatory")
    @Pattern(regexp = "\\d{10}", message = "Contact number must be a 10-digit number")
	private String contact;

	@ValidSlotTimeId
	private int slottimeId;
	
	private String timezone = "utc";

	@Size(min = 0, max = 1, message = "Status shoould be 0 or 1")
	private String status = "0";//0 = enable, 1 = disable

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public int getSlottimeId() {
		return slottimeId;
	}

	public void setSlottimeId(int slottimeId) {
		this.slottimeId = slottimeId;
	}

	public String getTimezone() {
		return timezone;
	}

	public String getStatus() {
		return status;
	}
	
}
