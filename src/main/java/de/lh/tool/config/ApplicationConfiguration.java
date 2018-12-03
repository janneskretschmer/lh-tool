package de.lh.tool.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "de.lh.tool.service.rest", "de.lh.tool.web" })
public class ApplicationConfiguration {
}
