package de.lh.tool.service.rest.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RestExceptionEnum {

	EX_USERS_NOT_FOUND("The users couldn't be found.", HttpStatus.NOT_FOUND);

	@Getter
	private String message;
	@Getter
	private HttpStatus httpStatus;
}
