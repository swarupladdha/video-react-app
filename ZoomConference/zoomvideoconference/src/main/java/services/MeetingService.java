package services;

import java.util.*;

import dbhelper.Meeting;

public interface MeetingService {
	
	public Meeting saveMeeting(Meeting me);
	
	public List<Meeting> getAllMeetings();

	public Optional<Meeting> getMeeting(Integer meetingId);
	
}
