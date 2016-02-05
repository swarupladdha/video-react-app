package ivr.modules.recordRequest;

import ivr.servlets.RecordCallServlet;
import ivr.tables.ContextMapping;
import ivr.tables.IvrGroupzMapping;
import ivr.tables.IvrGroupzBaseMapping;
import ivr.utils.GroupzInfoDetails;
import ivr.utils.GroupzMemberInfo;
import ivr.utils.recordUtils;
import ivr.utils.StaticUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.ozonetel.kookoo.Response;

public class ProcessRecordrequest {
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
			String callSessionId) {

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

			if (RecordCallServlet.maintenanceFlag == true) {

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
								IvrGroupzMapping smap = IvrGroupzMapping
										.getSingleivrSourceMapwithGroupzCode(
												ivrNumber, grpzcode);

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

								int memberId = memberList.get(0).getMemberId();
								String memberCode = memberList.get(0)
										.getMemberCode();

								cm.setCallerId(formattedNumber);
								cm.setgroupzCode(grpzcode);
								cm.setgroupzId(groupzId);
								cm.setCallSessionId(callSessionId);
								cm.setIvrNumber(ivrNumber);
								cm.setLastupdatetime(new Date());
								cm.setmainMenuFlag(true);
								cm.setmemberId(memberId);
								cm.setmemberCode(memberCode);
								cm.save();

								// Main Menu Selection

								kkResponse = recordUtils
										.generateMainMenuRequest(smap, cm,
												ivrsetting);

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

	public String processContinuesRecordCall(String callSessionId, String data) {
		ContextMapping contxt = new ContextMapping();
		Response kkResponse = new Response();
		String ivrNumber = null;
		String callerId = null;
		try {

			String repeatCode = prop.getProperty("repeatCode");
			String hangupCode = prop.getProperty("hangupCode");
			String previousMenucode = prop.getProperty("previousMenucode");

			boolean repeatFlag = false;
			boolean containsKey = false;

			contxt = ContextMapping.getSingleContext(callSessionId);

			String alloptionValue = prop.getProperty("alloptionValue");
			String reRecordoption = prop.getProperty("rerecordcordoption");
			//String recordConfirmation = prop.getProperty("recordConfirmation");
			String recordRepeat = prop.getProperty("repeatRecOptn");

			if (contxt == null) {

				logger.info("The context does not exists in the context table in continuous call callsessionId , ivrnumber,caller ID :"
						+ callSessionId + "," + ivrNumber + "," + callerId);
				kkResponse = StaticUtils.senderrorResp(callSessionId,
						ivrNumber, contxt);

				kkResponse.setSid(callSessionId);
				return kkResponse.getXML();
			}

			String selectionlist = contxt.getcontextselectionList();
			String dispList = contxt.getcontextdisplayList();
			ivrNumber = contxt.getIvrNumber();
			callerId = contxt.getCallerId();
			boolean grpzSelectFalg = contxt.getmultiGrpzFlag();
			boolean mainMenuFlag = contxt.getmainMenuFlag();
			boolean subMenuFlag = contxt.getsubMenuFlag();
			boolean recordFlag = contxt.getrecordFlag();

			boolean multilangFlag = contxt.getmultiLanguageFlag();

			boolean moreSubMenuaddedFlag = contxt.getmoreSubMenuFlag();

			if (StaticUtils.isEmptyOrNull(callSessionId) == true) {
				logger.info("The call session id is  empty callsessionId , ivrnumber,caller ID :"
						+ callSessionId + "," + ivrNumber + "," + callerId);

				kkResponse = StaticUtils.senderrorResp(callSessionId,
						ivrNumber, contxt);

				kkResponse.setSid(callSessionId);
				return kkResponse.getXML();
			}

			IvrGroupzBaseMapping settingObj = IvrGroupzBaseMapping
					.getSingleivrnumberMap(ivrNumber);

			int playspeed = settingObj.getplayspeed();
			int settimeout = settingObj.getsettimeout();

			if (data == null || data.isEmpty() == true) {

				repeatFlag = true;
			}

			if (data != null && data.trim().isEmpty() == false) {

				if (data.equals(hangupCode)) {

					String hangUpAdudio = null;
					String hangUpText = null;

					if (settingObj != null) {
						hangUpAdudio = settingObj.getAudiogeneralHangupUrl();
						hangUpText = settingObj.getgeneralHangupNotes();

					}

					kkResponse = StaticUtils.playUrlOrTextMessage(hangUpText,
							hangUpAdudio, playspeed, contxt, multilangFlag);

					kkResponse.addHangup();
					kkResponse.setSid(callSessionId);
					return kkResponse.getXML();
				}

				if (recordFlag) {

					String groupzCode = contxt.getgroupzCode();

					IvrGroupzMapping smap = IvrGroupzMapping
							.getSingleivrSourceMapwithGroupzCode(ivrNumber,
									groupzCode);

					String finallangsubmit = prop
							.getProperty("finalreclangSubmit");
					
					String language = prop
							.getProperty("defualtLanguage");

					if (data.equals(finallangsubmit)) {

						containsKey = true;
						String newUrl = contxt.geturl();
						StaticUtils.createUrlListJSONString(newUrl, language,
								null, contxt);
						
						recordUtils.InsertDataToPublishXML(contxt);

						String recordhangUpAdudio = null;
						String recordhangUpText = null;

						if (settingObj != null) {
							recordhangUpAdudio = settingObj
									.getaudioSelectionHangupUrl();
							recordhangUpText = settingObj
									.getselectionHangupNotes();

						}

						kkResponse = StaticUtils.playUrlOrTextMessage(
								recordhangUpText, recordhangUpAdudio,
								playspeed, contxt, multilangFlag);
						kkResponse.addHangup();

					}

					else {

						if (data.equals(reRecordoption)) {
							containsKey = true;
							kkResponse = recordUtils.startRecord(callSessionId,
									contxt, smap);

						}



						if (data.equals(recordRepeat)) {
							containsKey = true;
							String disptext = contxt.getcontextdisplayList();
							kkResponse = StaticUtils.processUrlOrTextMultiList(
									disptext, playspeed, settimeout);
						}
					}

					if (containsKey == false) {

						repeatFlag = true;

					}
				}

				else if (moreSubMenuaddedFlag) {

					String moresubMenuCode = prop
							.getProperty("moresubMenuCode");
					String continueRecord = prop.getProperty("continueRecord");

					if (data.equals(moresubMenuCode)) {

						containsKey = true;
						contxt.setmultisubMenulevelFlag(false);
						contxt.setmoreSubMenuFlag(false);
						contxt.save();
						kkResponse = recordUtils.createSubMenu(ivrNumber,
								contxt, playspeed, settimeout, settingObj);

					} else if (data.equals(continueRecord)) {
						containsKey = true;

						contxt.setsubMenuFlag(false);
						contxt.setmoreSubMenuFlag(false);
						contxt.setisRecMultiLangSelectionFlag(true);
						contxt.save();
						String groupzCode = contxt.getgroupzCode();

						IvrGroupzMapping smap = IvrGroupzMapping
								.getSingleivrSourceMapwithGroupzCode(ivrNumber,
										groupzCode);

						kkResponse = recordUtils.startRecord(callSessionId,
								contxt, smap);

					}

					if (containsKey == false) {

						repeatFlag = true;

					}

				}

				else {

					String selection = null;

					if (selectionlist != null
							&& selectionlist.isEmpty() == false) {

						JSONObject selectObj = (JSONObject) JSONSerializer
								.toJSON(selectionlist);

						JSONObject selectionListJSNObj = (JSONObject) selectObj
								.get("selectionList");

						if (selectionListJSNObj.containsKey(data)) {

							containsKey = true;

							selection = selectionListJSNObj.getString(data);
							
							if(StaticUtils.isEmptyOrNull(selection)){
								repeatFlag = true;
							}

							if (grpzSelectFalg) {

								JSONObject grpzJSNOBJ = selectionListJSNObj
										.getJSONObject(data);

								String grpzCode = grpzJSNOBJ
										.getString("grpzcode");

								String groupzId = grpzJSNOBJ
										.getString("groupzID");

								String mobileNumber = contxt.getCallerId();

								List<GroupzMemberInfo> memberList = null;

								ArrayList<String> memberDetails = StaticUtils
										.getMemberMobileValidationList(
												mobileNumber, groupzId);

								String sucessFlagmemStr = memberDetails.get(0);

								if (sucessFlagmemStr.equals("true")) {

									String membXmlInfo = memberDetails.get(1);

									memberList = StaticUtils
											.getMemberInfoList(membXmlInfo);

								} else {
									logger.info(" error code in get memberList xml for mobile information from Rest API in record ivr : sessionID - "
											+ callSessionId
											+ "number : "
											+ mobileNumber);

									kkResponse = StaticUtils.senderrorResp(
											callSessionId, ivrNumber, contxt);
									kkResponse.addHangup();

								}

								int memberId = memberList.get(0).getMemberId();
								String memberCode = memberList.get(0)
										.getMemberCode();

								contxt.setgroupzCode(grpzCode);
								contxt.setgroupzId(groupzId);
								contxt.setmemberId(memberId);
								contxt.setmemberCode(memberCode);
								contxt.setmultiGrpzFlag(false);
								contxt.setmainMenuFlag(true);
								contxt.save();
								String groupzCode = contxt.getgroupzCode();

								IvrGroupzMapping smap = IvrGroupzMapping
										.getSingleivrSourceMapwithGroupzCode(
												ivrNumber, groupzCode);

								kkResponse = recordUtils
										.generateMainMenuRequest(smap, contxt,
												settingObj);

							}

							if (mainMenuFlag) {

								if (selection.equals(alloptionValue)) {

									contxt.setmainMenuSelection(selection);
									contxt.setmainMenuFlag(false);
									//contxt.setisRecMultiLangSelectionFlag(true);
									contxt.save();
									String groupzCode = contxt.getgroupzCode();

									IvrGroupzMapping smap = IvrGroupzMapping
											.getSingleivrSourceMapwithGroupzCode(
													ivrNumber, groupzCode);
									
									kkResponse = recordUtils.startRecord(
											callSessionId, contxt, smap);
								}

								else {

									contxt.setmainMenuSelection(selection);
									contxt.setmainMenuFlag(false);
									contxt.save();

									kkResponse = recordUtils.createSubMenu(
											ivrNumber, contxt, playspeed,
											settimeout, settingObj);
								}

							}

							if (subMenuFlag) {

								ArrayList<String> dataArraywelcomedisplay = new ArrayList<String>();
								String groupzCode = contxt.getgroupzCode();

								IvrGroupzMapping smap = IvrGroupzMapping
										.getSingleivrSourceMapwithGroupzCode(
												ivrNumber, groupzCode);
								String selectionListStr = smap
										.getrecordSubMenuSelectionList();

								String subMenuDispString = smap
										.getrecordSubMenuDisplayList();

								JSONObject subdataObj = (JSONObject) JSONSerializer
										.toJSON(subMenuDispString);

								JSONObject jsnsubdatalistObj = subdataObj
										.getJSONObject("subMenuDisplayList");
								
								JSONArray jsndataendlistObj = new JSONArray();
								
								jsndataendlistObj = jsnsubdatalistObj.getJSONArray("endnotesList");
								
								String mainMenuSelected = contxt
										.getmainMenuSelection();

								JSONObject sublistObj = jsnsubdatalistObj
										.getJSONObject(mainMenuSelected);

								if (sublistObj.containsKey(selection)) { // multilevel
																			// submenu

									String starUrl = null;
									String starNotes = null;

									if (settingObj != null) {
										starUrl = settingObj
												.getpreviousMenuSelectUrl();
										starNotes = settingObj
												.getpreviousMenuSelectNotes();

									}

									contxt.setmultisubMenulevelFlag(true);
									contxt.save();

									JSONObject jsnmenulevelObj = sublistObj
											.getJSONObject(selection);

									JSONObject audiotObj = jsnmenulevelObj
											.getJSONObject("audio");

									String audioUrl = null;
									if (audiotObj != null) {
										audioUrl = audiotObj.getString("url");
									}

									if (audioUrl != null
											&& audioUrl.trim().isEmpty() == false) {
										dataArraywelcomedisplay.add(audioUrl);

									} else {

										JSONObject datalistObj = jsnmenulevelObj
												.getJSONObject("text");

										dataArraywelcomedisplay = recordUtils
												.createivrRecordDisplayMenuList(datalistObj,jsndataendlistObj
														);
									}

									if (starUrl != null
											&& starUrl.isEmpty() == false) {
										dataArraywelcomedisplay.add(starUrl);

									} else {

										JSONObject dataObj = (JSONObject) JSONSerializer
												.toJSON(starNotes);

										JSONArray jsndatalistObj = dataObj
												.getJSONArray("dataList");

										int datasize = jsndatalistObj.size();

										for (int i = 0; i < datasize; i++) {

											String playStr = jsndatalistObj
													.getString(i);

											dataArraywelcomedisplay.add(playStr
													.trim());

										}

									}

									JSONObject selectiondataObj = (JSONObject) JSONSerializer
											.toJSON(selectionListStr);
									JSONObject jsndatalistObj = selectiondataObj
											.getJSONObject("subMenuSelectionList");

									JSONObject subselectionlistObj = jsndatalistObj
											.getJSONObject(mainMenuSelected);

									JSONObject seletedsubkeyObj = subselectionlistObj
											.getJSONObject(selection);

									String displayList = StaticUtils
											.createJSONString(dataArraywelcomedisplay);

									contxt.setmainMenuFlag(false);
									contxt.setsubMenuFlag(true);
									contxt.setcontextdisplayList(displayList);
									contxt.setcontextselectionList(seletedsubkeyObj
											.toString());
									contxt.save();

									kkResponse = StaticUtils
											.processUrlOrTextMultiList(
													displayList, playspeed,
													settimeout);

								} else {

									String newList = "";
									boolean multilevelsubMenuFlag = contxt
											.getmultisubMenulevelFlag();

									String subMenuSlectedlist = contxt
											.getrecordselectedList();

									if (selection.equals(alloptionValue) // //
																			// If
																			// user
																			// selects
																			// ALL
																			// option
																			// from
																			// submenu
																			// .
											&& multilevelsubMenuFlag == false) {
										newList = selection + ",";

										containsKey = true;

										contxt.setsubMenuFlag(false);
										contxt.setmoreSubMenuFlag(false);
										contxt.setisRecMultiLangSelectionFlag(true);
										contxt.setrecordselectedList(newList);
										contxt.save();
										kkResponse = recordUtils.startRecord(
												callSessionId, contxt, smap);

									}

									else {// If user selects ALL option from the
											// sub-level(group) of submenu
											// selection.

										if (selection.equals(alloptionValue)
												&& multilevelsubMenuFlag) {

											if (StaticUtils
													.isEmptyOrNull(subMenuSlectedlist) == false) {

												newList = subMenuSlectedlist
														+ ",";

											}

											Collection<?> jsnValues = selectionListJSNObj
													.values();

											Iterator<?> itr = jsnValues
													.iterator();

											while (itr.hasNext()) {

												String datacatgryStr = (String) itr
														.next();

												if (datacatgryStr
														.equals(alloptionValue) == false) {

													newList = newList
															+ datacatgryStr
															+ ",";

												}

											}

										} else { // If it is a normal selection
													// from sub menu.

											if (StaticUtils
													.isEmptyOrNull(subMenuSlectedlist)) {

												newList = selection + ",";

											} else {
												newList = subMenuSlectedlist
														+ selection + ",";
											}

										}

										contxt.setmainMenuFlag(false);
										contxt.setsubMenuFlag(false);
										contxt.setmoreSubMenuFlag(true);
										contxt.setcontextselectionList(null);
										contxt.setrecordselectedList(newList);
										contxt.save();

										String mainMenuselection = contxt
												.getmainMenuSelection();

										ArrayList<String> dataList = new ArrayList<String>();

										String moreSubmenyUrl = smap
												.getmoreRecordSubmenuOptionsUrl();

										if (StaticUtils
												.isEmptyOrNull(moreSubmenyUrl) == false) {

											dataList.add(moreSubmenyUrl);

										} else {

											String moreSubmenuNotes = smap
													.getmoreRecordSubmenuOptionsNotes();

											dataList = StaticUtils
													.createJSONdataArray(moreSubmenuNotes);

											String tempStr = dataList.get(0);
											tempStr = tempStr + " "
													+ mainMenuselection;
											dataList.set(0, tempStr);
										}
										System.out.println(dataList);

										String displayList = StaticUtils
												.createJSONString(dataList);

										contxt.setmoreSubMenuFlag(true);
										contxt.setcontextdisplayList(displayList);
										contxt.save();

										kkResponse = StaticUtils
												.processUrlOrTextMultiList(
														displayList, playspeed,
														settimeout);

									}
								}
							}

						}

						if (data.trim().equalsIgnoreCase(previousMenucode)) {

							if (recordFlag) {
								repeatFlag = true;
							}

							if (grpzSelectFalg) {
								repeatFlag = true;
							}

							if (mainMenuFlag) {
								String multiGroupzWelNotes = contxt
										.getmultigrpzWelcomeNotes();

								if (multiGroupzWelNotes != null
										&& multiGroupzWelNotes.isEmpty() == false) {
									contxt.setmultiGrpzFlag(true);
									String displayList = contxt
											.getmultigrpzWelcomeNotes();
									String selectList = contxt
											.getmultigrpzselectlist();
									contxt.setcontextdisplayList(displayList);
									contxt.setcontextselectionList(selectList);
									contxt.setmainMenuFlag(false);
									contxt.save();
									kkResponse = StaticUtils
											.processUrlOrTextMultiList(
													multiGroupzWelNotes,
													playspeed, settimeout);

								} else {
									repeatFlag = true;
								}
							}

							else if (subMenuFlag) {

								boolean multiSubMenuflag = contxt
										.getmultisubMenulevelFlag();
								if (multiSubMenuflag) {
									contxt.setmultisubMenulevelFlag(false);
									contxt.save();
									kkResponse = recordUtils.createSubMenu(
											ivrNumber, contxt, playspeed,
											settimeout, settingObj);
								} else {
									contxt.setsubMenuFlag(false);
									contxt.setmainMenuFlag(true);
									contxt.setrecordselectedList(null);
									contxt.save();
									String groupzCode = contxt.getgroupzCode();

									IvrGroupzMapping smap = IvrGroupzMapping
											.getSingleivrSourceMapwithGroupzCode(
													ivrNumber, groupzCode);

									kkResponse = recordUtils
											.generateMainMenuRequest(smap,
													contxt, settingObj);

								}
							}

							else if (moreSubMenuaddedFlag) {
								repeatFlag = true;
							}
						}

					} else {
						logger.info("The selection list is null");
						kkResponse = StaticUtils.senderrorResp(callSessionId,
								ivrNumber, contxt);

						kkResponse.setSid(callSessionId);
						return kkResponse.getXML();
					}
				}

			}

			if (repeatFlag
					|| data.trim().equals(repeatCode)
					|| (containsKey == false && !(data.trim().equals(
							previousMenucode) || data.trim().equals(hangupCode)))) {

				kkResponse = StaticUtils.processUrlOrTextMultiList(dispList,
						playspeed, settimeout);

				kkResponse.setSid(callSessionId);
				return kkResponse.getXML();
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("technical error in record request continuous call  session ID , ivrnumber , callernumber:"
					+ callSessionId
					+ ","
					+ ivrNumber
					+ ","
					+ callerId
					+ "error message" + e);
			kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber,
					contxt);
			kkResponse.setSid(callSessionId);
			return kkResponse.getXML();
		}

		kkResponse.setSid(callSessionId);
		return kkResponse.getXML();
	}

	public String processRecord(String callSessionId, String data) {

		System.out.println("selection " + data);

		Response kkResponse = new Response();

		ContextMapping recntxt = ContextMapping.getSingleContext(callSessionId);

		String ivrNumber = recntxt.getIvrNumber();

		IvrGroupzBaseMapping settingObj = IvrGroupzBaseMapping
				.getSingleivrnumberMap(ivrNumber);

		if (recntxt != null) {

			String grpzCode = recntxt.getgroupzCode();

			IvrGroupzMapping smap = IvrGroupzMapping
					.getSingleivrSourceMapwithGroupzCode(ivrNumber, grpzCode);

			int playspeed = 5;
			int settimeout = 5000;
			if (settingObj != null) {
				playspeed = settingObj.getplayspeed();
				settimeout = settingObj.getsettimeout();
			}

			ArrayList<String> dataList = new ArrayList<String>();

			String recordOptionsList = smap.getrecordOptionsUrl();

			JSONArray recoptListObj = new JSONArray();

			if (StaticUtils.isEmptyOrNull(recordOptionsList)) {

				recordOptionsList = smap.getrecordOptionsText();

				JSONObject recoptObj = (JSONObject) JSONSerializer
						.toJSON(recordOptionsList);

				recoptListObj = recoptObj.getJSONArray("dataList");

			} else {
				JSONObject recoptObj = (JSONObject) JSONSerializer
						.toJSON(recordOptionsList);
				recoptListObj = recoptObj.getJSONArray("urlList");
			}

			for (int i = 0; i < recoptListObj.size(); i++) {

				dataList.add(recoptListObj.getString(i));
				if (i == 0) {

					dataList.add(data.trim());
				}

			}
			String displayList = StaticUtils.createJSONString(dataList);

			recntxt.setcontextdisplayList(displayList);
			recntxt.seturl(data);
			recntxt.setrecordFlag(true);
			recntxt.setisRecMultiLangSelectionFlag(false);
			recntxt.save();

			kkResponse = StaticUtils.processUrlOrTextMultiList(displayList,
					playspeed, settimeout);

		}

		kkResponse.setSid(callSessionId);
		return kkResponse.getXML();

	}

	public static void main(String args[]) {

		ProcessRecordrequest newobj = new ProcessRecordrequest();
		newobj.processNewCall("919944085085", "1234567333", "sessionid1paya");

	}
}
