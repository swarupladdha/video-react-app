package com.services;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;


public class ConnectAndRecive{
	
	static final Logger logger = Logger.getLogger(ConnectAndRecive.class);
	public String ConnectandRecieve(String urlString) {
		String connectRecieveResponse = "";
		StringBuffer output = new StringBuffer("");
		try {
			logger.info("URL FINAL : " + urlString);

			InputStream stream = null;
			URL url = new URL(urlString);
			logger.info("URL FINAL22 : " + url);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			httpConnection.setRequestMethod("POST");
			httpConnection.setDoOutput(true);
			httpConnection.setConnectTimeout(300000);
			httpConnection.setReadTimeout(300000);
			httpConnection.setRequestProperty("Content-Type","application/json");
			httpConnection.setRequestProperty("Accept", "application/json");
			httpConnection.connect();
			if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				stream = httpConnection.getInputStream();
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(stream));
				String s = "";
				while ((s = buffer.readLine()) != null) {
					output.append(s);
				}
				buffer.close();
				connectRecieveResponse = output.toString();
//				if ((connectRecieveResponse.contains("json") || connectRecieveResponse.contains("data")||
//						(connectRecieveResponse.contains("statuscode"))) == false) {
//					connectRecieveResponse = null;
//				}
			}
		} catch (IOException e1) {
			logger.error("Exception",e1);
			return output.toString();

		} catch (Exception e) {
			logger.error("Exception",e);
			return connectRecieveResponse;

		}
		return connectRecieveResponse;
	}
}