package de.lh.tool.util.mappings;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public abstract class JsonSerializers {
	public static class LocalDateSerializer extends JsonSerializer<LocalDate> {
		@Override
		public Class<LocalDate> handledType() {
			return LocalDate.class;
		}

		@Override
		public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeString(value.format(DateTimeFormatter.ISO_DATE));
		}
	}

	public static class LocalTimeSerializer extends JsonSerializer<LocalTime> {
		@Override
		public Class<LocalTime> handledType() {
			return LocalTime.class;
		}

		@Override
		public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeString(value.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_TIME));
		}
	}

	public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
		@Override
		public Class<LocalDateTime> handledType() {
			return LocalDateTime.class;
		}

		@Override
		public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
				throws IOException {
			gen.writeString(value.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME));
		}
	}
}
