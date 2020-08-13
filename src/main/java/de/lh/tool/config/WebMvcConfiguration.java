package de.lh.tool.config;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@Configuration
@ComponentScan(basePackages = { "de.lh.tool", "springfox.documentation.swagger.web" })
@EnableWebMvc
class WebMvcConfiguration implements WebMvcConfigurer {

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

	@Bean
	public ViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
		internalResourceViewResolver.setPrefix("/WEB-INF/views/");
		internalResourceViewResolver.setSuffix(".jsp");
		return internalResourceViewResolver;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		builder.serializers(new JsonSerializer<LocalDateTime>() {
			@Override
			public Class<LocalDateTime> handledType() {
				return LocalDateTime.class;
			}

			@Override
			public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
					throws IOException {
				gen.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			}
		});
		builder.serializers(new JsonSerializer<LocalDate>() {
			@Override
			public Class<LocalDate> handledType() {
				return LocalDate.class;
			}

			@Override
			public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers)
					throws IOException {
				gen.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
			}
		});
		builder.serializers(new JsonSerializer<LocalTime>() {
			@Override
			public Class<LocalTime> handledType() {
				return LocalTime.class;
			}

			@Override
			public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers)
					throws IOException {
				gen.writeString(value.format(DateTimeFormatter.ISO_LOCAL_TIME));
			}
		});
		converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
	}

}
