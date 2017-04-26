package com.sni.manager;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.sni.task.FacebookTask;
import com.sni.task.RegistrationTask;
import com.sni.utils.InterfaceKeys;
import com.sni.utils.PropertiesUtil;
import com.sni.utils.RestUtils;

public class SNIManager {
	static final Logger logger = Logger.getLogger(SNIManager.class);
	public String getRegistrationResponse(String registrationRequest, String idTokenString) {
		
		String ofdResponse = "";
		logger.info("Recevd request : " + registrationRequest);
		JSONObject json = RestUtils.isJSONValid(registrationRequest) ;
		if ( json == null) {
			logger.info("Json parsing fails");
			ofdResponse = RestUtils.processError(PropertiesUtil.getProperty(InterfaceKeys.XMLRequest_code),PropertiesUtil.getProperty(InterfaceKeys.XMLRequest_message));
			return ofdResponse;
		}
		String serviceType = "";
		String functionType = "";
		try {
			serviceType = json.getJSONObject(InterfaceKeys.request).getJSONObject(InterfaceKeys.service).getString(InterfaceKeys.servicetype);
			functionType = json.getJSONObject(InterfaceKeys.request).getJSONObject(InterfaceKeys.service).getString(InterfaceKeys.functiontype);
		
			if (RestUtils.isEmpty(serviceType) == false || serviceType.equalsIgnoreCase(PropertiesUtil.getProperty(InterfaceKeys.signUp_serviceType)) == false) {
				ofdResponse = RestUtils.processError(PropertiesUtil.getProperty(InterfaceKeys.invalidserviceType_code),PropertiesUtil.getProperty(InterfaceKeys.invalidserviceType_message));
				return ofdResponse;
			}
			JSONObject data = json.getJSONObject(InterfaceKeys.request).getJSONObject(InterfaceKeys.data);
			
			if(functionType.equalsIgnoreCase(PropertiesUtil.getProperty(InterfaceKeys.googleSignUp_functionType))){
		
			String s[]  = new String[]{"815965216106-8v22o6dj837tbjqjd5c9ae2a5a4qg8fu.apps.googleusercontent.com","815965216106-jsne1j2esdipifkiahhbef4ks2s4gtn2.apps.googleusercontent.com","815965216106-hm2f5ipvsrv0s7g1dkiaeddoe82ai2fq.apps.googleusercontent.com"};
			RegistrationTask rm = new RegistrationTask(s, "815965216106-jsne1j2esdipifkiahhbef4ks2s4gtn2.apps.googleusercontent.com");
			ofdResponse = rm.GoogleSignUpProfile(serviceType, functionType, data, json, idTokenString);
			logger.info("inside OFDManager calling RegisterUserProfile()");
			return ofdResponse;
			}
			
			if(functionType.equalsIgnoreCase(PropertiesUtil.getProperty(InterfaceKeys.facebookSignUp_functionType))){
			FacebookTask rm = new FacebookTask();
			ofdResponse = rm.FacebookSignUpProfile(serviceType, functionType, data);
			logger.info("inside OFDManager calling RegisterUserProfile()");
			return ofdResponse;
			}
			else{
				ofdResponse = RestUtils.processError(PropertiesUtil.getProperty(InterfaceKeys.invalid_service_function_code),PropertiesUtil.getProperty(InterfaceKeys.invalid_service_function_message));
				return ofdResponse;
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
			
		ofdResponse = RestUtils.processError(PropertiesUtil.getProperty(InterfaceKeys.XMLRequest_code),PropertiesUtil.getProperty(InterfaceKeys.XMLRequest_message));
		return ofdResponse;
	}

}
