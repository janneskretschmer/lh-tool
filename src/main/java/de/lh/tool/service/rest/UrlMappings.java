package de.lh.tool.service.rest;

public abstract class URLMappings {

	private URLMappings() {
		throw new IllegalStateException("class for constants");
	}

	public static final String REST_PREFIX = "/rest";

	public static final String INFO_PREFIX = REST_PREFIX + "/info";
	public static final String INFO_HEARTBEAT = "/heartbeat";

	public static final String ADMIN_PREFIX = REST_PREFIX + "/admin";
	public static final String ADMIN_DBUPDATE = "/dbupdate";
}
