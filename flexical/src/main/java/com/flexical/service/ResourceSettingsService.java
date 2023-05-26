package com.flexical.service;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.flexical.dao.ClientOperationDao;
import com.flexical.dao.ResourceSettingsDao;
import com.flexical.util.AllKeys;
import com.flexical.util.PropertiesUtil;
import com.flexical.util.RestUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ResourceSettingsService {

	public static final Logger logger = Logger.getLogger(ResourceSettingsService.class);
	RestUtils utils = new RestUtils();
	ClientOperationDao cDao = new ClientOperationDao();
	ResourceSettingsDao rsDao = new ResourceSettingsDao();
	
	public String addResourceSettings(JSONObject dataObject, Connection dbConnection) {
		String response = null;
		String clientKey = "", vendorId = "", resourceId = "", fromDate = "", toDate = "";
		int clientId, clientVendorId = 0, slotTimeId;
		if (dataObject.containsKey(AllKeys.CLIENT_KEY)) {
			clientKey = dataObject.getString(AllKeys.CLIENT_KEY);
			if (utils.isEmpty(clientKey)) {
				response = utils.processError(PropertiesUtil.getProperty("empty_clientkey_code"),
						PropertiesUtil.getProperty("empty_clientkey_message"));
				logger.info("Response is :" + response);
				return response;
			}else {
				clientId = cDao.getClientId(clientKey,  dbConnection);
				if(clientId <= 0) {
					response = utils.processError(PropertiesUtil.getProperty("invalid_clientkey_code"),
							PropertiesUtil.getProperty("invalid_clientkey_message"));
					logger.info("Response is :" + response);
					return response;
				}
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_clientkey_key_code"),
					PropertiesUtil.getProperty("missing_clientkey_key_message"));
			logger.info("Response is :" + response);
			return response;
		}

		if (dataObject.containsKey(AllKeys.VENDORID_KEY)) {
			vendorId = dataObject.getString(AllKeys.VENDORID_KEY);
			if (utils.isEmpty(vendorId)) {
				vendorId="-1";
			}else {
				clientVendorId = cDao.checkVendorExist(vendorId,  dbConnection);
				if(clientVendorId <= 0) {
					clientVendorId = cDao.addClientVendor(clientId, vendorId, dbConnection);
				}
			}
		} else {
			vendorId="-1";
		}

		if (dataObject.containsKey(AllKeys.RESOURCEID_KEY)) {
			resourceId = dataObject.getString(AllKeys.RESOURCEID_KEY);
			if (utils.isEmpty(resourceId)) {
				response = utils.processError(PropertiesUtil.getProperty("empty_resourceid_code"),
						PropertiesUtil.getProperty("empty_resourceid_message"));
				logger.info("Response is :" + response);
				return response;
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_resourceid_key_code"),
					PropertiesUtil.getProperty("missing_resourceid_key_message"));
			logger.info("Response is :" + response);
			return response;
		}
		
		if(dataObject.containsKey(AllKeys.FROMDATE_KEY)) {
			fromDate = dataObject.getString(AllKeys.FROMDATE_KEY);
			if (utils.isEmpty(fromDate)) {
				response = utils.processError(PropertiesUtil.getProperty("empty_from_date_code"),
						PropertiesUtil.getProperty("empty_from_date_message"));
				logger.info("Response is :" + response);
				return response;
			}else if(!utils.isDateValid(fromDate)){
				response = utils.processError(PropertiesUtil.getProperty("invalid_dateformat_code"),
						PropertiesUtil.getProperty("invalid_dateformat_message"));
				logger.info("Response is :" + response);
				return response;
			}
		}else {
			response = utils.processError(PropertiesUtil.getProperty("missing_from_date_key_code"),
					PropertiesUtil.getProperty("missing_from_date_key_message"));
			logger.info("Response is :" + response);
			return response;
		}

		if(dataObject.containsKey(AllKeys.TODATE_KEY)) {
			toDate = dataObject.getString(AllKeys.TODATE_KEY);
			if (utils.isEmpty(toDate)) {
				response = utils.processError(PropertiesUtil.getProperty("empty_to_date_code"),
						PropertiesUtil.getProperty("empty_to_date_message"));
				logger.info("Response is :" + response);
				return response;
			}else if(!utils.isDateValid(toDate)){
				response = utils.processError(PropertiesUtil.getProperty("invalid_dateformat_code"),
						PropertiesUtil.getProperty("invalid_dateformat_message"));
				logger.info("Response is :" + response);
				return response;
			}
		}else {
			response = utils.processError(PropertiesUtil.getProperty("missing_to_date_key_code"),
					PropertiesUtil.getProperty("missing_to_date_key_message"));
			logger.info("Response is :" + response);
			return response;
		}

		/*if(dataObject.containsKey(AllKeys.SLOTTIMEID_KEY)) {
			slotTimeId = dataObject.getInt(AllKeys.SLOTTIMEID_KEY);
			if (utils.isEmpty(toDate)) {
				response = utils.processError(PropertiesUtil.getProperty("empty_to_date_code"),
						PropertiesUtil.getProperty("empty_to_date_message"));
				logger.info("Response is :" + response);
				return response;
			}else if(!utils.isDateValid(toDate)){
				response = utils.processError(PropertiesUtil.getProperty("invalid_dateformat_code"),
						PropertiesUtil.getProperty("invalid_dateformat_message"));
				logger.info("Response is :" + response);
				return response;
			}
		}else {
			slotTimeId = ;
		}*/
		
		rsDao.addResourceAvailabilitySettings(dataObject, dbConnection);
		return response;
	}
}
