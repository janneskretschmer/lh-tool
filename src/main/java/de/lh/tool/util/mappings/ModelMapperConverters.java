package de.lh.tool.util.mappings;

import java.time.LocalTime;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public final class ModelMapperConverters {

	private ModelMapperConverters() {
		// this class is just a namespace
	}

	public static class StringToLocalTime implements Converter<String, LocalTime> {

		@Override
		public LocalTime convert(MappingContext<String, LocalTime> context) {
			return LocalTime.parse(context.getSource());
		}
	}
}
