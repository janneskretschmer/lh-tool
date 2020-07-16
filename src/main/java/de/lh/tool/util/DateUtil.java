package de.lh.tool.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.lang3.ObjectUtils;

public class DateUtil {

	private DateUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static String getReadableFormat(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		return simpleDateFormat.format(date);
	}

	public static String getReadableFormat(LocalDate date) {
		DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		return date.format(simpleDateFormat);
	}

	/**
	 * @param date
	 * @param startInclusive
	 * @param endInclusive
	 * @return true if startInclusive <= date <= endInclusive
	 */
	public static boolean isDateWithinRange(LocalDate date, LocalDate startInclusive, LocalDate endInclusive) {
		if (ObjectUtils.allNotNull(date, startInclusive, endInclusive)) {
			return !date.isBefore(startInclusive) && !date.isAfter(endInclusive);
		}
		return false;
	}

}
