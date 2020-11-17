package de.lh.tool.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class StringUtilTest {
	@Test
	public void testConstantTimeEquals() {
		assertTrue(StringUtil.constantTimeEquals(null, null));
		assertTrue(StringUtil.constantTimeEquals("Test", "Test"));
		assertFalse(StringUtil.constantTimeEquals("Testa", "Testb"));
		assertFalse(StringUtil.constantTimeEquals("Testa", "Test"));
		assertFalse(StringUtil.constantTimeEquals("Test", "Testb"));
		assertFalse(StringUtil.constantTimeEquals(null, "Test"));
		assertFalse(StringUtil.constantTimeEquals("Test", null));
	}
}
