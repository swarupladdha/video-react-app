package com.groupz.message;

import com.groupz.utils.RestUtils;

public class SmsMessage {
	private String toAddress = "";
	private String fromAddress = "";
	private String title = "";
	private String body = "";

	public String addAddress(String toNumber, String name, String fromNumber) {

		String toString = constructToXmlTagsForPerson(toNumber, name);
		toAddress += "<to>";
		toAddress += toString;
		toAddress += "</to>";
		String toStr = getAddressAsXml(toAddress);

		this.fromAddress = "<from><name>" + "" + "</name><number>" + ""
				+ "</number></from>";

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

	public String getMessage(String body) {

		String msg = "<shorttext>" + RestUtils.encode(body) + "</shorttext>";

		return msg;
	}

	/*
	 * public String addFromAddress(String fromMail) {
	 * 
	 * this.fromAddress = "<from><name>" + "" + "</name><email>" +
	 * RestUtils.encode(fromMail) + "</email></from>"; return fromAddress; }
	 */

	private String constructToXmlTagsForPerson(String number, String name) {

		String ret = "";
		ret += "<contactpersonname>" + RestUtils.encode(name)
				+ "</contactpersonname>";
		ret += "<name>" + "" + "</name><email>" + "" + "</email>";
		ret += "<number>" + RestUtils.encode(number) + "</number>";
		ret += "<prefix>" + "" + "</prefix>";

		ret += "<contactpersonprefix>" + "" + "</contactpersonprefix>";

		return ret;
	}

	public String getAddressAsXml(String toAddress) {
		String address = "<tolist>" + toAddress + "</tolist>";

		address += fromAddress;
		return address;
	}
}
