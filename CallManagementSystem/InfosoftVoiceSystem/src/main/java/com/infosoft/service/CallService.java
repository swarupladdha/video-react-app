package com.infosoft.service;

import java.sql.Connection;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.infosoft.connections.InfosoftConnection;
import com.infosoft.dao.CallDao;
import com.infosoft.implementation.VoiceImp;
import com.infosoft.utils.AllKeys;
import com.infosoft.utils.Utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class CallService {
	public static final Logger logger = Logger.getLogger(CallService.class);
	Utils utils = new Utils();

	@Autowired
	private VoiceImp vi;
	@Autowired
	CallDao cd;
	@Autowired
	InfosoftConnection ic;

	@Value("${callLimit}")
	private String callLimit;

	@Value("${trialCountValue}")
	private int trialCountValue;

	public String makeCall(JSONObject dataObj, Connection dbCon) {
		String response = null;
		// logger.info("inside make call ");
		String fromNumber = null;
		String toNumber = null;
		String timeLimit = null;
		String callbackURL = null;
		String callid = null;

		if (dataObj != null) {
			fromNumber = dataObj.getString(AllKeys.FROM_NUMBER);
			System.out.println("from number is " + fromNumber);
			toNumber = dataObj.getString(AllKeys.TO_NUMBER);
			System.out.println("to number is " + toNumber);
			timeLimit = dataObj.getString(AllKeys.TIME_LIMIT);
			callbackURL = dataObj.getString(AllKeys.CALLBACK_URL);
			System.out.println("call back URL is " + callbackURL);
			callid = dataObj.getString(AllKeys.CALLID);

			response = cd.insertCallDetails(fromNumber, toNumber, AllKeys.INITIATED, timeLimit, callbackURL, callid,
					dbCon);
		} else {
			response = utils.invalidJsonError();
		}

		return response;
	}

	public void initiateCall(Connection con) {
		String fromNumber = "";
		String toNumber = "";
		String timeLimit = "0";
		int id = 0;
		int trialCount = 0;
		JSONArray arr = cd.getNewPhoneDetails(trialCountValue, con);
		if (arr.size() > 0) {

			for (Object ob : arr) {
				JSONObject obj = (JSONObject) ob;
				if (obj.size() > 0) {
					if (obj.has(AllKeys.ID)) {
						id = obj.getInt(AllKeys.ID);
					}
					if (obj.has(AllKeys.FROM_NUMBER)) {
						fromNumber = obj.getString(AllKeys.FROM_NUMBER);

					}
					if (obj.has(AllKeys.TO_NUMBER)) {
						toNumber = obj.getString(AllKeys.TO_NUMBER);
					}
					if (obj.has(AllKeys.TIME_LIMIT)) {
						timeLimit = obj.getString(AllKeys.TIME_LIMIT);

					} else {
						timeLimit = callLimit;
					}

					if (obj.has(AllKeys.TRIAL_COUNT)) {
						trialCount = obj.getInt(AllKeys.TRIAL_COUNT);
					}
					if (!fromNumber.isEmpty() && !toNumber.isEmpty()) {

						String response = vi.connectToAgent(fromNumber, toNumber, timeLimit);
						if (utils.isJsonValid(response)) {
							JSONObject resObj = JSONObject.fromObject(response);
							JSONObject callObj = resObj.getJSONObject(AllKeys.CALL);
							String callSid = callObj.getString(AllKeys.SID);
							String callStatus = callObj.getString(AllKeys.STATUS);
							String callStartTime = callObj.getString(AllKeys.CALL_START_TIME);
							logger.info(callStatus);
							logger.info(callSid);
							logger.info(callStartTime);
							cd.updateCallDetailsOnceCallInitiate(id, callSid, callStatus, callStartTime, trialCount + 1,
									con);

							sendCallBackResponse(0, callSid, con);
						} else {
							cd.updateCallDetailsOnceCallInitiate(id, null, "call failed", null, trialCount + 1, con);
							sendCallBackResponse(id, null, con);

						}

					}
				}
			}
		}
	}

	public void updateCallBackResponse(JSONObject obj, Connection con) {
		String callSid = null;
		String callStatus = null;
		String callStartTime = null;
		String callEndTime = null;
		JSONArray arr = cd.updateCallDetailsAfterCallBackResponse(callSid, callStatus, callStartTime, callEndTime, con);
		if (obj != null) {
			if (obj.has(AllKeys.CALL_SID)) {
				callSid = obj.getString(AllKeys.CALL_SID);
			}
			if (obj.has(AllKeys.STATUS)) {
				callStatus = obj.getString(AllKeys.STATUS);
			}
			if (obj.has(AllKeys.CALL_START_TIME)) {
				callStartTime = obj.getString(AllKeys.CALL_START_TIME);
			}
			if (obj.has(AllKeys.CALL_END_TIME)) {
				callEndTime = obj.getString(AllKeys.CALL_END_TIME);
				logger.info(callEndTime);
			}
			if (callSid != null && callStatus != null) {
				cd.updateCallDetailsAfterCallBackResponse(callSid, callStatus, callStartTime, callEndTime, con);
				sendCallBackResponse(0, callSid, con);
			}
		}
	}

	public void sendCallBackResponse(int i, String sid, Connection con) {
		logger.info("inside send callback response ");
		int id = 0;
		String callId = null;
		String callSid = null;
		String callStatus = null;
		String callBackUrl = null;
		JSONArray arr = cd.getPhoneDetailsForCallBack(i, sid, con);
		if (arr.size() > 0) {
			for (Object ob : arr) {
				JSONObject obj = (JSONObject) ob;
				if (obj.size() > 0) {
					if (obj.has(AllKeys.ID)) {
						id = obj.getInt(AllKeys.ID);
					}
					if (obj.has(AllKeys.CALLID)) {
						callId = obj.getString(AllKeys.CALLID);

					}
					if (obj.has(AllKeys.CALLSID)) {
						callSid = obj.getString(AllKeys.CALLSID);
					}
					if (obj.has(AllKeys.CALL_STATUS)) {
						callStatus = obj.getString(AllKeys.CALL_STATUS);

					}
					if (obj.has(AllKeys.CALLBACK_URL)) {
						callBackUrl = obj.getString(AllKeys.CALLBACK_URL);

					}
					if (!utils.isEmpty(callId) && !utils.isEmpty(callStatus)) {
						String response = ic.sendCallBackResponse(callId, callSid, callStatus, callBackUrl);
						logger.info("call back response result is " + response);

					}
				}
			}
		}

	}

	public String callResponse2(JSONObject obj, Connection con) {

		String response2 = "";
		String callSid = "";
		String callStatus = "";
		JSONArray arr = cd.getNewCallDetails(con);
		if (arr.size() > 0) {
			for (Object ob : arr) {
				JSONObject obj1 = (JSONObject) ob;
				callSid = obj1.getString(AllKeys.CALLSID);

			}
		}
		if (!utils.isEmpty(callSid)) {

			logger.info("call back response result is " + response2);
			logger.info("Sid is" + callSid);

		}
		

		else {
			response2 = utils.invalidJsonError();
		}
		logger.info("inside call response2");

		
		
		response2 = vi.bringBackResponse(callSid);

		if (!utils.isJsonValid(response2)) {

			logger.info("invalid json");
		}

		JSONObject o = JSONObject.fromObject(response2);
		String status = o.getJSONObject(AllKeys.CALL).getString(AllKeys.STATUS);
		String endTime = o.getJSONObject(AllKeys.CALL).getString(AllKeys.CALL_END_TIME);
		logger.info("status from exotel is " + status);

		if (!status.equals("in-progress")) {

			if (callSid != null && callStatus != null) {

				cd.updateCallDetailsAfterCallBackResponse2(callSid, status, endTime, con);
				logger.info("Sid is" + callSid);
				logger.info("Status is" + status);
			}
		}
		return response2;

	}

}
