package ivr.utils;

import ivr.tables.ContextMapping;
import ivr.tables.IvrGroupzMapping;
import ivr.tables.IvrGroupzBaseMapping;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.apache.log4j.Logger;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;

public class serviceUtils
{
	static Properties prop = new Properties();
	public static Logger loggerServ = Logger.getLogger("serviceLogger");

	static
	{
		try
		{
			InputStream in = StaticUtils.class.getResourceAsStream("/ivr.properties");
			prop.load(in);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static List<String> getCategoryList(String grpzid, String callerID, String ivrnumber)
	{
		List<String> categoryList = new ArrayList<String>();
		
		try
		{
			String servicetype = prop.getProperty("servicetype");
			String functiontype = prop.getProperty("categoryListFunctype");
			String urltoinvoke = prop.getProperty("serviceRequestUrl");
			String module = prop.getProperty("serviceReqModulecode");

			JSONObject dataJson = new JSONObject();
			JSONObject requestJson = new JSONObject();

			dataJson.put("servicetype", servicetype);
			dataJson.put("functiontype", functiontype);
			dataJson.put("groupzid", grpzid);
			dataJson.put("moduleid", module);
			requestJson.put("request", dataJson);

			XMLSerializer serializer = new XMLSerializer();

			JSON jsonadd = JSONSerializer.toJSON(requestJson);
			serializer.setRootName("xml");
			serializer.setTypeHintsEnabled(false);

			String xmlsmsString = serializer.write(jsonadd);

			String responsexmlString = StaticUtils.ConnectAndGetResponse(urltoinvoke, xmlsmsString);

			if (StaticUtils.isEmptyOrNull(responsexmlString))
			{
				loggerServ.info("categoryList xml response is empty for callerID and ivrnumber " + callerID + "  " + ivrnumber);
				categoryList = null;
			}
			else
			{
				boolean sucessFlag = StaticUtils.getStatusCode(responsexmlString);
				
				if (sucessFlag)
				{
					XMLSerializer xmlSerializer = new XMLSerializer();
					JSON json = xmlSerializer.read(responsexmlString);
					JSONObject jo = (JSONObject) JSONSerializer.toJSON(json);
					JSONObject jores = (JSONObject) jo.get("response");
					String statusCode = jores.getString("statuscode");
				
					if (statusCode.equals("0"))
					{
						Object obj = jores.get("servicerequestcategories");

						if (obj instanceof JSONArray)
						{

							JSONArray jsnlistarry = jores.getJSONArray("servicerequestcategories");

							for (int i = 0; i < jsnlistarry.size(); i++)
							{
								JSONObject jogrpz = jsnlistarry.getJSONObject(i);
								String category = jogrpz.getString("category");
								categoryList.add(category);
							}
						}
						else
						{
							JSONObject jogrpz = jores.getJSONObject("servicerequestcategories");
							JSONObject jogele = jogrpz.getJSONObject("element");
							String category = jogele.getString("category");
							categoryList.add(category);
						}
					}
				}
				else
				{
					loggerServ.info("categoryList  empty for groupzid, callerID and ivrnumber  " + grpzid + "  " + callerID + "  " + ivrnumber);
					categoryList = null;
				}
			}
		}
		catch (Exception e)
		{
			loggerServ.info("Exception occured while geting category list for groupzid, callerID and ivrnumber  "  + grpzid + "  " + callerID 
					+ "  " + ivrnumber);
			e.printStackTrace();
			categoryList = null;
			return categoryList;
		}
		return categoryList;
	}

	public static Response generateNewCallResponse(IvrGroupzMapping sm, ContextMapping cm, int playspeed, int timeout, IvrGroupzBaseMapping inm)
	{

		String multiGrpzwelcome = null;
		String multiMembwelcome = null;
	
		ArrayList<String> dataArraywelcomedisplay = new ArrayList<String>();
		
		Response kkResponse = new Response();

		boolean multiLangFlag = false;
		String starUrl = null;
		String starData = null;
		String audioUrl = sm.getselectionlistUrl();
		System.out.println("audioUrl  +++++++++++++" + audioUrl);
		String selectionList = sm.getselectionlist();
		cm.setcontextselectionList(selectionList);
		cm.save();

		if (inm != null)
		{
//			String selectionhangupurl = inm.getpreviousMenuSelectUrl();
//			ArrayList<String> selectiongrpzhangupurl = new ArrayList<String>();
//			selectiongrpzhangupurl = StaticUtils.createJSONdataArray(selectionhangupurl);
//			starUrl = selectiongrpzhangupurl.get(0);
			starUrl = inm.getpreviousMenuSelectUrl();
//			System.out.println("previousMenuSelectUrl +++++   " + starUrl);
			starData = inm.getpreviousMenuSelectNotes();
			multiLangFlag = inm.getmultiLanguageFlag();
		}

		if (multiLangFlag && StaticUtils.isEmptyOrNull(audioUrl) == false)
		{
			audioUrl = StaticUtils.getSelectedLangUrl(audioUrl, cm, multiLangFlag);
		}

		if (multiLangFlag && StaticUtils.isEmptyOrNull(starUrl) == false)
		{
			starUrl = StaticUtils.getSelectedLangUrl(starUrl, cm, multiLangFlag);
		}

		if (cm != null)
		{
			multiGrpzwelcome = cm.getmultigrpzWelcomeNotes();
			multiMembwelcome = cm.getmultimembWelcomeNotes();
		}

		if (audioUrl == null || audioUrl.trim().isEmpty() == true)
		{
			String wnotes = sm.getwelcomeNotes();
			System.out.println("wnotes  " + wnotes );

			if (wnotes != null && wnotes.trim().isEmpty() == false && selectionList != null && selectionList.trim().isEmpty() == false)
			{
				String endNotes = inm.getselectionEndNotes();
				System.out.println("endNotes " +endNotes);
				dataArraywelcomedisplay = StaticUtils.createivrselectionMenuList(wnotes, selectionList,endNotes);
				

				if ((multiGrpzwelcome != null && multiGrpzwelcome.isEmpty() == false) 
						|| (multiMembwelcome != null && multiMembwelcome.isEmpty() == false))
				{
					if (inm != null)
					{
						if (starUrl == null || starUrl.isEmpty() == true)
						{
							if (starData != null && starData.isEmpty() == false)
							{
								ArrayList<String> datalistObj = StaticUtils.createJSONdataArray(starData);
								dataArraywelcomedisplay.addAll(datalistObj);
							}
						}
						else
						{
							dataArraywelcomedisplay.add(starUrl);
						}
					}

					if (inm == null || starData == null || starData.isEmpty() == true)
					{
						starData = prop.getProperty("ivrstarselectionNotes");
					
						ArrayList<String> datalistObj = StaticUtils.createJSONdataArray(starData);
						dataArraywelcomedisplay.addAll(datalistObj);
					}
				}
			}
		}
		else
		{
			if ((multiGrpzwelcome != null && multiGrpzwelcome.isEmpty() == false)
					|| (multiMembwelcome != null && multiMembwelcome.isEmpty() == false))
			{
				if (inm != null)
				{
					if (starUrl == null || starUrl.isEmpty() == true)
					{
						if (starData != null && starData.isEmpty() == false)
						{
							dataArraywelcomedisplay.add(audioUrl.trim());
						
							ArrayList<String> datalistObj = StaticUtils.createJSONdataArray(starData);
							dataArraywelcomedisplay.addAll(datalistObj);
						}
					}
					else
					{
						dataArraywelcomedisplay.add(audioUrl.trim());
					}
				}
				if (inm == null || ((starData == null || starData.isEmpty() == true) && (starUrl == null || starUrl .isEmpty())))
				{
					starData = prop.getProperty("ivrstarselectionNotes");
					dataArraywelcomedisplay.add(audioUrl.trim());

					ArrayList<String> datalistObj = StaticUtils.createJSONdataArray(starData);
					dataArraywelcomedisplay.addAll(datalistObj);
				}
			}
			else
			{
				dataArraywelcomedisplay.add(audioUrl.trim());
				System.out.println("dataArraywelcomedisplay   url trim ::: " + dataArraywelcomedisplay);
			}
		}

		String displayListString = StaticUtils.createJSONString(dataArraywelcomedisplay);
		cm.setcontextselectionList(selectionList);
		cm.setcontextdisplayList(displayListString);
		System.out.println(" displayListString +++++++     " +displayListString);
		cm.save();

		kkResponse = StaticUtils.processUrlOrTextMultiList(displayListString, playspeed, timeout);
		return kkResponse;
	}

	public static Response generateGlobalNewCallResponse(ContextMapping cm)
	{
		String categoryListJSN = new String();
		Response kkResponse = new Response();
		CollectDtmf cd = new CollectDtmf();
		String setTerminator = prop.getProperty("settermChar");
		cd.setTermChar(setTerminator);
		String maxdigStr = prop.getProperty("maxDigit");
		int maxdig = Integer.parseInt(maxdigStr);
		cd.setMaxDigits(maxdig);
		String dataJSNString = null;
		String playspeed = prop.getProperty("playspeed");
		String timeout = prop.getProperty("settimeout");
		int speed = Integer.parseInt(playspeed);
		int time = Integer.parseInt(timeout);

		cd.setTimeOut(time);

		String groupzId = cm.getgroupzId();
		String formattednumber = cm.getCallerId();
		String ivrNumber = cm.getIvrNumber();
		String callSessionId = cm.getCallSessionId();

		List<String> categoryList = serviceUtils.getCategoryList(groupzId, formattednumber, ivrNumber);
		
		//creating xml and connectingAndGettingResponse and also getting status using getCategoryList

		if (categoryList == null || categoryList.isEmpty())
		{
			loggerServ.info("The category list for global number is empty : session ID &  number" + callSessionId + " : " + formattednumber);
			kkResponse = StaticUtils.senderrorResp(callSessionId, ivrNumber, cm);
		}

		String wn = serviceUtils.createWelcomeNotesForContext(categoryList);
		// To create category list for context table using createWelcomeNotesForContext
		
		String selectionList = serviceUtils.createCategorySelectionList(categoryList);
		//To create that no grpz for global ivr selected using createCategorySelectionList()

		cm.setglobalcategorywelcomeNotes(wn);
		cm.setcontextdisplayList(wn);
		cm.setcontextselectionList(selectionList);
		cm.save();

		String multiGrpzwelcome = null;
		String multiMembwelcome = null;

		if (cm != null)
		{
			multiGrpzwelcome = cm.getmultigrpzWelcomeNotes();
			multiMembwelcome = cm.getmultimembWelcomeNotes();
		}

		if (wn != null && wn.trim().isEmpty() == false)
		{
			categoryListJSN = cm.getglobalcategorywelcomeNotes();
			
			ArrayList<String> dataarrayList = new ArrayList<String>();

			dataarrayList = StaticUtils.createJSONdataArray(categoryListJSN);

			if ((multiGrpzwelcome != null && multiGrpzwelcome.isEmpty() == false) 
					|| (multiMembwelcome != null && multiMembwelcome.isEmpty() == false))
			{
				String starNotes = prop.getProperty("ivrstarselectionNotes");
				
				ArrayList<String> dataarrayStarList = new ArrayList<String>();
				
				dataarrayStarList = StaticUtils.createJSONdataArray(starNotes);
				dataarrayList.addAll(dataarrayStarList);
			}
			dataJSNString = StaticUtils.createJSONString(dataarrayList);
			kkResponse = StaticUtils.processUrlOrTextMultiList(dataJSNString, speed, time);
			//processUrlOrTextMultiList used to play audio or text of the above category list 
			
		}
		kkResponse.addCollectDtmf(cd);
		return kkResponse;
	}

	

	public static String createWelcomeNotesForContext(List<String> categoryList)
	{
		String welcomeselectionString = null;
		
		ArrayList<String> categoryDataArray = new ArrayList<String>();

		String defualtWelcomeMsg = prop.getProperty("ivrcategorySelwelcomNotes");

		ArrayList<String> welcomeDataArray = new ArrayList<String>();

		welcomeDataArray = StaticUtils.createJSONdataArray(defualtWelcomeMsg);
		String defualtwelcomeHangup = prop.getProperty("ivrselectionHangupNotes");

		ArrayList<String> endDataArray = new ArrayList<String>();

		endDataArray = StaticUtils.createJSONdataArray(defualtwelcomeHangup);
		Iterator<String> catgiter = categoryList.iterator();
		int i = 1;
		
		while (catgiter.hasNext())
		{
			String categry = catgiter.next();
			String pressStr = "Press " + i + " for ";
			categoryDataArray.add(pressStr);
			categoryDataArray.add(categry);
			i++;
		}
		welcomeDataArray.addAll(categoryDataArray);
		welcomeDataArray.addAll(endDataArray);
		welcomeselectionString = StaticUtils.createJSONString(welcomeDataArray);
		return welcomeselectionString;
	}

	public static String createCategorySelectionList(List<String> categoryList)
	{
		String selectionString = null;

		JSONObject sellistObj = new JSONObject();
		JSONObject listObj = new JSONObject();
		Iterator<String> catgiter = categoryList.iterator();
		int i = 1;
		
		while (catgiter.hasNext())
		{
			String categry = catgiter.next();
			sellistObj.put(i + "", categry);
			i++;
		}
		listObj.put("selectionList", sellistObj);
		selectionString = listObj.toString();
		return selectionString;
	}

	public static Response sendNoGlobalGrupzResp()
	{
		Response kkResponse = new Response();
		int playspeed = 1;
		String dedicatedText = null;
		String dedicatedUrl = null;
		String defaultNotes = null;
		String defaulturl = null;

		String playspeedstr = prop.getProperty("playspeed");
		playspeed = Integer.parseInt(playspeedstr);

		defaultNotes = prop.getProperty("ivrnogrpzForGlobalSelect");
		defaulturl = prop.getProperty("ivrnogrpzForGlobalSelecturl");

		kkResponse = StaticUtils.processUrlOrTextMessage(dedicatedUrl, dedicatedText, defaultNotes, defaulturl, playspeed, false, null);
		kkResponse.addHangup();
		return kkResponse;
	}



	public static Response hangUpProcess(IvrGroupzBaseMapping ivrnummap, boolean globalFlag, int playspeed, ContextMapping cm)
	{
		Response kkResponse = new Response();

		String dedicatedaudioHangupUrl = null;
		String dedicatedaudioHangupText = null;
		boolean multiLanguageFlag = false;

		if (!globalFlag)
		{
			if (ivrnummap != null)
			{
				dedicatedaudioHangupUrl = ivrnummap.getaudiogeneralHangupUrl();
				dedicatedaudioHangupText = ivrnummap.getgeneralHangupNotes();
				multiLanguageFlag = ivrnummap.getmultiLanguageFlag();
			}
		}

		String defualtaudioHangupUrl = prop.getProperty("ivrGeneralHangupUrl");
		String defualtaudioHangupText = prop.getProperty("ivrGeneralHangupNotes");
		kkResponse = StaticUtils.processUrlOrTextMessage(dedicatedaudioHangupUrl, dedicatedaudioHangupText, defualtaudioHangupText, 
				defualtaudioHangupUrl, playspeed, multiLanguageFlag, cm);
		kkResponse.addHangup();
		return kkResponse;
	}

	public static boolean placeGroupzIssueWithSourceType(String grpzId, String memberid, String category, String callerID, String callSessionID,String groupzcode)
	{

		boolean statusFlag = false;
		String responsexmlString = null;
		
		try
		{
			String servicetype = prop.getProperty("servicetype");
			String urltoinvoke = prop.getProperty("registerRequestUrl");
			String moduleCOde = prop.getProperty("serviceReqModulecode");
			String functionType = prop.getProperty("serviceIssuePlaceFunType");
			String title = prop.getProperty("issueplacingTitle");
			String description = prop.getProperty("issuePlacedescription");
			String preferredTime = prop.getProperty("issuePlacepreferredTime");
			String sourceType = prop.getProperty("issuePlacesourceType");
			String issuetype = prop.getProperty("issuetype");
			String scopetype = prop.getProperty("issuescopetype");
			
			 JSONObject dataJson = new JSONObject();
             JSONObject selectedData = new JSONObject();
             JSONObject requestJson = new JSONObject();
             JSONObject requestfinalJson = new JSONObject();

             selectedData.put("type", issuetype);
             selectedData.put("category", category);
             selectedData.put("title", title);
             
             dataJson.put("memberid", memberid);
             dataJson.put("groupzcode", groupzcode);
             dataJson.put("servicetype", servicetype);
             dataJson.put("functiontype", functionType);
             dataJson.put("data", selectedData);
             requestJson.put("request", dataJson);
             requestfinalJson.put("json", requestJson);
/*
			JSONObject dataJson = new JSONObject();
			JSONObject requestJson = new JSONObject();

			dataJson.put("servicetype", servicetype);
			dataJson.put("functiontype", functionType);
			dataJson.put("moduleid", moduleCOde);
			dataJson.put("groupzid", grpzId);
			dataJson.put("memberid", memberid);
			dataJson.put("category", category);
			dataJson.put("title", title);
			dataJson.put("description", description);
			dataJson.put("preferredTime", preferredTime);
			dataJson.put("sourceType", sourceType);
			dataJson.put("issuetype", issuetype);
			dataJson.put("scopetype", scopetype);
			requestJson.put("request", dataJson);*/

			//XMLSerializer serializer = new XMLSerializer();

			//JSON jsonadd = JSONSerializer.toJSON(requestJson);
			//serializer.setRootName("xml");
			//serializer.setTypeHintsEnabled(false);

			//String xmlsmsString = serializer.write(jsonadd);
			
			String xmlsmsString=requestfinalJson.toString();
			
			System.out.println("ISSUE REQUEST "+xmlsmsString);

			responsexmlString = StaticUtils.ConnectAndGetResponse(urltoinvoke, xmlsmsString);

			if (StaticUtils.isEmptyOrNull(responsexmlString) == false)
				
				
			{
				JSONObject joobj = (JSONObject)JSONSerializer.toJSON(responsexmlString);
						
						
						
				
				JSONObject jores =  joobj.getJSONObject("json");
				JSONObject joresponse=  jores.getJSONObject("response");
				String statusCode = joresponse.getString("statuscode");
				String statusMessage=joresponse.getString("statusmessage");

				if(statusCode.equals("0")){
				statusFlag = true; //StaticUtils.getStatusCode(responsexmlString);
				}
			}
		}
		catch (Exception e)
		{
			loggerServ.info("Exception occured while sending request for placing the issue through IVR callerID and sessionID are : ."
							+ callerID + " , " + callSessionID, e);
			e.printStackTrace();
			return statusFlag;
		}
		return statusFlag;
	}
}
