package com.groupz.sendsms.tables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import com.gpzhibernate.BaseDatabaseObject;
import com.gpzhibernate.DBCommonOpertion;


@Entity
@Table(name = "trackstatus")
public class TrackStatus extends BaseDatabaseObject implements Serializable {
	
	private static Logger logger = Logger
			.getLogger(TrackStatus.class);

	private static final long serialVersionUID = -1;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;

	@Column(name = "addressString")
	private String addressString;

	@Column(name = "messageString")
	private String messageString;

	@Column(name = "groupzMsgId")
	private String groupzMsgId;

	@Column(name = "resultantString")
	private String resultantString;

	@Column(name = "provider")
	private String provider;

	@Column(name = "completed")
	private int completed;

	@Column(name = "receivedTimeStamp")
	private Date receivedTimeStamp;

	@Column(name = "groupzid")
	private int groupzId;

	@Column(name = "smscount")
	private int smscount;
	
	@Column(name = "segments")
	private int segments;
	
	@Column(name = "smscost")
	private float smscost;
	
	@Column(name = "mappingId")
	private int mappingId;

	@Column(name = "memberId")
	private int memberId;

	@Column(name = "userid")
	private int userid;

	@Column(name = "username")
	private String username;

	@Column(name = "servicetype")
	private String servicetype;

	@Column(name = "functiontype")
	private String functiontype;

	@Column(name = "networkid")
	private String networkid;

	@Column(name = "networkname")
	private String networkname;

	@Column(name = "transactionid")
	private int transactionid;

	@Column(name = "SourceType")
	private String sourceType;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getgroupzMsgId() {
		return groupzMsgId;
	}

	public void setgroupzMsgId(String groupzMsgId) {
		this.groupzMsgId = groupzMsgId;
	}

	public String getaddressString() {
		return addressString;
	}

	public void setaddressString(String addressString) {
		this.addressString = addressString;
	}

	public String getmessageString() {
		return messageString;
	}

	public void setmessageString(String messageString) {
		this.messageString = messageString;
	}

	public String getprovider() {
		return provider;
	}

	public void setprovider(String provider) {
		this.provider = provider;
	}

	public String getresultantString() {
		return resultantString;
	}

	public void setresultantString(String resultantString) {
		this.resultantString = resultantString;
	}

	public int getcompleted() {
		return completed;
	}

	public void setcompleted(int completed) {
		this.completed = completed;
	}
	
	
	public int getsmscount() {
		return smscount;
	}

	public void setsmscount(int smscount) {
		this.smscount = smscount;
	}
	
	public int getsegments() {
		return segments;
	}

	public void setsegments(int segments) {
		this.segments = segments;
	}
	
	public float getsmscost() {
		return smscost;
	}

	public void setsmscost(float smscost) {
		this.smscost= smscost;
	}


	public Date getreceivedTimeStamp() {
		return receivedTimeStamp;
	}

	public void setreceivedTimeStamp(Date receivedTimeStamp) {
		this.receivedTimeStamp = receivedTimeStamp;
	}

	public String getnetworkid() {
		return networkid;
	}

	public void setnetworkid(String networkid) {
		this.networkid = networkid;
	}

	public String getnetworkname() {
		return networkname;
	}

	public void setnetworkname(String networkname) {
		this.networkname = networkname;
	}

	public String getFunctiontype() {
		return functiontype;
	}

	public void setFunctiontype(String functiontype) {
		this.functiontype = functiontype;
	}

	public String getServicetype() {
		return servicetype;
	}

	public void setServicetype(String servicetype) {
		this.servicetype = servicetype;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getTransactionid() {
		return transactionid;
	}

	public void setTransactionid(int transactionid) {
		this.transactionid = transactionid;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public int getGroupzId() {
		return groupzId;
	}

	public void setGroupzId(int groupzId) {
		this.groupzId = groupzId;
	}

	public int getMappingId() {
		return mappingId;
	}

	public void setMappingId(int mappingId) {
		this.mappingId = mappingId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public static TrackStatus getSingleTrackStatus(String groupzMsgId) {
		String queryCond = "  ";
		if (groupzMsgId == null) {
			return null;
		}
		queryCond = " groupzMsgId = '" + groupzMsgId + "'";
		try {
			System.out.println("The query condition is : " + queryCond);
			TrackStatus statusResult = (TrackStatus) DBCommonOpertion
					.getSingleDatabaseObject(TrackStatus.class, queryCond);

			return statusResult;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(
					"Exception occured in TrackStatus table class",
					e);
			return null;
		}
	}

	public static List<TrackStatus> listFalsetrackStatus() {
		String queryCond = "  ";
		// int comp = 0;

		try {

			queryCond = " completed = 0";

			System.out.println("The query condition is : " + queryCond);

			List<TrackStatus> statusResult = (List<TrackStatus>) DBCommonOpertion
					.getDatabaseObjects(TrackStatus.class, queryCond);
			System.out.println("The return list is : " + statusResult);
			return statusResult;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(
					"Exception occured in TrackStatus table class",
					e);
			return new ArrayList<TrackStatus>();
		}
	}

	public static List<TrackStatus> listoldtrackStatus(String durValue) {
		String queryCond = "  ";

		try {
			queryCond = " receivedTimeStamp < (NOW()-INTERVAL " + durValue
					+ " )";

			System.out.println("The query condition is : " + queryCond);

			List<TrackStatus> statusResult = (List<TrackStatus>) DBCommonOpertion
					.getDatabaseObjects(TrackStatus.class, queryCond);
			System.out.println("The return list is : " + statusResult);
			return statusResult;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(
					"Exception occured in TrackStatus table class",
					e);
			return new ArrayList<TrackStatus>();
		}
	}
}
