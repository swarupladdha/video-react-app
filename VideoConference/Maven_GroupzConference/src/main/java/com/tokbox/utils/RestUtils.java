package com.tokbox.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

public class RestUtils {

	static String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

	// JSON VALID

	public static JSONObject isJSONValid(String jsonString) {
		JSONObject obj = null;

		try {
			obj = (JSONObject) JSONSerializer.toJSON(jsonString);
			return obj ;

		} catch (Exception e) {
//			System.out.println(obj);
//			e.printStackTrace();
			return obj;
		}
	}

	public static  String checkDobIsAfterCurrentDate(java.sql.Date dob) {
		if (dob.after(getCurrentDate())) {
			String error = RestUtils.processError(PropertiesUtil.getProperty("invalid_dob_code"),PropertiesUtil.getProperty("invalid_dob_message"));
			return error;
		}
		return null;
	}







	public static boolean checkUserNotMinor(java.sql.Date dob) {
			
		Calendar c = Calendar.getInstance();
	    c.setTime(dob);
	    c.add(Calendar.YEAR,18);		
		System.out.println(c.getTime().after(new Date()));
		if (c.getTime().after(new Date())) {
			return true;
			
		}
		if (c.getTime().before(new Date())) {
			return false;
		}
		return true;
	}

	public static  Date getCurrentDate() {
		String d = RestUtils.getFormattedDateStr(new Date());
		String cDate= RestUtils.getCurrentDateddmmyyyy();
		Date curDate = RestUtils.getDate(cDate.replaceAll("-", "/"));
	
		return curDate;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static String containsAllMandatoryFields( JSONObject data, JSONArray mandatoryfields){

		for (int i = 0; i < mandatoryfields.size(); i++) {
			String info=mandatoryfields.getString(i);
			if (!RestUtils.isEmpty(info) == false) {
				try{
					String fieldValue = data.getString(info).toString() ;
					if( !RestUtils.isEmpty(fieldValue) == true){
						String registrationResponse = RestUtils.processError(info+" "+PropertiesUtil.getProperty("field_isempty_code"), info +" " + PropertiesUtil.getProperty("field_isempty_message"));
						return registrationResponse;		
					}
				}
				catch(Exception e){
					String registrationResponse = RestUtils.processError(info+" "+PropertiesUtil.getProperty("field_notpresent_code"), info +" " + PropertiesUtil.getProperty("field_notpresent_message"));
					return registrationResponse;		
				}
			}
		}
		return null ;
	}
	
	
	public static String containsforupdateMandatoryFields( JSONObject data, JSONArray mandatoryfields){

		for (int i = 0; i < mandatoryfields.size(); i++) {
			System.out.println(mandatoryfields.getString(i));
			String info=mandatoryfields.getString(i);
			if (!RestUtils.isEmpty(info) == false) {
				
				try{
				String fieldValue = data.getString(info).toString() ;
				if( !RestUtils.isEmpty(fieldValue) == true){
					String registrationResponse = RestUtils.processError(info+" "+PropertiesUtil.getProperty("field_isempty_code"), info +" " + PropertiesUtil.getProperty("field_isempty_message"));
					return registrationResponse;		
				}
				}
				catch(Exception e)
				{
					String registrationResponse = RestUtils.processError(info+" "+PropertiesUtil.getProperty("field_notpresent_code"), info +" " + PropertiesUtil.getProperty("field_notpresent_message"));
					return registrationResponse;		
				}
			}
		}
		return null ;
	}
	
/*	public static String getOtpNotValidatedResponse(String serviceType, String functionType, JSONObject existingProfile,String session_id) {
		JSONObject prObject = new JSONObject();
		JSONObject responseJsonObject = new JSONObject();
		prObject.put(TokBoxInterfaceKeys.otpvalidation, existingProfile.getString(TokBoxInterfaceKeys.otpvalidation));
		prObject.put(TokBoxInterfaceKeys.message, PropertiesUtil.getProperty("otp_notvalidated_message"));
		responseJsonObject.put(TokBoxInterfaceKeys.profileInfo, prObject);
		String message = RestUtils.processSucess(serviceType, functionType, responseJsonObject,session_id);
		return message;
	}

	public static String getEmailNotValidatedResponse(String serviceType,	String functionType, JSONObject existingProfile,String session_id) {
		JSONObject prObject = new JSONObject();
		JSONObject responseJsonObject = new JSONObject();
		prObject.put(TokBoxInterfaceKeys.emailvalidation, existingProfile.getString(TokBoxInterfaceKeys.emailvalidation));
		prObject.put(TokBoxInterfaceKeys.message, PropertiesUtil.getProperty("email_notvalidated_message"));
		responseJsonObject.put(TokBoxInterfaceKeys.profileInfo, prObject);
		String message = RestUtils.processSucess(serviceType, functionType, responseJsonObject,session_id);
		return message;
	}
*/
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
	public static boolean isEmpty(String test) {
		try{
			if (test == null ||	test.trim().isEmpty() == true	|| test.equalsIgnoreCase("[]") || test == "" || test.equalsIgnoreCase("null") || test.equalsIgnoreCase("[null]")) {
				return false;
				//empty
			}
		}catch(Exception e){
			
		}
		return true;
	}
	
	public static String getJSONStringValue( JSONObject json, String key) {
		String value = null ;
		if( json != null && isEmpty(key) == true){
			try{
				value = json.getString(key) ;
				return value;
			}catch(Exception e){
			}
			return null ;
		}
		return null ;
	}

	// checking valid email
	

	// checking time between 1 hour
	public boolean checktimeWithinHour(Date d1, Date d2) {
		try {
			// in milliseconds
			long diff = d2.getTime() - d1.getTime();
			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			if ((diffDays == 0 && diffHours == 0 && diffMinutes <= 59 && diffSeconds <= 59)
					|| (diffDays == 0 && diffHours == 1 && diffMinutes == 0 && diffSeconds == 0)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String formQueryforJsonValue(String updateRoleInfoSQL, String tablefieldname,JSONObject newvalue, JSONObject existingvalue) {
		System.out.println(newvalue);
		System.out.println(existingvalue);
					
		if (existingvalue==null) {
			if (RestUtils.isEmpty(updateRoleInfoSQL) == false) {
				if ( RestUtils.isEmpty(newvalue.toString()) == true) {
					 if(!existingvalue.equals(newvalue)){
						updateRoleInfoSQL+=tablefieldname+"='"+newvalue+"'";
						return updateRoleInfoSQL;	
					 }
				}
			}
			else{
				updateRoleInfoSQL+=","+tablefieldname+"='"+newvalue+"'";
				return updateRoleInfoSQL;	
			}
		}
		if(newvalue==null){
			if (RestUtils.isEmpty(updateRoleInfoSQL) == false) {
				updateRoleInfoSQL+=tablefieldname+"='"+newvalue+"'";
				return updateRoleInfoSQL;	
			}
			else{
				updateRoleInfoSQL+=","+tablefieldname+"='"+newvalue+"'";
				return updateRoleInfoSQL;	
			}
		}
		if (!existingvalue.equals(newvalue)) {
			if (RestUtils.isEmpty(updateRoleInfoSQL) == false) {
				updateRoleInfoSQL+=tablefieldname+"='"+newvalue+"'";
				return updateRoleInfoSQL;	
				}
			else{
				updateRoleInfoSQL+=","+tablefieldname+"='"+newvalue+"'";
				return updateRoleInfoSQL;
			}
		}
		return updateRoleInfoSQL;	
}
	public static String formQueryForString(String updateProfileSql, String tablefieldname,String newvalue, String existingvalue) {
		System.out.println(tablefieldname+"          "+existingvalue);
		if ( RestUtils.isEmpty(existingvalue) == false ) {
			if (RestUtils.isEmpty(updateProfileSql) == false) {
				updateProfileSql+=tablefieldname+"='"+newvalue+"'";
				return updateProfileSql;	
			}
			else{
				updateProfileSql+=","+tablefieldname+"='"+newvalue+"'";
				return updateProfileSql;	
			}
		}
		if(!RestUtils.isEmpty(newvalue)){
			if (RestUtils.isEmpty(updateProfileSql) == false) {
				updateProfileSql+=tablefieldname+"='"+newvalue+"'";
				return updateProfileSql;	
			}
			else{
				updateProfileSql+=","+tablefieldname+"='"+newvalue+"'";
				return updateProfileSql;	
			}
		}
		if ( !existingvalue.equals(newvalue)) {
			if (RestUtils.isEmpty(updateProfileSql) == false) {
				updateProfileSql+=tablefieldname+"='"+newvalue+"'";
				return updateProfileSql;	
			}
			else{
				updateProfileSql+=","+tablefieldname+"='"+newvalue+"'";
				return updateProfileSql;	
			}
		}
	return updateProfileSql;
}


	public static String formQueryforJsonArray(String updateRoleInfoSQL, String tablefieldname,JSONArray newvalue, JSONArray existingvalue) {
		if (existingvalue==null) {
			if (RestUtils.isEmpty(updateRoleInfoSQL) == false) {
				if ( RestUtils.isEmpty(newvalue.toString())==false ) {
				updateRoleInfoSQL+=tablefieldname+"='"+newvalue+"'";
				return updateRoleInfoSQL;	
				}
			}
			else{
				updateRoleInfoSQL+=","+tablefieldname+"='"+newvalue+"'";
			}
			return updateRoleInfoSQL;	
		}
		
		if(newvalue==null){
			if (RestUtils.isEmpty(updateRoleInfoSQL) == false) {
				updateRoleInfoSQL+=tablefieldname+"='"+newvalue+"'";
				return updateRoleInfoSQL;	
			}
			else{
				updateRoleInfoSQL+=","+tablefieldname+"='"+newvalue+"'";
				return updateRoleInfoSQL;	
			}
		}
		
		if ( !existingvalue.equals(newvalue)) {
			if (RestUtils.isEmpty(updateRoleInfoSQL) == false) {
				updateRoleInfoSQL+=tablefieldname+"='"+newvalue+"'";
				return updateRoleInfoSQL;	
			}
			else{
				updateRoleInfoSQL+=","+tablefieldname+"='"+newvalue+"'";
				return updateRoleInfoSQL;	
			}
		}
		return updateRoleInfoSQL;
	}


	public String getValidURL(String url) {
		String validURL = "";
		Pattern pattern = Pattern
				.compile("(http[s]?://)([^:^/]*)(:\\d*)?(.*)?");
		Matcher matcher = pattern.matcher(url);
		matcher.find();
		String protocol = matcher.group(1);
		String domain = matcher.group(2);
		String port = matcher.group(3);
		String uri = matcher.group(4);
		System.out.println("Protocol:" + protocol);
		System.out.println("Domain:" + domain);
		System.out.println("Port:" + port);
		System.out.println("URI:" + uri);
		validURL = protocol + domain + uri;
		return validURL;
	}

	public int extractId(String exprString) {
		String pattern = "(.*/ProfilePhoto/)(\\d+)([/.*]*)";
		Pattern pat = Pattern.compile(pattern);
		Matcher match = pat.matcher(exprString);
		if (match.find()) {
			String s = match.group(2);
			int a = Integer.parseInt(s);
			System.out.println("Value is :" + a);
			return a;
			// System.out.println(match.group(3)) ;
		} else {
			System.out.println("No Match");
			return 0;
		}
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

	public static String getFormattedDateStr(Date date) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = null;
		if (date != null) {
			strDate = f.format(date);
			strDate.trim();
		}
		return strDate;
	}

	/*
	 * public Date getLastSynchTime() { SimpleDateFormat f = new
	 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
	 * f.setTimeZone(TimeZone.getTimeZone("UTC")); String utcTime = f.format(new
	 * Date()); System.out.println("String date:" + utcTime); Date lastSynch =
	 * Utils.StringDateToDate(utcTime); System.out.println("Date :" +
	 * lastSynch); return lastSynch; }
	 */

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
	public static String processError(String statusCode, String message) {
		String errorJSONString = new String();
		JSONObject errorJSON = new JSONObject();
		JSONObject errorRespJSON = new JSONObject();
		JSONObject statusJSON = new JSONObject();
		statusJSON.put("code", statusCode);
		statusJSON.put("message", message);
		errorRespJSON.put("status", statusJSON);
		errorJSON.put("response", errorRespJSON);
		errorJSONString = errorJSON.toString();
		return errorJSONString;

	}
	// process success
	
		  public static String processSucess(String serviceType, String functionType, JSONObject obj) { 
		  JSONObject sucessJSON = new JSONObject(); 
		  JSONObject sucessRespJSON = new JSONObject(); 
		  JSONObject serviceJSON = new JSONObject(); 
		  JSONObject statusJSON = new JSONObject(); 
		  
		  serviceJSON.put("servicetype",serviceType); 
		  serviceJSON.put("functiontype", functionType);
		  statusJSON.put("statuscode",PropertiesUtil.getProperty("statuscodesuccessvalue"));
		  statusJSON.put("statusmessage", PropertiesUtil.getProperty("statusmessagesuccessvalue"));
		  sucessJSON.put("status", statusJSON);
		  sucessJSON.put("service", serviceJSON);
		  sucessJSON.put("data", obj);
		  sucessRespJSON.put("response", sucessJSON); 
		  return sucessRespJSON.toString();
		  
		  }
		 
	// getcomment json data object
	/*
	 * public JSONObject getCommentDataObj(int moduleReferenceId,String
	 * moduleType,ConversationComments convComments){ JSONObject dataObj = new
	 * JSONObject(); dataObj.put("commentid", convComments.getId());
	 * dataObj.put("modulereferenceid", moduleReferenceId);
	 * dataObj.put("updateddate",
	 * getFormattedDateStr(convComments.getUpdatedDate()));
	 * dataObj.put("moduletype", moduleType); dataObj.put("commentcreateddate",
	 * getFormattedDateStr(convComments.getCreatedDate()));
	 * dataObj.put("commentupdateddate",
	 * getFormattedDateStr(convComments.getUpdatedDate())); return dataObj; }
	 */

	

	//
	/*
	 * public String processSucessForModules(String serviceType, String
	 * functionType, String dataText, Object Obj) { JSONObject sucessJSON = new
	 * JSONObject(); JSONObject sucessRespJSON = new JSONObject(); JSONObject
	 * contentJSON = new JSONObject(); contentJSON.put("servicetype",
	 * serviceType); contentJSON.put("functiontype", functionType);
	 * contentJSON.put("statuscode",
	 * PropertiesUtil.getProperty("statuscodesuccessvalue"));
	 * contentJSON.put("statusmessage",
	 * PropertiesUtil.getProperty("statusmessagesuccessvalue")); if (Obj
	 * instanceof JSONArray) { contentJSON.put(dataText, Obj); } else if (Obj
	 * instanceof JSONObject) { contentJSON.put(dataText, Obj); }
	 * sucessRespJSON.put("response", contentJSON); sucessJSON.put("json",
	 * sucessRespJSON); return sucessJSON.toString();
	 * 
	 * }
	 */

	// perform substitution text
	/*
	 * public String performMessageTextSubstitutions(String text,
	 * UserFlatMapping member) { if (Utils.isNullOrEmpty(text)) return text;
	 * UserFlatMapping user = member; Person contactPerson =
	 * user.getFlat().getContactPerson(); if (contactPerson == null)
	 * contactPerson = user.getFlat().getRegisteredPerson(); String
	 * contactPersonName = contactPerson.getName();
	 * 
	 * String ret = Utils.replaceAll(text, "$Name", user.getFlat()
	 * .getRegisteredPerson().getName()); ret = Utils.replaceAll(ret,
	 * "$MemberName", user.getFlat() .getRegisteredPerson().getName()); ret =
	 * Utils.replaceAll(ret, "$ContactPersonName", contactPersonName); ret =
	 * Utils.replaceAll(ret, "$Prefix", user.getFlat()
	 * .getRegisteredPerson().getPrefix() + " " +
	 * user.getFlat().getRegisteredPerson().getName()); ret =
	 * Utils.replaceAll(ret, "$Number", user.getFlat()
	 * .getRegisteredPerson().getMobile());
	 * 
	 * ret = Utils.replaceAll(ret, "$ContactPersonPrefix",
	 * contactPerson.getPrefix() + " " + contactPersonName);
	 * 
	 * String email = user.getUser().getEmail(); if (Utils.isNullOrEmpty(email))
	 * { email = user.getFlat().getRegisteredPerson().getEmail(); } if
	 * (Utils.isNullOrEmpty(email)) { email = user.getUser().getEmail(); }
	 * 
	 * ret = Utils.replaceAll(ret, "$Email", email); return ret; }
	 */
	// generate message unique id
	public String generateMessageUniqueId(int networkGroupid) {
		String messageGroupUniqueId = UUID.randomUUID().toString();
		if (networkGroupid > 0) {
			messageGroupUniqueId = networkGroupid + "|" + messageGroupUniqueId;
		}
		return messageGroupUniqueId;
	}

	// verify message template
	public boolean verifyTemplate(String mesg, String template) {
		boolean status = true;
		String trimmedTemplate = template.trim();
		String templateTokens[] = trimmedTemplate.split("\\*");
		String trimmedMesg = mesg.trim();

		int prevIndex = 0;
		int endTemplate = 0;

		System.out.println("Message : " + trimmedMesg);
		for (int i = 0; i < templateTokens.length; i++) {
			System.out.println("I = " + i + " prevIndex : " + prevIndex
					+ " Token : " + templateTokens[i] + " String Left : "
					+ trimmedMesg.substring(prevIndex));
			if (templateTokens[i] == null
					|| templateTokens[i].isEmpty() == true) {
				continue;
			}
			String trimmedTemplateToken = templateTokens[i].trim();
			int ind = trimmedMesg.indexOf(trimmedTemplateToken, prevIndex);
			if (ind > prevIndex && i != 0) {
				prevIndex = ind;
				endTemplate = prevIndex + trimmedTemplateToken.length();
			} else {
				if (prevIndex == 0 && ind == 0 && i == 0) {
					endTemplate = prevIndex + trimmedTemplateToken.length();
					continue;
				} else {
					System.out.println("Failing to match : " + ind);
					status = false;
					return status;
				}
			}
		}
		if (endTemplate < trimmedMesg.length()) {
			if (trimmedTemplate.endsWith("*")) {
				status = true;
				return status;
			} else {
				status = false;
				return status;
			}

		}
		return status;
	}

	public Date get30DaysBefore(Date d) {
		Calendar calendar = Calendar.getInstance(); // this would default to now
		calendar.add(Calendar.DAY_OF_MONTH, -30);
		System.out.println("Date before:" + calendar.getTime());
		return calendar.getTime();
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
	public static JSONObject getJSONObject(JSONArray jsonArray, String key,String value) {
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			if (json.containsValue(value) == true) {
				return json;
			}
		}
		return null;
	}

	public JSONArray getJSOnArray(JSONArray jsonArray, String key, String value) {
		if (jsonArray.toString().contains("\"" + key + "\":\"" + value + "\"") == true) {
			System.out.println("Json array is present");
			return jsonArray;
		}
		return new JSONArray();
	}

	// get Issue Settings
	public String getIssueSettings(String issueSettingsXML) {
		String issueSettingsJSON = null;
		XMLSerializer xmlSerializer = new XMLSerializer();
		JSONObject issueJSON = (JSONObject) xmlSerializer
				.read(issueSettingsXML);
		System.out.println("ISSUE JSON : " + issueJSON.toString());
		issueSettingsJSON = issueJSON.toString();
		return issueSettingsJSON;
	}

	public static java.sql.Date getDate(String dobString) 
	{
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date sql;

		try {
			sql = format.parse(dobString);
			java.sql.Date dob = new java.sql.Date(sql.getTime());
			return dob;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;
	}

	public static String getCurrentDateddmmyyyy() {
		String pattern = "dd-MM-yyyy";
		String dateInString =new SimpleDateFormat(pattern).format(new Date());
		return dateInString;
	}
	
	/*public static String getDateInddmmyyyy(String d) {
		String pattern = "dd-MM-yyyy";
		String dateInString =new SimpleDateFormat(pattern).format(new Date());
		return dateInString;
	}*/

	
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

	public String formVerificationEmail(String validationLink) {
		String verificationEmailStr = "";
		String text1 = TokBoxInterfaceKeys.welcomeToOFD;
		String text2 = TokBoxInterfaceKeys.clickOnLinkToActivate;
		String text3 = TokBoxInterfaceKeys.ifRecievedMailByMistake;
		String text4 = TokBoxInterfaceKeys.sincerely;
		String text5 = TokBoxInterfaceKeys.ofdTeam;

		String htmlLink = "<a href=\"" + validationLink + "\"> click here</a>";

		verificationEmailStr = "<html><body> " + text1 + "<br/><br/>" + text2
				+ "<br/><br/>" + htmlLink + "<br/><br/>" + text3 + "<br/><br/>"
				+ text4 + "<br/>" + text5 + " </body></html>";
		return verificationEmailStr;
	}
	
/*	public static String formQuery(String updateJob, String newvalue, String existingvalue, String tablefieldname) {
		if (RestUtils.isEmpty(existingvalue)==false || existingvalue !=null) {
			if (RestUtils.isEmpty(updateJob) == false) {
				updateJob+=tablefieldname+"='"+newvalue+"'";
				return updateJob;	
			}
			else{
				updateJob+=","+tablefieldname+"='"+newvalue+"'";
				return updateJob;	
			}
		}
	else{
		if (!existingvalue.equals(newvalue)) {
			if (RestUtils.isEmpty(updateJob) == false) {
				updateJob+=tablefieldname+"='"+newvalue+"'";
				return updateJob;	
			}
			else{
				updateJob+=","+tablefieldname+"='"+newvalue+"'";
				return updateJob;
			}
		}
	}
		return updateJob;
	}
	*/
	public String forgotPasswordEmail(String validationLink) {
		String verificationEmailStr = "";

		String text1 = TokBoxInterfaceKeys.welcomeToOFD;
		String text2 = TokBoxInterfaceKeys.toResetPassword;
		String text3 = TokBoxInterfaceKeys.ifRecievedMailByMistake;
		String text4 = TokBoxInterfaceKeys.sincerely;
		String text5 = TokBoxInterfaceKeys.ofdTeam;

		String htmlLink = "<a href=\"" + validationLink + "\"> click here</a>";

		verificationEmailStr = "<html><body> " + text1 + "<br/><br/>" + text2
				+ "<br/><br/>" + htmlLink + "<br/><br/>" + text3 + "<br/><br/>"
				+ text4 + "<br/>" + text5 + " </body></html>";
		return verificationEmailStr;
	}

	public static String passwordIsValid(String password, String rpassword) {
		// TODO Auto-generated method stub
		String error = null;
		if (password.trim().length()<8) {
			error = processError(PropertiesUtil.getProperty("invalid_password_length_code"),PropertiesUtil.getProperty("invalid_password_length_message"));
			System.out.println("len"+error);
			return error;
			}
		else{
				if (!password.matches(".*[a-z].*") || !password.matches(".*[A-Z].*") ||!password.matches(".*[0-9].*") || password.matches("[A-Za-z0-9 ]*")) { 
					error = processError(PropertiesUtil.getProperty("invalid_password_format_code"),PropertiesUtil.getProperty("invalid_password_format_message"));
					return error;
				}
				if (!(password.equals(rpassword))) {
					error = RestUtils.processError(PropertiesUtil.getProperty("password_mismatch_code"),PropertiesUtil.getProperty("password_mismatch_message"));
					return error;
				}
			}
		
		return null;
		}

	public static String emailIsValid(String email) {
	String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
	if (email.matches(EMAIL_REGEX) == true) {
		return null;
	}
	else{
		String error = processError(PropertiesUtil.getProperty("invalid_emailformat_code"),PropertiesUtil.getProperty("invalid_emailformat_message"));
		return error;
	}
	}
	
	public static String getContactName(boolean minor, String contactperson, String firstname, String lastname) {
		// TODO Auto-generated method stub 
		String error ="";
		if (minor) {
		    System.out.println("The employee is not above 18..");
			if (contactperson.trim().equals("")) {
				error = processError(PropertiesUtil.getProperty("contactperson_isempty_code"),PropertiesUtil.getProperty("contactperson_isempty_message"));
				return error;
			}
	}
			contactperson=firstname.concat(" ").concat(lastname);
			return contactperson;
	}

	public static String ifEmptyGetErrorMessage(String field, String fieldName) {
		// TODO Auto-generated method stub
		if (field == null ||	field.trim().isEmpty() == true	|| field.equalsIgnoreCase("[]") || field == "") {
			return null;
		}
		String message = processError(fieldName+" "+PropertiesUtil.getProperty("field_isempty_code"),PropertiesUtil.getProperty("field_isempty_message"));
		return message;
	}

	public static boolean profileHasField(String string, JSONArray planArray) {
		for (int i = 0; i < planArray.size(); i++) {
			String info=planArray.getString(i);
			System.out.println(info);
				if (info.equalsIgnoreCase(string)) {
					return true	;
				}
		}
		return false;
	}

	public static String convertToDDMMYYYY(java.sql.Date date) {
		// TODO Auto-generated method stub
		String pattern = "dd/MM/yyyy";
		String dateInString =new SimpleDateFormat(pattern).format(date);
		System.out.println(dateInString);
		return dateInString;
	}

	public static String ifJsonIsEmptyGetErrorMessage(JSONArray planInfo, String plancode) {
		// TODO Auto-generated method stub
		if(planInfo.isEmpty() || planInfo.size()==0)
		{
		String message = processError(plancode+" "+PropertiesUtil.getProperty("field_isempty_code"),PropertiesUtil.getProperty("field_isempty_message"));
		return message;
		}
		return null;
	}

	public static String ifInvalidJsonArrayValueGetErrorMessage(JSONArray jsonarray,String fieldName) {
		if(jsonarray==null || jsonarray.size()==0){
			String message = processError(fieldName+" "+PropertiesUtil.getProperty("field_invalid_value_code"),PropertiesUtil.getProperty("field_invalid_value_message"));
			return message;
		}
		return null;
	}

	public static JSONArray getJsonArrayFromJsonObjectString(JSONObject dataArray, String key) {
		
		if( dataArray != null && isEmpty(key) == true){
			try{
				JSONArray array = dataArray.getJSONArray(key) ;
				return array;
			}catch(Exception e){
		//		e.printStackTrace();
				return null ;
			}
		}
		return null ;
	}
	
	public static JSONObject getJsonObjectFromJsonObjectString(JSONObject dataArray, String key) {
			
		System.out.println(dataArray.get(key));
			if( dataArray != null && isEmpty(key) == true){
				try{
					JSONObject obj = dataArray.getJSONObject(key) ;
					return obj;
				}catch(Exception e){
					e.printStackTrace();
					return null ;
				}
			}
			return null ;
		}

	public static boolean isEmpty(java.sql.Date dob) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean getJsonArrayFromJsonObjectString(JSONArray planArray,
			String role) {
		// TODO Auto-generated method stub
		return false;
	}

	public static JSONObject checkUserIsValid(JSONObject existingProfile) {
			JSONObject profileInfo = new JSONObject();
			profileInfo.put(TokBoxInterfaceKeys.plancode, existingProfile.getString(TokBoxInterfaceKeys.plancode));
			profileInfo.put(TokBoxInterfaceKeys.otpvalidation, existingProfile.getInt(TokBoxInterfaceKeys.otpvalidation));
			profileInfo.put(TokBoxInterfaceKeys.emailvalidation,existingProfile.getInt(TokBoxInterfaceKeys.emailvalidation));
			profileInfo.put(TokBoxInterfaceKeys.emailvalidation,existingProfile.getInt(TokBoxInterfaceKeys.emailvalidation));
			return profileInfo;
	}

	public static int getRoleMandatory(JSONArray rol) {
			if(rol!=null){
				for (Object j : rol) {
					if (JSONObject.fromObject(j).getInt(TokBoxInterfaceKeys.mandatorycomplete) == 0) {
						return 0;
					}
				}
			}
			return 1;	
	}

/*	public static String validateUser(String serviceType, String functionType, JSONObject profileInfo,String session_id) {
		String profileResponse = "";
		if(profileInfo==null){
			profileResponse = RestUtils.processError(PropertiesUtil.getProperty("invalid_session_code"),PropertiesUtil.getProperty("invalid_session_message"));
			return profileResponse;
		}
		if ((profileInfo.getInt(TokBoxInterfaceKeys.otpvalidation)) == 0 ) {
			profileResponse =RestUtils.getOtpNotValidatedResponse(serviceType,functionType,profileInfo, session_id);
			return profileResponse;
		}
		if ((profileInfo.getInt(TokBoxInterfaceKeys.emailvalidation)) == 0) {
			profileResponse =RestUtils.getEmailNotValidatedResponse(serviceType,functionType,profileInfo, session_id);
			return profileResponse;
		}
		return null;
	}*/

	public static String encodeText(String text) {
		return text = text.replace("'", "\\'");
	}

	public static JSONObject getJSONArrayFromDB(ResultSet res, String fieldname, String fieldtablename, JSONObject profileInfo) {
		try
		{
		if (RestUtils.isEmpty(res.getString(fieldtablename))==false) {
			profileInfo.put(fieldname, new JSONArray());
			return profileInfo;
		} 
		else {
			
			profileInfo.put(fieldname, JSONArray.fromObject(res.getString(fieldtablename)));
			return profileInfo;
		}
		}
		catch(Exception e){
			//e.printStackTrace();
			return profileInfo;
		}
	}

		public static String getJobExpiryDate(String[] durationArray,Calendar calendar) {
		java.util.Date date = null;
		if(durationArray[1].contains("week")){
			int noOfDays = 7*Integer.parseInt(durationArray[0]); //i.e two weeks
		//	calendar.setTime(dateOfOrder);            
			calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
			
			 
		}
		if(durationArray[1].contains("month")){
			calendar.add(Calendar.MONTH, Integer.parseInt(durationArray[0]));
		}
		date =  calendar.getTime();
		Timestamp timestamp = new java.sql.Timestamp(date.getTime());
		
		return timestamp.toString();
	}

		public static JSONObject getJSONObjectFromDB(ResultSet res,String fieldname, String fieldtablename, JSONObject profileInfo) {
			try
			{
			if (RestUtils.isEmpty(res.getString(fieldtablename))==false) {
				profileInfo.put(fieldname, new JSONObject());
				return profileInfo;
			} 
			else {
				
				profileInfo.put(fieldname, JSONObject.fromObject(res.getString(fieldtablename)));
				System.out.println("------------------------");
				System.out.println(profileInfo);
				return profileInfo;
			}
			}
			catch(Exception e){
				//e.printStackTrace();
				return profileInfo;
			}
		}

		public static String getConcatenatedJSONids(JSONArray array) {
			String concatenatedString = "";
			for(Object rol:array){
				concatenatedString +=   "'"+rol+"',";
			}
			if(RestUtils.isEmpty(concatenatedString)==true){
			return concatenatedString.substring(0,concatenatedString.length()-1);
			}
			return concatenatedString;
		}

		public static JSONArray getRoles(JSONArray mandatoryCompleteAndRolenames) {
			JSONArray roles = new JSONArray();
			for (int i = 0; i < mandatoryCompleteAndRolenames.size(); i++) {
				System.out.println();
				roles.add(mandatoryCompleteAndRolenames.getJSONObject(i).get("rolename").toString());
			}
			return roles;
		}

		public static int getInt(JSONObject json,String key) {
			int value = 0 ;
			if( json != null && isEmpty(key) == true){
				try{
					value = json.getInt(key) ;
					System.out.println("key: "+key);
					return value;
				}catch(Exception e){
				//	e.printStackTrace();
				}
				return 0 ;
			}
			return 0 ;
		}

		public static String replaceDoubleQuotes(String string) {
			if(RestUtils.isEmpty(string)==true){
			return string.replaceAll("\"", "");
			}
			return string;
		}

		public static String formFullTextSearchQuery(String jobSearchQuery,	String string, String fieldname, String fieldname_desc) {
			 return jobSearchQuery+=" MATCH("+fieldname+","+fieldname_desc+") AGAINST ('"+string.trim()+"*'  IN BOOLEAN MODE) and ";
		}

		public static String formSearchQuery(String jobSearchQuery,	String fieldname, String string) {
			 return jobSearchQuery+=fieldname +" = '"+string+"' and ";
		}

		public static String formMultiORSearchQuery(String jobSearchQuery,	String fieldname, JSONArray rolename) {
			jobSearchQuery+="(";
			for (int i = 0; i < rolename.size(); i++) {
				 jobSearchQuery+=fieldname +" = '"+rolename.getString(i)+"' or ";
			}
			return jobSearchQuery;
		}

		public static String getConcatenatedJOBids(JSONObject jobsids) {
			String concatenatedString = "";
			for (int i = 0; i < jobsids.size(); i++) {
				concatenatedString +=   "'"+jobsids.getString("jobid")+"',";
			}
			return concatenatedString.substring(1,concatenatedString.length()-2);
		}

		public static boolean JsonObjectisEmpty(String test) {
			try{
				System.out.println(test);
				if (test == null ||	test.trim().isEmpty() == true	|| test.equalsIgnoreCase("{}") || test == "" || test.equalsIgnoreCase("null") || test.equalsIgnoreCase("{null}")) {
					return false;
					//empty
				}
			}catch(Exception e){
				
			}
			return true;
		}

		public static String getConcatenatedJOBJSONids(JSONArray jobsids) {
			String concatenatedString = "";
			for(Object rol:jobsids){
				concatenatedString +=   "'"+rol+"',";
			}
			return concatenatedString.substring(0,concatenatedString.length()-1);
		}

		public static String getConcatenatedHashSetids(HashSet<Integer> blockedids) {
			String concatenatedString = "";
			for(Object rol:blockedids){
				concatenatedString +=   "'"+rol+"',";
			}
			if(RestUtils.isEmpty(concatenatedString)==true){
			return concatenatedString.substring(0,concatenatedString.length()-1);
			}
			return concatenatedString;
		}

		public static String ValidateSort(JSONObject sort, String sortBy, String sorttype) {
			String socialnetworkingResponse="";
			if (RestUtils.isEmpty(sort.toString()) == false) {
				socialnetworkingResponse = RestUtils.processError(PropertiesUtil.getProperty("sort_isempty_code"), PropertiesUtil.getProperty("sort_isempty_message"));
				return socialnetworkingResponse;
			}
			
			if (RestUtils.isEmpty(sortBy) == false) {
				socialnetworkingResponse = RestUtils.processError(PropertiesUtil.getProperty("sortby_isempty_code"), PropertiesUtil.getProperty("sortby_isempty_message"));
				return socialnetworkingResponse;
			}
			
			
			if (RestUtils.isEmpty(sorttype) == false) {
				socialnetworkingResponse = RestUtils.processError(PropertiesUtil.getProperty("sorttype_isempty_code"), PropertiesUtil.getProperty("sorttype_isempty_message"));
				return socialnetworkingResponse;
			}
			return socialnetworkingResponse;
		}

		public static String ValidateRecords(JSONObject records,String start_no, String count) {
			String socialnetworkingResponse="";
			if (RestUtils.isEmpty(records.toString()) == false ) {
				socialnetworkingResponse = RestUtils.processError(PropertiesUtil.getProperty("records_isempty_code"), PropertiesUtil.getProperty("records_isempty_message"));
				return socialnetworkingResponse;
			}
				
			
			if (RestUtils.isEmpty(start_no) == false ||!( records.containsKey(TokBoxInterfaceKeys.start_no))) {
				socialnetworkingResponse = RestUtils.processError(PropertiesUtil.getProperty("startno_isempty_code"), PropertiesUtil.getProperty("startno_isempty_message"));
				return socialnetworkingResponse;
			}
				
			
			if (RestUtils.isEmpty(count) == false || !( records.containsKey(TokBoxInterfaceKeys.count))) {
				socialnetworkingResponse = RestUtils.processError(PropertiesUtil.getProperty("endno_isempty_code"), PropertiesUtil.getProperty("endno_isempty_message"));
				return socialnetworkingResponse;
			}
			return socialnetworkingResponse;
		}

		public static String CalculateExpiryDate(String profileExpirydate, int unit, String duration) {
 			
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 			 sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
 			 Date date = null;
			try {
				date = sdf.parse(profileExpirydate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			 Calendar calendar = Calendar.getInstance();
			 calendar.setTime(date);
			
			
			if(duration.contains("month")){
				calendar.add(Calendar.MONTH, unit);
			}
			
		
			date =  calendar.getTime();
			Timestamp timestamp = new java.sql.Timestamp(date.getTime());
			
			
			return timestamp.toString();
		}

		public static String checkFeatureIsPermitted(String enabled, JSONObject permission) {
			String response="";
			try {
				if(permission == null){
					response = RestUtils.processError(PropertiesUtil.getProperty("invalid_permissions_code"),PropertiesUtil.getProperty("invalid_permissions_message"));
					return response;
				}
				if(permission.containsKey(TokBoxInterfaceKeys.AllAccessPermitted)){
					if(permission.getInt(TokBoxInterfaceKeys.AllAccessPermitted)==1){
						return response;
					}
					else{
						response = RestUtils.processError(PropertiesUtil.getProperty("invalid_permissions_code"),PropertiesUtil.getProperty("invalid_permissions_message"));
						return response;
					}
				}
				else{
					System.out.println(RestUtils.getJSONStringValue(permission,enabled));
					if(permission.containsKey(enabled)){
						if(permission.getInt(enabled)!=1){
							response = RestUtils.processError(PropertiesUtil.getProperty("invalid_permissions_code"),PropertiesUtil.getProperty("invalid_permissions_message"));
							return response;
						}
						else{
							return response;
						}
					}
					else{
						response = RestUtils.processError(PropertiesUtil.getProperty("invalid_permissions_code"),PropertiesUtil.getProperty("invalid_permissions_message"));
						return response;
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			response = RestUtils.processError(PropertiesUtil.getProperty("XMLRequest_code"),PropertiesUtil.getProperty("XMLRequest_message"));
			return response;
		}

		public static String get_SHA_512_SecurePassword(int userid,String createdDate) throws UnsupportedEncodingException {
			String generatedPassword = null;
		    try {
		    	
		         MessageDigest md = MessageDigest.getInstance("SHA-512");
		         StringBuilder string = new StringBuilder();
		         string.append( userid);
		         string.append(".");
		         string.append(createdDate);
		        // md.update(salt.getBytes("UTF-8"));
		         byte[] bytes = md.digest((string.toString()).getBytes("UTF-8"));
		         StringBuilder sb= new StringBuilder();
		         for(int i=0; i< bytes.length ;i++){
		        	 sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		         }
		         generatedPassword = sb.toString();
		         return generatedPassword;
		        } 
		       catch (NoSuchAlgorithmException e){
		        e.printStackTrace();
		       }
		    return null;
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

/*		private String checkPagincollaborationResponseation(JSONObject data) {
			String collaborationResponse="";
			JSONObject sort = RestUtils.getJsonObjectFromJsonObjectString(data, OFDInterfaceKeys.sort);
			if (sort==null) {
				collaborationResponse = RestUtils.processError(PropertiesUtil.getProperty("sort_isempty_code"), PropertiesUtil.getProperty("sort_isempty_message"));
				return collaborationResponse;
			}
			String sortBy =RestUtils.getJSONStringValue(sort, OFDInterfaceKeys.sortby);
			String sorttype =RestUtils.getJSONStringValue(sort, OFDInterfaceKeys.sorttype);
			collaborationResponse  = RestUtils.ValidateSort(sort,sortBy,sorttype);
			if (RestUtils.isEmpty(collaborationResponse) == true) {
				return collaborationResponse;
			}
			else{
				//sort by
				limitandsort+=" order by "+sortBy+" "+sorttype;
			}

			JSONObject records = RestUtils.getJsonObjectFromJsonObjectString(data, OFDInterfaceKeys.records);
			if (RestUtils.isEmpty(records.toString()) == false) {
				collaborationResponse = RestUtils.processError(PropertiesUtil.getProperty("records_isempty_code"), PropertiesUtil.getProperty("records_isempty_message"));
				return collaborationResponse;
			}
			String start_no =RestUtils.getJSONStringValue(records, OFDInterfaceKeys.start_no);
			String count =RestUtils.getJSONStringValue(records, OFDInterfaceKeys.count);
			collaborationResponse  = RestUtils.ValidateRecords(records,start_no,count);
			if (RestUtils.isEmpty(collaborationResponse) == true) {
				return collaborationResponse;
			}
			else{
				//limit
				limitandsort+=" limit "+start_no+","+count;
			}
			return collaborationResponse;

		}
*/

		
	// process success for synch module

	// valid response
	/*
	 * public String processSucessForSynch(String dataPartStr, Object Obj,
	 * String lastTimeSynch) { JSONObject sucessJSON = new JSONObject();
	 * JSONObject sucessRespJSON = new JSONObject(); JSONObject contentJSON =
	 * new JSONObject(); contentJSON.put("statuscode",
	 * PropertiesUtil.getProperty("statuscodesuccessvalue"));
	 * contentJSON.put("statusmessage",
	 * PropertiesUtil.getProperty("statusmessagesuccessvalue")); if (Obj
	 * instanceof JSONArray) { contentJSON.put(dataPartStr, Obj); } else if (Obj
	 * instanceof JSONObject) { contentJSON.put(dataPartStr, Obj); }
	 * contentJSON.put("lastsynchtime", lastTimeSynch);
	 * sucessRespJSON.put("response", contentJSON); sucessJSON.put("json",
	 * sucessRespJSON); return sucessJSON.toString();
	 * 
	 * }
	 */
	// check transaction key exists

	/*
	 * public boolean isTransactionKeyExists(String key,int memberId){
	 * if(isEmpty(key)==false){ return true; } TransactionKeyStatus keystatus =
	 * (TransactionKeyStatus)
	 * DBOperations.getSingleDatabaseObject(TransactionKeyStatus.class,
	 * "TransactionKey='"+key+"'"); if(keystatus!=null){ return true; }else{
	 * TransactionKeyStatus transKey = new TransactionKeyStatus();
	 * transKey.setMemberId(memberId); transKey.setTransactionKey(key);
	 * transKey.save(); transKey = new TransactionKeyStatus(); return false; } }
	 */
	// process success
	/*
	 * public String processSucessMessage(String serviceType, String
	 * functionType, String dataText, JSONArray dataArray) { JSONObject
	 * sucessJSON = new JSONObject(); JSONObject sucessRespJSON = new
	 * JSONObject(); JSONObject contentJSON = new JSONObject();
	 * contentJSON.put("servicetype", serviceType);
	 * contentJSON.put("functiontype", functionType);
	 * contentJSON.put("statuscode",
	 * PropertiesUtil.getProperty("statuscodesuccessvalue"));
	 * contentJSON.put("statusmessage",
	 * PropertiesUtil.getProperty("statusmessagesuccessvalue")); if (dataArray
	 * != null && dataArray.size() > 0) { contentJSON.put(dataText, dataArray);
	 * } sucessRespJSON.put("response", contentJSON); sucessJSON.put("json",
	 * sucessRespJSON); return sucessJSON.toString();
	 * 
	 * }
	 */

}