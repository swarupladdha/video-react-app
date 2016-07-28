package com.groupz.message;

import java.io.FileInputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Statement;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.groupz.followup.utils.ConnectionUtils;

public class Message {

	private String toName;
	private String toContactName;
	private String namePrefix;
	private String contactPrefix;
	private String mobNumber;
	private String email;
	private String fromEmail;
	private String fromMobile;
	private String senderId;
	private String groupzName;
	private int memberId;
	private String groupzCode;

	static final Logger logger = Logger.getLogger(Message.class);

	 String messageInsertSQL = "insert into messagesintable (customdata, date, provider, time, msgtype, address, message) values "
			+ "( '0,0' , now(), '%s', now(), '%s',  '%s', '%s' )";

	String encode(String s) {
		if (s == null)
			return null;
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(s);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '<') {
				result.append("&lt;");
			} else if (character == '>') {
				result.append("&gt;");
			} else if (character == '\"') {
				result.append("&quot;");
			} else if (character == '\'') {
				result.append("&#039;");
			} else if (character == '&') {
				result.append("&amp;");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	public boolean sendMessage(Connection c, String providerCode,
			String username, String passwd, String smsText, String emailText) {
		StringBuffer address = new StringBuffer("<tolist>");
		address.append("<to>");
		address.append("<name>" + toName + "</name>");
		address.append("<contactpersonname>" + toContactName
				+ "</contactpersonname>");
		address.append("<number>" + mobNumber + "</number>");
		address.append("<email>" + email + "</email>");
		address.append("<prefix>" + namePrefix + "</prefix>");
		address.append("<contactpersonprefix>" + contactPrefix
				+ "</contactpersonprefix>");
		address.append("</to>");
		address.append("</tolist>");
		address.append("<from>");
		address.append("<name>" + groupzName + "</name>");
		address.append("<number>" + fromMobile + "</number>");
		address.append("<email>" + fromEmail + "</email>");
		address.append("</from>");
		StringBuffer smsMessage = new StringBuffer();

		if (providerCode != null && providerCode.trim().isEmpty() == false) {
			smsMessage.append("<sid>" + senderId + "</sid>");
			smsMessage.append("<shorttext>" + encode(smsText) + "</shorttext>");
			smsMessage.append("<provider>");
			smsMessage.append("<code>" + providerCode + "</code>");
			smsMessage.append("<username>" + username + "</username>");
			smsMessage.append("<password>" + passwd + "</password>");
			smsMessage.append("</provider>");
		}
		try {
			String insertSql = String.format(messageInsertSQL, providerCode,
					"1", address, smsMessage.toString());
			Statement stmt = c.createStatement();
			stmt.executeUpdate(insertSql);
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean sendFollowupinURL(int gbFollowupId, String groupzCode,
			int memberId) {
		boolean followUpStatus = false;
		try {
			String fileName = System.getenv("FE_CONFIG_FILE");
			if (fileName == null) {
				logger.debug("Env. Variable FE is not set, using default file vinralerts.properties");
				fileName = "conf/db.properties";
			}

			Properties p = new Properties(System.getProperties());
			FileInputStream propFile = new FileInputStream(fileName);
			p.load(propFile);
			JSONObject jsonObj = new JSONObject();
			JSONObject reqObj = new JSONObject();
			JSONObject dataObj = new JSONObject();
			dataObj.put("servicetype", "21");
			dataObj.put("functiontype", "2027");
			dataObj.put("groupzcode", groupzCode);
			dataObj.put("memberid", memberId);
			dataObj.put("followupid", gbFollowupId);
			reqObj.put("request", dataObj);
			jsonObj.put("json", reqObj);
			System.out.println("Final JSON:" + jsonObj.toString());
			String followUpURL = p.getProperty("contactfollowup_URL")
					+ URLEncoder.encode(jsonObj.toString());
			logger.debug("Followup URL:" + followUpURL);
			ConnectionUtils connectionUtils = new ConnectionUtils();
			String followUpResponse = connectionUtils
					.ConnectandRecieve(followUpURL);
			if (followUpResponse == null || followUpResponse.length() == 0) {
				followUpStatus = false;
			} else {
				try {
					JSONObject respJSON = JSONObject
							.fromObject(followUpResponse);
					System.out.println("Response JSON:"+respJSON.toString());
					logger.debug("Status code:"
							+ respJSON.getJSONObject("json")
									.getJSONObject("response")
									.getString("statuscode"));
					logger.debug("Status code:"
							+ respJSON.getJSONObject("json")
									.getJSONObject("response")
									.getString("statusmessage"));
					if (respJSON.getJSONObject("json").getJSONObject("response")
							.getString("statuscode").equalsIgnoreCase("0") == true) {
						followUpStatus = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
					followUpStatus = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return followUpStatus;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	public String getToContactName() {
		return toContactName;
	}

	public void setToContactName(String toContactName) {
		this.toContactName = toContactName;
	}

	public String getNamePrefix() {
		return namePrefix;
	}

	public void setNamePrefix(String namePrefix) {
		this.namePrefix = namePrefix;
	}

	public String getContactPrefix() {
		return contactPrefix;
	}

	public void setContactPrefix(String contactPrefix) {
		this.contactPrefix = contactPrefix;
	}

	public String getMobNumber() {
		return mobNumber;
	}

	public void setMobNumber(String mobNumber) {
		this.mobNumber = mobNumber;
	}

	public void setEmail(String em) {
		email = em;
	}

	public String getEmail() {
		return email;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getFromMobile() {
		return fromMobile;
	}

	public void setFromMobile(String fromMobile) {
		this.fromMobile = fromMobile;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getGroupzName() {
		return groupzName;
	}

	public void setGroupzName(String groupzName) {
		this.groupzName = groupzName;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public String getGroupzCode() {
		return groupzCode;
	}

	public void setGroupzCode(String groupzCode) {
		this.groupzCode = groupzCode;
	}

}
