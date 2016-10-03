package com.groupz.message;

import com.groupz.utils.RestUtils;

public class SmsMessage {
	private String toAddress = "";
	private String fromAddress = "";
	private String title = "";
	private String body = "";

	public String addAddress(String toNumber, String name, String fromNumber,
			String cc) {

		String toString = constructToXmlTagsForPerson(toNumber, name, cc);
		toAddress += "<to>";
		toAddress += toString;
		toAddress += "</to>";
		String toStr = getAddressAsXml(toAddress);

		this.fromAddress = "<from><name>" + "admin" + "</name><number>"
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

		msg += "<shorttext>" + RestUtils.encode(body) + "</shorttext>";

		return msg;
	}

	/*
	 * public String addFromAddress(String fromMail) {
	 * 
	 * this.fromAddress = "<from><name>" + "" + "</name><email>" +
	 * RestUtils.encode(fromMail) + "</email></from>"; return fromAddress; }
	 */

	private String constructToXmlTagsForPerson(String number, String name,
			String cc) {

		String ret = "";

		ret += "<name>" + "candidate" + "</name>";
		ret += "<contactpersonname>" + RestUtils.encode(name)
				+ "</contactpersonname>";
		ret += "<number>" + "+" + cc + "." + RestUtils.encode(number)
				+ "</number>";
		ret += "<email>" + "noreply@jobztop.in" + "</email>";
		ret += "<prefix>" + "mr." + "</prefix>";

		ret += "<contactpersonprefix>" + "mr." + "</contactpersonprefix>";

		return ret;
	}

	public String getAddressAsXml(String toAddress) {
		String address = "<tolist>" + toAddress + "</tolist>";

		address += fromAddress;
		return address;
	}
}
