package com.flexical.service;

import java.sql.Connection;
import org.apache.log4j.Logger;
import com.flexical.dao.ClientOperationDao;
import com.flexical.dao.ResourceSettingsDao;
import com.flexical.model.ResourceAvailabilityBean;
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
	AvailabilityCalculator ac = new AvailabilityCalculator();

	public String addResourceSettings(JSONObject dataObject, Connection dbConnection) {
		String response = null;
		String clientKey = "", vendorId = "", resourceId = "", startTime = "", endTime = "", timeZone = "Asia/Kolkota";
		int clientId, clientVendorId, weekdayId, working, resourceVendorId, slotTimeId;
		if (dataObject.containsKey(AllKeys.CLIENT_KEY)) {
			clientKey = dataObject.getString(AllKeys.CLIENT_KEY);
			if (utils.isEmpty(clientKey)) {
				response = utils.processError(PropertiesUtil.getProperty("empty_clientkey_code"),
						PropertiesUtil.getProperty("empty_clientkey_message"));
				logger.info("Response is :" + response);
				return response;
			} else {
				clientId = cDao.getClientId(clientKey, dbConnection);
				if (clientId <= 0) {
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
				vendorId = "-1";
			}
		} else {
			vendorId = "-1";
		}
		clientVendorId = cDao.checkVendorExist(vendorId, clientId, dbConnection);
		if (clientVendorId <= 0) {
			clientVendorId = cDao.addClientVendor(clientId, vendorId, dbConnection);
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
		if (dataObject.containsKey(AllKeys.STARTTIME_KEY)) {
			startTime = dataObject.getString(AllKeys.STARTTIME_KEY);
			if (utils.isEmpty(startTime)) {
				response = utils.processError(PropertiesUtil.getProperty("empty_start_time_code"),
						PropertiesUtil.getProperty("empty_start_time_message"));
				logger.info("Response is :" + response);
				return response;
			} else if (!utils.isTimeValid(startTime)) {
				response = utils.processError(PropertiesUtil.getProperty("invalid_timeformat_code"),
						PropertiesUtil.getProperty("invalid_timeformat_message"));
				logger.info("Response is :" + response);
				return response;
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_start_time_key_code"),
					PropertiesUtil.getProperty("missing_start_time_key_message"));
			logger.info("Response is :" + response);
			return response;
		}
		if (dataObject.containsKey(AllKeys.ENDTIME_KEY)) {
			endTime = dataObject.getString(AllKeys.ENDTIME_KEY);
			if (utils.isEmpty(endTime)) {
				response = utils.processError(PropertiesUtil.getProperty("empty_end_time_code"),
						PropertiesUtil.getProperty("empty_end_time_message"));
				logger.info("Response is :" + response);
				return response;
			} else if (!utils.isTimeValid(endTime)) {
				response = utils.processError(PropertiesUtil.getProperty("invalid_timeformat_code"),
						PropertiesUtil.getProperty("invalid_timeformat_message"));
				logger.info("Response is :" + response);
				return response;
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_end_time_key_code"),
					PropertiesUtil.getProperty("missing_end_time_key_message"));
			logger.info("Response is :" + response);
			return response;
		}
		if (dataObject.containsKey(AllKeys.WEEKDAYID_KEY)) {
			weekdayId = dataObject.getInt(AllKeys.WEEKDAYID_KEY);
			if (weekdayId < 1) {
				response = utils.processError(PropertiesUtil.getProperty("invalid_weekdayid_code"),
						PropertiesUtil.getProperty("invalid_weekdayid_message"));
				logger.info("Response is :" + response);
				return response;
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_weekdayid_key_code"),
					PropertiesUtil.getProperty("missing_weekdayid_key_message"));
			logger.info("Response is :" + response);
			return response;
		}
		if (dataObject.containsKey(AllKeys.WORKING_KEY)) {
			working = dataObject.getInt(AllKeys.WORKING_KEY);
			if (working == 1 || working == 0) {
			} else {
				response = utils.processError(PropertiesUtil.getProperty("invalid_working_code"),
						PropertiesUtil.getProperty("invalid_working_message"));
				logger.info("Response is :" + response);
				return response;
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_working_key_code"),
					PropertiesUtil.getProperty("missing_working_key_message"));
			logger.info("Response is :" + response);
			return response;
		}
		if (dataObject.containsKey(AllKeys.SLOTTIMEID_KEY)) {
			slotTimeId = dataObject.getInt(AllKeys.SLOTTIMEID_KEY);
			System.out.println("slotTimeId " + slotTimeId);
			if (slotTimeId < 1) {
				response = utils.processError(PropertiesUtil.getProperty("invalid_slottimeid_code"),
						PropertiesUtil.getProperty("invalid_slottimeid_message"));
				logger.info("Response is :" + response);
				return response;
			}
		} else {
			slotTimeId = 1;
		}
		resourceVendorId = rsDao.checkResourceExist(resourceId, vendorId, clientId, dbConnection);
		if (resourceVendorId <= 0) {
			resourceVendorId = rsDao.addResourceVendor(resourceId, vendorId, clientId, clientVendorId, slotTimeId, timeZone, dbConnection);
		}
		String slotTime = cDao.getSlotTimeFromresourceId(resourceId, dbConnection);
		JSONObject obj = new JSONObject();
		//int resourceAvailabiltyId = rsDao.addResourceAvailabilitySettings(clientId, vendorId, clientVendorId, resourceId, resourceVendorId, weekdayId, startTime, endTime, working, dbConnection);
		int resourceAvailabiltyId = rsDao.addResourceAvailabilitySettingsbySlotTime(clientId, vendorId, clientVendorId, resourceId, resourceVendorId, weekdayId, startTime, endTime, slotTime, working, dbConnection);
		obj.put(AllKeys.RESOURCEAVAILABILITYID_KEY, resourceAvailabiltyId);
		response = utils.processSucessForModules("data", obj);
		return response;
	}

	public String getResourceAvailability(ResourceAvailabilityBean dataObject, Connection dbConnection) {
		String response = null;
		String clientKey = "", vendorId = "", resourceId = "";
		int clientId, weekday;
		clientKey = dataObject.getClientKey();
		if (dataObject.getVendorId().isEmpty()) {
			vendorId = "-1";
		} else {
			vendorId = dataObject.getVendorId();
		}
		resourceId = dataObject.getResourceId();
		clientId = cDao.getClientId(clientKey, dbConnection);
		if (clientId <= 0) {
			response = utils.processError(PropertiesUtil.getProperty("invalid_clientkey_code"),
					PropertiesUtil.getProperty("invalid_clientkey_message"));
			logger.info("Response is :" + response);
			return response;
		}
		if (dataObject.getDate() == null) {
			weekday = 0;
		} else {
			weekday = dataObject.getDate().getDay() + 1;
		}
		JSONObject obj = new JSONObject();
		JSONArray jsonArray = rsDao.getResourceAvailabilitySettings(clientId, vendorId, resourceId, weekday, dbConnection);
		JSONArray resultArray = ac.availabilityCalculator(jsonArray);
		response = utils.processSucessForModules("data", resultArray);
		return response;
	}
}
