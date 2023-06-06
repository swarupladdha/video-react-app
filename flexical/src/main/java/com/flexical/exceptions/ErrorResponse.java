package com.flexical.exceptions;

import java.util.Map;

import org.apache.catalina.connector.Response;

//@Data
public class ErrorResponse {

	private String code;
	private String message;
	private Map<String, String> fields;
	public ErrorResponse(String string) {
		// TODO Auto-generated constructor stub
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Map<String, String> getFields() {
		return fields;
	}
	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}
	public void setResponse(Response response) {
		// TODO Auto-generated method stub
		
	}
	
}
