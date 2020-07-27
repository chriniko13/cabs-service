package com.booking.producer.resource.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

	@ExceptionHandler(BindException.class)
	public ResponseEntity<Object> handle(BindException e) {

		List<ObjectError> allErrors = e.getAllErrors();

		List<ValidationErrorMessage> validationErrorMessages = allErrors
				.stream()
				.map(error -> new ValidationErrorMessage(error.getDefaultMessage()))
				.collect(Collectors.toList());

		return new ResponseEntity<>(new ValidationErrorMessages(validationErrorMessages), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidBookingIdException.class)
	public ResponseEntity<Object> handle(InvalidBookingIdException e) {

		ValidationErrorMessage message = new ValidationErrorMessage("provided id is not valid, a valid one is of UUID type");

		return new ResponseEntity<>(new ValidationErrorMessages(Collections.singletonList(message)), HttpStatus.BAD_REQUEST);
	}

}
