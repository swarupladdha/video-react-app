package com.zoom.zoomvideoconference.zoomapicalls;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

public class MeetingApi {

	private final static Logger l = Logger.getLogger(MeetingApi.class);
	private final static String CREATE_MEETING_URI = "";
	
	public String createMeeting(String token, String auth, String meetingBody)
			throws IOException, InterruptedException {
		
		l.info("Inside "+MeetingApi.class);

		String decoded = new String(java.util.Base64.getDecoder().decode(auth.substring(6).getBytes()));
		String[] creentials = decoded.trim().split(":");
		
		JSONObject obj = JSONObject.fromObject(meetingBody);
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest req = HttpRequest.newBuilder().uri(URI.create(CREATE_MEETING_URI)).POST(BodyPublishers.ofString(obj.toString()))
				.setHeader("Authorization", "Bearer " + "access token goes here")
				.setHeader("content-type", "application/json").build();
		HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
		return creentials[0] + " " + creentials[1];
	}
	
	
}
