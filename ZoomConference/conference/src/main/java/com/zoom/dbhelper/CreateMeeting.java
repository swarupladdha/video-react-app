package com.zoom.dbhelper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/*
 * All the fields in this class should be overridden and 
 * 
 * as of now some fields are set to default values 
 * 
 * */

@Entity
@Table(name = "createMeeting")
public class CreateMeeting {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long _id;

	@Column(name = "meetingagenda", nullable = false)
	private String meetingAgenda = "My Meeting Agenda";

	@Column(name = "defaultpassword", nullable = false)
	private String defaultPassword = "12345";

	@Column(name = "meetingduration", nullable = false)
	private String meetingDuration = "60";

	@Column(name = "meetingpassword", nullable = false)
	private String meetingPassword = "12345";

	@Column(name = "preschedule", nullable = false)
	private boolean preSchedule = false;

	private String[] scheduledFor;

	@Column(name = "exptime", nullable = false)
	private String expTime = "60";

	// This field should be in utc formate
	@Column(name = "start_time", nullable = false)
	private String startTime;

	private boolean allow_multiple_devices = false;

	@Column(name = "alternativ_host", nullable = false)
	private String alternative_hosts;

	@Column(name = "meeting_authentication", nullable = false)
	private boolean meeting_authentication = true;

	// only invited candidates are going join other than that should not be allowed
	// in the meeting
	private String[] meetingInvities;

	@Column(name = "mettingtopic", nullable = false)
	private String meetingTopic = "My Meeting Topic";

	@Column(name = "meetingid", nullable = true)
	private long meetingId;
	
	public void setMeetingId(int meetingId) {
		this.meetingId = this.meetingId;
	}
}
