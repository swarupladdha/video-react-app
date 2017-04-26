package manager;

import java.sql.Connection;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import operations.DBOperations;
import utils.PropertiesUtil;

public class SmsManager {
	utils.RestUtils utils = new utils.RestUtils();
	DBOperations dbo = new DBOperations();

	private String toAddress = "";
	private String fromAddress = "";
	private String title = "";
	private String body = "";

	public String addAddress(String toNumber, String name, String fromNumber,
			String contactPerName, String email, String prefix, String fromName) {

		String toString = constructToXmlTagsForPerson(name, toNumber,
				contactPerName, email, prefix);
		toAddress += "<to>";
		toAddress += toString;
		toAddress += "</to>";
		String toStr = getAddressAsXml(toAddress);

		this.fromAddress = "<from><name>" + fromName + "</name><number>"
				+ "0000" + "</number></from>";

		String Address = toStr + fromAddress;
		return Address;

	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage(String body, String sid) {
		String msg = "<sid>" + sid + "</sid>";
		msg += "<shorttext>" + utils.encode(body) + "</shorttext>";

		return msg;
	}

	private String constructToXmlTagsForPerson(String name, String tonumber,
			String contactPersonName, String email, String prefix) {

		String ret = "";
		ret += "<name>" + "User" + "</name><contactpersonname>"
				+ utils.encode(contactPersonName)
				+ "</contactpersonname><number>" + utils.encode(tonumber)
				+ "</number><email>" + email + "</email>";
		ret += "<prefix>" + prefix + "</prefix>";
		ret += "<contactpersonprefix>" + prefix + "</contactpersonprefix>";

		return ret;
	}

	public String getAddressAsXml(String toAddress) {
		String address = "<tolist>" + toAddress + "</tolist>";

		address += fromAddress;
		return address;
	}

	public void sendSms(String text, String toAddress, String fromAddress,
			Connection con) {

		String insertIntoMessagesInTableSQL = "Insert Into messagesintable (Address,CustomData,Date,Message,MsgType,Time) values('%s','%s','%s','%s',%d,'%s')";

		String customData = "0,0";
		Date date = new Date();
		Time time = new Time(date.getTime());

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String now = utils.getFormattedDateStr(cal.getTime());
		System.out.println(now);

		int messageType = 1;
		String toAdd = addAddress(toAddress, "uesr",
				PropertiesUtil.getProperty("from_number_for_sms_alert"),
				"user", PropertiesUtil.getProperty("from_email_for_sms_alert"),
				"Mr.", PropertiesUtil.getProperty("from_name_for_sms_alert"));

		String body = getMessage(text,
				PropertiesUtil.getProperty("sid_for_sending_alert"));

		String insertIntoMessagesInTableQuery = insertIntoMessagesInTableSQL
				.format(insertIntoMessagesInTableSQL, toAdd, customData, now,
						body, messageType, time);

		dbo.executeUpdate(insertIntoMessagesInTableQuery, con);

	}
}
