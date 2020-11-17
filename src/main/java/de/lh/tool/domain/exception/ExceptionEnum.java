package de.lh.tool.domain.exception;

import org.springframework.http.HttpStatus;

import de.lh.tool.domain.model.PasswordChangeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionEnum {

	EX_ILLEGAL_ID("Please don't provide an id for new entities.", HttpStatus.BAD_REQUEST),
	EX_FORBIDDEN("You don't have the sufficient rights for this action.", HttpStatus.FORBIDDEN),

	EX_INVALID_ID("The provided id is invalid.", HttpStatus.BAD_REQUEST),
	EX_INVALID_USER_ID("The provided user id is invalid.", HttpStatus.BAD_REQUEST),
	EX_INVALID_PROJECT_ID("The provided project id is invalid.", HttpStatus.BAD_REQUEST),
	EX_INVALID_HELPER_TYPE_ID("The provided id is invalid.", HttpStatus.BAD_REQUEST),
	EX_INVALID_STORE_ID("The provided store id is invalid.", HttpStatus.BAD_REQUEST),
	EX_INVALID_SLOT_ID("The provided slot id is invalid.", HttpStatus.BAD_REQUEST),
	EX_INVALID_ITEM_ID("The provided item id is invalid.", HttpStatus.BAD_REQUEST),
	EX_INVALID_NOTE_ID("The provided note id is invalid.", HttpStatus.BAD_REQUEST),
	EX_INVALID_TECHNICAL_CREW_ID("The provided technical crew id is invalid.", HttpStatus.BAD_REQUEST),

	EX_NO_BROKEN("The provided field broken is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_CONSUMABLE("The provided field consumable is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_DATE("The provided date is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_EMAIL("The provided email address is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_END_DATE("The provided end date is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_END_TIME("The provided end time is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_FIRST_NAME("The provided first name is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_GENDER("The provided gender is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_HAS_BARCODE("The provided field has barcode is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_HELPER_TYPE_ID("The provided helper type id is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_ID("The provided id is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_IDENTIFIER("The provided identifier is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_IMAGE("The provided image is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_ITEM_ID("The provided item id is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_LAST_NAME("The provided last name is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_MEDIA_TYPE("The provided media type is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_NAME("The provided name is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_NEED_ID("The provided need id is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_NOTE("The provided note is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_OUTSIDE_QUALIFIED("The provided field outside qualified is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_OUTSIDE("The provided field outside is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_PROJECT_ID("The provided project id is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_PROJECT_HELPER_TYPE_ID("The provided project helper type id is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_QUANTITY("The provided quanity is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_ROLE("The provided role is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_SLOT_ID("The provided slot id is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_STORE_ID("The provided store id is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_START_DATE("The provided start date is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_START_TIME("The provided start time is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_STATE("The provided state is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_ITEM_TAG_ID("The provided item tag id is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_TECHNICAL_CREW_ID("The provided technical crew id is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_TIMESTAMP("The provided timestamp is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_TYPE("The provided type is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_UNIT("The provided unit is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_USER_ID("The provided user id is empty.", HttpStatus.BAD_REQUEST),
	EX_NO_WEEKDAY("The provided weekday is empty.", HttpStatus.BAD_REQUEST),

	EX_USER_EMAIL_ALREADY_IN_USE("The provided e-mail address is already in use.", HttpStatus.CONFLICT),
	EX_USER_ROLE_ALREADY_EXISTS("The user already has this role.", HttpStatus.CONFLICT),
	EX_USER_SUICIDE("Please don't delete yourself, that hurts!", HttpStatus.NOT_ACCEPTABLE),

	EX_PASSWORDS_DO_NOT_MATCH("The provided passwords do not match.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_NO_TOKEN_OR_OLD_PASSWORD("No token or old password was provided.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_INVALID_TOKEN("The provided token is invalid.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_EXPIRED_TOKEN("The provided token is expired.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_INVALID_PASSWORD("The provided old password is invalid.", HttpStatus.BAD_REQUEST),
	EX_PASSWORDS_SHORT_PASSWORD(
			"The provided password has less than " + PasswordChangeToken.MIN_PASSWORD_LENGTH + " letters.",
			HttpStatus.BAD_REQUEST),

	EX_PROJECT_NAME_ALREADY_EXISTS("A project with the provided name already exists.", HttpStatus.CONFLICT),

	EX_NEED_ALREADY_EXISTS("A need with the provided date and project helper type already exists.",
			HttpStatus.CONFLICT),
	EX_NEED_USER_INVALID_STATE("The provided state is invalid.", HttpStatus.BAD_REQUEST),

	EX_HELPER_TYPE_ALREADY_EXISTS("A helper type with the provided name already exists.", HttpStatus.CONFLICT),
	EX_HELPER_TYPE_WEEKDAY_WITHOUT_PROJECT("Please provide a valid project id with the weekday query.",
			HttpStatus.BAD_REQUEST),

	EX_ITEM_ITEM_TAG_ALREADY_EXISTS("The item already has the provided tag.", HttpStatus.CONFLICT),
	EX_ITEM_IDENTIFIER_ALREADY_IN_USE("The identifier is already in use.", HttpStatus.CONFLICT),
	EX_ITEM_SELF_RELATION("An item can't be related to itself.", HttpStatus.BAD_REQUEST),
	EX_ITEM_RELATION_ALREADY_EXISTS("The items are already related.", HttpStatus.CONFLICT),

	EX_STORE_NOT_EMPTY("The store is not empty.", HttpStatus.BAD_REQUEST),

	EX_SLOT_NOT_EMPTY("The slot is not empty.", HttpStatus.BAD_REQUEST),
	//
	;

	public interface ExceptionEnumWrapper {
		ExceptionEnum getException();
	}

	@Getter
	private String message;
	@Getter
	private HttpStatus httpStatus;

	public DefaultException createDefaultException() {
		return new DefaultException(this);
	}

	public DefaultRuntimeException createDefaultRuntimeException() {
		return new DefaultRuntimeException(this);
	}

	public DefaultException createDefaultException(Throwable cause) {
		return new DefaultException(this, cause);
	}
}
