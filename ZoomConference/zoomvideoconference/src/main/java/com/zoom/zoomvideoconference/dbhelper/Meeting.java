package com.zoom.zoomvideoconference.dbhelper;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "meeting_table")
public class Meeting {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer Id;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "user_email", nullable = false)
	private String userEmail;

	@Column(name = "isHost", nullable = false)
	private boolean isHost;

	@Column(name = "start_date", nullable = false)
	private Date startDate;

	@Column(name = "end_date", nullable = false)
	private Date endDate;

	@Column(name = "meetingId", nullable = false)
	private String meetingId;
	
	@Column(name = "mettingPass")
	private String mettingPass;
	
	public Meeting() {
	}

	public Meeting(String username, String userEmail, Date startDate, Date endDate, String meetingId) {
		this.username = username;
		this.userEmail = userEmail;
		this.isHost = true;
		this.startDate = startDate;
		this.endDate = endDate;
		this.meetingId = meetingId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public boolean getIsHost() {
		return isHost;
	}

	public void setIsHost(boolean isHost) {
		this.isHost = isHost;
	}

	public void setHost(boolean isHost) {
		this.isHost = isHost;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setMeetingId(String meetingID) {
		this.meetingId = meetingID;
	}
	
	public String getMeetingId(String meetingId) {
		return this.meetingId;
	}
	
	public void setMeetingPass(String meetingPass) {
		this.mettingPass = meetingPass;
	}
	
	public String getMeetingPass() {
		return this.mettingPass;
	}
}