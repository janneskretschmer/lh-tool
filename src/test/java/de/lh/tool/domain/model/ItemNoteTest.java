package de.lh.tool.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ItemNoteTest {
	@Test
	public void testCompareTo() {
		// the newer, the higher in the list of notes
		ItemNote lower = ItemNote.builder().timestamp(LocalDateTime.of(2020, 1, 1, 10, 10)).build();
		ItemNote same = ItemNote.builder().timestamp(LocalDateTime.of(2020, 1, 1, 10, 10)).build();
		ItemNote greater = ItemNote.builder().timestamp(LocalDateTime.of(2020, 1, 1, 10, 9)).build();

		assertEquals(-1, lower.compareTo(greater));
		assertEquals(1, greater.compareTo(lower));
		assertEquals(0, lower.compareTo(same));
		assertEquals(0, same.compareTo(same));

		assertEquals(1, lower.compareTo(null));
	}
}
