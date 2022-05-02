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
		logger.info("inside make call ");
		String fromNumber = null;
		String toNumber = null;
		String timeLimit = null;
		String callbackURL = null;
		String callid = null;

		if (dataObj != null) {
			if (dataObj.has(AllKeys.FROM_NUMBER)) {
				fromNumber = dataObj.getString(AllKeys.FROM_NUMBER);
				if (utils.isEmpty(fromNumber)) {
					response = utils.errorMessage("From Number is Empty!");
					return response;
				}
			} else {
				response = utils.errorMessage("From Number is Missing!");
				return response;
			}
			if (dataObj.has(AllKeys.TO_NUMBER)) {
				toNumber = dataObj.getString(AllKeys.TO_NUMBER);
				if (utils.isEmpty(fromNumber)) {
					response = utils.errorMessage("To Number is Empty!");
					return response;
				}
			} else {
				response = utils.errorMessage("To Number is Missing!");
				return response;
			}
			if (dataObj.has(AllKeys.TIME_LIMIT)) {
				timeLimit = dataObj.getString(AllKeys.TIME_LIMIT);

			}
			if (dataObj.containsKey(AllKeys.CALLBACK_URL)) {
				callbackURL = dataObj.getString(AllKeys.CALLBACK_URL);
				if (utils.isEmpty(fromNumber)) {
					response = utils.errorMessage("CallBack URL is Empty!");
					return response;
				}
			} else {
				response = utils.errorMessage("CallBack URL is Missing!");
				return response;
			}
			if (dataObj.has(AllKeys.CALLID)) {

				callid = dataObj.getString(AllKeys.CALLID);
				if (utils.isEmpty(fromNumber)) {
					response = utils.errorMessage("CallID is Empty!");
					return response;

				}
			} else {
				response = utils.errorMessage("CallID is Missing!");
				return response;
			}
			if (!utils.isEmpty(fromNumber) && !utils.isEmpty(toNumber)) {
				response = cd.insertCallDetails(fromNumber, toNumber, AllKeys.INITIATED, timeLimit, callbackURL, callid,
						dbCon);

			}
		} else {
			response = utils.invalidJsonError();
			return response;
		}
		return response;

	}

	public void initiateCall(Connection con) {
		String fromNumber = null;
		String toNumber = null;
		String timeLimit = null;
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
						if (utils.isEmpty(timeLimit)) {

							timeLimit = callLimit;
							logger.info("default time limit is " + timeLimit + "seconds");

						}

					} else {
						logger.info("default time limit is " + callLimit);
						timeLimit = callLimit;
						logger.info("time limit is " + timeLimit);
					}

					if (obj.has(AllKeys.TRIAL_COUNT)) {
						trialCount = obj.getInt(AllKeys.TRIAL_COUNT);
					}
					if (!utils.isEmpty(fromNumber) && !utils.isEmpty(toNumber)) {

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
							cd.updateCallDetailsOnceCallInitiate(id, null, "failed", null, trialCount + 1, con);
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

}
