package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;

import de.lh.tool.domain.dto.JwtAuthenticationDto;
import de.lh.tool.domain.dto.LoginDto;
import de.lh.tool.domain.dto.PasswordChangeDto;
import de.lh.tool.domain.dto.ProjectDto;
import de.lh.tool.domain.dto.UserCreationDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.dto.UserRolesDto;
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.User.Gender;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.rest.bean.EndpointTest;
import de.lh.tool.rest.bean.UserTest;
import de.lh.tool.service.rest.testonly.IntegrationTestRestService;
import de.lh.tool.service.rest.testonly.dto.DatabaseValidationResult;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public abstract class BasicRestIntegrationTest {
	private static final String PASSWORD = "testing";

	protected static final String ADMIN_EMAIL = "test-admin@lh-tool.de";
	protected static final String CONSTRUCTION_SERVANT_EMAIL = "test-construction_servant@lh-tool.de";
	protected static final String LOCAL_COORDINATOR_EMAIL = "test-local_coordinator@lh-tool.de";
	protected static final String ATTENDANCE_EMAIL = "test-attendance@lh-tool.de";
	protected static final String PUBLISHER_EMAIL = "test-publisher@lh-tool.de";
	protected static final String STORE_KEEPER_EMAIL = "test-store_keeper@lh-tool.de";
	protected static final String INVENTORY_MANAGER_EMAIL = "test-inventory_manager@lh-tool.de";

	protected static final String CONSTRUCTION_SERVANT_1_EMAIL = "test-construction-servant1@lh-tool.de";
	protected static final String CONSTRUCTION_SERVANT_2_EMAIL = "test-construction-servant2@lh-tool.de";
	protected static final String LOCAL_COORDINATOR_1_EMAIL = "test-local-coordinator1@lh-tool.de";
	protected static final String LOCAL_COORDINATOR_2_EMAIL = "test-local-coordinator2@lh-tool.de";
	protected static final String PUBLISHER_1_EMAIL = "test-publisher1@lh-tool.de";
	protected static final String PUBLISHER_2_EMAIL = "test-publisher2@lh-tool.de";
	protected static final String STORE_KEEPER_1_EMAIL = "test-store-keeper1@lh-tool.de";
	protected static final String STORE_KEEPER_2_EMAIL = "test-store-keeper2@lh-tool.de";
	protected static final String INVENTORY_MANAGER_1_EMAIL = "test-inventory-manager1@lh-tool.de";
	protected static final String INVENTORY_MANAGER_2_EMAIL = "test-inventory-manager2@lh-tool.de";
	protected static final List<User> TEST_USERS = List.of(
			User.builder().email(CONSTRUCTION_SERVANT_1_EMAIL).firstName("Construction").lastName("Servant1")
					.roles(List.of(new UserRole(UserRole.ROLE_CONSTRUCTION_SERVANT))).build(),
			User.builder().email(CONSTRUCTION_SERVANT_2_EMAIL).firstName("Construction").lastName("Servant2")
					.roles(List.of(new UserRole(UserRole.ROLE_CONSTRUCTION_SERVANT))).build(),
			User.builder().email(LOCAL_COORDINATOR_1_EMAIL).firstName("Local").lastName("Coordinator1")
					.roles(List.of(new UserRole(UserRole.ROLE_LOCAL_COORDINATOR))).build(),
			User.builder().email(LOCAL_COORDINATOR_2_EMAIL).firstName("Local").lastName("Coordinator2")
					.roles(List.of(new UserRole(UserRole.ROLE_LOCAL_COORDINATOR))).build(),
			User.builder().email(PUBLISHER_1_EMAIL).firstName("Pub").lastName("Lisher1")
					.roles(List.of(new UserRole(UserRole.ROLE_PUBLISHER))).build(),
			User.builder().email(PUBLISHER_2_EMAIL).firstName("Pub").lastName("Lisher2")
					.roles(List.of(new UserRole(UserRole.ROLE_PUBLISHER))).build(),
			User.builder().email(STORE_KEEPER_1_EMAIL).firstName("Store").lastName("Keeper1")
					.roles(List.of(new UserRole(UserRole.ROLE_STORE_KEEPER))).build(),
			User.builder().email(STORE_KEEPER_2_EMAIL).firstName("Store").lastName("Keeper2")
					.roles(List.of(new UserRole(UserRole.ROLE_STORE_KEEPER))).build(),
			User.builder().email(INVENTORY_MANAGER_1_EMAIL).firstName("Inventory").lastName("Manager1")
					.roles(List.of(new UserRole(UserRole.ROLE_INVENTORY_MANAGER))).build(),
			User.builder().email(INVENTORY_MANAGER_2_EMAIL).firstName("Inventory").lastName("Manager2")
					.roles(List.of(new UserRole(UserRole.ROLE_INVENTORY_MANAGER))).build());

	private Map<String, String> jwtCache = new HashMap<>();

	private static final int TIMEOUT = 30000;
	protected static final String REST_URL = "http://localhost:8080/lh-tool/rest";

	@BeforeAll
	protected static void waitForLocalTomcat() {
		long timeout = System.currentTimeMillis() + TIMEOUT;
		while (System.currentTimeMillis() < timeout) {
			try {
				RestAssured.when().get(REST_URL + "/info/heartbeat").then().statusCode(200);
				return;
			} catch (Exception e) {
				System.out.println("Connect failed, waiting and trying again");
				try {
					Thread.sleep(2000);// 2 seconds
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
	}

	protected String getJwtByEmail(String email) {
		if (!jwtCache.containsKey(email)) {
			String url = REST_URL + "/login/";
			JwtAuthenticationDto res = RestAssured.given().body(new LoginDto(email, PASSWORD))
					.contentType(ContentType.JSON).when().post(url).as(JwtAuthenticationDto.class);
			jwtCache.put(email, res != null ? res.getAccessToken() : null);
		}
		return jwtCache.get(email);
	}

	protected RequestSpecification getRequestSpecWithJwt(String jwt) {
		return RestAssured.given().header("Authorization", "Bearer " + jwt);
	}

	protected RequestSpecification getRequestSpecWithJwtByEmail(String email) {
		return getRequestSpecWithJwt(getJwtByEmail(email));
	}

	protected Long getUserIdByEmail(String email) {
		return getRequestSpecWithJwtByEmail(email).get(REST_URL + "/users/current").as(UserDto.class).getId();
	}

	protected void createTestUsers() throws Exception {
		deleteTestUsers();
		deleteTestProjects();

		String jwt = getJwtByEmail(ADMIN_EMAIL);
		assertNotNull(jwt);
		String registrationUrl = REST_URL + "/users/";
		String passwordUrl = REST_URL + "/users/password";
		for (User user : TEST_USERS) {
			Long userId = getRequestSpecWithJwt(jwt)
					.body(UserCreationDto.builder().firstName(user.getFirstName()).lastName(user.getLastName())
							.email(user.getEmail()).gender(Gender.MALE.name()).telephoneNumber("+49 123456789")
							.mobileNumber("+49 87654321").businessNumber("+49 123454321").build())
					.contentType(ContentType.JSON).post(registrationUrl).as(UserDto.class).getId();
			getRequestSpecWithJwt(jwt).body(
					PasswordChangeDto.builder().userId(userId).newPassword(PASSWORD).confirmPassword(PASSWORD).build())
					.contentType(ContentType.JSON).put(passwordUrl).then().statusCode(200);
			getRequestSpecWithJwt(jwt)
					.body(new UserRolesDto(
							user.getRoles().stream().map(UserRole::getRole).collect(Collectors.toList())))
					.contentType(ContentType.JSON).put(registrationUrl + userId + "/roles").then().statusCode(200);
		}
	}

	protected void deleteTestUsers() {
		String jwt = getJwtByEmail(ADMIN_EMAIL);
		assertNotNull(jwt);
		String url = REST_URL + "/users/";
		List<UserDto> users = getRequestSpecWithJwt(jwt).get(url).then().extract().jsonPath().getList("content",
				UserDto.class);
		List<String> emails = TEST_USERS.stream().map(User::getEmail).collect(Collectors.toList());
		for (UserDto user : users) {
			if (emails.contains(user.getEmail())) {
				getRequestSpecWithJwt(jwt).delete(url + user.getId()).then().statusCode(204);
			}
		}
	}

	protected void deleteTestProjects() {
		String jwt = getJwtByEmail(ADMIN_EMAIL);
		assertNotNull(jwt);
		String url = REST_URL + "/projects/";
		List<ProjectDto> projects = getRequestSpecWithJwt(jwt).get(url).then().extract().jsonPath().getList("content",
				ProjectDto.class);
		projects.stream().filter(p -> p.getName().startsWith("Test"))
				.forEach(p -> getRequestSpecWithJwt(jwt).delete(url + p.getId()).then().statusCode(204));
	}

	protected void testForUsers(Consumer<RequestSpecification> consumer, String... emails) {
		for (String email : emails) {
			consumer.accept(getRequestSpecWithJwtByEmail(email));
		}
	}

	protected boolean testEndpoint(EndpointTest endpointTest) throws IOException {
		RestAssured.defaultParser = Parser.JSON;

		List<String> defaultEmails = IntegrationTestRestService.getDefaultEmails();

		endpointTest.getUserTests().forEach(userTest -> {
			userTest.getEmails().forEach(email -> {
				defaultEmails.remove(email);
				testEndpointForUser(endpointTest, userTest, email);
			});
		});

		defaultEmails.forEach(email -> testEndpointForUser(endpointTest,
				UserTest.builder().expectedHttpCode(endpointTest.getHttpCodeForOthers())
						.expectedResponse(endpointTest.getResponseForOthers())
						.validationQueries(endpointTest.getValidationQueriesForOthers()).build(),
				email));
		// codacy wants to have at least one assertion in every test method.
		return true;

	}

	private void testEndpointForUser(EndpointTest endpointTest, UserTest userTest, String email) {
		resetDatabase();
		initializeDatabase(endpointTest);

		RequestSpecification requestSepcification = getRequestSpecWithJwtByEmail(email).contentType(ContentType.JSON);
		if (endpointTest.getBody() != null) {
			requestSepcification = requestSepcification.body(endpointTest.getBody());
		}

		Response response = requestSepcification.request(endpointTest.getMethod(), endpointTest.getUrl());

		String message = getAsssertFailedMessage(endpointTest, email);

		validateResponse(userTest, email, response, message);
	}

	private void initializeDatabase(EndpointTest endpointTest) {
		Optional.ofNullable(endpointTest.getInitializationQueries())
				.ifPresent(queries -> assertEquals(200, RestAssured.given().body(queries).contentType(ContentType.JSON)
						.post(REST_URL + "/testonly/integration/database/initialize").getStatusCode()));
	}

	private void resetDatabase() {
		RestAssured.get(REST_URL + "/testonly/integration/database/reset");
	}

	private void validateResponse(UserTest userTest, String email, Response response, String message) {
		assertEquals(userTest.getExpectedHttpCode().value(), response.getStatusCode(), message);
		Optional.ofNullable(userTest.getExpectedResponse())
				.ifPresent(expected -> assertEquals(expected, response.asString(), message));
		Optional.ofNullable(userTest.getValidationQueries())
				.ifPresent(queries -> assertEquals(List.of(),
						RestAssured.given()
								.body(queries.stream().map(query -> query.replace(":email", "'" + email + "'"))
										.collect(Collectors.toList()))
								.contentType(ContentType.JSON)
								.post(REST_URL + "/testonly/integration/database/validate")
								.as(DatabaseValidationResult.class).getFailingQueries(),
						message));
	}

	private String getAsssertFailedMessage(EndpointTest endpointTest, String email) {
		return StringUtils.join(endpointTest.getMethod(), " ", endpointTest.getUrl(), " as ", email);
	}

}
