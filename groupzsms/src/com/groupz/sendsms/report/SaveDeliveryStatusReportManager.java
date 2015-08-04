package com.groupz.sendsms.report;



import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import com.groupz.sendsms.tables.DeliveryStatus;

public class SaveDeliveryStatusReportManager {

	private static Logger logger = Logger
			.getLogger("smsLogger");
	static {

		try {

			System.setProperty("Hibernate-Url", SaveDeliveryStatusReportManager.class
					.getResource("/Hibernate.cfg.xml").toString());
			
			Properties logProperties = null;
			logProperties = new Properties(System.getProperties());
			
			InputStream in = SaveDeliveryStatusReportManager.class.getResourceAsStream("/log4j.properties"); 
			logProperties.load(in);
			
			logger.info("Logging and hibernate url initialized in GroupzSmsmService class payaswinibhat ");
			System.out.println("entered config");

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception occured in GroupzSmsmService class.", e);
		}

	}


	public void saveDeliveryReport(String statusreport, String providerCode)
			throws Exception {

		String jobid = null;
		String status = null;
		DeliveryStatus delStats = new DeliveryStatus();
		String provider = providerCode;

		try {
			ArrayList<String> reportList = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(statusreport, " ");

			while (st.hasMoreTokens()) {
				String token = st.nextToken().trim();
				reportList.add(token);

			}

			for (int i = 0; i < reportList.size(); i++) {
				String str = reportList.get(i);
				if (str.contains("id")) {
					String[] idstr = str.split(":");
					jobid = idstr[1];
				}

				if (str.contains("stat")) {
					String[] idstrstat = str.split(":");
					status = idstrstat[1];
				}
			}

			delStats = DeliveryStatus.getSingleDeliveryStatus(provider, jobid);
			if (delStats != null) {
				delStats.setmsgStatus(status);
				delStats.save();
			}

			if (delStats == null) {

				DeliveryStatus delStatsinsert = new DeliveryStatus();
				delStatsinsert.setjobid(jobid);
				delStatsinsert.setprovider(provider);
				delStatsinsert.setmsgStatus(status);
				delStatsinsert.save();

			}

		} catch (Exception e) {

			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			e.printStackTrace(printWriter);
			String errorMessage = writer.toString();
			System.out.println(errorMessage);

			if (errorMessage != null) {

				if (errorMessage.contains("Duplicate")) {

					delStats.setmsgStatus(status);
					delStats.save();

				}

			}

			e.printStackTrace();
			logger.info(
					"Exception occured in Delivery Status Report Manager method of Delivery Report Status",
					e);

		}

	}

	/*public static void main(String[] args) {

		String report = "id:0000092053 sub:000 dlvrd:000 submit date:1212111142 done date:1212111142 stat:DELIVRD err:000 text:Dear Test, New Servi";
		try{
		SaveDeliveryStatusReportManager delstat = new SaveDeliveryStatusReportManager();
		delstat.saveDeliveryReport(report, "smscountry");
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
}
