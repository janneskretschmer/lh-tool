package de.lh.tool.rest;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import de.lh.tool.domain.dto.PasswordChangeDto;
import de.lh.tool.domain.dto.UserCreationDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.model.User.Gender;
import de.lh.tool.rest.bean.EndpointTest;
import de.lh.tool.rest.bean.UserTest;
import de.lh.tool.service.rest.testonly.IntegrationTestRestService;
import io.restassured.http.Method;

public class UserIT extends BasicRestIntegrationTest {

//  ██████╗__██████╗_███████╗████████╗
//  ██╔══██╗██╔═══██╗██╔════╝╚══██╔══╝
//  ██████╔╝██║___██║███████╗___██║___
//  ██╔═══╝_██║___██║╚════██║___██║___
//  ██║_____╚██████╔╝███████║___██║___
//  ╚═╝______╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testUserCreation() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/").method(Method.POST)
				.body(UserCreationDto.builder().email("test@lh-tool.de").firstName("Tes").lastName("Ter")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":" + (IntegrationTestRestService.getDefaultEmails().size() + 1)
								+ ",\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"MALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null},{\"rel\":\"/password\",\"href\":\"http://localhost:8080/lh-tool/rest/users/password\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'")).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
				.build());
	}

	// TODO test missing values
	// TODO test existing email

//  ██████╗_██╗___██╗████████╗
//  ██╔══██╗██║___██║╚══██╔══╝
//  ██████╔╝██║___██║___██║___
//  ██╔═══╝_██║___██║___██║___
//  ██║_____╚██████╔╝___██║___
//  ╚═╝______╚═════╝____╚═╝___

	@Test
	public void testUserModificationForeign() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')"))
				.body(UserDto.builder().email("changed@lh-tool.de").firstName("Chan").lastName("Ged")
						.telephoneNumber("987").mobileNumber("").businessNumber("321").profession("KA").skills("nix")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, "test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Chan\",\"lastName\":\"Ged\",\"gender\":\"MALE\",\"email\":\"changed@lh-tool.de\",\"telephoneNumber\":\"987\",\"mobileNumber\":\"\",\"businessNumber\":\"321\",\"profession\":\"KA\",\"skills\":\"nix\",\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM user WHERE email='changed@lh-tool.de' AND first_name='Chan' AND last_name='Ged' AND telephone_number='987' AND mobile_number='' AND business_number='321' AND profession='KA' AND skills='nix'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='changed@lh-tool.de')"))
				.build());
	}

	@Test
	public void testUserModificationOwnProjectLocalCoordinator() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_LOCAL_COORDINATOR')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.body(UserDto.builder().email("changed@lh-tool.de").firstName("Chan").lastName("Ged")
						.telephoneNumber("987").mobileNumber("").businessNumber("321").profession("KA").skills("nix")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, "test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Chan\",\"lastName\":\"Ged\",\"gender\":\"MALE\",\"email\":\"changed@lh-tool.de\",\"telephoneNumber\":\"987\",\"mobileNumber\":\"\",\"businessNumber\":\"321\",\"profession\":\"KA\",\"skills\":\"nix\",\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM user WHERE email='changed@lh-tool.de' AND first_name='Chan' AND last_name='Ged' AND telephone_number='987' AND mobile_number='' AND business_number='321' AND profession='KA' AND skills='nix'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='changed@lh-tool.de')"))
				.build());
	}

	@Test
	public void testUserModificationOwnProjectStoreKeeper() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_STORE_KEEPER')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.body(UserDto.builder().email("changed@lh-tool.de").firstName("Chan").lastName("Ged")
						.telephoneNumber("987").mobileNumber("").businessNumber("321").profession("KA").skills("nix")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, "test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Chan\",\"lastName\":\"Ged\",\"gender\":\"MALE\",\"email\":\"changed@lh-tool.de\",\"telephoneNumber\":\"987\",\"mobileNumber\":\"\",\"businessNumber\":\"321\",\"profession\":\"KA\",\"skills\":\"nix\",\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM user WHERE email='changed@lh-tool.de' AND first_name='Chan' AND last_name='Ged' AND telephone_number='987' AND mobile_number='' AND business_number='321' AND profession='KA' AND skills='nix'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='changed@lh-tool.de')"))
				.build());
	}

	@Test
	public void testUserModificationOwnProjectInventoryManager() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.body(UserDto.builder().email("changed@lh-tool.de").firstName("Chan").lastName("Ged")
						.telephoneNumber("987").mobileNumber("").businessNumber("321").profession("KA").skills("nix")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, "test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Chan\",\"lastName\":\"Ged\",\"gender\":\"MALE\",\"email\":\"changed@lh-tool.de\",\"telephoneNumber\":\"987\",\"mobileNumber\":\"\",\"businessNumber\":\"321\",\"profession\":\"KA\",\"skills\":\"nix\",\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM user WHERE email='changed@lh-tool.de' AND first_name='Chan' AND last_name='Ged' AND telephone_number='987' AND mobile_number='' AND business_number='321' AND profession='KA' AND skills='nix'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='changed@lh-tool.de')"))
				.build());
	}

	@Test
	public void testUserModificationOwnProjectPublisher() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.body(UserDto.builder().email("changed@lh-tool.de").firstName("Chan").lastName("Ged")
						.telephoneNumber("987").mobileNumber("").businessNumber("321").profession("KA").skills("nix")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								"test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Chan\",\"lastName\":\"Ged\",\"gender\":\"MALE\",\"email\":\"changed@lh-tool.de\",\"telephoneNumber\":\"987\",\"mobileNumber\":\"\",\"businessNumber\":\"321\",\"profession\":\"KA\",\"skills\":\"nix\",\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM user WHERE email='changed@lh-tool.de' AND first_name='Chan' AND last_name='Ged' AND telephone_number='987' AND mobile_number='' AND business_number='321' AND profession='KA' AND skills='nix'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='changed@lh-tool.de')"))
				.build());
	}

	// TODO test missing values
	// TODO test existing email

	@Test
	public void testPasswordModificationToken() throws Exception {
		testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO password_change_token(user_id,token) VALUES (1000,'test-token')"))
				.body(PasswordChangeDto
						.builder().userId(1000l).token(
								"test-token")
						.newPassword("changed").confirmPassword("changed").build())
				.userTests(List.of(//
						UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"MALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false,\"links\":[]}")
								.validationQueries(List.of(
										"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash!='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'",
										"SELECT * FROM password_change_token WHERE user_id=1000"))
								.build(),
						UserTest.builder().emails(List.of("test@lh-tool.de")).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"MALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false,\"links\":[]}")
								.validationQueries(List.of(
										"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash!='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'",
										"SELECT 1 WHERE NOT EXISTS(SELECT * FROM password_change_token WHERE user_id=1000)"))
								.build()))
				.httpCodeForOthers(HttpStatus.BAD_REQUEST)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'",
						"SELECT * FROM password_change_token WHERE user_id=1000"))
				.build());
	}

	@Test
	public void testPasswordModificationOldPassword() throws Exception {
		testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)"))
				.body(PasswordChangeDto.builder().userId(1000l).oldPassword("testing").newPassword("changed")
						.confirmPassword("changed").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, "test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"MALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false,\"links\":[]}")
						.validationQueries(List.of(
								"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash!='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'"))
						.build()))
				.httpCodeForOthers(HttpStatus.BAD_REQUEST)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'"))
				.build());
	}

	@Test
	public void testPasswordModificationShort() throws Exception {
		testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)"))
				.body(PasswordChangeDto.builder().userId(1000l).oldPassword("testing").newPassword("short")
						.confirmPassword("short").build())
				.userTests(List.of(UserTest.builder().emails(List.of("test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_PASSWORDS_SHORT_PASSWORD\",\"message\":\"The provided password has less than 6 letters.\",\"httpCode\":400}")
						.validationQueries(List.of(
								"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'"))
						.build()))
				.httpCodeForOthers(HttpStatus.BAD_REQUEST)
				.responseForOthers(
						"{\"key\":\"EX_PASSWORDS_SHORT_PASSWORD\",\"message\":\"The provided password has less than 6 letters.\",\"httpCode\":400}")
				.validationQueriesForOthers(List.of(
						"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'"))
				.build());
	}

	@Test
	public void testPasswordModificationNoMatch() throws Exception {
		testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)"))
				.body(PasswordChangeDto.builder().userId(1000l).oldPassword("testing").newPassword("changed")
						.confirmPassword("short").build())
				.userTests(List.of(UserTest.builder().emails(List.of("test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_PASSWORDS_DO_NOT_MATCH\",\"message\":\"The provided passwords do not match.\",\"httpCode\":400}")
						.validationQueries(List.of(
								"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'"))
						.build()))
				.httpCodeForOthers(HttpStatus.BAD_REQUEST)
				.responseForOthers(
						"{\"key\":\"EX_PASSWORDS_DO_NOT_MATCH\",\"message\":\"The provided passwords do not match.\",\"httpCode\":400}")
				.validationQueriesForOthers(List.of(
						"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'"))
				.build());
	}

	@Test
	public void testPasswordModificationNoUser() throws Exception {
		testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)"))
				.body(PasswordChangeDto.builder().oldPassword("testing").newPassword("changed")
						.confirmPassword("changed").build())
				.userTests(List.of(UserTest.builder().emails(List.of("test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_PASSWORDS_NO_USER_ID\",\"message\":\"No user id was provided.\",\"httpCode\":400}")
						.validationQueries(List.of(
								"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'"))
						.build()))
				.httpCodeForOthers(HttpStatus.BAD_REQUEST)
				.responseForOthers(
						"{\"key\":\"EX_PASSWORDS_NO_USER_ID\",\"message\":\"No user id was provided.\",\"httpCode\":400}")
				.validationQueriesForOthers(List.of(
						"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'"))
				.build());
	}

	@Test
	public void testPasswordModificationInvalidPassword() throws Exception {
		testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)"))
				.body(PasswordChangeDto.builder().userId(1000l).oldPassword("invalid").newPassword("changed")
						.confirmPassword("changed").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"MALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false,\"links\":[]}")
						.validationQueries(List.of(
								"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash!='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'"))
						.build(),
						UserTest.builder().emails(List.of("test@lh-tool.de")).expectedHttpCode(HttpStatus.BAD_REQUEST)
								.expectedResponse(
										"{\"key\":\"EX_PASSWORDS_INVALID_PASSWORD\",\"message\":\"The provided old password is invalid.\",\"httpCode\":400}")
								.validationQueries(List.of(
										"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'"))
								.build()))
				.httpCodeForOthers(HttpStatus.BAD_REQUEST)
				.responseForOthers(
						"{\"key\":\"EX_INVALID_USER_ID\",\"message\":\"The provided user id is invalid.\",\"httpCode\":400}")
				.validationQueriesForOthers(List.of(
						"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'"))
				.build());
	}

	@Test
	public void testPasswordModificationInvalidToken() throws Exception {
		testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO password_change_token(user_id,token) VALUES (1000,'test-token')"))
				.body(PasswordChangeDto
						.builder().userId(1000l).token(
								"invalid-token")
						.newPassword("changed").confirmPassword("changed").build())
				.userTests(List.of(//
						UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"MALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false,\"links\":[]}")
								.validationQueries(List.of(
										"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash!='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'",
										"SELECT * FROM password_change_token WHERE user_id=1000"))
								.build(),
						UserTest.builder().emails(List.of("test@lh-tool.de")).expectedHttpCode(HttpStatus.BAD_REQUEST)
								.expectedResponse(
										"{\"key\":\"EX_PASSWORDS_INVALID_TOKEN\",\"message\":\"The provided token is invalid.\",\"httpCode\":400}")
								.validationQueries(List.of(
										"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'",
										"SELECT * FROM password_change_token WHERE user_id=1000"))
								.build()))
				.httpCodeForOthers(HttpStatus.BAD_REQUEST)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'",
						"SELECT * FROM password_change_token WHERE user_id=1000"))
				.build());
	}

	@Test
	public void testPasswordModificationExpiredToken() throws Exception {
		testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO password_change_token(user_id,token,updated) VALUES (1000,'test-token',DATE_SUB( NOW() , INTERVAL 15 DAY ))"))
				.body(PasswordChangeDto
						.builder().userId(1000l).token(
								"test-token")
						.newPassword("changed").confirmPassword("changed").build())
				.userTests(List.of(//
						UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"MALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false,\"links\":[]}")
								.validationQueries(List.of(
										"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash!='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'",
										"SELECT * FROM password_change_token WHERE user_id=1000"))
								.build(),
						UserTest.builder().emails(List.of("test@lh-tool.de")).expectedHttpCode(HttpStatus.BAD_REQUEST)
								.expectedResponse(
										"{\"key\":\"EX_PASSWORDS_EXPIRED_TOKEN\",\"message\":\"The provided token is expired.\",\"httpCode\":400}")
								.validationQueries(List.of(
										"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'",
										"SELECT * FROM password_change_token WHERE user_id=1000"))
								.build()))
				.httpCodeForOthers(HttpStatus.BAD_REQUEST)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM user WHERE email='test@lh-tool.de' AND password_hash='$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W'",
						"SELECT * FROM password_change_token WHERE user_id=1000"))
				.build());
	}

	// TODO test put userRoles

//  ██████╗_███████╗██╗_____███████╗████████╗███████╗
//  ██╔══██╗██╔════╝██║_____██╔════╝╚══██╔══╝██╔════╝
//  ██║__██║█████╗__██║_____█████╗_____██║___█████╗__
//  ██║__██║██╔══╝__██║_____██╔══╝_____██║___██╔══╝__
//  ██████╔╝███████╗███████╗███████╗___██║___███████╗
//  ╚═════╝_╚══════╝╚══════╝╚══════╝___╚═╝___╚══════╝

	@Test
	public void testUserDeletionOwnProjectLocalCoordinator() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', NULL, 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_LOCAL_COORDINATOR')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.body(UserDto.builder().email("changed@lh-tool.de").firstName("Chan").lastName("Ged")
						.telephoneNumber("987").mobileNumber("").businessNumber("321").profession("KA").skills("nix")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Chan\",\"lastName\":\"Ged\",\"gender\":\"MALE\",\"email\":\"changed@lh-tool.de\",\"telephoneNumber\":\"987\",\"mobileNumber\":\"\",\"businessNumber\":\"321\",\"profession\":\"KA\",\"skills\":\"nix\",\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM user WHERE email='changed@lh-tool.de' AND first_name='Chan' AND last_name='Ged' AND telephone_number='987' AND mobile_number='' AND business_number='321' AND profession='KA' AND skills='nix'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='changed@lh-tool.de')"))
				.build());
	}

	@Test
	public void testUserDeletionOwnProjectStoreKeeper() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.DELETE)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', NULL, 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_STORE_KEEPER')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
						.validationQueries(
								List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'")).build());
	}

	@Test
	public void testUserDeletionOwnProjectInventoryManager() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.DELETE)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', NULL, 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
						.validationQueries(
								List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'")).build());
	}

	@Test
	public void testUserDeletionOwnProjectPublisher() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.DELETE)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', NULL, 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
						.validationQueries(
								List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'")).build());
	}

	@Test
	public void testUserDeletionNotExisting() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)"))
				.url(REST_URL + "/users/1001").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_USER_ID\",\"message\":\"The provided user id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build());
	}

//  _██████╗_███████╗████████╗
//  ██╔════╝_██╔════╝╚══██╔══╝
//  ██║__███╗█████╗_____██║___
//  ██║___██║██╔══╝_____██║___
//  ╚██████╔╝███████╗___██║___
//  _╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testUserGetForeign() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Terin', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1002', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test2@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)"))
				.userTests(List.of(//
						UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/{?project_id,role}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"firstName\":\"Test\",\"lastName\":\"Admin\",\"gender\":\"MALE\","
												+ "\"email\":\"test-admin@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":4,\"firstName\":\"Test\",\"lastName\":\"Attendance\",\"gender\":\"MALE\","
												+ "\"email\":\"test-attendance@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":2,\"firstName\":\"Test\",\"lastName\":\"Construction_servant\",\"gender\":\"MALE\","
												+ "\"email\":\"test-construction_servant@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":7,\"firstName\":\"Test\",\"lastName\":\"Inventory_manager\",\"gender\":\"MALE\","
												+ "\"email\":\"test-inventory_manager@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":3,\"firstName\":\"Test\",\"lastName\":\"Local_coordinator\",\"gender\":\"MALE\","
												+ "\"email\":\"test-local_coordinator@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":5,\"firstName\":\"Test\",\"lastName\":\"Publisher\",\"gender\":\"MALE\","
												+ "\"email\":\"test-publisher@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":6,\"firstName\":\"Test\",\"lastName\":\"Store_keeper\",\"gender\":\"MALE\","
												+ "\"email\":\"test-store_keeper@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":1002,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"MALE\","
												+ "\"email\":\"test2@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Terin\",\"gender\":\"FEMALE\","
												+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
								.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/{?project_id,role}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":2,\"firstName\":\"Test\",\"lastName\":\"Construction_servant\",\"gender\":\"MALE\",\"email\":\"test-construction_servant@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false}]}")
								.build(),
						UserTest.builder().emails(List.of(LOCAL_COORDINATOR_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/{?project_id,role}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":3,\"firstName\":\"Test\",\"lastName\":\"Local_coordinator\",\"gender\":\"MALE\",\"email\":\"test-local_coordinator@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false}]}")
								.build(),
						UserTest.builder().emails(List.of(ATTENDANCE_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/{?project_id,role}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":4,\"firstName\":\"Test\",\"lastName\":\"Attendance\",\"gender\":\"MALE\",\"email\":\"test-attendance@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false}]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build());
	}

	@Test
	public void testUserGetOwn() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Terin', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1002', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test2@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/{?project_id,role}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"firstName\":\"Test\",\"lastName\":\"Admin\",\"gender\":\"MALE\","
										+ "\"email\":\"test-admin@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":4,\"firstName\":\"Test\",\"lastName\":\"Attendance\",\"gender\":\"MALE\","
										+ "\"email\":\"test-attendance@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":2,\"firstName\":\"Test\",\"lastName\":\"Construction_servant\",\"gender\":\"MALE\","
										+ "\"email\":\"test-construction_servant@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":7,\"firstName\":\"Test\",\"lastName\":\"Inventory_manager\",\"gender\":\"MALE\","
										+ "\"email\":\"test-inventory_manager@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":3,\"firstName\":\"Test\",\"lastName\":\"Local_coordinator\",\"gender\":\"MALE\","
										+ "\"email\":\"test-local_coordinator@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":5,\"firstName\":\"Test\",\"lastName\":\"Publisher\",\"gender\":\"MALE\","
										+ "\"email\":\"test-publisher@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":6,\"firstName\":\"Test\",\"lastName\":\"Store_keeper\",\"gender\":\"MALE\","
										+ "\"email\":\"test-store_keeper@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":1002,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"MALE\","
										+ "\"email\":\"test2@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Terin\",\"gender\":\"FEMALE\","
										+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
						.build(),
						UserTest.builder()
								.emails(List.of(CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL, ATTENDANCE_EMAIL))
								.expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/{?project_id,role}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"firstName\":\"Test\",\"lastName\":\"Admin\",\"gender\":\"MALE\","
												+ "\"email\":\"test-admin@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":4,\"firstName\":\"Test\",\"lastName\":\"Attendance\",\"gender\":\"MALE\","
												+ "\"email\":\"test-attendance@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":2,\"firstName\":\"Test\",\"lastName\":\"Construction_servant\",\"gender\":\"MALE\","
												+ "\"email\":\"test-construction_servant@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":7,\"firstName\":\"Test\",\"lastName\":\"Inventory_manager\",\"gender\":\"MALE\","
												+ "\"email\":\"test-inventory_manager@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":3,\"firstName\":\"Test\",\"lastName\":\"Local_coordinator\",\"gender\":\"MALE\","
												+ "\"email\":\"test-local_coordinator@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":5,\"firstName\":\"Test\",\"lastName\":\"Publisher\",\"gender\":\"MALE\","
												+ "\"email\":\"test-publisher@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":6,\"firstName\":\"Test\",\"lastName\":\"Store_keeper\",\"gender\":\"MALE\","
												+ "\"email\":\"test-store_keeper@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Terin\",\"gender\":\"FEMALE\","
												+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build());
	}

	@Test
	public void testUserGetByOwnProjectId() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users?project_id=1").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1002', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test2@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-08-09', '2020-11-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 2,id FROM user",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1003', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test3@lh-tool.de', '541681', '61', NULL, '', NULL)"))
				.userTests(List.of(UserTest.builder()
						.emails(List
								.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL, ATTENDANCE_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?project_id=1{&role}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"firstName\":\"Test\",\"lastName\":\"Admin\",\"gender\":\"MALE\","
										+ "\"email\":\"test-admin@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":4,\"firstName\":\"Test\",\"lastName\":\"Attendance\",\"gender\":\"MALE\","
										+ "\"email\":\"test-attendance@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":2,\"firstName\":\"Test\",\"lastName\":\"Construction_servant\",\"gender\":\"MALE\","
										+ "\"email\":\"test-construction_servant@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":7,\"firstName\":\"Test\",\"lastName\":\"Inventory_manager\",\"gender\":\"MALE\","
										+ "\"email\":\"test-inventory_manager@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":3,\"firstName\":\"Test\",\"lastName\":\"Local_coordinator\",\"gender\":\"MALE\","
										+ "\"email\":\"test-local_coordinator@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":5,\"firstName\":\"Test\",\"lastName\":\"Publisher\",\"gender\":\"MALE\","
										+ "\"email\":\"test-publisher@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":6,\"firstName\":\"Test\",\"lastName\":\"Store_keeper\",\"gender\":\"MALE\","
										+ "\"email\":\"test-store_keeper@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\","
										+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build());
	}

	@Test
	public void testUserGetForeignProjectId() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users?project_id=2").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-08-09', '2020-11-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Terin', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO project_user(project_id, user_id) VALUES(2,1000)",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1002', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test2@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO project_user(project_id, user_id) VALUES(2,1002)",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1003', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test3@lh-tool.de', '541681', '61', NULL, '', NULL)"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?project_id=2{&role}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1002,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"MALE\","
										+ "\"email\":\"test2@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Terin\",\"gender\":\"FEMALE\","
										+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build());
	}

	@Test
	public void testUserGetOwnProjectIdAndRole() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users?project_id=1&role=ROLE_INVENTORY_MANAGER").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-08-09', '2020-11-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1002', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test2@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1002,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO project_user(project_id, user_id) VALUES(2,1002)",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1003', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test3@lh-tool.de', '541681', '61', NULL, '', NULL)"))
				.userTests(List.of(UserTest.builder()
						.emails(List
								.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL, ATTENDANCE_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?project_id=1&role=ROLE_INVENTORY_MANAGER\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":7,\"firstName\":\"Test\",\"lastName\":\"Inventory_manager\",\"gender\":\"MALE\","
										+ "\"email\":\"test-inventory_manager@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\","
										+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build());
	}

	@Test
	public void testUserGetForeignProjectIdAndRole() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users?project_id=2&role=ROLE_PUBLISHER").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-08-09', '2020-11-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO project_user(project_id, user_id) VALUES(2,1000)",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1002', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test2@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO project_user(project_id, user_id) VALUES(2,1002)",
						"INSERT INTO user_role(user_id,role) VALUES(1002,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1003', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test3@lh-tool.de', '541681', '61', NULL, '', NULL)"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?project_id=2&role=ROLE_PUBLISHER\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build());
	}

	@Test
	public void testUserGetRole() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users?role=ROLE_INVENTORY_MANAGER").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-08-09', '2020-11-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1002', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test2@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1002,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO project_user(project_id, user_id) VALUES(2,1002)",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1003', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test3@lh-tool.de', '541681', '61', NULL, '', NULL)"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?role=ROLE_INVENTORY_MANAGER{&project_id}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":7,\"firstName\":\"Test\",\"lastName\":\"Inventory_manager\",\"gender\":\"MALE\","
										+ "\"email\":\"test-inventory_manager@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\","
										+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false},{\"id\":1002,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"MALE\","
										+ "\"email\":\"test2@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
						.build(),
						UserTest.builder()
								.emails(List.of(CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL, ATTENDANCE_EMAIL))
								.expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?role=ROLE_INVENTORY_MANAGER{&project_id}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":7,\"firstName\":\"Test\",\"lastName\":\"Inventory_manager\",\"gender\":\"MALE\","
												+ "\"email\":\"test-inventory_manager@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\","
												+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build());
	}

	@Test
	public void testUserGetByIdOwnProject() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-08-09', '2020-11-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1002', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test2@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1002,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO project_user(project_id, user_id) VALUES(2,1002)",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1003', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test3@lh-tool.de', '541681', '61', NULL, '', NULL)"))
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								ATTENDANCE_EMAIL, "test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build());
	}

	@Test
	public void testUserGetByIdForeignProject() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, "test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build());
	}

	@Test
	public void testUserGetByIdNonExisting() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.GET).initializationQueries(List.of())
				.userTests(List.of(UserTest.builder()
						.emails(List
								.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL, ATTENDANCE_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_WRONG_ID_PROVIDED\",\"message\":\"Please provide a valid ID.\",\"httpCode\":400}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build());
	}

}
