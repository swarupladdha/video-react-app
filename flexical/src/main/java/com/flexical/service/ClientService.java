package com.flexical.service;

import java.sql.Connection;
import java.time.LocalTime;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexical.dao.ClientOperationDao;
import com.flexical.model.Availability;
import com.flexical.model.ClientDetailsBean;
import com.flexical.model.ClientSettingsBean;
import com.flexical.util.AllKeys;
import com.flexical.util.PropertiesUtil;
import com.flexical.util.RestUtils;

import net.sf.json.JSONObject;

public class ClientService {

	public static final Logger logger = Logger.getLogger(ClientService.class);
	RestUtils utils = new RestUtils();
	ClientOperationDao cDao = new ClientOperationDao();
	
	//public String generateClientKey(JSONObject dataObject, Connection dbConnection) {
	public JSONObject generateClientKey(ClientDetailsBean clientDetails, Connection dbConnection) throws JsonProcessingException {
		String response = null;
		String orgName = clientDetails.getOrgName();
		String address = clientDetails.getAddress();
		String contact = clientDetails.getContact();
		int slotTimeId = clientDetails.getSlottimeId();
		String timeZone = clientDetails.getTimezone();
		String status = clientDetails.getStatus();
		int clientId = cDao.checkClientExist(orgName, contact,  dbConnection);
		String key = null;
		JSONObject obj = new JSONObject();
		if(clientId > 0) {
			obj.put(AllKeys.MESSAGE_KEY, "This Client already exists");
		}else {
			clientId = cDao.addClientscreat(status, dbConnection);
			cDao.addClientContact(clientId, orgName, address, contact, slotTimeId, timeZone, dbConnection);
			cDao.addClientDefaultAvailabilitySettings(clientId, dbConnection);
		}
		key = cDao.getClientKey(clientId, dbConnection);
		obj.put(AllKeys.CLIENTID_KEY, clientId);
		obj.put(AllKeys.CLIENT_KEY, key);
		response = utils.processSucessForModules("data", obj);
		return obj;
	}

	public String addClientAvailabilitySettings(ClientSettingsBean dataObject, Connection dbConnection) throws JsonProcessingException {
		String response = null;
		int clientId = 0, weekdayId = 0, timeSlot = 0, working = 0;
		String clientKey, description = "";
		JSONObject obj = new JSONObject();
		ObjectMapper mapper = new ObjectMapper();

		clientId = cDao.getClientId(dataObject.getClientKey(), dbConnection);
		//clientId = cDao.getClientId(dataObject.getString("clientKey"), dbConnection);
		if (clientId <= 0) {
			response = utils.processError(PropertiesUtil.getProperty("invalid_clientkey_code"),
					PropertiesUtil.getProperty("invalid_clientkey_message"));
			logger.info("Response is :" + response);
			return response;
		}
		logger.info("dataObject "+mapper.writeValueAsString(dataObject));
		//List<JSONObject> availabilityList = dataObject.getJSONArray("availability");
		List<Availability> availabilityList = dataObject.getAvailability();
		String Values = "";
		logger.info("before for "+mapper.writeValueAsString(availabilityList));
		for (Availability availabilityDay : availabilityList) {
			weekdayId = availabilityDay.getWeekdayId();
			working = availabilityDay.getWorking();
			if(working == 1 ) {
				LocalTime startTime = LocalTime.parse(availabilityDay.getStartTime());
			    LocalTime endTime = LocalTime.parse(availabilityDay.getEndTime());

			    // Perform any validation checks on the start and end time
			    // Example: Check if start time is before end time
			    if (startTime.isAfter(endTime)) {
			        // Handle the validation error
			        // You can throw an exception, log an error, or return an error message
			        throw new IllegalArgumentException("Start time must be before end time");
			    }
			}else {
				availabilityDay.setStartTime("00:00:00");
				availabilityDay.setEndTime("00:00:00");
			}
		}
		logger.info("after for "+mapper.writeValueAsString(availabilityList));

		cDao.addClientAvailabilitySettings(clientId, availabilityList, dbConnection);
		//cDao.addClientAvailabilitySettings(clientId, dataObject, dbConnection);
				response = utils.processSucessForModules("data", obj);
		return response;
	}

}
