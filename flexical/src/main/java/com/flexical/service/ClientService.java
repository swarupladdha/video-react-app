package com.flexical.service;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.flexical.dao.ClientOperationDao;
import com.flexical.util.AllKeys;
import com.flexical.util.PropertiesUtil;
import com.flexical.util.RestUtils;

import net.sf.json.JSONObject;

public class ClientService {

	public static final Logger logger = Logger.getLogger(ClientService.class);
	RestUtils utils = new RestUtils();
	ClientOperationDao cDao = new ClientOperationDao();
	
	public String generateClientKey(JSONObject dataObject, Connection dbConnection) {
		String response = null;
		String orgName = "";
		String address = "";
		String contact = "";
		String status = "0";
		if (dataObject.containsKey(AllKeys.ORGNAME_KEY)) {
			orgName = dataObject.getString(AllKeys.ORGNAME_KEY);
			if (utils.isEmpty(orgName)) {
				response = utils.processError(PropertiesUtil.getProperty("empty_org_name_code"),
						PropertiesUtil.getProperty("empty_org_name_message"));
				logger.info("Response is :" + response);
				return response;
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_org_name_key_code"),
					PropertiesUtil.getProperty("missing_org_name_key_message"));
			logger.info("Response is :" + response);
			return response;
		}

		if (dataObject.containsKey(AllKeys.ADDRESS_KEY)) {
			address = dataObject.getString(AllKeys.ADDRESS_KEY);
			if (utils.isEmpty(address)) {
				response = utils.processError(PropertiesUtil.getProperty("empty_address_code"),
						PropertiesUtil.getProperty("empty_address_message"));
				logger.info("Response is :" + response);
				return response;
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_address_key_code"),
					PropertiesUtil.getProperty("missing_address_key_message"));
			logger.info("Response is :" + response);
			return response;
		}

		if (dataObject.containsKey(AllKeys.CONTACT_KEY)) {
			contact = dataObject.getString(AllKeys.CONTACT_KEY);
			if (utils.isEmpty(contact)) {
				response = utils.processError(PropertiesUtil.getProperty("empty_contact_code"),
						PropertiesUtil.getProperty("empty_contact_message"));
				logger.info("Response is :" + response);
				return response;
			} else if (!utils.isValidMobile(contact)) {
				response = utils.processError(PropertiesUtil.getProperty("invalid_status_code"),
						PropertiesUtil.getProperty("invalid_status_message"));
				logger.info("Response is :" + response);
				return response;
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_contact_key_code"),
					PropertiesUtil.getProperty("missing_contact_key_message"));
			logger.info("Response is :" + response);
			return response;
		}

		if (dataObject.containsKey(AllKeys.STATUS_KEY)) {
			status = dataObject.getString(AllKeys.STATUS_KEY);
			if(utils.isEmpty(status)) {
				status = "0";
			}
		}
		int clientId = cDao.checkClientExist(orgName, address, contact,  dbConnection);
		String key = null;
		JSONObject obj = new JSONObject();
		if(clientId > 0) {
			obj.put(AllKeys.MESSAGE_KEY, "This Client already exists");
		}else {
			clientId = cDao.addClientscreat(status, dbConnection);
			cDao.addClientContact(clientId, orgName, address, contact, dbConnection);
		}
		key = cDao.getClientKey(clientId, dbConnection);
		obj.put(AllKeys.CLIENTID_KEY, clientId);
		obj.put(AllKeys.CLIENT_KEY, key);
		response = utils.processSucessForModules("data", obj);
		return response;
	}

	public String addBusinessSettings(JSONObject dataObject, Connection dbConnection) {
		String response = null;
		int clientId = 0, eventId = 0, timeSlot = 0;
		String description = "";
		if (dataObject.containsKey(AllKeys.CLIENTID_KEY)) {
			clientId = dataObject.getInt(AllKeys.CLIENTID_KEY);
			//if (utils.isEmpty(clientId)) {
			if(clientId <= 0) {
				response = utils.processError(PropertiesUtil.getProperty("empty_clientid_code"),
						PropertiesUtil.getProperty("empty_clientid_message"));
				logger.info("Response is :" + response);
				return response;
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_clientid_key_code"),
					PropertiesUtil.getProperty("missing_clientid_key_message"));
			logger.info("Response is :" + response);
			return response;
		}

		if (dataObject.containsKey(AllKeys.EVENTID_KEY)) {
			eventId = dataObject.getInt(AllKeys.EVENTID_KEY);
			//if (utils.isEmpty(clientId)) {
			if(eventId <= 0) {
				System.out.println("ifffff");
				response = utils.processError(PropertiesUtil.getProperty("empty_eventid_code"),
						PropertiesUtil.getProperty("empty_eventid_message"));
				logger.info("Response is :" + response);
				return response;
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_eventid_key_code"),
					PropertiesUtil.getProperty("missing_eventid_key_message"));
			logger.info("Response is :" + response);
			return response;
		}

		if (dataObject.containsKey(AllKeys.DESCRIPTION_KEY)) {
			description = dataObject.getString(AllKeys.DESCRIPTION_KEY);
			//if (utils.isEmpty(clientId)) {
			if(utils.isEmpty(description)) {
				System.out.println("ifffff");
				response = utils.processError(PropertiesUtil.getProperty("empty_description_code"),
						PropertiesUtil.getProperty("empty_description_code"));
				logger.info("Response is :" + response);
				return response;
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_description_key_code"),
					PropertiesUtil.getProperty("missing_description_key_message"));
			logger.info("Response is :" + response);
			return response;
		}

		if (dataObject.containsKey(AllKeys.TIMESLOT_KEY)) {
			timeSlot = dataObject.getInt(AllKeys.TIMESLOT_KEY);
			//if (utils.isEmpty(clientId)) {
			if(timeSlot <= 0) {
				response = utils.processError(PropertiesUtil.getProperty("empty_timeslot_code"),
						PropertiesUtil.getProperty("empty_timeslot_message"));
				logger.info("Response is :" + response);
				return response;
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_timeslot_key_code"),
					PropertiesUtil.getProperty("missing_timeslot_key_message"));
			logger.info("Response is :" + response);
			return response;
		}

		JSONObject obj = new JSONObject();
		cDao.addClientSettings(dataObject, dbConnection);
		/*int clientId = cDao.checkClientExist(orgName, address, contact,  dbConnection);
		String key = null;
		if(clientId <= 0) {
			key = UUID.randomUUID().toString();
			clientId = cDao.addClientscreat(key, status, dbConnection);
			cDao.addClientContact(clientId, orgName, address, contact, dbConnection);
		}else {
			key = cDao.getClientKey(clientId, dbConnection);
			obj.put(AllKeys.MESSAGE_KEY, "This Client already exists");
		}
		obj.put(AllKeys.CLIENTID_KEY, clientId);
		obj.put(AllKeys.CLIENT_KEY, key);*/
		response = utils.processSucessForModules("data", obj);
		return response;
	}

}
