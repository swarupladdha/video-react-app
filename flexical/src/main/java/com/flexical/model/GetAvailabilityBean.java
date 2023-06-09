package com.flexical.model;

import jakarta.validation.constraints.NotBlank;

public class GetAvailabilityBean {

    @NotBlank(message="clientKey is mandatory")
	private String clientKey;
    
    @NotBlank(message="vendorId can't be empty")
	private String vendorId = "-1";

    @NotBlank(message="resourceId is mandatory")
	private String resourceId;

	public String getClientKey() {
		return clientKey;
	}

	public void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}

	public String getVendorId() {
		return vendorId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

}
