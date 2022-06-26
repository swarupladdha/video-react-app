package controller;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import authentication.TokenGenerator;
import otpservice.OTPService;
import services.MeetingService;
import zoomapicalls.MeetingApi;

@RestController
@RequestMapping(path = "/meeting")
public class CreateMeetingController {

	private static final Logger l = Logger.getLogger(CreateMeetingController.class);

	private TokenGenerator gen = new TokenGenerator();
	private MeetingApi m;

	private MeetingService meetingRepo;
	private OTPService ser;
	private Environment env;

	@Autowired
	public CreateMeetingController(OTPService ser, Environment env, MeetingApi m) {
		this.ser = ser;
		this.env = env;
		this.m = m;
	}

	@PostMapping(path = "/createmeeting")
	public ResponseEntity<?> createMeetingEndpoint(@RequestHeader(value = "Authorization") String auth,
			@RequestBody String body) {
		l.info("inside createMeeting " + CreateMeetingController.class.getName());

		l.info("new application " + auth);
		l.info("new Application authorization " + body);

		String reponse = "";
		HttpStatus s = HttpStatus.OK;
		try {
			String oauthAcessToken = gen.getTokenOauth(env.getProperty("app.oauthAccountId"),
					env.getProperty("app.oauthClientId"), env.getProperty("app.oauthClientSecrete"));
			reponse = m.createMeeting(oauthAcessToken, auth, body);
			if (reponse.equals("false")) {
				s = HttpStatus.BAD_REQUEST;
				reponse = "{ \"msg\": \"invalid authorization \"}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			l.info(e);
		}

		return new ResponseEntity<>(reponse, s);
	}

	@GetMapping(path = "/")
	public String test(@RequestParam String code) throws IOException, InterruptedException {
		l.info("inside test");
		l.info("Code generated " + code);
		String res = gen.getoauthToken(env.getProperty("app.clientId"), env.getProperty("app.secrete"), code);
		return res;
	}

	@PostMapping(path = "/endmeeting")
	public String endmeeting(@RequestHeader(value = "Authorization") String auth, @RequestBody String body)
			throws IOException, InterruptedException {
		String oauthAcessToken = gen.getTokenOauth(env.getProperty("app.oauthAccountId"),
				env.getProperty("app.oauthClientId"), env.getProperty("app.oauthClientSecrete"));
		String l = "85752064185";
		boolean res = m.endMeetingForcefully(oauthAcessToken, l, body);
		if (res) {
			return "Meeting ended";
		} else {
			return "Failed";
		}
	}
//	@PostMapping(path = "/refresh")
//	public String refresh(@RequestHeader(value = "Authorization") String auth,
//			@RequestBody String body) {
//		try {
//			String access_token = gen.getAccessToken(env.getProperty("app.clientId"), env.getProperty("app.secrete"));
//			String res = m.createMeeting(access_token, auth, body);
//			l.info("inside refresh "+res);
//			return res;
//		} catch (IOException | InterruptedException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
}
