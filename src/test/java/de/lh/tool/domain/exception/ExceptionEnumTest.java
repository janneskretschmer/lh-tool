package de.lh.tool.domain.exception;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import de.lh.tool.util.BasicJsSyncTest;

public class ExceptionEnumTest extends BasicJsSyncTest<ExceptionEnum> {
	private final static String EXCEPTIONS_JS_PATH = "src/main/js/exceptions.js";

	@Test
	public void testIfInSync() throws FileNotFoundException, IllegalAccessException {
		testIfInSync(EXCEPTIONS_JS_PATH, ExceptionEnum.class);
	}

}
