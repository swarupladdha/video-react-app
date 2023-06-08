package com.flexical.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ClientSettingsBean {

	@NotBlank(message = "clientKey is mandatory")
	private String clientKey;
	
    @NotNull(message = "availability is mandatory")
    @Size(min = 1, message = "availability should be greater than or equal to 1")
    private List<Availability> availability;
    

	public void AvailabilitySettings() {
		// TODO Auto-generated constructor stub
		availability = new ArrayList<>();
        availability.add(new Availability(1, 0, "00:00:00", "00:00:00"));
        availability.add(new Availability(2, 1, "09:00:00", "17:00:00"));
        availability.add(new Availability(3, 1, "09:00:00", "17:00:00"));
        availability.add(new Availability(4, 1, "09:00:00", "17:00:00"));
        availability.add(new Availability(5, 1, "09:00:00", "17:00:00"));
        availability.add(new Availability(6, 1, "09:00:00", "17:00:00"));
        availability.add(new Availability(7, 0, "00:00:00", "00:00:00"));
	}


	public String getClientKey() {
		return clientKey;
	}

	public void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}

	public List<Availability> getAvailability() {
		return availability;
	}

	public void setAvailability(List<Availability> availability) {
		this.availability = availability;
	}
	
}
