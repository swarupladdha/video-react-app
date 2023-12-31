package com.managers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bson.Document;
import org.bson.types.ObjectId;

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

public class AuthenticatorManager {
	Mongo_Connection conn = new Mongo_Connection();
	MongoDatabase db = conn.getConnection();
	RestUtils utils = new RestUtils();
	int groupzid = 0;
	int memberid = 0;
	int adminId = 0;
	int manageusers = 0;
	String groupzbasekey = "";
	String groupzCode = "";
	String groupzBaseKey = "";
	String groupzUpdatedTime = "";
	String memberUpdatedTime = "";

	public String getResponse(String regRequest) {
		System.out.println("Inside AuthenticatorManager getResponse");
		String response = "";
		String servicetype = "";
		String functiontype = "";
		System.out.println("Mongo Request Is : " + regRequest);
		try {
			if (RestUtils.isJSONValid(regRequest) == false) {
				response = RestUtils.processError(
						PropertiesUtil.getProperty("invalidJson_code"),
						PropertiesUtil.getProperty("invalidJson_message"));
				return response;
			}
			JSONObject json = new JSONObject();
			json = JSONObject.fromObject(regRequest);
			JSONObject request = json.getJSONObject("json").getJSONObject(
					"request");

			servicetype = request.getString("servicetype");
			if (RestUtils.isEmpty(servicetype) == false) {
				response = RestUtils.processError(PropertiesUtil
						.getProperty("invalidServicetype_code"), PropertiesUtil
						.getProperty("invalidServicetype_message"));
				return response;
			}

			functiontype = request.getString("functiontype");
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
				System.out.println("==================");
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
			boolean sessionvalidation = jObj.getBoolean("sessionvalidation");
			boolean groupzRefresh = false;
			boolean memberRefresh = false;
			if (jObj.containsKey(GlobalTags.GROUPZ_REFRESH_TAG)) {
				groupzRefresh = jObj.getBoolean(GlobalTags.GROUPZ_REFRESH_TAG);
			} else if (jObj.containsKey("groupzRefresh")) {
				groupzRefresh = jObj.getBoolean("groupzRefresh");
			}
			if (jObj.containsKey(GlobalTags.MEM_REFRESH_TAG)) {
				memberRefresh = jObj.getBoolean(GlobalTags.MEM_REFRESH_TAG);
			} else if (jObj.containsKey("memberRefresh")) {
				memberRefresh = jObj.getBoolean("memberRefresh");
			}
			System.out.println("GROUPZREFRESH : " + groupzRefresh
					+ "MEMBERREFRESH : " + memberRefresh);
			if ((sessionvalidation == true)
					&& (request.containsKey("session_id") == true)) {
				System.out
						.println("-------------------------------------------");
				if (request.containsKey("groupzCode")) {
					groupzCode = request.getString("groupzCode");
				}
				if (request.containsKey("groupzbasekey")) {
					groupzBaseKey = request.getString("groupzbasekey");
				}
				Object data = request.get("data");
				String sessionId = request.getString("session_id");
				JSONArray dataArray = new JSONArray();
				if (data instanceof JSONArray) {
					dataArray = request.getJSONArray("data");
					response = getDeatilsAndBackendResponse(sessionId, out,
							groupzCode, dataArray);
				}
				JSONObject dataObj = new JSONObject();
				if (data instanceof JSONObject) {
					dataObj = request.getJSONObject("data");
					response = getDeatilsAndBackendResponse(sessionId, out,
							groupzCode, groupzBaseKey, dataObj);
				}

				// JSONObject data = request.getJSONObject("data");
				// String sessionId = request.getString("session_id");
				// response =
				// getDeatilsAndBackendResponse(sessionId,out,groupzCode,data);
				System.out.println("---" + response);
				if (response != null) {

					JSONObject bResponse = JSONObject.fromObject(response);
					if (bResponse
							.getJSONObject("json")
							.getJSONObject("response")
							.getString("statuscode")
							.equals(PropertiesUtil
									.getProperty("permissionError_code"))) {
						response = RestUtils
								.processError(
										PropertiesUtil
												.getProperty("permissionError_code"),
										PropertiesUtil
												.getProperty("permissionError_message"));
						return response;
					} else if (bResponse
							.getJSONObject("json")
							.getJSONObject("response")
							.getString("statuscode")
							.equals(PropertiesUtil
									.getProperty("invalid_session_code"))) {
						return response;
					} else if (bResponse
							.getJSONObject("json")
							.getJSONObject("response")
							.getString("statuscode")
							.equals(PropertiesUtil
									.getProperty("technical_issue_code"))) {
						response = RestUtils
								.processError(
										PropertiesUtil
												.getProperty("technical_issue_code"),
										PropertiesUtil
												.getProperty("technical_issue_message"));
						return response;
					}
					if (bResponse
							.getJSONObject("json")
							.getJSONObject("response")
							.getString("statuscode")
							.equals(PropertiesUtil
									.getProperty("statuscodesuccessvalue"))) {
						bResponse.getJSONObject("json")
								.getJSONObject("response")
								.remove("servicetype");
						bResponse.getJSONObject("json")
								.getJSONObject("response")
								.remove("functiontype");
						bResponse.getJSONObject("json")
								.getJSONObject("response")
								.put("servicetype", servicetype);
						bResponse.getJSONObject("json")
								.getJSONObject("response")
								.put("functiontype", functiontype);
						MongoDatabase db1 = conn.getConnection();
						MongoCollection<Document> updateCollection = db1
								.getCollection("updategroupz");
						if (groupzRefresh) {
							System.out.println("Updating For GroupzRefresh");
							BasicDBObject whereQuery = new BasicDBObject();
							List<BasicDBObject> list = new ArrayList<BasicDBObject>();
							list.add(new BasicDBObject("groupzcode", groupzCode));
							list.add(new BasicDBObject("groupzbasekey",
									groupzbasekey));
							whereQuery.put("$and", list);
							System.out.println("whereQuery : " + whereQuery);
							FindIterable<Document> resultant = updateCollection
									.find(whereQuery);
							MongoCursor<Document> result = resultant.iterator();
							if (result.hasNext()) {
								System.out.println("Got Value");
								BasicDBObject set = new BasicDBObject();
								set.put(GlobalTags.LAST_UPDATED_TIME_TAG,
										RestUtils.getLastSynchTime().toString());
								BasicDBObject updateQuery = new BasicDBObject();
								updateQuery.put("$set", set);
								updateCollection.updateOne(whereQuery,
										updateQuery);
							} else {
								System.out
										.println("----------------------------------------");
								System.out.println("No Values Found!");
								System.out
										.println("----------------------------------------");
								System.out.println("groupzid " + groupzid);
								System.out.println("memberid " + memberid);
								System.out.println("groupz "
										+ groupzUpdatedTime);
								System.out.println("member "
										+ memberUpdatedTime);
								Document insertQuery = new Document();
								insertQuery.append("groupzcode", groupzCode);
								insertQuery.append("groupzbasekey",
										groupzbasekey);
								insertQuery.append("memberid", memberid);
								insertQuery
										.append(GlobalTags.LAST_UPDATED_TIME_TAG,
												RestUtils.getLastSynchTime()
														.toString());
								insertQuery.append(
										GlobalTags.PROCESSED_TIME_TAG, null);
								updateCollection.insertOne(insertQuery);
							}
						} else {
							System.out.println("groupzRefresh Disabled");
						}

						return bResponse.toString();
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
			}

			else {
				System.out.println("No Session Id Or Session Validation");
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
		} finally {
			Mongo_Connection conn = new Mongo_Connection();
			conn.closeConnection();
		}
	}

	private String getServicetypeAndFunctiontypefromDB(String serviceType,
			String functionType) {
		String res = "";
		try {
			System.out.println("ST : " + serviceType + " FT : " + functionType);
			MongoCollection<Document> collection = db
					.getCollection("authtables");

			BasicDBObject whereQuery = new BasicDBObject();
			List<BasicDBObject> list = new ArrayList<BasicDBObject>();
			list.add(new BasicDBObject("servicetype", Integer
					.parseInt(serviceType)));
			list.add(new BasicDBObject("functiontype", Integer
					.parseInt(functionType)));
			whereQuery.put("$and", list);

			System.out.println(whereQuery.toString());

			FindIterable<Document> result = collection.find(whereQuery);
			MongoCursor<Document> re = null;
			try {
				re = result.iterator();
				JSONArray datas = new JSONArray();
				System.out.println(re.toString() + re.hasNext());
				if (re.hasNext()) {
					while (re.hasNext()) {
						JSONObject val = new JSONObject();
						Document value = re.next();
						val.put("servicetype",
								value.getInteger("contentservicetype"));
						val.put("functiontype",
								value.getInteger("contentfunctiontype"));
						val.put("roleoffset", value.getString("roleoffset"));
						val.put("url", value.getString("uri"));
						val.put(GlobalTags.GROUPZ_MODULENAME_TAG, value
								.getString(GlobalTags.GROUPZ_MODULENAME_TAG));
						val.put(GlobalTags.SESSION__VALIDATE_TAG, value
								.getBoolean(GlobalTags.SESSION__VALIDATE_TAG));
						// Handling for older conflicts of camel case letters
						if (value.containsKey(GlobalTags.GROUPZ_REFRESH_TAG)) {
							val.put(GlobalTags.GROUPZ_REFRESH_TAG, value
									.getBoolean(GlobalTags.GROUPZ_REFRESH_TAG));
						} else if (value.containsKey("groupzRefresh")) {
							val.put(GlobalTags.GROUPZ_REFRESH_TAG,
									value.getBoolean("groupzRefresh"));
						}
						if (value.containsKey(GlobalTags.MEM_REFRESH_TAG)) {
							val.put(GlobalTags.MEM_REFRESH_TAG, value
									.getBoolean(GlobalTags.MEM_REFRESH_TAG));
						} else if (value.containsKey("memberRefresh")) {
							val.put(GlobalTags.MEM_REFRESH_TAG,
									value.getBoolean("memberRefresh"));
						}
						datas.add(val);
					}
					res = datas.toString();
					System.out.println("Value from session ");
					System.out.println(datas.toString());
					return res;
				} else {
					System.out.println("No Data Found!");
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (re != null) {
					re.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out
					.println("Exception in getServicetypeAndFunctiontypefromDB");
			return null;
		}
		return res;
	}

	private String getDeatilsAndBackendResponse(String sessionId, String out,
			String groupzCode, String baseKey, JSONObject data) {
		System.out.println("Inside getDeatilsAndBackendResponse on Object");
		String resp = "";

		try {
			int len = sessionId.length();
			MongoCollection<Document> collection = db
					.getCollection("memberdetails");
			BasicDBObject query = new BasicDBObject();
			// if (len <= 9){
			// System.out.println("Id is String");
			// query.put("_id",sessionId);
			// }
			// else{
			System.out.println("Id is Object");
			query.put("_id", new ObjectId(sessionId));
			// }
			FindIterable<Document> values = collection.find(query);
			System.out.println(query);
			MongoCursor<Document> re = values.iterator();
			if (re.hasNext()) {
				Document value = re.next();
				System.out.println(value);
				if (value.containsKey("adminid")) {
					System.out.println("admin request");
					adminId = value.getInteger("adminid");
				}
				if (value.containsKey("groupzid")) {
					groupzid = value.getInteger("groupzid");
				}
				if (value.containsKey("memberid")) {
					memberid = value.getInteger("memberid");
				}
				if (value.containsKey("manageusers")) {
					manageusers = value.getInteger("manageusers");
				}
				memberUpdatedTime = value
						.getString(GlobalTags.LAST_UPDATED_TIME_TAG);
			} else {
				resp = RestUtils.processError(
						PropertiesUtil.getProperty("invalid_session_code"),
						PropertiesUtil.getProperty("invalid_session_message"));
				return resp;
			}
			System.out.println(groupzid + "---" + memberid + manageusers);
			MongoCollection<Document> collection1 = db
					.getCollection("groupzdetails");
			BasicDBObject query1 = new BasicDBObject();
			query1.put("groupzid", groupzid);
			System.out.println(query1);
			FindIterable<Document> values1 = collection1.find(query1);
			System.out.println(query1);
			MongoCursor<Document> re1 = values1.iterator();
			if (re1.hasNext()) {
				Document value = re1.next();
				groupzbasekey = value.getString("groupzbasekey");
				groupzUpdatedTime = value
						.getString(GlobalTags.LAST_UPDATED_TIME_TAG);
			}
			// else{
			// return null;
			// }
			System.out.println("---------------------");
			System.out.println(groupzbasekey);
			System.out.println("---------------------");
			String roleoffset = "";
			JSONArray requestArray = new JSONArray();
			requestArray = JSONArray.fromObject(out);
			System.out.println(requestArray.size());
			if (requestArray.size() >= 1) {
				JSONObject req = requestArray.getJSONObject(0);
				System.out.println(req);
				String url = req.getString("url");
				roleoffset = req.getString("roleoffset");
				JSONObject request = new JSONObject();
				request.put("servicetype", req.getString("servicetype"));
				request.put("functiontype", req.getString("functiontype"));
				request.put("groupzCode", groupzCode);
				request.put("groupzbasekey", baseKey);
				request.put("data", data);

				// System.out.println("---"+roleoffset+"---"+manageusers);

				if (!roleoffset.equalsIgnoreCase("*")) {
					if (manageusers == 0) {
						System.out.println("No Manage Users Permission");
						resp = RestUtils
								.processError(
										PropertiesUtil
												.getProperty("permissionError_code"),
										PropertiesUtil
												.getProperty("permissionError_message"));
						// return resp;
					}
				}
				if (memberid != 0) {
					request.put("memberid", memberid);
				}
				if (adminId != 0) {
					request.put("adminid", adminId);
				}
				if (RestUtils.isEmpty(groupzbasekey) == true) {
					request.put("groupzbasekey", groupzbasekey);
				}
				JSONObject json = new JSONObject();
				json.put("request", request);
				JSONObject js = new JSONObject();
				js.put("json", json);
				// System.out.println(url+"?request="+RestUtils.encode(js.toString()));
				String reqs = js.toString();
				// java.net.URLEncoder.encode(reqs,"UTF-8");
				ConnectionUtils cu = new ConnectionUtils();
				resp = cu.ConnectandRecieve(url + "?request=", reqs);
				if (resp == null) {
					resp = RestUtils.processError(PropertiesUtil
							.getProperty("technical_issue_code"),
							PropertiesUtil
									.getProperty("technical_issue_message"));
					return resp;
				}
				return resp;
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception -- getDeatilsAndBackendResponse");
			return null;
		}
	}

	private String getDeatilsAndBackendResponse(String sessionId, String out,
			String groupzCode, JSONArray data) {
		System.out.println("Inside getDeatilsAndBackendResponse On Array");
		String resp = "";
		try {
			int len = sessionId.length();
			MongoCollection<Document> collection = db
					.getCollection("memberdetails");
			BasicDBObject query = new BasicDBObject();
			System.out.println("Id is Object");
			query.put("_id", new ObjectId(sessionId));
			FindIterable<Document> values = collection.find(query);
			System.out.println(query);
			MongoCursor<Document> re = values.iterator();
			System.out.println("re : " + re.toString());
			if (re.hasNext()) {
				Document value = re.next();
				System.out.println(value);
				groupzid = value.getInteger("groupzid");
				memberid = value.getInteger("memberid");
				manageusers = value.getInteger("manageusers");
				memberUpdatedTime = value
						.getString(GlobalTags.LAST_UPDATED_TIME_TAG);
			} else {
				resp = RestUtils.processError(
						PropertiesUtil.getProperty("invalid_session_code"),
						PropertiesUtil.getProperty("invalid_session_message"));
				return resp;
			}
			System.out.println(groupzid + "---" + memberid + manageusers);
			MongoCollection<Document> collection1 = db
					.getCollection("groupzdetails");
			BasicDBObject query1 = new BasicDBObject();
			query1.put("groupzid", groupzid);
			System.out.println(query1);
			FindIterable<Document> values1 = collection1.find(query1);
			System.out.println(query1);
			MongoCursor<Document> re1 = values1.iterator();
			if (re1.hasNext()) {
				Document value = re1.next();
				groupzbasekey = value.getString("groupzbasekey");
				groupzUpdatedTime = value
						.getString(GlobalTags.LAST_UPDATED_TIME_TAG);
			}
			System.out.println("---------------------");
			System.out.println(groupzbasekey);
			System.out.println("---------------------");
			String roleoffset = "";
			JSONArray requestArray = new JSONArray();
			requestArray = JSONArray.fromObject(out);
			System.out.println(requestArray.size());
			if (requestArray.size() >= 1) {
				JSONObject req = requestArray.getJSONObject(0);
				System.out.println(req);
				String url = req.getString("url");
				roleoffset = req.getString("roleoffset");
				JSONObject request = new JSONObject();
				request.put("servicetype", req.getString("servicetype"));
				request.put("functiontype", req.getString("functiontype"));
				request.put("groupzCode", groupzCode);
				request.put("data", data);
				if (!roleoffset.equalsIgnoreCase("*")) {

					if (manageusers == 0) {
						resp = RestUtils
								.processError(
										PropertiesUtil
												.getProperty("permissionError_code"),
										PropertiesUtil
												.getProperty("permissionError_message"));
						return resp;
					}
				}
				if (memberid != 0) {
					request.put("memberid", memberid);
				}
				if (RestUtils.isEmpty(groupzbasekey) == true) {
					request.put("groupzbasekey", groupzbasekey);
				}
				JSONObject json = new JSONObject();
				json.put("request", request);
				JSONObject js = new JSONObject();
				js.put("json", json);
				// System.out.println(url+"?request="+RestUtils.encode(js.toString()));
				String reqs = js.toString();
				// java.net.URLEncoder.encode(reqs,"UTF-8");
				ConnectionUtils cu = new ConnectionUtils();
				resp = cu.ConnectandRecieve(url + "?request=", reqs);
				if (resp == null) {
					resp = RestUtils.processError(PropertiesUtil
							.getProperty("technical_issue_code"),
							PropertiesUtil
									.getProperty("technical_issue_message"));
					return resp;
				}
				return resp;
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception -- getDeatilsAndBackendResponse");
			return null;
		}
	}

	public String decode(String url) {
		try {
			System.out.println("Decoding");
			String prevURL = "";
			String decodeURL = url;
			while (!prevURL.equals(decodeURL)) {
				prevURL = decodeURL;
				decodeURL = URLDecoder.decode(decodeURL, "UTF-8");
			}
			System.out.println("Decoded URL : " + decodeURL);
			return decodeURL;
		} catch (UnsupportedEncodingException e) {
			return "Issue while decoding" + e.getMessage();
		}
	}

	public static String encode(String url) {
		try {
			System.out.println("Encoding");
			String encodeURL = URLEncoder.encode(url, "UTF-8");
			System.out.println("Encoded URL : " + encodeURL);
			return encodeURL;
		} catch (UnsupportedEncodingException e) {
			return "Issue while encoding" + e.getMessage();
		}
	}
}
