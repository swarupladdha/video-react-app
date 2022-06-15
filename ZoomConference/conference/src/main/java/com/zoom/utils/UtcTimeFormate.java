package com.zoom.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class UtcTimeFormate {
	public String getUtcTimeNow(Date d) throws ParseException {
		String testDate = "2022-10-12 12:23:23";
		final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss z";
		final Date date = new SimpleDateFormat(ISO_FORMAT).parse(testDate);
		SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
		final TimeZone utc = TimeZone.getTimeZone("UTC");
		sdf.setTimeZone(utc);
		System.out.println(sdf.format(date));
		return sdf.format(date);
	}
}
