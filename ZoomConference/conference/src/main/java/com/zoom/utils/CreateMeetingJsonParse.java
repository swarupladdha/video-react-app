package com.zoom.utils;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.zoom.dbhelper.CreateMeeting;

import net.sf.json.JSONObject;

public class CreateMeetingJsonParse {
	private CreateMeeting meeting = new CreateMeeting();
	private static Logger l = Logger.getLogger(CreateMeetingJsonParse.class);

	public void createMeetingJson(String json) {
		JSONObject obj = JSONObject.fromObject(json.trim());
		Iterator<String> key = obj.keys();
		
		while (key.hasNext()) {
			String k = key.next();
			l.info(k + " : " + obj.get(k));
			System.out.println(k + " : " + obj.get(k));
			inner: {
				switch (k) {
				case "meetingAgenda":
					System.out.println("meetingAgedna");
					break inner;
				case "defaultPassword":
					System.out.println("defaultPassword");
					break inner;
				case "meetingDuration":
					System.out.println("meetingDuration");
					break inner;
				case "meetingPassword":
					System.out.println("meetingPassword");
					break inner;
				}
			}

		}
	}
}
