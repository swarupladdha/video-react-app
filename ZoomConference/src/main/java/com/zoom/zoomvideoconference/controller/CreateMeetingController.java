package com.zoom.zoomvideoconference.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zoom.zoomvideoconference.dbhelper.UserDao;
import com.zoom.zoomvideoconference.otpservice.OTPService;
import com.zoom.zoomvideoconference.repository.UserRepository;

@Controller // This means that this class is a Controller
@RequestMapping(path="/demo")
public class CreateMeetingController {

	private static final Logger l = Logger.getLogger(CreateMeetingController.class);
	

	private UserRepository userRepo;
	private OTPService ser;
	@Autowired
	public CreateMeetingController(UserRepository rep, OTPService ser) {
		this.userRepo = rep;
		this.ser = ser;
	}
	
	
	@PostMapping(path = "/createmeeting")
	public String createMeetingEndpoint(@RequestHeader(value = "Authorization") String auth, @RequestBody String body) {
		l.info("inside createMeeting "+CreateMeetingController.class.getName());
		
		l.info("new application "+auth);
		l.info("new Application authorization "+body);
		
		//TODO: make call to otp method and send the otp
		//TODO: next call get all the details along with the otp
		
		UserDao d = new UserDao("username", "email", "password", "true");
		UserDao d2 = new UserDao("username2", "email2", "password2", "true2");
		UserDao d3 = new UserDao("username3", "email3", "password3", "true3");
		userRepo.save(d);
		userRepo.save(d2);
		userRepo.save(d3);
		
		Iterable<UserDao> it = userRepo.findAll();
		for (UserDao userDao : it) {
			l.info(userDao.getUsername());
			l.info(userDao.getPassword());
			l.info(userDao.getUserEmail());
			l.info(userDao.getIsHost());
		}
		
		ser.sendOtp("");
		return body;
	}
}
