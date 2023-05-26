package com.flexical.controller;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flexical.service.ResourceSettingsService;
import com.flexical.util.AllKeys;
import com.flexical.util.ConnectionPooling;
import com.flexical.util.RestUtils;

import net.sf.json.JSONObject;

@RestController
@CrossOrigin(origins = "*",maxAge=3600,allowCredentials= "false",allowedHeaders = "*")
@RequestMapping(value = "/ResourceSettings", produces = "application/json")
public class ResourceSettingsController {
	private static final String parentPath = "/ResourceSettings";
	private static final String AddResourceAvailability = "/AddResourceAvailability";
	private static final String GetResourceAvailability = "/GetResourceAvailability";
	ConnectionPooling connectionPooling = null;
	Connection dbConnection = null;
	
	public static final Logger logger = Logger.getLogger(ResourceSettingsController.class);
	RestUtils utils = new RestUtils();
	ResourceSettingsService rsService = new ResourceSettingsService();
	
	@PostMapping(value = AddResourceAvailability)
	public String addSchedule(@RequestBody String request){
		String response = null;
		
		utils.printRequest(parentPath+AddResourceAvailability, request);
		connectionPooling = ConnectionPooling.getInstance();
		dbConnection = connectionPooling.getConnection();
		try
		{
			if(utils.isJsonValid(request)) 
			{
				JSONObject jsonRequest = JSONObject.fromObject(request);
				JSONObject dataObject = jsonRequest.getJSONObject(AllKeys.JSON_KEY).getJSONObject(AllKeys.REQUEST_KEY)
						.getJSONObject(AllKeys.DATA_KEY);

				response = rsService.addResourceSettings(dataObject, dbConnection);
				
			}
		}
		catch(Exception e)
		{
			logger.error("Exception in getTalkToAstroList",e);
			response = utils.genericError();
		}
		finally
		{
			try 
			{
				if (dbConnection != null)
					connectionPooling.close(dbConnection);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
		System.out.println("response: " + response);
		return response;
	}

}
