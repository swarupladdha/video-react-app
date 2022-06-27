package com.zoom.zoomvideoconference.dbhelper;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "end_meeting_table")
public class EndMeeting {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer Id;

	@Column(name = "host_name", nullable = false)
	private String hostName;

	@Column(name = "host_email", nullable = false)
	private String hostEmail;

	@Column(name = "host_meeting", nullable = false)
	private String meetingId;

	@Column(name = "status")
	private String status;

	@Column(name = "end_time", nullable = false)
	private Date endTime;

	@Column(name = "meeting_end_body", nullable = false)
	private String meetingEndBody;

	public EndMeeting(String hostName, String hostEmail, String meetingId, String status, Date endTime,
			String meetingEndBody) {
		this.hostName = hostName;
		this.hostEmail = hostEmail;
		this.meetingId = meetingId;
		this.status = status;
		this.endTime = endTime;
		this.meetingEndBody = meetingEndBody;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostEmail() {
		return hostEmail;
	}

	public void setHostEmail(String hostEmail) {
		this.hostEmail = hostEmail;
	}

	public String getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getMeetingEndBody() {
		return meetingEndBody;
	}

	public void setMeetingEndBody(String meetingEndBody) {
		this.meetingEndBody = meetingEndBody;
	}

}
