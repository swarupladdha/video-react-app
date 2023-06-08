package com.flexical.controller;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
import com.flexical.model.ClientDetailsBean;
import com.flexical.model.ClientSettingsBean;
import com.flexical.service.ClientService;
import com.flexical.util.AllKeys;
import com.flexical.util.ConnectionPooling;
import com.flexical.util.RestUtils;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import net.sf.json.JSONObject;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "false", allowedHeaders = "*")
@RequestMapping(value = "/clientList", produces = "application/json")
@Validated
public class ClientController {
	private static final String parentPath = "/clientList";
	private static final String GenerateClientKey = "/GenerateClientKey";
	private static final String AddClientDefaultSettings = "/AddClientDefaultSettings";
	// private static final String RegenerateClientKey = "/RegenerateClientKey";

	ClientService cService = new ClientService();
	ConnectionPooling connectionPooling = null;
	Connection dbConnection = null;

	public static final Logger logger = Logger.getLogger(ClientController.class);
	RestUtils utils = new RestUtils();

	@GetMapping(value = GenerateClientKey)
	// public String getGenerateClientKey(@RequestBody String request) {
	public JSONObject getGenerateClientKey(@Valid @RequestBody ClientDetailsBean clienDetails)
			throws JsonProcessingException {
		JSONObject response = null;

		ObjectMapper mapper = new ObjectMapper();
		String request = mapper.writeValueAsString(clienDetails);
		utils.printRequest(parentPath + GenerateClientKey, request);
		connectionPooling = ConnectionPooling.getInstance();
		dbConnection = connectionPooling.getConnection();
		//response = cService.generateClientKey(dataObject, dbConnection);
		response = cService.generateClientKey(clienDetails, dbConnection);
		if (dbConnection != null)
			connectionPooling.close(dbConnection);
		logger.info("response: " + response);
		return response;
	}

	@PostMapping(value = AddClientDefaultSettings)
	public ResponseEntity<List<String>> addClientAvailabilitySettings(
			@Valid @RequestBody ClientSettingsBean clientSettings, BindingResult bindingResult)
			throws JsonProcessingException
	// public String addClientAvailabilitySettings(@RequestBody JSONObject
	// clientSettings)
	{
		String response = null;

		ObjectMapper mapper = new ObjectMapper();
		// String request = clientSettings.toString();
		String request = mapper.writeValueAsString(clientSettings);
		utils.printRequest(parentPath + AddClientDefaultSettings, request);
		connectionPooling = ConnectionPooling.getInstance();
		dbConnection = connectionPooling.getConnection();
		response = cService.addClientAvailabilitySettings(clientSettings, dbConnection);

		if (dbConnection != null)
			connectionPooling.close(dbConnection);
		List<String> errors = bindingResult.getAllErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());

		ResponseEntity<List<String>> result = ResponseEntity.badRequest().body(errors);
		logger.info("result " + result);
		return result;
		// System.out.println("response: " + response);
		// return response;
	}

}
