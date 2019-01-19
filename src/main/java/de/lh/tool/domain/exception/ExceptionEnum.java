package de.lh.tool.domain.exception;

import org.springframework.http.HttpStatus;

import de.lh.tool.domain.model.PasswordChangeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionEnum {

	EX_USERS_NOT_FOUND("The users couldn't be found.", HttpStatus.NOT_FOUND),
	EX_USER_NO_EMAIL("The user has no email adress.", HttpStatus.BAD_REQUEST),
	EX_USER_NO_FIRST_NAME("The user has no first name.", HttpStatus.BAD_REQUEST),
	EX_USER_NO_LAST_NAME("The user has no last name.", HttpStatus.BAD_REQUEST),
	EX_USER_NO_GENDER("The user has no gender.", HttpStatus.BAD_REQUEST),

	EX_PASSWORDS_DO_NOT_MATCH("The provided passwords do not match.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_NO_TOKEN_OR_OLD_PASSWORD("No token or old password was provided.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_NO_USER_ID("No user id was provided.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_INVALID_USER_ID("The provided user id is invalid.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_INVALID_TOKEN("The provided token is invalid.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_EXPIRED_TOKEN("The provided token is expired.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_INVALID_PASSWORD("The provided old password is invalid.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_SHORT_PASSWORD(
			"The provided password has less than " + PasswordChangeToken.MIN_PASSWORD_LENGTH + " letters.",
			HttpStatus.BAD_REQUEST),

	EX_PROJECT_NOT_FOUND("The project couldn't be found.", HttpStatus.NOT_FOUND),

	EX_NEED_NOT_FOUND("The need couldn't be found.", HttpStatus.NOT_FOUND),;

	@Getter
	private String message;
	@Getter
	private HttpStatus httpStatus;
}
