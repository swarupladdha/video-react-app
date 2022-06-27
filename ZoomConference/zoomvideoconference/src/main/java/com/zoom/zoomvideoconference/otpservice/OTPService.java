package com.zoom.zoomvideoconference.otpservice;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.zoom.zoomvideoconference.dbhelper.Meeting;
import com.zoom.zoomvideoconference.repository.MeetingRepository;

@Service
public class OTPService {

	private MeetingRepository userRepo;

	private static final Logger l = Logger.getLogger(OTPService.class);

	public OTPService(MeetingRepository repo) {
		this.userRepo = repo;
	}

	public boolean sendOtp(String email) {
		l.info("inside send otp method");
//		l.info(repo.findAll());

		Optional<Meeting> d = userRepo.findById(1);
		Meeting da = d.get();
		l.info("Single record " + "user email " + da.getUserEmail() + " user password ");
//		Iterable<UserDao> it= (List<UserDao>) repo.findAll();
//		for (UserDao userDao : it) {
//			l.info(userDao.getUsername());
//			l.info(userDao.getPassword());
//			l.info(userDao.getUserEmail());
//			l.info(userDao.getIsHost());
//		}
		return false;
	}
}
