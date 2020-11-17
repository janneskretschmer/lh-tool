package de.lh.tool.service.rest;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import de.lh.tool.util.CodeGenUtilTest.BasicJsSyncTest;

public class UrlMappingsTest extends BasicJsSyncTest<UrlMappings> {
	private final static String URL_MAPPINGS_JS_PATH = "src/main/js/urlmappings.js";

	@Test
	public void testIfInSync() throws FileNotFoundException, IllegalArgumentException, IllegalAccessException {
		assertTrue(testIfInSync(URL_MAPPINGS_JS_PATH, UrlMappings.class));
	}

}
