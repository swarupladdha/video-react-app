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
@Table(name = "deliverystatus")

public class DeliveryStatus extends BaseDatabaseObject implements Serializable {
	
	private static Logger logger = Logger
			.getLogger(DeliveryStatus.class);

	private static final long serialVersionUID = -1;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;


	@Column(name = "jobid")
	private String jobid;

	@Column(name = "groupzMsgId")
	private String groupzMsgId;

	@Column(name = "addressToString")
	private String addressToString;

	@Column(name = "mobileNo")
	private String mobileNo;

	@Column(name = "msgStatus")
	private String msgStatus;

	@Column(name = "message")
	private String message;

	@Column(name = "provider")
	private String provider;
	
	@Column(name = "errorData")
	private String errorData;

	@Column(name = "doneTimestamp")
	private Date doneTimestamp;

	
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getmobileNo() {
		return mobileNo;
	}

	public void setmobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getjobid() {
		return jobid;
	}

	public void setjobid(String jobid) {
		this.jobid = jobid;
	}

	public String getmsgStatus() {
		return msgStatus;
	}

	public void setmsgStatus(String msgStatus) {
		this.msgStatus = msgStatus;
	}

	public String getgroupzMsgId() {
		return groupzMsgId;
	}

	public void setgroupzMsgId(String groupzMsgId) {
		this.groupzMsgId = groupzMsgId;
	}

	public String getaddressToString() {
		return addressToString;
	}

	public void setaddressToString(String addressToString) {
		this.addressToString = addressToString;
	}

	public String getprovider() {
		return provider;
	}

	public void setprovider(String provider) {
		this.provider = provider;
	}

	public String getmessage() {
		return message;
	}

	public void setmessage(String message) {
		this.message = message;
	}

	public Date getdoneTimestamp() {
		return doneTimestamp;
	}

	public void setdoneTimestamp(Date doneTimestamp) {
		this.doneTimestamp = doneTimestamp;
	}
	
	public String geterrorData() {
		return errorData;
	}

	public void seterrorData(String errorData) {
		this.errorData = errorData;
	}

	public static DeliveryStatus getSingleDeliveryStatus(String providername,String jobId) {
		String queryCond = "  ";
		if (providername == null && jobId == null) {
			return null;
		}
		queryCond = " provider = '" + providername + "' and jobid='" + jobId + "'";
		try {
			System.out.println("The query condition is : " + queryCond);
			DeliveryStatus smsResult = (DeliveryStatus) DBCommonOpertion
					.getSingleDatabaseObject(DeliveryStatus.class, queryCond);

			return smsResult;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(
					"Exception occured in deliverystatus table class",
					e);
			return null;
		}
	}
	
	public static DeliveryStatus getSingleDeliveryStatuserrorId(String provider,String mobilenum,String groupzMsgId) {
		String queryCond = "  ";
		if (provider == null && mobilenum == null) {
			return null;
		}
		queryCond = " groupzMsgId = '" + groupzMsgId + "'" +" and provider = '"+provider+"'" + " and mobileNo= '"+mobilenum +"'";
		try {
			System.out.println("The query condition is : " + queryCond);
			DeliveryStatus smsResult = (DeliveryStatus) DBCommonOpertion
					.getSingleDatabaseObject(DeliveryStatus.class, queryCond);

			return smsResult;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(
					"Exception occured in deliverystatus table class",
					e);
		
			
			return null;
		}
	}

	public static List<DeliveryStatus> listDeliveryStatus(String grpzMsgid) {
		String queryCond = "  ";

		try {

			queryCond = " groupzMsgId = '" + grpzMsgid + "'";

			System.out.println("The query condition is : " + queryCond);

			List<DeliveryStatus> statusResult = (List<DeliveryStatus>) DBCommonOpertion
					.getDatabaseObjects(DeliveryStatus.class, queryCond);
			System.out.println("The return list is : " + statusResult);
			return statusResult;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<DeliveryStatus>();
		}
	}

	
	
	public static int deleteDeliveryStatus(String groupzMsgId) {
		int result = -1;
		String queryCond = "  ";
		if (groupzMsgId == null) {
			return result;
		}
		queryCond = " groupzMsgId = '" + groupzMsgId + "'";
		try {
			System.out.println("The query condition is : " + queryCond);
			result = (Integer) DBCommonOpertion.deleteDatabaseObject(
					DeliveryStatus.class, queryCond);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
	}
	
	public static int deleteDeliveryStatuserrorId(String provider,
			String mobilenum,String groupzMsgId) {
		int result = -1;
		String queryCond = "  ";
		if (groupzMsgId == null) {
			return result;
		}
		queryCond = " groupzMsgId = '" + groupzMsgId + "'" +" and provider = '"+provider+"'" + " and mobileNo= '"+mobilenum +"'";
		try {
			System.out.println("The query condition is : " + queryCond);
			result = (Integer) DBCommonOpertion.deleteDatabaseObject(
					DeliveryStatus.class, queryCond);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
	}

}
