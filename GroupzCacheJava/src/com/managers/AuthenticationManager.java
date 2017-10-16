package com.managers;


import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.connection.Mongo_Connection;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.utils.ConnectionUtils;
import com.utils.PropertiesUtil;
import com.utils.RestUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AuthenticationManager {
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
				response = RestUtils.processError(PropertiesUtil.getProperty("invalidServiceOrFunctionType_code"), PropertiesUtil.getProperty("invalidServiceOrFunctionType_message"));
				return response;
			}
			JSONArray jArray = JSONArray.fromObject(out);

			JSONObject jObj = jArray.getJSONObject(0);
			boolean sessionvalidation = jObj.getBoolean("sessionvalidation");
			if (sessionvalidation == true){
				response = RestUtils.processError(PropertiesUtil.getProperty("permissionError_code"), PropertiesUtil.getProperty("permissionError_message"));
				return response;	
			}
			
			if (request.containsKey("userdata")){
				JSONObject userData = request.getJSONObject("userdata");
				boolean selection = request.getBoolean("selection");
				response = formUrlAndConnect(out,selection,userData);
				String success = insertValuesInToTables(response);
				if (success !=null){
					JSONObject sucessJSON = new JSONObject();
					JSONObject sucessRespJSON = new JSONObject();
					JSONObject contentJSON = new JSONObject();
					
					contentJSON.put("servicetype", servicetype);
					contentJSON.put("functiontype", functiontype);
					contentJSON.put("statuscode",Integer.parseInt(PropertiesUtil.getProperty("statuscodesuccessvalue")));
					contentJSON.put("statusmessage", PropertiesUtil.getProperty("statusmessagesuccessvalue"));
					contentJSON.put("user", success);
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
	
	
	
	private String formUrlAndConnect(String out, boolean selection, JSONObject userData) {
		System.out.println("Inside formUrlAndConnect");
		String res = "";
		JSONArray requestArray = new JSONArray();
		requestArray = JSONArray.fromObject(out);
		if (requestArray.size() == 1){
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
			res = cu.ConnectandRecieve(url+"?request=",js.toString());
			return res;
		}
		else{
			return null;
		}
	}
	
	private String insertValuesInToTables (String response){
		String resp ="";
		try{
			JSONObject result = new JSONObject();
			result = JSONObject.fromObject(response);
			JSONObject res = result.getJSONObject("json").getJSONObject("response");
			if (res.getString("statusmessage").equals("Success") == false){
				return null;
			}
			JSONArray user = res.getJSONArray("user");
			JSONArray userRes = new JSONArray();
			if (user.size() <= 0){
				return null;
			} 
			for (int i =0 ; i < user.size() ; i++){
				
				JSONObject groupzDetails = user.getJSONObject(i).getJSONObject("groupzdetails");
				String groupzid = groupzDetails.getString("groupzid");
				MongoCollection<Document> collection = db.getCollection("groupzdetails");
				BasicDBObject query = new BasicDBObject ("groupzid",groupzid);
				
				FindIterable<Document> r = collection.find(query);
				MongoCursor<Document> re = r.iterator();
				if(re.hasNext()){
//					System.out.println("Present");
				}
				else{
					System.out.println("Should Insert Into groupzdetails!");
					Document doc = new Document("groupzid",groupzid);
					doc.append("groupzcode", groupzDetails.getString("groupzcode"));
					doc.append("groupzname", groupzDetails.getString("groupzname"));
					doc.append("shortname", groupzDetails.getString("shortname"));
					doc.append("groupzurl", groupzDetails.getString("groupzurl"));
					doc.append("type", groupzDetails.getInt("type"));
					if (groupzDetails.containsKey("groupzbasekey")){
						doc.append("groupzbasekey", groupzDetails.getString("groupzbasekey"));
					}
					doc.append("address", groupzDetails.getString("address"));
					doc.append("area", groupzDetails.getString("area"));
					doc.append("city", groupzDetails.getString("city"));
					doc.append("state", groupzDetails.getString("state"));
					doc.append("country", groupzDetails.getString("country"));
					doc.append("postalcode", groupzDetails.getString("postalcode"));
					doc.append("landline", groupzDetails.getString("landline"));
					doc.append("mobile", groupzDetails.getString("mobile"));
					doc.append("facebooklink", groupzDetails.getString("facebooklink"));
					doc.append("twitterlink", groupzDetails.getString("twitterlink"));
					doc.append("bloglink", groupzDetails.getString("bloglink"));
					doc.append("defaultuserrole", groupzDetails.getString("defaultuserrole"));
					doc.append("transactionstartdate", groupzDetails.getString("transactionstartdate"));
					doc.append("transactionenddate", groupzDetails.getString("transactionenddate"));
					doc.append("senderemail", groupzDetails.getString("senderemail"));
					doc.append("registrationno", groupzDetails.getString("registrationno"));
					doc.append("panno", groupzDetails.getString("panno"));
					doc.append("albumsize", groupzDetails.getString("albumsize"));
					doc.append("segments", groupzDetails.getString("segments"));
					doc.append("userareas", groupzDetails.getString("userareas"));
					doc.append("groupztype", groupzDetails.getString("groupztype"));
					doc.append("others", groupzDetails.getString("others"));
					doc.append("loginurl", groupzDetails.getString("loginurl"));
					doc.append("description", groupzDetails.getString("description"));
					doc.append("latitude", groupzDetails.getString("latitude"));
					doc.append("longitude", groupzDetails.getString("longitude"));
					doc.append("radius", groupzDetails.getInt("radius"));
					doc.append("groupzenabled", groupzDetails.getBoolean("groupzenabled"));
					doc.append("metatagdesc", groupzDetails.getString("metatagdesc"));
					doc.append("metatagkeywords", groupzDetails.getString("metatagkeywords"));
					doc.append("ipaddresses", groupzDetails.getString("ipaddresses"));
					doc.append("smscost", groupzDetails.getInt("smscost"));
					if(groupzDetails.containsKey("bannerimageurl")){
						doc.append("bannerimageurl", groupzDetails.getString("bannerimageurl"));
					}
					if(groupzDetails.containsKey("bannerlink:")){
						doc.append("bannerlink:", groupzDetails.getString("bannerlink:"));
					}
					doc.append("applicationskin", groupzDetails.getString("applicationskin"));
					if(groupzDetails.containsKey("redirecturl")){
						doc.append("redirecturl", groupzDetails.getString("redirecturl"));
					}
					if(groupzDetails.containsKey("smsprovidercode")){
						doc.append("smsprovidercode", groupzDetails.getString("smsprovidercode"));
					}
					if(groupzDetails.containsKey("smsproviderusername")){
						doc.append("smsproviderusername", groupzDetails.getString("smsproviderusername"));
					}
					if(groupzDetails.containsKey("smsproviderusernamedecrypted")){
						doc.append("smsproviderusernamedecrypted", groupzDetails.getString("smsproviderusernamedecrypted"));
					}
					if(groupzDetails.containsKey("smsproviderpassword")){
						doc.append("smsproviderpassword", groupzDetails.getString("smsproviderpassword"));
					}
					if(groupzDetails.containsKey("smsproviderpassworddecrypted")){
						doc.append("smsproviderpassworddecrypted", groupzDetails.getString("smsproviderpassworddecrypted"));
					}
					doc.append("recieptnoprefix", groupzDetails.getString("recieptnoprefix"));
					doc.append("userapprovedmailtitle", groupzDetails.getString("userapprovedmailtitle"));
					doc.append("userapprovedmailtext", groupzDetails.getString("userapprovedmailtext"));
					doc.append("userfailuremailtext", groupzDetails.getString("userfailuremailtext"));
					doc.append("userfailuremailtitle", groupzDetails.getString("userfailuremailtitle"));
					doc.append("contactmailtitle", groupzDetails.getString("contactmailtitle"));
					doc.append("contactmailtext", groupzDetails.getString("contactmailtext"));
					doc.append("contactsmstext", groupzDetails.getString("contactsmstext"));
					if (groupzDetails.containsKey("greetingmailtitle")){
						doc.append("greetingmailtitle", groupzDetails.getString("greetingmailtitle"));
					}
					if (groupzDetails.containsKey("greetingmailtext")){
						doc.append("greetingmailtext", groupzDetails.getString("greetingmailtext"));
					}
					if (groupzDetails.containsKey("greetingmailenabled")){
						doc.append("greetingmailenabled", groupzDetails.getBoolean("greetingmailenabled"));
					}
					if (groupzDetails.containsKey("greetingfamilymembersforemail")){
						doc.append("greetingfamilymembersforemail", groupzDetails.getBoolean("greetingfamilymembersforemail"));
					}
					if (groupzDetails.containsKey("greetingsmstext")){
						doc.append("greetingsmstext", groupzDetails.getString("greetingsmstext"));
					}
					if (groupzDetails.containsKey("greetingsmsenabled")){
						doc.append("greetingsmsenabled", groupzDetails.getBoolean("greetingsmsenabled"));
					}
					if (groupzDetails.containsKey("greetingfamilymembersforsms")){
						doc.append("greetingfamilymembersforsms", groupzDetails.getBoolean("greetingfamilymembersforsms"));
					}
					doc.append("dueslabel", groupzDetails.getString("dueslabel"));
					doc.append("duesenabled", groupzDetails.getInt("duesenabled"));
					doc.append("messagelabel", groupzDetails.getString("messagelabel"));
					doc.append("smsenabled", groupzDetails.getInt("smsenabled"));
					doc.append("emailenabled", groupzDetails.getInt("emailenabled"));
					doc.append("albumlabel", groupzDetails.getString("albumlabel"));
					doc.append("albumsenabled", groupzDetails.getInt("albumsenabled"));
					doc.append("announcementlabel", groupzDetails.getString("announcementlabel"));
					doc.append("announcementsenabled", groupzDetails.getInt("announcementsenabled"));
					doc.append("advertisementlabel", groupzDetails.getString("advertisementlabel"));
					doc.append("advertisementsenabled", groupzDetails.getInt("advertisementsenabled"));
					doc.append("surveylabel", groupzDetails.getString("surveylabel"));
					doc.append("surveysenabled", groupzDetails.getInt("surveysenabled"));
					doc.append("servicerequestlabel", groupzDetails.getString("servicerequestlabel"));
					doc.append("issuesenabled", groupzDetails.getInt("issuesenabled"));
					doc.append("meeting label", groupzDetails.getString("meeting label"));
					doc.append("meetingsenabled", groupzDetails.getInt("meetingsenabled"));
					if (groupzDetails.containsKey("noticeslabel")){
						doc.append("noticeslabel", groupzDetails.getString("noticeslabel"));
					}
					if (groupzDetails.containsKey("noticesenabled")){
						doc.append("noticesenabled", groupzDetails.getInt("noticesenabled"));
					}
					doc.append("documentslabel", groupzDetails.getString("documentslabel"));
					doc.append("documentssharingenabled", groupzDetails.getInt("documentssharingenabled"));
					doc.append("plannerlabel", groupzDetails.getString("plannerlabel"));
					doc.append("plannerenabled", groupzDetails.getInt("plannerenabled"));
					doc.append("banneradsenabled", groupzDetails.getInt("banneradsenabled"));
					doc.append("classifiedsearchenabled", groupzDetails.getInt("classifiedsearchenabled"));
					doc.append("classifiedsenabled", groupzDetails.getInt("classifiedsenabled"));
					doc.append("contactsenabled", groupzDetails.getInt("contactsenabled"));
					doc.append("contactsharingenabled", groupzDetails.getInt("contactsharingenabled"));
					doc.append("displayproductlogo", groupzDetails.getInt("displayproductlogo"));
					doc.append("familyinformationenabled", groupzDetails.getInt("familyinformationenabled"));
					doc.append("helpersenabled", groupzDetails.getInt("helpersenabled"));
					doc.append("memberssearchacrosssocietyenabled", groupzDetails.getInt("memberssearchacrosssocietyenabled"));
					doc.append("memberssearchenabled", groupzDetails.getInt("memberssearchenabled"));
					doc.append("scrollingadsenabled", groupzDetails.getInt("scrollingadsenabled"));
					doc.append("issuesettings", groupzDetails.getJSONObject("issuesettings"));
					doc.append("webtheme", groupzDetails.getJSONObject("webtheme"));
					doc.append("androidtheme", groupzDetails.getJSONObject("androidtheme"));
					doc.append("iostheme", groupzDetails.getJSONObject("iostheme"));

//					System.out.println("--"+doc);
					collection.insertOne(doc);
					Object id = doc.get("_id");
					if (id == null){
						System.out.println("Problem Occured while inserting in groupzdetails");
					}
					else{
							System.out.println("Inserted");
					}
				}
				
				
//				System.out.println("total Vales "+count);
				
				JSONObject memberDetails = user.getJSONObject(i).getJSONObject("memberdetails");
		
		
				Document doc1 = new Document("groupzid",memberDetails.getString("groupzid"));
				doc1.append("groupzcode", memberDetails.getString("groupzcode"));
				doc1.append("membercode", memberDetails.getString("membercode"));
				doc1.append("memberid", memberDetails.getInt("memberid"));
				doc1.append("registeredpersonid", memberDetails.getInt("registeredpersonid"));
				doc1.append("personid", memberDetails.getInt("personid"));
				doc1.append("membername", memberDetails.getString("membername"));
				doc1.append("relationship", memberDetails.getString("relationship"));
				doc1.append("profileurl", memberDetails.getString("profileurl"));
				doc1.append("email", memberDetails.getString("email"));
				doc1.append("username", memberDetails.getString("username"));
				doc1.append("rolename", memberDetails.getString("rolename"));
				if(memberDetails.containsKey("dateofbirth")){
					doc1.append("dateofbirth", memberDetails.getString("dateofbirth"));
				}
				if(memberDetails.containsKey("bloodgroup")){
					doc1.append("bloodgroup", memberDetails.getString("bloodgroup"));
				}
				doc1.append("divisionlabel", memberDetails.getString("divisionlabel"));
				doc1.append("divisionvalue", memberDetails.getString("divisionvalue"));
				doc1.append("subdivisionlabel", memberDetails.getString("subdivisionlabel"));
				doc1.append("subdivisionvalue", memberDetails.getString("subdivisionvalue"));
				doc1.append("leavetypelist", memberDetails.getJSONArray("leavetypelist"));
				doc1.append("networkadmin", memberDetails.getInt("networkadmin"));
				doc1.append("managegroupzbase", memberDetails.getInt("managegroupzbase"));
				doc1.append("localadmin", memberDetails.getInt("localadmin"));
				doc1.append("manageusers", memberDetails.getInt("manageusers"));
				doc1.append("createissue", memberDetails.getInt("createissue"));
				doc1.append("viewallissue", memberDetails.getInt("viewallissue"));
				doc1.append("closeissue", memberDetails.getInt("closeissue"));
				doc1.append("issuenotices", memberDetails.getInt("issuenotices"));
				doc1.append("manageresource", memberDetails.getInt("manageresource"));
				doc1.append("startsurvey", memberDetails.getInt("startsurvey"));
				doc1.append("loginotherusers", memberDetails.getInt("loginotherusers"));
				doc1.append("createissueforothers", memberDetails.getInt("createissueforothers"));
				doc1.append("sendmessages", memberDetails.getInt("sendmessages"));
				doc1.append("createduetemplate", memberDetails.getInt("createduetemplate"));
				doc1.append("followupissues", memberDetails.getInt("followupissues"));
				doc1.append("postannouncements", memberDetails.getInt("postannouncements"));
				doc1.append("createalbums", memberDetails.getInt("createalbums"));
				doc1.append("networkgrouppublisher", memberDetails.getInt("networkgrouppublisher"));
				doc1.append("lockreceipts", memberDetails.getInt("lockreceipts"));
				doc1.append("trackissues", memberDetails.getInt("trackissues"));
				doc1.append("staff", memberDetails.getInt("staff"));
				doc1.append("addhelpers", memberDetails.getInt("addhelpers"));
				doc1.append("viewconfidentialinfo", memberDetails.getInt("viewconfidentialinfo"));
				doc1.append("canapprove", memberDetails.getInt("canapprove"));
				doc1.append("canrecordmessage", memberDetails.getInt("canrecordmessage"));
				doc1.append("enableattendance", memberDetails.getInt("enableattendance"));
				doc1.append("selection", memberDetails.getJSONObject("selection"));
				
				
//				System.out.println(doc1);
				
				MongoCollection<Document> collection1 = db.getCollection("memberdetails");
				collection1.insertOne(doc1);

				Object id = doc1.get("_id");
				if (id == null){
					System.out.println("Problem Occured while inserting in memberdetails");
//					resp = RestUtils.processError(PropertiesUtil.getProperty("insertError_code"), PropertiesUtil.getProperty("insertError_message"));
					return null;
				}
				else{
					JSONObject data = new JSONObject();
					data.put("session_id", id+"");
					data.put("groupzcode", memberDetails.getString("groupzcode"));
					data.put("displayname", memberDetails.getString("membername")+" - "+groupzDetails.getString("shortname"));
					data.put("profileurl", memberDetails.get("profileurl"));
					userRes.add(data);	
				}	
			}
			resp = userRes.toString();
			return resp;
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception -- insertValuesInToTables");
			return null;
		}
	}
}
