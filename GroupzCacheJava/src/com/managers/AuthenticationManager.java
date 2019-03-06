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

import config.DomainModifier;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AuthenticationManager {
	Mongo_Connection conn = new Mongo_Connection();
	MongoDatabase db = conn.getConnection();

	public String getResponse(String regRequest) {
		System.out.println("Insde AuthenticationManager getResponse!");
		 DomainModifier d = new DomainModifier();
		 d.changeDomain();
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
						// Admin Login Response
						System.out.println("validate For Admin");
						if (isAdminLogin(response)) {
							System.out.println("isAdminLogin(success) : "
									+ isAdminLogin(response));
							JSONArray arr = new JSONArray();
							JSONObject originalResObj = JSONObject
									.fromObject(response);
							JSONObject obj = JSONObject.fromObject(response)
									.getJSONObject("json")
									.getJSONObject("response")
									.getJSONObject("data")
									.getJSONObject("admindetails");
							JSONObject successSessionIdObj = JSONObject
									.fromObject(success);
							obj.put("session_id",
									successSessionIdObj.getString("session_id"));
							arr.add(obj);
							originalResObj.getJSONObject("json")
									.getJSONObject("response").remove("data");
							originalResObj.getJSONObject("json")
									.getJSONObject("response").put("user", arr);
							response = originalResObj.toString();
							return response;
						}
						JSONArray array = JSONArray.fromObject(success);
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
				System.out.println("Returned resp : " + resp);
				return resp;
			}
			// Admin Login Response
			if (res.containsKey(GlobalTags.JSON_DATA_TAG)) {
				if (res.getJSONObject(GlobalTags.JSON_DATA_TAG).containsKey(
						"admindetails")) {
					JSONObject memberDetails = res.getJSONObject(
							GlobalTags.JSON_DATA_TAG).getJSONObject(
							"admindetails");
					MongoCollection<Document> collectionOnAdminLogin = db
							.getCollection("memberdetails");
					// collection1.insertOne(doc1);
					memberDetails.put(GlobalTags.LAST_UPDATED_TIME_TAG,
							RestUtils.getLastSynchTime().toString());
					@SuppressWarnings("unchecked")
					Document member = new Document(memberDetails);
					collectionOnAdminLogin.insertOne(member);
					System.out.println("--" + member);

					Object id = member.get("_id");
					if (id == null) {
						System.out
								.println("Problem Occured while inserting in memberdetails");
						resp = RestUtils.processError(PropertiesUtil
								.getProperty("insertError_code"),
								PropertiesUtil
										.getProperty("insertError_message"));
						return resp;
					} else {
						JSONObject data = new JSONObject();
						data.put("session_id", id + "");
						System.out.println("Admin Login Session Details : "
								+ data.toString());
						return data.toString();
					}
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
				}

				// System.out.println("total Vales "+count);

				JSONObject memberDetails = user.getJSONObject(i).getJSONObject(
						"memberdetails");
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
			System.out.println("Inside Validating Response : " + response);
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
			e.printStackTrace();
			System.out.println("Exception Occured");
			return isAdmin;
		}
		System.out.println("ISADMIN : " + isAdmin);
		return isAdmin;
	}
}
