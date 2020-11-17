package de.lh.tool.domain.model;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import de.lh.tool.util.CodeGenUtilTest;

public class UserRoleTest extends CodeGenUtilTest.BasicJsSyncTest<UserRole> {

	private final static String PERMISSIONS_JS_PATH = "src/main/js/permissions.js";

	@Test
	public void testConsitency() throws IllegalArgumentException, IllegalAccessException {
		for (Field field : UserRole.class.getFields()) {
			String name = field.getName();
			if (name.startsWith("RIGHT_")) {
				if (!name.startsWith("ROLE_")) {
					name = "ROLE_" + name;
				}
				assertEquals(name, field.get(null));
			}
		}
	}

	@Test
	public void testIfInSync() throws FileNotFoundException, IllegalAccessException {
		assertTrue(testIfInSync(PERMISSIONS_JS_PATH, UserRole.class));
	}

}
