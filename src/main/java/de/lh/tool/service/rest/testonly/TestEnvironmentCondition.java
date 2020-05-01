package de.lh.tool.service.rest.testonly;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class TestEnvironmentCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		// context.getEnvironment() doesn't have the property source
		Resource resource = new ClassPathResource("credentials.properties");
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			return "test".equals(props.getProperty("app.environment"));
		} catch (IOException e) {
			return false;
		}
	}
}
