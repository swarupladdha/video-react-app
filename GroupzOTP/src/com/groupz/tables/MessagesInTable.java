package com.groupz.tables;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.gpzhibernate.BaseDatabaseObject;
import com.gpzhibernate.DontPersistWhenSerializing;

@Entity
@Table(name = "MessagesInTable")
public class MessagesInTable extends BaseDatabaseObject implements Serializable {

	private static final long serialVersionUID = -6606640260934134100L;

	public final static int EMAIL_TYPE = 0;
	public final static int SMS_TYPE = 1;
	public final static int EMAIL_AND_SMS_TYPE = 2;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "MsgId")
	@DontPersistWhenSerializing
	private int id;

	@Column(name = "MsgType")
	private int msgType;

	@Column(name = "Address", columnDefinition = "LONGTEXT")
	private String address;

	@Column(name = "Message", length = 8192)
	private String message;

	@Column(name = "Time")
	private Time time;

	@Column(name = "Date", columnDefinition = "date")
	private Date date;

	@Column(name = "Attachment")
	@Lob()
	private byte[] attachment = null;

	@Column(name = "CustomData", length = 1024)
	private String customData;
	
	@Column(name = "Version")
	private String version;
	
	@Column(name = "Provider")
	private String provider;


	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getCustomData() {
		return customData;
	}

	public void setCustomData(String customData) {
		this.customData = customData;
	}

	public byte[] getAttachment() {
		return attachment;
	}

	public void setAttachment(byte[] attachment) {
		this.attachment = attachment;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
