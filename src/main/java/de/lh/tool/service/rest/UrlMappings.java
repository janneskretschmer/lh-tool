package de.lh.tool.service.rest;

// Needs to be kept in sync with urlmappings.js

public abstract class UrlMappings {

	public static final String REST_PREFIX = "/rest";

	public static final String ID_VARIABLE = "id";
	public static final String USER_ID_VARIABLE = "user_id";

	public static final String NO_EXTENSION = "/";
	public static final String ID_EXTENSION = "/{" + ID_VARIABLE + "}";
	public static final String ID_USER_ID_EXTENSION = "/{" + ID_VARIABLE + "}/{" + USER_ID_VARIABLE + "}";

	public static final String INFO_PREFIX = REST_PREFIX + "/info";
	public static final String INFO_HEARTBEAT = "/heartbeat";

	public static final String USER_PREFIX = REST_PREFIX + "/users";
	public static final String USER_PASSWORD = "/password";
	public static final String USER_CURRENT = "/current";
	public static final String USER_ROLES = "/{" + ID_VARIABLE + "}/roles";

	public static final String LOGIN_PEFIX = REST_PREFIX + "/login";

	public static final String PROJECT_PREFIX = REST_PREFIX + "/projects";

	public static final String NEED_PREFIX = REST_PREFIX + "/needs";

	public static final String MEDIA_TYPE_JSON = "application/json";

	private UrlMappings() {
		throw new IllegalStateException("class for constants");
	}
}
