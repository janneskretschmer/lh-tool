package de.lh.tool.domain.exception;

import de.lh.tool.domain.exception.ExceptionEnum.ExceptionEnumWrapper;
import lombok.Getter;

public class DefaultException extends Exception implements ExceptionEnumWrapper {

	private static final long serialVersionUID = 1718283140957405588L;

	@Getter
	private final ExceptionEnum exception;

	public DefaultException(ExceptionEnum exception) {
		super(exception.getMessage());
		this.exception = exception;
	}

	public DefaultException(ExceptionEnum exception, Throwable throwable) {
		super(exception.getMessage(), throwable);
		this.exception = exception;
	}
}
