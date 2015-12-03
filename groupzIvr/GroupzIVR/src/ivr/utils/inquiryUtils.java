package ivr.utils;

import java.io.InputStream;
import ivr.tables.ContextMapping;
import ivr.tables.IvrGroupzMapping;
import java.util.ArrayList;
import java.util.Properties;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.log4j.Logger;
import ivr.modules.inquiryRequest.SendInquirySms;
import ivr.tables.IvrGroupzBaseMapping;
import com.ozonetel.kookoo.Response;

public class inquiryUtils {
	static Properties prop = new Properties();

	private static Logger logger = Logger.getLogger("inquiryLogger");
	static {
		try {

			InputStream in = inquiryUtils.class
					.getResourceAsStream("/ivr.properties");
			prop.load(in);

		} catch (Exception e) {
			logger.error("Exception occured in load property file.", e);
			e.printStackTrace();

		}
	}

	public static String ReplaceWithInquiryDetails(String message,
			String option, String callerid) {

		String replacedString = message.replace("#S#", option);
		replacedString = replacedString.replace("#P#", callerid);

		return replacedString;
	}

	public static Response prcoessAfterFinalSelection(IvrGroupzBaseMapping ivrsetting,
			int playspeed, String ivrNumber, String callerno, String selectOpt,
			String selectedinqforAddress, ContextMapping co,
			String callSessionId, boolean multiLangIVR,IvrGroupzMapping sm) {
		Response kkResponse = new Response();
		try {

			String smstext = sm.getsmstext();

			if (smstext != null && smstext.isEmpty() == false) {
				String smsMessage = inquiryUtils.ReplaceWithInquiryDetails(
						smstext, selectOpt, callerno);

				logger.info("Inquiry Sms Text" + smsMessage);

				String smsaddress = sm.getAddress();

				SendInquirySms.sendinqsms(smsMessage, smsaddress,
						selectedinqforAddress);
				
				logger.info("Placed inquiry thorugh sms");
				System.out.println("Inside process continuous call ");

				String AudioURl = ivrsetting.getaudioSelectionHangupUrl();
				String Audiotext = ivrsetting.getselectionHangupNotes();

				kkResponse = StaticUtils.playUrlOrTextMessage(Audiotext, AudioURl,
						playspeed, co, multiLangIVR);

				kkResponse.addHangup();
			}else{
				
				logger.info("SMS text is null so didnt place the inquiry sms");
				kkResponse = StaticUtils
						.senderrorResp(callSessionId, ivrNumber, co);
				return kkResponse;
			}

	

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("There is error while invoking to send sms." + e);
			kkResponse = StaticUtils
					.senderrorResp(callSessionId, ivrNumber, co);
			return kkResponse;
		}
		return kkResponse;
	}

	public static Response generateNewInquiryRequest(IvrGroupzBaseMapping ivrsetting,
			int playspeed, int timeout, ContextMapping cm,IvrGroupzMapping sm) {

		Response kkResponse = new Response();

		ArrayList<String> dataArraywelcomedisplay = new ArrayList<String>();

		String audioUrl = null;
		String welcomenotes = null;
		String starUrl = null;
		String starNotes = null;
		boolean multiLangFlag = false;
		String selectionList = null;
		if (sm != null) {
			audioUrl = sm.getaudiogeneralinqWelcomeUrl();
			welcomenotes = sm.getgeneralinqwelcomeNotes();
			selectionList = sm.getgeneralinquirySelectionList();
		}if(ivrsetting!=null){
			starUrl = ivrsetting.getpreviousMenuSelectUrl();
			starNotes = ivrsetting.getpreviousMenuSelectUrl();
			multiLangFlag = ivrsetting.getmultiLanguageFlag();
		}

		if (multiLangFlag && StaticUtils.isEmptyOrNull(audioUrl) == false) {

			audioUrl = StaticUtils.getSelectedLangUrl(audioUrl, cm,
					multiLangFlag);

		}

		if (multiLangFlag && StaticUtils.isEmptyOrNull(starUrl) == false) {

			starUrl = StaticUtils
					.getSelectedLangUrl(starUrl, cm, multiLangFlag);

		}

		if (audioUrl == null || audioUrl.trim().isEmpty() == true) {

			if (welcomenotes != null && welcomenotes.trim().isEmpty() == false
					&& selectionList != null
					&& selectionList.trim().isEmpty() == false) {
				
				String endNotes = ivrsetting.getselectionEndNotes();

				dataArraywelcomedisplay = StaticUtils
						.createivrselectionMenuList(welcomenotes, selectionList,endNotes);

				// StaticUtils.ReplaceWithPlaytext(dataJSNString, cd,
				// playspeed);

			}
		} else {
			dataArraywelcomedisplay.add(audioUrl.trim());

		}
		String multiGrpzwelcome = null;
		if (cm != null) {
			multiGrpzwelcome = cm.getmultigrpzWelcomeNotes();

		}

		if (multiGrpzwelcome != null && multiGrpzwelcome.isEmpty() == false) {

			if (starUrl != null && starUrl.isEmpty() == false) {
				dataArraywelcomedisplay.add(starUrl.trim());

			} else {
			
				ArrayList<String> datalistObj = StaticUtils.createJSONdataArray(starNotes);	
				dataArraywelcomedisplay.addAll(datalistObj);
			}

		}

		String displayListString = StaticUtils
				.createJSONString(dataArraywelcomedisplay);
		cm.setnewInquiryDisplayList(displayListString);
		cm.setnewInquiryselectionList(selectionList);
		cm.setcontextselectionList(selectionList);
		cm.setcontextdisplayList(displayListString);
		cm.save();

		kkResponse = StaticUtils.processUrlOrTextMultiList(displayListString,
				playspeed, timeout);

		return kkResponse;
	}

	public static Response generatedetailedInquiryResponse(IvrGroupzMapping sm,
			ContextMapping cm, int playspeed, int timeout, IvrGroupzBaseMapping inm,
			String selectedOpt) {
		
		ArrayList<String> dataArraywelcomedisplay = new ArrayList<String>();
		Response kkResponse = new Response();

		boolean multiLangFlag = false;
		String starUrl = null;
		String starData = null;
		String slectedLanguage = null;
		String newselectionListStr = null;
		
		String audioUrl = sm.getAudioWelcomeUrl();
		String welcomenotes = sm.getWelcomeNotes();
		String selectionList = sm.getselectionlist();

		if (cm != null) {

			slectedLanguage = cm.getlanguageSelected();

		}

		if (inm != null) {
			multiLangFlag = inm.getmultiLanguageFlag();
			starUrl = inm.getpreviousMenuSelectUrl();
			starData = inm.getpreviousMenuSelectNotes();
		}

		if (multiLangFlag) {

			if (StaticUtils.isEmptyOrNull(audioUrl) == false) {

				JSONObject langObj = (JSONObject) JSONSerializer
						.toJSON(audioUrl);

				JSONObject langListObj = (JSONObject) langObj.get("urlList");

				JSONObject deicatedJSNObj = langListObj
						.getJSONObject(slectedLanguage);

				audioUrl = deicatedJSNObj.getString(selectedOpt);
				dataArraywelcomedisplay.add(audioUrl.trim());
			}

			if (multiLangFlag && StaticUtils.isEmptyOrNull(starUrl) == false) {

				starUrl = StaticUtils.getSelectedLangUrl(starUrl, cm,
						multiLangFlag);
				dataArraywelcomedisplay.add(starUrl.trim());

			}

		} else if (!multiLangFlag) {

			if (StaticUtils.isEmptyOrNull(audioUrl) == false) {

				JSONObject urlObj = (JSONObject) JSONSerializer
						.toJSON(audioUrl);

				JSONObject urlListObj = (JSONObject) urlObj.get("urlList");

				audioUrl = urlListObj.getString(selectedOpt);

				dataArraywelcomedisplay.add(audioUrl.trim());

			}
			if (audioUrl == null || audioUrl.trim().isEmpty() == true) {

				if (welcomenotes != null
						&& welcomenotes.trim().isEmpty() == false
						&& selectionList != null
						&& selectionList.trim().isEmpty() == false) {
					
					JSONObject selectiondataObj = (JSONObject) JSONSerializer
							.toJSON(selectionList);

					JSONObject jsnseldatalistObj = selectiondataObj
							.getJSONObject("selectionList");
					
					JSONObject jsnseldatalistinqObj = jsnseldatalistObj.getJSONObject(selectedOpt);
					
					JSONObject newSelectionList = new JSONObject();
					
					newSelectionList.put("selectionList", jsnseldatalistinqObj);
					
					 newselectionListStr = newSelectionList.toString();
					
					 String endNotes = inm.getselectionEndNotes();

					dataArraywelcomedisplay = StaticUtils
							.createivrselectionMenuList(welcomenotes,
									newselectionListStr,endNotes);

					// StaticUtils.ReplaceWithPlaytext(dataJSNString, cd,
					// playspeed);

				}
			}

			if (starUrl == null || starUrl.isEmpty() == true) {
				if (starData != null && starData.isEmpty() == false) {
					ArrayList<String> datalistObj = StaticUtils
							.createJSONdataArray(starData);
					dataArraywelcomedisplay.addAll(datalistObj);

				}

			} else {
				dataArraywelcomedisplay.add(starUrl.trim());
			}

		}

		String displayListString = StaticUtils
				.createJSONString(dataArraywelcomedisplay);

		cm.setcontextselectionList(newselectionListStr);
		cm.setcontextdisplayList(displayListString);
		cm.setdetailedInqFlag(true);

		cm.setselectedgeneralinqOption(selectedOpt);

		cm.save();

		kkResponse = StaticUtils.processUrlOrTextMultiList(displayListString,
				playspeed, timeout);

		return kkResponse;

	}


}
