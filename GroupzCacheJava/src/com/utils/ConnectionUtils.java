package com.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ConnectionUtils {

	public synchronized String ConnectandRecieve(String urls, String urlRequest) {
		String connectRecieveResponse = "";
		StringBuffer output = new StringBuffer("");
		try {
			// System.out.println("URL FINAL22 : " + urlString);
			System.out.println("-");
			InputStream stream = null;
			String urlString = URLEncoder.encode(urlRequest, "UTF-8");
			URL url = new URL(urls + urlString);
			System.out.println("URL FINAL : " + url);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			httpConnection.setRequestMethod("POST");
			httpConnection.setDoOutput(true);
			httpConnection.setConnectTimeout(50000);
			httpConnection.setReadTimeout(50000);
			httpConnection.setRequestProperty("Content-Type", "application/json");
			// httpConnection.setRequestProperty("Accept", "application/json");
			httpConnection.connect();
			if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				stream = httpConnection.getInputStream();
				BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
				String s = "";
				while ((s = buffer.readLine()) != null) {
					output.append(s);
				}
				buffer.close();

				connectRecieveResponse = output.toString();
				System.out.println(connectRecieveResponse);
				if (RestUtils.isEmpty(connectRecieveResponse) == false) {
					connectRecieveResponse = null;
				}
				// if ((connectRecieveResponse.contains("json") ||
				// connectRecieveResponse.contains("userdata") ||
				// connectRecieveResponse.contains("userList")) == false) {
				// connectRecieveResponse = null;
				// }
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return output.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return connectRecieveResponse;

		}
		return connectRecieveResponse;
	}

}