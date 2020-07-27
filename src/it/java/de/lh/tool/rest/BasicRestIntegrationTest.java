package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import de.lh.tool.domain.dto.JwtAuthenticationDto;
import de.lh.tool.domain.dto.LoginDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.rest.bean.EndpointTest;
import de.lh.tool.rest.bean.UserTest;
import de.lh.tool.service.rest.testonly.IntegrationTestRestService;
import de.lh.tool.service.rest.testonly.dto.DatabaseValidationResultDto;
import de.lh.tool.service.rest.testonly.dto.EmailDto;
import de.lh.tool.service.rest.testonly.dto.EmailWrapperDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class BasicRestIntegrationTest {
	private static final String PASSWORD = "testing";

	protected static final String ADMIN_EMAIL = "test-admin@lh-tool.de";
	protected static final String CONSTRUCTION_SERVANT_EMAIL = "test-construction_servant@lh-tool.de";
	protected static final String LOCAL_COORDINATOR_EMAIL = "test-local_coordinator@lh-tool.de";
	protected static final String ATTENDANCE_EMAIL = "test-attendance@lh-tool.de";
	protected static final String PUBLISHER_EMAIL = "test-publisher@lh-tool.de";
	protected static final String STORE_KEEPER_EMAIL = "test-store_keeper@lh-tool.de";
	protected static final String INVENTORY_MANAGER_EMAIL = "test-inventory_manager@lh-tool.de";

	private Map<String, String> jwtCache = new HashMap<>();

	private static final int TIMEOUT = 30000;
	protected static final String REST_URL = "http://localhost:8080/lh-tool/rest";

	@BeforeAll
	protected void setup() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		// wait for local tomcat
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
		assertEquals(200, RestAssured.get(REST_URL + "/testonly/integration/mailbox/initialize").getStatusCode());
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
		Optional.ofNullable(userTest.getExpectedResponse())
				.ifPresent(expected -> assertEquals(expected, response.asString(), message));
		assertEquals(userTest.getExpectedHttpCode().value(), response.getStatusCode(), message);
		Optional.ofNullable(userTest.getValidationQueries())
				.ifPresent(queries -> assertEquals(List.of(),
						RestAssured.given()
								.body(queries.stream().map(query -> query.replace(":email", "'" + email + "'"))
										.collect(Collectors.toList()))
								.contentType(ContentType.JSON)
								.post(REST_URL + "/testonly/integration/database/validate")
								.as(DatabaseValidationResultDto.class).getFailingQueries(),
						message));
		List<EmailDto> receivedMessages = RestAssured.get(REST_URL + "/testonly/integration/mailbox")
				.as(EmailWrapperDto.class).getEmails();
		if (userTest.getExpectedEmails() != null) {
			assertEquals(userTest.getExpectedEmails().size(), receivedMessages.size(), message);
			receivedMessages.forEach(receivedMessage -> assertTrue(userTest.getExpectedEmails().stream()
					.anyMatch(expectedEmail -> expectedEmail.getRecipient().equals(receivedMessage.getRecipient())
							&& receivedMessage.getSubject().matches(expectedEmail.getSubjectRegex())
							&& receivedMessage.getContent().matches(expectedEmail.getContentRegex())),
					message + "\n Unexpected Mail to " + receivedMessage.getRecipient() + " with subject \""
							+ receivedMessage.getSubject() + "\":\n" + receivedMessage.getContent()));
		} else {
			assertEquals(0, receivedMessages.size(), message);
		}

	}

	private String getAsssertFailedMessage(EndpointTest endpointTest, String email) {
		return StringUtils.join(endpointTest.getMethod(), " ", endpointTest.getUrl(), "\nas ", email);
	}

}
