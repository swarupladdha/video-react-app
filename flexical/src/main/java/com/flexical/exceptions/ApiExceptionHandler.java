package com.flexical.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.flexical.util.PropertiesUtil;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		BindingResult bindingResult = ex.getBindingResult();
		List<FieldError> fieldErrors = bindingResult.getFieldErrors();

		// Create a list of error messages
		List<String> errors = new ArrayList<>();
		for (FieldError fieldError : fieldErrors) {
			errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
		}

		// Create the custom error response
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage("Validation failed");
		errorResponse.setErrors(errors);

		// Return the custom error response with a 400 Bad Request status
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	//@ExceptionHandler(MethodArgumentNotValidException.class)
	/*public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();
		List<FieldError> fieldErrors = bindingResult.getFieldErrors();

		// Create a list of error messages
		List<String> errors = new ArrayList<>();
		for (FieldError fieldError : fieldErrors) {
			errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
		}

		// Create the custom error response
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage("Validation failed");
		errorResponse.setErrors(errors);

		// Return the custom error response with a 400 Bad Request status
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}*/

}
