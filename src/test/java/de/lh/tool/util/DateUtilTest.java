package de.lh.tool.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DateUtilTest {

	@Test
	public void testGetReadableFormat() {
		assertEquals("31.12.2020", DateUtil.getReadableFormat(LocalDate.of(2020, 12, 31)));
		assertEquals("01.01.2020", DateUtil.getReadableFormat(LocalDate.of(2020, 01, 01)));
	}

	@Test
	public void testDateWithinRange() {
		assertTrue(DateUtil.isDateWithinRange(LocalDate.of(2020, 02, 29), LocalDate.of(2000, 01, 31),
				LocalDate.of(2020, 12, 31)));
		assertTrue(DateUtil.isDateWithinRange(LocalDate.of(2020, 02, 29), LocalDate.of(2020, 02, 29),
				LocalDate.of(2020, 12, 31)));
		assertTrue(DateUtil.isDateWithinRange(LocalDate.of(2020, 02, 29), LocalDate.of(2000, 01, 31),
				LocalDate.of(2020, 02, 29)));

		assertFalse(DateUtil.isDateWithinRange(LocalDate.of(2020, 02, 29), LocalDate.of(2000, 01, 31),
				LocalDate.of(2020, 02, 28)));
		assertFalse(DateUtil.isDateWithinRange(LocalDate.of(2020, 02, 29), LocalDate.of(2020, 03, 01),
				LocalDate.of(2020, 12, 31)));
	}

}
