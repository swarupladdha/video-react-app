package ivr.utils;

import ivr.tables.ContextMapping;
import ivr.tables.RecordTransactions;
import ivr.tables.IvrGroupzMapping;
import ivr.tables.IvrGroupzBaseMapping;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.apache.log4j.Logger;

import com.ozonetel.kookoo.Record;
import com.ozonetel.kookoo.Response;

public class recordUtils {

	public static Logger logger = Logger.getLogger("recordLogger");
	static Properties prop = new Properties();

	static {

		try {
			System.setProperty("Hibernate-Url",
					recordUtils.class.getResource("/Hibernate.cfg.xml")
							.toString());

			InputStream in = recordUtils.class
					.getResourceAsStream("/ivr.properties");
			prop.load(in);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public static InputStream getaudiostream(int id) {
		InputStream inputstream = null;

		try {

			RecordTransactions stream = RecordTransactions.getSingleContext(id);

			byte[] bytes = stream.getwavdata();

			inputstream = new ByteArrayInputStream(bytes);

			logger.info("inputstream received " + inputstream);

		} catch (Exception e) {

		}

		return inputstream;
	}

	public static Response startRecord(String sessonid, ContextMapping cm,
			IvrGroupzMapping smap) {

		Record record = new Record();

		String recfilename = prop.getProperty("recfilename");
		String format = prop.getProperty("recordfileformat");
		String maxduration = prop.getProperty("recmaxduration");
		int duration = Integer.parseInt(maxduration);
		String silence = prop.getProperty("recsilence");
		int silencelimit = Integer.parseInt(silence);
		// //give unique file name for each recording

		int memberId = cm.getmemberId();
		String filename = recfilename + memberId + new Date().getTime();
		record.setFileName(filename);
		record.setFormat(format);
		record.setMaxDuration(duration);
		record.setSilence(silencelimit);

		cm.setrecordFlag(false);
		cm.setmultiLanguageFlag(false);
		cm.save();
		Response kookooResponse = new Response();

		String recinstruct = smap.getrecordInstructionUrl();

		if (StaticUtils.isEmptyOrNull(recinstruct)) {

			recinstruct = smap.getrecordInstructionNote();
			kookooResponse.addPlayText(recinstruct);
		} else {
			kookooResponse.addPlayAudio(recinstruct);
		}

		kookooResponse.addRecord(record);

		return kookooResponse;

	}



	public static void InsertDataToPublishXML(ContextMapping contxt) {

		String alloption = prop.getProperty("alloptionValue");
		String filterVal = prop.getProperty("filtervalue");
		String functionType = prop.getProperty("publishXMLFunctype");
		String servicetype = prop.getProperty("servicetype");

		String ivrNumber = contxt.getIvrNumber();
		String mainMStr = contxt.getmainMenuSelection();
		String subMenuTrack = contxt.getrecordselectedList();
		String kookoourllist = contxt.getpublishUrlList();
		String grpzcode = contxt.getgroupzCode();
		String mobileNumber = contxt.getCallerId();
		int memberId = contxt.getmemberId();
		String memberIDString = Integer.toString(memberId);
		String memberCode = contxt.getmemberCode();

		try {

			IvrGroupzMapping smap = IvrGroupzMapping
					.getSingleivrSourceMapwithGroupzCode(ivrNumber, grpzcode);
			String datestr = null;
			Date endDate = smap.getendDate();

			if (endDate == null) {
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.DATE, 7);
				endDate = c.getTime();
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			datestr = dateFormat.format(endDate);

			JSONObject finalReportJson = new JSONObject();
			JSONObject subMenuJSON = new JSONObject();
			JSONObject jsnoObj = new JSONObject();
			JSONObject grpzCode = new JSONObject();
			JSONObject contactNumbers = new JSONObject();
			JSONObject selectionJson = new JSONObject();
			JSONArray subMenuArray = new JSONArray();

			String[] numbers = null;
			String countryCode = null;
			String mobilenumber = null;
			
			jsnoObj.put("servicetype", servicetype);
			jsnoObj.put("functiontype", functionType);
			selectionJson.put("filter", filterVal);

			if (StaticUtils.isEmptyOrNull(mobileNumber) == false) {
				numbers = mobileNumber.split("\\.");
				countryCode = numbers[0];
				mobilenumber = numbers[1];
			}
			
			
			if (mainMStr.equals(alloption)) {
				selectionJson.put("allusers", "true");

			} else {
				selectionJson.put("allusers", "false");

				if (StaticUtils.isEmptyOrNull(subMenuTrack) == false) {

					String[] subMenuList = subMenuTrack.split(",");

					List<String> subCollectList = new ArrayList<String>();

					Collections.addAll(subCollectList, subMenuList);

					HashSet<String> hashSet = new HashSet<String>(
							subCollectList);

					subCollectList.clear();

					subCollectList.addAll(hashSet);

					String keyName = mainMStr.substring(0,
							mainMStr.length() - 1);
					subCollectList.contains(alloption);

					if (subCollectList.contains(alloption)) {

						subMenuArray.add(alloption);

					} else {

						for (int i = 0; i < subCollectList.size(); i++) {

							String str = subCollectList.get(i);
							subMenuJSON.put(keyName, str);
							subMenuArray.add(subMenuJSON);

						}

					}				

					selectionJson.put(mainMStr, subMenuArray);
				}
			}
				

				grpzCode.put("groupzcode", grpzcode);
				JSONArray grpzArray = new JSONArray();
				
				grpzArray.add(grpzCode);
				
				jsnoObj.put("groupzlist", grpzArray);

				contactNumbers.put("countrycode", countryCode);
				contactNumbers.put("mobilenumber", mobilenumber);
				jsnoObj.put("mobile", contactNumbers);
				
				
				jsnoObj.put("membercode", memberCode);
				jsnoObj.put("memberid", memberIDString);
				
				jsnoObj.put("selection", selectionJson);
				finalReportJson.put("request", jsnoObj);

				// Inserting data to table to download the recorded messages.
				RecordTransactions recTranobj = new RecordTransactions();

				recTranobj.setdatetime(new Date());
				recTranobj.setgroupzCode(grpzcode);
				recTranobj.setkookoourls(kookoourllist);
				recTranobj.setselectiondata(finalReportJson.toString());
				recTranobj.setdownloadFlag(false);
				recTranobj.setpublishedFlag(false);
				recTranobj.setendDateString(datestr);
				recTranobj.save();

			

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	public static Response generateMainMenuRequest(IvrGroupzMapping sm,
			ContextMapping cm, IvrGroupzBaseMapping inm) {

		Response kkResponse = new Response();
		ArrayList<String> dataArraywelcomedisplay = new ArrayList<String>();
		try {

			String displayList = null;

			String audioUrl = null;
			String welcomenotes = null;
			String starUrl = null;
			String starNotes = null;
			boolean multiLangFlag = false;
			String selectionList = null;
			int playspeed = 0;
			int timeout = 0;
			if (sm != null) {

				welcomenotes = sm.getWelcomeNotes();
				audioUrl = sm.getAudioWelcomeUrl();
				selectionList = sm.getselectionlist();

			}

			if (inm != null) {
				starUrl = inm.getpreviousMenuSelectUrl();
				starNotes = inm.getpreviousMenuSelectNotes();
				multiLangFlag = inm.getmultiLanguageFlag();
				playspeed = inm.getplayspeed();
				timeout = inm.getsettimeout();
			}

			if (multiLangFlag && StaticUtils.isEmptyOrNull(audioUrl) == false) {

				audioUrl = StaticUtils.getSelectedLangUrl(audioUrl, cm,
						multiLangFlag);
				dataArraywelcomedisplay.add(audioUrl);

			}

			if (multiLangFlag && StaticUtils.isEmptyOrNull(starUrl) == false) {

				starUrl = StaticUtils.getSelectedLangUrl(starUrl, cm,
						multiLangFlag);
				dataArraywelcomedisplay.add(starUrl);

			}

			if (audioUrl == null || audioUrl.trim().isEmpty() == true) {

				if (welcomenotes != null
						&& welcomenotes.trim().isEmpty() == false
						&& selectionList != null
						&& selectionList.trim().isEmpty() == false) {

					 String endNotes = inm.getselectionEndNotes();
					
					dataArraywelcomedisplay = StaticUtils
							.createivrselectionMenuList(welcomenotes,
									selectionList,endNotes);

				}
			} else {
				dataArraywelcomedisplay.add(audioUrl);

			}
			String multiGrpzwelcome = null;
			if (cm != null) {
				multiGrpzwelcome = cm.getmultigrpzWelcomeNotes();

			}

			if (multiGrpzwelcome != null && multiGrpzwelcome.isEmpty() == false) {

				if (starUrl != null && starUrl.isEmpty() == false) {
					dataArraywelcomedisplay.add(starUrl);

				} else {

					JSONObject dataObj = (JSONObject) JSONSerializer
							.toJSON(starNotes);

					JSONArray jsndatalistObj = dataObj.getJSONArray("dataList");

					int datasize = jsndatalistObj.size();

					for (int i = 0; i < datasize; i++) {

						String prevStr = jsndatalistObj.getString(i);

						dataArraywelcomedisplay.add(prevStr.trim());

					}

				}

			}

			displayList = StaticUtils.createJSONString(dataArraywelcomedisplay);

			cm.setrecordMainMenudisplayList(displayList);
			cm.setcontextdisplayList(displayList);
			cm.setcontextselectionList(selectionList);
			cm.save();

			kkResponse = StaticUtils.processUrlOrTextMultiList(displayList,
					playspeed, timeout);

			return kkResponse;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return kkResponse;
	}

	public static ArrayList<String> getrecGrpzMobileValidationList(
			String formattedNumber) {

		ArrayList<String> groupzInfodetailList = new ArrayList<String>();

		String grpzInfoxmlString = null;
		String statusFlag = "false";
		String[] numberlist = null;
		String countryCode = null;
		String mobileNumber = null;
		try {

			numberlist = formattedNumber.split("\\.");
			countryCode = numberlist[0];
			mobileNumber = numberlist[1];

			String functionType = prop
					.getProperty("grpzlistrecmobfunctiontype");
			String servicetype = prop.getProperty("servicetype");
			String urltoinvoke = prop.getProperty("getGroupzListUrl");
			String roleoffset = prop.getProperty("recordroleoffset");

			JSONObject dataJson = new JSONObject();
			JSONObject contactJson = new JSONObject();
			JSONObject requestJson = new JSONObject();
			JSONObject elementJson = new JSONObject();
			JSONObject roleoffsetJson = new JSONObject();

			contactJson.put("countrycode", countryCode);
			contactJson.put("mobilenumber", mobileNumber);

			roleoffsetJson.put("roleoffsetvalue", roleoffset);
			elementJson.put("element", roleoffsetJson);

			dataJson.put("servicetype", servicetype);
			dataJson.put("functiontype", functionType);
			dataJson.put("roleoffsetlist", elementJson);
			dataJson.put("mobile", contactJson);

			requestJson.put("request", dataJson);

			// requestJson.put("groupzreqlist", dataJson);

			XMLSerializer serializer = new XMLSerializer();

			JSON jsonadd = JSONSerializer.toJSON(requestJson);
			serializer.setRootName("xml");
			serializer.setTypeHintsEnabled(false);

			String xmlsmsString = serializer.write(jsonadd);

			grpzInfoxmlString = StaticUtils.ConnectAndGetResponse(urltoinvoke,
					xmlsmsString);

			if (StaticUtils.isEmptyOrNull(grpzInfoxmlString)) {

				logger.info(" grpzIfoXMl is empty :" + "number : "
						+ formattedNumber);

			} else {

				boolean statusboolFlag = StaticUtils
						.getStatusCode(grpzInfoxmlString);

				if (statusboolFlag) {

					statusFlag = "true";
				}

			}

		} catch (Exception e) {
			logger.info(" exception in get mobile validation record list :"
					+ "number : " + formattedNumber + "exception data :" + e);

			groupzInfodetailList.add(statusFlag);
			groupzInfodetailList.add(grpzInfoxmlString);
			return groupzInfodetailList;

		}

		groupzInfodetailList.add(statusFlag);
		groupzInfodetailList.add(grpzInfoxmlString);
		return groupzInfodetailList;

	}

	public static Response createSubMenu(String ivrNumber, ContextMapping cm,
			int playspeed, int settimeout, IvrGroupzBaseMapping inm) {

		Response kkResponse = new Response();

		try {

			String grpzcode = cm.getgroupzCode();

			String mainMenuSelct = cm.getmainMenuSelection();

			IvrGroupzMapping sm = IvrGroupzMapping
					.getSingleivrSourceMapwithGroupzCode(ivrNumber, grpzcode);

			ArrayList<String> dataArraywelcomedisplay = new ArrayList<String>();

			String displayList = null;

			String audioUrl = null;
			String welcomenotes = null;
			String starUrl = null;
			String starNotes = null;

			String selectionList = null;

			if (sm != null) {

				welcomenotes = sm.getrecordSubMenuDisplayList();
				logger.info("subMenuDispleay starting " + welcomenotes);
				
				String selectionListStr = sm.getrecordSubMenuSelectionList();
				
				JSONObject selectiondataObj = (JSONObject) JSONSerializer
						.toJSON(selectionListStr);
				
				JSONObject jsndatalistObj = selectiondataObj
						.getJSONObject("subMenuSelectionList");

				JSONObject sublistObj = jsndatalistObj
						.getJSONObject(mainMenuSelct);

				selectionList = sublistObj.toString();
			}

			if (inm != null) {
				starUrl = inm.getpreviousMenuSelectUrl();
				starNotes = inm.getpreviousMenuSelectNotes();

			}

			if (welcomenotes != null && welcomenotes.trim().isEmpty() == false) {

				JSONObject displaydataObj = (JSONObject) JSONSerializer
						.toJSON(welcomenotes);
				JSONObject jsndisplaylistObj = displaydataObj
						.getJSONObject("subMenuDisplayList");
				
				JSONArray jsndataendlistObj = new JSONArray();
				jsndataendlistObj = jsndisplaylistObj.getJSONArray("endnotesList");

				JSONObject subdisplaylistObj = jsndisplaylistObj
						.getJSONObject(mainMenuSelct);

				JSONObject audiotObj = subdisplaylistObj.getJSONObject("audio");
				if (audiotObj != null) {
					audioUrl = audiotObj.getString("url");
				}

				if (audioUrl != null && audioUrl.trim().isEmpty() == false) {
					dataArraywelcomedisplay.add(audioUrl);

				} else {

					JSONObject datalistObj = subdisplaylistObj
							.getJSONObject("text");

					dataArraywelcomedisplay = recordUtils
							.createivrRecordDisplayMenuList(datalistObj,
									jsndataendlistObj);
				}
			}

			if (starUrl != null && starUrl.isEmpty() == false) {
				dataArraywelcomedisplay.add(starUrl);

			} else {

				JSONObject dataObj = (JSONObject) JSONSerializer
						.toJSON(starNotes);

				JSONArray jsndatalistObj = dataObj.getJSONArray("dataList");

				int datasize = jsndatalistObj.size();

				for (int i = 0; i < datasize; i++) {

					String prevStr = jsndatalistObj.getString(i);

					dataArraywelcomedisplay.add(prevStr.trim());

				}

			}

			displayList = StaticUtils.createJSONString(dataArraywelcomedisplay);
			cm.setcontextdisplayList(displayList);
			cm.setcontextselectionList(selectionList);
			cm.setsubMenuFlag(true);
			cm.save();

			kkResponse = StaticUtils.processUrlOrTextMultiList(displayList,
					playspeed, settimeout);

			return kkResponse;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return kkResponse;

	}

	public static ArrayList<String> createivrRecordDisplayMenuList(
			JSONObject dataObj,JSONArray jsnenddatalistObj) {

		ArrayList<String> dataarrayList = new ArrayList<String>();
		int datasize = 0;
		JSONArray jsndatalistObj = new JSONArray();

		if (dataObj != null && dataObj.isEmpty()==false && dataObj.isNullObject()==false ) {

			

			if (dataObj.containsKey("welcomenotesList")) {

				jsndatalistObj = dataObj.getJSONArray("welcomenotesList");

				datasize = jsndatalistObj.size();

				for (int i = 0; i < datasize; i++) {

					String dataStr = jsndatalistObj.getString(i);

					dataarrayList.add(dataStr);
				}
			}

			JSONObject jsnseldatalistObj = dataObj.getJSONObject("dataList");

			datasize = jsnseldatalistObj.size();

			for (int i = 1; i <= datasize; i++) {

				String datacatgryStr = jsnseldatalistObj.getString(i + "");

				datacatgryStr = "Press " + i + " for " + datacatgryStr;

				dataarrayList.add(datacatgryStr);
			}

			

			datasize = jsnenddatalistObj.size();

			for (int i = 0; i < datasize; i++) {

				String dataStr = jsnenddatalistObj.getString(i);

				dataarrayList.add(dataStr);
			}

		}

		// dataJSNString = StaticUtils
		// .createJSONString(dataarrayList);

		return dataarrayList;

	}

	public static Response getRecordedAudioUrlList(
			IvrGroupzBaseMapping ivrgrpzBaseMap, ContextMapping cmap,
			String msgType, String fromValue, String size) {
		Response kkResponse = new Response();
		String responsexmlString = null;
		try {

			String formattednumber = cmap.getCallerId();

			int memberid = cmap.getmemberId();

			String memberCode = cmap.getmemberCode();

			String groupzCode = cmap.getgroupzCode();
			int playspeed = ivrgrpzBaseMap.getplayspeed();

			int timeout = ivrgrpzBaseMap.getsettimeout();

			ArrayList<String> dataList = new ArrayList<String>();
			String ipAddress = cmap.getipAddress();

			String servicetype = prop.getProperty("servicetype");
			String urltoinvoke = prop.getProperty("getrecordedAudioUrlList");
			String functiontype = prop
					.getProperty("getRecordedAduioUrlFunctype");
			String downloadURL = prop.getProperty("getAudioURL");

			String fileExtn = prop.getProperty("fileExtn");
			String endSelectionUrl = ivrgrpzBaseMap.getselectionEndUrl();
			String endSelectionText = ivrgrpzBaseMap.getselectionEndNotes();
			String listOfnumberMesgs = ivrgrpzBaseMap
					.getListOfnumbersRecordMsgText();
			String nextMsgsText = ivrgrpzBaseMap.getNextRecordedMsgOptionText();
			JSONObject datanumMsgObj = (JSONObject) JSONSerializer
					.toJSON(listOfnumberMesgs);
			JSONArray jsnMsglistArrayObj = datanumMsgObj
					.getJSONArray("dataList");
			String nextMsgVal = prop.getProperty("nextMessageset");

			JSONObject dataJson = new JSONObject();
			JSONObject contactJson = new JSONObject();
			JSONObject requestJson = new JSONObject();
			JSONObject numbRecords = new JSONObject();

			String[] numberlist = null;
			String countryCode = null;
			String mobileNumber = null;

			numberlist = formattednumber.split("\\.");
			countryCode = numberlist[0];
			mobileNumber = numberlist[1];

			contactJson.put("countrycode", countryCode);
			contactJson.put("mobilenumber", mobileNumber);
			dataJson.put("mobile", contactJson);

			numbRecords.put("from", fromValue);
			numbRecords.put("size", size);
			numbRecords.put("type", msgType);

			dataJson.put("servicetype", servicetype);
			dataJson.put("functiontype", functiontype);
			dataJson.put("groupzcode", groupzCode);
			dataJson.put("membercode", memberCode);
			dataJson.put("memberid", memberid);
			dataJson.put("ipaddress", ipAddress);
			dataJson.put("data", numbRecords);

			requestJson.put("request", dataJson);

			XMLSerializer serializer = new XMLSerializer();

			JSON jsonadd = JSONSerializer.toJSON(requestJson);
			serializer.setRootName("xml");
			serializer.setTypeHintsEnabled(false);

			String xmlString = serializer.write(jsonadd);

			System.out.println("XML String : " + xmlString);

			responsexmlString = StaticUtils.ConnectAndGetResponse(urltoinvoke,
					xmlString);

			System.out.println("response xml : " + responsexmlString);

			XMLSerializer xmlSerializer = new XMLSerializer();
			JSON json = xmlSerializer.read(responsexmlString);

			System.out.println("JSON STRING" + json.toString());

			JSONObject jo = (JSONObject) JSONSerializer.toJSON(json);
			JSONObject jores = (JSONObject) jo.get("response");
			String statusCode = jores.getString("statuscode");
			if (statusCode.equals("0")) {

				String fromVal = jores.getString("from");
				int fromValint = Integer.parseInt(fromVal);
				String totalNumberMsgs = jores.getString("total");
				int totalValint = Integer.parseInt(totalNumberMsgs);
				int sizeint = Integer.parseInt(size);
				int numberOfmsgsFetched = fromValint + sizeint;

				JSONObject joData = jores.getJSONObject("data");

				JSONObject selectionList = new JSONObject();
				JSONObject selectionOptionList = new JSONObject();

				JSONObject selectionDataList = new JSONObject();
				Object obj = joData.get("announcementlist");

				if (obj instanceof JSONArray) {

					JSONArray jsnlistarry = joData
							.getJSONArray("announcementlist");

					for (int i = 0; i < jsnlistarry.size(); i++) {

						JSONObject joadudioList = jsnlistarry.getJSONObject(i);

						String referenceid = joadudioList
								.getString("referenceid");

						JSONObject joUrls = joadudioList
								.getJSONObject("urllist");
						Iterator<?> urlkeys = joUrls.keys();
						while (urlkeys.hasNext()) {
							String keyname = (String) urlkeys.next();
							String value = joUrls.getString(keyname);
							
							GroupzKey gt = new GroupzKey(ipAddress, "1234",
									(short) 1, value);
							String groupzKey = gt.encode();
							String url = downloadURL + groupzKey + fileExtn;
							selectionDataList.put(referenceid, url);
						}
						selectionOptionList.put(i + "", selectionDataList);
						String msg = jsnMsglistArrayObj.getString(i);
						dataList.add(msg);

					}

					selectionList.put("selectionList", selectionOptionList);

					if (numberOfmsgsFetched != totalValint) {

						dataList.add(nextMsgsText);

						JSONObject getnextMsgsetObject = new JSONObject();

						String numberOfmsgsFetchedStr = Integer
								.toString(numberOfmsgsFetched);
						getnextMsgsetObject.put("from", numberOfmsgsFetchedStr);
						getnextMsgsetObject.put("size", size);
						getnextMsgsetObject.put("type", msgType);

						selectionOptionList
								.put(nextMsgVal, getnextMsgsetObject);

					} else {
						cmap.setEndOfMsgs(true);
						cmap.save();
					}
				} else {

					JSONObject singleobj = joData
							.getJSONObject("announcementlist");
					String referenceid = singleobj.getString("referenceid");

					JSONObject urlOBJ = singleobj.getJSONObject("announcement");

					System.out.println("urlobj :" + urlOBJ);

					JSONObject joUrls = urlOBJ.getJSONObject("urllist");

					System.out.println("urlobj :" + joUrls);

					Iterator<?> urlkeys = joUrls.keys();
					while (urlkeys.hasNext()) {
						String keyname = (String) urlkeys.next();
						String value = joUrls.getString(keyname);
						
						GroupzKey gt = new GroupzKey(ipAddress, "1234",
								(short) 1, value);
						String groupzKey = gt.encode();
						String url = downloadURL + groupzKey + fileExtn;
						dataList.add(url);
					}

					String referenceidList = cmap.getrecordreferenceIdList();
					if (StaticUtils.isEmptyOrNull(referenceidList)) {
						cmap.setrecordreferenceIdList(referenceid);
					} else {
						referenceidList = referenceidList + "," + referenceid;
						cmap.setrecordreferenceIdList(referenceidList);
					}

					cmap.setEndOfMsgs(true);
					cmap.save();
				}

				if (StaticUtils.isEmptyOrNull(endSelectionUrl) == false) {

					dataList.add(endSelectionUrl);

				} else {

					JSONObject recoptObj = (JSONObject) JSONSerializer
							.toJSON(endSelectionText);

					JSONArray recoptListObj = recoptObj
							.getJSONArray("dataList");
					for (int i = 0; i < recoptListObj.size(); i++) {

						dataList.add(recoptListObj.getString(i));

					}
				}

				String displayList = StaticUtils.createJSONString(dataList);

				cmap.setcontextdisplayList(displayList);
				cmap.setcontextselectionList(selectionOptionList.toString());
				cmap.save();

				kkResponse = StaticUtils.processUrlOrTextMultiList(displayList,
						playspeed, timeout);

			}

		} catch (Exception e) {

			logger.info(
					"Exception occured while getting list of recorded messages and test.",
					e);

			return kkResponse;

		}

		return kkResponse;
	}

	public static Response getMessageSummary(
			IvrGroupzBaseMapping ivrgrpzBaseMap, ContextMapping cmap) {
		Response kkResponse = new Response();

		String responsexmlString = null;
		try {

			String formattednumber = cmap.getCallerId();

			int memberid = cmap.getmemberId();

			String memberCode = cmap.getmemberCode();

			String groupzCode = cmap.getgroupzCode();
			int playspeed = ivrgrpzBaseMap.getplayspeed();

			int timeout = ivrgrpzBaseMap.getsettimeout();

			String oldmsgSel = prop.getProperty("oldMsgSelct");
			String newmsgSel = prop.getProperty("neMsgSelect");

			ArrayList<String> dataList = new ArrayList<String>();

			String servicetype = prop.getProperty("servicetype");
			String urltoinvoke = prop.getProperty("getrecordedAudioUrlList");
			String functiontype = prop.getProperty("getMessageSummaryFunctype");

			String endSelectionUrl = ivrgrpzBaseMap.getselectionEndUrl();
			String endSelectionText = ivrgrpzBaseMap.getselectionEndNotes();

			String ipAddress = cmap.getipAddress();

			JSONObject dataJson = new JSONObject();
			JSONObject contactJson = new JSONObject();
			JSONObject requestJson = new JSONObject();

			String[] numberlist = null;
			String countryCode = null;
			String mobileNumber = null;

			numberlist = formattednumber.split("\\.");
			countryCode = numberlist[0];
			mobileNumber = numberlist[1];

			contactJson.put("countrycode", countryCode);
			contactJson.put("mobilenumber", mobileNumber);
			dataJson.put("mobile", contactJson);

			dataJson.put("servicetype", servicetype);
			dataJson.put("functiontype", functiontype);
			dataJson.put("groupzcode", groupzCode);
			dataJson.put("membercode", memberCode);
			dataJson.put("memberid", memberid);
			dataJson.put("ipaddress", ipAddress);

			requestJson.put("request", dataJson);

			XMLSerializer serializer = new XMLSerializer();

			JSON jsonadd = JSONSerializer.toJSON(requestJson);
			serializer.setRootName("xml");
			serializer.setTypeHintsEnabled(false);

			String xmlString = serializer.write(jsonadd);

			System.out.println("XMLString : " + xmlString);

			responsexmlString = StaticUtils.ConnectAndGetResponse(urltoinvoke,
					xmlString);

			System.out.println("XMLString Response : " + responsexmlString);

			XMLSerializer xmlSerializer = new XMLSerializer();
			JSON json = xmlSerializer.read(responsexmlString);

			System.out.println("JSON STRING" + json.toString());

			JSONObject jo = (JSONObject) JSONSerializer.toJSON(json);
			JSONObject jores = (JSONObject) jo.get("response");
			String statusCode = jores.getString("statuscode");
			if (statusCode.equals("0")) {

				JSONObject joData = jores.getJSONObject("data");

				String oldMsg = joData.getString("old");
				String newMsg = joData.getString("new");

				if (StaticUtils.isEmptyOrNull(newMsg)
						&& StaticUtils.isEmptyOrNull(oldMsg)) {
					String noNewMsg = ivrgrpzBaseMap.getNoRecMessages();
					kkResponse.addPlayText(noNewMsg);
					kkResponse.addHangup();
					return kkResponse;
				}

				else if (StaticUtils.isEmptyOrNull(newMsg)
						&& StaticUtils.isEmptyOrNull(oldMsg) == false) {

					String noNewMsg = ivrgrpzBaseMap.getNoNewmessagesText();

					dataList.add(noNewMsg);

					String oldMsgtext = ivrgrpzBaseMap
							.getOldRecordMsgTitleText();

					JSONObject oldMsgObj = (JSONObject) JSONSerializer
							.toJSON(oldMsgtext);

					JSONArray optListObj = oldMsgObj.getJSONArray("dataList");
					for (int i = 0; i < optListObj.size(); i++) {

						dataList.add(optListObj.getString(i));

						if (i == 0) {

							dataList.add(oldMsg);
						}

					}

					dataList.add("Press " + oldmsgSel + " for old messages");

				} else if (StaticUtils.isEmptyOrNull(newMsg) == false
						&& StaticUtils.isEmptyOrNull(oldMsg)) {
					String newMsgtext = ivrgrpzBaseMap
							.getNewRecordMsgTitleText();

					JSONObject newMsgObj = (JSONObject) JSONSerializer
							.toJSON(newMsgtext);

					JSONArray optListObj = newMsgObj.getJSONArray("dataList");
					for (int i = 0; i < optListObj.size(); i++) {

						dataList.add(optListObj.getString(i));

						if (i == 0) {

							dataList.add(newMsg);
						}

					}

					dataList.add("Press " + newmsgSel + " for new messages");
				}

				else {
					String newMsgtext = ivrgrpzBaseMap
							.getNewRecordMsgTitleText();

					JSONObject newMsgObj = (JSONObject) JSONSerializer
							.toJSON(newMsgtext);

					JSONArray newoptListObj = newMsgObj
							.getJSONArray("dataList");

					for (int i = 0; i < newoptListObj.size(); i++) {

						dataList.add(newoptListObj.getString(i));

						if (i == 0) {

							dataList.add(newMsg);
						}

					}

					String oldMsgtext = ivrgrpzBaseMap
							.getOldRecordMsgTitleText();

					JSONObject oldMsgObj = (JSONObject) JSONSerializer
							.toJSON(oldMsgtext);

					JSONArray oldoptListObj = oldMsgObj
							.getJSONArray("dataList");
					for (int i = 0; i < oldoptListObj.size(); i++) {

						dataList.add(oldoptListObj.getString(i));

						if (i == 0) {

							dataList.add(oldMsg);
						}

					}

					dataList.add("Press " + newmsgSel + " for new messages");
					dataList.add("Press " + oldmsgSel + " for old messages");

				}

				if (StaticUtils.isEmptyOrNull(endSelectionUrl) == false) {

					dataList.add(endSelectionUrl);

				} else {

					JSONObject recoptObj = (JSONObject) JSONSerializer
							.toJSON(endSelectionText);

					JSONArray recoptListObj = recoptObj
							.getJSONArray("dataList");
					for (int i = 0; i < recoptListObj.size(); i++) {

						dataList.add(recoptListObj.getString(i));

					}
				}

				String displayList = StaticUtils.createJSONString(dataList);

				cmap.setcontextdisplayList(displayList);
				cmap.setMessgSummaryFlag(true);
				cmap.save();

				kkResponse = StaticUtils.processUrlOrTextMultiList(displayList,
						playspeed, timeout);

			} else {
				logger.info("There is error while getting message summary");

				String ivrNumber = cmap.getIvrNumber();
				String callSessionId = cmap.getCallSessionId();
				kkResponse = StaticUtils.senderrorResp(callSessionId,
						ivrNumber, cmap);

				kkResponse.setSid(callSessionId);
				return kkResponse;
			}

		} catch (Exception e) {

			logger.info(
					"Exception occured while getting message summary.",
					e);

			return kkResponse;

		}

		return kkResponse;
	}

	public static void markAsRead(String referenceid,
			IvrGroupzBaseMapping ivrgrpzBaseMap, ContextMapping cmap) {

		try {

			String formattednumber = cmap.getCallerId();

			int memberid = cmap.getmemberId();

			String memberCode = cmap.getmemberCode();

			String groupzCode = cmap.getgroupzCode();

			String servicetype = prop.getProperty("servicetype");
			String urltoinvoke = prop.getProperty("getrecordedAudioUrlList");
			String functiontype = prop.getProperty("markingmsgreadfunctiontype");

			String ipAddress = cmap.getipAddress();

			JSONObject dataJson = new JSONObject();
			JSONObject contactJson = new JSONObject();
			JSONObject requestJson = new JSONObject();

			String[] numberlist = null;
			String countryCode = null;
			String mobileNumber = null;

			numberlist = formattednumber.split("\\.");
			countryCode = numberlist[0];
			mobileNumber = numberlist[1];

			contactJson.put("countrycode", countryCode);
			contactJson.put("mobilenumber", mobileNumber);
			dataJson.put("mobile", contactJson);

			dataJson.put("servicetype", servicetype);
			dataJson.put("functiontype", functiontype);
			dataJson.put("groupzcode", groupzCode);
			dataJson.put("membercode", memberCode);
			dataJson.put("memberid", memberid);
			dataJson.put("ipaddress", ipAddress);

			requestJson.put("request", dataJson);

			XMLSerializer serializer = new XMLSerializer();

			JSON jsonadd = JSONSerializer.toJSON(requestJson);
			serializer.setRootName("xml");
			serializer.setTypeHintsEnabled(false);

			String xmlString = serializer.write(jsonadd);

			System.out.println("XMLString : " + xmlString);

			StringBuffer sendStr = new StringBuffer();
			sendStr.append("request=");
			sendStr.append(xmlString);
			String sendString = sendStr.toString();

			URL url = new URL(urltoinvoke);

			URLConnection connection = url.openConnection();

			connection.setDoOutput(true);
	
			OutputStreamWriter outputWriter = new OutputStreamWriter(
					connection.getOutputStream());
			outputWriter.write(sendString);
			outputWriter.close();
			

		} catch (Exception e) {

			logger.info(
					"Exception occured while sending request for making message read.",
					e);

		}

	}

	public static void main(String[] args) {

	}
}