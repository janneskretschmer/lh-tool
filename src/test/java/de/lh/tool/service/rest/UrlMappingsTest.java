package de.lh.tool.service.rest;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import de.lh.tool.util.BasicJsSyncTest;

public class UrlMappingsTest extends BasicJsSyncTest<UrlMappings> {
	private final static String URL_MAPPINGS_JS_PATH = "src/main/js/urlmappings.js";

	@Test
	public void testIfInSync() throws FileNotFoundException, IllegalArgumentException, IllegalAccessException {
		testIfInSync(URL_MAPPINGS_JS_PATH, UrlMappings.class);
	}

}
