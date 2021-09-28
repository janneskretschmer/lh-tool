package de.lh.tool.util.mappings;

import java.time.LocalTime;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public abstract class ModelMapperConverters {
	public static class StringToLocalTime implements Converter<String, LocalTime> {

		@Override
		public LocalTime convert(MappingContext<String, LocalTime> context) {
			return LocalTime.parse(context.getSource());
		}
	}
}
