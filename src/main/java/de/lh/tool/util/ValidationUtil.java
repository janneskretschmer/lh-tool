package de.lh.tool.util;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import de.lh.tool.domain.Identifiable;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import lombok.NonNull;

public class ValidationUtil {
	private ValidationUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static void checkNonBlank(@NonNull ExceptionEnum exception, Object object) throws DefaultException {
		if (object == null || (object instanceof CharSequence && StringUtils.isBlank((CharSequence) object))) {
			throw exception.createDefaultException();
		}
	}

	public static <E extends Identifiable<?>> void checkSameIdIfExists(@NonNull ExceptionEnum exception,
			Optional<E> existingEntity, E newEntity) throws DefaultException {
		if (existingEntity.map(Identifiable::getId).map(id -> !id.equals(newEntity.getId())).orElse(false)) {
			throw exception.createDefaultException();
		}
	}
}
