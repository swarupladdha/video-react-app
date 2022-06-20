package com.zoom.zoomvideoconference.otpservice;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.zoom.zoomvideoconference.repository.UserRepository;

@Service
public class OTPService {

	private UserRepository userRepo;
	
	private static final Logger l = Logger.getLogger(OTPService.class);
	
	public OTPService(UserRepository repo) {
		this.userRepo = repo;
	}
	public boolean sendOtp(String email) {
		l.info("inside send otp method");
//		l.info(repo.findAll());
		l.info("Single record "+ userRepo.findById(1));
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
