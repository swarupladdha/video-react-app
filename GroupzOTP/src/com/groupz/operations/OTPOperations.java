package com.groupz.operations;


import com.gpzhibernate.DBCommonOpertion;
import com.jobztop.tables.Otp;



public class OTPOperations {

	public Otp checkmobileExists(String countryCode, String mobileNo, String otp) {

		Otp otpValue = (Otp) DBCommonOpertion.getSingleDatabaseObject(Otp.class,
				"countrycode='" + countryCode + "'  and Mobile='" + mobileNo + "'and Otp='" + otp + "' order by id desc");
		if (otpValue != null) {

			return otpValue;

		} else
			return null;

	}

}
