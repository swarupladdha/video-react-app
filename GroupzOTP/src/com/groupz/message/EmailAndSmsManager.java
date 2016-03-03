package com.groupz.message;

import java.sql.Time;
import java.util.Date;

import com.jobztop.tables.MessagesinTable;

public class EmailAndSmsManager {

	public void sendEmail(String Message, String toAddress) {
		MessagesinTable mit = new MessagesinTable();
		EmailMessage message = new EmailMessage();
		String toAdd = message.addAddress(toAddress, "", "support@groupz.in");
		String body = message.getMessage(Message);
		mit.setAddress(toAdd);
		mit.setMessage(body);
		mit.setMsgType(0);
		Date now = new Date();
		Time time = new Time(now.getTime());
		mit.setDate(now);
		mit.setTime(time);
		mit.save();

	}

	public void sendSms(String Message, String toAddress) {
		String fromNumber = "12345";
		String toName = "";
		String toMessage = "Your one time password is " + Message;
		MessagesinTable mit = new MessagesinTable();
		SmsMessage sms = new SmsMessage();
		String toAdd = sms.addAddress(toAddress, toName, fromNumber);
		String body = sms.getMessage(toMessage);
		mit.setAddress(toAdd);
		mit.setMessage(body);
		mit.setMsgType(1);
		mit.setCustomData("0,0");
		Date now = new Date();
		Time time = new Time(now.getTime());
		mit.setDate(now);
		mit.setTime(time);
		mit.save();

	}

}
