package com.infosoft.controller;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.infosoft.connections.InfosoftConnection;
import com.infosoft.service.CallService;
import com.infosoft.utils.Utils;

import net.sf.json.JSONObject;

@RestController
public class VoiceController {
	public static final Logger logger = Logger.getLogger(VoiceController.class);
	Utils util = new Utils();

	@Autowired
	private CallService cs;

	@Autowired
	InfosoftConnection ic;

	@PostMapping(value = "/makeCall", produces = { "application/json" })
	public String makeCall(@RequestBody String request) throws SQLException {
		String response = null;
		if (util.isJsonValid(request)) {
			Connection con = ic.dataBaseConnection();
			logger.info("request is " + request);
			JSONObject dataObj = JSONObject.fromObject(request).getJSONObject("request").getJSONObject("data");
			logger.info("data object is " + dataObj);
			response = cs.makeCall(dataObj, con);
		}
		return response;
	}

	@PostMapping("/statusCallBack")
	public void callBackStatus(@RequestBody String request) {
		logger.info("inside call status " + request);
		if (util.isJsonValid(request)) {
			Connection con = ic.dataBaseConnection();
			JSONObject callBackResponse = JSONObject.fromObject(request);
			cs.updateCallBackResponse(callBackResponse, con);
		}

	}

}
