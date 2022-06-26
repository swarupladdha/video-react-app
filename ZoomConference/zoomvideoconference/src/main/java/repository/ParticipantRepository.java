package repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dbhelper.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Integer>{

}
