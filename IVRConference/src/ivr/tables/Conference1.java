package ivr.tables;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.gpzhibernate.BaseDatabaseObject;

@Entity
@Table(name= "conference1")
public class Conference1 extends BaseDatabaseObject implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	private int id;
	
	@Column(name = "participateNo")
	private String participate_no;
	
	@Column(name = "callerId")
	private String caller_id;
	
	@Column(name = "startTime")
	private Date start_time;
	
	@Column(name = "endTime")
	private Date end_time;
	
	@Column(name = "callDuration")
	private String duration;
	
	@Column(name = "callStatus")
	private String call_status;
	
	@Column(name = "ringingTime")
	private String ringing_time;
	
	@Column(name = "sessionId")
	private String session_id;
	
	
	
	
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="conferenceId", referencedColumnName="Id")
	private ConferenceCall conferenceId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCaller_id() {
		return caller_id;
	}

	public void setCaller_id(String caller_id) {
		this.caller_id = caller_id;
	}

	public String getParticipate_no() {
		return participate_no;
	}

	public void setParticipate_no(String participate_no) {
		this.participate_no = participate_no;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public String getRinging_time() {
		return ringing_time;
	}

	public void setRinging_time(String ringing_time) {
		this.ringing_time = ringing_time;
	}
	
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getCall_status() {
		return call_status;
	}

	public void setCall_status(String call_status) {
		this.call_status = call_status;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public ConferenceCall getConferenceId() {
		return conferenceId;
	}

	public void setConferenceId(ConferenceCall conference_id) {
		this.conferenceId = conference_id;
	}
	
	
	
}

