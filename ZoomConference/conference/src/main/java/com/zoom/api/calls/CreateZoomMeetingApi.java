package com.zoom.api.calls;

import java.io.IOException;

import org.apache.log4j.Logger;

public class CreateZoomMeetingApi {

	private Logger l = Logger.getLogger(CreateZoomMeetingApi.class);
	private static final String CREATE_MEETING_URI = "https://api.zoom.us/v2/users/me/meetings";

	// TODO: Add the access token to properties
	public String createMeeting(String token, String auth, String meetingBody)
			throws IOException, InterruptedException {
		l.info("Inside the Createzoomapi/createmeeting");

		String decoded = new String(java.util.Base64.getDecoder().decode(auth.substring(6).getBytes()));
		String[] creentials = decoded.trim().split(":");

		//userRepo.save(new UserDao(creentials[0].trim(), creentials[1].trim(), "true"));
//		JSONObject obj = JSONObject.fromObject(meetingBody);
//		HttpClient client = HttpClient.newHttpClient();
//		HttpRequest req = HttpRequest.newBuilder().uri(URI.create(CREATE_MEETING_URI)).POST(BodyPublishers.ofString(obj.toString()))
//				.setHeader("Authorization", "Bearer " + "access token goes here")
//				.setHeader("content-type", "application/json").build();
//		HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
//		return res.body().toString();
		return creentials[0] + " " + creentials[1];
	}
}
