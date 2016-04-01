package ivr.utils;


import ivr.tables.ContextMapping;
import ivr.tables.PhoneCodesList;
import ivr.tables.IvrGroupzBaseMapping;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.apache.log4j.Logger;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;

public class StaticUtils
{
	private static Logger logger = Logger.getLogger("serviceLogger");
	static Properties prop = new Properties();

	static
	{
		try
		{
			InputStream in = StaticUtils.class.getResourceAsStream("/ivr.properties");
			prop.load(in);
		}
		catch (Exception e)
		{
			logger.error("Exception occured in load property file.", e);
			e.printStackTrace();
		}
	}

	private static Map<String, List<String>> telephonecodes;

	public static void initializeTelephoneCodes()
	{
		telephonecodes = new HashMap<String, List<String>>();
		String key = null;
		List<String> areaCodeList = new ArrayList<String>();
		List<PhoneCodesList> phcodelist = (List<PhoneCodesList>) PhoneCodesList.getListofPhoneCodes();
		
		if (phcodelist != null && phcodelist.size() != 0)
		{
			System.out.println(phcodelist);

			for (PhoneCodesList pc : phcodelist)
			{
				key = pc.getcountrycodes();
				String areaoceString = pc.getareacodes();
				String[] areacdList = areaoceString.split(",");

				areaCodeList = Arrays.asList(areacdList);
				telephonecodes.put(key, areaCodeList);
				System.out.println(telephonecodes);
			}

		}
	}

	// This would remove the leading zeros and + in the mobile number, if any.
	public static boolean isEmptyOrNull(String str)
	{
		if (str == null || str.trim().isEmpty() == true)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static String TrimPhoneNumber(String mobileNo)
	{
		String trimmedNumber = new String();
		
		if (mobileNo == null || mobileNo.isEmpty())
		{
			return trimmedNumber;
		}
		int digitOffset = 0;
		String mobNo = mobileNo.trim();
		
		for (int ind = 0; ind < mobNo.length(); ind++)
		{
			if (mobNo.charAt(ind) != '+' && mobNo.charAt(ind) != '0')
			{
				break;
			}
			digitOffset++;
		}
		trimmedNumber = mobNo.substring(digitOffset);
		return trimmedNumber;
	}
	
	

	public static String FormatMobileNumber(String mobileNo, String ivrNumber)
	{
		String formattedMobile = new String();
		
		if (mobileNo == null || mobileNo.isEmpty() == true)
		{
			return formattedMobile;
		}

		String CountryCode = prop.getProperty("defaultcountryCode");

		if (telephonecodes == null) {
			initializeTelephoneCodes();
		}

		String formattedMobNo = null;
		Set<String> ccList = telephonecodes.keySet();
		String mobNo = mobileNo;
		int numlen = mobileNo.length();

		if (numlen <= 10)
		{
			for (String cc : ccList)
			{
				if (ivrNumber.startsWith(cc))
				{
					CountryCode = cc;
				}
			}
		}
		else
		{
			for (String cc : ccList)
			{
				if (mobileNo.startsWith(cc))
				{
					mobNo = mobileNo.substring(cc.length(), mobileNo.length());
					CountryCode = cc;
				}
			}
		}
		formattedMobNo = CountryCode + "." + mobNo;
		return formattedMobNo;
	}

	public static String FormatLandlineNumber(String landlineNo, String ivrnumber)
	{
		String formattedLLNo = new String();
		if (telephonecodes == null)
		{
			initializeTelephoneCodes();
		}
		String landlineNum = landlineNo;
		String tempIvrNum = null;
		String areaCode = null;
		String countryCode = null;

		Set<String> ccList = telephonecodes.keySet();

		if (landlineNo == null || landlineNo.isEmpty() == true)
		{
			return formattedLLNo;
		}

		if (landlineNo.length() > 8)
		{
			for (String cc : ccList)
			{
				if (landlineNo.startsWith(cc))
				{
					countryCode = cc;
					landlineNum = landlineNo.substring(countryCode.length(),
					landlineNo.length());

					List<String> areaCodeList = telephonecodes.get(countryCode);

					for (String code : areaCodeList)
					{
						if (landlineNum.startsWith(code))
						{
							landlineNum = landlineNum.substring(code.length(), landlineNum.length());
							areaCode = code;
							break;
						}
					}
				}
			}

			if (StaticUtils.isEmptyOrNull(countryCode))
			{

				for (String cc : ccList)
				{
					if (ivrnumber.startsWith(cc))
					{
						countryCode = cc;
						break;
					}
				}

				List<String> areaCodeList = telephonecodes.get(countryCode);

				for (String code : areaCodeList)
				{
					if (landlineNum.startsWith(code))
					{
						landlineNum = landlineNum.substring(code.length(), landlineNum.length());
						areaCode = code;
						break;
					}
				}

				if (StaticUtils.isEmptyOrNull(areaCode))
				{
					tempIvrNum = ivrnumber.substring(countryCode.length(),	ivrnumber.length());

					List<String> areaCodeListivr = telephonecodes.get(countryCode);

					for (String code : areaCodeListivr)
					{
						if (tempIvrNum.startsWith(code))
						{
							areaCode = code;
						}
					}
				}
			}
		}

		else if (landlineNo.length() <= 8)
		{
			// Finding countryCode and area code of ivrNumber

			for (String cc : ccList)
			{
				if (ivrnumber.startsWith(cc))
				{
					countryCode = cc;
					break;
				}
			}

			if (StaticUtils.isEmptyOrNull(countryCode)==false)
			{	
				tempIvrNum = ivrnumber.substring(countryCode.length(),	ivrnumber.length());
				
				List<String> areaCodeListivr = telephonecodes.get(countryCode);

				for (String code : areaCodeListivr)
				{
					if (tempIvrNum.startsWith(code))
					{
						areaCode = code;
					}
				}
			}

		}

		if (StaticUtils.isEmptyOrNull(countryCode))
		{
			countryCode = prop.getProperty("defaultcountryCode");
		}

		if (StaticUtils.isEmptyOrNull(areaCode))
		{
			areaCode = prop.getProperty("defaultareaCode");
		}

		formattedLLNo = countryCode + "." + areaCode + "." + landlineNum;
		System.out.println("The formatted landline is : " + formattedLLNo);
		return formattedLLNo;
	}

	public static String validateMobile(String callerId, String ivrNumber)throws Exception
	{

		String trimmedMob = StaticUtils.TrimPhoneNumber(callerId);
		String formattedMobNo = StaticUtils.FormatMobileNumber(trimmedMob, ivrNumber);
		return formattedMobNo;

	}

	public static String validateLandline(String callerId, String ivrnumber)throws Exception
	{
		String formattedLLNo = null;
		String trimmedLLNo = StaticUtils.TrimPhoneNumber(callerId);

		if (StaticUtils.isEmptyOrNull(trimmedLLNo) == true)
		{
			return null;
		}
		formattedLLNo = StaticUtils.FormatLandlineNumber(trimmedLLNo, ivrnumber);
		System.out.println("landline **** " + formattedLLNo);
		
		if (StaticUtils.isEmptyOrNull(formattedLLNo) == true)
		{
			return null;
		}
		return formattedLLNo;
	}

	public static Response processUrlOrTextMultiList(String displayList, int playspeed, int timeout)
	{
		Response kkResponse = new Response();
		CollectDtmf cd = new CollectDtmf();
		String setTerminator = prop.getProperty("settermChar");
		cd.setTermChar(setTerminator);
		cd.setTimeOut(timeout);
		String maxdigStr = prop.getProperty("maxDigit");
		int maxdig = Integer.parseInt(maxdigStr);
		cd.setMaxDigits(maxdig);
		if (displayList == null || displayList.isEmpty() == true) {

			logger.error("The display list is null in processUrlOrTextMultiList method");
			return kkResponse;
		}


		JSONObject dataObj = (JSONObject) JSONSerializer.toJSON(displayList);
		JSONArray jsndatalistObj = dataObj.getJSONArray("dataList");
		int datasize = jsndatalistObj.size();

		for (int i = 0; i < datasize; i++)
		{

			String playStr = jsndatalistObj.getString(i);

			if (playStr.contains("http:") || playStr.contains("https:"))
			{
				cd.addPlayAudio(playStr);
			}
			else
			{
				cd.addPlayText(playStr, playspeed);
			}
		}

		kkResponse.addCollectDtmf(cd);
		return kkResponse;
	}
	
	public static Response sendNotRegGrupzResp(IvrGroupzBaseMapping ivrnummap,	ContextMapping cm)
	{

		Response kkResponse = new Response();
		int playspeed = 1;
		String dedicatedText = null;
		String dedicatedUrl = null;
		String defaultNotes = null;
		String defaulturl = null;
		boolean multiLangFlag = false;

		if (ivrnummap != null)
		{

			dedicatedText = ivrnummap.getnotRegGroupzNotes();
			dedicatedUrl = ivrnummap.getnotRegGroupzUrl();
			playspeed = ivrnummap.getplayspeed();
			multiLangFlag = ivrnummap.getmultiLanguageFlag();
		}
		else
		{
			String playspeedstr = prop.getProperty("playspeed");
			playspeed = Integer.parseInt(playspeedstr);
		}

		defaultNotes = prop.getProperty("ivrgroupzNotregNotes");
		defaulturl = prop.getProperty("ivrgroupzNotregUrl");

		kkResponse = StaticUtils.processUrlOrTextMessage(dedicatedUrl, dedicatedText, defaultNotes, defaulturl, playspeed, multiLangFlag, cm);
		kkResponse.addHangup();
		return kkResponse;
	}
	
	public static Response playUrlOrTextMessage(String text, String audio, int playspeed, ContextMapping cm, boolean multiLangFlag)
	{
		Response kkResponse = new Response();

		if (multiLangFlag && StaticUtils.isEmptyOrNull(audio) == false)
		{
			audio = StaticUtils.getSelectedLangUrl(audio, cm, multiLangFlag);
		}

		if (audio != null && audio.isEmpty() == false)
		{
			kkResponse.addPlayAudio(audio);
			System.out.println("if audiourl is  true");
		}
		else
		{
			if (text != null && text.isEmpty() == false)
			{
				JSONObject dataObj = (JSONObject) JSONSerializer.toJSON(text);
				JSONArray jsndatalistObj = dataObj.getJSONArray("dataList");
				int datasize = jsndatalistObj.size();

				for (int i = 0; i < datasize; i++)
				{
					String playStr = jsndatalistObj.getString(i);
					kkResponse.addPlayText(playStr, playspeed);
				}
			}
		}
		return kkResponse;
	}


	public static Response processUrlOrTextMessage(String dedicatedUrl, String dedicatedText, String defualttext, String defualtUrl, int playspeed,
			boolean multiLangFlag, ContextMapping cm)
	{
		Response kkResponse = new Response();

		if (multiLangFlag && StaticUtils.isEmptyOrNull(dedicatedUrl) == false)
		{
			dedicatedUrl = StaticUtils.getSelectedLangUrl(dedicatedUrl, cm, multiLangFlag);
		}

		if (dedicatedUrl != null && dedicatedUrl.isEmpty() == false)
		{
			kkResponse.addPlayAudio(dedicatedUrl);
		}
		else
		{
			if (dedicatedText != null && dedicatedText.isEmpty() == false)
			{
				JSONObject dataObj = (JSONObject) JSONSerializer.toJSON(dedicatedText);
				JSONArray jsndatalistObj = dataObj.getJSONArray("dataList");
				int datasize = jsndatalistObj.size();

				for (int i = 0; i < datasize; i++)
				{
					String playStr = jsndatalistObj.getString(i);
					kkResponse.addPlayText(playStr, playspeed);
				}
			}
			else
			{
				if (defualtUrl != null && defualtUrl.isEmpty() == false)
				{
					kkResponse.addPlayAudio(defualtUrl);
				}
				else
				{
					JSONObject dataObj = (JSONObject) JSONSerializer.toJSON(defualttext);
					JSONArray jsndatalistObj = dataObj.getJSONArray("dataList");
					int datasize = jsndatalistObj.size();

					for (int i = 0; i < datasize; i++)
					{
						String playStr = jsndatalistObj.getString(i);
						kkResponse.addPlayText(playStr, playspeed);
					}
				}
			}
		}
		return kkResponse;
	}

	public static Response senderrorResp(String callSessionId, String ivrNumber, ContextMapping cm)
	{
		Response kkResponse = new Response();
		IvrGroupzBaseMapping ivrnummap = null;
		int playspeed = 1;
		String dedicatedText = null;
		String dedicatedUrl = null;
		String defaultNotes = null;
		String defaulturl = null;
		boolean multiLangFlag = false;

		if (ivrNumber != null && ivrNumber.isEmpty() == false)
		{
			ivrnummap = IvrGroupzBaseMapping.getSingleivrnumberMap(ivrNumber);
			
			if (ivrnummap != null)
			{
				dedicatedText = ivrnummap.geterrorNotes();
				dedicatedUrl = ivrnummap.getaudioerrorUrl();
				playspeed = ivrnummap.getplayspeed();
				multiLangFlag = ivrnummap.getmultiLanguageFlag();
			}
			else
			{
				String playspeedstr = prop.getProperty("playspeed");
				playspeed = Integer.parseInt(playspeedstr);
			}
		}

		defaultNotes = prop.getProperty("ivrerrorNotes");
		defaulturl = prop.getProperty("ivrerrorUrl");

		kkResponse = StaticUtils.processUrlOrTextMessage(dedicatedUrl, dedicatedText, defaultNotes, defaulturl, playspeed, 
				multiLangFlag, cm);

		kkResponse.addHangup();
		return kkResponse;
	}

	public static Response playURL(String urlStr, int timeout)
	{
		Response kkResponse = new Response();
		CollectDtmf cd = new CollectDtmf();
		String setTerminator = prop.getProperty("settermChar");
		cd.setTermChar(setTerminator);
		cd.setTimeOut(timeout);
		String maxdigStr = prop.getProperty("maxDigit");
		int maxdig = Integer.parseInt(maxdigStr);
		cd.setMaxDigits(maxdig);

		JSONObject langObj = (JSONObject) JSONSerializer.toJSON(urlStr);
		JSONArray langListObj = langObj.getJSONArray("urlList");
		int len = langListObj.size();

		for (int i = 0; i < len; i++)
		{
			String url = langListObj.getString(i);
			cd.addPlayAudio(url.trim());
		}
		kkResponse.addCollectDtmf(cd);
		return kkResponse;
	}

	public static ArrayList<String> numberUrlsDisplayString(String numberUrls, ArrayList<String> namelist, boolean regionalLanguage)
	{
		int j = 1;
		ArrayList<String> dataList = new ArrayList<String>();

		if (numberUrls != null && numberUrls.isEmpty() == false)
		{
			JSONObject dataObj = (JSONObject) JSONSerializer.toJSON(numberUrls);

			if (regionalLanguage)
			{

				for (int i = 0; i < namelist.size(); i++)
				{

					String pressStr = dataObj.getString(j + "");
					String groupzName = namelist.get(i);
					dataList.add(groupzName);
					dataList.add(pressStr);
					j++;
				}
			}
			else
			{
				for (int i = 0; i < namelist.size(); i++)
				{
					String pressStr = dataObj.getString(j + "");
					String groupzName = namelist.get(i);
					dataList.add(pressStr);
					dataList.add(groupzName);
					j++;
				}
			}
		}
		else
		{
			for (int i = 0; i < namelist.size(); i++)
			{
				String pressStr = "Press " + j + " for ";
				String groupzName = namelist.get(i);
				dataList.add(pressStr);
				dataList.add(groupzName);
				j++;
			}
		}
		return dataList;
	}

	public static Response createMultiGroupzData(IvrGroupzBaseMapping ivrnummap, HashMap<String, String> groupzcodeNamMap, 
			List<String> ivrgrpzcodeList, HashMap<String, String> groupzinfoMap, int playspeed, int timeout, ContextMapping cm)
	{
		boolean regionalLanguage = cm.getregionalLanguageFlag();
		Response kkResponse = new Response();
		boolean multilangFlag = false;
		String multiGrpzWelcomeUrl = null;
		//String multiGrpzWelcomeNotes = null;
		String numberListUrlsStr = null;
		String selectionmultigrpzhangupurl = null;
//		String selectionmultigrpzhangupnotes = null;
		String numberListUrls = null;
		JSONObject selectedUrllist = null;
		
		if (ivrnummap != null)
		{

			multilangFlag = ivrnummap.getmultiLanguageFlag();
			multiGrpzWelcomeUrl = ivrnummap.getaudioGrpzWelcomeUrl();
			//multiGrpzWelcomeNotes = ivrnummap.getgrpzWelcomeNotes();
			String selectionhangupurl = ivrnummap.getselectionEndUrl();
						
			ArrayList<String> selectiongrpzhangupurl = new ArrayList<String>();
			selectiongrpzhangupurl = StaticUtils.createJSONdataArray(selectionhangupurl);
			selectionmultigrpzhangupurl = selectiongrpzhangupurl.get(0);
			System.out.println("selectionmultigrpzhangupurl     ***  " + selectionmultigrpzhangupurl);
			
			numberListUrls = ivrnummap.getnumbersUrlList();
			JSONObject dataObj = (JSONObject) JSONSerializer.toJSON(numberListUrls);
			JSONObject numberurlsListObj = dataObj.getJSONObject("urlList");
			System.out.println("check : " + numberurlsListObj);

			if (multilangFlag)
			{

				if (StaticUtils.isEmptyOrNull(multiGrpzWelcomeUrl) == false)
				{
					multiGrpzWelcomeUrl = StaticUtils.getSelectedLangUrl(multiGrpzWelcomeUrl, cm, multilangFlag);
				}
//				if (StaticUtils.isEmptyOrNull(multiGrpzWelcomeNotes) == false)
//				{
//					multiGrpzWelcomeNotes = StaticUtils.getSelectedLangUrl(multiGrpzWelcomeNotes, cm, multilangFlag);
//				}

				if (StaticUtils.isEmptyOrNull(selectionmultigrpzhangupurl) == false)
				{
					selectionmultigrpzhangupurl = StaticUtils.getSelectedLangUrl(selectionmultigrpzhangupurl, cm, multilangFlag);
				}
//				if (StaticUtils.isEmptyOrNull(selectionmultigrpzhangupnotes) == false)
//				{
//					selectionmultigrpzhangupnotes = StaticUtils.getSelectedLangUrl(selectionmultigrpzhangupnotes, cm, multilangFlag);
//				}

				if (StaticUtils.isEmptyOrNull(numberListUrls) == false)
				{
					String languageSelected = null;
				
					if (cm != null)
					{
						languageSelected = cm.getlanguageSelected();
					}

					selectedUrllist = numberurlsListObj.getJSONObject(languageSelected);
				}
			}

			Iterator<String> grpzItr = ivrgrpzcodeList.iterator();
			int i = 1;

			ArrayList<String> grpzNameListinfo = new ArrayList<String>();

			String groupzNameDisplay = null;
			String selectionListmsg = "";

			ArrayList<String> displayGroupzList = new ArrayList<String>();

			JSONObject numlistObj = new JSONObject();
			JSONObject listObj = new JSONObject();

			while (grpzItr.hasNext())
			{
				String grpzcode = grpzItr.next();
				groupzNameDisplay = groupzcodeNamMap.get(grpzcode);

				if (groupzinfoMap != null)
				{
					String groupzID = groupzinfoMap.get(grpzcode);
					JSONObject setObj = new JSONObject();
					setObj.put("grpzcode", grpzcode);
					setObj.put("groupzID", groupzID);
					setObj.put("groupzName", groupzNameDisplay);
					numlistObj.put(i + "", setObj);
					listObj.put("selectionList", numlistObj);
				}
				grpzNameListinfo.add(groupzNameDisplay);
				i++;
			}
			selectionListmsg = listObj.toString();

			ArrayList<String> grpzNameListinfonew = new ArrayList<String>();

			if (multilangFlag)
			{

				for (String multilanggrpznameurl : grpzNameListinfo)
				{
					String selectedgrpznameurl = StaticUtils.getSelectedLangUrl(multilanggrpznameurl, cm, multilangFlag);
					grpzNameListinfonew.add(selectedgrpznameurl);
				}
				numberListUrlsStr = selectedUrllist.toString();
				displayGroupzList = StaticUtils.numberUrlsDisplayString(numberListUrlsStr, grpzNameListinfonew, regionalLanguage);
			}
			else
			{
				numberListUrlsStr = numberurlsListObj.toString();	
				displayGroupzList = StaticUtils.numberUrlsDisplayString(numberListUrlsStr, grpzNameListinfo, regionalLanguage);
			}

			ArrayList<String> dataArraywelcomedisplay = new ArrayList<String>();
			ArrayList<String> dataArrayhangUP = new ArrayList<String>();

			String finaldisplayListUrlText = null;
			String finalSelctEndUrlNotes = null;
			finaldisplayListUrlText = multiGrpzWelcomeUrl;
			dataArraywelcomedisplay.add(finaldisplayListUrlText);
			finalSelctEndUrlNotes = selectionmultigrpzhangupurl;
			System.out.println("finalSelctEndUrlNotes ++++++" +finalSelctEndUrlNotes);
			dataArrayhangUP.add(finalSelctEndUrlNotes);
						
//			String finaldisplayListNotesText = null;
//			String finalSelctEndNotes = null;
//			finaldisplayListNotesText = multiGrpzWelcomeNotes;
//			dataArraywelcomedisplay.add(finaldisplayListNotesText);
//			finalSelctEndNotes = selectionmultigrpzhangupnotes;
//			dataArrayhangUP.add(finalSelctEndNotes);
			
			if (ivrnummap != null)
			{
				if (finaldisplayListUrlText == null	|| finaldisplayListUrlText.isEmpty() == true)
				{
					finaldisplayListUrlText = ivrnummap.getgrpzWelcomeNotes();
					dataArraywelcomedisplay = StaticUtils.createJSONdataArray(finaldisplayListUrlText);
				}
//				if (finaldisplayListNotesText == null	|| finaldisplayListNotesText.isEmpty() == true)
//				{
//					finaldisplayListNotesText = ivrnummap.getgrpzWelcomeNotes();
//					dataArraywelcomedisplay = StaticUtils.createJSONdataArray(finaldisplayListNotesText);
//				}

				if (finalSelctEndUrlNotes == null || finalSelctEndUrlNotes.isEmpty() == true)
				{
					finalSelctEndUrlNotes = ivrnummap.getselectionEndNotes();
					dataArrayhangUP = StaticUtils.createJSONdataArray(finalSelctEndUrlNotes);
				}
//				if (finalSelctEndNotes == null || finalSelctEndNotes.isEmpty() == true)
//				{
//					finalSelctEndNotes = ivrnummap.getselectionEndNotes();
//					dataArrayhangUP = StaticUtils.createJSONdataArray(finalSelctEndNotes);
//				}
			}

			if (finaldisplayListUrlText == null || finaldisplayListUrlText.isEmpty() == true)
			{
				String defualtgrpzSelect = prop.getProperty("ivrMultigrpzWelcomeNotes");
				finaldisplayListUrlText = defualtgrpzSelect;
				dataArraywelcomedisplay = StaticUtils.createJSONdataArray(finaldisplayListUrlText);
			}
//			if (finaldisplayListNotesText == null || finaldisplayListNotesText.isEmpty() == true)
//			{
//				String defualtgrpzSelect = prop.getProperty("ivrMultigrpzWelcomeNotes");
//				finaldisplayListNotesText = defualtgrpzSelect;
//				dataArraywelcomedisplay = StaticUtils.createJSONdataArray(finaldisplayListNotesText);
//			}
			
			if (finalSelctEndUrlNotes == null || finalSelctEndUrlNotes.isEmpty() == true)
			{
				String defualtSect = prop.getProperty("ivrselectionHangupNotes");
				finalSelctEndUrlNotes = defualtSect;
				dataArrayhangUP = StaticUtils.createJSONdataArray(finalSelctEndUrlNotes);
			}
//			if (finalSelctEndNotes == null || finalSelctEndNotes.isEmpty() == true)
//			{
//				String defualtSect = prop.getProperty("ivrselectionHangupNotes");
//				finalSelctEndNotes = defualtSect;
//				dataArrayhangUP = StaticUtils.createJSONdataArray(finalSelctEndNotes);
//			}
			dataArraywelcomedisplay.addAll(displayGroupzList);
			dataArraywelcomedisplay.addAll(dataArrayhangUP);
			String displayMultiGroupzList = StaticUtils.createJSONString(dataArraywelcomedisplay);
			cm.setmultigrpzselectlist(selectionListmsg);
			cm.setmultigrpzWelcomeNotes(displayMultiGroupzList);
			cm.setcontextdisplayList(displayMultiGroupzList);
			cm.setcontextselectionList(selectionListmsg);
			cm.setmultiGrpzFlag(true);
			cm.save();

			kkResponse = StaticUtils.processUrlOrTextMultiList(displayMultiGroupzList, playspeed, timeout);
		}
		return kkResponse;
	}
	
	public static String createMemberlistString(String callSessionId, List<GroupzMemberInfo> memberList, ContextMapping cm, String ivrNumber)
	{
		String multiGroupzWelcomenotes = cm.getmultigrpzWelcomeNotes();
		System.out.println("||*||*||*||                    multiGroupzWelcomenotes    " + multiGroupzWelcomenotes + "    ||*||*||*||  ");
		GroupzMemberInfo grpzmem = null;
		Iterator<GroupzMemberInfo> grpzmemiter = memberList.iterator();
		int i = 1;
		String memberselectionlist = "";
		String memberdisplayText = "";
		JSONObject sellistObj = new JSONObject();
		JSONObject listObj = new JSONObject();
		ArrayList<String> multiMemberDataArray = new ArrayList<String>();

		while (grpzmemiter.hasNext())
		{
			grpzmem = grpzmemiter.next();
			String memberDiv = grpzmem.getDivision();
			int memberId = grpzmem.getMemberId();
			String membCOde = grpzmem.getMemberCode();
			JSONObject setObj = new JSONObject();

			setObj.put("memberid", memberId);
			setObj.put("membercode", membCOde);
			sellistObj.put(i + "", setObj);

			String pressStr = "Press " + i + " for ";
			
			multiMemberDataArray.add(pressStr);
			multiMemberDataArray.add(membCOde);
			multiMemberDataArray.add(memberDiv);
			i++;
		}

		listObj.put("selectionList", sellistObj);
		memberselectionlist = listObj.toString();

		IvrGroupzBaseMapping inm = IvrGroupzBaseMapping.getSingleivrnumberMap(ivrNumber);

		String memberwelcome = null;
		String memberwelcomeUrl = null;
		String selectionendNote = null;
		String selectionendUrl = null;
		String starselectNotes = null;
		String starselectUrl = null;

		if (inm != null)
		{
			memberwelcome = inm.getmemberWelcomeNotes();
			System.out.println();
			System.out.println("memberwelcome " + memberwelcome);
			memberwelcomeUrl = inm.getaudioMemberWelcomeUrl();
			System.out.println();
			System.out.println("memberwelcomeUrl " + memberwelcomeUrl);
			selectionendNote = inm.getselectionEndNotes();
			System.out.println();
			System.out.println("selectionendNote " + selectionendNote);
			selectionendUrl = inm.getselectionEndUrl();
			System.out.println();
			System.out.println("selectionendUrl " + selectionendUrl);
			starselectNotes = inm.getpreviousMenuSelectNotes();
			System.out.println();
			System.out.println("starselectNotes " + starselectNotes);
			starselectUrl = inm.getpreviousMenuSelectUrl();
			System.out.println();
			System.out.println("starselectUrl " + starselectUrl);
			
		}

		if (starselectNotes == null || starselectNotes.isEmpty() == true)
		{
			starselectNotes = prop.getProperty("ivrstarselectionNotes");
		}

		if (memberwelcome == null || memberwelcome.isEmpty() == true)
		{
			memberwelcome = prop.getProperty("ivrMultimemberwelcomeNotes");
		}

		if (selectionendNote == null || selectionendNote.isEmpty() == true)
		{
			selectionendNote = prop.getProperty("ivrselectionHangupNotes");
		}

		ArrayList<String> memberWelcomeArray = new ArrayList<String>();

//		memberWelcomeArray = StaticUtils.createJSONdataArray(memberwelcome);
		memberWelcomeArray = StaticUtils.createJSONdataArray(memberwelcomeUrl);
		System.out.println("member welcome Url :::  "+memberWelcomeArray);
		
		ArrayList<String> memberSelectionEndArray = new ArrayList<String>();

//		memberSelectionEndArray = StaticUtils.createJSONdataArray(selectionendNote);
		memberSelectionEndArray = StaticUtils.createJSONdataArray(selectionendUrl);
		System.out.println("selection Hangup Url :::  "+memberSelectionEndArray);
		memberWelcomeArray.addAll(multiMemberDataArray);
		memberWelcomeArray.addAll(memberSelectionEndArray);

		if (multiGroupzWelcomenotes != null && multiGroupzWelcomenotes.isEmpty() == false)
		{
			ArrayList<String> starDataArray = new ArrayList<String>();

//			starDataArray = StaticUtils.createJSONdataArray(starselectNotes);
			starDataArray = StaticUtils.createJSONdataArray(starselectUrl);
			System.out.println("previousmenu selection url   :::  " + starDataArray);
			memberWelcomeArray.addAll(starDataArray);
		}

		memberdisplayText = StaticUtils.createJSONString(memberWelcomeArray);
		cm.setmultiMembSelectlist(memberselectionlist);
		cm.setcontextdisplayList(memberdisplayText);
		cm.setcontextselectionList(memberselectionlist);
		cm.setmultimembWelcomeNotes(memberdisplayText);
		cm.save();

		return memberdisplayText;
	}

	public static String Createrespobject(String responseString, String statuscode)
	{
		String xmlresponse = "error";

		try
		{
			JSONObject responseJson = new JSONObject();
			JSONObject finalReportJson = new JSONObject();
			JSONObject reportJson = new JSONObject();
			reportJson.put("status", responseString);
			reportJson.put("statuscode", statuscode);
			finalReportJson.put("details", reportJson);
			responseJson.put("response", finalReportJson);

			XMLSerializer serializer = new XMLSerializer();
			JSON jsonobj = JSONSerializer.toJSON(responseJson);
			serializer.setRootName("xml");
			serializer.setTypeHintsEnabled(false);
			xmlresponse = serializer.write(jsonobj);

		}
		catch (Exception e)
		{
			logger.info("Exception occured while creating response xml string.", e);
			e.printStackTrace();

			return xmlresponse;
		}
		return xmlresponse;
	}

	public static String getGroupzInfoXML(String countryCode, String number, String stateCode, String request, String functiontype)
	{

		String responsexmlString = null;
	
		try
		{
			String servicetype = prop.getProperty("servicetype");
			String urltoinvoke = prop.getProperty("getGroupzListUrl");
			String moduleCOde = prop.getProperty("serviceReqModulecode");

			JSONObject dataJson = new JSONObject();
			JSONObject contactJson = new JSONObject();
			JSONObject requestJson = new JSONObject();

			if (request.equals("mobile"))
			{
				contactJson.put("countrycode", countryCode);
				contactJson.put("mobilenumber", number);
				dataJson.put("mobile", contactJson);
			}
			else
			{
				contactJson.put("countrycode", countryCode);
				contactJson.put("statecode", stateCode);
				contactJson.put("landlinenumber", number);
				dataJson.put("landline", contactJson);
			}

			dataJson.put("servicetype", servicetype);
			dataJson.put("functiontype", functiontype);
			dataJson.put("modulecode", moduleCOde);
			requestJson.put("request", dataJson);

			// requestJson.put("groupzreqlist", dataJson);

			XMLSerializer serializer = new XMLSerializer();

			JSON jsonadd = JSONSerializer.toJSON(requestJson);
			serializer.setRootName("xml");
			serializer.setTypeHintsEnabled(false);

			String xmlsmsString = serializer.write(jsonadd);

			responsexmlString = StaticUtils.ConnectAndGetResponse(urltoinvoke, xmlsmsString);
			
			System.out.println("INVALID "+ responsexmlString);
		}
		catch (Exception e)
		{
			logger.info("Exception occured while sending request for groupzinfo List for mobile number.", e);
			e.printStackTrace();
			return responsexmlString;
		}
		return responsexmlString;
	}

	public static String getMemberInfoXML(String countryCode, String number, String stateCode, String request, String functiontype, String grpzid)
	{
		String responsexmlString = null;

		try
		{
			String servicetype = prop.getProperty("servicetype");
			String urltoinvoke = prop.getProperty("memberListUrl");//from server using property file

			JSONObject dataJson = new JSONObject();
			JSONObject contactJson = new JSONObject();
			JSONObject requestJson = new JSONObject();

			if (request.equals("mobile"))
			{
				contactJson.put("countrycode", countryCode);
				contactJson.put("mobilenumber", number);
				dataJson.put("mobile", contactJson);
			}
			else
			{
				contactJson.put("countrycode", countryCode);
				contactJson.put("statecode", stateCode);
				contactJson.put("landlinenumber", number);
				dataJson.put("landline", contactJson);
			}

			dataJson.put("servicetype", servicetype);
			dataJson.put("functiontype", functiontype);
			dataJson.put("groupzid", grpzid);
			dataJson.put("includecontacts", false);

			requestJson.put("request", dataJson);

			// requestJson.put("groupzreqlist", dataJson);

			XMLSerializer serializer = new XMLSerializer();

			JSON jsonadd = JSONSerializer.toJSON(requestJson);
			serializer.setRootName("xml");
			serializer.setTypeHintsEnabled(false);

			String xmlsmsString = serializer.write(jsonadd);

			responsexmlString = StaticUtils.ConnectAndGetResponse(urltoinvoke, xmlsmsString);
		}
		catch (Exception e)
		{
			logger.info("Exception occured while sending request for groupzinfo List for mobile number.", e);
			e.printStackTrace();
			return responsexmlString;
		}
		return responsexmlString;
	}

	public static String ConnectAndGetResponse(String urlString, String xmlString)
	{
		String resultString = null;

		try
		{
			StringBuffer sendStr = new StringBuffer();
			sendStr.append("request=");
			sendStr.append(xmlString);
			String sendString = sendStr.toString();

			System.out.println("member info :::  "+urlString + sendString);

			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);

			OutputStreamWriter outputWriter = new OutputStreamWriter(connection.getOutputStream());
			outputWriter.write(sendString);
			outputWriter.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer responseStr = new StringBuffer();

			while ((inputLine = in.readLine()) != null)
			{
				responseStr.append(inputLine);
			}
			in.close();
		
			resultString = responseStr.toString();
		}
		catch (Exception e)
		{
			logger.info("Exception occured in url connection.", e);
			e.printStackTrace();
			return resultString;
		}
		return resultString;
	}

	public static List<GroupzMemberInfo> getMemberInfoList(String xmlString)
	{

		List<GroupzMemberInfo> memberInfoList = new ArrayList<GroupzMemberInfo>();

		try
		{
			XMLSerializer xmlSerializer = new XMLSerializer();
			JSON json = xmlSerializer.read(xmlString);
			JSONObject jo = (JSONObject) JSONSerializer.toJSON(json);
			JSONObject jores = (JSONObject) jo.get("response");
			String statusCode = jores.getString("statuscode");
		
			if (statusCode.equals("0"))
			{
				Object obj = jores.get("memberslist");

				if (obj instanceof JSONArray)
				{
					JSONArray jsnlistarry = jores.getJSONArray("memberslist");
					
					for (int i = 0; i < jsnlistarry.size(); i++)
					{
						GroupzMemberInfo membinfo = new GroupzMemberInfo();
						JSONObject jogrpz = jsnlistarry.getJSONObject(i);

						String membercode = jogrpz.getString("membercode");
						String division = jogrpz.getString("division");
						String memberid = jogrpz.getString("memberid");

						int memid = Integer.parseInt(memberid);
						membinfo.setMemberCode(membercode);
						membinfo.setDivision(division);
						membinfo.setMemberId(memid);
						memberInfoList.add(membinfo);
					}
				}
				else
				{
					GroupzMemberInfo membinfo = new GroupzMemberInfo();

					JSONObject jsngrpzlistObj = jores.getJSONObject("memberslist");
					JSONObject jogrpz = jsngrpzlistObj.getJSONObject("element");
					String membercode = jogrpz.getString("membercode");
					String division = jogrpz.getString("division");
					String memberid = jogrpz.getString("memberid");

					int memid = Integer.parseInt(memberid);
					membinfo.setMemberCode(membercode);
					membinfo.setDivision(division);
					membinfo.setMemberId(memid);
					memberInfoList.add(membinfo);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.info("Exception occured in get staus code.", e);
			return memberInfoList;
		}
		return memberInfoList;
	}

	public static List<GroupzInfoDetails> getGroupzInfoList(String xmlString)
	{

		List<GroupzInfoDetails> groupzInfoList = new ArrayList<GroupzInfoDetails>();

		try
		{
			XMLSerializer xmlSerializer = new XMLSerializer();
			JSON json = xmlSerializer.read(xmlString);
			JSONObject jo = (JSONObject) JSONSerializer.toJSON(json);
			JSONObject jores = (JSONObject) jo.get("response");
			String statusCode = jores.getString("statuscode");
		
			if (statusCode.equals("0"))
			{
				Object obj = jores.get("groupzlist");

				if (obj instanceof JSONArray)
				{
					JSONArray jsnlistarry = jores.getJSONArray("groupzlist");

					for (int i = 0; i < jsnlistarry.size(); i++)
					{
						GroupzInfoDetails grpzinfo = new GroupzInfoDetails();
						JSONObject jogrpz = jsnlistarry.getJSONObject(i);
						String groupzcode = jogrpz.getString("groupzcode");
						String groupzname = jogrpz.getString("groupzname");
						String groupzid = jogrpz.getString("groupzid");

						grpzinfo.setgrpzcode(groupzcode);
						grpzinfo.setgrpzname(groupzname);
						grpzinfo.setgrpzid(groupzid);
						groupzInfoList.add(grpzinfo);
					}
				}
				else
				{
					GroupzInfoDetails grpzinfo = new GroupzInfoDetails();

					JSONObject jsngrpzlistObj = jores.getJSONObject("groupzlist");
					JSONObject jogrpz = jsngrpzlistObj.getJSONObject("element");
					String groupzcode = jogrpz.getString("groupzcode");
					String groupzname = jogrpz.getString("groupzname");
					String groupzid = jogrpz.getString("groupzid");

					grpzinfo.setgrpzcode(groupzcode);
					grpzinfo.setgrpzname(groupzname);
					grpzinfo.setgrpzid(groupzid);
					groupzInfoList.add(grpzinfo);
				}
			}
		}
		catch (Exception e)
		{
			logger.info("Exception occured in get staus code.", e);
			e.printStackTrace();
			return groupzInfoList;
		}
		return groupzInfoList;
	}

	public static String getSelectedLangUrl(String dedicatedUrl, ContextMapping cm, boolean multiLangFlag)
	{
		String selectedUrl = null;
	
		if (multiLangFlag && StaticUtils.isEmptyOrNull(dedicatedUrl) == false)//? need help to understand this
		{
			String languageSelected = null;

			if (cm != null)
			{
				languageSelected = cm.getlanguageSelected();
			}

			if (StaticUtils.isEmptyOrNull(languageSelected))
			{
				languageSelected = prop.getProperty("defualtLanguage");
			}

			JSONObject langObj = (JSONObject) JSONSerializer.toJSON(dedicatedUrl);
			JSONObject langListObj = (JSONObject) langObj.get("urlList");
			selectedUrl = langListObj.getString(languageSelected);
		}
		return selectedUrl.trim();
	}
	
	public static boolean getStatusCode(String xmlString)
	{
		boolean sucessflag = true;

		try
		{
			
			String invalidmobilenumber = prop.getProperty("invalidmobilenumber");
			String invalidlandlinenumber = prop.getProperty("invalidlandlinenumber");
			
			XMLSerializer xmlSerializer = new XMLSerializer();
			JSON json = xmlSerializer.read(xmlString);
			JSONObject jo = (JSONObject) JSONSerializer.toJSON(json);
			JSONObject jores =  jo.getJSONObject("response");
			String statusCode = jores.getString("statuscode");
			String statusMessage=jores.getString("statusmessage");

			if (statusCode.equals("0")||statusCode.equals(invalidmobilenumber)||statusCode.equals(invalidlandlinenumber))
			{
				sucessflag = true;
			}
			else
			{
				sucessflag = false;
				logger.info("status message is :" + statusMessage);	
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.info("Exception occured in get staus code.", e);
			return sucessflag;
		}
		return sucessflag;
	}
	
	public static boolean getJSONStatusCode(String responseJSONString,String grpzCode)
	{
		boolean sucessflag = true;

		try
		{
			JSONObject jsnrespObj = (JSONObject) JSONSerializer.toJSON(responseJSONString);
			JSONObject jsonObj = jsnrespObj.getJSONObject("json");
			JSONObject jores = jsonObj.getJSONObject("response");
			
			String statusCode = jores.getString("statuscode");
			String statusMessage=jores.getString("statusmessage");

			if (statusCode.equals("0"))
			{
				sucessflag = true;
			}
			else
			{
				sucessflag = false;
				logger.info("  Status message for " + grpzCode + " is :  " + statusMessage);	
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.info("Exception occured in get staus code.", e);
			return sucessflag;
		}
		return sucessflag;
	}

	public static ArrayList<String> getGrpzMobileValidationList(String formattedNumber)
	{

		ArrayList<String> groupzInfodetailList = new ArrayList<String>();

		String grpzInfoxmlString = null;
		String statusFlag = "false";
		String[] numberlist = null;
		String countryCode = null;
		String mobileNumber = null;
	
		try
		{
			numberlist = formattedNumber.split("\\.");
			countryCode = numberlist[0];
			mobileNumber = numberlist[1];

			String functionType = prop.getProperty("grpzListMobFunType");

			grpzInfoxmlString = StaticUtils.getGroupzInfoXML(countryCode, mobileNumber, null, "mobile", functionType);

			if (StaticUtils.isEmptyOrNull(grpzInfoxmlString))
			{
				logger.info(" grpzIfoXMl is empty :" + "number : "	+ formattedNumber);
			}
			else
			{
				boolean statusboolFlag = StaticUtils.getStatusCode(grpzInfoxmlString);

				if (statusboolFlag)
				{
					statusFlag = "true";
				}
			}

		}
		catch (Exception e)
		{
			logger.info(" exception in get mobile validation list :" + "number : " + formattedNumber + "exception data :" + e);
			groupzInfodetailList.add(statusFlag);
			groupzInfodetailList.add(grpzInfoxmlString);
			return groupzInfodetailList;
		}
		
		groupzInfodetailList.add(statusFlag);
		groupzInfodetailList.add(grpzInfoxmlString);
		return groupzInfodetailList;
	}

	public static ArrayList<String> getGrpzLandlineValidationList(String formattedNumber, String ivrNumber)
	{

		ArrayList<String> groupzInfodetailList = new ArrayList<String>();
		String grpzInfoxmlString = null;
		String statusFlag = "false";
		String[] numberlist = null;
		String countryCode = null;
		String number = null;
		String stateCode = null;
		
		try
		{
			numberlist = formattedNumber.split("\\.");
			countryCode = numberlist[0];
			stateCode = numberlist[1];
			number = numberlist[2];

			String functionType = prop.getProperty("grpzListlandFunType");
			grpzInfoxmlString = StaticUtils.getGroupzInfoXML(countryCode, number, stateCode, "landline", functionType);

			System.out.println("xmllandline :" + grpzInfoxmlString);

			if (StaticUtils.isEmptyOrNull(grpzInfoxmlString))
			{
				logger.info(" grpzIfoXMl is empty for landline :" + "number : " + formattedNumber);
			}
			else
			{
				boolean statusboolFlag = StaticUtils.getStatusCode(grpzInfoxmlString);

				if (statusboolFlag)
				{
					statusFlag = "true";
				}
			}
		}
		catch (Exception e)
		{
			logger.info(" exception in get landline validation status :" + "number : " + formattedNumber + "exception data :" + e);
			groupzInfodetailList.add(statusFlag);
			groupzInfodetailList.add(grpzInfoxmlString);
			return groupzInfodetailList;
		}
		groupzInfodetailList.add(statusFlag);
		groupzInfodetailList.add(grpzInfoxmlString);
		return groupzInfodetailList;
	}

	public static ArrayList<String> getMemberMobileValidationList(String formattedNumber, String grpzID)
	{

		ArrayList<String> groupzInfodetailList = new ArrayList<String>();

		String membXmlInfo = null;
		String statusFlag = "false";
		String[] numberlist = null;
		String countryCode = null;
		String mobileNumber = null;
	
		try
		{
			numberlist = formattedNumber.split("\\.");
			countryCode = numberlist[0];
			mobileNumber = numberlist[1];

			String functionType = prop.getProperty("memberlistMobFunType");

			membXmlInfo = StaticUtils.getMemberInfoXML(countryCode, mobileNumber, null, "mobile", functionType, grpzID);

			if (StaticUtils.isEmptyOrNull(membXmlInfo))
			{
				logger.info(" membXmlInfo for mobile is empty :" + "number : " + formattedNumber);
			}
			else
			{
				boolean statusboolFlag = StaticUtils.getStatusCode(membXmlInfo);

				if (statusboolFlag)
				{
					statusFlag = "true";
				}
			}
		}
		catch (Exception e)
		{
			logger.info(" exception in get mobile member list  :" + "number : " + formattedNumber + "exception data :" + e);

			groupzInfodetailList.add(statusFlag);
			groupzInfodetailList.add(membXmlInfo);
			return groupzInfodetailList;
		}
		groupzInfodetailList.add(statusFlag);
		groupzInfodetailList.add(membXmlInfo);
		return groupzInfodetailList;
	}

	public static ArrayList<String> getMemberLandLineValidationList(String formattedNumber, String grpzID) {

		ArrayList<String> groupzInfodetailList = new ArrayList<String>();

		String membXmlInfo = null;
		String statusFlag = "false";
		String[] numberlist = null;
		String countryCode = null;
		String statecode = null;
		String mobileNumber = null;

		try
		{
			numberlist = formattedNumber.split("\\.");
			countryCode = numberlist[0];
			statecode = numberlist[1];
			mobileNumber = numberlist[2];

			String functionType = prop.getProperty("memberlistlandFunType");

			membXmlInfo = StaticUtils.getMemberInfoXML(countryCode, mobileNumber, statecode, "landline", functionType, grpzID);

			if (StaticUtils.isEmptyOrNull(membXmlInfo))
			{
				logger.info(" membXmlInfo for landline is empty :" + "number : " + formattedNumber);
			}
			else
			{
				boolean statusboolFlag = StaticUtils.getStatusCode(membXmlInfo);

				if (statusboolFlag)
				{
					statusFlag = "true";
				}
			}
		}
		catch (Exception e)
		{
			logger.info(" exception in get landline member list  :" + "number : " + formattedNumber + "exception data :" + e);

			groupzInfodetailList.add(statusFlag);
			groupzInfodetailList.add(membXmlInfo);
			return groupzInfodetailList;
		}
		groupzInfodetailList.add(statusFlag);
		groupzInfodetailList.add(membXmlInfo);
		return groupzInfodetailList;
	}

	public static String createJSONString(ArrayList<String> dataArray)
	{
		String jsonString = null;
		JSONObject languageObj = new JSONObject();
		JSONArray settingObj = new JSONArray();

		for (int i = 0; i < dataArray.size(); i++)
		{
			String dataStr = dataArray.get(i);
			settingObj.add(dataStr);	
		}
		languageObj.put("dataList", settingObj);
		jsonString = languageObj.toString();
		return jsonString;
	}
	
	public static ArrayList<String> createJSONdataArray(String dataSTR)
	{
		ArrayList<String> dataArray = new ArrayList<String>();

		JSONObject dataObj = (JSONObject) JSONSerializer.toJSON(dataSTR);
		JSONArray jsndatalistObj = dataObj.getJSONArray("dataList");
		
		for (int i = 0; i < jsndatalistObj.size(); i++)
		{
			String playStr = jsndatalistObj.getString(i);
			dataArray.add(playStr);
		}
		return dataArray;
	}
	
	public static Response displayTextOrURLforSelection(String text, String audioUrl, int playspeed, int timeout,ContextMapping contxt)
	{
		Response kkResponse = new Response();

		ArrayList<String> changeurldataList = new ArrayList<String>();
		
		String recordTypeDisplayList = null;

		if (audioUrl == null || audioUrl.trim().isEmpty() == true)
		{
			recordTypeDisplayList=text;
		}
		else
		{
			changeurldataList.add(audioUrl.trim());
			recordTypeDisplayList = StaticUtils.createJSONString(changeurldataList);
		}	
		kkResponse = StaticUtils.processUrlOrTextMultiList(recordTypeDisplayList, playspeed, timeout);
		contxt.setcontextdisplayList(recordTypeDisplayList);		
		contxt.save();
		return kkResponse;
	}
	
	public static String createUrlListJSONString(String url, String lan, String jsonString,ContextMapping cm)
	{
		String finaljsonString = jsonString;
		JSONObject jsnUrlObj = new JSONObject();
		
		if (StaticUtils.isEmptyOrNull(jsonString) == false)
		{
			JSONObject dataObj = (JSONObject) JSONSerializer.toJSON(jsonString);
			JSONObject jsndatalistObj = dataObj.getJSONObject("urlList");

			if(jsndatalistObj.containsKey(lan))
			{
				jsndatalistObj.remove(lan);
			}
			jsndatalistObj.put(lan, url);				
			jsnUrlObj.put("urlList", jsndatalistObj);
			finaljsonString = jsnUrlObj.toString();
		}
		else
		{
			JSONObject jsndataObj = new JSONObject();
			jsndataObj.put(lan, url);
			jsnUrlObj.put("urlList", jsndataObj);
			finaljsonString = jsnUrlObj.toString();	
		}
		cm.setpublishUrlList(finaljsonString);
		cm.save();
		return finaljsonString;
	}
	
	public static ArrayList<String> createivrselectionMenuList(String welcomeNotes,String selectionList,String endNotes)
	{		
		JSONObject dataObj = (JSONObject) JSONSerializer.toJSON(welcomeNotes);
		JSONArray jsndatalistObj = dataObj.getJSONArray("welcomenotesList");
		int datasize = jsndatalistObj.size();

			ArrayList<String> dataarrayList = new ArrayList<String>();

			for (int i = 0; i < datasize; i++)
			{
				String dataStr = jsndatalistObj.getString(i);
				dataarrayList.add(dataStr);
			}
System.out.println("selectionList *****************" +selectionList);
			
try{
			//JSONObject selectiondataObj = JSONObject.fromObject(selectionList);
			JSONObject selectiondataObj = (JSONObject) JSONSerializer.toJSON(selectionList);
			JSONObject jsnseldatalistObj = selectiondataObj.getJSONObject("selectionList");

			datasize = jsnseldatalistObj.size();

			for (int i = 1; i <=datasize; i++)
			{
				String datacatgryStr = jsnseldatalistObj.getString(i + "");
				datacatgryStr="Press "+i+" for " +datacatgryStr;
				dataarrayList.add(datacatgryStr);
			}
			
			JSONObject dataendObj = (JSONObject) JSONSerializer.toJSON(endNotes);
			 jsndatalistObj = dataendObj.getJSONArray("dataList");

			 datasize = jsndatalistObj.size();		

			for (int i = 0; i <datasize; i++)
			{
				String dataStr = jsndatalistObj.getString(i);
				dataarrayList.add(dataStr);
			}

	//	dataJSNString = StaticUtils.createJSONString(dataarrayList);
}catch(Exception e){
	e.printStackTrace();
}
		return dataarrayList;		
	}
	
	public static Response sendNoDetailsNotes(String notes,String urlStr)
	{
		Response kkResponse = new Response();
		String defaultNotes = null;
		String defaulturl = null;
		String playspeedStr = prop.getProperty("playspeed");
		int playspeed = Integer.parseInt(playspeedStr);
		defaultNotes =notes;
		defaulturl = urlStr;

		if (StaticUtils.isEmptyOrNull(defaulturl) == false)
		{
			kkResponse.addPlayAudio(defaulturl);
		}
		else
		{
			JSONObject dataObj = (JSONObject) JSONSerializer.toJSON(defaultNotes);
			JSONArray jsndatalistObj = dataObj.getJSONArray("dataList");
			int datasize = jsndatalistObj.size();
			
			for (int i = 0; i < datasize; i++)
			{
				String starStr = jsndatalistObj.getString(i);
				kkResponse.addPlayText(starStr, playspeed);
			}
		}
		kkResponse.addHangup();
		return kkResponse;
	}

	public static void main(String args[])
	{
		// List<GroupzInfo>
		// groupzInfoList=StaticUtils.getGroupzInfoMobileList("91",
		// "9986067657");
		// String xml = StaticUtils.getGroupzInfoXML("91", "9986067657",
		// "http://code.groupz.in:8080/RestAPI/SelectGroupzServlet?", null,
		// "mobile");

		// List<GroupzInfoDetails> groupzInfoList = StaticUtils
		// .getGroupzInfoList("<xml><response><servicetype>21</servicetype><functiontype>2002</functiontype><statuscode>0</statuscode><statusmessage>Success</statusmessage><groupzlist><element><groupzcode>5676</groupzcode><groupzname>apart001</groupzname><groupzid>186</groupzid></element></groupzlist></response></xml>");

		String xml = StaticUtils.ConnectAndGetResponse("http://code.groupz.in:8080/RestAPI/SelectGroupzServlet?", 
				"<xml><request><functiontype>2001</functiontype><mobile><countrycode>91</countrycode>"
				+ "<mobilenumber>9986067657</mobilenumber></mobile><modulecode>8</modulecode><servicetype>21</servicetype></request></xml>");

		// List<String> categories = StaticUtils.getCategoryList("61",
		// "919986067657", "567889");

		System.out.println(xml);
	}
}
