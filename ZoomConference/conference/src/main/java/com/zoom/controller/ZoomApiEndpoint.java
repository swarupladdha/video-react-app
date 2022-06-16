package com.zoom.controller;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.zoom.api.calls.CreateZoomMeetingApi;
import com.zoom.api.calls.ZoomApiReq;
import com.zoom.utils.ApplicationProperties;
import com.zoom.utils.CreateMeetingJsonParse;

@RestController
public class ZoomApiEndpoint {
	CreateMeetingJsonParse j = new CreateMeetingJsonParse();

	@Autowired
	ApplicationProperties properties;
	
	CreateZoomMeetingApi c = new CreateZoomMeetingApi();
	
	private static Logger l = Logger.getLogger(ZoomApiEndpoint.class);

	@GetMapping(value = "/getlistofmeeting")
	public String loginZoom() {
		try {
			ZoomApiReq z = new ZoomApiReq();
			z.getTokenOauth("null");
			System.out.println(properties.getJwtApiKey());
			System.out.println(properties.getJwtApiSecrete());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Success";
	}

	@PostMapping(value = "/createmeeting")
	public String createMeeting(@RequestHeader(value = "Authorization") String auth, @RequestBody String body) throws IOException, InterruptedException {
		
		ZoomApiReq z = new ZoomApiReq();
		properties.setOauthAccessToken(z.getTokenOauth("s"));
		l.info("This is access token" +properties.getOauthAccessToken());
		l.info("This is a auth"+auth);
		l.info("this is a body"+body);
		String res = c.createMeeting(properties.getOauthAccessToken(), auth, body);
		System.out.println(res);
		l.info(res);
		return body;
	}

	@GetMapping("/")
	public String a() {
		return "s";
	}
}
