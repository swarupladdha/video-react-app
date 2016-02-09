package com.groupz.message;

import com.groupz.utils.RestUtils;

public class EmailMessage {
	private String toAddress = "";

	private String fromAddress = "";
	private String title = "";
	private String body = "";

	public String addAddress(String toEmail, String name, String fromMail) {

		String toString = constructToXmlTagsForPerson(toEmail, name);
		toAddress += "<to>";
		toAddress += toString;
		toAddress += "</to>";
		String toStr = getAddressAsXml(toAddress);

		this.fromAddress = "<from><name>" + "" + "</name><email>"
				+ RestUtils.encode(fromMail) + "</email></from>";

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
		String disclaimerText = "";

		String msg = "<subject>" + "" + "</subject>";
		msg += "<body>" + RestUtils.encode(body + disclaimerText) + "</body>";

		msg += "<type>html</type>";
		return msg;
	}

	/*
	 * public String addFromAddress(String fromMail) {
	 * 
	 * this.fromAddress = "<from><name>" + "" + "</name><email>" +
	 * RestUtils.encode(fromMail) + "</email></from>"; return fromAddress; }
	 */

	private String constructToXmlTagsForPerson(String email, String name) {

		String ret = "";
		ret += "<contactpersonname>" + RestUtils.encode(name)
				+ "</contactpersonname>";
		ret += "<name>" + "" + "</name><email>" + RestUtils.encode(email)
				+ "</email>";
		ret += "<number>" + "" + "</number>";
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
