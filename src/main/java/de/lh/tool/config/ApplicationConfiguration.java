package de.lh.tool.config;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "de.lh.tool" })
public class ApplicationConfiguration {
	@Bean
	public ModelMapper getModelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
		modelMapper.addConverter(new Converter<String, LocalTime>() {

			@Override
			public LocalTime convert(MappingContext<String, LocalTime> context) {
				return LocalTime.parse(context.getSource());
			}
		});
		// FUTURE use new time api everywhere
		modelMapper.addConverter(new AbstractConverter<Date, LocalDate>() {
			@Override
			protected LocalDate convert(Date date) {
				return Optional.ofNullable(date).map(Date::getTime).map(Instant::ofEpochMilli)
						.map(instant -> instant.atZone(ZoneId.systemDefault()).toLocalDate()).orElse(null);
			}
		});
		// FUTURE use new time api everywhere
		modelMapper.addConverter(new AbstractConverter<LocalDate, Date>() {
			@Override
			protected Date convert(LocalDate localDate) {
				return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			}
		});

		return modelMapper;
	}
}
