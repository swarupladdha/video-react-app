package com.sni.task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import net.sf.json.JSONObject;

import com.sni.utils.ConnectionUtils;
import com.sni.utils.InterfaceKeys;
import com.sni.utils.PropertiesUtil;
import com.sni.utils.RestUtils;

public class FacebookTask {

	public String FacebookSignUpProfile(String serviceType,	String functionType, JSONObject data) {
	//	Connection con = null;
		String registrationResponse="";
		
	//	ConnectionPooling connectionPooling = ConnectionPooling.getInstance();
		try{
			String email = RestUtils.getJSONStringValue(data, InterfaceKeys.email);
			registrationResponse = RestUtils.emailIsValid(email);
			if (registrationResponse!=null) {
				return registrationResponse;
			}
			
			String name=RestUtils.getJSONStringValue(data, InterfaceKeys.name);
			if (RestUtils.isEmpty(name) == false) {
				registrationResponse = RestUtils.processError(PropertiesUtil.getProperty(InterfaceKeys.empty_name_code),PropertiesUtil.getProperty(InterfaceKeys.empty_name_message));
				return registrationResponse;
			}
			
			String atoken=RestUtils.getJSONStringValue(data, InterfaceKeys.token);
			if (RestUtils.isEmpty(atoken) == false) {
				registrationResponse = RestUtils.processError(PropertiesUtil.getProperty(InterfaceKeys.empty_token_code),PropertiesUtil.getProperty(InterfaceKeys.empty_token_message));
				return registrationResponse;
			}
			
			
			String urlString= PropertiesUtil.getProperty("facebookSignUp");
			String connectRecieveResponse = "";
			StringBuffer output = new StringBuffer("");
			try {
				System.out.println("URL FINAL : " + urlString);

				URL url = new URL(urlString);		
				URLConnection connection = url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setRequestMethod("POST");
				httpConnection.setDoOutput(true);
				httpConnection.setConnectTimeout(50000);
				httpConnection.setReadTimeout(50000);
				httpConnection.setRequestProperty("Content-Type","application/json");
				
				boolean followUpStatus = false;
				String followUpURL = urlString+ URLEncoder.encode(atoken);
			//	String followUpURL = urlString+ atoken;
				System.out.println("Followup URL:" + followUpURL);
				ConnectionUtils connectionUtils = new ConnectionUtils();
				String followUpResponse = connectionUtils.ConnectandRecieve(followUpURL);
				if (followUpResponse == null || followUpResponse.length() == 0) {
					followUpStatus = false;
				} 
				else {
					try {
						JSONObject respJSON = JSONObject.fromObject(followUpResponse);
						System.out.println("Response JSON:"+respJSON.toString());
						System.out.println("respo"+respJSON);
						if (respJSON.containsKey("id")) {
							followUpStatus = true;
						}
						if(followUpStatus){
							connectRecieveResponse = RestUtils.processSucess(serviceType, functionType, respJSON, "");
							return connectRecieveResponse;
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
						followUpStatus = false;
					}
				}
			} 
			catch (IOException e1) {
				e1.printStackTrace();
				return output.toString();
			} 
			catch (Exception e) {
				e.printStackTrace();
				return connectRecieveResponse;
			}
			
		}
		catch(IllegalStateException e){
			registrationResponse = RestUtils.processError(PropertiesUtil.getProperty(InterfaceKeys.connection_timeout_code),PropertiesUtil.getProperty(InterfaceKeys.connection_timeouts_message));
			return registrationResponse;
		}
		catch(Exception e)	{
			e.printStackTrace();
			registrationResponse = RestUtils.processError(PropertiesUtil.getProperty(InterfaceKeys.XMLRequest_code),PropertiesUtil.getProperty(InterfaceKeys.XMLRequest_message));
			return registrationResponse;
		}
	/*	finally	{
			if(con!=null){
				connectionPooling.close(con);
			}
		}*/
		registrationResponse = RestUtils.processError(PropertiesUtil.getProperty(InterfaceKeys.XMLRequest_code),PropertiesUtil.getProperty(InterfaceKeys.XMLRequest_message));
		return registrationResponse;
	}

}
