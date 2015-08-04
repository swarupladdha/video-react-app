package com.groupz.sendsms.tables;

import java.io.Serializable;

import java.util.Date;

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
@Table(name = "heartbeatcheck")
public class HeartBeatTable extends BaseDatabaseObject implements Serializable {
	public static Logger logger = Logger.getLogger("smscountryLogger");
	private static final long serialVersionUID = -1;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;

	@Column(name = "doneTimestamp")
	private Date doneTimestamp;

	@Column(name = "provider")
	private String provider;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getdoneTimestamp() {
		return doneTimestamp;
	}

	public void setdoneTimestamp(Date doneTimestamp) {
		this.doneTimestamp = doneTimestamp;
	}

	public String getprovider() {
		return provider;
	}

	public void setprovider(String provider) {
		this.provider = provider;
	}

	public static HeartBeatTable getSingleheartbeat(String provider) {
		String queryCond = "  ";
		if (provider == null) {
			return null;
		}
		queryCond = " provider = '" + provider + "'";
		try {
			System.out.println("The query condition is : " + queryCond);
			HeartBeatTable statusResult = (HeartBeatTable) DBCommonOpertion
					.getSingleDatabaseObject(HeartBeatTable.class, queryCond);

			return statusResult;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception occured in heartbeat table class", e);
			return null;
		}
	}
}
