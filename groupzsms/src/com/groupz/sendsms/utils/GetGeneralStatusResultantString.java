package com.groupz.sendsms.utils;

import java.io.InputStream;

import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;

public class GetGeneralStatusResultantString {

	private static Logger logger = Logger.getLogger("smsLogger");
	static Properties prop = new Properties();
	static {
		try {
			InputStream in = GetGeneralStatusResultantString.class
					.getResourceAsStream("/smppConf.properties");
			prop.load(in);

		} catch (Exception e) {
			logger.info(
					"Exception occured in load property file in GetGeneralStatus xml class.",
					e);
			e.printStackTrace();
		}
	}

	public static String getResultantStringforGeneralStatus(
			String addressJsnoString, String messageJsnoString, String statusmsg) {

		String statusmergXML = null;
		String stsucess = null;
		String stserror = null;
		String statuscode = null;
		JSONArray jsntolistarry = null;
		JSONArray jsntoarraytemp = new JSONArray();
		JSONObject jonewtolist = new JSONObject();

		JSONObject jonewtoarray = new JSONObject();
		try {
			stsucess = prop.getProperty("sucesscode");
			stserror = prop.getProperty("errorcode");

			JSONObject addressJsno = (JSONObject) JSONSerializer
					.toJSON(addressJsnoString);
			System.out.println(addressJsno);

			JSONObject messgJsno = (JSONObject) JSONSerializer
					.toJSON(messageJsnoString);
			
			JSONObject messageJSNO = (JSONObject) messgJsno
					.get("message");

			JSONObject joaddress = (JSONObject) addressJsno.get("address");
			System.out.println(joaddress);

			Object obj = joaddress.get("tolist");

			if (obj instanceof JSONArray) {

				jsntolistarry = joaddress.getJSONArray("tolist");

				for (int i = 0; i < jsntolistarry.size(); i++) {

					Object objtemp = jsntolistarry.get(i);

					if (objtemp instanceof JSONArray) {

						if (((JSONArray) objtemp).isEmpty()) {

							System.out.println("empty");

						}

					} else if (objtemp instanceof JSONObject) {
						JSONObject jotemp = jsntolistarry.getJSONObject(i);

						jotemp.put("status", statusmsg);

						jsntoarraytemp.add(jotemp);
					}
				}

				jonewtoarray.put("to", jsntoarraytemp);
				jonewtolist.put("tolist", jonewtoarray);

			}

			else {

				JSONObject jsntolisObj = (JSONObject) joaddress.get("tolist");
				Object objtemp = jsntolisObj.get("to");

				if (objtemp instanceof JSONArray) {

					if (((JSONArray) objtemp).isEmpty()) {

						System.out.println("empty");

					}

				} else if (objtemp instanceof JSONObject) {
					JSONObject jotemp = (JSONObject) jsntolisObj.get("to");

					jotemp.put("status", statusmsg);
					jonewtoarray.put("to", jotemp);
					jonewtolist.put("tolist", jonewtoarray);

				}
			}

			JSONObject responseJson = new JSONObject();
			JSONObject finalReportJson = new JSONObject();
			JSONObject reportJson = new JSONObject();

			reportJson.put("address", jonewtolist);
			reportJson.put("message", messageJSNO);
			reportJson.put("statuscode", stsucess);
			finalReportJson.put("report", reportJson);
			responseJson.put("response", finalReportJson);

			statusmergXML = responseJson.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception occured in get resultand general status ", e);

			String resultstr = "Sms is in process.";

			statuscode = stserror;

			statusmergXML = CreateResponseString.Createrespobject(resultstr,
					statuscode);

			return statusmergXML;

		}

		return statusmergXML;
	}

}
