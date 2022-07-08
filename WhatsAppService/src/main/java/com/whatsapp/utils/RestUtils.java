package com.whatsapp.utils;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class RestUtils {
	public boolean isEmpty(JSONObject obj) {
		if ((obj == null) || obj.isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean isEmpty(String test) {
		if ((test == null) || (test.trim().isEmpty() == true) || (test.equalsIgnoreCase("[]")) || (test == "")) {
			return true;
		}
		return false;
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

}
