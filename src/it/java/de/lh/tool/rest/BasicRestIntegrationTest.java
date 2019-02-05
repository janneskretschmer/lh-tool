package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;

import de.lh.tool.domain.dto.JwtAuthenticationDto;
import de.lh.tool.domain.dto.LoginDto;
import de.lh.tool.domain.dto.PasswordChangeDto;
import de.lh.tool.domain.dto.UserCreationDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.dto.UserRolesDto;
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.User.Gender;
import de.lh.tool.domain.model.UserRole;
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
		String url = REST_URL + "/login/";
		JwtAuthenticationDto res = RestAssured.given().body(new LoginDto(email, PASSWORD)).contentType(ContentType.JSON)
				.when().post(url).as(JwtAuthenticationDto.class);
		return res != null ? res.getAccessToken() : null;
	}

	protected RequestSpecification getRequestSpecWithJwt(String jwt) {
		return RestAssured.given().header("Authorization", "Bearer " + jwt);
	}

	protected RequestSpecification getRequestSpecWithJwtByEmail(String email) {
		return getRequestSpecWithJwt(getJwtByEmail(email));
	}

	protected void createTestUsers() throws Exception {
		deleteTestUsers();
		String jwt = getJwtByEmail(ADMIN_EMAIL);
		assertNotNull(jwt);
		String registrationUrl = REST_URL + "/users/";
		String passwordUrl = REST_URL + "/users/password";
		int counter = 0;
		for (User user : TEST_USERS) {
			Long userId = getRequestSpecWithJwt(jwt)
					.body(new UserCreationDto(user.getFirstName(), user.getLastName(), user.getEmail(),
							Gender.MALE.name()))
					.contentType(ContentType.JSON).post(registrationUrl).as(UserDto.class).getId();
			getRequestSpecWithJwt(jwt).body(
					PasswordChangeDto.builder().userId(userId).newPassword(PASSWORD).confirmPassword(PASSWORD).build())
					.contentType(ContentType.JSON).put(passwordUrl).then().statusCode(200);
			getRequestSpecWithJwt(jwt)
					.body(new UserRolesDto(
							user.getRoles().stream().map(UserRole::getRole).collect(Collectors.toList())))
					.contentType(ContentType.JSON).put(registrationUrl + userId + "/roles").then().statusCode(200);
			counter++;
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
}
