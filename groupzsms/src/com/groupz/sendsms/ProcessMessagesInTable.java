package com.groupz.sendsms;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.log4j.Logger;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.OptionalParameter.Tag;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;
import com.groupz.sendsms.tables.SmsDataTransaction;
import com.groupz.sendsms.tables.TrackStatus;

//Class which fetches records from table for sending sms through SMPP.

public class ProcessMessagesInTable implements Runnable {

	private TimeFormatter timeFormatter = new AbsoluteTimeFormatter();
	private static Logger logger = Logger.getLogger("smsLogger");
	static Properties prop = new Properties();
	private int id = 0;
	String servtype = null;
	String insNum = null;
	String transID = null;
	String statusMsg = null;

	static {

		try {
			System.setProperty("Hibernate-Url", GroupzSmsService.class
					.getResource("/Hibernate.cfg.xml").toString());

			Properties logProperties = null;
			logProperties = new Properties(System.getProperties());

			InputStream inlog = GroupzSmsService.class
					.getResourceAsStream("/log4j.properties");
			logProperties.load(inlog);

			logger.debug("Log and hibernate url initialized in GroupzSmsmService class ");

			System.out.println("entered config");
			InputStream in = ProcessMessagesInTable.class
					.getResourceAsStream("/smppConf.properties");
			prop.load(in);
			// prop.load(new FileInputStream("src/smppConf.properties"));

		} catch (Exception e) {
			logger.info("Exception occured in load property file.", e);
			e.printStackTrace();
		}
	}

	public ProcessMessagesInTable(int id) {
		this.id = id;

	}

	public void run() {
		while (true) {
			try {
				// Thread.sleep(5000);
				// settings for logging
				// logger.debug("Logging initialized in processmessagesintable class ");

				int counter = 0;
				int errorCount = 0;
				String username = null;
				String password = null;
				String sourceAddrss = null;
				String jobid = null;
				String errjobid = "ErrorID";

				String grpzmsgId = null;
				String toValue = null;

				SMPPSession session = new SMPPSession();
				InsertStatusIntoDeliveryStatus insdelv = new InsertStatusIntoDeliveryStatus();

				SMSErrorManager smsobj = new SMSErrorManager();

				String messageText = null;
				String destNumber = null;
				int iddel = 0;
				String finalnewaddress = null;
				int threadId = this.id;
				String provider = null;
				String smppHost = null;
				int smppport = 0;
				int tosize = 0;
				boolean defualtflag = false;

				String counterFlag = prop.getProperty("counter_flag");
				String modVal = prop.getProperty("thread_pool_size");
				String retryVal = prop.getProperty("retry_count");
				String sendfail = prop.getProperty("failedbindanderror");
				String threadSleepTime = prop.getProperty("threadsmsSleepTime");
				int sleepTime = Integer.parseInt(threadSleepTime);

				JSONObject addressObj = null;
				JSONObject messageObj = null;
				JSONArray jsntoarraytemp = new JSONArray();
				JSONObject jonnewtoValues = new JSONObject();
				JSONObject jonewtolist = new JSONObject();
				JSONObject jonewaddress = new JSONObject();
				HashMap<String, String> smsMap = new HashMap<String, String>();

				List<SmsDataTransaction> is = (List<SmsDataTransaction>) SmsDataTransaction
						.listAllSmsData(threadId, counterFlag, retryVal, modVal);

				if (is != null) {
					if (is.size() != 0) {

						for (SmsDataTransaction ts : is) {

							String addresString = ts.getAddress();
							String msgString = ts.getMessage();
							provider = ts.getProvider();
							grpzmsgId = ts.getgroupzMsgId();

							if (provider != null && provider.isEmpty() == false) {

								if (provider.equals("smscountry")
										|| provider.equals("valueleaf")) {

									if (provider.equals("smscountry")) {
										smppHost = prop
												.getProperty("smscountry_host");
										smppport = Integer
												.parseInt(prop
														.getProperty("smscountry_port"));

										sourceAddrss = prop
												.getProperty("smscountry_source");

										System.out
												.println("smscountry provider");

									}

									else if (provider.equals("valueleaf")) {
										smppHost = prop
												.getProperty("valueleaf_host");
										smppport = Integer.parseInt(prop
												.getProperty("valueleaf_port"));

										sourceAddrss = prop
												.getProperty("valueleaf_source");

										System.out
												.println("valueleaf provider");

									}
								} else {
									provider = prop
											.getProperty("defaultProv_provider");
									defualtflag = true;
								}

							}
							iddel = ts.getMsgId();
							counter = ts.getcounter();

							System.out.println("ThreadID :" + threadId
									+ " tableId : " + iddel);

							try {

								addressObj = (JSONObject) JSONSerializer
										.toJSON(addresString);

								messageObj = (JSONObject) JSONSerializer
										.toJSON(msgString);

								JSONObject messageData = (JSONObject) messageObj
										.get("message");

								

								if (messageData.containsKey("sid")) {

									sourceAddrss = messageData.getString("sid");
									if (sourceAddrss != null
											&& sourceAddrss.equals("[]")) {
										sourceAddrss = null;
									}
								}

								Object objprov = messageData.get("provider");

								if (objprov instanceof JSONArray) {
									JSONArray jsntoarray = messageData
											.getJSONArray("provider");
									if (jsntoarray.isEmpty()) {
										System.out.println("provider null values");
									}
								} else if (objprov instanceof JSONObject) {
									JSONObject jsnprov = (JSONObject) messageData
											.get("provider");

									if (jsnprov.containsKey("username")) {

										username = jsnprov
												.getString("username");
										if (username != null
												&& username.equals("[]")) {
											username = null;
										}
									}
									if (jsnprov.containsKey("password")) {

										password = jsnprov
												.getString("password");
										if (password != null
												&& password.equals("[]")) {
											password = null;
										}
									}
								}
								if (provider == null
										|| provider.isEmpty() == true
										|| username == null
										|| username.isEmpty() == true
										|| password == null
										|| password.isEmpty() == true
										|| sourceAddrss == null
										|| sourceAddrss.isEmpty() == true
										|| defualtflag == true) {

									System.out.println("test");

									username = prop
											.getProperty("defaultProv_username");
									password = prop
											.getProperty("defaultProv_password");

									sourceAddrss = prop
											.getProperty("defaultProv_source");

									provider = prop
											.getProperty("defaultProv_provider");

									smppHost = prop
											.getProperty("defaultProv_host");

									smppport = Integer.parseInt(prop
											.getProperty("defaultProv_port"));

									System.out.println(username + password
											+ sourceAddrss + provider);

								}
								System.out.println(smppHost + ":" + smppport
										+ ":" + username + ":" + password + ":"
										+ sourceAddrss);
								// Connect and Bind to SMPP
								session.connectAndBind(smppHost, smppport,
										new BindParameter(BindType.BIND_TX,
												username, password, "cp",
												TypeOfNumber.INTERNATIONAL,
												NumberingPlanIndicator.ISDN,
												null));

								JSONObject joaddress = (JSONObject) addressObj
										.get("address");

								Object obj = joaddress.get("tolist");
								String destinatnNum = "000";
								if (obj instanceof JSONArray) {

									JSONArray jsntolistarry = joaddress
											.getJSONArray("tolist");
									int j = 0;
									for (int i = 0; i < jsntolistarry.size(); i++) {
										Object objtemp = jsntolistarry.get(i);

										if (objtemp instanceof JSONArray) {
											JSONArray jsntoarray = jsntolistarry
													.getJSONArray(i);
											if (jsntoarray.isEmpty()) {
												System.out.println(" 'to' null values");
											}
										} else if (objtemp instanceof JSONObject) {

											JSONObject jotemp = jsntolistarry
													.getJSONObject(i);

											if (jotemp.containsKey("number")) {
												String check = jotemp
														.getString("number");
												if (check.isEmpty() == false
														&& check.equals("[]") == false) {
													destinatnNum = jotemp
															.getString("number");
													smsMap.put(destinatnNum,
															jotemp.toString());
												}
											}

											j++;
										}
									}
									tosize = j;
								}

								else {
									JSONObject jsntolisObj = (JSONObject) joaddress
											.get("tolist");

									Object objtemp = jsntolisObj.get("to");

									if (objtemp instanceof JSONArray) {
										if (((JSONArray) objtemp).isEmpty()) {

											System.out.println(" 'to' empty");

										}
									} else if (objtemp instanceof JSONObject) {
										JSONObject jotemp = (JSONObject) jsntolisObj
												.get("to");
										if (jotemp.containsKey("number")) {
											String check = jotemp
													.getString("number");
											if (check.isEmpty() == false
													&& check.equals("[]") == false) {
												destinatnNum = jotemp
														.getString("number");

											}
										}

										tosize = 1;

										smsMap.put(destinatnNum,
												jotemp.toString());
									}
								}

								// Thread.sleep(15000);
								for (String key : smsMap.keySet()) {
									// creating to adddress xml
									destNumber = key;
									toValue = smsMap.get(key);

									messageText = messageData
											.getString("shorttext");
									
									messageText = ReplaceMessageTags
											.changeMessageText(messageText,
													toValue);
									
									OptionalParameter messagePayloadParameter = new OptionalParameter.OctetString(Tag.MESSAGE_PAYLOAD, messageText);

									jobid = "";
									errorCount = 0;

									System.out
											.println("destino  " + destNumber);

									int indx = destNumber.indexOf(".");
									if (destNumber != null
											&& indx != -1
											&& destNumber.equals("000") == false) {

										String sudbst1 = destNumber.substring(
												0, indx);
										String sudstr2 = destNumber.substring(
												indx + 1, destNumber.length());

										String sendNum = sudbst1 + sudstr2;

										try {

											String countryCode = prop
													.getProperty("countrycode");

											if (sudbst1.equals(countryCode)) {
									
													jobid = session
															.submitShortMessage(
																	null,
																	TypeOfNumber.INTERNATIONAL,
																	NumberingPlanIndicator.ISDN,
																	sourceAddrss,
																	TypeOfNumber.INTERNATIONAL,
																	NumberingPlanIndicator.ISDN,
																	sendNum,
																	new ESMClass(),
																	(byte) 0,
																	(byte) 1,
																	null,
																	null,
																	new RegisteredDelivery(
																			SMSCDeliveryReceipt.DEFAULT),
																	(byte) 0,
																	new GeneralDataCoding(
																			0),
																	(byte) 0,
																	new byte[0],messagePayloadParameter);

													System.out
															.println("Sms Message_id is "
																	+ jobid);
												
												statusMsg = "Message Sent";

												logger.info("Sms sent sucessfully. ");

												insdelv.InsertintoDeliveryStatus(
														jobid, grpzmsgId,
														toValue, messageText,
														destNumber, provider,
														statusMsg);
											} else {
												statusMsg = smsobj
														.ManageError("errorCode");
												insdelv.InsertErrorDeliveryStatus(
														errjobid, grpzmsgId,
														toValue, messageText,
														destNumber, provider,
														statusMsg, statusMsg);

												System.err
														.println("Invalid country code");

												logger.info("Invalid country code. The country code should be +91 - "
														+ destNumber);
											}
										} catch (PDUException e) { // Invalid
																	// PDU
																	// parameter
											Writer writer = new StringWriter();
											PrintWriter printWriter = new PrintWriter(
													writer);
											e.printStackTrace(printWriter);
											String s = writer.toString();

											statusMsg = smsobj.ManageError(s);

											if (!statusMsg
													.equals("Invalid Number")) {

												errorCount = 1;
											}

											insdelv.InsertErrorDeliveryStatus(
													errjobid, grpzmsgId,
													toValue, messageText,
													destNumber, provider,
													statusMsg, s);
											System.err
													.println("Invalid PDU parameter");

											logger.info(
													"Exception occured in SendSms submitShortMessage."
															+ destNumber, e);
											e.printStackTrace();
										} catch (ResponseTimeoutException e) { // Response
																				// timeout
											Writer writer = new StringWriter();
											PrintWriter printWriter = new PrintWriter(
													writer);
											e.printStackTrace(printWriter);
											String s = writer.toString();

											statusMsg = smsobj.ManageError(s);

											if (!statusMsg
													.equals("Invalid Number")) {

												errorCount = 1;
											}

											insdelv.InsertErrorDeliveryStatus(
													errjobid, grpzmsgId,
													toValue, messageText,
													destNumber, provider,
													statusMsg, s);

											System.err
													.println("Response timeout");
											logger.info(
													"Exception occured in SendSms submitShortMessage."
															+ destNumber, e);
											e.printStackTrace();
										} catch (InvalidResponseException e) {

											Writer writer = new StringWriter();
											PrintWriter printWriter = new PrintWriter(
													writer);
											e.printStackTrace(printWriter);
											String s = writer.toString();
											statusMsg = smsobj.ManageError(s);

											if (!statusMsg
													.equals("Invalid Number")) {

												errorCount = 1;
											}

											insdelv.InsertErrorDeliveryStatus(
													errjobid, grpzmsgId,
													toValue, messageText,
													destNumber, provider,
													statusMsg, s);

											// Invalid
											// response
											System.err
													.println("Receive invalid respose");
											logger.info(
													"Exception occured in SendSms submitShortMessage."
															+ destNumber, e);
											e.printStackTrace();
										} catch (NegativeResponseException e) {
											Writer writer = new StringWriter();
											PrintWriter printWriter = new PrintWriter(
													writer);
											e.printStackTrace(printWriter);
											String s = writer.toString();
											statusMsg = smsobj.ManageError(s);

											if (!statusMsg
													.equals("Invalid Number")) {

												errorCount = 1;
											}

											insdelv.InsertErrorDeliveryStatus(
													errjobid, grpzmsgId,
													toValue, messageText,
													destNumber, provider,
													statusMsg, s);

											System.err
													.println("Receive negative response");
											logger.info(
													"Exception occured in SendSms submitShortMessage."
															+ destNumber, e);
											e.printStackTrace();
										} catch (IOException e) {
											Writer writer = new StringWriter();
											PrintWriter printWriter = new PrintWriter(
													writer);
											e.printStackTrace(printWriter);
											String s = writer.toString();
											statusMsg = smsobj.ManageError(s);

											if (!statusMsg
													.equals("Invalid Number")) {

												errorCount = 1;
											}

											insdelv.InsertErrorDeliveryStatus(
													errjobid, grpzmsgId,
													toValue, messageText,
													destNumber, provider,
													statusMsg, s);

											System.err
													.println("IO error occur");
											logger.info(
													"Exception occured in SendSms submitShortMessage."
															+ destNumber, e);
											e.printStackTrace();
										}

									} else {
										statusMsg = smsobj
												.ManageError("0000000b");
										insdelv.InsertErrorDeliveryStatus(
												errjobid, grpzmsgId, toValue,
												messageText, destNumber,
												provider, statusMsg, statusMsg);

										System.err
												.println("Invalid country code");

										logger.info("Invalid country code. The country code should be +91. - "
												+ destNumber);
									}

									if (errorCount == 1) {

										JSONObject tonewVl = (JSONObject) JSONSerializer
												.toJSON(toValue);

										jsntoarraytemp.add(tonewVl);

									}
								}

								TrackStatus setsize = new TrackStatus();
								setsize = TrackStatus
										.getSingleTrackStatus(grpzmsgId);

								setsize.setsmscount(tosize);
								setsize.save();

								if (errorCount == 1) {
									jonnewtoValues.put("to", jsntoarraytemp);
									jonewtolist.put("tolist", jonnewtoValues);
									jonewaddress.put("address", jonewtolist);
								}

								if (errorCount == 0) {

									SmsDataTransaction.deleteSmsData(iddel);

								} else if (errorCount == 1
										&& counterFlag.equals("0")) {
									SmsDataTransaction.deleteSmsData(iddel);
								} else if (errorCount == 1
										&& counterFlag.equals("1")) {
									counter++;

									Date schedDate = ts.getDate();
									Calendar cal = Calendar.getInstance();
									cal.setTime(schedDate);

									cal.add(Calendar.HOUR_OF_DAY, 1);
									Date newSchdate = cal.getTime();
									ts.setDate(newSchdate);
									ts.setcounter(counter);
									ts.save();

									finalnewaddress = jonewaddress.toString();

									ts.setAddress(finalnewaddress);
									ts.save();
								}
							} catch (IOException e) {

								errorCount = 1;
								if (counterFlag.equals("1")) {
									counter++;
									Date schedDate = ts.getDate();
									Calendar cal = Calendar.getInstance();
									cal.setTime(schedDate);

									cal.add(Calendar.HOUR_OF_DAY, 1);
									Date newSchdate = cal.getTime();
									ts.setDate(newSchdate);
									ts.setcounter(counter);
									ts.save();
								}

								else if (errorCount == 1
										&& counterFlag.equals("0")) {
									SmsDataTransaction.deleteSmsData(iddel);
								}

								System.err
										.println("Failed connect and bind to host");
								logger.info(
										"Exception occured in SendSms connect and bind method."
												+ grpzmsgId, e);
								e.printStackTrace();

								// Insert into trackstatus table and also merge
								// the status.

								smsobj.UpdateTrackStatusforErrorStatus(
										addresString, msgString, grpzmsgId,
										provider, tosize, sendfail);

							}

							catch (Exception e) {
								errorCount = 1;
								if (counterFlag.equals("1")) {

									counter++;
									Date schedDate = ts.getDate();
									Calendar cal = Calendar.getInstance();
									cal.setTime(schedDate);

									cal.add(Calendar.HOUR_OF_DAY, 1);
									Date newSchdate = cal.getTime();
									ts.setDate(newSchdate);
									ts.setcounter(counter);
									ts.save();
								}

								else if (errorCount == 1
										&& counterFlag.equals("0")) {
									SmsDataTransaction.deleteSmsData(iddel);
								}
								e.printStackTrace();
								System.err
										.println("Error occured while sending sms");
								logger.info("Exception occured in SendSMS."
										+ grpzmsgId, e);

								// Insert into trackstatus table and also merge
								// the status.

								smsobj.UpdateTrackStatusforErrorStatus(
										addresString, msgString, grpzmsgId,
										provider, tosize, sendfail);

							}

							finally {

								session.unbindAndClose();
							}

						}
					}
				}

				else {

					Thread.sleep(sleepTime);
					Thread.yield();
				}

			} catch (Exception e) {
				logger.info("Exception occured in ProcessMessagesInTable.", e);
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		ProcessMessagesInTable smsobj = new ProcessMessagesInTable(9);
		Thread newthrd = new Thread(smsobj);
		newthrd.run();
	}

}
