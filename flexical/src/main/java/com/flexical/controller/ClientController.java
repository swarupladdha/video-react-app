package com.flexical.controller;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flexical.service.ClientService;
import com.flexical.util.AllKeys;
import com.flexical.util.ConnectionPooling;
import com.flexical.util.RestUtils;

import net.sf.json.JSONObject;

@RestController
@CrossOrigin(origins = "*",maxAge=3600,allowCredentials= "false",allowedHeaders = "*")
@RequestMapping(value = "/clientList", produces = "application/json")
public class ClientController {
	private static final String parentPath = "/clientList";
	private static final String GenerateClientKey = "/GenerateClientKey";
	private static final String AddBusinessSettings = "/AddBusinessSettings";
	//private static final String RegenerateClientKey = "/RegenerateClientKey";
	
	ClientService cService = new ClientService();
	ConnectionPooling connectionPooling = null;
	Connection dbConnection = null;
	
	public static final Logger logger = Logger.getLogger(ClientController.class);
	RestUtils utils = new RestUtils();
	
	@GetMapping(value = GenerateClientKey)
	public String getGenerateClientKey(@RequestBody String request){
		String response = null;
		
		utils.printRequest(parentPath+GenerateClientKey, request);
		connectionPooling = ConnectionPooling.getInstance();
		dbConnection = connectionPooling.getConnection();
		try
		{
			if(utils.isJsonValid(request)) 
			{
				JSONObject jsonRequest = JSONObject.fromObject(request);
				JSONObject dataObject = jsonRequest.getJSONObject(AllKeys.JSON_KEY).getJSONObject(AllKeys.REQUEST_KEY)
						.getJSONObject(AllKeys.DATA_KEY);

				response = cService.generateClientKey(dataObject, dbConnection);
				
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
		
		logger.info("response: " + response);
		return response;
	}
	
	@GetMapping(value = AddBusinessSettings)
	public String addBusinessSettings(@RequestBody String request){
		String response = null;
		
		utils.printRequest(parentPath+AddBusinessSettings, request);
		connectionPooling = ConnectionPooling.getInstance();
		dbConnection = connectionPooling.getConnection();
		try
		{
			if(utils.isJsonValid(request)) 
			{
				JSONObject jsonRequest = JSONObject.fromObject(request);
				JSONObject dataObject = jsonRequest.getJSONObject(AllKeys.JSON_KEY).getJSONObject(AllKeys.REQUEST_KEY)
						.getJSONObject(AllKeys.DATA_KEY);

				response = cService.addBusinessSettings(dataObject, dbConnection);
				
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
