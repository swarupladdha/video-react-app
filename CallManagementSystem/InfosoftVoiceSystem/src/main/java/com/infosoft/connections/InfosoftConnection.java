package com.infosoft.connections;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.infosoft.utils.AllKeys;

import net.sf.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Configuration
public class InfosoftConnection {
	public static final Logger logger = Logger.getLogger(InfosoftConnection.class);

	@Value("${dburl}")
	private String dbUrl;
	@Value("${dbusername}")
	private String dbUserName;
	@Value("${dbpassword}")
	private String dbPassword;
	private static BasicDataSource ds = null;

	public synchronized Connection dataBaseConnection() {
		Connection con = null;
		try {
			if (ds == null) {
				ds = new BasicDataSource();
				ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
				ds.setUrl(dbUrl);
				ds.setUsername(dbUserName);
				ds.setPassword(dbPassword);
				ds.setInitialSize(10);
				ds.setMaxTotal(20);
			}
			con = ds.getConnection();
			return con;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String sendCallBackResponse(String callId, String callSid, String callStatus, String callBackUrl) {
		OkHttpClient client = new OkHttpClient();
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.connectTimeout(5, TimeUnit.MINUTES) // connect timeout
				.writeTimeout(5, TimeUnit.MINUTES) // write timeout
				.readTimeout(5, TimeUnit.MINUTES); // read timeout
		client = builder.build();
		JSONObject data = new JSONObject();
		JSONObject res = new JSONObject();
		JSONObject main = new JSONObject();
		data.put(AllKeys.CALLID, callId);
		data.put(AllKeys.CALLSID, callSid);
		data.put(AllKeys.CALL_STATUS, callStatus);
		res.put(AllKeys.DATA, data);
		main.put(AllKeys.RESPONSE, res);
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(main.toString(), mediaType);
		Request request = new Request.Builder().url(callBackUrl).method("POST", body)
				.addHeader("Content-Type", "application/json").build();
		try {
			Response response = client.newCall(request).execute();
			logger.info(response.toString());
			return response.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
