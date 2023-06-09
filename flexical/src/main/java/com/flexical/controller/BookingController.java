package com.flexical.controller;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexical.model.BookingDetailsBean;
import com.flexical.model.GetAvailabilityBean;
import com.flexical.service.BookingService;
import com.flexical.util.AllKeys;
import com.flexical.util.ConnectionPooling;
import com.flexical.util.RestUtils;

import jakarta.validation.Valid;
import net.sf.json.JSONObject;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "false", allowedHeaders = "*")
@RequestMapping(value = "/bookingList", produces = "application/json")
@Validated
public class BookingController {
	private static final String parentPath = "/bookingList";
	private static final String AddSchedule = "/AddSchedule";
	private static final String GetSchedule = "/GetSchedule";

	BookingService bService = new BookingService();
	ConnectionPooling connectionPooling = null;
	Connection dbConnection = null;

	public static final Logger logger = Logger.getLogger(BookingController.class);
	RestUtils utils = new RestUtils();
	ObjectMapper mapper = new ObjectMapper();

	@PostMapping(value = AddSchedule)
	// public String addSchedule(@RequestBody String request){
	public String addSchedule(@Valid @RequestBody BookingDetailsBean bookingDetails) throws JsonProcessingException {
		String response = null;

		String request = mapper.writeValueAsString(bookingDetails);
		utils.printRequest(parentPath + AddSchedule, request);
		connectionPooling = ConnectionPooling.getInstance();
		dbConnection = connectionPooling.getConnection();
		response = bService.addSchedule(bookingDetails, dbConnection);
		if (dbConnection != null)
			connectionPooling.close(dbConnection);
		return response;
	}

	@GetMapping(value = GetSchedule)
	public String getSchedule(@Valid @RequestBody GetAvailabilityBean getDetails) throws JsonProcessingException {
		String response = null;

		String request = mapper.writeValueAsString(getDetails);
		utils.printRequest(parentPath + GetSchedule, request);
		connectionPooling = ConnectionPooling.getInstance();
		dbConnection = connectionPooling.getConnection();
		response = bService.getSchedule(getDetails, dbConnection);
		if (dbConnection != null)
			connectionPooling.close(dbConnection);

		System.out.println("response: " + response);
		return response;
	}

}
