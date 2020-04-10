package de.lh.tool.service.rest.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import de.lh.tool.domain.dto.ExceptionDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.DefaultRuntimeException;
import de.lh.tool.domain.exception.ExceptionEnum;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(DefaultException.class)
	protected ResponseEntity<Object> handleRestException(DefaultException restException, WebRequest request) {
		ExceptionEnum exception = restException.getException();
		return handleException(restException, request, exception);
	}

	@ExceptionHandler(DefaultRuntimeException.class)
	protected ResponseEntity<Object> handleRestRuntimeException(DefaultRuntimeException restException,
			WebRequest request) {
		ExceptionEnum exception = restException.getException();
		return handleException(restException, request, exception);
	}

	private ResponseEntity<Object> handleException(Exception restException, WebRequest request,
			ExceptionEnum exception) {
		return handleExceptionInternal(restException,
				new ExceptionDto(exception.name(), exception.getMessage(), exception.getHttpStatus().value()),
				new HttpHeaders(), exception.getHttpStatus(), request);
	}

}
