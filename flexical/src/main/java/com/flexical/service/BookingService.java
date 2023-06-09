package com.flexical.service;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;

import com.flexical.dao.BookingOperationDao;
import com.flexical.dao.ClientOperationDao;
import com.flexical.dao.ResourceSettingsDao;
import com.flexical.model.BookingDetailsBean;
import com.flexical.model.GetAvailabilityBean;
import com.flexical.util.AllKeys;
import com.flexical.util.PropertiesUtil;
import com.flexical.util.RestUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BookingService {

	public static final Logger logger = Logger.getLogger(BookingService.class);
	RestUtils utils = new RestUtils();
	ClientOperationDao cDao = new ClientOperationDao();
	BookingOperationDao bDao = new BookingOperationDao();
	ResourceSettingsDao rsDao = new ResourceSettingsDao();

	public String addSchedule(BookingDetailsBean bookingDetails, Connection dbConnection) {
		String response = null;
		String clientKey = "", vendorId = "", resourceId = "", userId = "", startTime = "", slotTime = "";
		int clientId, clientVendorId = 0, resourceVendorId;
		clientKey = bookingDetails.getClientKey();
		vendorId = bookingDetails.getVendorId();
		resourceId = bookingDetails.getResourceId();
		userId = bookingDetails.getUserId();
		startTime = bookingDetails.getStartTime();
		if (userId.equals(resourceId)) {
			//response = utils.processError(PropertiesUtil.getProperty("userid_and_resourceid_code"), PropertiesUtil.getProperty("userid_and_resourceid_message"));
			response = utils.processError(HttpStatus.OK, PropertiesUtil.getProperty("userid_and_resourceid_message"));
			logger.info("Response is :" + response);
			return response;
		}
		clientId = cDao.getClientId(clientKey, dbConnection);
		if (clientId <= 0) {
			//response = utils.processError(PropertiesUtil.getProperty("invalid_clientkey_code"), PropertiesUtil.getProperty("invalid_clientkey_message"));
			response = utils.processError(HttpStatus.OK, PropertiesUtil.getProperty("invalid_clientkey_message"));
			logger.info("Response is :" + response);
			return response;
		}
		clientVendorId = cDao.checkVendorExist(vendorId, clientId, dbConnection);
		if (clientVendorId <= 0) {
			clientVendorId = cDao.addClientVendor(clientId, vendorId, dbConnection);
		}

		resourceVendorId = cDao.checkResourceExist(resourceId, dbConnection);
		if (resourceVendorId <= 0) {
			cDao.addResourceVendor(clientId, vendorId, resourceId, clientVendorId, dbConnection);
		}

		resourceVendorId = cDao.checkResourceExist(userId, dbConnection);
		if (resourceVendorId <= 0) {
			cDao.addResourceVendor(clientId, vendorId, userId, clientVendorId, dbConnection);
		}
		// slotTime = cDao.getSlotTimeFromresourceId(userId, dbConnection);
		logger.info("clientId " + clientId + " vendorId " + vendorId + " resourceId " + resourceId + " userId " + userId
				+ " startTime " + startTime + " slotTime " + slotTime);

		// boolean resourceavailable = rsDao.checkResourceAvailabiliy(clientId,
		// vendorId, resourceId, userId, startTime, slotTime);
		boolean result = bDao.checkBookingAvailability(clientId, vendorId, resourceId, userId, startTime, dbConnection);
		if (!result) {
			//response = utils.processError(PropertiesUtil.getProperty("no_slots_available_code"), PropertiesUtil.getProperty("no_slots_available_message"));
			response = utils.processError(HttpStatus.OK, PropertiesUtil.getProperty("no_slots_available_message"));
			logger.info("Response is :" + response);
			return response;
		} else {
			JSONObject obj = new JSONObject();
			int bookingId = bDao.addBookingSchedule(clientId, vendorId, resourceId, userId, startTime, slotTime, dbConnection);

			obj.put(AllKeys.BOOKINGID_KEY, bookingId);
			response = utils.processSucessForModules("data", obj);
		}
		return response;
	}

	public String getSchedule(GetAvailabilityBean getDetails, Connection dbConnection) {
		String response = null;
		String clientKey = "", vendorId = "", resourceId = "";
		int clientId, clientVendorId = 0;
		clientKey = getDetails.getClientKey();
		vendorId = getDetails.getVendorId();
		resourceId = getDetails.getResourceId();
		clientId = cDao.getClientId(clientKey, dbConnection);
		if (clientId <= 0) {
			//response = utils.processError(PropertiesUtil.getProperty("invalid_clientkey_code"), PropertiesUtil.getProperty("invalid_clientkey_message"));
			response = utils.processError(HttpStatus.OK, PropertiesUtil.getProperty("invalid_clientkey_message"));
			logger.info("Response is :" + response);
			return response;
		}

		logger.info("clientId " + clientId + " vendorId " + vendorId + " resourceId " + resourceId);
		JSONArray bookingDetails = bDao.getBookingDetails(clientId, vendorId, resourceId, dbConnection);
		response = utils.processSucessForModules("data", bookingDetails);
		return response;
	}
}
