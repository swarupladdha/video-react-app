package com.groupz.sendsms;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Properties;

import net.sf.json.JSON;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.apache.log4j.Logger;

//import com.gpzbasexml.GetAndRemoveKeyValue;

import com.groupz.sendsms.tables.SmsDataTransaction;
import com.groupz.sendsms.tables.TrackStatus;
import com.groupz.sendsms.utils.CreateResponseString;
import com.groupz.sendsms.utils.GetGeneralStatusResultantString;
import com.groupz.sendsms.utils.StaticUtils;

//Class which inserts xml data into DB.
public class InsertSmsData {
	private static Logger logger = Logger.getLogger("smsLogger");
	static Properties prop = new Properties();

	static {

		try {

			System.setProperty("Hibernate-Url", GroupzSmsService.class
					.getResource("/Hibernate.cfg.xml").toString());
			InputStream in = InsertSmsData.class
					.getResourceAsStream("/smppConf.properties");
			prop.load(in);

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception occured in Insert SMS Data.", e);
		}
	}

	public String InsertData(String smsData) {
		String gmsgid = null;
		String xmlresponse = null;
		String resultstr = null;
		String statuscode = null;

		String statusMsg = null;
		String stsucess = null;
		String stserror = null;
		String stswrngdata = null;
		try {
			boolean flag = false;
			String jsnoaddressStr = null;
			String jsnomessageStr = null;
			SmsDataTransaction insObj = new SmsDataTransaction();

			String providerCode = null;

			Date scheduleTime = null;
			

			stsucess = prop.getProperty("sucesscode");
			stserror = prop.getProperty("errorcode");
			stswrngdata = prop.getProperty("wrongdatacode");
			String smsprocess = prop.getProperty("smsprocess");

			XMLSerializer xmlSerializer = new XMLSerializer();
			JSON json = xmlSerializer.read(smsData);
			JSONObject jo = (JSONObject) JSONSerializer.toJSON(json);

			JSONObject joreq = (JSONObject) jo.get("request");

			if (joreq.containsKey("address") && joreq.containsKey("message")) {
				Object objcheck = joreq.get("address");
				flag = StaticUtils.checkEmptyJSONArray(objcheck, joreq,
						"address");
				if (!flag) {

					Object objcheck1 = joreq.get("message");
					flag = StaticUtils.checkEmptyJSONArray(objcheck1, joreq,
							"message");

					if (!flag) {

						JSONObject joaddress = (JSONObject) joreq
								.get("address");
						JSONObject jomessage = (JSONObject) joreq
								.get("message");

						JSONObject addressJson = new JSONObject();
						JSONObject messageJson = new JSONObject();

						addressJson.put("address", joaddress);
						messageJson.put("message", jomessage);

						String smscost = null;
						

						int segments = 1;
						if (jomessage.containsKey("shorttext")) {

							String text = jomessage.getString("shorttext");

							if (text == null || text.isEmpty() == true
									|| text.equals("[]")) {
								flag = true;
							} else {
								int length = text.length();
								if (length > 160) {
									segments = (int) Math
											.ceil(text.length() / 153.0);
								}
								jsnoaddressStr = addressJson.toString();
								jsnomessageStr = messageJson.toString();

								System.out.println("print" + jsnoaddressStr
										+ "-----" + jsnomessageStr);
								if (joaddress.containsKey("tolist")) {

									Object obj = joaddress.get("tolist");

									flag = StaticUtils.checkEmptyJSONArray(obj,
											joaddress, "tolist");
									if (!flag) {

										
										if (jomessage.containsKey("provider")) {
											Object objprov = jomessage
													.get("provider");
											boolean provflag = StaticUtils
													.checkEmptyJSONArray(
															objprov, jomessage,
															"provider");
											if (!provflag) {
												JSONObject jsnprovobj = (JSONObject) jomessage
														.get("provider");
												if (jsnprovobj
														.containsKey("code")) {
													providerCode = jsnprovobj
															.getString("code");
												}
											}
										}

										if (providerCode == null
												|| providerCode.isEmpty() == true
												|| providerCode.equals("[]")) {
											providerCode = prop
													.getProperty("defaultProv_provider");
										}

										boolean smscostexists = true;
										float smscostfloat = 0.02f;
										if (joreq.containsKey("smscost")) {

											smscost = joreq
													.getString("smscost");

											if (smscost.isEmpty() == false
													&& smscost.equals("[]") == false) {
												smscostfloat = Float
														.valueOf(smscost);
												
											}else{
												smscostexists = false;
											}

										}else{
											smscostexists = false;
										}

										DateFormat df = new SimpleDateFormat(
												"yyyy-MM-dd hh:mm:ss");

										Date currentDate = new Date();
										String currstr = df.format(currentDate);
										scheduleTime = df.parse(currstr);

										// Creating groupzMesgId
										gmsgid = GenerateSmsIds
												.generateGroupzMsgId();

										// Creating Resultant xml with status as
										// SMS is
										// in
										// Process
										String resultntString = GetGeneralStatusResultantString
												.getResultantStringforGeneralStatus(
														jsnoaddressStr,
														jsnomessageStr,
														smsprocess);

										System.out.println("resulatnt xml  : "
												+ resultntString);

										// Insert the sms data into trackstatus
										// table
										// for
										// immediate report.
										System.out.println(smscostfloat);
										Date currentdate = new Date();
										TrackStatus trst = new TrackStatus();
										trst.setaddressString(jsnoaddressStr);
										trst.setmessageString(jsnomessageStr);
										trst.setgroupzMsgId(gmsgid);
										trst.setprovider(providerCode);
										trst.setreceivedTimeStamp(currentdate);
										trst.setresultantString(resultntString);
										if(smscostexists){
											System.out.println("inside cost");
										trst.setsmscost(smscostfloat);
										}
										trst.setsegments(segments);
										trst.save();

										// Inserting data to
										// smsqueue(messagesintable)
										insObj.setProvider(providerCode);
										insObj.setAddress(jsnoaddressStr);
										insObj.setMessage(jsnomessageStr);
										insObj.setDate(scheduleTime);
										insObj.setgroupzMsgId(gmsgid);
										insObj.save();
										System.out
												.println("inserted sms data into messagesintable");

										statusMsg = "Sucessfully Created  the Transaction.";
										statuscode = stsucess;
									}
								}

							}
						}
					}
				}
			}

			if (flag) {
				statusMsg = "Please provide the required information for sending sms.";
				statuscode = stswrngdata;
			}

			logger.debug("placed Request for sending sms");

			JSONObject responseJson = new JSONObject();
			JSONObject finalReportJson = new JSONObject();
			JSONObject reportJson = new JSONObject();
			reportJson.put("grpzmsgid", gmsgid);
			reportJson.put("status", statusMsg);
			reportJson.put("statuscode", statuscode);
			finalReportJson.put("report", reportJson);
			responseJson.put("response", finalReportJson);
			xmlresponse = responseJson.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception occured in SendSms insert sms data.", e);

			resultstr = "Technical Error Occured.";

			statuscode = stserror;

			xmlresponse = CreateResponseString.Createrespobject(resultstr,
					statuscode);
			return xmlresponse;
		}

		return xmlresponse;
	}

	public static void main(String[] args) {

		// String xmlStr =
		// "<xml><request><datetime></datetime><address></address><message><shorttext>"
		// +
		// "Dear $ContactPersonPrefix $ContactPersonName, New Service Request for $Prefix $Name Created for $Email category Testing (Technician Assigned : Software Engineer). Please use $Email as your reference for future $Number correspondence.-payaswini</shorttext><sid>Groupz</sid><provider><code></code><username></username><password></password></provider></message></request></xml>";
		String xmlStr = "<xml><request><datetime></datetime><address><tolist><to><contactpersonname>gnbhat2</contactpersonname><name>payaswini2</name><email>payswini2@mail.com</email><number>%2B91.9986</number><prefix>Mrs</prefix><contactpersonprefix>Dr</contactpersonprefix></to><to></to></tolist></address><message><shorttext>Dear%20Parents,%20g%20Please%20send%20ifdoif%20udi%20pofidufPodar%20Jumbo%20Kids%20VB%20Layout</shorttext><sid></sid><provider><code></code><username></username><password></password></provider></message></request></xml>";

		InsertSmsData smsobj = new InsertSmsData();
		String resp = smsobj.InsertData(xmlStr);
		System.out.println(resp);

	}

}
