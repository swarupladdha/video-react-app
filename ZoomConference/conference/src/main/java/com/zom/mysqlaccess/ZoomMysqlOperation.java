package com.zom.mysqlaccess;

import org.springframework.beans.factory.annotation.Autowired;

import com.zoom.api.calls.CreateMeeting;

public class ZoomMysqlOperation {
	
	@Autowired
	MeetingRepository rep;
	
	public String saveMeeting(CreateMeeting m) {
		return null;
	}
	
}
