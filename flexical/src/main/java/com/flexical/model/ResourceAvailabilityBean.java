package com.flexical.model;

import java.util.Date;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResourceAvailabilityBean {
	
	@NotNull(message = "clientKey is mandatory")
	private String clientKey;
	
	private String vendorId;

	@NotNull(message = "resourceId is mandatory")
	@Size(min = 2, message = "resourceId can't be empty")
	private String resourceId;
	
	private Date date;
	
	public String getClientKey() {
		return clientKey;
	}
	public void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}
	public String getVendorId() {
		return vendorId;
	}
	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
