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
import com.utils.PropertiesUtil;
import com.utils.RestUtils;

public class GetSessionInfoManager {

	MongoDatabase db = Mongo_Connection.getConnection();
	public String getResponse(String regRequest) {
		System.out.println("Insde GetSessionInfoManager getResponse");
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
			if(RestUtils.isEmpty(servicetype) == false && servicetype.equalsIgnoreCase(PropertiesUtil.getProperty("getSessionDetailsServicetype"))){
				response = RestUtils.processError(PropertiesUtil.getProperty("invalidServicetype_code"), PropertiesUtil.getProperty("invalidServicetype_message"));
				return response;
			}
			functiontype = request.getString("functiontype");
			if (RestUtils.isEmpty(functiontype)== true && functiontype.equalsIgnoreCase(PropertiesUtil.getProperty("getSessionDetailsFunctiontype"))){
				String id = request.getString("session_id");
				response = getDataFromDb(id);
				if (response != null){
				System.out.println("-------------------"+response);
				JSONObject res = JSONObject.fromObject(response);
				if (res.getJSONObject("json").getJSONObject("response").containsKey("statuscode")){
					if(PropertiesUtil.getProperty("invalid_session_code").equalsIgnoreCase(res.getJSONObject("json").getJSONObject("response").getString("statuscode"))){
						return response;
					}
				}
					response = formResponse(servicetype,functiontype,response);
				}
				return response;
			}
			else{
				response = RestUtils.processError(PropertiesUtil.getProperty("invalidFunctiontype_code"), PropertiesUtil.getProperty("invalidFunctiontype_message"));
				return response;
			}

		}catch (Exception e){
			e.printStackTrace();
			response = RestUtils.processError(PropertiesUtil.getProperty("incompleteInput_code"), PropertiesUtil.getProperty("incompleteInput_message"));
			return response;
		}
	}
	
	private String getDataFromDb(String id){
		String resp = "";
		
		try{
			int len = id.length();
			System.out.println(len);
			
			MongoCollection<Document> col = db.getCollection("memberdetails");
			BasicDBObject query = new BasicDBObject();
			if (len <= 9){
				System.out.println("Id is String");
				 query.put("_id",id);
			}
			else{
				System.out.println("Id is Object");
				query.put("_id",new ObjectId(id));
			}
			FindIterable<Document> values = col.find(query);
			System.out.println(query);
			JSONArray resArray = new JSONArray();
			MongoCursor<Document> re = values.iterator();
			String groupzid = ""; 
			if(re.hasNext()){
				System.out.println("Data");
				while (re.hasNext()){
					Document value = re.next();
					groupzid = value.getString("groupzid");
					resArray.add(value.toJson());
					System.out.println(resp);
				}
			}
			else{
				resp = RestUtils.processError(PropertiesUtil.getProperty("invalid_session_code"), PropertiesUtil.getProperty("invalid_session_message"));
				return resp;
			}
			if (RestUtils.isEmpty(groupzid) == true){
				MongoCollection<Document> col1 = db.getCollection("groupzdetails");
				BasicDBObject query1 = new BasicDBObject("groupzid",groupzid);
				FindIterable<Document> values1 = col1.find(query1);
				MongoCursor<Document> re1 = values1.iterator();
				if(re1.hasNext()){
					System.out.println("Data1");
					System.out.println(query1);
					while (re1.hasNext()){
						Document value1 = re1.next();
						resArray.add(value1.toJson());
						System.out.println(resp);
					}
				}
			}
			JSONObject json = new JSONObject();
			JSONObject response = new JSONObject();
			response.put("data", resArray.toString());
			json.put("response", response);
			JSONObject j = new JSONObject();
			j.put("json", json);
			return j.toString();  
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in getDataFromDb!");
			return null;
		}
	}
	
	
	private String formResponse(String servicetype, String functiontype, String response) {
		String res ="";
		try{
			JSONObject j = JSONObject.fromObject(response);
			JSONArray dataArray = j.getJSONObject("json").getJSONObject("response").getJSONArray("data");
			
			JSONObject memInfo = dataArray.getJSONObject(0);
			JSONObject grpInfo = dataArray.getJSONObject(1);
			
			JSONArray user = new JSONArray();
			JSONObject memberDetails = new JSONObject();
			JSONObject groupzDetails = new JSONObject();
			
			memberDetails.put("_id", memInfo.getJSONObject("_id").getString("$oid"));
			memberDetails.put("localadmin", memInfo.getInt("localadmin"));
			memberDetails.put("networkadmin", memInfo.getInt("networkadmin"));
			//lastactivitytime
			memberDetails.put("groupzcode", memInfo.getString("groupzcode"));
			memberDetails.put("groupzid", memInfo.getString("groupzid"));
			memberDetails.put("personid", memInfo.getInt("personid"));
			memberDetails.put("email", memInfo.getString("email"));
			memberDetails.put("profileurl", memInfo.getString("profileurl"));
			memberDetails.put("membername", memInfo.getString("membername"));
			memberDetails.put("memberid", memInfo.getString("memberid"));
			memberDetails.put("membercode", memInfo.getString("membercode"));
			
			JSONObject morememInfo = new JSONObject();
			
			morememInfo.put("registeredpersonid", memInfo.getInt("registeredpersonid"));
			morememInfo.put("relationship", memInfo.getString("relationship"));
			morememInfo.put("username", memInfo.getString("username"));
			morememInfo.put("rolename", memInfo.getString("rolename"));
			morememInfo.put("divisionlabel", memInfo.getString("divisionlabel"));
			morememInfo.put("subdivisionlabel", memInfo.getString("subdivisionlabel"));
			morememInfo.put("leavetypelist", memInfo.getJSONArray("leavetypelist"));
			morememInfo.put("managegroupzbase", memInfo.getInt("managegroupzbase"));
			morememInfo.put("createissue", memInfo.getInt("createissue"));			
			morememInfo.put("viewallissue", memInfo.getInt("viewallissue"));
			morememInfo.put("closeissue", memInfo.getInt("closeissue"));
			morememInfo.put("issuenotices", memInfo.getInt("issuenotices"));
			morememInfo.put("manageresource", memInfo.getInt("manageresource"));
			morememInfo.put("loginotherusers", memInfo.getInt("loginotherusers"));
			morememInfo.put("createissueforothers", memInfo.getInt("createissueforothers"));
			morememInfo.put("sendmessages", memInfo.getInt("sendmessages"));
			morememInfo.put("createduetemplate", memInfo.getInt("createduetemplate"));
			morememInfo.put("followupissues", memInfo.getInt("followupissues"));
			morememInfo.put("postannouncements", memInfo.getInt("postannouncements"));
			morememInfo.put("createalbums", memInfo.getInt("createalbums"));
			morememInfo.put("networkgrouppublisher", memInfo.getInt("networkgrouppublisher"));
			morememInfo.put("lockreceipts", memInfo.getInt("lockreceipts"));
			morememInfo.put("trackissues", memInfo.getInt("trackissues"));
			morememInfo.put("staff", memInfo.getInt("staff"));
			morememInfo.put("addhelpers", memInfo.getInt("addhelpers"));
			morememInfo.put("viewconfidentialinfo", memInfo.getInt("viewconfidentialinfo"));
			morememInfo.put("canapprove", memInfo.getInt("canapprove"));
			morememInfo.put("canrecordmessage", memInfo.getInt("canrecordmessage"));
			morememInfo.put("enableattendance", memInfo.getInt("enableattendance"));
			
			memberDetails.put("morememinfo", morememInfo);
			memberDetails.put("selection", memInfo.getJSONObject("selection"));
			
			groupzDetails.put("_id", grpInfo.getJSONObject("_id").getString("$oid"));
			groupzDetails.put("groupzbasekey", grpInfo.getString("groupzbasekey"));
			groupzDetails.put("shortname", grpInfo.getString("shortname"));
			groupzDetails.put("groupzurl", grpInfo.getString("groupzurl"));
			groupzDetails.put("groupzname", grpInfo.getString("groupzname"));
			groupzDetails.put("groupzcode", grpInfo.getString("groupzcode"));
			groupzDetails.put("groupzid", grpInfo.getString("groupzid"));
			
			JSONObject moregroupInfo = new JSONObject();
			
			moregroupInfo.put("type", grpInfo.getString("type"));
			moregroupInfo.put("address", grpInfo.getString("address"));
			moregroupInfo.put("area", grpInfo.getString("area"));
			moregroupInfo.put("city", grpInfo.getString("city"));
			moregroupInfo.put("state", grpInfo.getString("state"));
			moregroupInfo.put("country", grpInfo.getString("country"));
			moregroupInfo.put("postalcode", grpInfo.getString("postalcode"));
			moregroupInfo.put("landline", grpInfo.getString("landline"));
			moregroupInfo.put("mobile", grpInfo.getString("mobile"));
			moregroupInfo.put("facebooklink", grpInfo.getString("facebooklink"));
			moregroupInfo.put("twitterlink", grpInfo.getString("twitterlink"));
			moregroupInfo.put("bloglink", grpInfo.getString("bloglink"));
			moregroupInfo.put("defaultuserrole", grpInfo.getString("defaultuserrole"));
			moregroupInfo.put("transactionstartdate", grpInfo.getString("transactionstartdate"));
			moregroupInfo.put("transactionenddate", grpInfo.getString("transactionenddate"));
			moregroupInfo.put("senderemail", grpInfo.getString("senderemail"));
			moregroupInfo.put("registrationno", grpInfo.getString("registrationno"));
			moregroupInfo.put("panno", grpInfo.getString("panno"));
			moregroupInfo.put("albumsize", grpInfo.getString("albumsize"));
			moregroupInfo.put("segments", grpInfo.getString("segments"));
			moregroupInfo.put("userareas", grpInfo.getString("userareas"));
			moregroupInfo.put("groupztype", grpInfo.getString("groupztype"));
			moregroupInfo.put("others", grpInfo.getString("others"));
			moregroupInfo.put("loginurl", grpInfo.getString("loginurl"));
			moregroupInfo.put("description", grpInfo.getString("description"));
			moregroupInfo.put("latitude", grpInfo.getString("latitude"));
			moregroupInfo.put("longitude", grpInfo.getString("longitude"));
			moregroupInfo.put("radius", grpInfo.getInt("radius"));
			moregroupInfo.put("groupzenabled", grpInfo.getBoolean("groupzenabled"));
			moregroupInfo.put("metatagdesc", grpInfo.getString("metatagdesc"));
			moregroupInfo.put("metatagkeywords", grpInfo.getString("metatagkeywords"));
			moregroupInfo.put("ipaddresses", grpInfo.getString("ipaddresses"));
			moregroupInfo.put("smscost", grpInfo.getInt("smscost"));
			moregroupInfo.put("bannerimageurl", grpInfo.getString("bannerimageurl"));
			moregroupInfo.put("bannerlink:", grpInfo.getString("bannerlink:"));
			moregroupInfo.put("applicationskin", grpInfo.getString("applicationskin"));
			moregroupInfo.put("recieptnoprefix", grpInfo.getString("recieptnoprefix"));
			moregroupInfo.put("userapprovedmailtitle", grpInfo.getString("userapprovedmailtitle"));
			moregroupInfo.put("userapprovedmailtext", grpInfo.getString("userapprovedmailtext"));
			moregroupInfo.put("userfailuremailtext", grpInfo.getString("userfailuremailtext"));
			moregroupInfo.put("userfailuremailtitle", grpInfo.getString("userfailuremailtitle"));
			moregroupInfo.put("contactmailtitle", grpInfo.getString("contactmailtitle"));
			moregroupInfo.put("contactmailtext", grpInfo.getString("contactmailtext"));
			moregroupInfo.put("contactsmstext", grpInfo.getString("contactsmstext"));
			moregroupInfo.put("greetingmailenabled", grpInfo.getBoolean("greetingmailenabled"));
			moregroupInfo.put("greetingfamilymembersforemail", grpInfo.getBoolean("greetingfamilymembersforemail"));
			moregroupInfo.put("greetingsmsenabled", grpInfo.getBoolean("greetingsmsenabled"));
			moregroupInfo.put("greetingfamilymembersforsms", grpInfo.getBoolean("greetingfamilymembersforsms"));
			moregroupInfo.put("dueslabel", grpInfo.getString("dueslabel"));
			moregroupInfo.put("duesenabled", grpInfo.getInt("duesenabled"));
			moregroupInfo.put("messagelabel", grpInfo.getString("messagelabel"));
			moregroupInfo.put("smsenabled", grpInfo.getInt("smsenabled"));
			moregroupInfo.put("emailenabled", grpInfo.getInt("emailenabled"));
			moregroupInfo.put("albumlabel", grpInfo.getString("albumlabel"));
			moregroupInfo.put("albumsenabled", grpInfo.getInt("albumsenabled"));
			moregroupInfo.put("announcementlabel", grpInfo.getString("announcementlabel"));
			moregroupInfo.put("announcementsenabled", grpInfo.getInt("announcementsenabled"));
			moregroupInfo.put("advertisementlabel", grpInfo.getString("advertisementlabel"));
			moregroupInfo.put("advertisementsenabled", grpInfo.getInt("advertisementsenabled"));
			moregroupInfo.put("surveylabel", grpInfo.getString("surveylabel"));
			moregroupInfo.put("surveysenabled", grpInfo.getInt("surveysenabled"));
			moregroupInfo.put("servicerequestlabel", grpInfo.getString("servicerequestlabel"));
			moregroupInfo.put("issuesenabled", grpInfo.getInt("issuesenabled"));
			moregroupInfo.put("meeting label", grpInfo.getString("meeting label"));
			moregroupInfo.put("meetingsenabled", grpInfo.getInt("meetingsenabled"));
			moregroupInfo.put("noticeslabel", grpInfo.getString("noticeslabel"));
			moregroupInfo.put("noticesenabled", grpInfo.getString("noticesenabled"));
			moregroupInfo.put("documentslabel", grpInfo.getString("documentslabel"));
			moregroupInfo.put("documentssharingenabled", grpInfo.getString("documentssharingenabled"));
			moregroupInfo.put("plannerlabel", grpInfo.getString("plannerlabel"));
			moregroupInfo.put("plannerenabled", grpInfo.getInt("plannerenabled"));
			moregroupInfo.put("banneradsenabled", grpInfo.getInt("banneradsenabled"));
			moregroupInfo.put("classifiedsearchenabled", grpInfo.getInt("classifiedsearchenabled"));
			moregroupInfo.put("classifiedsenabled", grpInfo.getInt("classifiedsenabled"));
			moregroupInfo.put("classifiedsenabled", grpInfo.getInt("classifiedsenabled"));
			moregroupInfo.put("contactsharingenabled", grpInfo.getInt("contactsharingenabled"));
			moregroupInfo.put("displayproductlogo", grpInfo.getInt("displayproductlogo"));
			moregroupInfo.put("familyinformationenabled", grpInfo.getInt("familyinformationenabled"));
			moregroupInfo.put("helpersenabled", grpInfo.getInt("helpersenabled"));
			moregroupInfo.put("memberssearchacrosssocietyenabled", grpInfo.getInt("memberssearchacrosssocietyenabled"));
			moregroupInfo.put("memberssearchenabled", grpInfo.getInt("memberssearchenabled"));
			moregroupInfo.put("scrollingadsenabled", grpInfo.getInt("scrollingadsenabled"));
			moregroupInfo.put("issuesettings", grpInfo.getJSONObject("issuesettings"));
			moregroupInfo.put("webtheme", grpInfo.getJSONObject("webtheme"));
			moregroupInfo.put("androidtheme", grpInfo.getJSONObject("androidtheme"));
			moregroupInfo.put("iostheme", grpInfo.getJSONObject("iostheme"));
			moregroupInfo.put("latitude", grpInfo.getString("latitude"));
			moregroupInfo.put("longitude", grpInfo.getString("longitude"));
			groupzDetails.put("moregroupdetails", moregroupInfo);
			
			JSONObject jObj = new JSONObject();
			jObj.put("membersdetails", memberDetails);
			jObj.put("groupzdetails", groupzDetails);
			user.add(jObj);
			JSONObject sucessJSON = new JSONObject();
			JSONObject sucessRespJSON = new JSONObject();
			JSONObject contentJSON = new JSONObject();
			
			contentJSON.put("servicetype", servicetype);
			contentJSON.put("functiontype", functiontype);
			contentJSON.put("statuscode",PropertiesUtil.getProperty("statuscodesuccessvalue"));
			contentJSON.put("statusmessage", PropertiesUtil.getProperty("statusmessagesuccessvalue"));
			contentJSON.put("user", user);
			sucessRespJSON.put("response", contentJSON);
			sucessJSON.put("json", sucessRespJSON);
			res = sucessJSON.toString();
			System.out.println("===========");
			return res;
			
			
			
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in formResponse!");
			return res;
		}
		
		
	}
}
