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

	@GetMapping(value = "/webhooks", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> whatsAppCallBack(@RequestParam("hub.mode") String mode,
			@RequestParam("hub.verify_token") String token, @RequestParam("hub.challenge") String challenge) {
		// log.info("request is " + request);
		if (mode.equals("subscribe") && token.equals("1ajSGF2294Cf0wVDt4Ln57pnhNviTBqeqspAETM3Z1I=")) {
			log.info("insided true condition");
			// JSONObject obj = JSONObject.fromObject(request);
			return ResponseEntity.status(HttpStatus.OK).body(challenge);

			// return ws.callBackResponse(token, obj);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
		}

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
