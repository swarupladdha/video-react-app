package com.infosoft.utils;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class Utils {
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

	public String invalidJsonError() {
		try {
			JSONObject main = new JSONObject();
			JSONObject response = new JSONObject();
			JSONObject data = new JSONObject();
			data.put(AllKeys.STATUS_MESSAGE, "invalid json");
			response.put(AllKeys.DATA, data);
			main.put(AllKeys.RESPONSE, response);
			return main.toString();
		} catch (Exception e) {
			return "We are facing issues! Please cooperate!";
		}
	}

	public String successResponse() {
		try {
			JSONObject main = new JSONObject();
			JSONObject response = new JSONObject();
			JSONObject data = new JSONObject();
			data.put(AllKeys.STATUS_MESSAGE, "Your call has been initiated, Please wait "
					+ "we are connecting you !.");
			response.put(AllKeys.DATA, data);
			main.put(AllKeys.RESPONSE, response);
			return main.toString();
		} catch (Exception e) {
			return "We are connecting you! Please wait!";
		}
	}

}
