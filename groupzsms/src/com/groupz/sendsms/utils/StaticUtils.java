package com.groupz.sendsms.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class StaticUtils {

	public static boolean checkEmptyJSONArray(Object objcheck,
			JSONObject joreq, String keyCheck) {

		boolean flag = false;
		if (objcheck instanceof JSONArray) {

			JSONArray jsnaddarry = joreq.getJSONArray(keyCheck);
			if (jsnaddarry == null || jsnaddarry.isEmpty()) {
				flag = true;
			}

		} else {
			JSONObject joaddress = (JSONObject) joreq.get(keyCheck);
			if (joaddress == null || joaddress.isEmpty()) {
				flag = true;
			}
		}
		return flag;

	}
}
