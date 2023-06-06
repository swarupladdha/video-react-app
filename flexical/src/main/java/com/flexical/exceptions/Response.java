package com.flexical.exceptions;

public class Response {
    private String statuscode;
    private String statusmessage;

    public Response(String statuscode, String statusmessage) {
        this.statuscode = statuscode;
        this.statusmessage = statusmessage;
    }

    public String getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(String statuscode) {
        this.statuscode = statuscode;
    }

    public String getStatusmessage() {
        return statusmessage;
    }

    public void setStatusmessage(String statusmessage) {
        this.statusmessage = statusmessage;
    }
}
