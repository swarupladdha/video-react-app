package com.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarOutputStream;

import org.bson.Document;
import org.bson.types.ObjectId;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.connection.Mongo_Connection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.utils.PropertiesUtil;
import com.utils.RestUtils;

public class Add_ServiceManager {
	
	public String getResponse(String regRequest) {
		System.out.println("Insde Add_ServiceManager getResponse");
		String response = "";
		String servicetype = "";
		String functiontype = "";
		System.out.println(regRequest);
		try{
			if(RestUtils.isJSONValid(regRequest) == false){
				response = RestUtils.processError(PropertiesUtil.getProperty("invalidJson_code"), PropertiesUtil.getProperty("invalidJson_message"));
				return response;
			}
			JSONObject json = new JSONObject();
			json = JSONObject.fromObject(regRequest);
			JSONObject request = json.getJSONObject("json").getJSONObject("request");
			
			
			servicetype = request.getString("servicetype");
			if(RestUtils.isEmpty(servicetype) == false ){
				response = RestUtils.processError(PropertiesUtil.getProperty("invalidServicetype_code"), PropertiesUtil.getProperty("invalidServicetype_message"));
				return response;
			}
			
			functiontype = request.getString("functiontype");
			if (RestUtils.isEmpty(functiontype)== false){
				response = RestUtils.processError(PropertiesUtil.getProperty("invalidFunctiontype_code"), PropertiesUtil.getProperty("invalidFunctiontype_message"));
				return response;
			}
			if (functiontype.equalsIgnoreCase(PropertiesUtil.getProperty("add_service_functiontype"))){
				System.out.println(functiontype);
				JSONObject data = json.getJSONObject("json").getJSONObject("request").getJSONObject("data");
				System.out.println(data);
				response = addServiceToDb(servicetype,functiontype,data);
			}
			if (functiontype.equalsIgnoreCase(PropertiesUtil.getProperty("list_service_functiontype"))){
				System.out.println(functiontype);
				JSONObject data = json.getJSONObject("json").getJSONObject("request").getJSONObject("data");
				System.out.println(data);
				response = listServiceFromDb(servicetype,functiontype,data);
			}
			if (functiontype.equalsIgnoreCase(PropertiesUtil.getProperty("update_service_functiontype"))){
				System.out.println(functiontype);
				JSONObject data = json.getJSONObject("json").getJSONObject("request").getJSONObject("data");
				System.out.println(data);
				response = updateServiceInDb(servicetype,functiontype,data);
			}
			if (functiontype.equalsIgnoreCase(PropertiesUtil.getProperty("delete_service_functiontype"))){
				System.out.println(functiontype);
				JSONObject data = json.getJSONObject("json").getJSONObject("request").getJSONObject("data");
				System.out.println(data);
				response = deleteServiceFromDb(servicetype,functiontype,data);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
			response = RestUtils.processError(PropertiesUtil.getProperty("incompleteInput_code"), PropertiesUtil.getProperty("incompleteInput_message"));
			return response;
		}
		
		return response;
	}

	public String addServiceToDb(String servicetype, String functiontype,JSONObject data) {
		String resp = "";
		boolean sessionValidation = false;
		try{
			MongoDatabase db = Mongo_Connection.getConnection();
			MongoCollection<Document> collection = db.getCollection("authtables");
			String serviceType = PropertiesUtil.getProperty("getSessionDetailsServicetype");
			String functionType = data.getString("functionType");
			String contentServiceType = data.getString("contentServiceType");
			String contentFunctionServiceType = data.getString("contentFunctionServiceType");
			String uri = data.getString("uri");
			String groupzmodulename = data.getString("groupzmodulename");
			String roleOffset = data.getString("roleOffset");
			if (data.containsKey("sessionValidation")){
				sessionValidation = data.getBoolean("sessionValidation");
			}
			else{
				resp = RestUtils.processError(PropertiesUtil.getProperty("sessionFlag_missing_code"), PropertiesUtil.getProperty("sessionFlag_missing_message"));
				return resp;
			}
			
			if (RestUtils.isEmpty(functionType) == false){
				resp = RestUtils.processError(PropertiesUtil.getProperty("invalidFunctiontype_code"), PropertiesUtil.getProperty("invalidFunctiontype_message"));
				return resp;
			}
			if (RestUtils.isEmpty(contentServiceType) == false){
				resp = RestUtils.processError(PropertiesUtil.getProperty("contentServicetype_missing_code"), PropertiesUtil.getProperty("contentServicetype_missing_message"));
				return resp;
			}
			if (RestUtils.isEmpty(contentFunctionServiceType) == false){
				resp = RestUtils.processError(PropertiesUtil.getProperty("contentFunctiontype_missing_code"), PropertiesUtil.getProperty("contentFunctiontype_missing_message"));
				return resp;
			}
			if (RestUtils.isEmpty(uri) == false){
				resp = RestUtils.processError(PropertiesUtil.getProperty("url_missing_code"), PropertiesUtil.getProperty("url_missing_message"));
				return resp;
			}
			if (RestUtils.isEmpty(groupzmodulename) == false){
				resp = RestUtils.processError(PropertiesUtil.getProperty("groupzModule_missing_code"), PropertiesUtil.getProperty("groupzModule_missing_message"));
				return resp;
			}
			if (RestUtils.isEmpty(roleOffset) == false){
				resp = RestUtils.processError(PropertiesUtil.getProperty("roleOffset_missing_code"), PropertiesUtil.getProperty("roleOffset_missing_message"));
				return resp;
			}
			
			JSONArray a = new JSONArray();
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("functiontype",Integer.parseInt(functionType));
			System.out.println(searchQuery);
			FindIterable<Document> res = collection.find(searchQuery);
			MongoCursor<Document> cursor = res.iterator();
			while(cursor.hasNext()){
//				Document doc = cursor.next();
//				a.add(doc);
				System.out.println(cursor.next());
				resp = RestUtils.processError(PropertiesUtil.getProperty("service_present_code"), PropertiesUtil.getProperty("service_present_message"));
				return resp;
			}
//			resp = a.toString();
//			System.out.println(serviceType);
//			System.out.println(functionType);
//			System.out.println(contentServiceType);
//			System.out.println(contentFunctionServiceType);
//			System.out.println(uri);
//			System.out.println(groupzmodulename);
//			System.out.println(roleOffset);
//			System.out.println(sessionValidation);
			
			Document doc = new Document();
			doc.append("servicetype", Integer.parseInt(serviceType));
			doc.append("functiontype", Integer.parseInt(functionType));
			doc.append("contentservicetype", Integer.parseInt(contentServiceType));
			doc.append("contentfunctiontype", Integer.parseInt(contentFunctionServiceType));
			doc.append("groupzmodulename", groupzmodulename);
			doc.append("roleoffset", roleOffset);
			doc.append("uri", uri);
			doc.append("sessionvalidation", sessionValidation);
			
			System.out.println(doc);
			collection.insertOne(doc);
			ObjectId id = doc.getObjectId("_id");
			System.out.println(id);
			if (id == null){
				resp = RestUtils.processError(PropertiesUtil.getProperty("incompleteInput_code"), PropertiesUtil.getProperty("incompleteInput_message"));
				return resp;
			}
			JSONObject j = new JSONObject();
			JSONObject json = new JSONObject();
			JSONObject res1 = new JSONObject();
			JSONObject datas = new JSONObject();
			datas.put("id", id+"");
			res1.put("statuscode",Integer.parseInt( PropertiesUtil.getProperty("statuscodesuccessvalue")));
			res1.put("statusmessage", PropertiesUtil.getProperty("statusmessagesuccessvalue"));
			res1.put("servicetype", servicetype);
			res1.put("functiontype", functiontype);
			res1.put("data", datas);
			json.put("response", res1);
			j.put("json", json);
			resp = j.toString();
			return resp;
		}catch(Exception e){
			e.printStackTrace();
			resp = RestUtils.processError(PropertiesUtil.getProperty("incompleteInput_code"), PropertiesUtil.getProperty("incompleteInput_message"));
			return resp;
		}
	}
	

	public String listServiceFromDb(String servicetype, String functiontype,JSONObject data) {
		String resp = "";
		try{
			int limit =-1;
			int offset =-1;
			if (data.containsKey("limit")){
				limit = data.getInt("limit");
			}
			if (data.containsKey("offset")){
				offset = data.getInt("offset");
			}
			JSONArray list = new JSONArray();
			MongoDatabase db = Mongo_Connection.getConnection();
			MongoCollection<Document> col = db.getCollection("authtables");
			FindIterable<Document> res = null;
			if (limit !=-1 && offset !=-1){
				 res = col.find().skip(offset).limit(limit);
			}else if(limit !=-1){
				res = col.find().limit(limit);
			}
			else if(limit !=-1){
				res = col.find().skip(offset);
			}
			else{
				res = col.find();
			}
			MongoCursor<Document> cursor = res.iterator();
			while(cursor.hasNext()){
				Document doc = cursor.next();
				String id = doc.get("_id")+"";
				doc.remove("_id");
				doc.put("id", id);
				list.add(doc);
			}
			JSONObject j = new JSONObject();
			JSONObject json = new JSONObject();
			JSONObject res1 = new JSONObject();
//			JSONObject datas = new JSONObject();
			res1.put("statuscode",Integer.parseInt( PropertiesUtil.getProperty("statuscodesuccessvalue")));
			res1.put("statusmessage", PropertiesUtil.getProperty("statusmessagesuccessvalue"));
			res1.put("servicetype", servicetype);
			res1.put("functiontype", functiontype);
			res1.put("data", list);
			json.put("response", res1);
			j.put("json", json);
			resp = j.toString();
			
			
		}catch(Exception e){
			e.printStackTrace();
			resp = RestUtils.processError(PropertiesUtil.getProperty("incompleteInput_code"), PropertiesUtil.getProperty("incompleteInput_message"));
			return resp;
		}
		return resp;
	}


	public String updateServiceInDb(String servicetype, String functiontype,JSONObject data) {
		String resp = "";
		boolean sessionValidation = false;
		try{
			String id = data.getString("id");
			MongoDatabase db = Mongo_Connection.getConnection();
			MongoCollection<Document> collection = db.getCollection("authtables");
			
			BasicDBObject where = new BasicDBObject();
			where.put("_id",new ObjectId(id+""));
			FindIterable<Document> values = collection.find(where);
			System.out.println(where);
			MongoCursor<Document> cursor = values.iterator();
			if(cursor.hasNext()){
				
			}
			else{
				resp = RestUtils.processError(PropertiesUtil.getProperty("invalid_Id_code"), PropertiesUtil.getProperty("invalid_Id_message"));
				return resp;
			}
			BasicDBObject doc = new BasicDBObject();
			if (data.containsKey("functionType")){
				String functionType = data.getString("functionType");
				doc.append("functiontype", Integer.parseInt(functionType));
			}
			if (data.containsKey("contentServiceType")){
				String contentServiceType = data.getString("contentServiceType");
				doc.append("contentservicetype", Integer.parseInt(contentServiceType));
			}
			if (data.containsKey("contentFunctionServiceType")){
				String contentFunctionServiceType = data.getString("contentFunctionServiceType");
				doc.append("contentfunctiontype", Integer.parseInt(contentFunctionServiceType));
			}
			if (data.containsKey("uri")){
				String uri = data.getString("uri");
				doc.append("uri", uri);
			}
			if (data.containsKey("groupzmodulename")){
				String groupzmodulename = data.getString("groupzmodulename");
				doc.append("groupzmodulename", groupzmodulename);
			}
			if (data.containsKey("roleOffset")){
				String roleOffset = data.getString("roleOffset");
				doc.append("roleoffset", roleOffset);
			}
			if (data.containsKey("sessionValidation")){
				sessionValidation = data.getBoolean("sessionValidation");
				doc.append("sessionvalidation", sessionValidation);
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
			datas.put("id", id+"");
			res1.put("statuscode",Integer.parseInt( PropertiesUtil.getProperty("statuscodesuccessvalue")));
			res1.put("statusmessage", PropertiesUtil.getProperty("statusmessagesuccessvalue"));
			res1.put("servicetype", servicetype);
			res1.put("functiontype", functiontype);
			res1.put("data", datas);
			json.put("response", res1);
			j.put("json", json);
			resp = j.toString();
			return resp;
		}catch(Exception e){
			e.printStackTrace();
			resp = RestUtils.processError(PropertiesUtil.getProperty("incompleteInput_code"), PropertiesUtil.getProperty("incompleteInput_message"));
			return resp;
		}
	}
	
	private String deleteServiceFromDb(String servicetype, String functiontype,JSONObject data) {
		String resp = "";
		try{
			MongoDatabase db = Mongo_Connection.getConnection();
			MongoCollection<Document> collection = db.getCollection("authtables");
			String id = data.getString("id");
			BasicDBObject query = new BasicDBObject();
				query.put("_id",new ObjectId(id+""));
			FindIterable<Document> values = collection.find(query);
			System.out.println(query);
			MongoCursor<Document> re = values.iterator();
			if (re.hasNext()){
				System.out.println(re.next());
				collection.deleteOne(query);
				JSONObject j = new JSONObject();
				JSONObject json = new JSONObject();
				JSONObject res1 = new JSONObject();
				JSONObject datas = new JSONObject();
				datas.put("id", id);
				res1.put("statuscode",Integer.parseInt( PropertiesUtil.getProperty("statuscodesuccessvalue")));
				res1.put("statusmessage", PropertiesUtil.getProperty("statusmessagesuccessvalue"));
				res1.put("servicetype", servicetype);
				res1.put("functiontype", functiontype);
				res1.put("data", datas);
				json.put("response", res1);
				j.put("json", json);
				resp = j.toString();
				return resp;
			}else{
				resp = RestUtils.processError(PropertiesUtil.getProperty("invalid_Id_code"), PropertiesUtil.getProperty("invalid_Id_message"));
				return resp;
			}
					
		}catch(Exception e){
			e.printStackTrace();
			resp = RestUtils.processError(PropertiesUtil.getProperty("incompleteInput_code"), PropertiesUtil.getProperty("incompleteInput_message"));
			return resp;
		}
	}
}
