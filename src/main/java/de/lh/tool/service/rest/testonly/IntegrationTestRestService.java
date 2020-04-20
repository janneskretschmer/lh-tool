package de.lh.tool.service.rest.testonly;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.rest.testonly.dto.DatabaseValidationResult;

/**
 * !!! Must never get deployed !!! It gets deleted in deploy.sh => should only
 * be used for test purposes
 */
@RestController()
@RequestMapping("/rest/testonly/integration")
@PropertySource(value = { "classpath:credentials.properties" })
public class IntegrationTestRestService {

	@Autowired
	private Environment environment;

	@PersistenceContext
	private EntityManager entityManager;

	private void checkEnvironment() throws DefaultException {
		String env = environment.getProperty("app.environment");
		if (!"test".equals(env)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
	}

	/**
	 * should only be used for test purposes
	 * 
	 * @throws DefaultException
	 */
	@Transactional
	@PostMapping("/database/initialize")
	public int initializeDatabase(@RequestBody List<String> queries) throws DefaultException {
		checkEnvironment();
		return queries.stream().mapToInt(query -> entityManager.createNativeQuery(query).executeUpdate()).sum();
	}

	/**
	 * should only be used for test purposes
	 * 
	 * @throws DefaultException
	 */
	@Transactional
	@PostMapping("/database/validate")
	public DatabaseValidationResult validateDatabase(@RequestBody List<String> queries) throws DefaultException {
		checkEnvironment();
		List<String> failingQueries = queries.stream()
				.filter(query -> entityManager.createNativeQuery(query).getResultList().isEmpty())
				.collect(Collectors.toList());
		return new DatabaseValidationResult(failingQueries);
	}

	/**
	 * should only be used for test purposes
	 * 
	 * @throws DefaultException
	 */
	@Transactional
	@GetMapping("/database/reset")
	public int resetDatabase() throws DefaultException {
		checkEnvironment();
		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=0").executeUpdate();
		int result = entityManager.createNativeQuery("SHOW TABLES").getResultStream()
				.filter(tableName -> !"schema_version".equalsIgnoreCase(tableName.toString()))
				.mapToInt(tableName -> entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate())
				.sum();
		entityManager.unwrap(Session.class).clear();
		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=1").executeUpdate();

		AtomicInteger id = new AtomicInteger(0);
		getRoleStream().forEach(role -> createUser(id.incrementAndGet(), role));

		return result;
	}

	private static Stream<String> getRoleStream() {
		return Arrays.stream(UserRole.class.getFields()).map(Field::getName)
				.filter(fieldName -> fieldName.startsWith("ROLE_")).map(role -> role.substring(5).toLowerCase());
	}

	private void createUser(int id, String role) {
		entityManager.createNativeQuery(
				"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`) VALUES"
						+ "(:id, 'Test', :capitalized, 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', :email)")
				.setParameter("id", id).setParameter("capitalized", StringUtils.capitalize(role))
				.setParameter("email", "test-" + role + "@lh-tool.de").executeUpdate();
		entityManager.createNativeQuery("INSERT INTO `user_role` (`id`, `user_id`, `role`) VALUES (:id, :id, :role)")
				.setParameter("id", id).setParameter("role", "ROLE_" + role.toUpperCase()).executeUpdate();
	}

	public static List<String> getDefaultEmails() {
		return getRoleStream().map(role -> StringUtils.join("test-", role, "@lh-tool.de")).collect(Collectors.toList());
	}

	@GetMapping("/shutdown")
	public void goodBye() throws DefaultException {
		checkEnvironment();
		System.exit(0);
	}
}
