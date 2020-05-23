package de.lh.tool.service.rest;

// Needs to be kept in sync with urlmappings.js

public abstract class UrlMappings {

	public static final String REST_PREFIX = "/rest";

	public static final String ID_VARIABLE = "id";
	public static final String USER_ID_VARIABLE = "user_id";
	public static final String PROJECT_ID_VARIABLE = "project_id";
	public static final String HELPER_TYPE_ID_VARIABLE = "helper_type_id";
	public static final String PROJECT_HELPER_TYPE_ID_VARIABLE = "project_helper_type_id";

	public static final String DATE_VARIABLE = "date";
	public static final String ROLE_VARIABLE = "role";
	public static final String STORE_VARIABLE = "store";
	public static final String WEEKDAY_VARIABLE = "weekday";
	public static final String START_DATE_VARIABLE = "start_date";
	public static final String END_DATE_VARIABLE = "end_date";

	public static final String NO_EXTENSION = "";
	public static final String ID_EXTENSION = "/{" + ID_VARIABLE + "}";
	public static final String ID_USER_EXTENSION = ID_EXTENSION + "/users";
	public static final String ID_USER_ID_EXTENSION = ID_USER_EXTENSION + "/{" + USER_ID_VARIABLE + "}";

	public static final String INFO_PREFIX = REST_PREFIX + "/info";
	public static final String INFO_HEARTBEAT = "/heartbeat";
	public static final String INFO_TIMEZONE = "/timezone";

	public static final String USER_PREFIX = REST_PREFIX + "/users";
	public static final String USER_PASSWORD = "/password";
	public static final String USER_CURRENT = "/current";
	public static final String USER_ROLES = "/{" + ID_VARIABLE + "}/roles";
	public static final String USER_ROLES_ID = "/{" + USER_ID_VARIABLE + "}/roles/{" + ID_VARIABLE + "}";

	public static final String ROLES_PREFIX = REST_PREFIX + "/roles";

	public static final String LOGIN_PREFIX = REST_PREFIX + "/login";
	public static final String LOGIN_PASSWORD_RESET = "/pwreset";

	public static final String PROJECT_PREFIX = REST_PREFIX + "/projects";
	public static final String PROJECT_DELETE = PROJECT_PREFIX + ID_EXTENSION;
	public static final String PROJECT_HELPER_TYPES = ID_EXTENSION + "/helper_types/{" + HELPER_TYPE_ID_VARIABLE + "}";

	public static final String HELPER_TYPE_PREFIX = REST_PREFIX + "/helper_types";

	public static final String NEED_PREFIX = REST_PREFIX + "/needs";

	public static final String STORE_PREFIX = REST_PREFIX + "/stores";
	public static final String STORE_PROJECTS = ID_EXTENSION + "/projects";

	public static final String SLOT_PREFIX = REST_PREFIX + "/slots";

	public static final String ITEM_PREFIX = REST_PREFIX + "/items";
	public static final String ITEM_NOTES = ID_EXTENSION + "/notes";
	public static final String ITEM_TAGS = ID_EXTENSION + "/tags";
	public static final String ITEM_HISTORY = ID_EXTENSION + "/history";

	public static final String TECHNICAL_CREW_PREFIX = REST_PREFIX + "/technical_crews";

	public static final String MEDIA_TYPE_JSON = "application/json";

	// for the frontend optimized endpoints that would result in performance issues
	// not strictly compliant to the REST philosophy -.-
	public static final String ASSEMBLED_PREFIX = REST_PREFIX + "/assembled";
	public static final String ASSEMBLED_NEED_FOR_CALENDAR = "/needs_calendar";

	private UrlMappings() {
		throw new IllegalStateException("class for constants");
	}
}
