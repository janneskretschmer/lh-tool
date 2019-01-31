package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;

import de.lh.tool.domain.dto.JwtAuthenticationDto;
import de.lh.tool.domain.dto.LoginDto;
import de.lh.tool.domain.dto.PasswordChangeDto;
import de.lh.tool.domain.dto.UserCreationDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.model.User.Gender;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public abstract class BasicRestIntegrationTest {
	private static final String PASSWORD = "testing";

	protected static final String ADMIN_EMAIL = "test-admin@lh-tool.de";
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
	protected static final List<String> TEST_USER_EMAILS = List.of(CONSTRUCTION_SERVANT_1_EMAIL,
			CONSTRUCTION_SERVANT_2_EMAIL, LOCAL_COORDINATOR_1_EMAIL, LOCAL_COORDINATOR_2_EMAIL, PUBLISHER_1_EMAIL,
			PUBLISHER_2_EMAIL, STORE_KEEPER_1_EMAIL, STORE_KEEPER_2_EMAIL, INVENTORY_MANAGER_1_EMAIL,
			INVENTORY_MANAGER_2_EMAIL);

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

	protected String getJWTByEmail(String email) {
		String url = REST_URL + "/login/";
		JwtAuthenticationDto res = RestAssured.given().body(new LoginDto(email, PASSWORD)).contentType(ContentType.JSON)
				.when().post(url).as(JwtAuthenticationDto.class);
		return res != null ? res.getAccessToken() : null;
	}

	protected RequestSpecification getRequestSpecWithJWT(String jwt) {
		return RestAssured.given().header("Authorization", "Bearer " + jwt);
	}

	public void createTestUsers() throws Exception {
		String jwt = getJWTByEmail(ADMIN_EMAIL);
		assertNotNull(jwt);
		String registrationUrl = REST_URL + "/users/";
		String passwordUrl = REST_URL + "/users/password";
		int counter = 0;
		for (String email : TEST_USER_EMAILS) {
			Long userId = getRequestSpecWithJWT(jwt)
					.body(new UserCreationDto("Tes" + counter, "Ter" + counter, email, Gender.MALE.name()))
					.contentType(ContentType.JSON).post(registrationUrl).as(UserDto.class).getId();
			getRequestSpecWithJWT(jwt).body(
					PasswordChangeDto.builder().userId(userId).newPassword(PASSWORD).confirmPassword(PASSWORD).build())
					.contentType(ContentType.JSON).put(passwordUrl).then().statusCode(200);
			counter++;
		}
	}

	public void deleteTestUsers() {
		String jwt = getJWTByEmail(ADMIN_EMAIL);
		assertNotNull(jwt);
		String url = REST_URL + "/users/";
		List<UserDto> users = getRequestSpecWithJWT(jwt).get(url).then().extract().jsonPath().getList("content",
				UserDto.class);
		for (UserDto user : users) {
			if (TEST_USER_EMAILS.contains(user.getEmail())) {
				getRequestSpecWithJWT(jwt).body(user).contentType(ContentType.JSON).delete(url).then().statusCode(200);
			}
		}
	}
}
