package com.managers;

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
import com.utils.PropertiesUtil;
import com.utils.RestUtils;

public class AuthenticatorManager {
	
	MongoDatabase db = Mongo_Connection.getConnection();
	public String getResponse(String regRequest) {
		System.out.println("Insde AuthenticationManager getResponse");
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
			
			String out = getServicetypeAndFunctiontypefromDB(servicetype, functiontype);
			if (out == null){
				System.out.println("==================");
				response = RestUtils.processError(PropertiesUtil.getProperty("invalidServiceOrFunctionType_code"), PropertiesUtil.getProperty("invalidServiceOrFunctionType_message"));
				return response;
			}
			
			JSONArray jArray = JSONArray.fromObject(out);
			JSONObject jObj = jArray.getJSONObject(0);
			boolean sessionvalidation = jObj.getBoolean("sessionvalidation");
			if ((sessionvalidation == true) && (request.containsKey("session_id")==true)){
				System.out.println("-------------------------------------------");
				String groupzCode = request.getString("groupzCode");
				JSONObject data = request.getJSONObject("data");
					String sessionId =  request.getString("session_id");
					response = getDeatilsAndBackendResponse(sessionId,out,groupzCode,data);
					System.out.println("---"+response);
					if (response !=null){
						JSONObject sucessJSON = new JSONObject();
						JSONObject sucessRespJSON = new JSONObject();
						JSONObject contentJSON = new JSONObject();
						JSONArray userList = new JSONArray();
						JSONObject bResponse = JSONObject.fromObject(response);
						if (bResponse.getJSONObject("json").getJSONObject("response").getString("statuscode").equals(PropertiesUtil.getProperty("permissionError_code"))){
							response = RestUtils.processError(PropertiesUtil.getProperty("permissionError_code"), PropertiesUtil.getProperty("permissionError_message"));
							return response;
						}
						if (bResponse.getJSONObject("json").getJSONObject("response").containsKey("userList")){
							userList = bResponse.getJSONObject("json").getJSONObject("response").getJSONArray("userList");	
						}
						JSONObject datas = new JSONObject();
						JSONArray dataArray = new JSONArray();
						if (bResponse.getJSONObject("json").getJSONObject("response").containsKey("data")){
							
							Object obj = bResponse.getJSONObject("json").getJSONObject("response").get("data");
							if(obj instanceof JSONObject){
								datas = bResponse.getJSONObject("json").getJSONObject("response").getJSONObject("data");
							}
							else{
								dataArray = bResponse.getJSONObject("json").getJSONObject("response").getJSONArray("data");
							}
							
							
						}
						contentJSON.put("servicetype", servicetype);
						contentJSON.put("functiontype", functiontype);
						contentJSON.put("statuscode",Integer.parseInt(bResponse.getJSONObject("json").getJSONObject("response").getString("statuscode")));
						contentJSON.put("statusmessage", bResponse.getJSONObject("json").getJSONObject("response").getString("statusmessage"));
						if(userList.size()>0){
							contentJSON.put("userList", userList);	
						}
						if (dataArray.size()>0){
							contentJSON.put("data", dataArray);
						}
						else{
							contentJSON.put("data", datas);
						}
						sucessRespJSON.put("response", contentJSON);
						sucessJSON.put("json", sucessRespJSON);
						response = sucessJSON.toString();
						return response;
					}
					else{
						response = RestUtils.processError(PropertiesUtil.getProperty("invalidServiceOrFunctionType_code"), PropertiesUtil.getProperty("invalidServiceOrFunctionType_message"));
						return response;
					}
				}

			else{
				response = RestUtils.processError(PropertiesUtil.getProperty("invalidServiceOrFunctionType_code"), PropertiesUtil.getProperty("invalidServiceOrFunctionType_message"));
				return response;
			}
			
			
		}catch (Exception e){
			e.printStackTrace();
			response = RestUtils.processError(PropertiesUtil.getProperty("incompleteInput_code"), PropertiesUtil.getProperty("incompleteInput_message"));
			return response;
		}
	}

	private String getServicetypeAndFunctiontypefromDB(String serviceType, String functionType){
		String res= "";
		try{
			
			MongoCollection<Document> collection = db.getCollection("authtables");
			
			BasicDBObject whereQuery = new BasicDBObject();
			List <BasicDBObject> list = new ArrayList<BasicDBObject>(); 
			list.add(new BasicDBObject("servicetype",Integer.parseInt(serviceType)));
			list.add(new BasicDBObject("functiontype", Integer.parseInt(functionType)));
			whereQuery.put("$and", list);
			
			System.out.println(whereQuery.toString());
			
			FindIterable<Document> result = collection.find(whereQuery);
			
			MongoCursor<Document> re = result.iterator();
			JSONArray datas = new JSONArray();
			System.out.println(re.toString() + re.hasNext());
			if(re.hasNext()){
				while(re.hasNext()){
					JSONObject val = new JSONObject();
					Document value = re.next();
					val.put("servicetype", value.getInteger("contentservicetype"));
					val.put("functiontype",value.getInteger("contentfunctiontype"));
					val.put("roleoffset", value.getString("roleoffset"));
					val.put("url", value.getString("uri"));
					val.put("groupzmodulename", value.getString("groupzmodulename"));
					val.put("sessionvalidation", value.getBoolean("sessionvalidation"));
					datas.add(val);
				}
				res = datas.toString();
				System.out.println("Vale from session ");
				System.out.println(datas.toString());
				return res;
			}
			else{	
				System.out.println("No Data Found!");
				return null;
			}
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Exception in getServicetypeAndFunctiontypefromDB");
			return null;
		}
	}

	
	private String getDeatilsAndBackendResponse(String sessionId, String out,String groupzCode, JSONObject data) {
		System.out.println("Inside getDeatilsAndBackendResponse");
		String resp = "";
		
		try{
			int len = sessionId.length();
			MongoCollection<Document> collection = db.getCollection("memberdetails");
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
			String groupzid = "";
			int memberid = 0;
			int manageusers = 0;
			String groupzbasekey ="";
			if(re.hasNext()){
					Document value = re.next();
					groupzid = value.getString("groupzid");
					memberid = value.getInteger("memberid");
					manageusers = value.getInteger("manageusers");
			}
			else{
				return null;
			}
			MongoCollection<Document> collection1 = db.getCollection("groupzdetails");
			BasicDBObject query1 = new BasicDBObject();
			query.put("groupzid",groupzid);
			FindIterable<Document> values1 = collection1.find(query1);
			System.out.println(query1);
			MongoCursor<Document> re1 = values1.iterator();
			if(re1.hasNext()){
					Document value = re1.next();
					groupzbasekey = value.getString("groupzbasekey");
			}
			else{
				return null;
			}
			String roleoffset ="";
			JSONArray requestArray = new JSONArray();
			requestArray = JSONArray.fromObject(out);
			if (requestArray.size() == 1){
				JSONObject req = requestArray.getJSONObject(0);
				System.out.println(req);
				String url = req.getString("url");
				roleoffset = req.getString("roleoffset");
				JSONObject request = new JSONObject();
				request.put("servicetype", req.getString("servicetype"));
				request.put("functiontype", req.getString("functiontype"));
				request.put("groupzCode", groupzCode);
				request.put("data",data);
				
//				System.out.println("---"+roleoffset+"---"+manageusers);
				
				if (!roleoffset.equalsIgnoreCase("*") ){
					
					if (manageusers == 0){
						resp = RestUtils.processError(PropertiesUtil.getProperty("permissionError_code"), PropertiesUtil.getProperty("permissionError_message"));
						return resp;
					}
				}
				if (memberid !=0){
					request.put("memberid", memberid);	
				}
				if (RestUtils.isEmpty(groupzbasekey)==true){
					request.put("groupzbasekey", groupzbasekey);
				}
				JSONObject json = new JSONObject();
				json.put("request", request);
				JSONObject js = new JSONObject();
				js.put("json", json);
				System.out.println(url+"?request="+js);
				ConnectionUtils cu = new ConnectionUtils();
				resp = cu.ConnectandRecieve(url+"?request="+js);
				return resp;
			}
			else{
				return null;
			}

		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception -- getDeatilsAndBackendResponse");
			return null;
		}
	}

}
