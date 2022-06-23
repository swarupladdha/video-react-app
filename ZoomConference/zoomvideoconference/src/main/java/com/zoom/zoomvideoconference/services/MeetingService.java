package com.zoom.zoomvideoconference.services;

import java.util.*;
import com.zoom.zoomvideoconference.dbhelper.Meeting;

public interface MeetingService {
	
	public Meeting saveMeeting(Meeting me);
	
	public List<Meeting> getAllMeetings();

	public Optional<Meeting> getMeeting(Integer meetingId);
	
}
