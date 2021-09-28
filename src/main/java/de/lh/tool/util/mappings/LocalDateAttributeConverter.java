package de.lh.tool.util.mappings;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date> {

	@Override
	public Date convertToDatabaseColumn(LocalDate localDate) {
		return Optional.ofNullable(localDate).map(Date::valueOf).orElse(null);
	}

	@Override
	public LocalDate convertToEntityAttribute(Date sqlDate) {
		return Optional.ofNullable(sqlDate).map(Date::toLocalDate).orElse(null);
	}
}