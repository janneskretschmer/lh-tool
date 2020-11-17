package de.lh.tool.service.rest.exception;

import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import de.lh.tool.domain.dto.ExceptionDto;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.exception.ExceptionEnum.ExceptionEnumWrapper;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleRestException(Exception restException, WebRequest request) throws Exception {
		if (restException instanceof AccessDeniedException) {
			return handleException(restException, request, ExceptionEnum.EX_FORBIDDEN);
		}

		Exception cause = restException;

		while (cause != null) {
			Optional<ResponseEntity<Object>> optionalResponse = Optional.of(cause)
					.filter(ExceptionEnumWrapper.class::isInstance).map(ExceptionEnumWrapper.class::cast)
					.map(ExceptionEnumWrapper::getException)
					.map(exception -> handleException(restException, request, exception));
			if (optionalResponse.isPresent()) {
				return optionalResponse.get();
			}
			cause = Optional.ofNullable(cause.getCause()).filter(Exception.class::isInstance).map(Exception.class::cast)
					.orElse(null);
		}
		return super.handleException(restException, request);
	}

	private ResponseEntity<Object> handleException(Exception restException, WebRequest request,
			ExceptionEnum exception) {
		return handleExceptionInternal(restException,
				new ExceptionDto(exception.name(), exception.getMessage(), exception.getHttpStatus().value()),
				new HttpHeaders(), exception.getHttpStatus(), request);
	}

}
