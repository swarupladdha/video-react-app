package com.flexical.controller;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexical.model.ResourceAvailabilityBean;
import com.flexical.service.ResourceSettingsService;
import com.flexical.util.AllKeys;
import com.flexical.util.ConnectionPooling;
import com.flexical.util.RestUtils;

import jakarta.validation.Valid;
import net.sf.json.JSONObject;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "false", allowedHeaders = "*")
@RequestMapping(value = "/ResourceSettings", produces = "application/json")
@Validated
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
	public String addResourceAvailabilty(@RequestBody String request) {
		String response = null;

		utils.printRequest(parentPath + AddResourceAvailability, request);
		connectionPooling = ConnectionPooling.getInstance();
		dbConnection = connectionPooling.getConnection();
		try {
			if (utils.isJsonValid(request)) {
				JSONObject jsonRequest = JSONObject.fromObject(request);
				JSONObject dataObject = jsonRequest.getJSONObject(AllKeys.JSON_KEY).getJSONObject(AllKeys.REQUEST_KEY)
						.getJSONObject(AllKeys.DATA_KEY);

				response = rsService.addResourceSettings(dataObject, dbConnection);

			}
		} catch (Exception e) {
			logger.error("Exception in addResourceAvailabilty", e);
			response = utils.genericError();
		} finally {
			try {
				if (dbConnection != null)
					connectionPooling.close(dbConnection);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("response: " + response);
		return response;
	}

	@GetMapping(value = GetResourceAvailability)
	public String getResourceAvailability(@Valid @RequestBody ResourceAvailabilityBean resourceAvail)
			throws MethodArgumentNotValidException, JsonProcessingException {
		String response = null;

		ObjectMapper mapper = new ObjectMapper();
		String request = mapper.writeValueAsString(resourceAvail);
		utils.printRequest(parentPath + GetResourceAvailability, request);
		connectionPooling = ConnectionPooling.getInstance();
		dbConnection = connectionPooling.getConnection();

		System.out.println("request " + request);

		response = rsService.getResourceAvailability(resourceAvail, dbConnection);
		if (dbConnection != null)
			connectionPooling.close(dbConnection);
		System.out.println("response: " + response);
		return response;
	}

}
