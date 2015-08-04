package com.groupz.sendsms.report;


import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;


import com.groupz.sendsms.tables.DeliveryStatus;
import com.groupz.sendsms.tables.TrackStatus;



public class ClearDeliveryStatusRecordsManager {
	private static Logger logger = Logger
			.getLogger("smsLogger");
	static Properties prop = new Properties();
	
	static {

		try {
			InputStream in = ClearDeliveryStatusRecordsManager.class.getResourceAsStream("/smppConf.properties"); 
			prop.load(in);
			
			 
		} catch (Exception e) {
			logger.info("Exception occured in load property file.", e);
			e.printStackTrace();
		}
	}
	

	public String clearstatusrecords() throws Exception {
		String durValue = prop.getProperty("durationValue");
		String status = "No records to clear";
		int completed = 1;
		try {
			List<TrackStatus> trackstatus = (List<TrackStatus>) TrackStatus
					.listoldtrackStatus(durValue);
			if (trackstatus.size() != 0) {

				for (TrackStatus ts : trackstatus) {
					String grpzMsId = ts.getgroupzMsgId();
					DeliveryStatus.deleteDeliveryStatus(grpzMsId);

				
					ts.setcompleted(completed);
					ts.save();

				}
				status = "Sucessfully cleared deliverystatus table old records";
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception occured in Clear Records", e);
			status = "Failed to Clear Records";
			return status;
		}

		return status;
	}
	
	/*public static void main(String[] args) {

		
		try {

			ClearDeliveryStatusRecordsManager smsobj = new ClearDeliveryStatusRecordsManager();
			 smsobj.clearstatusrecords();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/


}
