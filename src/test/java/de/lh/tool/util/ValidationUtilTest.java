package de.lh.tool.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;

public class ValidationUtilTest {

	@Test
	public void testCheckIdNull() {
		assertDoesNotThrow(() -> ValidationUtil.checkIdNull(1l));
		assertDoesNotThrow(() -> ValidationUtil.checkIdNull(0l, 1231l));
		assertDoesNotThrow(
				() -> ValidationUtil.checkIdNull(ExceptionEnum.EX_HELPER_TYPE_WEEKDAY_WITHOUT_PROJECT, -1l, 0l, 1l));

		ExceptionEnum noIdException1 = assertThrows(DefaultException.class,
				() -> ValidationUtil.checkIdNull((Long) null)).getException();
		assertEquals(ExceptionEnum.EX_NO_ID_PROVIDED, noIdException1);
		ExceptionEnum noIdException2 = assertThrows(DefaultException.class,
				() -> ValidationUtil.checkIdNull(1l, 2l, (Long) null, 3l)).getException();
		assertEquals(ExceptionEnum.EX_NO_ID_PROVIDED, noIdException2);

		ExceptionEnum customException = assertThrows(DefaultException.class,
				() -> ValidationUtil.checkIdNull(ExceptionEnum.EX_HELPER_TYPE_WEEKDAY_WITHOUT_PROJECT, (Long) null))
						.getException();
		assertEquals(ExceptionEnum.EX_HELPER_TYPE_WEEKDAY_WITHOUT_PROJECT, customException);
	}
}
