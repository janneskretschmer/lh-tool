package de.lh.tool.util.mappings;

import java.io.IOException;
import java.time.LocalDate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public abstract class JsonDeserializers {
	public static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

		@Override
		public Class<?> handledType() {
			return LocalDate.class;
		}

		@Override
		public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return LocalDate.parse(p.readValueAs(String.class));
		}
	}
}
