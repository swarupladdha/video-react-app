package utils;

import manager.GlobalVariables;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class RestUtils {
	public RestUtils() {
	}

	public boolean isEmpty(String test) {
		if ((test == null) || (test.trim().isEmpty() == true)
				|| (test.equalsIgnoreCase("[]")) || (test == "")) {
			return false;
		}
		return true;
	}

	public String processError(String statusCode, String message) {
		String errorJSONString = new String();
		JSONObject errorJSON = new JSONObject();
		errorJSON.put("statuscode", statusCode);
		errorJSON.put("statusmessage", message);
		errorJSONString = errorJSON.toString();
		return errorJSONString;
	}

	public String paginationQry(int limit, int offset) {
		String paginationQry = "";

		if ((limit != -1) && (offset != -1)) {
			paginationQry = " limit " + limit + " offset " + offset;
		}

		if ((limit != -1) && (offset == -1)) {
			paginationQry = " limit " + limit;
		}

		if ((limit == -1) && (offset != -1)) {
			paginationQry = " offset " + offset;
		}

		return paginationQry;
	}

	public boolean isJSONValid(String jsonString) {
		boolean valid = true;
		JSONObject obj = new JSONObject();
		try {
			obj = (JSONObject) JSONSerializer.toJSON(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
			valid = false;
		}

		return valid;
	}

	public String processSuccessWithJsonArray(int serviceType,
			int functionType, JSONArray dataArray) {
		JSONObject obj = new JSONObject();
		obj.put("statuscode", "success");
		obj.put(GlobalVariables.SERVICE_TYPE_TAG, serviceType);
		obj.put(GlobalVariables.FUNCTION_TYPE_TAG, functionType);
		obj.put("data", dataArray);
		return obj.toString();
	}

	public String processSuccessWithJsonObject(int serviceType,
			int functionType, JSONObject dataObject) {
		JSONObject obj = new JSONObject();
		obj.put("statuscode", "success");
		obj.put(GlobalVariables.SERVICE_TYPE_TAG, serviceType);
		obj.put(GlobalVariables.FUNCTION_TYPE_TAG, functionType);
		obj.put("data", dataObject);
		return obj.toString();
	}

}