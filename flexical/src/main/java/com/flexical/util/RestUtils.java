package com.flexical.util;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class RestUtils {
	String General_date_format = "yyyy-MM-dd HH:mm:ss";
	//String General_slot

	public static final Logger logger = Logger.getLogger(RestUtils.class);
	
	public void printRequest(String path, String request) {
		System.out.println("Request received  at " + path + " is :- " + request);
	}

	public boolean isJsonValid(String jsonString) {
		boolean valid = true;
		JSONObject obj = new JSONObject();
		try {
			obj = (JSONObject) JSONSerializer.toJSON(jsonString);
			System.out.println(obj);
		} catch (Exception e) {
			e.printStackTrace();
			valid = false;
		}
		return valid;
	}

	public boolean isEmpty(String test) {
		if ((test == null) || (test.trim().isEmpty() == true) || (test.equalsIgnoreCase("[]")) || (test == "")) {
			return true;
		}
		return false;
	}

	public String processError(String statusCode, String message) {
		String errorJSONString = new String();
		JSONObject errorJSON = new JSONObject();
		JSONObject errorRespJSON = new JSONObject();
		JSONObject statusJSON = new JSONObject();
		statusJSON.put(AllKeys.STATUS_CODE, statusCode);
		statusJSON.put(AllKeys.STATUS_MESSAGE, message);
		errorRespJSON.put(AllKeys.RESPONSE, statusJSON);
		errorJSON.put(AllKeys.JSON_KEY, errorRespJSON);
		errorJSONString = errorJSON.toString();
		return errorJSONString;
	}

	public boolean isDateTimeValid(String strDate) {
		if (strDate.matches("\\d{4}:\\d{2}:\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
			return true;
		}
		return false;
	}

	public boolean isDateValid(String strDate) {
		if (strDate.matches("\\d{4}:\\d{2}:\\d{2}")) {
			return true;
		}
		return false;
	}

	public boolean isTimeValid(String strDate) {
		if (strDate.matches("\\d{2}:\\d{2}:\\d{2}")) {
			return true;
		}
		return false;
	}

	public boolean isStatusValid(String text) {
		if (isEmpty(text) == false) {
			if (text.matches("[0-1]+")) {
				return true;
			}
		}
		return false;
	}

	public boolean isNumber(String text) {
		if (isEmpty(text) == false) {
			if (text.matches("[0-9]+")) {
				return true;
			}
		}
		return false;
	}

	public boolean isValidMobile(String text) {
		if (isNumber(text)) {
			return true;
		}
		return false;
	}
	
	public String processSucessForModules(String dataText, Object Obj) {
		JSONObject sucessJSON = new JSONObject();
		JSONObject sucessRespJSON = new JSONObject();
		JSONObject contentJSON = new JSONObject();
		contentJSON.put(AllKeys.STATUS_CODE, PropertiesUtil.getProperty("statuscodesuccessvalue"));

		contentJSON.put(AllKeys.STATUS_MESSAGE, PropertiesUtil.getProperty("statusmessagesuccessvalue"));

		contentJSON.put(dataText, Obj);

		sucessRespJSON.put(AllKeys.RESPONSE, contentJSON);
		sucessJSON.put(AllKeys.JSON_KEY, sucessRespJSON);
		return sucessJSON.toString();
	}

	public String processSuccessOnly() {
		JSONObject sucessJSON = new JSONObject();
		JSONObject sucessRespJSON = new JSONObject();
		JSONObject contentJSON = new JSONObject();
		contentJSON.put(AllKeys.STATUS_CODE, PropertiesUtil.getProperty("statuscodesuccessvalue"));
		contentJSON.put(AllKeys.STATUS_MESSAGE, PropertiesUtil.getProperty("statusmessagesuccessvalue"));
		sucessRespJSON.put(AllKeys.RESPONSE, contentJSON);
		sucessJSON.put(AllKeys.JSON_KEY, sucessRespJSON);
		return sucessJSON.toString();
	}

	public String genericError() {
		try {
			JSONObject errorJSON = new JSONObject();
			JSONObject errorRespJSON = new JSONObject();
			JSONObject statusJSON = new JSONObject();
			statusJSON.put(AllKeys.STATUS_CODE, PropertiesUtil.getProperty("common_error_code"));
			statusJSON.put(AllKeys.STATUS_MESSAGE, PropertiesUtil.getProperty("common_error_message"));
			errorRespJSON.put(AllKeys.RESPONSE, statusJSON);
			errorJSON.put(AllKeys.JSON_KEY, errorRespJSON);
			return errorJSON.toString();
		} catch (Exception e) {
			logger.error("Exception", e);
			return "We are facing issues! Please cooperate!";
		}
	}


}
