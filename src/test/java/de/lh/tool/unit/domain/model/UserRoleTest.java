package de.lh.tool.unit.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import de.lh.tool.domain.model.UserRole;

public class UserRoleTest {

	@Test
	public void testConsitency() throws IllegalArgumentException, IllegalAccessException {
		for (Field field : UserRole.class.getFields()) {
			String name = field.getName();
			if (!name.startsWith("ROLE_")) {
				name = "ROLE_" + name;
			}
			assertEquals(name, field.get(null));
		}
	}
}
