package utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

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

}