package com.zoom.zoomvideoconference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zoom.zoomvideoconference.dbhelper.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Integer>{

}
