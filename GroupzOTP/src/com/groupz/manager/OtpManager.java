package com.groupz.manager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.groupz.tables.Otp;
import com.groupz.utils.PropertiesUtil;
import com.groupz.utils.RestUtils;

import net.sf.json.JSONObject;

public class OtpManager {

	RestUtils utils = new RestUtils();
	String response = "";

	public String generateOTPResponse(String req) {
		String response = "";

		try {
			System.out.println("1");

			// json check
			JSONObject jsonreq = new JSONObject();
			jsonreq = JSONObject.fromObject(req);
			System.out.println("json request is :" + jsonreq);
			System.out.println("2");

			Otp otp = new Otp();
			String mobile = jsonreq.getJSONObject("json").getJSONObject("request").getString("mobile");
			String countrycode = jsonreq.getJSONObject("json").getJSONObject("request").getString("countrycode");
			System.out.println("3");


			if (utils.isEmpty(mobile) == false && utils.isEmpty(countrycode) == false) {
				if ((utils.isNumber(mobile) == true) && (utils.isNumber(countrycode) == true)) {
					otp.setMobile(utils.encrypt(mobile));
					System.out.println("4");

					if (countrycode.equalsIgnoreCase("91") == true) {
						otp.setCountrycode(utils.encrypt(countrycode));
						String genOtp = utils.generateOTP();

						System.out.println("One Time Password is: " + genOtp);
						otp.setOtp(utils.encrypt(genOtp));

						// End of GenerateOTP
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date date = new Date();
						System.out.println(dateFormat.format(date));
						otp.setTime(date);
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						cal.add(Calendar.MINUTE, 30);
						// Date date1 = otpop.StringDateToDate(date);
						System.out.println("proper format :" + date);
						Date lapps_time = cal.getTime();
						otp.setLapstime(lapps_time);
						otp.save();
						response = utils.processSucess("otp",genOtp );
						return response;
					} else {
						response = utils.processError(PropertiesUtil.getProperty("countrycode_invalid_code"),
								PropertiesUtil.getProperty("countrycode_invalid_message"));
						return response;
					}
				} else {
					// not an number
					response = utils.processError(PropertiesUtil.getProperty("mobile_invalid_code"),
							PropertiesUtil.getProperty("mobile_invalid_message"));

					return response;
				}
			} else {
				response = utils.processError(PropertiesUtil.getProperty("mobile_invalid_code"),
						PropertiesUtil.getProperty("mobile_invalid_message"));

				return response;
			}
		} catch (Exception e)

		{
			e.printStackTrace();

		}
		return response;
	}
	
	// create function validateOTP

}
