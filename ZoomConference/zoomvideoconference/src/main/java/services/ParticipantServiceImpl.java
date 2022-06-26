package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dbhelper.Participant;
import repository.ParticipantRepository;

@Service
public class ParticipantServiceImpl implements ParticipantService {

	@Autowired
	private ParticipantRepository repo;

	@Override
	public Participant addParticipant(Participant p) {
		repo.save(p);
		return p;
	}

}
