package com.utils;

import java.sql.Connection;

import net.sf.json.JSONObject;

public interface Layer {
	
	int apiKey = Integer.parseInt(PropertiesUtil.getProperty("apiKey"));
	String apiSecret = PropertiesUtil.getProperty("apiSecret");
	
	String createSession (Connection connection,String serviceType,String functionType);
	
	String retrieveSession (Connection connection,String serviceType,String functionType,JSONObject data);
	
	String getSessionList (Connection connection,String serviceType,String functionType,JSONObject json);
	
	String getVideoForSession (Connection connection,String serviceType,String functionType,JSONObject data);
	
	void startArchive(Connection connection,JSONObject data);
	
	void monitor(String request);
	
	void callBack(String request);

	String reconnectSession(Connection connection, String serviceType, String functionType, JSONObject data);
	
}
