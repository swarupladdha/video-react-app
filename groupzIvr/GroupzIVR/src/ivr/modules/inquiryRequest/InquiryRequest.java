package ivr.modules.inquiryRequest;

import ivr.servlets.Inquiry;
import ivr.servlets.ServiceRequest;
import ivr.tables.CallHistory;

import ivr.tables.ContextMapping;

import ivr.tables.IvrGroupzMapping;
import ivr.tables.IvrGroupzBaseMapping;

import ivr.utils.GroupzInfoDetails;
import ivr.utils.StaticUtils;
import ivr.utils.inquiryUtils;
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

public class InquiryRequest {

	public static Logger logger = Logger.getLogger("inquiryLogger");
	static Properties prop = new Properties();

	public static List<GroupzInfoDetails> groupzList = null;

	static {
		try {

			InputStream in = InquiryRequest.class
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

		int timeout = 5000;

		IvrGroupzBaseMapping ivrsetting = null;

		String languageWelcomeUrl = null;
		String langSelectionList=null;
		try {

			boolean multiLangFlag = false;
			if (StaticUtils.isEmptyOrNull(callerId) == false
					&& StaticUtils.isEmptyOrNull(ivrNumber) == false
					&& StaticUtils.isEmptyOrNull(callSessionId) == false) {

				ivrNumber = ivrNumber.trim();
				callSessionId = callSessionId.trim();
				callerId = callerId.trim();

				ivrsetting = IvrGroupzBaseMapping.getSingleivrnumberMap(ivrNumber);

				if (ivrsetting != null) {
					multiLangFlag = ivrsetting.getmultiLanguageFlag();
					langSelectionList=ivrsetting.getlanguageSelectionList();
					languageWelcomeUrl = ivrsetting.getlanguageWelcomeURL();
					
					timeout = ivrsetting.getsettimeout();

				} else {
					logger.info("No Inquiry for the callsession id , caller id,ivrnumber : "
							+ callSessionId
							+ "number : "
							+ callerId
							+ "IVRnumber :" + ivrNumber);

					String notes = prop.getProperty("ivrNoInquiryNotes");
					String url = prop.getProperty("ivrNoInquiryUrl");

					kkResponse = StaticUtils.sendNoDetailsNotes(notes,url);
					kkResponse.setSid(callSessionId);
					return kkResponse.getXML();
				}

				if (multiLangFlag) {
					kkResponse = StaticUtils.playURL(languageWelcomeUrl,
							timeout);

					ContextMapping cm = new ContextMapping();
					cm.setCallSessionId(callSessionId);
					cm.setcontextdisplayList(languageWelcomeUrl);
					cm.setcontextselectionList(langSelectionList);
					cm.setIvrNumber(ivrNumber);
					cm.setCallerId(callerId);
					cm.setLastupdatetime(new Date());
					cm.setmultiLanguageFlag(true);
					cm.save();

				} else {
					kkResponse = InquiryRequest.processDetailedNewCall(
							ivrNumber, callSessionId, ivrsetting, callerId);
				}

			} else {
				logger.info("The callerid or sessionid or ivrnumber is"
						+ " null in inquiry request. Cal session Id is : "
						+ callSessionId + "number : " + callerId
						+ "IVRnumber :" + ivrNumber);

				kkResponse = StaticUtils.senderrorResp(callSessionId,
						ivrNumber, null);
				kkResponse.setSid(callSessionId);
				return kkResponse.getXML();
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Inside Catch of process new call Inquiry");
			logger.info(
					"Exception occured in process new inquiry. call session id,caller id ,ivrnumber  are "
							+ callSessionId
							+ "number : "
							+ callerId
							+ "IVRnumber :" + ivrNumber, e);

			kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber,
					null);
			kkResponse.setSid(callSessionId);
			return kkResponse.getXML();
		}

		kkResponse.setSid(callSessionId);
		return kkResponse.getXML();
	}

	public static Response processDetailedNewCall(String ivrNumber,
			String callSessionId, IvrGroupzBaseMapping ivrsetting, String callerId) {
	ContextMapping cm = new ContextMapping();
		Response kkResponse = new Response();
		int playspeed = 5;
		int timeout = 5000;
		boolean contactFlag = false;
		String groupzCode = null;
		GroupzInfoDetails selectedGroupzInfo = null;
	
		String dedicatedUrl = null;
		String dedicatedText = null;
		String defaulturl = null;
		String defaultNotes = null;

		String grpzInfoxmlString = null;
		String sucessFlagStr = "false";
		String formattedNumber = null;

		boolean multiLangFlag = false;
		try {

			cm = ContextMapping.getSingleContext(callSessionId);

			if (cm == null) {
				cm = new ContextMapping();
				cm.setCallSessionId(callSessionId);
				cm.setCallerId(callerId);
				cm.setIvrNumber(ivrNumber);
			}

			if (ivrsetting != null) {

				playspeed = ivrsetting.getplayspeed();
				timeout = ivrsetting.getsettimeout();
				multiLangFlag = ivrsetting.getmultiLanguageFlag();
			}

			if (Inquiry.maintenanceFlag == true) {

				if (ivrsetting != null) {
					dedicatedText = ivrsetting.getmaintenanceNotes();
					dedicatedUrl = ivrsetting.getmaintenanceUrl();

				}

				defaultNotes = prop.getProperty("ivrMaintenanceMsg");
				defaulturl = prop.getProperty("ivrMaintenanceUrl");

				kkResponse = StaticUtils.processUrlOrTextMessage(dedicatedUrl,
						dedicatedText, defaultNotes, defaulturl, playspeed,
						multiLangFlag, cm);

				kkResponse.setSid(callSessionId);
				kkResponse.addHangup();
				return kkResponse;

			}
			// Number validation for registry.
			formattedNumber = StaticUtils.validateMobile(callerId, ivrNumber);

			ArrayList<String> grpzDetails = StaticUtils
					.getGrpzMobileValidationList(formattedNumber);

			sucessFlagStr = grpzDetails.get(0);

			if (sucessFlagStr.equals("true")) {

				grpzInfoxmlString = grpzDetails.get(1);

				groupzList = StaticUtils.getGroupzInfoList(grpzInfoxmlString);

				if (groupzList == null || groupzList.isEmpty() == true) {

					formattedNumber = StaticUtils.validateLandline(callerId,
							ivrNumber);

					ArrayList<String> grpzLandLineDetails = StaticUtils
							.getGrpzLandlineValidationList(formattedNumber,
									ivrNumber);

					sucessFlagStr = grpzLandLineDetails.get(0);

					if (sucessFlagStr.equals("true")) {

						grpzInfoxmlString = grpzLandLineDetails.get(1);

						groupzList = StaticUtils
								.getGroupzInfoList(grpzInfoxmlString);

					} else {
						logger.info("There is error code in response while geting groupz list while checking for landline"
								+ callSessionId
								+ "number : "
								+ callerId
								+ "IVRnumber :" + ivrNumber);

						kkResponse = StaticUtils.senderrorResp(callSessionId,
								ivrNumber, cm);
						kkResponse.setSid(callSessionId);
						kkResponse.addHangup();
						return kkResponse;
					}

				}
			} else {
				logger.info("There is errorcode or problem while geting groupz list while checking for mobile"
						+ callSessionId
						+ "number : "
						+ callerId
						+ "IVRnumber :" + ivrNumber);

				kkResponse = StaticUtils.senderrorResp(callSessionId,
						ivrNumber, cm);
				kkResponse.setSid(callSessionId);
				kkResponse.addHangup();
				return kkResponse;
			}// end of validation.

			List<IvrGroupzMapping> smList = (List<IvrGroupzMapping>) IvrGroupzMapping
					.getListivrSourceMap(ivrNumber);

			if (smList == null || smList.size() == 0) {

				logger.info("No Inquiry for the callsession id , caller id,ivrnumber : "
						+ callSessionId
						+ "number : "
						+ callerId
						+ "IVRnumber :" + ivrNumber);

				String notes = prop.getProperty("ivrNoInquiryNotes");
				String url = prop.getProperty("ivrNoInquiryUrl");

				kkResponse = StaticUtils.sendNoDetailsNotes(notes,url);
				kkResponse.setSid(callSessionId);
				return kkResponse;
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

				Iterator<GroupzInfoDetails> grpziter = groupzList.iterator();
				List<String> grpzcodeList = new ArrayList<String>();

				while (grpziter.hasNext()) {
					selectedGroupzInfo = grpziter.next();
					String grpzCode = selectedGroupzInfo.getgrpzcode();

					grpzcodeList.add(grpzCode);

				}
				ivrgrpzcodeList.retainAll(grpzcodeList);

				if (ivrgrpzcodeList != null
						&& ivrgrpzcodeList.isEmpty() == false) {

					cm.setLastupdatetime(new Date());
					cm.save();

					if (ivrgrpzcodeList.size() > 1) {

						kkResponse = StaticUtils.createMultiGroupzData(
								ivrsetting, groupzcodeNamMap, ivrgrpzcodeList,
								null, playspeed, timeout, cm);

					} else {

						groupzCode = ivrgrpzcodeList.get(0);

						cm.setgroupzCode(groupzCode);
						cm.save();
						
						IvrGroupzMapping smap = IvrGroupzMapping
								.getSingleivrSourceMapwithGroupzCode(ivrNumber,
										groupzCode);
						
						kkResponse = inquiryUtils.generateNewInquiryRequest(
								ivrsetting, playspeed, timeout, cm,smap);

					}

				} else {

					contactFlag = true;
					ivrgrpzcodeList = ivrgrpzcodeListtemp;
				}

			}

			else {

				contactFlag = true;
			}

			if (contactFlag) {

				cm.setLastupdatetime(new Date());
				cm.setcontactFlag(contactFlag);
				cm.save();

				if (smList != null && smList.size() > 1) {

					kkResponse = StaticUtils.createMultiGroupzData(ivrsetting,
							groupzcodeNamMap, ivrgrpzcodeList, null, playspeed,
							timeout, cm);

				} else {

					IvrGroupzMapping srcmap = new IvrGroupzMapping();
					srcmap = smList.get(0);
					groupzCode = srcmap.getGroupzCode();

					cm.setgroupzCode(groupzCode);
					cm.save();
					
					IvrGroupzMapping smap = IvrGroupzMapping
							.getSingleivrSourceMapwithGroupzCode(ivrNumber,
									groupzCode);

					kkResponse = inquiryUtils.generateNewInquiryRequest(
							ivrsetting, playspeed, timeout, cm,smap);

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Inside Catch of process new call Inquiry");
			logger.info("Exception occured in process detailed new inquiry call session id is "
					+ callSessionId + e);

			kkResponse = StaticUtils
					.senderrorResp(callSessionId, ivrNumber, cm);
			return kkResponse;
		}
		return kkResponse;
	}

	public String processContinuousCall(String callSessionId, String data) {

		
		int timeout = 5000;
		int playspeed = 5;
		boolean ismultilanguageIVR = false;
		Response kkResponse = new Response();
		String ivrNumber = null;

		IvrGroupzBaseMapping ivrsetting = null;
		boolean contactFlag = false;
		boolean repeatMenu = false;
		boolean detailedInqFlag = false;
		boolean containsKey = false;
		String groupzCode = null;
		String languageWelcomeUrl = null;
	

		ContextMapping co = new ContextMapping();

		try {

			if (data != null && data.isEmpty() == false) {
				data = data.trim();
			}

			String repeatCode = prop.getProperty("repeatCode");
			String hangupCode = prop.getProperty("hangupCode");
			String previousMenucode = prop.getProperty("previousMenucode");

			co = ContextMapping.getSingleContext(callSessionId);

			if (co != null) {

				groupzCode = co.getgroupzCode();
				contactFlag = co.getcontactFlag();
				detailedInqFlag = co.getdetailedInqFlag();
				boolean multigrpzflag = co.getmultiGrpzFlag();
				boolean multiLanguageFlag = co.getmultiLanguageFlag();

				ivrNumber = co.getIvrNumber();
				String callerno = co.getCallerId();

				ivrsetting = IvrGroupzBaseMapping.getSingleivrnumberMap(ivrNumber);

				IvrGroupzMapping sm = IvrGroupzMapping
						.getSingleivrSourceMapwithGroupzCode(ivrNumber,
								groupzCode);

				if (ivrsetting != null) {
					playspeed = ivrsetting.getplayspeed();
					timeout = ivrsetting.getsettimeout();
					languageWelcomeUrl = ivrsetting.getlanguageWelcomeURL();
				
					ismultilanguageIVR = ivrsetting.getmultiLanguageFlag();
				}

			

				if (data == null || data.isEmpty() == true) {
					repeatMenu = true;

				} else if (data != null && data.trim().isEmpty() == false) {

					String selectionList = co.getcontextselectionList();

					if (selectionList != null
							&& selectionList.isEmpty() == false) {

						JSONObject selectObj = (JSONObject) JSONSerializer
								.toJSON(selectionList);

						JSONObject selectionListObj = (JSONObject) selectObj
								.get("selectionList");

						if (selectionListObj.containsKey(data)) {

							containsKey = true;

							if (multiLanguageFlag) {

								String selectedOption = null;

								selectedOption = selectionListObj
										.getString(data);

								if (StaticUtils.isEmptyOrNull(selectedOption) == false) {

									co.setmultiLanguageFlag(false);
									co.setlanguageSelected(selectedOption);
									co.save();

									String defualtlang = prop
											.getProperty("defualtLanguage");

									if (defualtlang.equals(selectedOption) == false) {
										co.setregionalLanguageFlag(true);
										co.save();
									}

									kkResponse = InquiryRequest
											.processDetailedNewCall(ivrNumber,
													callSessionId, ivrsetting,
													callerno);
									kkResponse.setSid(callSessionId);
									return kkResponse.getXML();
								}

							}

							if (multigrpzflag) {

								JSONObject selectedListObj = selectionListObj
										.getJSONObject(data);

								groupzCode = selectedListObj
										.getString("grpzcode");

								co.setgroupzCode(groupzCode);
								co.setmultiGrpzFlag(false);
								co.save();
								
								IvrGroupzMapping smap = IvrGroupzMapping
										.getSingleivrSourceMapwithGroupzCode(ivrNumber,
												groupzCode);

								kkResponse = inquiryUtils
										.generateNewInquiryRequest(ivrsetting,
												playspeed, timeout, co,smap);

							}

							if (detailedInqFlag) {

								boolean migrationStatus = true;
								String detailedinqselect = null;

								String selectedInqOption = co
										.getselectedgeneralinqOption();

								detailedinqselect = selectionListObj
										.getString(data);

								CallHistory callhst = new CallHistory();
								callhst.setcontactNumb(callerno);
								callhst.setdatetime(new Date());
								callhst.setgroupzCode(groupzCode);
								callhst.setivrnumber(ivrNumber);
								callhst.setselection(detailedinqselect);
								callhst.setmigrationStatus(migrationStatus);
								callhst.save();

								kkResponse = inquiryUtils
										.prcoessAfterFinalSelection(ivrsetting,
												playspeed, ivrNumber, callerno,
												detailedinqselect,
												selectedInqOption, co,
												callSessionId,
												ismultilanguageIVR,sm);

							}

							else if (!multigrpzflag && !multiLanguageFlag
									&& !detailedInqFlag) {

								groupzCode = co.getgroupzCode();

								String selectOpt = selectionListObj
										.getString(data);

								if (!contactFlag) {

									kkResponse = inquiryUtils
											.generatedetailedInquiryResponse(
													sm, co, playspeed, timeout,
													ivrsetting, selectOpt);

								}

								else if (contactFlag) {
									// insert transaction data to
									// call
									// history
									// table.

									CallHistory callhst = new CallHistory();
									callhst.setcontactNumb(callerno);
									callhst.setdatetime(new Date());
									callhst.setgroupzCode(groupzCode);
									callhst.setivrnumber(ivrNumber);
									callhst.setselection(selectOpt);
									callhst.setmigrationStatus(false);
									callhst.save();

									co.setselectedgeneralinqOption(selectOpt);
									co.save();

									kkResponse = inquiryUtils
											.prcoessAfterFinalSelection(
													ivrsetting, playspeed,
													ivrNumber, callerno,
													selectOpt, selectOpt, co,
													callSessionId,
													ismultilanguageIVR,sm);

								}

							}

						}
					}

					if (data.trim().equalsIgnoreCase(hangupCode)) {

						String AudioURl = ivrsetting.getAudiogeneralHangupUrl();
						String Audiotext = ivrsetting.getgeneralHangupNotes();

						kkResponse = StaticUtils.playUrlOrTextMessage(
								Audiotext, AudioURl, playspeed, co,
								ismultilanguageIVR);

						kkResponse.addHangup();

						kkResponse.setSid(callSessionId);
						return kkResponse.getXML();
					}

					else if (data.trim().equalsIgnoreCase(previousMenucode)) {

						if (multiLanguageFlag) {

							repeatMenu = true;

						}

						if (detailedInqFlag) {

							String displayList = co.getnewInquiryDisplayList();
							String selectList = co.getnewInquiryselectionList();
							
							co.setcontextdisplayList(displayList);
							co.setcontextselectionList(selectList);
							co.setdetailedInqFlag(false);
							co.save();
							kkResponse = StaticUtils.processUrlOrTextMultiList(
									displayList, playspeed, timeout);
						}

						else if (multigrpzflag) {

							repeatMenu = true;

						}

						else if (!multigrpzflag && !multiLanguageFlag
								&& !detailedInqFlag) {

							String multiGroupzWelNotes = co
									.getmultigrpzWelcomeNotes();

							if (multiGroupzWelNotes != null
									&& multiGroupzWelNotes.isEmpty() == false) {

								co.setmultiGrpzFlag(true);
								String displayList = co
										.getmultigrpzWelcomeNotes();
								String selectList = co.getmultigrpzselectlist();
								co.setcontextdisplayList(displayList);
								co.setcontextselectionList(selectList);
								co.setmultiMemberFlag(false);
								co.setmultimembWelcomeNotes(null);
								co.setmultiMembSelectlist(null);
								co.save();
								kkResponse = StaticUtils
										.processUrlOrTextMultiList(
												multiGroupzWelNotes, playspeed,
												timeout);

							}

						}
					}

					else if (data.trim().equals(repeatCode)
							|| (containsKey == false
									&& !(data.trim().equals(hangupCode)) && !(data
										.trim().equals(previousMenucode)))) {

						repeatMenu = true;

					}

				}

				if (repeatMenu) {

					if (multiLanguageFlag) {

						kkResponse = StaticUtils.playURL(languageWelcomeUrl,
								timeout);
						kkResponse.setSid(callSessionId);
						return kkResponse.getXML();

					}

					else {

						String welcomeNote = co.getcontextdisplayList();

						kkResponse = StaticUtils.processUrlOrTextMultiList(
								welcomeNote, playspeed, timeout);
						kkResponse.setSid(callSessionId);
						return kkResponse.getXML();
					}
				}
			} else {

				logger.info("The context ir ivrsetting is null -- "
						+ callSessionId);
				kkResponse = StaticUtils.senderrorResp(callSessionId,
						ivrNumber, co);

			}

			kkResponse.setSid(callSessionId);
			return kkResponse.getXML();
		} catch (Exception e) {
			e.printStackTrace();
			System.out
					.println("Inside Catch of process continous new call Inquiry");
			logger.info("Exception occured in process continous inquiry. call session id is "
					+ callSessionId + e);

			kkResponse = StaticUtils
					.senderrorResp(callSessionId, ivrNumber, co);
			kkResponse.setSid(callSessionId);
			return kkResponse.getXML();
		}

	}
}
