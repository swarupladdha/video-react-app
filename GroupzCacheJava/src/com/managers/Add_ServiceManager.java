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
			
			
		}catch(Exception e){
			e.printStackTrace();
			response = RestUtils.processError(PropertiesUtil.getProperty("incompleteInput_code"), PropertiesUtil.getProperty("incompleteInput_message"));
			return response;
		}
		
		return response;
	}

	protected String addServiceToDb(String servicetype, String functiontype,JSONObject data) {
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
			List <BasicDBObject> list = new ArrayList<BasicDBObject>(); 
			list.add(new BasicDBObject("functiontype",Integer.parseInt(functionType)));
			list.add(new BasicDBObject("contentfunctiontype", Integer.parseInt(contentFunctionServiceType)));
			searchQuery.put("$or", list);
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
			doc.append("sesstionvalidation", sessionValidation);
			
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

}
