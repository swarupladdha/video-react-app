package com.groupz.operations;

import com.gpzhibernate.DBCommonOpertion;
import com.groupz.tables.Otp;

public class OTPOperations {

	public Otp checkmobileExists(String countryCode, String mobileNo, String otp) {

		Otp canid = (Otp) DBCommonOpertion.getSingleDatabaseObject(Otp.class,
				"countrycode='" + countryCode + "'  and Mobile='" + mobileNo + "'and Otp='" + otp + "' order by id desc");
		if (canid != null) {

			return canid;

		} else
			return null;

	}

}
