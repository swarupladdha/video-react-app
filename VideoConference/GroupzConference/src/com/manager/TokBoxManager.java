package com.manager;

import java.sql.Connection;


import org.apache.log4j.Logger;

import com.connection.ConnectionPooling;
import com.task.TokBox;
import com.utils.Layer;
import com.utils.PropertiesUtil;
import com.utils.RestUtils;
import com.utils.TokBoxInterfaceKeys;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class TokBoxManager {
	static final Logger logger = Logger.getLogger(TokBoxManager.class);

	public String getResponse(String eduRequest) {
		Connection connection = null;
		String response="";
		ConnectionPooling connectionPooling = ConnectionPooling.getInstance();
		String serviceType = "";
		String functionType = "";
		try
			{ 
			logger.info("inside tokbox manager");
			logger.info(eduRequest);
			JSONObject json = RestUtils.isJSONValid(eduRequest) ;
			if ( json == null) {
				logger.info("Json parsing fails");
				response = RestUtils.processError(PropertiesUtil.getProperty(TokBoxInterfaceKeys.XMLRequest_code),PropertiesUtil.getProperty(TokBoxInterfaceKeys.XMLRequest_message));
				return response;
			}
			serviceType = json.getJSONObject(TokBoxInterfaceKeys.request).getJSONObject(TokBoxInterfaceKeys.service).getString(TokBoxInterfaceKeys.servicetype);
			functionType = json.getJSONObject(TokBoxInterfaceKeys.request).getJSONObject(TokBoxInterfaceKeys.service).getString(TokBoxInterfaceKeys.functiontype);
	
			if (RestUtils.isEmpty(serviceType) == false || serviceType.equalsIgnoreCase(PropertiesUtil.getProperty(TokBoxInterfaceKeys.getsessionedu_st)) == false) {
				response = RestUtils.processError(PropertiesUtil.getProperty(TokBoxInterfaceKeys.invalidserviceType_code),PropertiesUtil.getProperty(TokBoxInterfaceKeys.invalidserviceType_message));
				return response;
			}
			logger.info("Pooling Connection!");
			connection=connectionPooling.getConnection();
			logger.info("Got Connection! "+connection);

			Layer edu = new TokBox();
			
			JSONObject data=null;
			if(json.getJSONObject(TokBoxInterfaceKeys.request).containsKey(TokBoxInterfaceKeys.data)) {
				data = json.getJSONObject(TokBoxInterfaceKeys.request).getJSONObject(TokBoxInterfaceKeys.data);
			}
			if(functionType.equalsIgnoreCase(PropertiesUtil.getProperty(TokBoxInterfaceKeys.getsessionedu_ft))){
				logger.info("inside create session");
				
				response = edu.createSession(connection,serviceType,functionType,data);
				return response;
			}
			
			if(functionType.equalsIgnoreCase(PropertiesUtil.getProperty(TokBoxInterfaceKeys.retrievesessionedu_ft))){
				logger.info("inside retrieve session");
				
				response = edu.retrieveSession(connection,serviceType,functionType,data);
				return response;
			}
			
			if(functionType.equalsIgnoreCase(PropertiesUtil.getProperty(TokBoxInterfaceKeys.getSessionList))){
				logger.info("inside get session list");
				
				response = edu.getSessionList(connection,serviceType,functionType,json);
				return response;
			}
			if(functionType.equalsIgnoreCase(PropertiesUtil.getProperty(TokBoxInterfaceKeys.getVideoId))){
				logger.info("inside get video id");
				
				response = edu.getVideoForSession(connection,serviceType, functionType, data);
				return response;
			}
			if(functionType.equalsIgnoreCase(PropertiesUtil.getProperty(TokBoxInterfaceKeys.startArchive))){
				logger.info("inside start archive");
				
				edu.startArchive(connection,data);
				response = RestUtils.processSucess(serviceType, functionType, null);
				return response;
			}
			//For Reconnecting Session
			if(functionType.equalsIgnoreCase(PropertiesUtil.getProperty(TokBoxInterfaceKeys.reconnectSession))){
				logger.info("inside reconnect session");
				
				response = edu.reconnectSession(connection,serviceType,functionType,data);
				return response;
			}
			if(functionType.equalsIgnoreCase(PropertiesUtil.getProperty(TokBoxInterfaceKeys.getVideo))){
				logger.info("inside getVideo session");
				
				response = edu.getVideo(connection,serviceType,functionType,data);
				return response;
			}
			else{
				response = RestUtils.processError(PropertiesUtil.getProperty(TokBoxInterfaceKeys.invalid_functiontype_length_code),PropertiesUtil.getProperty(TokBoxInterfaceKeys.invalid_functiontype_length_message));
				return response;
			}
		}
		
		catch (JSONException e) {
			response = RestUtils.processError(PropertiesUtil.getProperty("JSONRequest_code"),PropertiesUtil.getProperty("JSONRequest_message"));
			return response;
		}
		
		catch(Exception e) {
			logger.error("Exception",e);;
		}
		finally	{
			if(connection!=null){
				logger.info("Inside finally");
				connectionPooling.close(connection);
			}
		}
	response = RestUtils.processError(PropertiesUtil.getProperty("XMLRequest_code"),PropertiesUtil.getProperty("XMLRequest_message"));
	return response;
	}

}

