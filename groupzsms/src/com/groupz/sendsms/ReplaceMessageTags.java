package com.groupz.sendsms;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class ReplaceMessageTags {

	public static String changeMessageText(String message, String jsonString) {
		String modifyMsg = message;

		JSONObject addressObj = (JSONObject) JSONSerializer.toJSON(jsonString);

		String name = "";
		String nameprefix = "";
		String contactName = "";
		String contactPrefix = "";
		String emailStr = "";
		String number = "";

		if (addressObj != null) {
			if (addressObj.containsKey("name")) {
				String check = addressObj.getString("name");
				if (check.isEmpty() == false && check.equals("[]") == false) {
					name = addressObj.getString("name");
				}
			}

			if (addressObj.containsKey("prefix")) {
				String check = addressObj.getString("prefix");
				if (check.isEmpty() == false && check.equals("[]") == false) {
					nameprefix = addressObj.getString("prefix");
				}
			}

			if (addressObj.containsKey("contactpersonname")) {
				String check = addressObj.getString("contactpersonname");
				if (check.isEmpty() == false && check.equals("[]") == false) {
					contactName = addressObj.getString("contactpersonname");
				}
			}

			if (addressObj.containsKey("contactpersonprefix")) {
				String check = addressObj.getString("contactpersonprefix");
				if (check.isEmpty() == false && check.equals("[]") == false) {
					contactPrefix = addressObj.getString("contactpersonprefix");
				}
			}

			if (addressObj.containsKey("email")) {
				String check = addressObj.getString("email");
				if (check.isEmpty() == false && check.equals("[]") == false) {
					emailStr = addressObj.getString("email");
				}
			}

			if (addressObj.containsKey("number")) {
				String check = addressObj.getString("number");
				if (check.isEmpty() == false && check.equals("[]") == false) {
					number = addressObj.getString("number");
				}
			}

			modifyMsg = modifyMsg.replace("$Name", name);
			modifyMsg = modifyMsg.replace("$Prefix", nameprefix);
			modifyMsg = modifyMsg.replace("$ContactPersonName", contactName);
			modifyMsg = modifyMsg
					.replace("$ContactPersonPrefix", contactPrefix);
			modifyMsg = modifyMsg.replace("$Email", emailStr);
			modifyMsg = modifyMsg.replace("$Number", number);
		}

		return modifyMsg;

	}



}
