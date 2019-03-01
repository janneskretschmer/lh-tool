package de.lh.tool.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "de.lh.tool" })
public class ApplicationConfiguration {

	@Value("${app.base}")
	private String baseUrl;

	@Bean(name = "baseUrl")
	public String getBaseUrl() {
		return baseUrl;
	}
}
