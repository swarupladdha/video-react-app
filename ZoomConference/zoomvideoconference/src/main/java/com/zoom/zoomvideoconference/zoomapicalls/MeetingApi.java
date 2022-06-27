package com.zoom.zoomvideoconference.zoomapicalls;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zoom.zoomvideoconference.dbhelper.Meeting;
import net.sf.json.JSONObject;
import com.zoom.zoomvideoconference.services.MeetingService;
import com.zoom.zoomvideoconference.services.ParticipantService;

@Component
public class MeetingApi {

	@Autowired
	private MeetingService meetingservice;

	@Autowired
	private ParticipantService service;

	private final static Logger l = Logger.getLogger(MeetingApi.class);
	private final static String CREATE_MEETING_URI = "https://api.zoom.us/v2/users/me/meetings";
	private final String ADD_MEETING_REGISTRANTS = "https://api.zoom.us/v2/meetings/%s/registrants";
	private final String END_MEETING_URI = "https://api.zoom.us/v2/meetings/%s/status";

	String access = "eyJhbGciOiJIUzUxMiIsInYiOiIyLjAiLCJraWQiOiI4OWFmOTI4OS01Y2NkLTQ4ZjQtYjEzNS03OTE4ODJjYzc1NDQifQ.eyJ2ZXIiOjcsImF1aWQiOiIxMTBmYzQ2NmUzOGQyYTliNDgzZTRlM2YxNTEwYjNmMiIsImNvZGUiOiJ6ZnlIcGVJMGJJX3lLbFREc1hUVHdDU2I3QUMyOVdlSGciLCJpc3MiOiJ6bTpjaWQ6SWZQNlJ2bW9RSm1iSkNaNEowNExydyIsImdubyI6MCwidHlwZSI6MCwidGlkIjowLCJhdWQiOiJodHRwczovL29hdXRoLnpvb20udXMiLCJ1aWQiOiJ5S2xURHNYVFR3Q1NiN0FDMjlXZUhnIiwibmJmIjoxNjU1ODc1MDE5LCJleHAiOjE2NTU4Nzg2MTksImlhdCI6MTY1NTg3NTAxOSwiYWlkIjoiWG13TFRMZmlUN1diVHVPMGRNakl4ZyIsImp0aSI6IjU2MzY1MjIzLWRiMjAtNGEyZi04ZjE4LTE2YTI1ZGZmY2RlMiJ9.k-qnZoNMP7deYBQukuWd9odz78RSuct54gC9cIEotcCb6t1o1V2SPaKyJ-We03GevLW0E0sIQOyU3ORrFB6IVg";

	public String createMeeting(String token, String auth, String meetingBody)
			throws IOException, InterruptedException {
		boolean flag = false;
		l.info("Inside " + MeetingApi.class);

		String decoded = new String(java.util.Base64.getDecoder().decode(auth.substring(6).getBytes()));
		String[] credntials = decoded.trim().split(":");
		l.info("USernae" + credntials[0]);
		if (credntials[0].equals("Username") && credntials[1].equals("Password")) {
			l.info("The request is authorized");
			JSONObject obj = JSONObject.fromObject(meetingBody);
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest req = HttpRequest.newBuilder().uri(URI.create(CREATE_MEETING_URI))
					.POST(BodyPublishers.ofString(obj.getString("meeting_body")))
					.setHeader("Authorization", "Bearer " + token).setHeader("content-type", "application/json")
					.build();
			HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
			JSONObject obj2 = JSONObject.fromObject(res.body());
			l.info(res.body());
			if (res.statusCode() == 201) {
				JSONObject obj3 = obj.getJSONObject("meeting_body").getJSONObject("settings");
				l.info("settings " + obj3);
				JSONObject participants = obj.getJSONObject("participants");
				l.info("participantwss " + participants);
				l.info(obj2);
				l.info("inside settings and in alternative hosts " + obj3.getString("alternative_hosts"));
				l.info("THis is mysql operation");
				if (!(participants.getString("first_name").isEmpty() & participants.getString("email").isBlank())) {
					meetingservice.saveMeeting(new Meeting("Suthindran", obj3.getString("alternative_hosts"),
							obj2.getString("host_id"), new Date(), new Date(), obj2.getString("id"),
							obj2.getString("password"), String.valueOf(obj2)));
					boolean result = addMeetingRegistrants(token, obj2.getString("id"), participants.toString());
					if (result) {
						l.info("participants added");
					} else {
						l.info("failed to add participants");
					}
				} else {
					return "false";
				}
			} else {
				return res.body();
			}
			return res.body();
		} else {
			return "false";
		}
	}

	public String getListOfMeetings(String token) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest req = HttpRequest.newBuilder().uri(URI.create(CREATE_MEETING_URI)).POST(BodyPublishers.ofString(""))
				.setHeader("Authorization", "Bearer " + token).setHeader("content-type", "application/json").build();
		HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
		return res.body();
	}

	public boolean addMeetingRegistrants(String token, String meetingId, String body)
			throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest req = HttpRequest.newBuilder().uri(URI.create(String.format(ADD_MEETING_REGISTRANTS, meetingId)))
				.PUT(BodyPublishers.ofString(body)).setHeader("Authorization", "Bearer " + token)
				.setHeader("content-type", "application/json").build();
		HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
		if (res.statusCode() == 201) {
			return true;
		}
		return false;
	}

	public boolean endMeetingForcefully(String token, String meetingId, String body)
			throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest req = HttpRequest.newBuilder().uri(URI.create(String.format(END_MEETING_URI, meetingId)))
				.PUT(BodyPublishers.ofString(body)).setHeader("Authorization", "Bearer " + token)
				.setHeader("content-type", "application/json").build();
		HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
		if (res.statusCode() == 204) {
			return true;
		}
		l.info("in ending meeting" + res.body());
		return false;
	}

}
