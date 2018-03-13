package manager;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;
import operations.DBOperations;
import utils.PropertiesUtil;
import utils.RestUtils;

public class OTPManager {
	RestUtils utils = new RestUtils();
	DBOperations dbo = new DBOperations();
	SmsManager sms = new SmsManager();

	Connection dbConnection = null;
	ConnectionPooling connectionPooling = ConnectionPooling.getInstance();

	public String generateOtp(String otpRequest) {
		String generateOtpResponse = "";
		String mobile = "";
		String encryptedMobile = "";
		String countryCode = "";
		String encryptedCountryCode = "";
		String mobileWithCountryCode = "";
		boolean newOtp = false;
		String otp = "";
		String insertIntoOtpTableSQL = "Insert into Otp (Mobile,CountryCode,CreatedTime,LapseTime,Otp,OriginalOtp) values('%s','%s','%s','%s','%s','%s')";
		String updateDuplicateEntrySQL = "Update Otp set Invalid=true where Id=%d ";
		String checkMobileExist = "Select Id from Otp where Mobile='%s' and CountryCode='%s' and LapseTime>='%s'";

		try {
			if (utils.isJSONValid(otpRequest) == false) {
				generateOtpResponse = utils.processError(
						PropertiesUtil.getProperty("json_invalid_code"),
						PropertiesUtil.getProperty("json_invalid_message"));
				return generateOtpResponse;

			}
			JSONObject jsonReq = new JSONObject();
			jsonReq = JSONObject.fromObject(otpRequest);

			JSONObject mobileObject = jsonReq.getJSONObject("json")
					.getJSONObject("request").getJSONObject("mobile");

			if (mobileObject.containsKey("countrycode")) {
				countryCode = mobileObject.getString("countrycode");
				if (utils.isEmpty(countryCode) == true) {

					generateOtpResponse = utils.processError(PropertiesUtil
							.getProperty("empty_countrycode_code"),
							PropertiesUtil
									.getProperty("empty_countrycode_message"));
					return generateOtpResponse;

				}
				encryptedCountryCode = utils.encrypt(countryCode);
			} else {
				generateOtpResponse = utils.processError(PropertiesUtil
						.getProperty("no_countrycode_tag_code"), PropertiesUtil
						.getProperty("no_countrycode_tag_message"));
				return generateOtpResponse;

			}
			if (mobileObject.containsKey("mobilenumber")) {
				mobile = mobileObject.getString("mobilenumber");
				if (utils.isEmpty(mobile) == true) {
					generateOtpResponse = utils.processError(
							PropertiesUtil.getProperty("empty_mobile_code"),
							PropertiesUtil.getProperty("empty_mobile_message"));
					return generateOtpResponse;
				}
				encryptedMobile = utils.encrypt(mobile);
				mobileWithCountryCode = "+" + countryCode + "." + mobile;

			} else {
				generateOtpResponse = utils.processError(
						PropertiesUtil.getProperty("no_mobile_tag_code"),
						PropertiesUtil.getProperty("no_mobile_tag_code"));
				return generateOtpResponse;

			}

			int count = 0;
			for (int i = 0, len = mobile.length(); i < len; i++) {
				if (Character.isDigit(mobile.charAt(i))) {
					count++;
				}

			}

			if (count < 10) {
				generateOtpResponse = utils.processError(
						PropertiesUtil.getProperty("mobile_invalid_code"),
						PropertiesUtil.getProperty("mobile_invalid_message"));
				return generateOtpResponse;

			}

			if (mobileObject.containsKey("newotp")) {
				newOtp = mobileObject.getBoolean("newotp");
			}

			if (countryCode.equalsIgnoreCase("91") == true
					|| countryCode.equalsIgnoreCase("1") == true) {
				dbConnection = connectionPooling.getConnection();
				String currentTime = utils.getFormattedDateStr(utils
						.getLastSynchTime());
				if (newOtp == true) {
					String checkMobileExistQuery = checkMobileExist.format(
							checkMobileExist, encryptedMobile,
							encryptedCountryCode, currentTime);
					System.out.println(" Mobile Exist Query :"
							+ checkMobileExistQuery);
					List<Integer> otpEntryList = dbo.getIntegerList(
							dbConnection, checkMobileExistQuery);
					if (otpEntryList != null && otpEntryList.size() > 0) {
						for (int j = 0; j < otpEntryList.size(); j++) {
							String updateOtpEntry = updateDuplicateEntrySQL
									.format(updateDuplicateEntrySQL,
											otpEntryList.get(j));
							dbo.executeUpdate(updateOtpEntry, dbConnection);
						}

					}

					Calendar cal = Calendar.getInstance();
					cal.setTime(utils.getLastSynchTime());
					cal.add(Calendar.MINUTE, 30);
					Date lapse_time = cal.getTime();
					String lapseTime = utils.getFormattedDateStr(lapse_time);

					otp = utils.generateOTP();
					String encryptedOtp = utils.encrypt(otp);

					String insertIntoOtpQuery = insertIntoOtpTableSQL.format(
							insertIntoOtpTableSQL, encryptedMobile,
							encryptedCountryCode, currentTime, lapseTime,
							encryptedOtp, otp);
					System.out.println(insertIntoOtpQuery);
					dbo.executeUpdate(insertIntoOtpQuery, dbConnection);
				} else {
					String query = "Select OriginalOtp from Otp where Mobile='"
							+ encryptedMobile + "' and CountryCode='"
							+ encryptedCountryCode + "' and LapseTime>='"
							+ currentTime + "' ORDER BY id DESC LIMIT 1";
					System.out.println("Query for getting old otp :" + query);
					otp = dbo.getOldOTP(query, dbConnection);
					if (otp == null) {
						otp = utils.generateOTP();

						Calendar cal = Calendar.getInstance();
						cal.setTime(utils.getLastSynchTime());
						cal.add(Calendar.MINUTE, 30);
						Date lapse_time = cal.getTime();
						String lapseTime = utils
								.getFormattedDateStr(lapse_time);

						otp = utils.generateOTP();
						String encryptedOtp = utils.encrypt(otp);

						String insertIntoOtpQuery = insertIntoOtpTableSQL
								.format(insertIntoOtpTableSQL, encryptedMobile,
										encryptedCountryCode, currentTime,
										lapseTime, encryptedOtp, otp);
						System.out.println(insertIntoOtpQuery);
						dbo.executeUpdate(insertIntoOtpQuery, dbConnection);

					}

				}

				String sms_text = PropertiesUtil.getProperty("otp_sms_text")
						+ " " + otp;
				System.out.println("sms text :" + sms_text);

				sms.sendSms(
						sms_text,
						mobileWithCountryCode,
						PropertiesUtil.getProperty("from_number_for_sms_alert"),
						dbConnection);

				generateOtpResponse = utils.processSuccess();
				return generateOtpResponse;

			}

		} catch (Exception e) {
			e.printStackTrace();
			generateOtpResponse = utils.processError(
					PropertiesUtil.getProperty("common_error_code"),
					PropertiesUtil.getProperty("common_error_message"));
			return generateOtpResponse;
		} finally {
			try {
				if (dbConnection != null)
					connectionPooling.close(dbConnection);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return generateOtpResponse;

	}

	public String validateOtp(String otpRequest) {
		String validateOtpResponse = "";
		String countryCode = "";
		String mobile = "";
		String otp = "";
		String encryptedOtp = "";

		String validateOtpSQL = "Select Id from Otp where Mobile='%s' and CountryCode='%s' and Otp='%s' and Invalid=false and LapseTime>='%s'";
		try {

			if (utils.isJSONValid(otpRequest) == false) {
				validateOtpResponse = utils.processError(
						PropertiesUtil.getProperty("json_invalid_code"),
						PropertiesUtil.getProperty("json_invalid_message"));
				return validateOtpResponse;

			}
			JSONObject jsonReq = new JSONObject();
			jsonReq = JSONObject.fromObject(otpRequest);

			JSONObject mobileObject = jsonReq.getJSONObject("json")
					.getJSONObject("request").getJSONObject("mobile");

			if (mobileObject.containsKey("countrycode")) {
				countryCode = mobileObject.getString("countrycode");
				if (utils.isEmpty(countryCode) == true) {

					validateOtpResponse = utils.processError(PropertiesUtil
							.getProperty("empty_countrycode_code"),
							PropertiesUtil
									.getProperty("empty_countrycode_message"));
					return validateOtpResponse;

				}
			} else {
				validateOtpResponse = utils.processError(PropertiesUtil
						.getProperty("no_countrycode_tag_code"), PropertiesUtil
						.getProperty("no_countrycode_tag_message"));
				return validateOtpResponse;

			}
			if (mobileObject.containsKey("mobilenumber")) {

				mobile = mobileObject.getString("mobilenumber");
				if (utils.isEmpty(mobile) == true) {
					validateOtpResponse = utils.processError(
							PropertiesUtil.getProperty("empty_mobile_code"),
							PropertiesUtil.getProperty("empty_mobile_message"));
					return validateOtpResponse;
				}
			} else {
				validateOtpResponse = utils.processError(
						PropertiesUtil.getProperty("no_mobile_tag_code"),
						PropertiesUtil.getProperty("no_mobile_tag_code"));
				return validateOtpResponse;

			}

			if (jsonReq.getJSONObject("json").getJSONObject("request")
					.containsKey("otp")) {
				otp = jsonReq.getJSONObject("json").getJSONObject("request")
						.getString("otp");
				encryptedOtp = utils.encrypt(otp);

			} else {
				validateOtpResponse = utils.processError(
						PropertiesUtil.getProperty("no_otptag_code"),
						PropertiesUtil.getProperty("no_otptag_message"));
				return validateOtpResponse;
			}

			int count = 0;
			for (int i = 0, len = mobile.length(); i < len; i++) {
				if (Character.isDigit(mobile.charAt(i))) {
					count++;
				}

			}

			if (count < 10) {
				validateOtpResponse = utils.processError(
						PropertiesUtil.getProperty("mobile_invalid_code"),
						PropertiesUtil.getProperty("mobile_invalid_message"));
				return validateOtpResponse;

			}

			if (countryCode.equalsIgnoreCase("91") == true
					|| countryCode.equalsIgnoreCase("1") == true) {

				dbConnection = connectionPooling.getConnection();

				String mobileEncrypted = utils.encrypt(mobile);
				String countryCodeEnrypted = utils.encrypt(countryCode);

				String CurrentTime = utils.getFormattedDateStr(utils
						.getLastSynchTime());

				String validateOtpQuery = validateOtpSQL.format(validateOtpSQL,
						mobileEncrypted, countryCodeEnrypted, encryptedOtp,
						CurrentTime);

				List<Integer> validateOtp = dbo.getIntegerList(dbConnection,
						validateOtpQuery);

				if (validateOtp == null || validateOtp.size() == 0) {
					validateOtpResponse = utils.processError(
							PropertiesUtil.getProperty("invalid_otp_code"),
							PropertiesUtil.getProperty("invalid_otp_message"));
					return validateOtpResponse;

				}

				validateOtpResponse = utils.processSuccess();
				return validateOtpResponse;

			} else {

				validateOtpResponse = utils.processError(PropertiesUtil
						.getProperty("invalid_countrycode_code"),
						PropertiesUtil
								.getProperty("invalid_countrycode_message"));
				return validateOtpResponse;

			}

		} catch (Exception e) {
			e.printStackTrace();
			validateOtpResponse = utils.processError(
					PropertiesUtil.getProperty("common_error_code"),
					PropertiesUtil.getProperty("common_error_message"));
			return validateOtpResponse;
		} finally {
			try {
				if (dbConnection != null)
					connectionPooling.close(dbConnection);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
