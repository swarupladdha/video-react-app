package com.groupz.followup.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import src.followupconfig.PropertiesUtil;


public class FollowUpUtils {

	String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

	public static boolean isJSONValid(String jsonString) {
		boolean valid = true;
		JSONObject obj = new JSONObject();

		try {
			obj = (JSONObject) JSONSerializer.toJSON(jsonString);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			valid = false;
		}

		return valid;
	}

	// bit offset calculation
	public static boolean checkBitOffset(int value, int offsetFromLeft) {
		if (offsetFromLeft < 0 || offsetFromLeft > 31)
			return false;
		int bitOffset = (offsetFromLeft % 32);
		byte reqByte = (byte) (value >>> (31 - bitOffset));

		if ((reqByte & (byte) 0x01) != 0)
			return true;
		return false;
	}

	// checking null
	public boolean isEmpty(String test) {
		if (test == null || test.trim().isEmpty() == true
				|| test.equalsIgnoreCase("[]") || test == "") {
			return false;
		}
		return true;
	}

	public Date StringDateToDate(String StrDate) {
		Date dateToReturn = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
		try {
			dateToReturn = (Date) dateFormat.parse(StrDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateToReturn;
	}

	public String getFormattedDateStr(Date date) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = null;
		if (date != null) {
			strDate = f.format(date);
			strDate.trim();
		}
		return strDate;
	}

	/*public Date getLastSynchTime() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcTime = f.format(new Date());
		System.out.println("String date:" + utcTime);
		Date lastSynch = Utils.StringDateToDate(utcTime);
		System.out.println("Date :" + lastSynch);
		return lastSynch;
	}*/

	// get latest synch time
	public String getLatestSynchTime() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcTime = f.format(new Date());
		// System.out.println("String date:"+utcTime);
		Date lastSynch = StringDateToDate(utcTime);
		String lastSynchTime = getFormattedDateStr(lastSynch);
		return lastSynchTime.trim();
	}

	// Invalid response
	public String processError(String statusCode, String message) {
		String errorJSONString = new String();
		JSONObject errorJSON = new JSONObject();
		JSONObject errorRespJSON = new JSONObject();
		JSONObject statusJSON = new JSONObject();
		statusJSON.put("statuscode", statusCode);
		statusJSON.put("statusmessage", message);
		errorRespJSON.put("response", statusJSON);
		errorJSON.put("json", errorRespJSON);
		errorJSONString = errorJSON.toString();
		return errorJSONString;

	}

	// process success
	public String processSucess(String serviceType, String functionType,
			String dataText, JSONArray dataArray) {
		JSONObject sucessJSON = new JSONObject();
		JSONObject sucessRespJSON = new JSONObject();
		JSONObject contentJSON = new JSONObject();
		contentJSON.put("servicetype", serviceType);
		contentJSON.put("functiontype", functionType);
		contentJSON.put("statuscode",
				PropertiesUtil.getProperty("statuscodesuccessvalue"));
		contentJSON.put("statusmessage",
				PropertiesUtil.getProperty("statusmessagesuccessvalue"));
		if (dataArray != null && dataArray.size() > 0) {
			contentJSON.put(dataText, dataArray);
		}
		sucessRespJSON.put("response", contentJSON);
		sucessJSON.put("json", sucessRespJSON);
		return sucessJSON.toString();

	}
	
	
	// process success
		public JSONObject processSucessMessage(String serviceType, String functionType,
				String dataText, JSONArray dataArray) {
			JSONObject sucessJSON = new JSONObject();
			JSONObject sucessRespJSON = new JSONObject();
			JSONObject contentJSON = new JSONObject();
			contentJSON.put("servicetype", serviceType);
			contentJSON.put("functiontype", functionType);
			contentJSON.put("statuscode",
					PropertiesUtil.getProperty("statuscodesuccessvalue"));
			contentJSON.put("statusmessage",
					PropertiesUtil.getProperty("statusmessagesuccessvalue"));
			if (dataArray != null && dataArray.size() > 0) {
				contentJSON.put(dataText, dataArray);
			}
			sucessRespJSON.put("response", contentJSON);
			sucessJSON.put("json", sucessRespJSON);
			return sucessJSON;

		}

	// value exists in json array
	public boolean valueExists(JSONArray jsonArray, String key, String value) {
		// System.out.println(" JSON ARRAY:"+jsonArray.toString());
		System.out.println("Status:"
				+ jsonArray.toString().contains(
						"\"" + key + "\":\"" + value + "\""));
		return jsonArray.toString().contains(
				"\"" + key + "\":\"" + value + "\"");
	}

	// return json object in country array
	public JSONObject getJSONObject(JSONArray jsonArray, String key,
			String value) {
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			System.out.println("JSON OBJ:" + json.toString());
			if (json.containsValue(value) == true) {
				System.out.println("Value present:" + value);
				return json;
			}
		}
		return new JSONObject();
	}

	public JSONArray getJSOnArray(JSONArray jsonArray, String key, String value) {
		if (jsonArray.toString().contains("\"" + key + "\":\"" + value + "\"") == true) {
			System.out.println("Json array is present");
			return jsonArray;
		}
		return new JSONArray();
	}
	
}
