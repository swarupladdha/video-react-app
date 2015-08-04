package com.groupz.sendsms.utils;

import java.io.InputStream;
import java.util.Properties;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

public class CreateResponseString {
	private static Logger logger = Logger.getLogger("smsLogger");
	static Properties prop = new Properties();
	static {
		try {
			InputStream in = CreateResponseString.class
					.getResourceAsStream("/smppConf.properties");
			prop.load(in);

		} catch (Exception e) {
			logger.info(
					"Exception occured in load property file in create response xml class.",
					e);
			e.printStackTrace();
		}
	}

	public static String Createrespobject(String responseString, String statuscode) {
		String xmlresponse = "error";
		String stserror = null;
		try {
			stserror = prop.getProperty("errorcode");
			
			JSONObject responseJson = new JSONObject();
			JSONObject finalReportJson = new JSONObject();
			JSONObject reportJson = new JSONObject();
			reportJson.put("status", responseString);
			reportJson.put("statuscode", statuscode);
			finalReportJson.put("report", reportJson);
			responseJson.put("response", finalReportJson);
			xmlresponse=responseJson.toString();
			System.out.println(xmlresponse);
		} catch (Exception e) {
			logger.info(
					"Exception occured while creating response xml string.", e);
			e.printStackTrace();

			String resultstr = "Technical Error Occured";

			statuscode = stserror;

		
			xmlresponse = CreateResponseString.Createrespobject(resultstr, statuscode);

			return xmlresponse;

		}
		return xmlresponse;

	}
	
/*	public static void main(String[] args) {

		CreateResponseString.Createrespobject("sucscess","0");

		
	}*/

}
