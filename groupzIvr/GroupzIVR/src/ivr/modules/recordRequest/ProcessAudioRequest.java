package ivr.modules.recordRequest;

import ivr.servlets.GetRecordedAudioServlet;
import ivr.tables.ContextMapping;
import ivr.tables.IvrGroupzBaseMapping;
import ivr.tables.IvrGroupzMapping;
import ivr.utils.GroupzInfoDetails;
import ivr.utils.GroupzMemberInfo;
import ivr.utils.StaticUtils;
import ivr.utils.recordUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.log4j.Logger;
import com.ozonetel.kookoo.Response;

public class ProcessAudioRequest {

	public static Logger logger = Logger.getLogger("recordLogger");
	static Properties prop = new Properties();

	static {
		try {

			System.setProperty("Hibernate-Url", ProcessRecordrequest.class
					.getResource("/Hibernate.cfg.xml").toString());
			InputStream in = ProcessRecordrequest.class
					.getResourceAsStream("/ivr.properties");
			prop.load(in);
		} catch (Exception e) {
			logger.info("Exception occured in load property file.", e);
			e.printStackTrace();

		}
	}

	public String processNewCall(String callerId, String ivrNumber,
			String callSessionId, String ipaddress) {

		Response kkResponse = new Response();
		String dedicatedUrl = null;
		String dedicatedText = null;
		String defaulturl = null;
		String defaultNotes = null;
		boolean multiLangFlag = false;
		int playspeed = 5;
		int timeout = 5000;
		String formattedNumber = null;
		String sucessFlagStr = "false";
		ContextMapping cm = new ContextMapping();
		String grpzdetailsxmlString = null;
		GroupzInfoDetails selectedGroupzInfo = null;
		List<GroupzInfoDetails> groupzList = null;
		try {

			IvrGroupzBaseMapping ivrsetting = IvrGroupzBaseMapping
					.getSingleivrnumberMap(ivrNumber);

			if (ivrsetting != null) {
				dedicatedText = ivrsetting.getmaintenanceNotes();
				dedicatedUrl = ivrsetting.getmaintenanceUrl();
				playspeed = ivrsetting.getplayspeed();
				timeout = ivrsetting.getsettimeout();
				multiLangFlag = ivrsetting.getmultiLanguageFlag();

			}

			if (GetRecordedAudioServlet.maintenanceFlag == true) {

				if (ivrsetting != null) {
					dedicatedText = ivrsetting.getmaintenanceNotes();
					dedicatedUrl = ivrsetting.getmaintenanceUrl();
				} else {
					logger.info("No record details for the callsession id , caller id,ivrnumber : "
							+ callSessionId
							+ "number : "
							+ callerId
							+ "IVRnumber :" + ivrNumber);

					String notes = prop.getProperty("ivrNorecordNotes");
					String url = prop.getProperty("ivrNorecordUrl");

					kkResponse = StaticUtils.sendNoDetailsNotes(notes, url);
					kkResponse.setSid(callSessionId);
					return kkResponse.getXML();
				}

				defaultNotes = prop.getProperty("ivrMaintenanceMsg");
				defaulturl = prop.getProperty("ivrMaintenanceUrl");

				kkResponse = StaticUtils.processUrlOrTextMessage(dedicatedUrl,
						dedicatedText, defaultNotes, defaulturl, playspeed,
						multiLangFlag, cm);

				kkResponse.setSid(callSessionId);
				kkResponse.addHangup();
				return kkResponse.getXML();

			}
			// Number validation for registry.
			formattedNumber = StaticUtils.validateMobile(callerId, ivrNumber);

			ArrayList<String> grpzDetails = recordUtils
					.getrecGrpzMobileValidationList(formattedNumber);

			sucessFlagStr = grpzDetails.get(0);

			if (sucessFlagStr.equals("true")) {

				grpzdetailsxmlString = grpzDetails.get(1);

				groupzList = StaticUtils
						.getGroupzInfoList(grpzdetailsxmlString);

				if (groupzList == null || groupzList.isEmpty() == true) {

					kkResponse = StaticUtils
							.sendNotRegGrupzResp(ivrsetting, cm);

				} else {
					List<IvrGroupzMapping> smList = (List<IvrGroupzMapping>) IvrGroupzMapping
							.getListivrSourceMap(ivrNumber);

					if (smList == null || smList.size() == 0) {

						logger.info("No record details for the callsession id , caller id,ivrnumber : "
								+ callSessionId
								+ "number : "
								+ callerId
								+ "IVRnumber :" + ivrNumber);

						String notes = prop.getProperty("ivrNorecordNotes");
						String url = prop.getProperty("ivrNorecordUrl");

						kkResponse = StaticUtils.sendNoDetailsNotes(notes, url);
						kkResponse.setSid(callSessionId);
						return kkResponse.getXML();
					}

					List<String> ivrgrpzcodeList = new ArrayList<String>();
					List<String> ivrgrpzcodeListtemp = new ArrayList<String>();
					Iterator<IvrGroupzMapping> iterator = smList.iterator();

					HashMap<String, String> groupzcodeNamMap = new HashMap<String, String>();
					IvrGroupzMapping sm = null;

					while (iterator.hasNext()) {
						sm = iterator.next();
						String grpzCode = sm.getGroupzCode();
						String grpzNameUrl = sm.getgroupzNameUrl();
						groupzcodeNamMap.put(grpzCode, grpzNameUrl);
						ivrgrpzcodeList.add(grpzCode);

					}

					ivrgrpzcodeListtemp.addAll(ivrgrpzcodeList);

					if (groupzList != null && groupzList.isEmpty() == false
							&& groupzList.size() > 0) {

						Iterator<GroupzInfoDetails> grpziter = groupzList
								.iterator();
						List<String> grpzcodeList = new ArrayList<String>();
						HashMap<String, String> groupzinfoMap = new HashMap<String, String>();
						while (grpziter.hasNext()) {
							selectedGroupzInfo = grpziter.next();
							String grpzCode = selectedGroupzInfo.getgrpzcode();
							String groupzId = selectedGroupzInfo.getgrpzid();
							groupzinfoMap.put(grpzCode, groupzId);
							grpzcodeList.add(grpzCode);

						}
						ivrgrpzcodeList.retainAll(grpzcodeList);

						if (ivrgrpzcodeList != null
								&& ivrgrpzcodeList.isEmpty() == false) {

							if (ivrgrpzcodeList.size() > 1) {

								cm.setIvrNumber(ivrNumber);
								cm.setipAddress(ipaddress);
								cm.setCallSessionId(callSessionId);
								cm.setCallerId(formattedNumber);
								cm.setLastupdatetime(new Date());
								cm.setmultiGrpzFlag(true);
								cm.setglobalFlag(false);

								cm.save();

								kkResponse = StaticUtils.createMultiGroupzData(
										ivrsetting, groupzcodeNamMap,
										ivrgrpzcodeList, groupzinfoMap,
										playspeed, timeout, cm);

							} else {

								String grpzcode = ivrgrpzcodeList.get(0);
								String groupzId = groupzinfoMap.get(grpzcode);

								List<GroupzMemberInfo> memberList = null;

								ArrayList<String> memberDetails = StaticUtils
										.getMemberMobileValidationList(
												formattedNumber, groupzId);

								String sucessFlagmemStr = memberDetails.get(0);

								if (sucessFlagmemStr.equals("true")) {

									String membXmlInfo = memberDetails.get(1);

									memberList = StaticUtils
											.getMemberInfoList(membXmlInfo);

								} else {
									logger.info(" error code in get memberList xml for mobile information from Rest API in record ivr : sessionID - "
											+ callSessionId
											+ "number : "
											+ formattedNumber);

									kkResponse = StaticUtils.senderrorResp(
											callSessionId, ivrNumber, cm);
									kkResponse.addHangup();

								}
								if (memberList != null && memberList.size() > 0) {

									int memblistSize = memberList.size();

									if (memblistSize > 1) {

										cm.setIvrNumber(ivrNumber);
										cm.setCallerId(formattedNumber);
										cm.setLastupdatetime(new Date());
										cm.setmultiGrpzFlag(false);
										cm.setipAddress(ipaddress);
										cm.setmultiMemberFlag(true);
										cm.setglobalFlag(true);
										cm.setgroupzId(groupzId);
										cm.setgroupzCode(grpzcode);
										cm.save();

										String displayMemberList = StaticUtils
												.createMemberlistString(
														callSessionId,
														memberList, cm,
														ivrNumber);

										cm.setmultimembWelcomeNotes(displayMemberList);
										cm.save();

										kkResponse = StaticUtils
												.processUrlOrTextMultiList(
														displayMemberList,
														playspeed, timeout);

									}

									else if (memblistSize == 1) {
										// creates new call response which shows
										// catagory selection list.

										int memberId = memberList.get(0)
												.getMemberId();

										cm.setIvrNumber(ivrNumber);
										cm.setCallerId(formattedNumber);
										cm.setLastupdatetime(new Date());
										cm.setmultiGrpzFlag(false);
										cm.setipAddress(ipaddress);
										cm.setgroupzCode(grpzcode);
										cm.setgroupzId(groupzId);
										cm.setmemberId(memberId);
										cm.setCallSessionId(callSessionId);
										cm.setmultiMemberFlag(false);
										cm.setglobalFlag(true);
										cm.save();

										kkResponse = recordUtils
												.getMessageSummary(ivrsetting,
														cm);

									}
								} else {

									logger.info("Member doesnt not exists - "
											+ formattedNumber + " sessionId : "
											+ callSessionId);

									kkResponse = StaticUtils.senderrorResp(
											callSessionId, ivrNumber, cm);

									kkResponse.setSid(callSessionId);
									return kkResponse.getXML();
								}

							}

						}

					} else {
						logger.info("The GroupzList is empty not registered to any groups.:"
								+ callSessionId);

						kkResponse = StaticUtils.sendNotRegGrupzResp(
								ivrsetting, cm);
						kkResponse.setSid(callSessionId);
						kkResponse.addHangup();
						return kkResponse.getXML();
					}

				}
			}

			else {
				logger.info("There is errorcode or problem while geting groupz list while checking for mobile"
						+ callSessionId
						+ "number : "
						+ callerId
						+ "IVRnumber :" + ivrNumber);

				kkResponse = StaticUtils.senderrorResp(callSessionId,
						ivrNumber, cm);

				kkResponse.addHangup();

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("technical error in record request call session ID , ivrnumber , callernumber:"
					+ callSessionId + "," + ivrNumber + "," + callerId);
			kkResponse = StaticUtils
					.senderrorResp(callSessionId, ivrNumber, cm);

			kkResponse.setSid(callSessionId);
			return kkResponse.getXML();
		}

		kkResponse.setSid(callSessionId);
		return kkResponse.getXML();

	}

	public String processContinuousCall(String callSessionId, String data) {

		Response kkResponse = new Response();
		int playspeed = 5;
		int timeout = 5000;
		String languageWelcomeUrl = null;
		IvrGroupzBaseMapping ivrnummap = null;
		int memberId = -1;
		String ivrNumber = null;
		boolean containsKey = false;
		String membXmlInfo = null;
		String sucessFlagStr = "false";
		ContextMapping co = null;
		String formattednumber = null;

		try {

			if (data != null && data.isEmpty() == false) {
				data = data.trim();
			}

			logger.info("The data received is " + data + "session id is : "
					+ callSessionId);
			boolean repeatFlag = false;

			if (StaticUtils.isEmptyOrNull(callSessionId) == true) {
				logger.info("The call session id is  empty :" + callSessionId);
				kkResponse = StaticUtils.senderrorResp(callSessionId,
						ivrNumber, co);

				kkResponse.setSid(callSessionId);
				return kkResponse.getXML();
			}

			co = ContextMapping.getSingleContext(callSessionId);

			if (co == null) {

				logger.info("The context does not exists in the context table");
				kkResponse = StaticUtils.senderrorResp(callSessionId,
						ivrNumber, co);

				kkResponse.setSid(callSessionId);
				return kkResponse.getXML();
			}

			String formattedCallerId = co.getCallerId();

			if (StaticUtils.isEmptyOrNull(formattedCallerId) == true) {

				logger.info("The formatted number is empty :"
						+ formattedCallerId);
				kkResponse = StaticUtils.senderrorResp(callSessionId,
						ivrNumber, co);

				kkResponse.setSid(callSessionId);
				return kkResponse.getXML();
			}

			boolean multigrpzFalg = co.getmultiGrpzFlag();
			boolean multimembFalg = co.getmultiMemberFlag();
			boolean multiLanguageFlag = co.getmultiLanguageFlag();
			boolean messgSummaryFlag = co.isMessgSummaryFlag();
			boolean messageTraverseFlag = co.isMessgTraverseFlag();

			formattednumber = co.getCallerId();

			ivrNumber = co.getIvrNumber();
			ivrnummap = IvrGroupzBaseMapping.getSingleivrnumberMap(ivrNumber);

			if (ivrnummap != null) {

				playspeed = ivrnummap.getplayspeed();
				timeout = ivrnummap.getsettimeout();
				languageWelcomeUrl = ivrnummap.getlanguageWelcomeURL();

			} else {

				String playspeedstr = prop.getProperty("playspeed");
				playspeed = Integer.parseInt(playspeedstr);

				String timestr = prop.getProperty("settimeout");
				timeout = Integer.parseInt(timestr);
			}

			String repeatCode = prop.getProperty("repeatCode");
			String hangupCode = prop.getProperty("hangupCode");
			String previousMenucode = prop.getProperty("previousMenucode");

			String selectionList = co.getcontextselectionList();

			String welcomeNote = co.getcontextdisplayList();

			if (data == null || data.isEmpty() == true) {

				repeatFlag = true;

			}

			if (data != null && data.trim().isEmpty() == false) {

				if (data.equals(hangupCode)) {

					String hangUpAdudio = null;
					String hangUpText = null;

					if (ivrnummap != null) {
						hangUpAdudio = ivrnummap.getAudiogeneralHangupUrl();
						hangUpText = ivrnummap.getgeneralHangupNotes();

					}

					kkResponse = StaticUtils.playUrlOrTextMessage(hangUpText,
							hangUpAdudio, playspeed, co, false);

					kkResponse.addHangup();
					kkResponse.setSid(callSessionId);
					return kkResponse.getXML();
				}

				else if (messgSummaryFlag) {

					String oldmsgSel = prop.getProperty("oldMsgSelct");
					String newmsgSel = prop.getProperty("neMsgSelect");
					memberId = co.getmemberId();

					String fromValue = prop.getProperty("fromval");
					String size = prop.getProperty("msgsSize");

					if (data.equals(oldmsgSel)) {
						containsKey = true;
						String type = "old";
						co.setMessgSummaryFlag(false);
						co.setMessgTraverseFlag(true);
						co.save();

						kkResponse = recordUtils.getRecordedAudioUrlList(
								ivrnummap, co, type, fromValue, size);

					} else if (data.equals(newmsgSel)) {
						containsKey = true;
						String type = "new";
						co.setMessgSummaryFlag(false);
						co.setMessgTraverseFlag(true);
						co.save();

						kkResponse = recordUtils.getRecordedAudioUrlList(
								ivrnummap, co, type, fromValue, size);
					} else {
						repeatFlag = true;
					}

				}

				else if (selectionList != null
						&& selectionList.isEmpty() == false) {

					JSONObject selectObj = (JSONObject) JSONSerializer
							.toJSON(selectionList);

					JSONObject selectionListObj = (JSONObject) selectObj
							.get("selectionList");

					if (selectionListObj.containsKey(data)) {

						containsKey = true;

						if (multigrpzFalg == true) {

							JSONObject grpzselectionListObj = selectionListObj
									.getJSONObject(data);

							String groupzcode = grpzselectionListObj
									.getString("grpzcode");
							String groupzId = grpzselectionListObj
									.getString("groupzID");

							co.setgroupzCode(groupzcode);
							co.setgroupzId(groupzId);
							co.save();

							IvrGroupzMapping sm = IvrGroupzMapping
									.getSingleivrSourceMapwithGroupzCode(
											ivrNumber, groupzcode);

							if (sm != null) {
								selectionList = sm.getselectionlist();
							} else {
								logger.info("the sourceMap is missing for the IVRNUMBER : "
										+ ivrNumber
										+ " and sessionId is :"
										+ callSessionId
										+ " and number is :"
										+ formattednumber);

								kkResponse = StaticUtils.senderrorResp(
										callSessionId, ivrNumber, co);

								kkResponse.setSid(callSessionId);
								return kkResponse.getXML();
							}

							List<GroupzMemberInfo> memberList = null;

							ArrayList<String> memberDetails = StaticUtils
									.getMemberMobileValidationList(
											formattednumber, groupzId);

							sucessFlagStr = memberDetails.get(0);

							if (sucessFlagStr.equals("true")) {

								membXmlInfo = memberDetails.get(1);

								memberList = StaticUtils
										.getMemberInfoList(membXmlInfo);

							} else {
								logger.info(" error code in get memberList xml for mobile information from Rest API : sessionID - "
										+ callSessionId
										+ "number : "
										+ formattednumber);

								kkResponse = StaticUtils.senderrorResp(
										callSessionId, ivrNumber, co);
								kkResponse.setSid(callSessionId);
								kkResponse.addHangup();
								return kkResponse.getXML();
							}

							if (memberList != null && memberList.size() > 0) {
								int memblistSize = memberList.size();

								if (memblistSize > 1) {

									String displayMemberList = StaticUtils
											.createMemberlistString(
													callSessionId, memberList,
													co, ivrNumber);

									co.setmultiGrpzFlag(false);
									co.setmultiMemberFlag(true);
									co.save();

									kkResponse = StaticUtils
											.processUrlOrTextMultiList(
													displayMemberList,
													playspeed, timeout);

								}

								else if (memblistSize == 1) {

									memberId = memberList.get(0).getMemberId();

									co.setmultiGrpzFlag(false);
									co.setmultiMemberFlag(false);
									co.setgroupzCode(groupzcode);
									co.setgroupzId(groupzId);
									co.setmemberId(memberId);
									co.save();

									kkResponse = recordUtils.getMessageSummary(
											ivrnummap, co);

									System.out.println("audio list"
											+ kkResponse);

									kkResponse
											.addPlayText("List URLS received");

								}
							} else {

								logger.info("Member doesnt not exists - "
										+ formattedCallerId + " sessionId : "
										+ callSessionId);

								kkResponse = StaticUtils.senderrorResp(
										callSessionId, ivrNumber, co);

								kkResponse.setSid(callSessionId);
								return kkResponse.getXML();

							}

						}

						else if (multimembFalg == true) {

							String selectdmemb = null;

							JSONObject memberdetails = selectionListObj
									.getJSONObject(data);

							selectdmemb = memberdetails.getString("memberid");

							memberId = Integer.parseInt(selectdmemb);

							co.setmemberId(memberId);
							co.setmultiGrpzFlag(false);
							co.setmultiMemberFlag(false);
							co.save();

							kkResponse = recordUtils.getMessageSummary(
									ivrnummap, co);

							System.out.println("audio list" + kkResponse);

						}

						else if (messageTraverseFlag) {
							memberId = co.getmemberId();

							String nextMsgVal = prop
									.getProperty("nextMessageset");

							JSONObject masgselectionListObj = selectionListObj
									.getJSONObject(data);

							if (data.equals(nextMsgVal)) {

								String nextfromVal = masgselectionListObj
										.getString("from");

								String size = masgselectionListObj
										.getString("size");

								String type = masgselectionListObj
										.getString("type");

								kkResponse = recordUtils
										.getRecordedAudioUrlList(

										ivrnummap, co, type, nextfromVal, size);

							} else {

								Iterator<?> urlkeys = masgselectionListObj
										.keys();
								while (urlkeys.hasNext()) {

									String keyname = (String) urlkeys.next();
									String value = masgselectionListObj
											.getString(keyname);

									String referenceidList = co
											.getrecordreferenceIdList();
									if (StaticUtils
											.isEmptyOrNull(referenceidList)) {
										co.setrecordreferenceIdList(keyname);
									} else {
										referenceidList = referenceidList + ","
												+ keyname;
										co.setrecordreferenceIdList(referenceidList);
									}
									co.save();

									ArrayList<String> dataArray = new ArrayList<String>();

									String displayList = co
											.getcontextdisplayList();

									dataArray = StaticUtils
											.createJSONdataArray(displayList);

									dataArray.add(0, value);

									String displayMenu = StaticUtils
											.createJSONString(dataArray);

									recordUtils.markAsRead(keyname, ivrnummap,
											co);

									kkResponse = StaticUtils
											.processUrlOrTextMultiList(
													displayMenu, playspeed,
													timeout);

								}
							}

						}

					}

				}
			}

			else if (data.trim().equalsIgnoreCase(previousMenucode)) {

				if (multiLanguageFlag) {
					repeatFlag = true;
				}

				if (multigrpzFalg == true) {
					repeatFlag = true;
				}

				if (messgSummaryFlag == true) {
					repeatFlag = true;
				}

				if (messageTraverseFlag == true) {
					repeatFlag = true;
				}

				else if (multimembFalg == true) {

					String multiGroupzWelNotes = co.getmultigrpzWelcomeNotes();

					if (multiGroupzWelNotes != null
							&& multiGroupzWelNotes.isEmpty() == false) {
						co.setmultiGrpzFlag(true);
						String displayList = co.getmultigrpzWelcomeNotes();
						String selectList = co.getmultigrpzselectlist();
						co.setcontextdisplayList(displayList);
						co.setcontextselectionList(selectList);
						co.setmultiMemberFlag(false);
						co.setmultimembWelcomeNotes(null);
						co.setmultiMembSelectlist(null);
						co.save();
						kkResponse = StaticUtils.processUrlOrTextMultiList(
								multiGroupzWelNotes, playspeed, timeout);

					} else {
						repeatFlag = true;
					}
				}

				else if (multigrpzFalg == false && multimembFalg == false
						&& multiLanguageFlag == false) {

					String multiMemberWelNotes = co.getmultimembWelcomeNotes();
					String multiGroupzWelNotes = co.getmultigrpzWelcomeNotes();

					if (multiMemberWelNotes != null
							&& multiMemberWelNotes.isEmpty() == false) {

						co.setmultiMemberFlag(true);

						String displayList = co.getmultimembWelcomeNotes();
						String selectList = co.getmultiMembSelectlist();
						co.setcontextdisplayList(displayList);
						co.setcontextselectionList(selectList);
						co.save();
						kkResponse = StaticUtils.processUrlOrTextMultiList(
								displayList, playspeed, timeout);

					}

					else if (multiGroupzWelNotes != null
							&& multiGroupzWelNotes.isEmpty() == false) {

						co.setmultiGrpzFlag(true);
						String displayList = co.getmultigrpzWelcomeNotes();
						String selectList = co.getmultigrpzselectlist();
						co.setcontextdisplayList(displayList);
						co.setcontextselectionList(selectList);
						co.save();

						kkResponse = StaticUtils.processUrlOrTextMultiList(
								displayList, playspeed, timeout);

					}

					else {
						repeatFlag = true;
					}

				}

			}

			if (repeatFlag
					|| data.trim().equals(repeatCode)
					|| (containsKey == false && !(data.trim().equals(
							previousMenucode) || data.trim().equals(hangupCode)))) {

				if (multiLanguageFlag) {
					kkResponse = StaticUtils.playURL(languageWelcomeUrl,
							timeout);
				} else {

					kkResponse = StaticUtils.processUrlOrTextMultiList(
							welcomeNote, playspeed, timeout);
				}
				kkResponse.setSid(callSessionId);
				return kkResponse.getXML();
			}

		} catch (Exception e) {

			e.printStackTrace();

			logger.info("Exception occured in process continous call in service request for number,IVRNUMBER : "
					+ formattednumber
					+ ivrNumber
					+ " and sessionId is :"
					+ callSessionId);

			kkResponse = StaticUtils
					.senderrorResp(callSessionId, ivrNumber, co);
			kkResponse.setSid(callSessionId);
			return kkResponse.getXML();
		}
		kkResponse.setSid(callSessionId);
		return kkResponse.getXML();
	}
}
