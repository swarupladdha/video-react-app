package repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dbhelper.Meeting;

public interface MeetingRepository extends JpaRepository<Meeting, Integer>{
	
}
