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
			// Json check
			DBConnect dbConnect = new DBConnect();
			System.out.println("Trying to connect hiberate in jobztop");
			dbConnect.establishConnection();
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

					if (countrycode.equalsIgnoreCase("91") == true) {
						otpActions.setCountrycode(utils.encrypt(countrycode));
						String genOtp = utils.generateOTP();
						System.out.println("One Time Password is: " + genOtp);
						otpActions.setOtp(utils.encrypt(genOtp));

						otpActions.setCreatedTime(utils.getLastSynchTime());
						Calendar cal = Calendar.getInstance();
						cal.setTime(utils.getLastSynchTime());
						cal.add(Calendar.MINUTE, 30);
						// Date date1 = otpop.StringDateToDate(date);
						System.out.println("proper format :"
								+ utils.getLastSynchTime());
						Date lapps_time = cal.getTime();
						otpActions.setLapsetime(lapps_time);
						System.out.println("OTP trying to save");
						try {
							System.out.println("OTP SAVED IN TRY BLOCK");
							otpActions.save();
						} catch (HibernateException ef) {
							System.out
									.println("OTP EXCEPTION IN HIBERNATE CATCH");
							ef.printStackTrace();
						} catch (Exception e) {
							System.out.println("OTP EXCEPTION IN FINAL CATCH");
							e.printStackTrace();
						}
						EmailAndSmsManager sms = new EmailAndSmsManager();
						sms.sendSms(genOtp, mobile);
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
			} else {
				response = utils.processError(
						PropertiesUtil.getProperty("mobile_invalid_code"),
						PropertiesUtil.getProperty("mobile_invalid_message"));
				return response;
			}
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
			DBConnect dbConnect = new DBConnect();
			System.out.println("Trying to connect hiberate in jobztop");
			dbConnect.establishConnection();
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
			if (isExist != null) {

				if (utils.getLastSynchTime().before(isExist.getLapsetime())) {
					response = utils.processSucess("otp", true);
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
