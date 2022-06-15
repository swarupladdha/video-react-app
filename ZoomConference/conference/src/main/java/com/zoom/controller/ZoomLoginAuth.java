package com.zoom.controller;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.zoom.api.calls.CreateZoomMeetingApi;
import com.zoom.api.calls.ZoomApiReq;
import com.zoom.utils.ApplicationProperties;
import com.zoom.utils.CreateMeetingJsonParse;

import net.sf.json.JSONObject;

@RestController
public class ZoomLoginAuth {
	CreateMeetingJsonParse j = new CreateMeetingJsonParse();
	
	@Autowired
	ApplicationProperties properties;

	private static Logger l = Logger.getLogger(ZoomLoginAuth.class);

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
	public String createMeeting() throws IOException, InterruptedException {
//		j.createMeetingJson(body);
		CreateZoomMeetingApi c = new CreateZoomMeetingApi();
		ZoomApiReq z = new ZoomApiReq();
		z.getTokenOauth("s");
		String res = c.createMeeting("https://api.zoom.us/v2/users/me/meetings");
		l.info("res");
		return res;
	}
}
