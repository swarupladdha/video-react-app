package com.managers;

import org.bson.Document;
import org.bson.types.ObjectId;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.connection.Mongo_Connection;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.utils.GlobalTags;
import com.utils.PropertiesUtil;
import com.utils.RestUtils;

public class Add_ServiceManager {

	public String getResponse(String regRequest) {
		System.out.println("Insde Add_ServiceManager getResponse!");
		String response = "";
		String servicetype = "";
		String functiontype = "";
		System.out.println(regRequest);
		try {
			if (RestUtils.isJSONValid(regRequest) == false) {
				response = RestUtils.processError(
						PropertiesUtil.getProperty("invalidJson_code"),
						PropertiesUtil.getProperty("invalidJson_message"));
				return response;
			}
			JSONObject json = new JSONObject();
			json = JSONObject.fromObject(regRequest);
			JSONObject requestObj = json.getJSONObject(GlobalTags.JSON_TAG)
					.getJSONObject(GlobalTags.JSON_REQUEST_TAG);

			servicetype = requestObj.getString(GlobalTags.SERVICE_TYPE_TAG);
			if (RestUtils.isEmpty(servicetype) == false) {
				response = RestUtils.processError(PropertiesUtil
						.getProperty("invalidServicetype_code"), PropertiesUtil
						.getProperty("invalidServicetype_message"));
				return response;
			}

			functiontype = requestObj.getString(GlobalTags.FUNCTION_TYPE_TAG);
			if (RestUtils.isEmpty(functiontype) == false) {
				response = RestUtils.processError(PropertiesUtil
						.getProperty("invalidFunctiontype_code"),
						PropertiesUtil
								.getProperty("invalidFunctiontype_message"));
				return response;
			}
			if (functiontype.equalsIgnoreCase(PropertiesUtil
					.getProperty("add_service_functiontype"))) {
				System.out.println(functiontype);
				JSONObject data = requestObj.getJSONObject("data");
				System.out.println(data);
				response = addServiceToDb(servicetype, functiontype, data);
			}
			if (functiontype.equalsIgnoreCase(PropertiesUtil
					.getProperty("list_service_functiontype"))) {
				System.out.println(functiontype);
				JSONObject data = json.getJSONObject("json")
						.getJSONObject("request").getJSONObject("data");
				System.out.println(data);
				response = listServiceFromDb(servicetype, functiontype, data);
			}
			if (functiontype.equalsIgnoreCase(PropertiesUtil
					.getProperty("update_service_functiontype"))) {
				System.out.println(functiontype);
				JSONObject data = requestObj.getJSONObject("data");
				System.out.println(data);
				response = updateServiceInDb(servicetype, functiontype, data);
			}
			if (functiontype.equalsIgnoreCase(PropertiesUtil
					.getProperty("delete_service_functiontype"))) {
				System.out.println(functiontype);
				JSONObject data = requestObj.getJSONObject("data");
				System.out.println(data);
				response = deleteServiceFromDb(servicetype, functiontype, data);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response = RestUtils.processError(
					PropertiesUtil.getProperty("incompleteInput_code"),
					PropertiesUtil.getProperty("incompleteInput_message"));
			return response;
		}

		return response;
	}

	public String addServiceToDb(String servicetype, String functiontype,
			JSONObject data) {
		String resp = "";
		boolean sessionValidation = false;
		boolean groupzRefresh = false;
		boolean memberRefresh = false;
		try {
			Mongo_Connection conn = new Mongo_Connection();
			MongoDatabase db = conn.getConnection();
			MongoCollection<Document> collection = db
					.getCollection("authtables");
			String serviceType = PropertiesUtil
					.getProperty("getSessionDetailsServicetype");
			String functionType = data.getString("functionType");
			String contentServiceType = data.getString("contentServiceType");
			String contentFunctionServiceType = data
					.getString("contentFunctionServiceType");
			String uri = data.getString(GlobalTags.URI_TAG);
			String groupzmodulename = data
					.getString(GlobalTags.GROUPZ_MODULENAME_TAG);
			String roleOffset = data.getString(GlobalTags.ROLE_OFFSET_TAG);
			if (data.containsKey(GlobalTags.SESSION__VALIDATE_TAG)) {
				sessionValidation = data
						.getBoolean(GlobalTags.SESSION__VALIDATE_TAG);
			} else {
				resp = RestUtils.processError(
						PropertiesUtil.getProperty("flag_missing_code"),
						PropertiesUtil.getProperty("flag_missing_message"));
				return resp;
			}
			if (data.containsKey(GlobalTags.GROUPZ_REFRESH_TAG)) {
				groupzRefresh = data.getBoolean(GlobalTags.GROUPZ_REFRESH_TAG);
			} else {
				resp = RestUtils.processError(
						PropertiesUtil.getProperty("flag_missing_code"),
						PropertiesUtil.getProperty("flag_missing_message"));
				return resp;
			}
			if (data.containsKey(GlobalTags.MEM_REFRESH_TAG)) {
				memberRefresh = data.getBoolean(GlobalTags.MEM_REFRESH_TAG);
			} else {
				resp = RestUtils.processError(
						PropertiesUtil.getProperty("flag_missing_code"),
						PropertiesUtil.getProperty("flag_missing_message"));
				return resp;
			}
			if (RestUtils.isEmpty(functionType) == false) {
				resp = RestUtils.processError(PropertiesUtil
						.getProperty("invalidFunctiontype_code"),
						PropertiesUtil
								.getProperty("invalidFunctiontype_message"));
				return resp;
			}
			if (RestUtils.isEmpty(contentServiceType) == false) {
				resp = RestUtils
						.processError(
								PropertiesUtil
										.getProperty("contentServicetype_missing_code"),
								PropertiesUtil
										.getProperty("contentServicetype_missing_message"));
				return resp;
			}
			if (RestUtils.isEmpty(contentFunctionServiceType) == false) {
				resp = RestUtils
						.processError(
								PropertiesUtil
										.getProperty("contentFunctiontype_missing_code"),
								PropertiesUtil
										.getProperty("contentFunctiontype_missing_message"));
				return resp;
			}
			if (RestUtils.isEmpty(uri) == false) {
				resp = RestUtils.processError(
						PropertiesUtil.getProperty("url_missing_code"),
						PropertiesUtil.getProperty("url_missing_message"));
				return resp;
			}
			if (RestUtils.isEmpty(groupzmodulename) == false) {
				resp = RestUtils
						.processError(
								PropertiesUtil
										.getProperty("groupzModule_missing_code"),
								PropertiesUtil
										.getProperty("groupzModule_missing_message"));
				return resp;
			}
			if (RestUtils.isEmpty(roleOffset) == false) {
				resp = RestUtils.processError(PropertiesUtil
						.getProperty("roleOffset_missing_code"), PropertiesUtil
						.getProperty("roleOffset_missing_message"));
				return resp;
			}
			// JSONArray a = new JSONArray();
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put(GlobalTags.FUNCTION_TYPE_TAG,
					Integer.parseInt(functionType));
			System.out.println(searchQuery);
			FindIterable<Document> res = collection.find(searchQuery);
			MongoCursor<Document> cursor = res.iterator();
			while (cursor.hasNext()) {
				// Document doc = cursor.next();
				// a.add(doc);
				System.out.println(cursor.next());
				resp = RestUtils.processError(
						PropertiesUtil.getProperty("service_present_code"),
						PropertiesUtil.getProperty("service_present_message"));
				return resp;
			}
			// resp = a.toString();
			// System.out.println(serviceType);
			// System.out.println(functionType);
			// System.out.println(contentServiceType);
			// System.out.println(contentFunctionServiceType);
			// System.out.println(uri);
			// System.out.println(groupzmodulename);
			// System.out.println(roleOffset);
			// System.out.println(sessionValidation);

			Document doc = new Document();
			doc.append(GlobalTags.SERVICE_TYPE_TAG,
					Integer.parseInt(serviceType));
			doc.append(GlobalTags.FUNCTION_TYPE_TAG,
					Integer.parseInt(functionType));
			doc.append(GlobalTags.CONTENT_ST_TYPE_TAG,
					Integer.parseInt(contentServiceType));
			doc.append(GlobalTags.CONTENT_FT_TAG,
					Integer.parseInt(contentFunctionServiceType));
			doc.append(GlobalTags.GROUPZ_MODULENAME_TAG, groupzmodulename);
			doc.append(GlobalTags.ROLE_OFFSET_TAG, roleOffset);
			doc.append(GlobalTags.URI_TAG, uri);
			doc.append(GlobalTags.SESSION__VALIDATE_TAG, sessionValidation);
			doc.append(GlobalTags.GROUPZ_REFRESH_TAG, groupzRefresh);
			doc.append(GlobalTags.MEM_REFRESH_TAG, memberRefresh);

			System.out.println(doc);
			collection.insertOne(doc);
			ObjectId id = doc.getObjectId(GlobalTags.ID_UND_TAG);
			System.out.println(id);
			if (id == null) {
				resp = RestUtils.processError(
						PropertiesUtil.getProperty("incompleteInput_code"),
						PropertiesUtil.getProperty("incompleteInput_message"));
				return resp;
			}
			JSONObject j = new JSONObject();
			JSONObject json = new JSONObject();
			JSONObject res1 = new JSONObject();
			JSONObject datas = new JSONObject();
			datas.put("id", id + "");
			res1.put(GlobalTags.STATUS_CODE_TAG, Integer
					.parseInt(PropertiesUtil
							.getProperty("statuscodesuccessvalue")));
			res1.put(GlobalTags.STATUS_MSG_TAG,
					PropertiesUtil.getProperty("statusmessagesuccessvalue"));
			res1.put(GlobalTags.SERVICE_TYPE_TAG, servicetype);
			res1.put(GlobalTags.FUNCTION_TYPE_TAG, functiontype);
			res1.put(GlobalTags.JSON_DATA_TAG, datas);
			json.put(GlobalTags.RESPONSE_TAG, res1);
			j.put(GlobalTags.JSON_TAG, json);
			resp = j.toString();
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
			resp = RestUtils.processError(
					PropertiesUtil.getProperty("incompleteInput_code"),
					PropertiesUtil.getProperty("incompleteInput_message"));
			return resp;
		}
	}

	public String listServiceFromDb(String servicetype, String functiontype,
			JSONObject data) {
		String resp = "";
		try {
			int limit = -1;
			int offset = -1;
			if (data.containsKey(GlobalTags.LIMIT_TAG)) {
				limit = data.getInt(GlobalTags.LIMIT_TAG);
			}
			if (data.containsKey(GlobalTags.OFFSET_TAG)) {
				offset = data.getInt(GlobalTags.OFFSET_TAG);
			}
			JSONArray list = new JSONArray();
			Mongo_Connection conn = new Mongo_Connection();
			MongoDatabase db = conn.getConnection();
			MongoCollection<Document> col = db.getCollection("authtables");
			FindIterable<Document> res = null;
			if (limit != -1 && offset != -1) {
				res = col.find().skip(offset).limit(limit);
			} else if (limit != -1) {
				res = col.find().limit(limit);
			} else if (limit != -1) {
				res = col.find().skip(offset);
			} else {
				res = col.find();
			}
			MongoCursor<Document> cursor = res.iterator();
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				String id = doc.get("_id") + "";
				doc.remove("_id");
				doc.put("id", id);
				list.add(doc);
			}
			JSONObject j = new JSONObject();
			JSONObject json = new JSONObject();
			JSONObject res1 = new JSONObject();
			// JSONObject datas = new JSONObject();
			res1.put(GlobalTags.STATUS_CODE_TAG, Integer
					.parseInt(PropertiesUtil
							.getProperty("statuscodesuccessvalue")));
			res1.put(GlobalTags.STATUS_MSG_TAG,
					PropertiesUtil.getProperty("statusmessagesuccessvalue"));
			res1.put(GlobalTags.SERVICE_TYPE_TAG, servicetype);
			res1.put(GlobalTags.FUNCTION_TYPE_TAG, functiontype);
			res1.put(GlobalTags.JSON_DATA_TAG, list);
			json.put(GlobalTags.RESPONSE_TAG, res1);
			j.put(GlobalTags.JSON_TAG, json);
			resp = j.toString();

		} catch (Exception e) {
			e.printStackTrace();
			resp = RestUtils.processError(
					PropertiesUtil.getProperty("incompleteInput_code"),
					PropertiesUtil.getProperty("incompleteInput_message"));
			return resp;
		}
		return resp;
	}

	public String updateServiceInDb(String servicetype, String functiontype,
			JSONObject data) {
		String resp = "";
		boolean sessionValidation = false;
		boolean groupzRefresh = false;
		boolean memberRefresh = false;
		try {
			String id = data.getString(GlobalTags.ID_TAG);
			Mongo_Connection conn = new Mongo_Connection();
			MongoDatabase db = conn.getConnection();
			MongoCollection<Document> collection = db
					.getCollection("authtables");

			BasicDBObject where = new BasicDBObject();
			where.put("_id", new ObjectId(id + ""));
			FindIterable<Document> values = collection.find(where);
			System.out.println(where);
			MongoCursor<Document> cursor = values.iterator();
			if (cursor.hasNext()) {

			} else {
				resp = RestUtils.processError(
						PropertiesUtil.getProperty("invalid_Id_code"),
						PropertiesUtil.getProperty("invalid_Id_message"));
				return resp;
			}
			BasicDBObject doc = new BasicDBObject();
			if (data.containsKey(GlobalTags.FUNCTION_TYPE_TAG)) {
				String functionType = data
						.getString(GlobalTags.FUNCTION_TYPE_TAG);
				doc.append(GlobalTags.FUNCTION_TYPE_TAG,
						Integer.parseInt(functionType));
			}
			if (data.containsKey(GlobalTags.CONTENT_ST_TYPE_TAG)) {
				String contentServiceType = data
						.getString(GlobalTags.CONTENT_ST_TYPE_TAG);
				doc.append(GlobalTags.CONTENT_ST_TYPE_TAG,
						Integer.parseInt(contentServiceType));
			}
			if (data.containsKey(GlobalTags.CONTENT_FST_TYPE_TAG)) {
				String contentFunctionServiceType = data
						.getString(GlobalTags.CONTENT_FST_TYPE_TAG);
				doc.append(GlobalTags.CONTENT_FT_TAG,
						Integer.parseInt(contentFunctionServiceType));
			}
			if (data.containsKey(GlobalTags.URI_TAG)) {
				String uri = data.getString(GlobalTags.URI_TAG);
				doc.append(GlobalTags.URI_TAG, uri);
			}
			if (data.containsKey(GlobalTags.GROUPZ_MODULENAME_TAG)) {
				String groupzmodulename = data
						.getString(GlobalTags.GROUPZ_MODULENAME_TAG);
				doc.append(GlobalTags.GROUPZ_MODULENAME_TAG, groupzmodulename);
			}
			if (data.containsKey(GlobalTags.ROLE_OFFSET_TAG)) {
				String roleOffset = data.getString(GlobalTags.ROLE_OFFSET_TAG);
				doc.append(GlobalTags.ROLE_OFFSET_TAG, roleOffset);
			}
			if (data.containsKey(GlobalTags.SESSION__VALIDATE_TAG)) {
				sessionValidation = data
						.getBoolean(GlobalTags.SESSION__VALIDATE_TAG);
				doc.append(GlobalTags.SESSION__VALIDATE_TAG, sessionValidation);
			}
			if (data.containsKey(GlobalTags.GROUPZ_REFRESH_TAG)) {
				groupzRefresh = data.getBoolean(GlobalTags.GROUPZ_REFRESH_TAG);
				doc.append(GlobalTags.GROUPZ_REFRESH_TAG, groupzRefresh);
			}
			if (data.containsKey(GlobalTags.MEM_REFRESH_TAG)) {
				memberRefresh = data.getBoolean(GlobalTags.MEM_REFRESH_TAG);
				doc.append(GlobalTags.MEM_REFRESH_TAG, memberRefresh);
			}

			BasicDBObject setQuery = new BasicDBObject();
			setQuery.put("$set", doc);
			System.out.println(where);
			System.out.println(setQuery);
			collection.updateOne(where, setQuery);

			JSONObject j = new JSONObject();
			JSONObject json = new JSONObject();
			JSONObject res1 = new JSONObject();
			JSONObject datas = new JSONObject();
			datas.put("id", id + "");
			res1.put(GlobalTags.STATUS_CODE_TAG, Integer
					.parseInt(PropertiesUtil
							.getProperty("statuscodesuccessvalue")));
			res1.put(GlobalTags.STATUS_MSG_TAG,
					PropertiesUtil.getProperty("statusmessagesuccessvalue"));
			res1.put(GlobalTags.SERVICE_TYPE_TAG, servicetype);
			res1.put(GlobalTags.FUNCTION_TYPE_TAG, functiontype);
			res1.put(GlobalTags.JSON_DATA_TAG, datas);
			json.put(GlobalTags.RESPONSE_TAG, res1);
			j.put(GlobalTags.JSON_TAG, json);
			resp = j.toString();
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
			resp = RestUtils.processError(
					PropertiesUtil.getProperty("incompleteInput_code"),
					PropertiesUtil.getProperty("incompleteInput_message"));
			return resp;
		}
	}

	private String deleteServiceFromDb(String servicetype, String functiontype,
			JSONObject data) {
		String resp = "";
		try {
			Mongo_Connection conn = new Mongo_Connection();
			MongoDatabase db = conn.getConnection();
			MongoCollection<Document> collection = db
					.getCollection("authtables");
			String id = data.getString(GlobalTags.ID_TAG);
			BasicDBObject query = new BasicDBObject();
			query.put("_id", new ObjectId(id + ""));
			FindIterable<Document> values = collection.find(query);
			System.out.println(query);
			MongoCursor<Document> re = values.iterator();
			if (re.hasNext()) {
				System.out.println(re.next());
				collection.deleteOne(query);
				JSONObject j = new JSONObject();
				JSONObject json = new JSONObject();
				JSONObject res1 = new JSONObject();
				JSONObject datas = new JSONObject();
				datas.put(GlobalTags.ID_TAG, id);
				res1.put(GlobalTags.STATUS_CODE_TAG, Integer
						.parseInt(PropertiesUtil
								.getProperty("statuscodesuccessvalue")));
				res1.put(GlobalTags.STATUS_MSG_TAG,
						PropertiesUtil.getProperty("statusmessagesuccessvalue"));
				res1.put(GlobalTags.SERVICE_TYPE_TAG, servicetype);
				res1.put(GlobalTags.FUNCTION_TYPE_TAG, functiontype);
				res1.put(GlobalTags.JSON_DATA_TAG, datas);
				json.put(GlobalTags.RESPONSE_TAG, res1);
				j.put(GlobalTags.JSON_TAG, json);
				resp = j.toString();
				return resp;
			} else {
				resp = RestUtils.processError(
						PropertiesUtil.getProperty("invalid_Id_code"),
						PropertiesUtil.getProperty("invalid_Id_message"));
				return resp;
			}

		} catch (Exception e) {
			e.printStackTrace();
			resp = RestUtils.processError(
					PropertiesUtil.getProperty("incompleteInput_code"),
					PropertiesUtil.getProperty("incompleteInput_message"));
			return resp;
		}
	}
}
