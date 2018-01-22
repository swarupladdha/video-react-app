package com.managers;

import org.bson.Document;
import org.bson.types.ObjectId;

import net.sf.json.JSONObject;

import com.connection.Mongo_Connection;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.utils.PropertiesUtil;
import com.utils.RestUtils;

public class LogoutManager {

	public String getResponse(String regRequest) {
		System.out.println("Insde LogoutManager getResponse!");
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
			if (functiontype.equalsIgnoreCase(PropertiesUtil.getProperty("logout_functiontype"))){
				System.out.println(functiontype);
				JSONObject data = json.getJSONObject("json").getJSONObject("request").getJSONObject("data");
				System.out.println(data);
				response = deletefromMemberDetails(servicetype,functiontype,data);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
			response = RestUtils.processError(PropertiesUtil.getProperty("incompleteInput_code"), PropertiesUtil.getProperty("incompleteInput_message"));
			return response;
		}
		
		return response;
	}

	private String deletefromMemberDetails(String servicetype,String functiontype, JSONObject data) {
		String resp = "";
		try{
			Mongo_Connection conn = new Mongo_Connection();
			MongoDatabase db = conn.getConnection();
			MongoCollection<Document> collection = db.getCollection("memberdetails");
			String sessionId = data.getString("session_id");
			int len = sessionId.length();
			BasicDBObject query = new BasicDBObject();
			if (len <= 9){
				System.out.println("Id is String");
				 query.put("_id",sessionId);
			}
			else{
				System.out.println("Id is Object");
				query.put("_id",new ObjectId(sessionId));
			}
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
				datas.put("session_id", sessionId);
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
				resp = RestUtils.processError(PropertiesUtil.getProperty("invalid_session_code"), PropertiesUtil.getProperty("invalid_session_message"));
				return resp;
			}
					
		}catch(Exception e){
			e.printStackTrace();
			resp = RestUtils.processError(PropertiesUtil.getProperty("incompleteInput_code"), PropertiesUtil.getProperty("incompleteInput_message"));
			return resp;
		}
	}

}
