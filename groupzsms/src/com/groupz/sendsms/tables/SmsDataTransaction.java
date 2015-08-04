package com.groupz.sendsms.tables;


import java.io.Serializable;
import java.util.Date;
import java.util.List;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import com.gpzhibernate.BaseDatabaseObject;
import com.gpzhibernate.DBCommonOpertion;

import com.mysql.jdbc.Blob;


@Entity
@Table(name = "smsqueue")

public class SmsDataTransaction  extends BaseDatabaseObject implements Serializable {
	
	private static Logger logger = Logger
			.getLogger(SmsDataTransaction.class);


	private static final long serialVersionUID = -1;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "MsgId")
	private int MsgId;
	
	@Column(name = "counter")
	private int counter;
	
	@Column(name = "Address")
	private String Address;
	
	@Column(name = "Message")
	private String Message;
	
	@Column(name = "CustomData")
	private String CustomData;
	
	@Column(name = "groupzMsgId")
	private String groupzMsgId;
	
	@Column(name = "Provider")
	private String Provider;
	
	@Column(name = "Attachment")
	@Lob 
	private Blob Attachment;
	
	
	
	@Column(name = "Time")
	private Date Time;
	
	@Column(name = "Date")
	private Date Date;
	
	public Date getDate() {
		return Date;
	}

	public void setDate(Date Date) {
		this.Date = Date;
	}
	
	public Date getTime() {
		return Time;
	}

	public void setTime(Date Time) {
		this.Time = Time;
	}
	
	
	public int getMsgId() {
		return MsgId;
	}

	public void setMsgId(int MsgId) {
		this.MsgId = MsgId;
	}
	
	public String getgroupzMsgId() {
		return groupzMsgId;
	}

	public void setgroupzMsgId(String groupzMsgId) {
		this.groupzMsgId = groupzMsgId;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String Address) {
		this.Address = Address;
	}
	
	public String getMessage() {
		return Message;
	}

	public void setMessage(String Message) {
		this.Message = Message;
	}
	
	
	public int getcounter() {
		return counter;
	}

	public void setcounter(int counter) {
		this.counter = counter;
	}
	

	
	public String getCustomData() {
		return CustomData;
	}

	public void setCustomData(String CustomData) {
		this.CustomData = CustomData;
	}
	

	
	public String getProvider() {
		return Provider;
	}

	public void setProvider(String Provider) {
		this.Provider = Provider;
	}
	

	
	public Blob getAttachment() {
		return Attachment;
	}

	public void setAttachment(Blob Attachment) {
		this.Attachment = Attachment;
	}

	
	public static List<SmsDataTransaction> listAllSmsData( int id,String counterFlag,String retryCount,String modVal) {
		
	
		String queryCond = "  ";
		
		if (counterFlag.equals("1")){
			queryCond = "counter<" +retryCount+" and MsgId % "+ modVal + "=" + id +" and Now()>=Date and groupzMsgId is not null";
		}
		else{
		queryCond = "MsgId % "+ modVal + "=" + id +" and counter<" +retryCount+" and groupzMsgId is not null";
		}
		try {
			///System.out.println("The query condition is : " + queryCond);
			 List<SmsDataTransaction>  smsData = ( List<SmsDataTransaction> ) DBCommonOpertion
						.getDatabaseObjects(SmsDataTransaction.class, queryCond);

			return smsData;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(
					"Exception occured in SmsDataTransaction table class",
					e);
			return null;
		}
	}

	public static int deleteSmsData(int iddel) {
		int result=-1;
		String queryCond = "  ";
		if (iddel ==0 ) {
			return result;
		}
		queryCond = " MsgId = " + iddel;
		try {
			System.out.println("The query condition is : " + queryCond);
			result = (Integer) DBCommonOpertion
					.deleteDatabaseObject(SmsDataTransaction.class, queryCond);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(
					"Exception occured in SmsDataTransaction table class",
					e);
			return result;
		}
	}
	
	
}
