package com.flexical.exceptions;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.flexical.util.PropertiesUtil;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		CustomErrorResponse errorResponse = new CustomErrorResponse();
		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
				.collect(Collectors.toList());

		logger.info("errors " + errors.toString());
		if (errors.contains("clientKey is mandatory")) {
			// Error: clientKey is mandatory
			errorResponse.setResponse(new Response(PropertiesUtil.getProperty("missing_clientkey_key_code"),
					PropertiesUtil.getProperty("missing_clientkey_key_message")));
		} else if (errors.contains("resourceId is mandatory")) {
			// Error: resourceId can't be empty
			errorResponse.setResponse(new Response(PropertiesUtil.getProperty("missing_resourceid_key_code"),
					PropertiesUtil.getProperty("missing_resourceid_key_message")));
		} else if (errors.contains("weekdayId is mandatory")) {
			// Error: resourceId can't be empty
			errorResponse.setResponse(new Response(PropertiesUtil.getProperty("missing_weekdayId_key_code"),
					PropertiesUtil.getProperty("missing_weekdayId_key_message")));
		} else if (errors.contains("startTime not valid")) {
			// Error: resourceId can't be empty
			errorResponse.setResponse(new Response(PropertiesUtil.getProperty("invalid_timeformat_code"),
					PropertiesUtil.getProperty("invalid_timeformat_message")));
		} else if (errors.contains("endTime not valid")) {
			// Error: resourceId can't be empty
			errorResponse.setResponse(new Response(PropertiesUtil.getProperty("invalid_timeformat_code"),
					PropertiesUtil.getProperty("invalid_timeformat_message")));
		} else if (errors.contains("working is mandatory")) {
			// Error: resourceId can't be empty
			errorResponse.setResponse(new Response(PropertiesUtil.getProperty("missing_working_key_code"),
					PropertiesUtil.getProperty("missing_working_key_message")));
		} else if (errors.contains("working must be in the range of 0 and 1")) {
			// Error: resourceId can't be empty
			errorResponse.setResponse(new Response(PropertiesUtil.getProperty("invalid_working_code"),
					PropertiesUtil.getProperty("invalid_working_message")));
		} else if (errors.contains("weekdayId must be in the range of 1 and 7")) {
			// Error: resourceId can't be empty
			errorResponse.setResponse(new Response(PropertiesUtil.getProperty("invalid_weekdayId_code"),
					PropertiesUtil.getProperty("invalid_weekdayId_message")));
		} else {
			// Default error code
			errorResponse.setResponse(new Response(PropertiesUtil.getProperty("common_error_code"), errors.toString()));
		}

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

	}
}
