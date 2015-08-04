package com.groupz.sendsms.report;

import java.io.InputStream;

import java.util.Properties;
import java.util.concurrent.Callable;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;


import org.apache.log4j.Logger;



import com.groupz.sendsms.tables.TrackStatus;
import com.groupz.sendsms.utils.*;

public class GetSmsDeliveryReport implements Callable<String> {
	private static Logger logger = Logger.getLogger("smsLogger");
	private String dataStr = null;
	static Properties prop = new Properties();
	static {

		try {
			InputStream in = GetSmsDeliveryReport.class
					.getResourceAsStream("/smppConf.properties");
			prop.load(in);

		} catch (Exception e) {
			logger.info("Exception occured in load property file.", e);
			e.printStackTrace();
		}
	}
	static {

		try {

			System.setProperty("Hibernate-Url", GetSmsDeliveryReport.class
					.getResource("/Hibernate.cfg.xml").toString());

			Properties logProperties = null;
			logProperties = new Properties(System.getProperties());

			InputStream in = GetSmsDeliveryReport.class
					.getResourceAsStream("/log4j.properties");
			logProperties.load(in);

			logger.info("Logging and hibernate url initialized in GetSmsDeliveryReport class");
			System.out.println("entered config");

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception occured in GroupzSmsmService class.", e);
		}

	}

	public GetSmsDeliveryReport(String dataString) {
		this.dataStr = dataString;

	}

	public String call() {
		String resultstr = null;
		String statuscode = null;
		String outputstr = null;
	
		String stserror = null;
		MergeSMSStatusReportManager mergobj = new MergeSMSStatusReportManager();
		String rgrpzmsgId = null;
				try {

			
			stserror = prop.getProperty("errorcode");
			if (dataStr != null) {
				

				XMLSerializer xmlSerializer = new XMLSerializer();
				JSON json = xmlSerializer.read(dataStr);
				JSONObject jo = (JSONObject) JSONSerializer.toJSON(json);

				JSONObject joreq = (JSONObject) jo.get("request");
				JSONObject joreport = (JSONObject) joreq.get("report");
				
				rgrpzmsgId = joreport.getString("grpzmsgid");
				
					if (rgrpzmsgId != null && rgrpzmsgId.isEmpty() == false && rgrpzmsgId.equals("[]")==false) {

						TrackStatus ts = TrackStatus
								.getSingleTrackStatus(rgrpzmsgId);
						if (ts != null) {
							int completed = ts.getcompleted();

							String reportString = ts.getresultantString();
							if (completed == 0) {
								outputstr = mergobj.getResulatantreportString(rgrpzmsgId);
								if(outputstr==null || outputstr.isEmpty()==true){
									outputstr = reportString;
								}
							} else {

								outputstr = reportString;
							}
						} else {

							resultstr = "Report Does not Exists";
							
							statuscode = stserror;

							
							outputstr = CreateResponseString.Createrespobject(resultstr,
									statuscode);

						}

					}else{
						resultstr = "Please provide the groupzMessageId for receiving the report.";
						statuscode = stserror;
						logger.info("GroupzMessage ID missing.");
						outputstr = CreateResponseString.Createrespobject(resultstr,
								statuscode);
					}

				
				}
			
				

			// Thread.yield();

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception occured in Get SMS Report method Status", e);
			resultstr = "Technical Error occured";

			statuscode = stserror;

			outputstr = CreateResponseString.Createrespobject(resultstr,
					statuscode);

			return outputstr;
		}

		return outputstr;

	}

	/*public static void main(String[] args) {

		String dataString = "<xml><request><report><grpzmsgid>1392210016267</grpzmsgid></report></request></xml>";
		try {
			ExecutorService getreportTPExecSvc = Executors
					.newFixedThreadPool(1);
			final Future<String> task;
			task = getreportTPExecSvc.submit(new GetSmsDeliveryReport(
					dataString));
			String result = task.get();
			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}*/

}
