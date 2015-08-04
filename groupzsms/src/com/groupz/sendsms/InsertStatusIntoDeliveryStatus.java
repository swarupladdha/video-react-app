package com.groupz.sendsms;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.log4j.Logger;

import com.groupz.sendsms.tables.DeliveryStatus;

public class InsertStatusIntoDeliveryStatus {
	private static Logger logger = Logger
			.getLogger("smsLogger");
// This is for sucessful data insertion into deliverystatus.
	public void InsertintoDeliveryStatus(String jobid, String groupzMsgId,
			String addressToString, String smstext, String mobilenum,
			String provider, String status) {
		DeliveryStatus delvobj = new DeliveryStatus();

		try {
// we need to delete the error data before inserting successful data for the same groupzMsgId(in case of retry)
			DeliveryStatus delvobjdel = new DeliveryStatus();
			delvobjdel = DeliveryStatus.getSingleDeliveryStatuserrorId(
					provider, mobilenum, groupzMsgId);

			if (delvobjdel != null) {
				DeliveryStatus.deleteDeliveryStatuserrorId(provider, mobilenum,
						groupzMsgId);
			}

			delvobj = DeliveryStatus.getSingleDeliveryStatus(provider, jobid);
			
			if (delvobj != null) {
				delvobj.setaddressToString(addressToString);
				delvobj.setgroupzMsgId(groupzMsgId);
				delvobj.setmessage(smstext);
				delvobj.setmobileNo(mobilenum);
				delvobj.save();
			}

			else if (delvobj == null) {
				DeliveryStatus delvobjnew = new DeliveryStatus();
				delvobjnew.setaddressToString(addressToString);
				delvobjnew.setgroupzMsgId(groupzMsgId);
				delvobjnew.setjobid(jobid);
				delvobjnew.setmessage(smstext);
				delvobjnew.setmsgStatus(status);
				delvobjnew.setmobileNo(mobilenum);
				delvobjnew.setprovider(provider);
				delvobjnew.save();
			}

		} catch (Exception e) {

			logger.info(
					"Exception occured in Insert data into deliverystatus table.",
					e);
			e.printStackTrace();

			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			e.printStackTrace(printWriter);

			String errorMessage = writer.toString();
			System.out.println(errorMessage);
			if (errorMessage != null&&errorMessage.isEmpty()==false) {

				if (errorMessage.contains("Duplicate")) {
					delvobj = DeliveryStatus.getSingleDeliveryStatus(provider,
							jobid);
					delvobj.setaddressToString(addressToString);
					delvobj.setgroupzMsgId(groupzMsgId);
					delvobj.setmessage(smstext);
					delvobj.setmobileNo(mobilenum);
					delvobj.save();
				}
			}

		}
	}
// This method is for failure data insertion into deliverystatus.(used in catch block).
	public void InsertErrorDeliveryStatus(String jobid, String groupzMsgId,
			String addressToString, String smstext, String mobilenum,
			String provider, String status,String errorData) {
		DeliveryStatus delvobj = new DeliveryStatus();
//When retry happens it should update the data to same groupzmsgId else  extra entries will be in table.(in case of retry)
		try {
			String jobiderr = GenerateSmsIds.generateJobId(provider);

			delvobj = DeliveryStatus.getSingleDeliveryStatuserrorId(provider,
					mobilenum, groupzMsgId);
			if (delvobj != null) {
				delvobj.setaddressToString(addressToString);
				delvobj.setgroupzMsgId(groupzMsgId);
				delvobj.setmessage(smstext);
				delvobj.setmobileNo(mobilenum);
				delvobj.setmsgStatus(status);
				delvobj.seterrorData(errorData);
				delvobj.save();
			}

			else if (delvobj == null) {
				DeliveryStatus delvobjnew = new DeliveryStatus();
				delvobjnew.setaddressToString(addressToString);
				delvobjnew.setgroupzMsgId(groupzMsgId);
				delvobjnew.setjobid(jobiderr);
				delvobjnew.setmessage(smstext);
				delvobjnew.setmsgStatus(status);
				delvobjnew.setmobileNo(mobilenum);
				delvobjnew.setprovider(provider);
				delvobjnew.seterrorData(errorData);
				delvobjnew.save();
			}

		} catch (Exception e) {

			logger.info(
					"Exception occured in Insert data into deliverystatus table.",
					e);
			e.printStackTrace();

			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			e.printStackTrace(printWriter);

			String errorMessage = writer.toString();
			System.out.println(errorMessage);
			

		}
	}
}
