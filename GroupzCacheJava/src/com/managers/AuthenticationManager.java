package com.managers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.bson.Document;

import com.connection.Mongo_Connection;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.utils.ConnectionUtils;
import com.utils.GlobalTags;
import com.utils.PropertiesUtil;
import com.utils.RestUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AuthenticationManager {
	Mongo_Connection conn = new Mongo_Connection();
	MongoDatabase db = conn.getConnection();

	public String getResponse(String regRequest) {
		System.out.println("Insde AuthenticationManager getResponse!");
		// DomainModifier d = new DomainModifier();
		// d.changeDomain();
		String response = "";
		String servicetype = "";
		String functiontype = "";
		System.out.println(regRequest);
		try {
			if (RestUtils.isJSONValid(regRequest) == false) {
				return RestUtils.processError(
						PropertiesUtil.getProperty("invalidJson_code"),
						PropertiesUtil.getProperty("invalidJson_message"));
			}
			JSONObject json = new JSONObject();
			json = JSONObject.fromObject(regRequest);
			JSONObject request = json.getJSONObject(GlobalTags.JSON_TAG)
					.getJSONObject(GlobalTags.JSON_REQUEST_TAG);

			servicetype = request.getString(GlobalTags.SERVICE_TYPE_TAG);
			if (RestUtils.isEmpty(servicetype) == false) {
				response = RestUtils.processError(PropertiesUtil
						.getProperty("invalidServicetype_code"), PropertiesUtil
						.getProperty("invalidServicetype_message"));
				return response;
			}

			functiontype = request.getString(GlobalTags.FUNCTION_TYPE_TAG);
			if (RestUtils.isEmpty(functiontype) == false) {
				response = RestUtils.processError(PropertiesUtil
						.getProperty("invalidFunctiontype_code"),
						PropertiesUtil
								.getProperty("invalidFunctiontype_message"));
				return response;
			}

			String out = getServicetypeAndFunctiontypefromDB(servicetype,
					functiontype);
			if (out == null) {
				response = RestUtils
						.processError(
								PropertiesUtil
										.getProperty("invalidServiceOrFunctionType_code"),
								PropertiesUtil
										.getProperty("invalidServiceOrFunctionType_message"));
				return response;
			}
			JSONArray jArray = JSONArray.fromObject(out);

			JSONObject jObj = jArray.getJSONObject(0);
			boolean sessionvalidation = false;
			if (jObj.containsKey(GlobalTags.SESSION__VALIDATE_TAG)) {
				sessionvalidation = jObj
						.getBoolean(GlobalTags.SESSION__VALIDATE_TAG);
			} else if (jObj.containsKey("sessionvalidation")) {
				sessionvalidation = jObj.getBoolean("sessionvalidation");
			}

			if (sessionvalidation == true) {
				response = RestUtils.processError(
						PropertiesUtil.getProperty("permissionError_code"),
						PropertiesUtil.getProperty("permissionError_message"));
				return response;
			}

			if (request.containsKey(GlobalTags.USER_DATA_TAG)) {
				JSONObject userData = request
						.getJSONObject(GlobalTags.USER_DATA_TAG);
				boolean selection = request
						.getBoolean(GlobalTags.SELECTION_TAG);
				response = formUrlAndConnect(out, selection, userData);
				System.out.println("response : " + response);
				JSONObject resObj = JSONObject.fromObject(response);
				if (resObj
						.getJSONObject(GlobalTags.JSON_TAG)
						.getJSONObject(GlobalTags.RESPONSE_TAG)
						.getString(GlobalTags.STATUS_CODE_TAG)
						.equalsIgnoreCase(
								PropertiesUtil
										.getProperty("statuscodesuccessvalue"))) {
					System.out
							.println("---------------------------------------");
					String success = insertValuesInToTables(response);
					System.out.println("-------Success : " + success);
					try {
						JSONArray array = JSONArray.fromObject(success);
						// Admin Login Response
						System.out.println("validate For Admin");
						if (isAdminLogin(success)) {
							System.out.println("isAdminLogin(success) : "
									+ isAdminLogin(success));
							response = success;
							return response;
						}
						if (array.size() == 0) {
							response = RestUtils
									.processError(
											PropertiesUtil
													.getProperty("insertError_code"),
											PropertiesUtil
													.getProperty("insertError_message"));
							return response;
						}
					} catch (Exception e) {
						response = RestUtils.processError(PropertiesUtil
								.getProperty("insertError_code"),
								PropertiesUtil
										.getProperty("insertError_message"));
						return response;
					}
					JSONObject sucessJSON = new JSONObject();
					JSONObject sucessRespJSON = new JSONObject();
					JSONObject contentJSON = new JSONObject();

					contentJSON.put(GlobalTags.SERVICE_TYPE_TAG, servicetype);
					contentJSON.put(GlobalTags.FUNCTION_TYPE_TAG, functiontype);
					contentJSON.put(GlobalTags.STATUS_CODE_TAG, Integer
							.parseInt(PropertiesUtil
									.getProperty("statuscodesuccessvalue")));
					contentJSON.put(GlobalTags.STATUS_MSG_TAG, PropertiesUtil
							.getProperty("statusmessagesuccessvalue"));
					contentJSON.put(GlobalTags.USER_TAG, success);
					sucessRespJSON.put(GlobalTags.RESPONSE_TAG, contentJSON);
					sucessJSON.put(GlobalTags.JSON_TAG, sucessRespJSON);
					response = sucessJSON.toString();
					return response;
				} else {
					return response;
				}

			} else {
				response = RestUtils
						.processError(
								PropertiesUtil
										.getProperty("invalidServiceOrFunctionType_code"),
								PropertiesUtil
										.getProperty("invalidServiceOrFunctionType_message"));
				return response;
			}

		} catch (Exception e) {
			e.printStackTrace();
			response = RestUtils.processError(
					PropertiesUtil.getProperty("incompleteInput_code"),
					PropertiesUtil.getProperty("incompleteInput_message"));
			return response;
		}
	}

	private String getServicetypeAndFunctiontypefromDB(String serviceType,
			String functionType) {
		String res = "";
		try {

			MongoCollection<Document> collection = db
					.getCollection("authtables");

			BasicDBObject whereQuery = new BasicDBObject();
			List<BasicDBObject> list = new ArrayList<BasicDBObject>();
			list.add(new BasicDBObject(GlobalTags.SERVICE_TYPE_TAG, Integer
					.parseInt(serviceType)));
			list.add(new BasicDBObject(GlobalTags.FUNCTION_TYPE_TAG, Integer
					.parseInt(functionType)));
			whereQuery.put("$and", list);

			System.out.println("Where Query : " + whereQuery.toString());

			FindIterable<Document> result = collection.find(whereQuery);
			if (result == null) {
				return RestUtils
						.processError(
								PropertiesUtil
										.getProperty("invalidServiceOrFunctionType_code"),
								PropertiesUtil
										.getProperty("invalidServiceOrFunctionType_message"));
			}
			MongoCursor<Document> re = result.iterator();
			JSONArray datas = new JSONArray();
			System.out.println("ST AND FT FROM MONGODB : " + re.toString()
					+ re.hasNext());
			if (re.hasNext()) {
				while (re.hasNext()) {
					JSONObject val = new JSONObject();
					Document value = re.next();
					val.put(GlobalTags.SERVICE_TYPE_TAG,
							value.getInteger("contentservicetype"));
					val.put(GlobalTags.FUNCTION_TYPE_TAG,
							value.getInteger("contentfunctiontype"));
					val.put("roleoffset", value.getString("roleoffset"));
					val.put("url", value.getString("uri"));
					val.put("groupzmodulename",
							value.getString("groupzmodulename"));
					val.put("sessionvalidation",
							value.getBoolean("sessionvalidation"));
					datas.add(val);
				}
				res = datas.toString();
				System.out.println("Vale from session ");
				System.out.println(datas.toString());
				return res;
			} else {
				System.out.println("No Data Found!");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out
					.println("Exception in getServicetypeAndFunctiontypefromDB");
			return null;
		}
	}

	private String formUrlAndConnect(String out, boolean selection,
			JSONObject userData) {
		System.out.println("Inside formUrlAndConnect");
		String res = "";
		try {
			JSONArray requestArray = new JSONArray();
			requestArray = JSONArray.fromObject(out);
			if (requestArray.size() >= 1) {
				JSONObject req = requestArray.getJSONObject(0);
				System.out.println(req);
				String url = req.getString("url");
				JSONObject request = new JSONObject();
				request.put("servicetype", req.getString("servicetype"));
				request.put("functiontype", req.getString("functiontype"));
				request.put("userdata", userData);
				request.put("selection", selection);
				JSONObject json = new JSONObject();
				json.put("request", request);
				JSONObject js = new JSONObject();
				js.put("json", json);
				ConnectionUtils cu = new ConnectionUtils();
				// url ="http://qa1.groupz.in/GroupzMobileApp/Authentication";
				res = cu.ConnectandRecieve(url + "?request=", js.toString());
			}
			if (RestUtils.isEmpty(res) == false) {
				res = RestUtils.processError(
						PropertiesUtil.getProperty("technical_issue_code"),
						PropertiesUtil.getProperty("technical_issue_message"));
				return res;
			}
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exceprtion in formUrlAndConnect()");
			res = RestUtils.processError(
					PropertiesUtil.getProperty("technical_issue_code"),
					PropertiesUtil.getProperty("technical_issue_message"));
			return res;
		}
	}

	private String insertValuesInToTables(String response) {
		String resp = "";
		try {
			JSONObject result = new JSONObject();
			result = JSONObject.fromObject(response);
			JSONObject res = result.getJSONObject("json").getJSONObject(
					"response");
			if (res.getString("statusmessage").equals("Success") == false) {
				resp = res.toString();
				return resp;
			}
			// Admin Login Response
			if (res.containsKey(GlobalTags.JSON_DATA_TAG)) {
				if (res.getJSONObject(GlobalTags.JSON_DATA_TAG).containsKey(
						"admindetails")) {
					return response;
				}
			}
			JSONArray user = res.getJSONArray("user");
			JSONArray userRes = new JSONArray();
			if (user.size() <= 0) {
				resp = RestUtils.processError(PropertiesUtil
						.getProperty("no_user_from_back_end_code"),
						PropertiesUtil
								.getProperty("no_user_from_back_end_message"));
				return resp;
			}
			for (int i = 0; i < user.size(); i++) {

				JSONObject groupzDetails = user.getJSONObject(i).getJSONObject(
						"groupzdetails");
				String groupzid = groupzDetails.getString("groupzid");
				MongoCollection<Document> collection = db
						.getCollection("groupzdetails");
				BasicDBObject query = new BasicDBObject("groupzid",
						Integer.parseInt(groupzid));

				FindIterable<Document> r = collection.find(query);
				MongoCursor<Document> re = r.iterator();
				if (re.hasNext()) {
					// System.out.println("Present");
				} else {
					/*
					 * System.out.println("Should Insert Into groupzdetails!");
					 * Document doc = new Document("groupzid",groupzid); if
					 * (groupzDetails.containsKey("groupzcode")){
					 * doc.append("groupzcode",
					 * groupzDetails.getString("groupzcode")); } if
					 * (groupzDetails.containsKey("groupzname")){
					 * doc.append("groupzname",
					 * groupzDetails.getString("groupzname")); } if
					 * (groupzDetails.containsKey("shortname")){
					 * doc.append("shortname",
					 * groupzDetails.getString("shortname")); } if
					 * (groupzDetails.containsKey("groupzurl")){
					 * doc.append("groupzurl",
					 * groupzDetails.getString("groupzurl")); } if
					 * (groupzDetails.containsKey("type")){ doc.append("type",
					 * groupzDetails.getInt("type")); } if
					 * (groupzDetails.containsKey("groupzbasekey")){
					 * doc.append("groupzbasekey",
					 * groupzDetails.getString("groupzbasekey")); } if
					 * (groupzDetails.containsKey("address")){
					 * doc.append("address",
					 * groupzDetails.getString("address")); } if
					 * (groupzDetails.containsKey("area")){ doc.append("area",
					 * groupzDetails.getString("area")); } if
					 * (groupzDetails.containsKey("city")){ doc.append("city",
					 * groupzDetails.getString("city")); } if
					 * (groupzDetails.containsKey("state")){ doc.append("state",
					 * groupzDetails.getString("state")); } if
					 * (groupzDetails.containsKey("country")){
					 * doc.append("country",
					 * groupzDetails.getString("country")); } if
					 * (groupzDetails.containsKey("postalcode")){
					 * doc.append("postalcode",
					 * groupzDetails.getString("postalcode")); } if
					 * (groupzDetails.containsKey("landline")){
					 * doc.append("landline",
					 * groupzDetails.getString("landline")); } if
					 * (groupzDetails.containsKey("mobile")){
					 * doc.append("mobile", groupzDetails.getString("mobile"));
					 * } if (groupzDetails.containsKey("facebooklink")){
					 * doc.append("facebooklink",
					 * groupzDetails.getString("facebooklink")); } if
					 * (groupzDetails.containsKey("twitterlink")){
					 * doc.append("facebooklink",
					 * groupzDetails.getString("facebooklink")); } if
					 * (groupzDetails.containsKey("bloglink")){
					 * doc.append("bloglink",
					 * groupzDetails.getString("bloglink")); } if
					 * (groupzDetails.containsKey("defaultuserrole")){
					 * doc.append("defaultuserrole",
					 * groupzDetails.getString("defaultuserrole")); } if
					 * (groupzDetails.containsKey("transactionstartdate")){
					 * doc.append("transactionstartdate",
					 * groupzDetails.getString("transactionstartdate")); } if
					 * (groupzDetails.containsKey("transactionenddate")){
					 * doc.append("transactionenddate",
					 * groupzDetails.getString("transactionenddate")); } if
					 * (groupzDetails.containsKey("senderemail")){
					 * doc.append("senderemail",
					 * groupzDetails.getString("senderemail")); } if
					 * (groupzDetails.containsKey("registrationno")){
					 * doc.append("registrationno",
					 * groupzDetails.getString("registrationno")); } if
					 * (groupzDetails.containsKey("panno")){ doc.append("panno",
					 * groupzDetails.getString("panno")); } if
					 * (groupzDetails.containsKey("albumsize")){
					 * doc.append("albumsize",
					 * groupzDetails.getString("albumsize")); } if
					 * (groupzDetails.containsKey("segments")){
					 * doc.append("segments",
					 * groupzDetails.getString("segments")); } if
					 * (groupzDetails.containsKey("userareas")){
					 * doc.append("userareas",
					 * groupzDetails.getString("userareas")); } if
					 * (groupzDetails.containsKey("groupztype")){
					 * doc.append("groupztype",
					 * groupzDetails.getString("groupztype")); } if
					 * (groupzDetails.containsKey("others")){
					 * doc.append("others", groupzDetails.getString("others"));
					 * } if (groupzDetails.containsKey("loginurl")){
					 * doc.append("loginurl",
					 * groupzDetails.getString("loginurl")); } if
					 * (groupzDetails.containsKey("description")){
					 * doc.append("description",
					 * groupzDetails.getString("description")); } if
					 * (groupzDetails.containsKey("latitude")){
					 * doc.append("latitude",
					 * groupzDetails.getString("latitude")); } if
					 * (groupzDetails.containsKey("longitude")){
					 * doc.append("longitude",
					 * groupzDetails.getString("longitude")); } if
					 * (groupzDetails.containsKey("radius")){
					 * doc.append("radius", groupzDetails.getInt("radius")); }
					 * if (groupzDetails.containsKey("groupzenabled")){
					 * doc.append("groupzenabled",
					 * groupzDetails.getBoolean("groupzenabled")); } if
					 * (groupzDetails.containsKey("metatagdesc")){
					 * doc.append("metatagdesc",
					 * groupzDetails.getString("metatagdesc")); } if
					 * (groupzDetails.containsKey("metatagkeywords")){
					 * doc.append("metatagkeywords",
					 * groupzDetails.getString("metatagkeywords")); } if
					 * (groupzDetails.containsKey("ipaddresses")){
					 * doc.append("ipaddresses",
					 * groupzDetails.getString("ipaddresses")); } if
					 * (groupzDetails.containsKey("smscost")){
					 * doc.append("smscost", groupzDetails.getInt("smscost")); }
					 * if(groupzDetails.containsKey("bannerimageurl")){
					 * doc.append("bannerimageurl",
					 * groupzDetails.getString("bannerimageurl")); }
					 * if(groupzDetails.containsKey("bannerlink:")){
					 * doc.append("bannerlink:",
					 * groupzDetails.getString("bannerlink:")); }
					 * doc.append("applicationskin",
					 * groupzDetails.getString("applicationskin"));
					 * if(groupzDetails.containsKey("redirecturl")){
					 * doc.append("redirecturl",
					 * groupzDetails.getString("redirecturl")); }
					 * if(groupzDetails.containsKey("smsprovidercode")){
					 * doc.append("smsprovidercode",
					 * groupzDetails.getString("smsprovidercode")); }
					 * if(groupzDetails.containsKey("smsproviderusername")){
					 * doc.append("smsproviderusername",
					 * groupzDetails.getString("smsproviderusername")); }
					 * if(groupzDetails
					 * .containsKey("smsproviderusernamedecrypted")){
					 * doc.append("smsproviderusernamedecrypted",
					 * groupzDetails.getString("smsproviderusernamedecrypted"));
					 * } if(groupzDetails.containsKey("smsproviderpassword")){
					 * doc.append("smsproviderpassword",
					 * groupzDetails.getString("smsproviderpassword")); }
					 * if(groupzDetails
					 * .containsKey("smsproviderpassworddecrypted")){
					 * doc.append("smsproviderpassworddecrypted",
					 * groupzDetails.getString("smsproviderpassworddecrypted"));
					 * } if(groupzDetails.containsKey("recieptnoprefix")){
					 * doc.append("recieptnoprefix",
					 * groupzDetails.getString("recieptnoprefix")); }
					 * if(groupzDetails.containsKey("userapprovedmailtitle")){
					 * doc.append("userapprovedmailtitle",
					 * groupzDetails.getString("userapprovedmailtitle")); }
					 * if(groupzDetails.containsKey("userapprovedmailtext")){
					 * doc.append("userapprovedmailtext",
					 * groupzDetails.getString("userapprovedmailtext")); }
					 * if(groupzDetails.containsKey("userfailuremailtext")){
					 * doc.append("userfailuremailtext",
					 * groupzDetails.getString("userfailuremailtext")); }
					 * if(groupzDetails.containsKey("userfailuremailtitle")){
					 * doc.append("userfailuremailtitle",
					 * groupzDetails.getString("userfailuremailtitle")); }
					 * if(groupzDetails.containsKey("contactmailtitle")){
					 * doc.append("contactmailtitle",
					 * groupzDetails.getString("contactmailtitle")); }
					 * if(groupzDetails.containsKey("contactmailtext")){
					 * doc.append("contactmailtext",
					 * groupzDetails.getString("contactmailtext")); }
					 * if(groupzDetails.containsKey("contactsmstext")){
					 * doc.append("contactsmstext",
					 * groupzDetails.getString("contactsmstext")); } if
					 * (groupzDetails.containsKey("greetingmailtitle")){
					 * doc.append("greetingmailtitle",
					 * groupzDetails.getString("greetingmailtitle")); } if
					 * (groupzDetails.containsKey("greetingmailtext")){
					 * doc.append("greetingmailtext",
					 * groupzDetails.getString("greetingmailtext")); } if
					 * (groupzDetails.containsKey("greetingmailenabled")){
					 * doc.append("greetingmailenabled",
					 * groupzDetails.getBoolean("greetingmailenabled")); } if
					 * (groupzDetails
					 * .containsKey("greetingfamilymembersforemail")){
					 * doc.append("greetingfamilymembersforemail",
					 * groupzDetails.
					 * getBoolean("greetingfamilymembersforemail")); } if
					 * (groupzDetails.containsKey("greetingsmstext")){
					 * doc.append("greetingsmstext",
					 * groupzDetails.getString("greetingsmstext")); } if
					 * (groupzDetails.containsKey("greetingsmsenabled")){
					 * doc.append("greetingsmsenabled",
					 * groupzDetails.getBoolean("greetingsmsenabled")); } if
					 * (groupzDetails
					 * .containsKey("greetingfamilymembersforsms")){
					 * doc.append("greetingfamilymembersforsms",
					 * groupzDetails.getBoolean("greetingfamilymembersforsms"));
					 * } if (groupzDetails.containsKey("dueslabel")){
					 * doc.append("dueslabel",
					 * groupzDetails.getString("dueslabel")); } if
					 * (groupzDetails.containsKey("duesenabled")){
					 * doc.append("duesenabled",
					 * groupzDetails.getInt("duesenabled")); } if
					 * (groupzDetails.containsKey("messagelabel")){
					 * doc.append("messagelabel",
					 * groupzDetails.getString("messagelabel")); } if
					 * (groupzDetails.containsKey("smsenabled")){
					 * doc.append("smsenabled",
					 * groupzDetails.getInt("smsenabled")); } if
					 * (groupzDetails.containsKey("emailenabled")){
					 * doc.append("emailenabled",
					 * groupzDetails.getInt("emailenabled")); } if
					 * (groupzDetails.containsKey("albumlabel")){
					 * doc.append("albumlabel",
					 * groupzDetails.getString("albumlabel")); } if
					 * (groupzDetails.containsKey("albumsenabled")){
					 * doc.append("albumsenabled",
					 * groupzDetails.getInt("albumsenabled")); } if
					 * (groupzDetails.containsKey("announcementlabel")){
					 * doc.append("announcementlabel",
					 * groupzDetails.getString("announcementlabel")); } if
					 * (groupzDetails.containsKey("announcementsenabled")){
					 * doc.append("announcementsenabled",
					 * groupzDetails.getInt("announcementsenabled")); } if
					 * (groupzDetails.containsKey("advertisementlabel")){
					 * doc.append("advertisementlabel",
					 * groupzDetails.getString("advertisementlabel")); } if
					 * (groupzDetails.containsKey("advertisementsenabled")){
					 * doc.append("advertisementsenabled",
					 * groupzDetails.getInt("advertisementsenabled")); } if
					 * (groupzDetails.containsKey("surveylabel")){
					 * doc.append("surveylabel",
					 * groupzDetails.getString("surveylabel")); } if
					 * (groupzDetails.containsKey("surveysenabled")){
					 * doc.append("surveysenabled",
					 * groupzDetails.getInt("surveysenabled")); } if
					 * (groupzDetails.containsKey("servicerequestlabel")){
					 * doc.append("servicerequestlabel",
					 * groupzDetails.getString("servicerequestlabel")); } if
					 * (groupzDetails.containsKey("issuesenabled")){
					 * doc.append("issuesenabled",
					 * groupzDetails.getInt("issuesenabled")); } if
					 * (groupzDetails.containsKey("meeting label")){
					 * doc.append("meeting label",
					 * groupzDetails.getString("meeting label")); } if
					 * (groupzDetails.containsKey("meetingsenabled")){
					 * doc.append("meetingsenabled",
					 * groupzDetails.getInt("meetingsenabled")); } if
					 * (groupzDetails.containsKey("noticeslabel")){
					 * doc.append("noticeslabel",
					 * groupzDetails.getString("noticeslabel")); } if
					 * (groupzDetails.containsKey("noticesenabled")){
					 * doc.append("noticesenabled",
					 * groupzDetails.getInt("noticesenabled")); } if
					 * (groupzDetails.containsKey("documentslabel")){
					 * doc.append("documentslabel",
					 * groupzDetails.getString("documentslabel")); } if
					 * (groupzDetails.containsKey("documentssharingenabled")){
					 * doc.append("documentssharingenabled",
					 * groupzDetails.getInt("documentssharingenabled")); } if
					 * (groupzDetails.containsKey("plannerlabel")){
					 * doc.append("plannerlabel",
					 * groupzDetails.getString("plannerlabel")); } if
					 * (groupzDetails.containsKey("plannerenabled")){
					 * doc.append("plannerenabled",
					 * groupzDetails.getInt("plannerenabled")); } if
					 * (groupzDetails.containsKey("banneradsenabled")){
					 * doc.append("banneradsenabled",
					 * groupzDetails.getInt("banneradsenabled")); } if
					 * (groupzDetails.containsKey("classifiedsearchenabled")){
					 * doc.append("classifiedsearchenabled",
					 * groupzDetails.getInt("classifiedsearchenabled")); } if
					 * (groupzDetails.containsKey("classifiedsenabled")){
					 * doc.append("classifiedsenabled",
					 * groupzDetails.getInt("classifiedsenabled")); } if
					 * (groupzDetails.containsKey("contactsenabled")){
					 * doc.append("contactsenabled",
					 * groupzDetails.getInt("contactsenabled")); } if
					 * (groupzDetails.containsKey("contactsharingenabled")){
					 * doc.append("contactsharingenabled",
					 * groupzDetails.getInt("contactsharingenabled")); } if
					 * (groupzDetails.containsKey("displayproductlogo")){
					 * doc.append("displayproductlogo",
					 * groupzDetails.getInt("displayproductlogo")); } if
					 * (groupzDetails.containsKey("familyinformationenabled")){
					 * doc.append("displayproductlogo",
					 * groupzDetails.getInt("displayproductlogo")); } if
					 * (groupzDetails.containsKey("helpersenabled")){
					 * doc.append("helpersenabled",
					 * groupzDetails.getInt("helpersenabled")); } if
					 * (groupzDetails
					 * .containsKey("memberssearchacrosssocietyenabled")){
					 * doc.append("memberssearchacrosssocietyenabled",
					 * groupzDetails
					 * .getInt("memberssearchacrosssocietyenabled")); } if
					 * (groupzDetails.containsKey("memberssearchenabled")){
					 * doc.append("memberssearchenabled",
					 * groupzDetails.getInt("memberssearchenabled")); } if
					 * (groupzDetails.containsKey("scrollingadsenabled")){
					 * doc.append("scrollingadsenabled",
					 * groupzDetails.getInt("scrollingadsenabled")); } if
					 * (groupzDetails.containsKey("issuesettings")){
					 * doc.append("issuesettings",
					 * groupzDetails.getJSONObject("issuesettings")); } if
					 * (groupzDetails.containsKey("webtheme")){
					 * doc.append("webtheme",
					 * groupzDetails.getJSONObject("webtheme")); } if
					 * (groupzDetails.containsKey("androidtheme")){
					 * doc.append("androidtheme",
					 * groupzDetails.getJSONObject("androidtheme")); } if
					 * (groupzDetails.containsKey("iostheme")){
					 * doc.append("iostheme",
					 * groupzDetails.getJSONObject("iostheme")); }
					 * doc.append("lastUpdatedTime",
					 * RestUtils.getLastSynchTime().toString());
					 */

					if (groupzDetails.getJSONObject("srsettings").containsKey(
							"issueflowrulelist")) {
						JSONArray issueflowrulelist = groupzDetails
								.getJSONObject("srsettings").getJSONArray(
										"issueflowrulelist");
						JSONArray issueflowrulelist1 = new JSONArray();
						groupzDetails.remove("issueflowrulelist");
						for (int k = 0; k < issueflowrulelist.size(); k++) {
							JSONObject obj = issueflowrulelist.getJSONObject(k);
							obj.remove("$$hashKey");
							issueflowrulelist1.add(obj);
						}
						groupzDetails.getJSONObject("srsettings").put(
								"issueflowrulelist", issueflowrulelist1);
					}

					groupzDetails.put(GlobalTags.LAST_UPDATED_TIME_TAG,
							RestUtils.getLastSynchTime().toString());
					System.out
							.println("--------------------------------------------");
					System.out.println(groupzDetails);
					System.out
							.println("--------------------------------------------");
					Document groupz = new Document(groupzDetails);
					collection.insertOne(groupz);
					System.out
							.println("--------------------------------------------");
					System.out.println("--" + groupz);
					System.out
							.println("--------------------------------------------");
					// collection.insertOne(doc);
					/*
					 * Object id = doc.get("_id"); if (id == null){
					 * System.out.println
					 * ("Problem Occured while inserting in groupzdetails");
					 * resp = RestUtils.processError(PropertiesUtil.getProperty(
					 * "insertError_code"),
					 * PropertiesUtil.getProperty("insertError_message"));
					 * return resp; } else{ System.out.println("Inserted"); }
					 */
				}

				// System.out.println("total Vales "+count);

				JSONObject memberDetails = user.getJSONObject(i).getJSONObject(
						"memberdetails");

				/*
				 * Document doc1 = new
				 * Document("groupzid",memberDetails.getString("groupzid")); if
				 * (memberDetails.containsKey("groupzcode")){
				 * doc1.append("groupzcode",
				 * memberDetails.getString("groupzcode")); } if
				 * (memberDetails.containsKey("membercode")){
				 * doc1.append("membercode",
				 * memberDetails.getString("membercode")); } if
				 * (memberDetails.containsKey("memberid")){
				 * doc1.append("memberid", memberDetails.getInt("memberid")); }
				 * if (memberDetails.containsKey("registeredpersonid")){
				 * doc1.append("registeredpersonid",
				 * memberDetails.getInt("registeredpersonid")); } if
				 * (memberDetails.containsKey("personid")){
				 * doc1.append("personid", memberDetails.getInt("personid")); }
				 * if (memberDetails.containsKey("membername")){
				 * doc1.append("membername",
				 * memberDetails.getString("membername")); } if
				 * (memberDetails.containsKey("relationship")){
				 * doc1.append("relationship",
				 * memberDetails.getString("relationship")); } if
				 * (memberDetails.containsKey("profileurl")){
				 * doc1.append("profileurl",
				 * memberDetails.getString("profileurl")); } if
				 * (memberDetails.containsKey("email")){ doc1.append("email",
				 * memberDetails.getString("email")); } if
				 * (memberDetails.containsKey("username")){
				 * doc1.append("username", memberDetails.getString("username"));
				 * } if (memberDetails.containsKey("username")){
				 * doc1.append("rolename", memberDetails.getString("rolename"));
				 * } if(memberDetails.containsKey("dateofbirth")){
				 * doc1.append("dateofbirth",
				 * memberDetails.getString("dateofbirth")); }
				 * if(memberDetails.containsKey("bloodgroup")){
				 * doc1.append("bloodgroup",
				 * memberDetails.getString("bloodgroup")); }
				 * if(memberDetails.containsKey("divisionlabel")){
				 * doc1.append("divisionlabel",
				 * memberDetails.getString("divisionlabel")); }
				 * if(memberDetails.containsKey("divisionvalue")){
				 * doc1.append("divisionvalue",
				 * memberDetails.getString("divisionvalue")); }
				 * if(memberDetails.containsKey("subdivisionlabel")){
				 * doc1.append("subdivisionlabel",
				 * memberDetails.getString("subdivisionlabel")); }
				 * if(memberDetails.containsKey("subdivisionvalue")){
				 * doc1.append("subdivisionvalue",
				 * memberDetails.getString("subdivisionvalue")); }
				 * if(memberDetails.containsKey("leavetypelist")){
				 * doc1.append("leavetypelist",
				 * memberDetails.getJSONArray("leavetypelist")); }
				 * if(memberDetails.containsKey("networkadmin")){
				 * doc1.append("networkadmin",
				 * memberDetails.getInt("networkadmin")); }
				 * if(memberDetails.containsKey("managegroupzbase")){
				 * doc1.append("managegroupzbase",
				 * memberDetails.getInt("managegroupzbase")); }
				 * if(memberDetails.containsKey("localadmin")){
				 * doc1.append("localadmin",
				 * memberDetails.getInt("localadmin")); }
				 * if(memberDetails.containsKey("manageusers")){
				 * doc1.append("manageusers",
				 * memberDetails.getInt("manageusers")); }
				 * if(memberDetails.containsKey("createissue")){
				 * doc1.append("createissue",
				 * memberDetails.getInt("createissue")); }
				 * if(memberDetails.containsKey("viewallissue")){
				 * doc1.append("viewallissue",
				 * memberDetails.getInt("viewallissue")); }
				 * if(memberDetails.containsKey("closeissue")){
				 * doc1.append("closeissue",
				 * memberDetails.getInt("closeissue")); }
				 * if(memberDetails.containsKey("issuenotices")){
				 * doc1.append("issuenotices",
				 * memberDetails.getInt("issuenotices")); }
				 * if(memberDetails.containsKey("manageresource")){
				 * doc1.append("manageresource",
				 * memberDetails.getInt("manageresource")); }
				 * if(memberDetails.containsKey("startsurvey")){
				 * doc1.append("startsurvey",
				 * memberDetails.getInt("startsurvey")); }
				 * if(memberDetails.containsKey("loginotherusers")){
				 * doc1.append("loginotherusers",
				 * memberDetails.getInt("loginotherusers")); }
				 * if(memberDetails.containsKey("createissueforothers")){
				 * doc1.append("createissueforothers",
				 * memberDetails.getInt("createissueforothers")); }
				 * if(memberDetails.containsKey("sendmessages")){
				 * doc1.append("sendmessages",
				 * memberDetails.getInt("sendmessages")); }
				 * if(memberDetails.containsKey("createduetemplate")){
				 * doc1.append("createduetemplate",
				 * memberDetails.getInt("createduetemplate")); }
				 * if(memberDetails.containsKey("followupissues")){
				 * doc1.append("followupissues",
				 * memberDetails.getInt("followupissues")); }
				 * if(memberDetails.containsKey("postannouncements")){
				 * doc1.append("postannouncements",
				 * memberDetails.getInt("postannouncements")); }
				 * if(memberDetails.containsKey("createalbums")){
				 * doc1.append("createalbums",
				 * memberDetails.getInt("createalbums")); }
				 * if(memberDetails.containsKey("networkgrouppublisher")){
				 * doc1.append("networkgrouppublisher",
				 * memberDetails.getInt("networkgrouppublisher")); }
				 * if(memberDetails.containsKey("lockreceipts")){
				 * doc1.append("lockreceipts",
				 * memberDetails.getInt("lockreceipts")); }
				 * if(memberDetails.containsKey("trackissues")){
				 * doc1.append("trackissues",
				 * memberDetails.getInt("trackissues")); }
				 * if(memberDetails.containsKey("staff")){ doc1.append("staff",
				 * memberDetails.getInt("staff")); }
				 * if(memberDetails.containsKey("addhelpers")){
				 * doc1.append("addhelpers",
				 * memberDetails.getInt("addhelpers")); }
				 * if(memberDetails.containsKey("viewconfidentialinfo")){
				 * doc1.append("viewconfidentialinfo",
				 * memberDetails.getInt("viewconfidentialinfo")); }
				 * if(memberDetails.containsKey("canapprove")){
				 * doc1.append("canapprove",
				 * memberDetails.getInt("canapprove")); }
				 * if(memberDetails.containsKey("canrecordmessage")){
				 * doc1.append("canrecordmessage",
				 * memberDetails.getInt("canrecordmessage")); }
				 * if(memberDetails.containsKey("enableattendance")){
				 * doc1.append("enableattendance",
				 * memberDetails.getInt("enableattendance")); }
				 * if(memberDetails.containsKey("selection")){
				 * doc1.append("selection",
				 * memberDetails.getJSONObject("selection")); }
				 * doc1.append("lastUpdatedTime",
				 * RestUtils.getLastSynchTime().toString());
				 */

				// System.out.println(doc1);

				MongoCollection<Document> collection1 = db
						.getCollection("memberdetails");
				// collection1.insertOne(doc1);
				memberDetails.put(GlobalTags.LAST_UPDATED_TIME_TAG, RestUtils
						.getLastSynchTime().toString());
				Document member = new Document(memberDetails);
				collection1.insertOne(member);
				System.out.println("--" + member);

				Object id = member.get("_id");
				if (id == null) {
					System.out
							.println("Problem Occured while inserting in memberdetails");
					resp = RestUtils.processError(
							PropertiesUtil.getProperty("insertError_code"),
							PropertiesUtil.getProperty("insertError_message"));
					return resp;
				} else {
					JSONObject data = new JSONObject();
					data.put("session_id", id + "");
					data.put("groupzcode",
							memberDetails.getString("groupzcode"));
					data.put("displayname",
							memberDetails.getString("membername") + " - "
									+ groupzDetails.getString("shortname"));
					data.put("profileurl", memberDetails.get("profileurl"));
					userRes.add(data);
				}
			}
			resp = userRes.toString();
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception -- insertValuesInToTables");
			resp = RestUtils.processError(
					PropertiesUtil.getProperty("insertError_code"),
					PropertiesUtil.getProperty("insertError_message"));
			return resp;
		}
	}

	private boolean isAdminLogin(String response) {
		boolean isAdmin = false;
		try {
			System.out.println("Inside Validating");
			if (response != null) {
				Object object = JSONObject.fromObject(response);
				if (object instanceof JSONObject) {
					System.out.println("JSON Object");
					JSONObject resObj = JSONObject.fromObject(response);
					if (resObj.containsKey("json")) {
						if (resObj.getJSONObject("json")
								.containsKey("response")) {
							if (resObj.getJSONObject("json")
									.getJSONObject("response")
									.containsKey("data")) {
								if (resObj.getJSONObject("json")
										.getJSONObject("response")
										.getJSONObject("data")
										.containsKey("admindetails")) {
									isAdmin = true;
									return isAdmin;
								}
							}
						}
					}
				} else {
					System.out.println("Not instanceof");
					return isAdmin;
				}
			}
		} catch (Exception e) {
			System.out.println("Exception Occured");
			return isAdmin;
		}

		return isAdmin;
	}
}
