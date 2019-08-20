package de.lh.tool.service.rest;

// Needs to be kept in sync with urlmappings.js

public abstract class UrlMappings {

	public static final String REST_PREFIX = "/rest";

	public static final String ID_VARIABLE = "id";
	public static final String USER_ID_VARIABLE = "user_id";
	public static final String PROJECT_ID_VARIABLE = "project_id";
	public static final String ROLE_VARIABLE = "role";

	public static final String NO_EXTENSION = "/";
	public static final String ID_EXTENSION = "/{" + ID_VARIABLE + "}";
	public static final String ID_USER_ID_EXTENSION = "/{" + ID_VARIABLE + "}/{" + USER_ID_VARIABLE + "}";

	public static final String INFO_PREFIX = REST_PREFIX + "/info";
	public static final String INFO_HEARTBEAT = "/heartbeat";

	public static final String USER_PREFIX = REST_PREFIX + "/users";
	public static final String USER_PASSWORD = "/password";
	public static final String USER_CURRENT = "/current";
	public static final String USER_ROLES = "/{" + ID_VARIABLE + "}/roles";

	public static final String LOGIN_PREFIX = REST_PREFIX + "/login";
	public static final String LOGIN_PASSWORD_RESET = "/pwreset";

	public static final String PROJECT_PREFIX = REST_PREFIX + "/projects";
	public static final String PROJECT_DELETE = PROJECT_PREFIX + ID_EXTENSION;

	public static final String NEED_PREFIX = REST_PREFIX + "/needs";
	public static final String NEED_START_DIFF_VARIABLE = "start_diff";
	public static final String NEED_END_DIFF_VARIABLE = "end_diff";

	public static final String STORE_PREFIX = REST_PREFIX + "/stores";
	public static final String STORE_PROJECTS = ID_EXTENSION + "/projects";

	public static final String MEDIA_TYPE_JSON = "application/json";

	private UrlMappings() {
		throw new IllegalStateException("class for constants");
	}
}
