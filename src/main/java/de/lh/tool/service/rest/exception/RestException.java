package de.lh.tool.service.rest.exception;

import lombok.Getter;

public class RestException extends Exception {

	private static final long serialVersionUID = 1718283140957405588L;

	@Getter
	private final RestExceptionEnum exception;

	public RestException(RestExceptionEnum exception) {
		super(exception.getMessage());
		this.exception = exception;
	}

	public RestException(RestExceptionEnum exception, Throwable throwable) {
		super(exception.getMessage(), throwable);
		this.exception = exception;
	}
}
