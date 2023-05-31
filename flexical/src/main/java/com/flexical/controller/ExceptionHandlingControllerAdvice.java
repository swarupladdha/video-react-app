package com.flexical.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import net.sf.json.JSONObject;

@RestControllerAdvice
public class ExceptionHandlingControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<JSONObject> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        JSONObject response = new JSONObject();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
        	response.put("statuscode", error.getCode());
            response.put("statusmessage", error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
