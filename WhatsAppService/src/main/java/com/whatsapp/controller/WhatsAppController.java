package com.whatsapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whatsapp.scheduler.SendMessageScheduler;
import com.whatsapp.service.WhatsAppService;
import com.whatsapp.utils.RestUtils;

import net.sf.json.JSONObject;

@RestController
public class WhatsAppController {
	Logger log = Logger.getLogger(WhatsAppController.class);
	RestUtils utils = new RestUtils();
	@Autowired
	private WhatsAppService ws;

	@Autowired
	private ScheduledAnnotationBeanPostProcessor postProcessor;

	private SendMessageScheduler ss = new SendMessageScheduler();

	@RequestMapping(value = "/webhooks", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> whatsAppCallBack(@RequestParam(value = "hub.mode", required = false) String mode,
			@RequestParam(value = "hub.verify_token", required = false) String token,
			@RequestParam(value = "hub.challenge", required = false) String challenge,
			@RequestBody(required = false) String request) {

		// log.info("request is " + request);
		if (!utils.isEmpty(mode)) {
			if (mode.equals("subscribe") && token.equals("1ajSGF2294Cf0wVDt4Ln57pnhNviTBqeqspAETM3Z1I=")) {
				log.info("insided true condition");
				log.info("Response from facebook is " + request);
				// JSONObject obj = JSONObject.fromObject(request);
				return ResponseEntity.status(HttpStatus.OK).body(challenge);

				// return ws.callBackResponse(token, obj);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
			}
		} else {
			if (token.equals("1ajSGF2294Cf0wVDt4Ln57pnhNviTBqeqspAETM3Z1I=")) {
				log.info("Response from facebook is " + request);
			}
		}
		return null;

	}

	@GetMapping(value = "/stopScheduler")
	public String stopSchedule() {
		postProcessor.postProcessBeforeDestruction(ss, ss.sendMessage());

		return "OK";
	}

	@GetMapping(value = "/startScheduler")
	public String startSchedule() {
		postProcessor.postProcessAfterInitialization(ss, ss.sendMessage());
		return "OK";
	}
}
