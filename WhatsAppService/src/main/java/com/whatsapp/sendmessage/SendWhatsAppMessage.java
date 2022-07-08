package com.whatsapp.sendmessage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.whatsapp.connections.ConnectionPooling;
import com.whatsapp.repository.WhatsAppRepo;
import com.whatsapp.utils.PropertiesUtil;
import com.whatsapp.utils.RestUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendWhatsAppMessage implements Runnable {
	RestUtils rs = new RestUtils();
	private int threadId;

	public SendWhatsAppMessage(int threadId) {
		this.threadId = threadId;
	}

	ConnectionPooling cp = new ConnectionPooling();

	@Override
	public void run() {
		try {
			getNumberAndSend(threadId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getNumberAndSend(int threadId) throws SQLException {
		Connection con = null;
		try {

			con = cp.getConnection();
			WhatsAppRepo wr = new WhatsAppRepo();
			JSONArray arr = wr.getNumbersWithMessage(5, threadId, con);
			System.out.println(arr);
			if (!arr.isEmpty())
				for (Object o : arr) {
					JSONObject obj = (JSONObject) o;
					if (!rs.isEmpty(obj)) {
						String num = obj.getString("number");
						String mes = obj.getString("message");
						//sendMessage(num, mes);

					}
				}

		} finally {
			if (con != null)
				cp.closeConnection(con);
		}

	}

	public String sendMessage(String number, String message) {
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create("{\"messaging_product\":\"whatsapp\",\"to\":\"" + number
				+ "\",\"type\":\"text\",\"text\":{\"body\":\"" + message + "\"}}", mediaType);
		Request request = new Request.Builder().url(PropertiesUtil.getProperty("sendMessageUrl")).method("POST", body)
				.addHeader("Authorization", PropertiesUtil.getProperty("authKey"))
				.addHeader("Content-Type", "application/json").build();
		try {
			Response response = client.newCall(request).execute();
			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
