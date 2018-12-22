package de.lh.tool.service.rest.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import de.lh.tool.domain.dto.ExceptionDto;
import de.lh.tool.domain.exception.DefaultException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(DefaultException.class)
	protected ResponseEntity<Object> handleRestException(DefaultException restException, WebRequest request) {
		return handleExceptionInternal(restException,
				new ExceptionDto(restException.getException().name(), restException.getException().getMessage(),
						restException.getException().getHttpStatus().value()),
				new HttpHeaders(), restException.getException().getHttpStatus(), request);
	}

}
