package ivr.modules.serviceRequest;

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

import ivr.servlets.ServiceRequest;
import ivr.tables.ContextMapping;
import ivr.tables.IvrGroupzMapping;
import ivr.tables.IvrGroupzBaseMapping;
import ivr.utils.GroupzInfoDetails;
import ivr.utils.GroupzMemberInfo;
import ivr.utils.StaticUtils;
import ivr.utils.serviceUtils;

public class ProcessServiceRequest
{

	static Properties prop = new Properties();
	public static boolean landlineFlag;
	public static Logger loggerServ = Logger.getLogger("serviceLogger");
	public static GroupzInfoDetails selectedGroupzInfo;

	static
	{
		try
		{
			InputStream in = ServiceRequest.class.getResourceAsStream("/ivr.properties");
			prop.load(in);
		}
		catch (Exception e)
		{
			loggerServ.info("Exception occured in load property file.", e);
			e.printStackTrace();
		}
	}

	public String processNewCall(String callerId, String ivrNumber, String callSessionId)
	{
		String responseXMl = null;
		Response kkResponse = new Response();
		String languageWelcomeUrl = null;
		IvrGroupzBaseMapping ivrnummap = null;
		int timeout = 5000;
		boolean multiLangFlag = false;
		String langSelectionList = null;
		
		
		try
		{
			if (StaticUtils.isEmptyOrNull(callerId) == false && StaticUtils.isEmptyOrNull(ivrNumber) == false 
					&& StaticUtils.isEmptyOrNull(callSessionId) == false)
			{

				ivrNumber = ivrNumber.trim();
				callSessionId = callSessionId.trim();
				callerId = callerId.trim();

				ivrnummap = IvrGroupzBaseMapping.getSingleivrnumberMap(ivrNumber);

				if (ivrnummap != null)
				{
					langSelectionList = ivrnummap.getlanguageSelectionList();
					timeout = ivrnummap.getsettimeout();
					multiLangFlag = ivrnummap.getmultiLanguageFlag();
					languageWelcomeUrl = ivrnummap.getlanguageWelcomeURL();
				}
				else
				{
					String timestr = prop.getProperty("settimeout");
					timeout = Integer.parseInt(timestr);
				}

				if (multiLangFlag)
				{

					kkResponse = StaticUtils.playURL(languageWelcomeUrl, timeout);

					kkResponse.setSid(callSessionId);
					responseXMl = kkResponse.getXML();

					ContextMapping cm = new ContextMapping();
					cm.setCallSessionId(callSessionId);
					cm.setIvrNumber(ivrNumber);
					cm.setCallerId(callerId);
					cm.setcontextdisplayList(languageWelcomeUrl);
					cm.setcontextselectionList(langSelectionList);
					cm.setLastupdatetime(new Date());
					cm.setmultiLanguageFlag(true);
					cm.save();

				}
				else
				{
					responseXMl = ProcessServiceRequest.processDetailedNewCall(callerId, ivrNumber, callSessionId);
					System.out.println("processDetailedNewCall " + responseXMl);
				}
			}
			else
			{
				loggerServ.info("The callerid or sessionid or ivrnumber is null in service request." + callSessionId);

				kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, null);
				kkResponse.setSid(callSessionId);
				return kkResponse.getXML();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			loggerServ.info("Exception occured in process new call in service request.", e);
			kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber,null);
			kkResponse.setSid(callSessionId);
			return kkResponse.getXML();
		}

		return responseXMl;

	}

	public static String processDetailedNewCall(String callerID, String ivrNumber, String callSessionId)
	{
		System.out.println("++++++++++++ Insde the function call processDetailedNewCall ++++++++++");
		Response kkResponse = new Response();
		String groupzCode = null;
		landlineFlag = false;
		String dedicatedUrl = null;
		String dedicatedText = null;
		String defaulturl = null;
		String defaultNotes = null;
		IvrGroupzBaseMapping ivrnummap = null;
		int playspeed = 5;
		int timeout = 5000;
		int memberId = -1;
		String membXmlInfo = null;
		String sucessFlagStr = "false";
		String formattedNumber = null;
		List<GroupzInfoDetails> groupzList = null;
		String grpzInfoxmlString = null;
		ContextMapping cm = new ContextMapping();
		boolean multiLangFlag = false;

		try
		{
			ivrnummap = IvrGroupzBaseMapping.getSingleivrnumberMap(ivrNumber);
			cm = ContextMapping.getSingleContext(callSessionId);

			if (cm == null)
			{
				cm = new ContextMapping();
				cm.setCallSessionId(callSessionId);
			}

			if (ivrnummap != null)
			{
				playspeed = ivrnummap.getplayspeed();
				timeout = ivrnummap.getsettimeout();
				multiLangFlag = ivrnummap.getmultiLanguageFlag();
			}
			else
			{
				String playspeedstr = prop.getProperty("playspeed");
				playspeed = Integer.parseInt(playspeedstr);

				String timestr = prop.getProperty("settimeout");
				timeout = Integer.parseInt(timestr);
			}

			if (ServiceRequest.maintenanceFlag == true)
			{
				if (ivrnummap != null)
				{
					dedicatedText = ivrnummap.getmaintenanceNotes();
					dedicatedUrl = ivrnummap.getmaintenanceUrl();
				}

				defaultNotes = prop.getProperty("ivrMaintenanceMsg");
				defaulturl = prop.getProperty("ivrMaintenanceUrl");

				kkResponse = StaticUtils.processUrlOrTextMessage(dedicatedUrl, dedicatedText, defaultNotes, defaulturl, playspeed, 
						multiLangFlag, cm);

				kkResponse.setSid(callSessionId);
				kkResponse.addHangup();
				return kkResponse.getXML();

			}
			
			// callerID validation
			formattedNumber = StaticUtils.validateMobile(callerID, ivrNumber);

			System.out.println("number : " + formattedNumber);

			ArrayList<String> grpzMobileDetails = StaticUtils.getGrpzMobileValidationList(formattedNumber);

			sucessFlagStr = grpzMobileDetails.get(0);

			
//   SuccessFlagStr = false, it means calling number is not a mobile number. So the below if and else condition is commented.
			
		if (sucessFlagStr.equals("true"))
			{
				grpzInfoxmlString = grpzMobileDetails.get(1);
				groupzList = StaticUtils.getGroupzInfoList(grpzInfoxmlString);

				if (groupzList == null || groupzList.isEmpty() == true)
				{
					formattedNumber = StaticUtils.validateLandline(callerID, ivrNumber);

					ArrayList<String> grpzLandLineDetails = StaticUtils.getGrpzLandlineValidationList(formattedNumber, ivrNumber);

					sucessFlagStr = grpzLandLineDetails.get(0);

			if (sucessFlagStr.equals("true"))
				{
						grpzInfoxmlString = grpzLandLineDetails.get(1);
						groupzList = StaticUtils.getGroupzInfoList(grpzInfoxmlString);

						if (groupzList != null && groupzList.isEmpty() == false && groupzList.size() > 0)
						{
							landlineFlag = true;
						}
						else
						{
							System.out.println("else condition not reg");
							loggerServ.info("The GroupzList is empty, you are not registered to any groups.:" + callSessionId);
							kkResponse = StaticUtils.sendNotRegGrupzResp(ivrnummap, cm);
							kkResponse.setSid(callSessionId);
							kkResponse.addHangup();
							return kkResponse.getXML();
						}
				}
				else
					{
						System.out.println("else condition not groupz");
						loggerServ.info("There is error code in response while geting groupz list while checking for landline" + callSessionId
										+ "number : " + callerID + "IVRnumber :" + ivrNumber);
						kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, cm);
						kkResponse.setSid(callSessionId);
						kkResponse.addHangup();
						return kkResponse.getXML();
					}
				}
			}
			else
			{
				loggerServ.info("There is errorcode in reponse while geting groupz list while checking for mobile" + callSessionId
								+ "number : " + callerID + "IVRnumber :" + ivrNumber);
				kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, cm);
				kkResponse.setSid(callSessionId);
				kkResponse.addHangup();
				return kkResponse.getXML();
			}

			// end validation

			List<IvrGroupzMapping> smList = (List<IvrGroupzMapping>) IvrGroupzMapping.getListivrSourceMap(ivrNumber);
			System.out.println("smList is");
			System.out.println(smList);
			if (smList == null || smList.isEmpty() == true)
			{
				System.out.println("smlist is null");
				//getting grpzcode and grpzid for the called ivrnumber which is not in the table
				boolean globalsingleregstrdgrpz = false;
				int size = groupzList.size();
				HashMap<String, String> groupzinfoMap = new HashMap<String, String>();
				String singlegrpzCode = null;

				Iterator<GroupzInfoDetails> grpziter = groupzList.iterator();
				List<String> regisgrpzcodeList = new ArrayList<String>();
				
				//regisgrpzcodeList = list of all grpzcode for that ivrnumber in grpz server
				
				while (grpziter.hasNext())
				{
					selectedGroupzInfo = grpziter.next();
					String grpzCode = selectedGroupzInfo.getgrpzcode();
					String groupzId = selectedGroupzInfo.getgrpzid();
					String groupzName = selectedGroupzInfo.getgrpzname();
					String grpzString = groupzId + "," + groupzName;
					groupzinfoMap.put(grpzCode, grpzString);
					regisgrpzcodeList.add(grpzCode);
				}

				if (size > 1)
				{
					List<String> srcgrpzcodeList = new ArrayList<String>();
					List<IvrGroupzMapping> allsrcmap = IvrGroupzMapping.getListofAllsourceMap();

					//allsrcmap = list of all grpzcode registered to cid

					if (allsrcmap != null && allsrcmap.size() != 0)
					{
						System.out.println(allsrcmap);
						for (IvrGroupzMapping smp : allsrcmap)
						{
							String grpzcde = smp.getgroupzCode();
							srcgrpzcodeList.add(grpzcde);
						}
					}
					srcgrpzcodeList.retainAll(regisgrpzcodeList);

					if (srcgrpzcodeList != null && srcgrpzcodeList.size() != 0)
					{
						for (String s : srcgrpzcodeList)
						{

							if (s != null)
							{
								regisgrpzcodeList.remove(s);//removing only the not matching grpzcode from both list and considering regisgrpzcodeList
							}
						}
					}

					if (regisgrpzcodeList != null && regisgrpzcodeList.size() > 0)
					{
						int i = 1;
						String selectionListmsg = "";
						String displayGroupzList = "";

						if (regisgrpzcodeList.size() == 1)
						{
							globalsingleregstrdgrpz = true;
							singlegrpzCode = regisgrpzcodeList.get(0);
						}
						else
						{
							ArrayList<String> displaydataArray = new ArrayList<String>();
							ArrayList<String> welcomedataArray = new ArrayList<String>();
							ArrayList<String> enddataArray = new ArrayList<String>();

							JSONObject listObj = new JSONObject();
							JSONObject sellistObj = new JSONObject();
							
							for (String grpzcode : regisgrpzcodeList)
							{

								String grpzString = groupzinfoMap.get(grpzcode);

								String[] grpzinfo = grpzString.split(",");

								String groupzID = grpzinfo[0];
								String groupzName = grpzinfo[1];

								
								
								JSONObject seldataObj = new JSONObject();

								seldataObj.put("grpzcode", grpzcode);
								seldataObj.put("groupzID", groupzID);
								seldataObj.put("groupzName", groupzName);

								sellistObj.put(i + "", seldataObj);

					

								//selectionListmsg = listObj.toString();

								String pressStr = "Press " + i + " for ";

								displaydataArray.add(pressStr);
								displaydataArray.add(groupzName);

								i++;

							}
							listObj.put("selectionList", sellistObj);
							selectionListmsg = listObj.toString();
							//selectionListmsg = selectionListmsg.substring(0,
							//		selectionListmsg.length() - 1);
							
							String finaldisplayListText = null;
							String finalSelctHangupNotes = null;
							
							if (ivrnummap != null)
							{
								finaldisplayListText = ivrnummap.getgrpzWelcomeNotes();
								finalSelctHangupNotes = ivrnummap.getselectionHangupNotes();
							}
							String defualtgrpzSelect = prop.getProperty("ivrMultigrpzWelcomeNotes");
							String defualtSect = prop.getProperty("ivrselectionHangupNotes");

							if (finaldisplayListText == null || finaldisplayListText.isEmpty() == true)
							{
								finaldisplayListText = defualtgrpzSelect;
							}
							if (finalSelctHangupNotes == null || finalSelctHangupNotes.isEmpty() == true)
							{
								finalSelctHangupNotes = defualtSect;
							}

							welcomedataArray = StaticUtils.createJSONdataArray(finaldisplayListText);
							enddataArray = StaticUtils.createJSONdataArray(finalSelctHangupNotes);

							welcomedataArray.addAll(displaydataArray);
							welcomedataArray.addAll(enddataArray);
							displayGroupzList = StaticUtils.createJSONString(welcomedataArray);
							
							cm.setIvrNumber(ivrNumber);
							cm.setmultigrpzselectlist(selectionListmsg);
							System.out.println("selectionListmsg  "+selectionListmsg);
							cm.setCallerId(formattedNumber);
							cm.setcontextdisplayList(displayGroupzList);
							cm.setcontextselectionList(selectionListmsg);
							cm.setLastupdatetime(new Date());
							cm.setmultiGrpzFlag(true);
							cm.setmemberId(memberId);
							cm.setmultiMemberFlag(false);
							cm.setglobalFlag(true);
							cm.setmultigrpzWelcomeNotes(displayGroupzList);
							cm.save();

							kkResponse = StaticUtils.processUrlOrTextMultiList(displayGroupzList, playspeed, timeout);
						}
					}
					else
					{
						kkResponse = StaticUtils.sendNotRegGrupzResp(ivrnummap, cm);
						kkResponse.setSid(callSessionId);
						kkResponse.addHangup();
						return kkResponse.getXML();
					}
				}
				else if (size == 1)
				{
					String grpzCode = groupzList.get(0).getgrpzcode();
					IvrGroupzMapping smp = IvrGroupzMapping.getSourcewithgrpzCode(grpzCode);
					
					//mapping and getting the grpzcode from ivrgroupz table
					
					if (smp != null)
					{
						kkResponse = StaticUtils.sendNotRegGrupzResp(ivrnummap, cm);
						kkResponse.setSid(callSessionId);
						kkResponse.addHangup();
						return kkResponse.getXML();
					}
					else
					{
						globalsingleregstrdgrpz = true;
						singlegrpzCode = grpzCode;
					}
				}

				if (globalsingleregstrdgrpz)
				{
					String groupzId = null;		
					String grpzdetails = groupzinfoMap.get(singlegrpzCode);
					String[] grpzdetList = grpzdetails.split(",");
					groupzId = grpzdetList[0];
					
					List<GroupzMemberInfo> memberList = null;

					if (landlineFlag == false)
					{
						ArrayList<String> memberDetails = StaticUtils.getMemberMobileValidationList(formattedNumber, groupzId);

						//validating the mobile number and created xml for getMemberMobileValidationList
						
						sucessFlagStr = memberDetails.get(0);

						if (sucessFlagStr.equals("true"))
						{
							membXmlInfo = memberDetails.get(1);
							memberList = StaticUtils.getMemberInfoList(membXmlInfo);
							
							// getting member id using getMemberInfoList
						}
						else
						{
							loggerServ.info(" error code in get memberList xml for mobile information from Rest API : sessionID - "	
						+ callSessionId + "number : " + callerID);

							kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, cm);
							kkResponse.setSid(callSessionId);
							kkResponse.addHangup();
							return kkResponse.getXML();
						}
					}
					else
					{
						ArrayList<String> memberDetails = StaticUtils.getMemberLandLineValidationList(formattedNumber, groupzId);

						sucessFlagStr = memberDetails.get(0);
						
						if (sucessFlagStr.equals("true"))
						{
							membXmlInfo = memberDetails.get(1);
							memberList = StaticUtils.getMemberInfoList(membXmlInfo);
							
							// getting member id using getMemberInfoList							
							
						}
						else
						{
							loggerServ.info(" error code in get memberList xml for landline information from Rest API : sessionID - "
											+ callSessionId	+ "number : " + callerID);
							kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, cm);
							kkResponse.setSid(callSessionId);
							kkResponse.addHangup();
							return kkResponse.getXML();
						}
					}

					if (memberList != null && memberList.size() > 0)
					{
						
						int memblistSize = memberList.size();

						if (memblistSize > 1)
						{
							cm.setIvrNumber(ivrNumber);
							cm.setCallerId(formattedNumber);
							cm.setLastupdatetime(new Date());
							cm.setmultiGrpzFlag(false);
							cm.setmemberId(memberId);
							cm.setmultiMemberFlag(true);
							cm.setglobalFlag(true);
							cm.setgroupzId(groupzId);
							cm.setgroupzCode(singlegrpzCode);
							cm.save();

							String displayMemberList = StaticUtils.createMemberlistString(callSessionId, memberList, cm, ivrNumber);

							//createMemberListString() is used to create a selection list i.e., press 1 for.....
							
							cm.setmultimembWelcomeNotes(displayMemberList);
							cm.save();

							kkResponse = StaticUtils.processUrlOrTextMultiList(displayMemberList, playspeed, timeout);
							
							//processUrlOrTextMultiList() is used to create play audio or text file

						}

						else if (memblistSize == 1)
						{
							// creates new call response which shows
							// category selection list.

							memberId = memberList.get(0).getMemberId();

							cm.setIvrNumber(ivrNumber);
							cm.setCallerId(formattedNumber);
							cm.setLastupdatetime(new Date());
							cm.setmultiGrpzFlag(false);
							cm.setgroupzCode(singlegrpzCode);
							cm.setgroupzId(groupzId);
							cm.setmemberId(memberId);
							cm.setmultiMemberFlag(false);
							cm.setglobalFlag(true);
							cm.save();

							kkResponse = serviceUtils.generateGlobalNewCallResponse(cm);
							
							// use of generateGlobalNewCallResponse() combining all the notes and url to create a category list and playing the audio or text list

						}
					}
					else
					{
						loggerServ.info("Member doesnt not exists - " + formattedNumber + " sessionId : " + callSessionId);
						kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, cm);
						kkResponse.setSid(callSessionId);
						return kkResponse.getXML();
					}
				}
			}
			//if ivrnumber is in the ivrgroupz(ivrgroupzbase) table and dedicated to many grpz
			else if (smList != null && smList.size() >= 1)
			{
				System.out.println("smList is not null");
				HashMap<String, String> groupzcodeNamMap = new HashMap<String, String>();

				List<String> ivrgrpzcodeList = new ArrayList<String>();
				Iterator<IvrGroupzMapping> iterator = smList.iterator();
				IvrGroupzMapping sm = null;

				while (iterator.hasNext())
				{
					sm = iterator.next();
					String grpzCode = sm.getgroupzCode();
					String grpzNameUrl = sm.getgroupzNameUrl();
					ivrgrpzcodeList.add(grpzCode);
					groupzcodeNamMap.put(grpzCode, grpzNameUrl); //mapping grpzurl for each grpzcode identically
				}
				if (groupzList != null && groupzList.isEmpty() == false)
				{
					Iterator<GroupzInfoDetails> grpziter = groupzList.iterator();
					List<String> grpzcodeList = new ArrayList<String>();

					HashMap<String, String> groupzinfoMap = new HashMap<String, String>();

					while (grpziter.hasNext())
					{
						selectedGroupzInfo = grpziter.next();
						String grpzCode = selectedGroupzInfo.getgrpzcode();
						String groupzId = selectedGroupzInfo.getgrpzid();
						groupzinfoMap.put(grpzCode, groupzId);
						System.out.println("groupzinfoMap ++" + groupzinfoMap);
						grpzcodeList.add(grpzCode);
					}
					
					ivrgrpzcodeList.retainAll(grpzcodeList);

					if (ivrgrpzcodeList != null	&& ivrgrpzcodeList.isEmpty() == false)
					{
						if (ivrgrpzcodeList.size() > 1)
						{
							System.out.println("ivrgrpzcodeList loop -----------");
							cm.setIvrNumber(ivrNumber);
							cm.setCallerId(formattedNumber);
							cm.setLastupdatetime(new Date());
							cm.setmultiGrpzFlag(true);
							cm.setmemberId(memberId);
							cm.setmultiMemberFlag(false);
							cm.setglobalFlag(false);
							cm.save();

							kkResponse = StaticUtils.createMultiGroupzData(ivrnummap, groupzcodeNamMap, ivrgrpzcodeList, groupzinfoMap, 
									playspeed, timeout, cm);
						}
						else if (ivrgrpzcodeList.size() == 1)
						{
							System.out.println("ivrgrpzcodeList loop value is 1");
							groupzCode = ivrgrpzcodeList.get(0);
							IvrGroupzMapping smap = IvrGroupzMapping.getSingleivrSourceMapwithGroupzCode(ivrNumber, groupzCode);
							String groupzId = groupzinfoMap.get(groupzCode);

							List<GroupzMemberInfo> memberList = null;

							if (landlineFlag == false)
							{
								ArrayList<String> memberDetails = StaticUtils.getMemberMobileValidationList(formattedNumber, groupzId);

								sucessFlagStr = memberDetails.get(0);

								if (sucessFlagStr.equals("true"))
								{
									membXmlInfo = memberDetails.get(1);
									memberList = StaticUtils.getMemberInfoList(membXmlInfo);

								} else {
									loggerServ.info(" error code in get memberList xml for mobile information from Rest API : sessionID - " + callSessionId
													+ "number : " + callerID);

									kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, cm);
									kkResponse.setSid(callSessionId);
									kkResponse.addHangup();
									return kkResponse.getXML();
								}
							}
							else
							{
								ArrayList<String> memberDetails = StaticUtils.getMemberLandLineValidationList(formattedNumber, 
										groupzId);

								sucessFlagStr = memberDetails.get(0);
								System.out.println("memberDetails is ++"+memberDetails);
								if (sucessFlagStr.equals("true"))
								{
									membXmlInfo = memberDetails.get(1);
									System.out.println("membXmlInfo is ++"+membXmlInfo);
									memberList = StaticUtils.getMemberInfoList(membXmlInfo);
									System.out.println("memberList is ++"+memberList);
								}
								else
								{
									loggerServ.info(" error code in get memberList xml for landline information from Rest API : "
											+ "sessionID - " + callSessionId + "number : " + callerID);
									
									kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, cm);
									kkResponse.setSid(callSessionId);
									kkResponse.addHangup();
									return kkResponse.getXML();
								}
							}

							if (memberList != null && memberList.size() > 0)
							{
								int memblistSize = memberList.size();
								System.out.println("member id %%%" + memblistSize);
																							
								if (memblistSize > 1)
								{
									cm.setIvrNumber(ivrNumber);							
									cm.setCallerId(formattedNumber);
									cm.setLastupdatetime(new Date());
									cm.setmultiGrpzFlag(false);
									cm.setgroupzCode(groupzCode);
									cm.setgroupzId(groupzId);
									cm.setmultiMemberFlag(true);
									cm.setmemberId(memberId);
									cm.setcontextselectionList(smap.getselectionlist());
									cm.save();

									String displayMemberList = StaticUtils.createMemberlistString(callSessionId, memberList, cm, ivrNumber);

									kkResponse = StaticUtils.processUrlOrTextMultiList(displayMemberList, playspeed, timeout);
								}
								else if (memblistSize == 1)
								{
									memberId = memberList.get(0).getMemberId();
									cm.setIvrNumber(ivrNumber);
									cm.setcontextselectionList(smap.getselectionlist());
									cm.setCallerId(formattedNumber);
									cm.setLastupdatetime(new Date());
									cm.setmultiGrpzFlag(false);
									cm.setgroupzCode(groupzCode);
									cm.setgroupzId(groupzId);
									cm.setmemberId(memberId);
									cm.setmultiMemberFlag(false);
									cm.save();

									kkResponse = serviceUtils.generateNewCallResponse(smap, cm, playspeed, timeout, ivrnummap);
								}
							}
							else
							{
								// If the callerId is not registered as member
								loggerServ.info("Member doesnt not exists - " + formattedNumber + " sessionId : " + callSessionId);
								kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, cm);
								kkResponse.setSid(callSessionId);
								return kkResponse.getXML();
							}
						}
					}
					else
					{
						loggerServ.info("The groupz intersection (groupcode in ivarMap table and the groupcode requested for the "
								+ "given formatted number is null for the number " + formattedNumber + " and  the session id :"
										+ callSessionId);

						kkResponse = StaticUtils.sendNotRegGrupzResp(ivrnummap, cm);
						kkResponse.setSid(callSessionId);
						return kkResponse.getXML();
					}
				}
				else
				{
					loggerServ.info("The groupz list requested for the given  formatted number is null for the number "
									+ formattedNumber + " and  the session id :" + callSessionId);
					kkResponse = StaticUtils.sendNotRegGrupzResp(ivrnummap, cm);
					kkResponse.setSid(callSessionId);
					return kkResponse.getXML();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			loggerServ.info("Exception occured in process new call in service request.", e);
			kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, cm);
			kkResponse.setSid(callSessionId);
			return kkResponse.getXML();
		}
		kkResponse.setSid(callSessionId);
		return kkResponse.getXML();
	}

	public String processContinuousCall(String callSessionId, String data)
	{
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
	
		try
		{
			if (data != null && data.isEmpty() == false)
			{
				data = data.trim();
			}

			loggerServ.info("The data received is " + data + "session id is : " + callSessionId);
			System.out.println("The data received is +++++" + data + "session id is : " + callSessionId);
			boolean repeatFlag = false;

			if (StaticUtils.isEmptyOrNull(callSessionId) == true)
			{
				loggerServ.info("The call session id is  empty :" + callSessionId);
				kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, co);
				kkResponse.setSid(callSessionId);
				return kkResponse.getXML();
			}
			co = ContextMapping.getSingleContext(callSessionId);
			System.out.println("The ContextMapping received is +++++" + co );
			if (co == null)
			{
				loggerServ.info("The context does not exists in the context table");
				kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, co);
				kkResponse.setSid(callSessionId);
				return kkResponse.getXML();
			}

			String formattedCallerId = co.getCallerId();

			if (StaticUtils.isEmptyOrNull(formattedCallerId) == true)
			{

				loggerServ.info("The formatted number is empty :" + formattedCallerId);
				kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, co);
				kkResponse.setSid(callSessionId);
				return kkResponse.getXML();
			}

			boolean multigrpzFalg = co.getmultiGrpzFlag();
			boolean multimembFalg = co.getmultiMemberFlag();
			boolean multiLanguageFlag = co.getmultiLanguageFlag();
			boolean globalFlag = co.getglobalFlag();
			formattednumber = co.getCallerId();
			ivrNumber = co.getIvrNumber();
			ivrnummap = IvrGroupzBaseMapping.getSingleivrnumberMap(ivrNumber);
			System.out.println("The ivrnummap received is +++++" + ivrnummap );
			if (ivrnummap != null)
			{
				playspeed = ivrnummap.getplayspeed();
				timeout = ivrnummap.getsettimeout();
				languageWelcomeUrl = ivrnummap.getlanguageWelcomeURL();
			}
			else
			{
				System.out.println("ivrnum is null ");
				String playspeedstr = prop.getProperty("playspeed");
				playspeed = Integer.parseInt(playspeedstr);
				String timestr = prop.getProperty("settimeout");
				timeout = Integer.parseInt(timestr);
			}
			
			String repeatCode = prop.getProperty("repeatCode");
			System.out.println("repeatCode   "  + repeatCode);
			String hangupCode = prop.getProperty("hangupCode");
			System.out.println("hangupCode   "  + hangupCode);
			String previousMenucode = prop.getProperty("previousMenucode");
			System.out.println("previousMenucode   "  + previousMenucode);
			
			String selectionList = co.getcontextselectionList();
			System.out.println("selectionList   "  + selectionList);
			String welcomeNote = co.getcontextdisplayList();
			System.out.println("welcomeNote   "  + welcomeNote);

			if (data == null || data.isEmpty() == true)
			{
				repeatFlag = true;
			}

			if (data != null && data.trim().isEmpty() == false)
			{
				if (selectionList != null && selectionList.isEmpty() == false)
				{
					System.out.println("The data selectionList is +++++" + selectionList + "+++++++++++++");
					JSONObject selectObj = (JSONObject) JSONSerializer.toJSON(selectionList);
					JSONObject selectionListObj = (JSONObject) selectObj.get("selectionList");
					System.out.println("The selectionListObj is +++++" + selectionListObj + "+++++++++++++");
					 
					if (selectionListObj.containsKey(data))
					{
						containsKey = true;
						System.out.println("####################################");
						if (multiLanguageFlag)
						{	
							String selectedOption = null;
							selectedOption = selectionListObj.getString(data);
							System.out.println("*********** processDetailedNewCall ************");
							
							if (StaticUtils.isEmptyOrNull(selectedOption) == false)
							{
								co.setmultiLanguageFlag(false);
								co.setlanguageSelected(selectedOption);
								co.save();

								String defualtlang = prop.getProperty("defualtLanguage");

								if (defualtlang.equals(selectedOption) == false)
								{
									co.setregionalLanguageFlag(true);
									co.save();
								}

								String responseXMl = ProcessServiceRequest.processDetailedNewCall(formattednumber, ivrNumber, 
										callSessionId);

								return responseXMl;
							}
						}

						if (multigrpzFalg == true)
						{
							JSONObject grpzselectionListObj = selectionListObj.getJSONObject(data);

							String groupzcode = grpzselectionListObj.getString("grpzcode");
							String groupzId = grpzselectionListObj.getString("groupzID");
						
							co.setgroupzCode(groupzcode);
							co.setgroupzId(groupzId);
							co.save();

							IvrGroupzMapping sm = IvrGroupzMapping.getSingleivrSourceMapwithGroupzCode(ivrNumber, groupzcode);

							/*if (sm != null)
							{
								selectionList = sm.getselectionlist();
							}
							else
							{
								loggerServ.info("the sourceMap is missing for the IVRNUMBER : " + ivrNumber + " and sessionId is :"
												+ callSessionId + " and number is :" + formattednumber);

								kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, co);
								kkResponse.setSid(callSessionId);
								return kkResponse.getXML();
							}*/

							List<GroupzMemberInfo> memberList = null;

							if (landlineFlag == false)
							{
								ArrayList<String> memberDetails = StaticUtils.getMemberMobileValidationList(formattednumber, groupzId);

								sucessFlagStr = memberDetails.get(0);

								if (sucessFlagStr.equals("true"))
								{
									membXmlInfo = memberDetails.get(1);
									memberList = StaticUtils.getMemberInfoList(membXmlInfo);
								}
								else
								{
									loggerServ.info(" error code in get memberList xml for mobile information from Rest API : "
											+ "sessionID - " + callSessionId + "number : " + formattednumber);

									kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, co);
									kkResponse.setSid(callSessionId);
									kkResponse.addHangup();
									return kkResponse.getXML();
								}
							}
							else
							{
							  ArrayList<String> memberDetails = StaticUtils.getMemberLandLineValidationList(formattednumber, groupzId);

								sucessFlagStr = memberDetails.get(0);

								if (sucessFlagStr.equals("true"))
								{
									membXmlInfo = memberDetails.get(1);
									memberList = StaticUtils.getMemberInfoList(membXmlInfo);
								}
								else
								{
									loggerServ.info(" error code in get memberList xml for landline information from Rest API : "
											+ "sessionID - " + callSessionId + "number : " + formattednumber);

									kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, co);
									kkResponse.setSid(callSessionId);
									kkResponse.addHangup();
									return kkResponse.getXML();
								}
							}

							if (memberList != null && memberList.size() > 0)
							{
								int memblistSize = memberList.size();

								if (memblistSize > 1)
								{
									String displayMemberList = StaticUtils.createMemberlistString(callSessionId, memberList,co,
											ivrNumber);

									co.setmultiGrpzFlag(false);
									co.setmultiMemberFlag(true);
									co.save();

									kkResponse = StaticUtils.processUrlOrTextMultiList(displayMemberList, playspeed, timeout);
								}
								else if (memblistSize == 1)
								{
									memberId = memberList.get(0).getMemberId();

									co.setmultiGrpzFlag(false);
									co.setmultiMemberFlag(false);
									co.setgroupzCode(groupzcode);
									co.setgroupzId(groupzId);
									co.setmemberId(memberId);
									co.save();

									if (globalFlag)
									{
										kkResponse = serviceUtils.generateGlobalNewCallResponse(co);
									}
									else
									{
										if (sm != null)
										{
											kkResponse = serviceUtils.generateNewCallResponse(sm, co, playspeed, timeout, ivrnummap);
										}
									}
								}
							}
							else
							{
								loggerServ.info("Member doesnt not exists - " + formattedCallerId + " sessionId : " + callSessionId);

								kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, co);
								kkResponse.setSid(callSessionId);
								return kkResponse.getXML();
							}
						}
						else if (multimembFalg == true)
						{
							String selectdmemb = null;
							String groupzCode = co.getgroupzCode();

							IvrGroupzMapping smmap = IvrGroupzMapping.getSingleivrSourceMapwithGroupzCode(ivrNumber, groupzCode);
							JSONObject memberdetails= selectionListObj.getJSONObject(data); 
							
							selectdmemb = memberdetails.getString("memberid"); 
							memberId = Integer.parseInt(selectdmemb);

							co.setmemberId(memberId);
							co.setmultiGrpzFlag(false);
							co.setmultiMemberFlag(false);
							co.save();
							
							if (globalFlag)
							{
								kkResponse = serviceUtils.generateGlobalNewCallResponse(co);
							}
							else
							{
								kkResponse = serviceUtils.generateNewCallResponse(smmap, co, playspeed, timeout, ivrnummap);
							}
						}
						else if (multigrpzFalg == false && multimembFalg == false && multiLanguageFlag == false)
						{
							int memid = co.getmemberId();
							String memberIDStr = Integer.toString(memid);
							String groupzId = co.getgroupzId();
							String groupzcode=co.getgroupzCode();
							String category = selectionListObj.getString(data);

							boolean statusFlag = serviceUtils.placeGroupzIssueWithSourceType(groupzId, memberIDStr, category, formattednumber, callSessionId,groupzcode);

							if (statusFlag)
							{
								String dedicatedaudioHangupUrl = null;
								String dedicatedaudioHangupText = null;

								if (!globalFlag)
								{
									if (ivrnummap != null)
									{
										dedicatedaudioHangupUrl = ivrnummap.getaudioSelectionHangupUrl();
										System.out.println("dedicatedaudioHangupUrl:     &&&&& " + dedicatedaudioHangupUrl);
										dedicatedaudioHangupText = ivrnummap.getselectionHangupNotes();
										System.out.println("dedicatedaudioHangupText:     &&&&& " + dedicatedaudioHangupText);
										multiLanguageFlag = ivrnummap.getmultiLanguageFlag();
									}
								}
								String defualtaudioHangupUrl = prop.getProperty("ivrhangupUrl");
								String defualtaudioHangupText = prop.getProperty("ivrhangupNotes");
								kkResponse = StaticUtils.processUrlOrTextMessage(dedicatedaudioHangupUrl, dedicatedaudioHangupText,
												defualtaudioHangupText, defualtaudioHangupUrl, playspeed, multiLanguageFlag, co);
								kkResponse.addHangup();
							}
							else
							{
								loggerServ.info("Problem in placing service request for number and callSessionID :  "
												+ formattednumber + callSessionId);
								kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, co);
							}
						}
					}
				}

				if (data.trim().equalsIgnoreCase(hangupCode))
				{
					System.out.println("hangupCode");
					kkResponse = serviceUtils.hangUpProcess(ivrnummap, globalFlag, playspeed, co);
				}
				else if (data.trim().equalsIgnoreCase(previousMenucode))
				{
					if (multiLanguageFlag)
					{
						repeatFlag = true;
					}
					if (multigrpzFalg == true)
					{
						repeatFlag = true;
					}
					else if (multimembFalg == true)
					{
						System.out.println("multimembFalg == true");
						String multiGroupzWelNotes = co.getmultigrpzWelcomeNotes();
						System.out.println("multiGroupzWelNotes  ::: "+multiGroupzWelNotes);

						if (multiGroupzWelNotes != null && multiGroupzWelNotes.isEmpty() == false)
						{
							co.setmultiGrpzFlag(true);
							String displayList = co.getmultigrpzWelcomeNotes();
							System.out.println("displayList  ::: "+displayList);
							String selectList = co.getmultigrpzselectlist();
							System.out.println("selectList  ::: "+selectList);
							
							co.setcontextdisplayList(displayList);
							co.setcontextselectionList(selectList);
							co.setmultiMemberFlag(false);
							co.setmultimembWelcomeNotes(null);
							co.setmultiMembSelectlist(null);
							co.save();
							
							kkResponse = StaticUtils.processUrlOrTextMultiList(multiGroupzWelNotes, playspeed, timeout);
						}
						else
						{
							repeatFlag = true;
						}
					}
					else if (multigrpzFalg == false && multimembFalg == false && multiLanguageFlag == false)
					{
						
						System.out.println("multigrpzFalg == false");
						String multiMemberWelNotes = co.getmultimembWelcomeNotes();
						String multiGroupzWelNotes = co.getmultigrpzWelcomeNotes();

						if (multiMemberWelNotes != null && multiMemberWelNotes.isEmpty() == false)
						{
							co.setmultiMemberFlag(true);

							String displayList = co.getmultimembWelcomeNotes();
							String selectList = co.getmultiMembSelectlist();
							co.setcontextdisplayList(displayList);
							co.setcontextselectionList(selectList);
							co.save();
						
							kkResponse = StaticUtils.processUrlOrTextMultiList(displayList, playspeed, timeout);
						}
						else if (multiGroupzWelNotes != null && multiGroupzWelNotes.isEmpty() == false)
						{
							co.setmultiGrpzFlag(true);
						
							String displayList = co.getmultigrpzWelcomeNotes();
							String selectList = co.getmultigrpzselectlist();
							co.setcontextdisplayList(displayList);
							co.setcontextselectionList(selectList);
							co.save();

							kkResponse = StaticUtils.processUrlOrTextMultiList(displayList, playspeed, timeout);
						}
						else
						{
							repeatFlag = true;
						}
					}
				}
			}

			if (repeatFlag || data.trim().equals(repeatCode) || (containsKey == false && !(data.trim().equals(previousMenucode)
					|| data.trim().equals(hangupCode))))
			{
				System.out.println("repeatFlag data trim");
				if (multiLanguageFlag)
				{
					kkResponse = StaticUtils.playURL(languageWelcomeUrl, timeout);
				}
				else
				{
					kkResponse = StaticUtils.processUrlOrTextMultiList(welcomeNote, playspeed, timeout);
				}
				kkResponse.setSid(callSessionId);
				return kkResponse.getXML();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			loggerServ.info("Exception occured in process continous call in service request for number,IVRNUMBER : "
							+ formattednumber + ivrNumber + " and sessionId is :" + callSessionId);
			kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, co);
			kkResponse.setSid(callSessionId);
			return kkResponse.getXML();
		}
		kkResponse.setSid(callSessionId);
		return kkResponse.getXML();
	}
}
