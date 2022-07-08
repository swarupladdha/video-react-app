package com.whatsapp.service;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import net.sf.json.JSONObject;

@Service
public class WhatsAppService {
	Logger log = Logger.getLogger(WhatsAppService.class);

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<String> callBackResponse(String token, JSONObject obj) {
		log.info("inside callbach service");
		String accessToken = "1ajSGF2294Cf0wVDt4Ln57pnhNviTBqeqspAETM3Z1I=";
		if (accessToken.equals(token)) {
			log.info("Access token matched");
			return ResponseEntity.status(HttpStatus.OK).body("");
		} else {
			log.info("Invalid token");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid token");
		}

	}

}
