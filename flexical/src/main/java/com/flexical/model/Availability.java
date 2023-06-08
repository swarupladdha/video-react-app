package com.flexical.model;

import java.util.ArrayList;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Availability {

    @NotBlank(message="weekdayId is mandatory")
    @Size(min=1, max =7, message = "weekdayId should be between 1 and 7")
    private Integer weekdayId;

    @NotBlank(message="working is mandatory")
    @Size(min=0, max =1, message = "working should be 0 or 1")
    private int working;

    @NotBlank(message="startTime is mandatory")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")
    private String startTime;

    @NotBlank(message="endTime is mandatory")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")
    private String endTime;

	public Availability(int i, int j, String string, String string2) {
		// TODO Auto-generated constructor stub
	}

	public Integer getWeekdayId() {
		return weekdayId;
	}

	public void setWeekdayId(Integer weekdayId) {
		this.weekdayId = weekdayId;
	}

	public int getWorking() {
		return working;
	}

	public void setWorking(int working) {
		this.working = working;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}
