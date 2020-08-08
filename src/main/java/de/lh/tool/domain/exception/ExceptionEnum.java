package de.lh.tool.domain.exception;

import org.springframework.http.HttpStatus;

import de.lh.tool.domain.model.PasswordChangeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionEnum {

	EX_ID_PROVIDED("Please don't provide an ID for new entities.", HttpStatus.BAD_REQUEST),
	EX_NO_ID_PROVIDED("Please provide an ID for changed entities.", HttpStatus.BAD_REQUEST),
	EX_WRONG_ID_PROVIDED("Please provide a valid ID.", HttpStatus.BAD_REQUEST),
	EX_INVALID_ID("The provided id is invalid.", HttpStatus.BAD_REQUEST),
	EX_INVALID_USER_ID("The provided user id is invalid.", HttpStatus.BAD_REQUEST),
	EX_INVALID_PROJECT_ID("The provided project id is invalid.", HttpStatus.BAD_REQUEST),
	EX_INVALID_HELPER_TYPE_ID("The provided id is invalid.", HttpStatus.BAD_REQUEST),
	EX_INVALID_ITEM_ID("The provided item id is invalid.", HttpStatus.BAD_REQUEST),
	EX_INVALID_NOTE_ID("The provided note id is invalid.", HttpStatus.BAD_REQUEST),
	EX_FORBIDDEN("You don't have the sufficient rights for this action.", HttpStatus.FORBIDDEN),

	EX_USERS_NOT_FOUND("The users couldn't be found.", HttpStatus.NOT_FOUND),
	EX_USER_NO_EMAIL("The user has no email address.", HttpStatus.BAD_REQUEST),
	EX_USER_NO_FIRST_NAME("The user has no first name.", HttpStatus.BAD_REQUEST),
	EX_USER_NO_LAST_NAME("The user has no last name.", HttpStatus.BAD_REQUEST),
	EX_USER_NO_GENDER("The user has no gender.", HttpStatus.BAD_REQUEST),
	EX_USER_EMAIL_ALREADY_IN_USE("The provided e-mail address is already in use.", HttpStatus.CONFLICT),
	EX_USER_ROLE_ALREADY_EXISTS("The user already has this role.", HttpStatus.CONFLICT),

	EX_PASSWORDS_DO_NOT_MATCH("The provided passwords do not match.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_NO_TOKEN_OR_OLD_PASSWORD("No token or old password was provided.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_NO_USER_ID("No user id was provided.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_INVALID_TOKEN("The provided token is invalid.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_EXPIRED_TOKEN("The provided token is expired.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_INVALID_PASSWORD("The provided old password is invalid.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_SHORT_PASSWORD(
			"The provided password has less than " + PasswordChangeToken.MIN_PASSWORD_LENGTH + " letters.",
			HttpStatus.BAD_REQUEST),

	EX_PROJECT_NOT_FOUND("The project couldn't be found.", HttpStatus.NOT_FOUND),
	EX_PROJECT_NAME_ALREADY_EXISTS("A project with the provided name already exists.", HttpStatus.CONFLICT),

	EX_NEED_NOT_FOUND("The need couldn't be found.", HttpStatus.NOT_FOUND),
	EX_NEED_ALREADY_EXISTS("A need with the provided date and project helper type already exists.",
			HttpStatus.CONFLICT),
	EX_NEED_USER_NOT_FOUND("The need-user-connection couldn't be found.", HttpStatus.NOT_FOUND),
	EX_NEED_USER_INVALID_STATE("The provided state is invalid.", HttpStatus.BAD_REQUEST),

	EX_HELPER_TYPE_ALREADY_EXISTS("A helper type with the provided name already exists.", HttpStatus.CONFLICT),
	EX_HELPER_TYPE_WEEKDAY_WITHOUT_PROJECT("Please provide a valid project id with the weekday query.",
			HttpStatus.BAD_REQUEST),

	EX_ITEM_NO_TAG("Please provide a tag name.", HttpStatus.BAD_REQUEST),
	EX_ITEM_ITEM_TAG_ALREADY_EXISTS("The item already has the provided tag.", HttpStatus.CONFLICT),
	EX_ITEM_NO_SLOT("The item has no slot.", HttpStatus.BAD_REQUEST),
	EX_ITEM_NO_IDENTIFIER("The item has no identifier.", HttpStatus.BAD_REQUEST),
	EX_ITEM_NO_NAME("The item has no name.", HttpStatus.BAD_REQUEST),
	EX_ITEM_NO_TECHNICAL_CREW("The item has no technical crew.", HttpStatus.BAD_REQUEST),
	EX_ITEM_IDENTIFIER_ALREADY_IN_USE("The identifier is already in use.", HttpStatus.CONFLICT),
	EX_ITEM_SELF_RELATION("An item can't be related to itself.", HttpStatus.BAD_REQUEST),
	EX_ITEM_RELATION_ALREADY_EXISTS("The items are already related.", HttpStatus.CONFLICT),
	//
	;

	@Getter
	private String message;
	@Getter
	private HttpStatus httpStatus;

	public DefaultException createDefaultException() {
		return new DefaultException(this);
	}

	public DefaultException createDefaultException(Throwable cause) {
		return new DefaultException(this, cause);
	}
}
