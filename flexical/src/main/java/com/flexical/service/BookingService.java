package com.flexical.service;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.flexical.dao.BookingOperationDao;
import com.flexical.dao.ClientOperationDao;
import com.flexical.dao.ResourceSettingsDao;
import com.flexical.util.AllKeys;
import com.flexical.util.PropertiesUtil;
import com.flexical.util.RestUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BookingService {

	public static final Logger logger = Logger.getLogger(BookingService.class);
	RestUtils utils = new RestUtils();
	ClientOperationDao cDao = new ClientOperationDao();
	BookingOperationDao bDao =new BookingOperationDao();
	ResourceSettingsDao rsDao = new ResourceSettingsDao();

	public String addSchedule(JSONObject dataObject, Connection dbConnection) {
		String response = null;
		String clientKey = "", vendorId = "", resourceId = "", userId = "",startTime = "", slotTime = "";
		int clientId, clientVendorId = 0, resourceVendorId;
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
				clientVendorId = cDao.checkVendorExist(vendorId, clientId, dbConnection);
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
			}else {
				resourceVendorId = cDao.checkResourceExist(resourceId, dbConnection);
				if(resourceVendorId <= 0) {
						cDao.addResourceVendor(clientId, vendorId, resourceId, clientVendorId, dbConnection);
				}
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_resourceid_key_code"),
					PropertiesUtil.getProperty("missing_resourceid_key_message"));
			logger.info("Response is :" + response);
			return response;
		}

		if (dataObject.containsKey(AllKeys.USERID_KEY)) {
			userId = dataObject.getString(AllKeys.USERID_KEY);
			if (utils.isEmpty(userId)) {
				response = utils.processError(PropertiesUtil.getProperty("empty_userid_code"),
						PropertiesUtil.getProperty("empty_userid_message"));
				logger.info("Response is :" + response);
				return response;
			}else {
				resourceVendorId = cDao.checkResourceExist(userId, dbConnection);
				if(resourceVendorId <= 0) {
					cDao.addResourceVendor(clientId, vendorId, userId, clientVendorId, dbConnection);
				}
				slotTime = cDao.getSlotTimeFromresourceId(userId, dbConnection);
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_userid_key_code"),
					PropertiesUtil.getProperty("missing_userid_key_message"));
			logger.info("Response is :" + response);
			return response;
		}

		if(userId.equals(resourceId)) {
			response = utils.processError(PropertiesUtil.getProperty("userid_and_resourceid_code"),
					PropertiesUtil.getProperty("userid_and_resourceid_message"));
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
			} else if (!utils.isDateTimeValid(startTime)) {
				response = utils.processError(PropertiesUtil.getProperty("invalid_datetimeformat_code"),
						PropertiesUtil.getProperty("invalid_datetimeformat_message"));
				logger.info("Response is :" + response);
				return response;
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_start_time_key_code"),
					PropertiesUtil.getProperty("missing_start_time_key_message"));
			logger.info("Response is :" + response);
			return response;
		}

		//boolean resourceavailable = rsDao.checkResourceAvailabiliy(clientId, vendorId, resourceId, userId, startTime, slotTime);
		boolean result = bDao.checkAvailability(clientId, vendorId, resourceId, userId, startTime, slotTime, dbConnection);
		if(result) {
			response = utils.processError(PropertiesUtil.getProperty("no_slots_available_code"),
					PropertiesUtil.getProperty("no_slots_available_message"));
			logger.info("Response is :" + response);
			return response;
		}
		JSONObject obj = new JSONObject();
		int bookingId = bDao.addBookingSchedule(clientId, vendorId, resourceId, userId, startTime, slotTime, dbConnection);;
		obj.put(AllKeys.BOOKINGID_KEY, bookingId);
		response = utils.processSucessForModules("data", obj);
		return response;
	}

	public String getSchedule(JSONObject dataObject, Connection dbConnection) {
		String response = null;
		String clientKey = "", vendorId = "", resourceId = "";
		int clientId, clientVendorId = 0;
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
				clientVendorId = cDao.checkVendorExist(vendorId, clientId, dbConnection);
				if(clientVendorId <= 0) {
					clientVendorId = cDao.addClientVendor(clientId, vendorId, dbConnection);
				}
			}
		} else {
			vendorId="-1";
		}

		if (dataObject.containsKey(AllKeys.USERID_KEY)) {
			resourceId = dataObject.getString(AllKeys.USERID_KEY);
			if (utils.isEmpty(resourceId)) {
				response = utils.processError(PropertiesUtil.getProperty("empty_userid_code"),
						PropertiesUtil.getProperty("empty_userid_message"));
				logger.info("Response is :" + response);
				return response;
			}
		} else {
			response = utils.processError(PropertiesUtil.getProperty("missing_userid_key_code"),
					PropertiesUtil.getProperty("missing_userid_key_message"));
			logger.info("Response is :" + response);
			return response;
		}
		JSONArray bookingDetails = bDao.getBookingDetails(clientId, vendorId, resourceId,  dbConnection);
		if (bookingDetails == null || bookingDetails.size() <= 0) {
			response = utils.processError(PropertiesUtil.getProperty("common_error_code"),
					PropertiesUtil.getProperty("common_error_message"));
			logger.info("Response is :" + response);
		}
		response = utils.processSucessForModules("data", bookingDetails);
		return response;
	}
}
