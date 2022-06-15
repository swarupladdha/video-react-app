package com.zom.mysqlaccess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zoom.api.calls.CreateMeeting;

@Repository
public interface MeetingRepository extends JpaRepository<CreateMeeting, Long>{
	
}
