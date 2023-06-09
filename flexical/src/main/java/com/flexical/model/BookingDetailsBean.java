package com.flexical.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class BookingDetailsBean {


    @NotBlank(message="clientKey is mandatory")
	private String clientKey;
    
    @NotBlank(message="vendorId can't be empty")
	private String vendorId = "-1";

    @NotBlank(message="resourceId is mandatory")
	private String resourceId;

    @NotBlank(message="userId is mandatory")
	private String userId;

    @NotBlank(message="startTime is mandatory")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "Invalid datetime format")
    private String startTime;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

}
