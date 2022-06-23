package com.zoom.zoomvideoconference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zoom.zoomvideoconference.dbhelper.Meeting;

public interface MeetingRepository extends JpaRepository<Meeting, Integer>{
	
}
