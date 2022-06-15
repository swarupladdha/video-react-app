package com.zoom.api.calls;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import net.sf.json.JSONObject;

public class CreateZoomMeetingApi {
	
	private static final String CREATE_MEETING_URI = "";
	
	//TODO: Add the access token to properties 
	public String createMeeting(String token) throws IOException, InterruptedException {
		String meetingBody = "{\n" + "\n" + "	    \"agenda\": \"Testing Zoom Api\",\n"
				+ "	    \"default_password\": false,\n" + "	    \"duration\": 60,\n"
				+ "	    \"password\": \"123456\",\n" + "	    \"pre_schedule\": false,\n"
				+ "	    \"start_time\": \"2022-06-14T06:40:55Z\",\n" + "	    \"topic\": \"Testing Zoom Api\"\n"
				+ "	}";
		JSONObject obj = JSONObject.fromObject(meetingBody);
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest req = HttpRequest.newBuilder().uri(URI.create(CREATE_MEETING_URI)).POST(BodyPublishers.ofString(obj.toString()))
				.setHeader("Authorization", "Bearer " + "access token goes here")
				.setHeader("content-type", "application/json").build();
		HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
		return res.body().toString();
	}
}

