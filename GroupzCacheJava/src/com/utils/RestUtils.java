package com.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class RestUtils {

	// checking null
	public static boolean isEmpty(String test) {
		if (test == null || test.trim().isEmpty() == true
				|| test.equalsIgnoreCase("[]") || test == "") {
			return false;
		}
		return true;
	}

	public String getEncryptedPassword(String password) {
		String encryptedPassword = "";

		String algorithm = "SHA";

		byte[] plainText = password.getBytes();

		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (Exception e) {
			e.printStackTrace();
		}

		md.reset();
		md.update(plainText);
		byte[] encodedPassword = md.digest();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < encodedPassword.length; i++) {
			if ((encodedPassword[i] & 0xff) < 0x10) {
				sb.append("0");
			}

			sb.append(Long.toString(encodedPassword[i] & 0xff, 16));
		}

		System.out.println("Plain     : " + password);
		System.out.println("Encrypted: " + sb.toString());
		encryptedPassword = sb.toString();
		return encryptedPassword;

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

	public static Date getLastSynchTime() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcTime = f.format(new Date());
		System.out.println("String date:" + utcTime);
		Date lastSynch = StringDateToDate(utcTime);
		System.out.println("Date :" + lastSynch);
		return lastSynch;
	}

	/*
	 * public static Date getDate() { SimpleDateFormat f = new
	 * SimpleDateFormat("dd-MM-yyyy");
	 * f.setTimeZone(TimeZone.getTimeZone("UTC")); String utcTime = f.format(new
	 * Date()); System.out.println("String date:" + utcTime); Date lastSynch =
	 * StringToDate(utcTime); System.out.println("Date :" + lastSynch); return
	 * lastSynch; }
	 */

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

	/*
	 * public static Date StringToDate(String StrDate) { String DATEFORMAT =
	 * "dd-MM-yyyy"; Date dateToReturn = null; SimpleDateFormat dateFormat = new
	 * SimpleDateFormat(DATEFORMAT); try { dateToReturn = (Date)
	 * dateFormat.parse(StrDate); } catch (ParseException e) {
	 * e.printStackTrace(); }
	 * 
	 * return dateToReturn; }
	 */

	protected void SimpleOTPGenerator() {
	}

	public boolean isValidEmail(String email) {
		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		if (email.matches(EMAIL_REGEX) == true) {
			return true;
		}
		return false;
	}

	public static String random(int size) {

		StringBuilder generatedToken = new StringBuilder();
		try {
			SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
			// Generate 20 integers 0..20
			for (int i = 0; i < size; i++) {
				generatedToken.append(number.nextInt(9));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return generatedToken.toString();
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

	public static String quoteencode(String s) {
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

	public static String processError(String statusCode, String message) {
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

	public String processSucessWithObject(String serviceType,
			String functionType, JSONObject obj) {
		JSONObject sucessJSON = new JSONObject();
		JSONObject sucessRespJSON = new JSONObject();
		JSONObject contentJSON = new JSONObject();
		contentJSON.put("servicetype", serviceType);
		contentJSON.put("functiontype", functionType);
		contentJSON.put("statuscode",
				PropertiesUtil.getProperty("statuscodesuccessvalue"));
		contentJSON.put("statusmessage",
				PropertiesUtil.getProperty("statusmessagesuccessvalue"));
		contentJSON.put("data", obj);
		sucessRespJSON.put("response", contentJSON);
		sucessJSON.put("json", sucessRespJSON);
		return sucessJSON.toString();

	}

	public String processSuccessOnly(String serviceType, String functionType) {
		JSONObject sucessJSON = new JSONObject();
		JSONObject sucessRespJSON = new JSONObject();
		JSONObject contentJSON = new JSONObject();
		contentJSON.put("servicetype", serviceType);
		contentJSON.put("functiontype", functionType);
		contentJSON.put("statuscode",
				PropertiesUtil.getProperty("statuscodesuccessvalue"));
		contentJSON.put("statusmessage",
				PropertiesUtil.getProperty("statusmessagesuccessvalue"));
		sucessRespJSON.put("response", contentJSON);
		sucessJSON.put("json", sucessRespJSON);
		return sucessJSON.toString();
	}

	// @SuppressWarnings("unused")
	public static boolean isJSONValid(String jsonString) {
		boolean valid = true;
		JSONObject obj = new JSONObject();
		try {
			obj = (JSONObject) JSONSerializer.toJSON(jsonString);
		} catch (Exception e) {
			valid = false;
			e.printStackTrace();
		}
		return valid;
	}

	public boolean isJSONValidDup(String test) {
		try {
			JSONObject obj = JSONObject.fromObject(test);
		} catch (Exception ex) {
			// edited, to include @Arthur's comment
			// e.g. in case JSONArray is valid as well...
			System.out.println("Caught exception here and Thread Id 1 : "
					+ Thread.currentThread().getId());
			try {
				JSONArray arr = JSONArray.fromObject(test);
			} catch (Exception ex1) {
				System.out.println("Caught exception here and Thread Id  2: "
						+ Thread.currentThread().getId());
				return false;
			}
		}
		return true;
	}

	public String getCategoriesSuccess(String dataPartStr, Object Obj,
			String lastTimeSynch) {
		JSONObject sucessJSON = new JSONObject();
		JSONObject sucessRespJSON = new JSONObject();
		JSONObject contentJSON = new JSONObject();
		contentJSON.put("statuscode",
				PropertiesUtil.getProperty("statuscodesuccessvalue"));
		contentJSON.put("statusmessage",
				PropertiesUtil.getProperty("statusmessagesuccessvalue"));
		if (Obj instanceof JSONArray) {
			contentJSON.put(dataPartStr, Obj);
		} else if (Obj instanceof JSONObject) {
			contentJSON.put(dataPartStr, Obj);
		}
		contentJSON.put("lastsynchtime", lastTimeSynch);
		sucessRespJSON.put("response", contentJSON);
		sucessJSON.put("json", sucessRespJSON);
		return sucessJSON.toString();

	}

	public String formVerificationEmail(String validationLink) {
		String verificationEmailStr = "";

		String text1 = "Welcome to JOBZTOP";
		String text2 = "To activate your account and verify your email address, please click on the following link:";
		String text3 = "If you have received this mail by mistake, please ignore.";
		String text4 = "Sincerely,";
		String text5 = "Jobztop Team";

		String htmlLink = "<a href=\"" + validationLink + "\"> click here</a>";

		verificationEmailStr = "<html><body> " + text1 + "<br/><br/>" + text2
				+ "<br/><br/>" + htmlLink + "<br/><br/>" + text3 + "<br/><br/>"
				+ text4 + "<br/>" + text5 + " </body></html>";
		return verificationEmailStr;
	}

	public String formResetPasswordEmail(String validationLink) {
		String verificationEmailStr = "";

		String text1 = "Hi";
		String text2 = "A password reset was requested for your Jobztop account.";
		String text3 = "Please click the link below to set the new password.";
		String text4 = "If you have received this mail by mistake, please ignore.";
		String text5 = "Sincerely,";
		String text6 = "Jobztop Team";

		String htmlLink = "<a href=\"" + validationLink + "\"> click here</a>";

		verificationEmailStr = "<html><body> " + text1 + "<br/><br/>" + text2
				+ "<br/><br/>" + text3 + "<br/><br/>" + htmlLink + "<br/><br/>"
				+ text4 + "<br/><br/>" + text5 + "<br/>" + text6
				+ " </body></html>";
		return verificationEmailStr;
	}

	public String paginationQry(int limit, int offset) {
		String paginationQry = "";

		if (limit != -1 && offset != -1) {
			paginationQry = " limit " + limit + " offset " + offset;

		}
		if (limit != -1 && offset == -1) {
			paginationQry = " limit " + limit;

		}
		if (limit == -1 && offset != -1) {
			paginationQry = " offset " + offset;

		}
		return paginationQry;
	}

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
			"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	public boolean validateEmail(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}

	@SuppressWarnings("static-access")
	public Date addMinutesToTime(Date startTime, int minutes) {
		Date endTime = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
		cal.add(cal.MINUTE, minutes);
		endTime = cal.getTime();
		return endTime;

	}

	public static String formSendOtpJson(String mobile, String Cc) {
		String jsonString = new String();
		JSONObject json = new JSONObject();
		JSONObject requestJson = new JSONObject();
		JSONObject mobileJson = new JSONObject();
		JSONObject JSON = new JSONObject();
		JSON.put("mobilenumber", mobile);
		JSON.put("countrycode", Cc);
		mobileJson.put("mobile", JSON);
		requestJson.put("request", mobileJson);
		json.put("json", requestJson);
		jsonString = json.toString();
		return jsonString;

	}

	public String formVerifyOtpJson(String mobile, String Cc, String otp) {
		String jsonString = new String();
		JSONObject json = new JSONObject();
		JSONObject requestJson = new JSONObject();
		JSONObject mobileJson = new JSONObject();
		JSONObject JSON = new JSONObject();
		mobileJson.put("otp", otp);
		JSON.put("mobilenumber", mobile);
		JSON.put("countrycode", Cc);
		mobileJson.put("mobile", JSON);
		requestJson.put("request", mobileJson);
		json.put("json", requestJson);
		jsonString = json.toString();
		return jsonString;

	}

	public JSONObject getMobile(JSONObject obj, String mobile) {
		String[] parts = mobile.split("\\+");
		String part2 = parts[1];
		String mobile1 = part2;
		String[] dotSplit = mobile1.split("\\.");
		String cc = dotSplit[0];
		String Mobile = dotSplit[1];
		obj.put("mobile", Mobile);
		obj.put("countrycode", cc);
		return obj;

	}

	public static String getConcatenatedJSONids(JSONArray array) {
		String concatenatedString = "";
		if (array.size() == 1) {
			concatenatedString = "'" + array.get(0) + "'";
			System.out.println("-------" + array.get(0));
			return concatenatedString;
		}
		for (Object rol : array) {
			concatenatedString += "'" + rol + "',";
		}
		return concatenatedString.substring(0, concatenatedString.length() - 1);

	}

	@SuppressWarnings("rawtypes")
	public static String getConcatenatedInt(List array) {
		String concatenatedString = "";
		if (array.size() == 1) {
			concatenatedString = "" + array.get(0);
			return concatenatedString;
		}

		for (Object rol : array) {
			concatenatedString += rol + ",";
		}

		return concatenatedString.substring(0, concatenatedString.length() - 1);

	}

	@SuppressWarnings("rawtypes")
	public static String getConcatenatedString(List array) {
		String concatenatedString = "";
		if (array.size() == 1) {
			concatenatedString = "'" + array.get(0) + "'";
			return concatenatedString;
		}

		for (Object rol : array) {
			concatenatedString += "'" + rol + "',";
		}

		return concatenatedString.substring(0, concatenatedString.length() - 1);

	}

	@SuppressWarnings("deprecation")
	public static int getMonthsDifference(String yoj, String yor) {
		try {
			DateFormat formatter = new SimpleDateFormat("MMM-yyyy");
			DateFormat formatter1 = new SimpleDateFormat("MMM -yyyy");
			Date yoJoi = (Date) formatter.parse(yoj);
			Date yoRel = (Date) formatter1.parse(yor);
			int m1 = yoJoi.getYear() * 12 + yoJoi.getMonth();
			int m2 = yoRel.getYear() * 12 + yoRel.getMonth();
			return m2 - m1;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static String getExperience(int experience) {
		int year = experience / 12;
		int month = experience % 12;
		String actualExperience = "";
		String yearsEx = "";
		String monthsEx = "";
		if (year > 0) {
			if (year > 1) {
				yearsEx = year + " Years";
			} else {
				yearsEx = year + " Year";
			}
		}

		if (month > 0) {
			if (month > 1) {
				monthsEx = month + " Months";
			} else {
				monthsEx = month + " Months";
			}
		}

		actualExperience = yearsEx + " " + monthsEx;
		return actualExperience;
	}

	/*
	 * public boolean validate(String str) { boolean valid = true; try {
	 * JSONParser p = new JSONParser(); p.parse(str); } catch (Exception jse) {
	 * valid = false; System.out.println("Not a valid Json String:" +
	 * jse.getMessage()); } System.out.println("Is Json Valid : " + valid);
	 * return valid; }
	 */
}
