package com.flexical.exceptions;

import org.springframework.http.HttpStatus;

public class Response {
    private HttpStatus statuscode;
    private String statusmessage;

    public Response(HttpStatus badRequest, String statusmessage) {
    	System.out.println("badRequest "+badRequest);
        this.statuscode = HttpStatus.BAD_REQUEST;
        this.statusmessage = statusmessage;
    }

    public HttpStatus getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(HttpStatus statuscode) {
        this.statuscode = statuscode;
    }

    public String getStatusmessage() {
        return statusmessage;
    }

    public void setStatusmessage(String statusmessage) {
        this.statusmessage = statusmessage;
    }
}
