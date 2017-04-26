package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.CharacterIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;
import java.util.TimeZone;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class RestUtils {

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

	public boolean isEmpty(String test) {
		if (test == null || test.trim().isEmpty() == true
				|| test.equalsIgnoreCase("[]") || test == "") {
			return true;
		}
		return false;
	}

	public Date getLastSynchTime() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcTime = f.format(new Date());
		System.out.println("String date:" + utcTime);
		Date lastSynch = StringDateToDate(utcTime);
		System.out.println("Date :" + lastSynch);
		return lastSynch;
	}

	public static Date StringDateToDate(String StrDate) {
		String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
		Date dateToReturn = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
		try {
			dateToReturn = (Date) dateFormat.parse(StrDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateToReturn;
	}

	public String generateOTP() {
		String generatedOTP = "";
		long timeNow = System.currentTimeMillis();
		generatedOTP = String.valueOf(timeNow);
		String genOtp = generatedOTP.substring(9, 13);
		return genOtp;
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

	public String encrypt(String Password) {
		String generatedPassword = null;
		if (Password == null || Password.length() == 0) {
			return generatedPassword;
		}
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(Password.getBytes());
			byte[] bytes = md.digest();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;

	}

	public String processSuccess() {
		String response = "";
		JSONObject sucessJSON = new JSONObject();
		JSONObject sucessRespJSON = new JSONObject();
		JSONObject contentJSON = new JSONObject();

		contentJSON.put("statuscode",
				PropertiesUtil.getProperty("statuscodesuccessvalue"));
		contentJSON.put("statusmessage",
				PropertiesUtil.getProperty("statusmessagesuccessvalue"));
		sucessRespJSON.put("response", contentJSON);
		sucessJSON.put("json", sucessRespJSON);
		return sucessJSON.toString();

	}

	public static String encode(String s) {
		if (s == null)
			return null;
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(s);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '<') {
				result.append("&lt;");
			} else if (character == '>') {
				result.append("&gt;");
			} else if (character == '\"') {
				result.append("&quot;");
			} else if (character == '\'') {
				result.append("&#039;");
			} else if (character == '&') {
				result.append("&amp;");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

}
