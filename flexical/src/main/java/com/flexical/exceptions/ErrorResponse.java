package com.flexical.exceptions;

import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;

public class ErrorResponse {
	private String message;
	private List<String> errors;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

}
