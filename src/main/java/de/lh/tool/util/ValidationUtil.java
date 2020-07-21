package de.lh.tool.util;

import java.util.Arrays;
import java.util.Objects;

import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ValidationUtil {
	private ValidationUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static void checkIdsNonNull(Long... ids) throws DefaultException {
		checkAllNonNull(ExceptionEnum.EX_NO_ID_PROVIDED, (Object[]) ids);
	}

	public static void checkAllNonNull(ExceptionEnum exception, Object... ids) throws DefaultException {
		if (!Arrays.stream(ids).allMatch(Objects::nonNull)) {
			throw exception.createDefaultException();
		}
	}
}