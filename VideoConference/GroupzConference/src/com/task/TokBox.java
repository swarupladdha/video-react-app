package com.task;

import java.net.URLEncoder;
import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import com.connection.ConnectionPooling;
import com.dao.TokBoxDAO;
import com.opentok.Archive;
import com.opentok.ArchiveMode;
import com.opentok.ArchiveProperties;
import com.opentok.MediaMode;
import com.opentok.OpenTok;
import com.opentok.Role;
import com.opentok.Session;
import com.opentok.SessionProperties;
import com.opentok.TokenOptions;
import com.opentok.exception.RequestException;
import com.utils.ConnectionUtils;
import com.utils.Layer;
import com.utils.PropertiesUtil;
import com.utils.RestUtils;
import com.utils.TokBoxInterfaceKeys;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TokBox implements Layer {
	String name = "test";
	static final Logger logger = Logger.getLogger(TokBox.class);
	RestUtils restUtils = new RestUtils();
	TokBoxDAO edu = new TokBoxDAO();
//	static int apiKey = Integer.parseInt(PropertiesUtil.getProperty("apiKey"));
	@Override
	public String createSession(Connection con, String serviceType,String functionType, JSONObject data) {
		String eduResponse="";
		boolean autoArchive = true;
		try{
			
			OpenTok opentok = new OpenTok(apiKey, apiSecret);
			
			if(data != null && !data.isEmpty()) {
				if (data.containsKey(TokBoxInterfaceKeys.type)) {
					String type = data.getString(TokBoxInterfaceKeys.type);
					if(type.equalsIgnoreCase(PropertiesUtil.getProperty("manualArchiveType"))) {
						autoArchive=false;
					}
				}
			}
			Session session=null;
			logger.info("archive type is "+autoArchive );
			if(autoArchive) {
				session = opentok.createSession(new SessionProperties.Builder()
						  .mediaMode(MediaMode.ROUTED)
						  .archiveMode(ArchiveMode.ALWAYS)
						  .build());
			}
			else {
				session = opentok.createSession(new SessionProperties.Builder()
						  .mediaMode(MediaMode.ROUTED)
						  .archiveMode(ArchiveMode.MANUAL)
						  .build());
			}
			String sessionId = session.getSessionId();
			// inside a class or method...
//			apiKey = Integer.parseInt(PropertiesUtil.getProperty("apiKey")); // YOUR API KEY
//			String apiSecret = PropertiesUtil.getProperty("apiSecret");
//			OpenTok opentok = new OpenTok(apiKey, apiSecret);
			
			/*// A session that attempts to stream media directly between clients:
			Session session = opentok.createSession();*/
			
			/*Session session = opentok.createSession(new SessionProperties.Builder()
			  .mediaMode(MediaMode.ROUTED)
			  .archiveMode(ArchiveMode.MANUAL)
			  .build());
			String sessionId = session.getSessionId();*/
			
			/*
			 * Session session = opentok.createSession(new SessionProperties.Builder()
			 * .mediaMode(MediaMode.ROUTED) .archiveMode(ArchiveMode.ALWAYS) .build());
			 * String sessionId = session.getSessionId();
			 */
			
			/*// A session that uses the OpenTok Media Router (which is required for archiving):
			Session session = opentok.createSession(new SessionProperties.Builder()
			  .mediaMode(MediaMode.ROUTED)
			  .build());*/
			
			/*
			// A Session with a location hint:
			Session session = opentok.createSession(new SessionProperties.Builder()
			  .location("12.34.56.78")
			  .build());

			// A session that is automatically archived (it must used the routed media mode)
			Session session = opentok.createSession(new SessionProperties.Builder()
			  .mediaMode(MediaMode.ROUTED)
			  .archiveMode(ArchiveMode.ALWAYS)
			  .build());*/

		/*	// Store this sessionId in the database for later use:
			String sessionId = session.getSessionId();*/
			
		/*	Archive archive = opentok.startArchive(sessionId, "");

			// Store this archiveId in the database for later use
			String archiveId = archive.getId();*/
			
			
			/*// Generate a token from just a sessionId (fetched from a database)
			String token = opentok.generateToken(sessionId);
			// Generate a token by calling the method on the Session (returned from createSession)
			String token = session.generateToken();*/

			// Set some options in a token
			
			String token = session.generateToken(new TokenOptions.Builder()
			  .role(Role.MODERATOR)
			  .expireTime((System.currentTimeMillis() / 1000L) + ( 60 * 60)) // in one hour
			  .data("name=Test")
			  .build());
			
			
			logger.info("--------------------------------------------");
			logger.info(sessionId);
			logger.info(token);
			logger.info("--------------------------------------------");
			TokBoxDAO edu = new TokBoxDAO();
			int id = edu.saveSession(con,sessionId,token);
			
			if(id !=0) {
				JSONObject responsedata = new JSONObject();
				responsedata.put(TokBoxInterfaceKeys.id, id);
				responsedata.put(TokBoxInterfaceKeys.sessionid, sessionId);
				responsedata.put(TokBoxInterfaceKeys.tokenid, token);
				responsedata.put(TokBoxInterfaceKeys.apiKey, apiKey);
				eduResponse = RestUtils.processSucess(serviceType,functionType,responsedata);
				return eduResponse;
			}
		}
		catch (RequestException e) {
			logger.info("Request Exception from opn tok for 403");
			eduResponse = RestUtils.processError(PropertiesUtil.getProperty("RequestError_code"),PropertiesUtil.getProperty("RequestError_message"));
			return eduResponse;
		}
			catch(Exception e)	{
			logger.error("Exception",e);
		}
		eduResponse = RestUtils.processError(PropertiesUtil.getProperty("XMLRequest_code"),PropertiesUtil.getProperty("XMLRequest_message"));
		return eduResponse;
	}

	@Override
	public String retrieveSession(Connection connection, String serviceType,String functionType, JSONObject data) {
		String eduResponse="";
		try{
			
			int id = data.getInt(TokBoxInterfaceKeys.id);
			if (id == 0 || id == -1) {
				eduResponse = RestUtils.processError(PropertiesUtil.getProperty("id_empty_code"), PropertiesUtil.getProperty("id_empty_message"));
				return eduResponse;
			}
			TokBoxDAO edu = new TokBoxDAO();
			JSONObject responseData = edu.retrieveSession(connection, id);
			responseData.put(TokBoxInterfaceKeys.apiKey, apiKey);
			eduResponse = RestUtils.processSucess(serviceType,functionType,responseData);
			return eduResponse;
		}
	
		catch(Exception e)	{
			logger.error("Exception",e);
		}
		eduResponse = RestUtils.processError(PropertiesUtil.getProperty("XMLRequest_code"),PropertiesUtil.getProperty("XMLRequest_message"));
		return eduResponse;
	}
	
	@Override
	public String getSessionList(Connection connection, String serviceType,String functionType, JSONObject req) {
		String eduResponse="";
		String sortby = "";
		String orderby = "";
		String sortQuery = "";
		String paginationQuery = "";
		int limit = -1;
		int offset = -1;
		try{
			JSONObject request = req.getJSONObject(TokBoxInterfaceKeys.request);
			JSONObject data = request.getJSONObject(TokBoxInterfaceKeys.data);
			JSONArray id = data.getJSONArray(TokBoxInterfaceKeys.id);
			
			if (request.containsKey(TokBoxInterfaceKeys.sort)) {
				 JSONObject sort = request.getJSONObject(TokBoxInterfaceKeys.sort);
				 if (sort.containsKey(TokBoxInterfaceKeys.sortby) && sort.containsKey(TokBoxInterfaceKeys.orderby)) {
					 sortby = sort.getString(TokBoxInterfaceKeys.sortby);
					 orderby = sort.getString(TokBoxInterfaceKeys.orderby);
					 sortQuery = " order by "+sortby+" "+orderby; 
				 }
				 else {
					 	eduResponse = RestUtils.processError(PropertiesUtil.getProperty("XMLRequest_code"),PropertiesUtil.getProperty("XMLRequest_message"));
						return eduResponse; 
				 }
			 }
			else {
				sortQuery = " order by createddate desc"; 
			}
			 if (request.containsKey(TokBoxInterfaceKeys.records)) {
				 JSONObject records = request.getJSONObject(TokBoxInterfaceKeys.records);
				 if (records.containsKey(TokBoxInterfaceKeys.limit) && records.containsKey(TokBoxInterfaceKeys.offset)) {
					 limit = records.getInt(TokBoxInterfaceKeys.limit);
					 offset = records.getInt(TokBoxInterfaceKeys.offset);
					 paginationQuery = restUtils.paginationQry(limit, offset);
				 }
				 else {
					 	eduResponse = RestUtils.processError(PropertiesUtil.getProperty("XMLRequest_code"),PropertiesUtil.getProperty("XMLRequest_message"));
						return eduResponse; 
				 }
			 }
			
			if (id != null && id.size()>0) {
				TokBoxDAO edu = new TokBoxDAO();
				String ids = RestUtils.getConcatenatedJOBJSONids(id);
				JSONObject responseData = edu.getList(connection, ids,sortQuery+" "+paginationQuery);
				eduResponse = RestUtils.processSucess(serviceType,functionType,responseData);
				return eduResponse;
			}
			return null;
		}
		
		catch(Exception e)	{
			logger.error("Exception",e);
			eduResponse = RestUtils.processError(PropertiesUtil.getProperty("XMLRequest_code"),PropertiesUtil.getProperty("XMLRequest_message"));
			return eduResponse;
		}
	}

	@Override
	public void monitor(String request) {
		Connection connection = null;
		ConnectionPooling connectionPooling = ConnectionPooling.getInstance();
		String event = "";
		String reason ="";
		String status = "";
		try
			{ 
			connection = connectionPooling.getConnection();
			if (RestUtils.isEmpty(request)== true) {
				JSONObject json = RestUtils.isJSONValid(request) ;
				logger.info("monitor==================> "+request);
				if ( json != null) {
					json = JSONObject.fromObject(request);
					if (json.containsKey("event")) {
						event = json.getString("event");
					}
					if (json.containsKey("reason")) {
						reason = json.getString("reason");
					}
					if (json.containsKey("status")) {
						status = json.getString("status");
					}
					String sessionId = json.getString("sessionId");
					TokBoxDAO edu = new TokBoxDAO();
					int insert = edu.updateSessionStatus(connection, sessionId,event,reason,status,json.toString());
					
					JSONObject sessiondetails = edu.getSessionDetails(connection, sessionId);
					logger.info("--------------------------------------------------------------");
					logger.info(sessiondetails);
					logger.info("--------------------------------------------------------------");
					if (sessiondetails != null) {
						JSONArray urlArrray = JSONArray.fromObject(PropertiesUtil.getProperty("call_back_urls"));
						for (int i=0;i<urlArrray.size(); i++) {
							logger.info("--------------------------------------------------------------");
							logger.info("Url is : "+urlArrray.getString(i));
							logger.info("--------------------------------------------------------------");
							String url = urlArrray.getString(i)+URLEncoder.encode(sessiondetails.toString(), "UTF-8");
							ConnectionUtils con = new ConnectionUtils();
							String res = con.ConnectandRecieve(url);
						}
					}
				}
			}
		}
		catch(Exception e) {
			logger.error("Exception",e);
		}
		finally	{
			if(connection!=null){
				connectionPooling.close(connection);
				}
			}	
	}

	@Override
	public void callBack(String request) {
		Connection connection = null;
		ConnectionPooling connectionPooling = ConnectionPooling.getInstance();
		String event = "";
		int duration =-1;
		String reason ="";
		String status = "";
		String downloadUrl ="";
		int size =-1;
		String archiveId ="";
		try
			{ 
			connection = connectionPooling.getConnection();
			if (RestUtils.isEmpty(request)== true) {
				JSONObject json = RestUtils.isJSONValid(request) ;
				logger.info("callback==================> "+request);
				if ( json != null) {
					json = JSONObject.fromObject(request);
					if (json.containsKey("id")) {
						archiveId = json.getString("id");
					}
					if (json.containsKey("event")) {
						event = json.getString("event");
					}
					if (json.containsKey("duration")) {
						duration = json.getInt("duration");
					}
//					if (json.containsKey("reason")) {
//						reason = json.getString("reason");
//					}
					if (json.containsKey("url")) {
						downloadUrl = json.getString("url");
					}
					if (json.containsKey("status")) {
						status = json.getString("status");
					}
					if (json.containsKey("size")) {
						size = json.getInt("size");
					}
					String sessionId = json.getString("sessionId");
					TokBoxDAO edu = new TokBoxDAO();
					int insert = edu.updateSession(connection, sessionId,event,reason,downloadUrl,status,duration,size,archiveId,json.toString());
					JSONObject sessionDetails =  edu.getSessionDetails(connection, sessionId);
					if (sessionDetails != null && sessionDetails.getInt(TokBoxInterfaceKeys.videoid) <=0) {
						if (RestUtils.isEmpty(downloadUrl)==true) {
							JSONObject obj = new JSONObject();
							obj.put("url", downloadUrl);
							String url = PropertiesUtil.getProperty("upload_video_from_url")+URLEncoder.encode(obj.toString(), "UTF-8");
							ConnectionUtils cu = new ConnectionUtils();
							String response = cu.ConnectandRecieve(url);
							logger.info("--------------------------------------------------------------");
							logger.info(response);
							logger.info("--------------------------------------------------------------");
							JSONObject res = RestUtils.isJSONValid(response);
							if (res != null) {
								if (res.containsKey("id")) {
									int vId = res.getInt("id");
									if (vId != -1 && vId != 0) {
										int update = edu.updateVideoId(connection, sessionId,vId);
										
										JSONObject sessiondetails = edu.getSessionDetails(connection, sessionId);
										logger.info("--------------------------------------------------------------");
										logger.info(sessiondetails);
										logger.info("--------------------------------------------------------------");
										if (sessiondetails != null) {
											JSONArray urlArrray = JSONArray.fromObject(PropertiesUtil.getProperty("call_back_urls"));
											for (int i=0;i<urlArrray.size(); i++) {
												logger.info("--------------------------------------------------------------");
												logger.info("Url is : "+urlArrray.getString(i));
												logger.info("--------------------------------------------------------------");
												String url1 = urlArrray.getString(i)+URLEncoder.encode(sessiondetails.toString(), "UTF-8");
												ConnectionUtils con = new ConnectionUtils();
												String res1 = con.ConnectandRecieve(url1);
											}
										}
									}
								}
							}
						}
					}
					
				}
			}
			
		}
		catch(Exception e) {
			logger.error("Exception",e);;
		}
		finally	{
			if(connection!=null){
				connectionPooling.close(connection);
			}
		}
		
	}

	@Override
	public String getVideoForSession(Connection connection,String serviceType,String functionType,JSONObject data) {
		String eduResponse="";
		try{
			String sessionId =RestUtils.getJSONStringValue(data, "sessionId");
			logger.info("--------------------------------------------------------------");
			logger.info(sessionId);
			logger.info("--------------------------------------------------------------");
			logger.info(RestUtils.isEmpty(sessionId) == false);
			if (  RestUtils.isEmpty(sessionId) == false  ) {
				eduResponse = RestUtils.processError(PropertiesUtil.getProperty("sessionid_isempty_code"), PropertiesUtil.getProperty("sessionid_isempty_message"));
				return eduResponse;
			}
			
			JSONObject sessiondetails = edu.getSessionDetails(connection, sessionId);
			logger.info("--------------------------------------------------------------");
			logger.info(sessiondetails);
			logger.info("--------------------------------------------------------------");
			if (sessiondetails != null) {
				
				int videoId = sessiondetails.getInt(TokBoxInterfaceKeys.videoid);
				if (videoId <=0) {
					OpenTok opentok = new OpenTok(apiKey, apiSecret);
					List<Archive> archives = opentok.listArchives();
					String downloadUrl ="";
					for (int i = 0; i < archives.size(); i++) {
						if(archives.get(i).getSessionId().equals(sessionId)){
							logger.info(archives.get(i).toString());
							downloadUrl= archives.get(i).getUrl();
							break;
						}
					}
					logger.info("archive url is :"+downloadUrl);
					if(RestUtils.isEmpty(downloadUrl)) {
						String url =PropertiesUtil.getProperty("upload_video_from_url");
						JSONObject obj = new JSONObject();
						obj.put("url", downloadUrl);
						ConnectionUtils utils = new ConnectionUtils();
						String response = utils.ConnectandRecieve(url+URLEncoder.encode(obj.toString(),"UTF-8"));
						logger.info(response);
						if (response != null) {
							JSONObject resObj = JSONObject.fromObject(response);
							String id = resObj.getString(TokBoxInterfaceKeys.id);
//							int insert = edu.updateSession(connection, sessionId,event,reason,downloadUrl,status,duration,size,archiveId,null);
							int updateId = edu.updateVideoId(connection, sessionId, Integer.parseInt(id));
							eduResponse = RestUtils.processSucess(serviceType, functionType, resObj);
							return eduResponse;
						}
						eduResponse = RestUtils.processError(PropertiesUtil.getProperty("XMLRequest_code"),PropertiesUtil.getProperty("XMLRequest_message"));
						return eduResponse;
					}
					else {
						eduResponse = RestUtils.processError(PropertiesUtil.getProperty("XMLRequest_code"),PropertiesUtil.getProperty("XMLRequest_message"));
						return eduResponse;
					}
				}
				else {
					logger.info("for sessionid "+sessionId+" video is already downloaded");
					eduResponse = RestUtils.processError(PropertiesUtil.getProperty("XMLRequest_code"),PropertiesUtil.getProperty("XMLRequest_message"));
					return eduResponse;
				}
			}
			else {
				logger.info("sessionid "+sessionId+" does not exist");
				eduResponse = RestUtils.processError(PropertiesUtil.getProperty("XMLRequest_code"),PropertiesUtil.getProperty("XMLRequest_message"));
				return eduResponse;
			}
		}
		catch(Exception e)	{
			e.printStackTrace();
		}
		eduResponse = RestUtils.processError(PropertiesUtil.getProperty("XMLRequest_code"),PropertiesUtil.getProperty("XMLRequest_message"));
		return eduResponse;
	}
	
	@Override
	public void startArchive(Connection connection,JSONObject data) {
		try {
			int id = data.getInt("id");
			
			String sessionId=edu.getSessionId(connection, id);
			logger.info("retrieved sessionid fro id "+id + " is "+sessionId);
			if (RestUtils.isEmpty(sessionId)) {
				OpenTok opentok = new OpenTok(apiKey, apiSecret);
				Archive archive = opentok.startArchive(sessionId,new ArchiveProperties.Builder().hasVideo(true).hasAudio(true).outputMode(Archive.OutputMode.COMPOSED).name(name).build());
				
				logger.info("archive started");
				logger.info(archive.getId());
				Archive archive1 = opentok.getArchive(archive.getId());
				logger.info(archive1.getId());
			}
			
		}catch(Exception e)	{
			e.printStackTrace();
		}
	}
	
	//For Reconnecting Session
	@Override
	public String reconnectSession(Connection connection, String serviceType, String functionType, JSONObject data)
	{
		String eduResponse="";
		int id = data.getInt(TokBoxInterfaceKeys.id);
		if (id == 0 || id == -1) {
			eduResponse = RestUtils.processError(PropertiesUtil.getProperty("id_empty_code"), PropertiesUtil.getProperty("id_empty_message"));
			return eduResponse;
		}
		TokBoxDAO edu = new TokBoxDAO();
		String checkid = edu.getSessionId(connection, id);
		if(checkid != null)
		{
			try {
			OpenTok opentok = new OpenTok(apiKey, apiSecret);
			Session session = opentok.createSession(new SessionProperties.Builder()
					  .mediaMode(MediaMode.ROUTED)
					  .archiveMode(ArchiveMode.ALWAYS)
					  .build());
					String sessionId = session.getSessionId();
					String token = session.generateToken(new TokenOptions.Builder()
							  .role(Role.MODERATOR)
							  .expireTime((System.currentTimeMillis() / 1000L) + ( 60 * 60)) // in one hour
							  .data("name=Test")
							  .build());
							
							
							logger.info("--------------------------------------------");
							logger.info(sessionId);
							logger.info(token);
							logger.info("--------------------------------------------");
							int id1 = edu.saveSessionRec(connection,sessionId,token,id);
							
							if(id1 !=0) {
								JSONObject responsedata = new JSONObject();
								responsedata.put(TokBoxInterfaceKeys.id, id1);
								responsedata.put(TokBoxInterfaceKeys.sessionid, sessionId);
								responsedata.put(TokBoxInterfaceKeys.tokenid, token);
								responsedata.put(TokBoxInterfaceKeys.apiKey, apiKey);
								eduResponse = RestUtils.processSucess(serviceType,functionType,responsedata);
								return eduResponse;
							}
							else {
								eduResponse = RestUtils.processError(PropertiesUtil.getProperty("id_invalid_code"),PropertiesUtil.getProperty("id_invalid_message"));
								return eduResponse;

							}
						}
						
							catch(Exception e)	{
							logger.error("Exception",e);
						}
						eduResponse = RestUtils.processError(PropertiesUtil.getProperty("XMLRequest_code"),PropertiesUtil.getProperty("XMLRequest_message"));
						return eduResponse;

			
		}
		return eduResponse;
	}
	
// For getting Video
	@Override
	public String getVideo(Connection connection, String serviceType, String functionType, JSONObject data)
	{
		String eduResponse="";
		int id = data.getInt(TokBoxInterfaceKeys.id);
		if (id == 0 || id == -1) {
			eduResponse = RestUtils.processError(PropertiesUtil.getProperty("id_empty_code"), PropertiesUtil.getProperty("id_empty_message"));
			return eduResponse;
		}
		TokBoxDAO edu = new TokBoxDAO();
		int videoid = edu.getVideo(connection, id);
		if(videoid !=0) {
			JSONObject responsedata = new JSONObject();
			String url =PropertiesUtil.getProperty("base_url");
			responsedata.put("url", url+videoid);
			eduResponse = RestUtils.processSucess(serviceType,functionType,responsedata);
			return eduResponse;
		}
		else {
			eduResponse = RestUtils.processError(PropertiesUtil.getProperty("video_preparing_code"),PropertiesUtil.getProperty("video_preparing_message"));
			return eduResponse;

		}
	}

}
