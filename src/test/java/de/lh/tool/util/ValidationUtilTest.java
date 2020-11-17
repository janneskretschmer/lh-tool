package de.lh.tool.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.lh.tool.domain.Identifiable;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

public class ValidationUtilTest {

	@Test
	public void testCheckNonBlank() {
		assertDoesNotThrow(() -> ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ID, 1l));
		assertDoesNotThrow(() -> ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ID, new Object()));
		assertDoesNotThrow(() -> ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ID, "a"));
		assertDoesNotThrow(() -> ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ID, "   0   "));

		ExceptionEnum noIdException1 = assertThrows(DefaultException.class,
				() -> ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ID, (Long) null)).getException();
		assertEquals(ExceptionEnum.EX_NO_ID, noIdException1);
		ExceptionEnum noIdException2 = assertThrows(DefaultException.class,
				() -> ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ID, "")).getException();
		assertEquals(ExceptionEnum.EX_NO_ID, noIdException2);
		ExceptionEnum noIdException3 = assertThrows(DefaultException.class,
				() -> ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ID, "    \n   ")).getException();
		assertEquals(ExceptionEnum.EX_NO_ID, noIdException3);
	}

	@Test
	public void testCheckSameIdIfExists() {
		assertDoesNotThrow(() -> ValidationUtil.checkSameIdIfExists(ExceptionEnum.EX_HELPER_TYPE_ALREADY_EXISTS,
				Optional.empty(), new TestIdentifiable(null)));
		assertDoesNotThrow(() -> ValidationUtil.checkSameIdIfExists(ExceptionEnum.EX_HELPER_TYPE_ALREADY_EXISTS,
				Optional.empty(), new TestIdentifiable(1l)));
		assertDoesNotThrow(() -> ValidationUtil.checkSameIdIfExists(ExceptionEnum.EX_HELPER_TYPE_ALREADY_EXISTS,
				Optional.of(new TestIdentifiable(1l)), new TestIdentifiable(1l)));
		ExceptionEnum exception = assertThrows(DefaultException.class,
				() -> ValidationUtil.checkSameIdIfExists(ExceptionEnum.EX_HELPER_TYPE_ALREADY_EXISTS,
						Optional.of(new TestIdentifiable(1l)), new TestIdentifiable(2l))).getException();
		assertEquals(ExceptionEnum.EX_HELPER_TYPE_ALREADY_EXISTS, exception);
	}

	@Data
	@AllArgsConstructor
	private class TestIdentifiable implements Identifiable<Long> {
		private Long id;
	}
}
