package de.lh.tool.domain.exception;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import de.lh.tool.util.CodeGenUtilTest.BasicJsSyncTest;

public class ExceptionEnumTest extends BasicJsSyncTest<ExceptionEnum> {
	private final static String EXCEPTIONS_JS_PATH = "src/main/js/exceptions.js";

	@Test
	public void testIfInSync() throws FileNotFoundException, IllegalAccessException {
		assertTrue(testIfInSync(EXCEPTIONS_JS_PATH, ExceptionEnum.class));
	}

}
