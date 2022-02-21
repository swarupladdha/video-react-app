package com.groupz.manager;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.HibernateException;

import net.sf.json.JSONObject;

import com.groupz.message.EmailAndSmsManager;
import com.groupz.operations.OTPOperations;
import com.groupz.utils.PropertiesUtil;
import com.groupz.utils.RestUtils;
import com.jobztop.tables.Otp;
import com.jobztop.utils.DBConnect;

public class OtpManager {
	OTPOperations oppop = new OTPOperations();
	RestUtils utils = new RestUtils();
	String response = "";

	public String generateOTPResponse(String req) {
		String response = "";

		try {
			System.out.println("Trying to connect hiberate in jobztop");
			DBConnect.establishConnection();
			System.out.println("Connected to jobztop db");
			JSONObject jsonreq = new JSONObject();
			jsonreq = JSONObject.fromObject(req);
			System.out.println("json request is :" + jsonreq);

			Otp otpActions = new Otp();
			String mobile = jsonreq.getJSONObject("json")
					.getJSONObject("request").getJSONObject("mobile")
					.getString("mobilenumber");
			String countrycode = jsonreq.getJSONObject("json")
					.getJSONObject("request").getJSONObject("mobile")
					.getString("countrycode");

			if (utils.isEmpty(mobile) == false
					&& utils.isEmpty(countrycode) == false) {
				if ((utils.isNumber(mobile) == true)
						&& (utils.isNumber(countrycode) == true)) {
					otpActions.setMobile(utils.encrypt(mobile));
					// otpActions.setMobile(mobile);

//					if (countrycode.equalsIgnoreCase("91") == true
//							|| countrycode.equalsIgnoreCase("1") == true) {
						otpActions.setCountrycode(utils.encrypt(countrycode));
						// otpActions.setCountrycode(countrycode);
						String genOtp = utils.generateOTP();
						otpActions.setOtp(utils.encrypt(genOtp));
						// otpActions.setOtp(genOtp);
						otpActions.setCreatedTime(utils.getLastSynchTime());
						Calendar cal = Calendar.getInstance();
						cal.setTime(utils.getLastSynchTime());
						cal.add(Calendar.MINUTE, 30);
						Date lapps_time = cal.getTime();
						otpActions.setLapsetime(lapps_time);

						try {

							otpActions.save();
						} catch (HibernateException ef) {

							ef.printStackTrace();
						} catch (Exception e) {

							e.printStackTrace();
						}
						EmailAndSmsManager sms = new EmailAndSmsManager();
						sms.sendSms(genOtp, mobile,countrycode);
						response = utils.processSucess("otp", genOtp);
						return response;
					} else {
						response = utils
								.processError(
										PropertiesUtil
												.getProperty("countrycode_invalid_code"),
										PropertiesUtil
												.getProperty("countrycode_invalid_message"));
						return response;
					}
				} else {
					response = utils.processError(PropertiesUtil
							.getProperty("mobile_invalid_code"), PropertiesUtil
							.getProperty("mobile_invalid_message"));
					return response;
				}
//			} else {
//				response = utils.processError(
//						PropertiesUtil.getProperty("mobile_invalid_code"),
//						PropertiesUtil.getProperty("mobile_invalid_message"));
//				return response;
//			}
		} catch (Exception e)

		{
			e.printStackTrace();
			response = utils.processError(
					PropertiesUtil.getProperty("Json_invalid_code"),
					PropertiesUtil.getProperty("Json_invalid_message"));
			return response;

		}
		// return response;
	}

	// create function validateOTP
	public String validateOTP(String request) {
		String response = "";
		try {

			DBConnect.establishConnection();
			JSONObject jsonreq = new JSONObject();
			jsonreq = JSONObject.fromObject(request);

			System.out.println("json request is :" + jsonreq);
			// JSONObject dataObj = jsonreq.getJSONObject("data");
			String countryCode = jsonreq.getJSONObject("json")
					.getJSONObject("request").getJSONObject("mobile")
					.getString("countrycode");

			String mobileNumber = jsonreq.getJSONObject("json")
					.getJSONObject("request").getJSONObject("mobile")
					.getString("mobilenumber");

			String otp = jsonreq.getJSONObject("json").getJSONObject("request")
					.getString("otp");

			Otp isExist = oppop.checkmobileExists(utils.encrypt(countryCode),
					utils.encrypt(mobileNumber), utils.encrypt(otp));

			// Otp isExist = oppop.checkmobileExists(countryCode, mobileNumber,
			// otp);
			if (isExist != null) {
				System.out.println("OTP matches");

				if (utils.getLastSynchTime().before(isExist.getLapsetime())) {
					response = utils.processSucess("otp", true);
					System.out.println("OTP matches with in time ");
					System.err.println("OTP sent successfully");
					return response;
				} else {
					response = utils.processError(
							PropertiesUtil.getProperty("otp_invalid_code"),
							PropertiesUtil.getProperty("otp_invalid_message"));
					return response;
				}

				// return response;
			} else {
				response = utils.processError(
						PropertiesUtil.getProperty("otp_invalid_code"),
						PropertiesUtil.getProperty("otp_invalid_message"));
				return response;
			}

		} catch (Exception e)

		{
			e.printStackTrace();
			e.printStackTrace();
			response = utils.processError(
					PropertiesUtil.getProperty("Json_invalid_code"),
					PropertiesUtil.getProperty("Json_invalid_message"));
			return response;

		}
	}

}
