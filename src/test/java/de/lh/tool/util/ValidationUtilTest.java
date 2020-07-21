package de.lh.tool.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;

public class ValidationUtilTest {

	@Test
	public void testCheckNonNull() {
		assertDoesNotThrow(() -> ValidationUtil.checkIdsNonNull(1l));
		assertDoesNotThrow(() -> ValidationUtil.checkIdsNonNull(0l, 1231l));
		assertDoesNotThrow(() -> ValidationUtil.checkAllNonNull(ExceptionEnum.EX_HELPER_TYPE_WEEKDAY_WITHOUT_PROJECT,
				-1l, 0l, 1l, "test"));

		ExceptionEnum noIdException1 = assertThrows(DefaultException.class,
				() -> ValidationUtil.checkIdsNonNull((Long) null)).getException();
		assertEquals(ExceptionEnum.EX_NO_ID_PROVIDED, noIdException1);
		ExceptionEnum noIdException2 = assertThrows(DefaultException.class,
				() -> ValidationUtil.checkIdsNonNull(1l, 2l, (Long) null, 3l)).getException();
		assertEquals(ExceptionEnum.EX_NO_ID_PROVIDED, noIdException2);

		ExceptionEnum customException = assertThrows(DefaultException.class, () -> ValidationUtil
				.checkAllNonNull(ExceptionEnum.EX_PASSWORDS_NO_TOKEN_OR_OLD_PASSWORD, (Object) null)).getException();
		assertEquals(ExceptionEnum.EX_PASSWORDS_NO_TOKEN_OR_OLD_PASSWORD, customException);
		ExceptionEnum customException2 = assertThrows(DefaultException.class,
				() -> ValidationUtil.checkAllNonNull(ExceptionEnum.EX_HELPER_TYPE_WEEKDAY_WITHOUT_PROJECT, -1l, 0l, 1l,
						(Object) null, "test")).getException();
		assertEquals(ExceptionEnum.EX_HELPER_TYPE_WEEKDAY_WITHOUT_PROJECT, customException2);
	}
}
