package com.groupz.sendsms;

public class GenerateSmsIds {

	public static String generateJobId(String provider) throws Exception {
		String jobiderr = null;
		try {
			long timeNow = System.currentTimeMillis();
			jobiderr = provider + timeNow;

		} catch (Exception e) {

			throw e;

		}
		return jobiderr;
	}

	public static String generateGroupzMsgId() throws Exception {

		String gmsgid = null;

		try {

			long timeNow = System.currentTimeMillis();
			gmsgid = String.valueOf(timeNow);

			System.out.print(gmsgid);
		} catch (Exception e) {

			throw e;

		}

		return gmsgid;

	}

}
