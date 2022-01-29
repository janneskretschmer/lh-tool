package de.lh.tool.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import de.lh.tool.util.mappings.ModelMapperConverters;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "de.lh.tool" })
public class ApplicationConfiguration {
	@Bean
	public ModelMapper getModelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
		modelMapper.addConverter(new ModelMapperConverters.StringToLocalTime());

		return modelMapper;
	}
}
