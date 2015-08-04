package com.groupz.sendsms;


import java.util.Date;


import org.apache.log4j.Logger;



import com.groupz.sendsms.tables.TrackStatus;
import com.groupz.sendsms.utils.GetGeneralStatusResultantString;

public class SMSErrorManager {

	private static Logger logger = Logger.getLogger("smsLogger");

	public String ManageError(String s) {
		String stat = null;

		try {
			String invalidNo=ProcessMessagesInTable.prop.getProperty("invalidnumber");
			String commonerr = ProcessMessagesInTable.prop.getProperty("commonerr");
			String invalidcc = ProcessMessagesInTable.prop.getProperty("countryCodeerr");
			

			boolean cont = s.contains("0000000b");
			if (cont) {

				stat = invalidNo;
			} else {

				if (s.contains("errorCode")) {

					stat = invalidcc;

				} else {
					stat = commonerr;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occured in Manage Error ", e);
		}
		return stat;

	}
	
	
	


	// inserts address and message String and resultant String into trackstatus as
	// send failed.
	public void UpdateTrackStatusforErrorStatus(String addressString, String messageString,
			String grpzmsgId, String provider, int tosize,String statusmsg) throws Exception {

		

		try {

			String statusmergString = GetGeneralStatusResultantString.getResultantStringforGeneralStatus(addressString, messageString, statusmsg);

			// Inserting String and status details into
			// trackstatus table.
			Date currentdate = new Date();

			TrackStatus trstins = new TrackStatus();
			trstins = TrackStatus.getSingleTrackStatus(grpzmsgId);

			if (trstins != null) {
				trstins.setcompleted(1);
				trstins.setresultantString(statusmergString);
				trstins.save();
			}
			else{
				TrackStatus trst = new TrackStatus();
				trst.setaddressString(addressString);
				trst.setmessageString(messageString);
				trst.setgroupzMsgId(grpzmsgId);
				trst.setprovider(provider);
				trst.setreceivedTimeStamp(currentdate);
				trst.setcompleted(1);
				trst.setresultantString(statusmergString);
				trst.setsmscount(tosize);
				trst.save();
				
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception occured in UpdateTrackStatusforBindError ",
					e);

		}

	}

}
