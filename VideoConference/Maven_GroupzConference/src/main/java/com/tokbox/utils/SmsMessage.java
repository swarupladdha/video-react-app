package com.tokbox.utils;

import com.tokbox.utils.PropertiesUtil;
import com.tokbox.utils.RestUtils;

public class SmsMessage {
	private String toAddress = "";
	private String fromAddress = "";
	private String title = "";
	private String body = "";

	public String addAddress(String toNumber, String name, String fromNumber,
			String cc) {

		String toString = constructToXmlTagsForPerson(toNumber, name, cc);
		toAddress += PropertiesUtil.getProperty("to_open_tag");
		toAddress += toString;
		toAddress += PropertiesUtil.getProperty("to_close_tag");
		String toStr = getAddressAsXml(toAddress);

		this.fromAddress = PropertiesUtil.getProperty("from_open_tag")+PropertiesUtil.getProperty("name_open_tag") 
		+ "admin" + PropertiesUtil.getProperty("name_close_tag")+PropertiesUtil.getProperty("number_open_tag")
				+ "0000" + PropertiesUtil.getProperty("number_close_tag")+ PropertiesUtil.getProperty("from_close_tag");

		String Address = toStr + fromAddress;
		return Address;

	}
	
	public String addAddresswithDeviceId(String toNumber, String name, String fromNumber,
			String cc,String fcmId,String deviceType) {

		String toString = constructToXmlTagsForPersonWithDeviceId(toNumber, name, cc, fcmId, deviceType);
		toAddress += PropertiesUtil.getProperty("to_open_tag");
		toAddress += toString;
		toAddress += PropertiesUtil.getProperty("to_close_tag");
		String toStr = getAddressAsXml(toAddress);

		this.fromAddress = PropertiesUtil.getProperty("from_open_tag")+PropertiesUtil.getProperty("name_open_tag")
		+ "admin" +PropertiesUtil.getProperty("name_close_tag")+PropertiesUtil.getProperty("number_open_tag")
				+ "0000" + PropertiesUtil.getProperty("number_close_tag")+ PropertiesUtil.getProperty("from_close_tag");

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
		String msg = PropertiesUtil.getProperty("sid_open_tag") + sid + PropertiesUtil.getProperty("sid_close_tag");

		msg += PropertiesUtil.getProperty("short_text_open_tag") + RestUtils.encode(body) + PropertiesUtil.getProperty("short_text_close_tag");

		return msg;
	}
	
	private String constructToXmlTagsForPerson(String number, String name,
			String cc) {

		String ret = "";

		ret += PropertiesUtil.getProperty("name_open_tag") + "candidate" + PropertiesUtil.getProperty("name_close_tag");
		ret += PropertiesUtil.getProperty("contact_person_name_open_tag") + RestUtils.encode(name)
				+ PropertiesUtil.getProperty("contact_person_name_close_tag");
		ret += PropertiesUtil.getProperty("number_open_tag") + "+" + cc + "." + RestUtils.encode(number)
				+ PropertiesUtil.getProperty("number_close_tag");
		ret += PropertiesUtil.getProperty("email_open_tag") + "noreply@jobztop.in" + PropertiesUtil.getProperty("email_close_tag");
		ret += PropertiesUtil.getProperty("prefix_open_tag") + "mr." + PropertiesUtil.getProperty("prefix_close_tag");

		ret += PropertiesUtil.getProperty("contact_person_prefix_open_tag") + "mr." + PropertiesUtil.getProperty("conatct_person_prefix_close_tag");

		return ret;
	}
	
	private String constructToXmlTagsForPersonWithDeviceId(String number, String name,
			String cc,String fcmId, String deviceType) {

		String ret = "";

		ret += PropertiesUtil.getProperty("name_open_tag") + "candidate" + PropertiesUtil.getProperty("name_close_tag");
		ret += PropertiesUtil.getProperty("contact_person_name_open_tag") + RestUtils.encode(name)
		+ PropertiesUtil.getProperty("contact_person_name_close_tag");
		ret += PropertiesUtil.getProperty("number_open_tag") + "+" + cc + "." + RestUtils.encode(number)
		+ PropertiesUtil.getProperty("number_close_tag");
		ret += PropertiesUtil.getProperty("fcmid_open_tag") + fcmId +PropertiesUtil.getProperty("fcmid_close_tag");
		ret += PropertiesUtil.getProperty("devicetype_open_tag") +deviceType +PropertiesUtil.getProperty("devicetype_close_tag");
		ret += PropertiesUtil.getProperty("serverkey_open_tag") + PropertiesUtil.getProperty("serverKey")+PropertiesUtil.getProperty("serverkey_close_tag");
		ret += PropertiesUtil.getProperty("email_open_tag") + "noreply@jobztop.in" + PropertiesUtil.getProperty("email_close_tag");
		ret += PropertiesUtil.getProperty("prefix_open_tag") + "mr." + PropertiesUtil.getProperty("prefix_close_tag");

		ret += PropertiesUtil.getProperty("contact_person_prefix_open_tag") + "mr." + PropertiesUtil.getProperty("conatct_person_prefix_close_tag");

		return ret;
	}

	public String getAddressAsXml(String toAddress) {
		String address = PropertiesUtil.getProperty("to_list_open_tag") + toAddress + PropertiesUtil.getProperty("to_list_close_tag");

		address += fromAddress;
		return address;
	}
}
