package com.zoom.zoomvideoconference.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zoom.zoomvideoconference.dbhelper.Meeting;
import com.zoom.zoomvideoconference.repository.MeetingRepository;

@Service
public class MeetingServiceImpl implements MeetingService{
	
	@Autowired
	private MeetingRepository repo;

	@Override
	public Meeting saveMeeting(Meeting me) {
		return repo.save(me);
	}
	
	@Override
	public List<Meeting> getAllMeetings() {
		return repo.findAll();
	}

	@Override
	public Optional<Meeting> getMeeting(Integer meetingId) {
		return repo.findById(meetingId);
	}
	
}
