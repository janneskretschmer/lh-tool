package de.lh.tool.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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

}
