package com.infosoft.implementation;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import exotel.ExotelStrings;
import okhttp3.Credentials;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Repository
public class VoiceImp {
	public static final Logger logger = Logger.getLogger(VoiceImp.class);

////	public static String customerNumber = "917338237086";
//	public static String url = "http://api.exotel.com/";
//	public static String exotelSid = "infosoftjoin2";
//	public static String apiid = "6672f5d29f4b8e9ca2b56f0c8d8cb98c417be685faac983c";
//	public static String apitoken = "b3abb41bc69377a45fa5a88863b8a7749e5b80a57694ebcd";
////	public static String agentNumber = "919790273390";

	@Value("${url}")
	private String url;

	@Value("${exotelSid}")
	private String exotelSid;

	@Value("${apiid}")
	private String apiid;

	@Value("${apitoken}")
	private String apitoken;

	@Value("${callBackUrl}")
	private String callBackUrl;

	@Value("${callerId}")
	private String callerId;

	public String connectToAgent(String fromNumber, String toNumber, String timeLimit) {
		System.out.println("inside connect agent");
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("From", fromNumber)
				.addFormDataPart("To", toNumber).addFormDataPart("TimeLimit", timeLimit)
				.addFormDataPart("CallerId", callerId).addFormDataPart("StatusCallback", callBackUrl + "statusCallBack")
				.addFormDataPart("StatusCallbackEvents[0]", "terminal")
				.addFormDataPart("StatusCallbackContentType", "application/json").build();

		String credentials = Credentials.basic(apiid, apitoken);

		Request request = new Request.Builder().url(String.format(ExotelStrings.CONNECT_TO_AGENT_URL, exotelSid))
				.method("POST", body).addHeader("Authorization", credentials)
				.addHeader("Content-Type", "application/json").build();

		try {
			Response response = client.newCall(request).execute();
			logger.info(response);
			String res = null;
			try {
				res = response.body().string();
				logger.info(res);
				logger.info("Status code is " + response.code());
				return res;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
