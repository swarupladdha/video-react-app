package com.groupz.sendsms.report;

import java.io.InputStream;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.log4j.Logger;

import com.groupz.sendsms.GroupzSmsService;
import com.groupz.sendsms.tables.DeliveryStatus;
import com.groupz.sendsms.tables.TrackStatus;
import com.groupz.sendsms.utils.CreateResponseString;

public class MergeSMSStatusReportManager implements Callable<String> {

	// private String dataStr = null;

	private static Logger logger = Logger.getLogger("smsLogger");

	static Properties prop = new Properties();
	static {

		try {
			InputStream in = MergeSMSStatusReportManager.class
					.getResourceAsStream("/smppConf.properties");
			prop.load(in);
			System.setProperty("Hibernate-Url", MergeSMSStatusReportManager.class
					.getResource("/Hibernate.cfg.xml").toString());

		} catch (Exception e) {
			logger.info("Exception occured in load property file.", e);
			e.printStackTrace();
		}
	}

	public String call() throws Exception {
		String resultstr = null;
		String statuscode = null;
		String outputstr = null;
		String stsucess = null;
		String stserror = null;
		try {

			stsucess = prop.getProperty("sucesscode");
			stserror = prop.getProperty("errorcode");
			List<TrackStatus> trackStatus = (List<TrackStatus>) TrackStatus
					.listFalsetrackStatus();
			if (trackStatus != null) {
				if (trackStatus.size() != 0) {
					for (TrackStatus ts : trackStatus) {
						String grpzMsgid = ts.getgroupzMsgId();
						getResulatantreportString(grpzMsgid);
						resultstr = "Sucessfully Merged the Status";

					}
				}

			} else {
				resultstr = " No records to merge ";
			}

			statuscode = stsucess;

			outputstr = CreateResponseString.Createrespobject(resultstr,
					statuscode);

			// Clear records for the given duration in the property file.
			ClearDeliveryStatusRecordsManager smsobj = new ClearDeliveryStatusRecordsManager();
			String clearstatus = smsobj.clearstatusrecords();
			System.out.println(clearstatus);
			return outputstr;

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception occured in Merge Report Status", e);

			resultstr = "Technical Error occured";
			statuscode = stserror;
			outputstr = CreateResponseString.Createrespobject(resultstr,
					statuscode);

			return outputstr;
		}

	}

	public String getResulatantreportString(String grpzMsgid) throws Exception {

		String resultstr = null;
		String statuscode = null;

		String stsucess = null;
		String stserror = null;

		stsucess = prop.getProperty("sucesscode");
		stserror = prop.getProperty("errorcode");
		JSONObject jonewtoObj = new JSONObject();
		JSONArray jsntolistarry = null;
		JSONArray jsntoarraytemp = new JSONArray();
		JSONObject jonewtolist = new JSONObject();
		String[] strlist = new String[2];

		String statusmergString = null;
		TrackStatus trackstatus = new TrackStatus();

		trackstatus = TrackStatus.getSingleTrackStatus(grpzMsgid);
		if (trackstatus != null) {
			String messageStr = trackstatus.getmessageString();

			String addressStr = trackstatus.getaddressString();

			System.out.println(addressStr);

			HashMap<String, String> hmap = new HashMap<String, String>();

			try {

				List<DeliveryStatus> deliveryStatus = (List<DeliveryStatus>) DeliveryStatus
						.listDeliveryStatus(grpzMsgid);
				if (deliveryStatus != null && deliveryStatus.size() != 0) {

					for (DeliveryStatus ds : deliveryStatus) {
						String mobilenum = ds.getmobileNo();
						String jobid = ds.getjobid();
						String statusMsg = ds.getmsgStatus();
						String resultStr = jobid + "~" + statusMsg;
						hmap.put(mobilenum, resultStr);

					}

					JSONObject addressJsno = (JSONObject) JSONSerializer
							.toJSON(addressStr);
					JSONObject messgJsno = (JSONObject) JSONSerializer
							.toJSON(messageStr);

					JSONObject joaddress = (JSONObject) addressJsno
							.get("address");
					System.out.println(joaddress);
					JSONObject messageJSNO = (JSONObject) messgJsno
							.get("message");
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

								JSONObject jotemp = jsntolistarry
										.getJSONObject(i);

								String mobile = jotemp.getString("number");

								String resultStr = hmap.get(mobile);

								if (resultStr != null
										&& resultStr.isEmpty() == false) {

									strlist = resultStr.split("~");

									String jobidStr = strlist[0];
									String statusStr = strlist[1];
									jotemp.put("jobid", jobidStr);
									jotemp.put("status", statusStr);

								}

								jsntoarraytemp.add(jotemp);

							}
						}

						jonewtoObj.put("to", jsntoarraytemp);
						jonewtolist.put("tolist", jonewtoObj);

					}

					else {

						JSONObject jsntolisObj = (JSONObject) joaddress
								.get("tolist");

						Object objtemp = jsntolisObj.get("to");

						if (objtemp instanceof JSONArray) {

							if (((JSONArray) objtemp).isEmpty()) {

								System.out.println("empty");

							}

						} else if (objtemp instanceof JSONObject) {

							JSONObject jotemp = (JSONObject) jsntolisObj
									.get("to");

							String mobile = jotemp.getString("number");

							String resultStr = hmap.get(mobile);

							if (resultStr != null
									&& resultStr.isEmpty() == false) {

								strlist = resultStr.split("~");

								String jobidStr = strlist[0];
								String statusStr = strlist[1];
								jotemp.put("jobid", jobidStr);
								jotemp.put("status", statusStr);

							}

							jonewtoObj.put("to", jotemp);
							jonewtolist.put("tolist", jonewtoObj);

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

					statusmergString = responseJson.toString();

					trackstatus.setresultantString(statusmergString);

					trackstatus.save();

				}

			}

			catch (Exception e) {
				e.printStackTrace();
				logger.info(
						"Exception occured in Get SMS Report Status get resultant String",
						e);

				resultstr = "Technical Error occured";
				statuscode = stserror;

				statusmergString = CreateResponseString.Createrespobject(
						resultstr, statuscode);

				return statusmergString;
			}

		}
		return statusmergString;
	}

	/*public static void main(String[] args) {

		try {

			MergeSMSStatusReportManager smsobj = new MergeSMSStatusReportManager();
			String resultxml = smsobj
					.getResulatantreportString("1392223833391");
			System.out.println(resultxml);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

}
