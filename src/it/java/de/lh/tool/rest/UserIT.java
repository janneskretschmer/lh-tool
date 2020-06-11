package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import de.lh.tool.domain.dto.PasswordChangeDto;
import de.lh.tool.domain.dto.UserCreationDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.dto.UserRoleDto;
import de.lh.tool.domain.model.User.Gender;
import de.lh.tool.rest.bean.EmailTest;
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
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/").method(Method.POST)
				.body(UserCreationDto.builder().email("test@lh-tool.de").firstName("Tes").lastName("Ter")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":" + (IntegrationTestRestService.getDefaultEmails().size() + 1)
								+ ",\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"MALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null},{\"rel\":\"/password\",\"href\":\"http://localhost:8080/lh-tool/rest/users/password\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.expectedEmails(List.of(EmailTest.builder().recipient("test@lh-tool.de")
								.subjectRegex("Account bei lh-tool\\.de")
								.contentRegex("Lieber Bruder Ter,\n\n"
										+ "es wurde für dich ein Account auf lh-tool\\.de angelegt\\. Auf dieser Webseite kannst du dich als Helfer bei der Baustelle an deinem Saal bewerben\\.\n"
										+ "Bitte rufe folgenden Link auf, um ein Passwort zu setzen\\.\n\n"
										+ "http://localhost:8080/lh-tool/web/changepw\\?uid=8&token=................................................................................................................................\n\n"
										+ "Vielen Dank für deine Bereitschaft\\. Wir wünschen dir Jehovas Segen\\.\n\n"
										+ "Viele Grüße\nLDC Baugruppe\n\n"
										+ "p\\.s\\. Das ist eine automatisch generierte Mail, bitte antworte nicht darauf\\. Bei Fragen wende dich bitte an den zuständigen Helferkoordinator\\.\n")
								.build()))
						.validationQueries(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'")).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
				.build()));
	}

	@Test
	public void testUserMissingFirstName() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/").method(Method.POST)
				.body(UserCreationDto.builder().email("test@lh-tool.de").firstName(null).lastName("Ter")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_USER_NO_FIRST_NAME\",\"message\":\"The user has no first name.\",\"httpCode\":400}")
						.validationQueries(
								List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
				.build()));
	}

	@Test
	public void testUserMissingLastName() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/").method(Method.POST)
				.body(UserCreationDto.builder().email("test@lh-tool.de").firstName("Tes").lastName(null)
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_USER_NO_LAST_NAME\",\"message\":\"The user has no last name.\",\"httpCode\":400}")
						.validationQueries(
								List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
				.build()));
	}

	@Test
	public void testUserMissingEmail() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/").method(Method.POST)
				.body(UserCreationDto.builder().email(null).firstName("Tes").lastName("Ter").gender(Gender.MALE.name())
						.build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_USER_NO_EMAIL\",\"message\":\"The user has no email address.\",\"httpCode\":400}")
						.validationQueries(
								List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
				.build()));
	}

	@Test
	public void testUserEmailDuplicate() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/").method(Method.POST)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')"))
				.body(UserCreationDto.builder().email("test@lh-tool.de").firstName("Tes").lastName("Ter")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.CONFLICT)
						.expectedResponse(
								"{\"key\":\"EX_USER_EMAIL_ALREADY_IN_USE\",\"message\":\"The provided e-mail address is already in use.\",\"httpCode\":409}")
						.validationQueries(
								List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM user WHERE email='test@lh-tool.de')=1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM user WHERE email='test@lh-tool.de')=1"))
				.build()));
	}

	@Test
	public void testUserRolesCreationOwnProject() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000/roles").method(Method.POST)
				.body(new UserRoleDto(null, 1000l, "ROLE_PUBLISHER"))
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_STORE_KEEPER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":9,\"userId\":1000,\"role\":\"ROLE_PUBLISHER\",\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000/roles\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(
								List.of("SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_STORE_KEEPER'",
										"SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_PUBLISHER'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_STORE_KEEPER'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_PUBLISHER')"))
				.build()));
	}

	@Test
	public void testUserRolesCreationAdmin() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000/roles").method(Method.POST)
				.body(new UserRoleDto(null, 1000l, "ROLE_ADMIN"))
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_STORE_KEEPER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":9,\"userId\":1000,\"role\":\"ROLE_ADMIN\",\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000/roles\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(
								List.of("SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_STORE_KEEPER'",
										"SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_ADMIN'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_STORE_KEEPER'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_ADMIN')"))
				.build()));
	}

	@Test
	public void testUserRolesCreationInvalidUser() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1001/roles").method(Method.POST)
				.body(new UserRoleDto(null, 1001l, "ROLE_PUBLISHER"))
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_STORE_KEEPER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_USER_ID\",\"message\":\"The provided user id is invalid.\",\"httpCode\":400}")
						.validationQueries(List
								.of("SELECT 1 WHERE (SELECT COUNT(*) FROM user_role WHERE role='ROLE_PUBLISHER')=1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM user_role WHERE role='ROLE_PUBLISHER')=1"))
				.build()));
	}

	@Test
	public void testUserRolesCreationDuplicate() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000/roles").method(Method.POST)
				.body(new UserRoleDto(null, 1000l, "ROLE_PUBLISHER"))
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_STORE_KEEPER')",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.CONFLICT)
						.expectedResponse(
								"{\"key\":\"EX_USER_ROLE_ALREADY_EXISTS\",\"message\":\"The user already has this role.\",\"httpCode\":409}")
						.validationQueries(List.of(
								"SELECT 1 WHERE (SELECT COUNT(*) FROM user_role WHERE user_id=1000 AND role='ROLE_PUBLISHER')=1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT 1 WHERE (SELECT COUNT(*) FROM user_role WHERE user_id=1000 AND role='ROLE_PUBLISHER')=1"))
				.build()));
	}

	@Test
	public void testUserRolesCreationForeignProject() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000/roles").method(Method.POST)
				.body(new UserRoleDto(null, 1000l, "ROLE_PUBLISHER"))
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_STORE_KEEPER')"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":9,\"userId\":1000,\"role\":\"ROLE_PUBLISHER\",\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000/roles\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(
								List.of("SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_STORE_KEEPER'",
										"SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_PUBLISHER'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_STORE_KEEPER'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_PUBLISHER')"))
				.build()));
	}

//  ██████╗_██╗___██╗████████╗
//  ██╔══██╗██║___██║╚══██╔══╝
//  ██████╔╝██║___██║___██║___
//  ██╔═══╝_██║___██║___██║___
//  ██║_____╚██████╔╝___██║___
//  ╚═╝______╚═════╝____╚═╝___

	@Test
	public void testUserModificationForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
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
				.build()));
	}

	@Test
	public void testUserModificationOwnProjectLocalCoordinator() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
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
				.build()));
	}

	@Test
	public void testUserModificationOwnProjectStoreKeeper() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
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
				.build()));
	}

	@Test
	public void testUserModificationOwnProjectInventoryManager() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
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
				.build()));
	}

	@Test
	public void testUserModificationOwnProjectPublisher() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
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
				.build()));
	}

	@Test
	public void testUserModificationOwnProjectMissingFirstName() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.body(UserDto.builder().email("changed@lh-tool.de").firstName(null).lastName("Ged")
						.telephoneNumber("987").mobileNumber("").businessNumber("321").profession("KA").skills("nix")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								"test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						// keep old values if missing
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ged\",\"gender\":\"MALE\",\"email\":\"changed@lh-tool.de\",\"telephoneNumber\":\"987\",\"mobileNumber\":\"\",\"businessNumber\":\"321\",\"profession\":\"KA\",\"skills\":\"nix\",\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM user WHERE email='changed@lh-tool.de' AND first_name='Tes' AND last_name='Ged' AND telephone_number='987' AND mobile_number='' AND business_number='321' AND profession='KA' AND skills='nix'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='changed@lh-tool.de')"))
				.build()));
	}

	@Test
	public void testUserModificationOwnProjectMissingLastName() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.body(UserDto.builder().email("changed@lh-tool.de").firstName("Chan").lastName(null)
						.telephoneNumber("987").mobileNumber("").businessNumber("321").profession("KA").skills("nix")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								"test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						// keep old values if missing
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Chan\",\"lastName\":\"Ter\",\"gender\":\"MALE\",\"email\":\"changed@lh-tool.de\",\"telephoneNumber\":\"987\",\"mobileNumber\":\"\",\"businessNumber\":\"321\",\"profession\":\"KA\",\"skills\":\"nix\",\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM user WHERE email='changed@lh-tool.de' AND first_name='Chan' AND last_name='Ter' AND telephone_number='987' AND mobile_number='' AND business_number='321' AND profession='KA' AND skills='nix'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='changed@lh-tool.de')"))
				.build()));
	}

	@Test
	public void testUserModificationOwnProjectMissingEmail() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.body(UserDto.builder().email(null).firstName("Chan").lastName("Ged").telephoneNumber("987")
						.mobileNumber("").businessNumber("321").profession("KA").skills("nix")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								"test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						// keep old values if missing
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Chan\",\"lastName\":\"Ged\",\"gender\":\"MALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"987\",\"mobileNumber\":\"\",\"businessNumber\":\"321\",\"profession\":\"KA\",\"skills\":\"nix\",\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM user WHERE email='test@lh-tool.de' AND first_name='Chan' AND last_name='Ged' AND telephone_number='987' AND mobile_number='' AND business_number='321' AND profession='KA' AND skills='nix'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='changed@lh-tool.de')"))
				.build()));
	}

	@Test
	public void testUserModificationOwnProjectDuplicateEmail() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1001', 'Tes', 'Ter2', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'changed@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.body(UserDto.builder().email("changed@lh-tool.de").firstName("Chan").lastName("Ged")
						.telephoneNumber("987").mobileNumber("").businessNumber("321").profession("KA").skills("nix")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								"test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.CONFLICT)
						.expectedResponse(
								"{\"key\":\"EX_USER_EMAIL_ALREADY_IN_USE\",\"message\":\"The provided e-mail address is already in use.\",\"httpCode\":409}")
						.validationQueries(List.of(
								"SELECT 1 WHERE (SELECT COUNT(*) FROM user WHERE email='test@lh-tool.de') = 1",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM user WHERE email='changed@lh-tool.de') = 1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM user WHERE email='test@lh-tool.de') = 1",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM user WHERE email='changed@lh-tool.de') = 1"))
				.build()));
	}

	@Test
	public void testPasswordModificationToken() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
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
				.build()));
	}

	@Test
	public void testPasswordModificationOldPassword() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
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
				.build()));
	}

	@Test
	public void testPasswordModificationShort() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
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
				.build()));
	}

	@Test
	public void testPasswordModificationNoMatch() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
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
				.build()));
	}

	@Test
	public void testPasswordModificationNoUser() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
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
				.build()));
	}

	@Test
	public void testPasswordModificationInvalidPassword() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
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
				.build()));
	}

	@Test
	public void testPasswordModificationInvalidToken() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
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
				.build()));
	}

	@Test
	public void testPasswordModificationExpiredToken() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder().url(REST_URL + "/users/password").method(Method.PUT)
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
				.build()));
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
		assertTrue(testEndpoint(EndpointTest.builder()//
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
				.build()));
	}

	@Test
	public void testUserDeletionOwnProjectStoreKeeper() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.DELETE)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', NULL, 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_STORE_KEEPER')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
						.validationQueries(
								List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'")).build()));
	}

	@Test
	public void testUserDeletionOwnProjectInventoryManager() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
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
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'")).build()));
	}

	@Test
	public void testUserDeletionOwnProjectPublisher() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
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
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'")).build()));
	}

	@Test
	public void testUserDeletionNotExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)"))
				.url(REST_URL + "/users/1001").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_USER_ID\",\"message\":\"The provided user id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testUserRolesDeletion() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000/roles/100").method(Method.DELETE)
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(id,user_id,role) VALUES(100,1000,'ROLE_PUBLISHER')",
						"INSERT INTO user_role(id,user_id,role) VALUES(101,1000,'ROLE_STORE_KEEPER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT)
						.validationQueries(List.of(
								"SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_STORE_KEEPER'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_PUBLISHER')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_STORE_KEEPER'",
								"SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_PUBLISHER'"))
				.build()));
	}

	@Test
	public void testUserRolesDeletionConstructionServant() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000/roles/101").method(Method.DELETE)
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(id,user_id,role) VALUES(100,1000,'ROLE_PUBLISHER')",
						"INSERT INTO user_role(id,user_id,role) VALUES(101,1000,'ROLE_CONSTRUCTION_SERVANT')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT)
						.validationQueries(List.of(
								"SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_PUBLISHER'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_CONSTRUCTION_SERVANT')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_PUBLISHER'",
								"SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_CONSTRUCTION_SERVANT'"))
				.build()));
	}

	@Test
	public void testUserRolesDeletionNonExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/100/roles/102").method(Method.DELETE)
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(id,user_id,role) VALUES(100,1000,'ROLE_PUBLISHER')",
						"INSERT INTO user_role(id,user_id,role) VALUES(101,1000,'ROLE_CONSTRUCTION_SERVANT')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of(
								"SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_PUBLISHER'",
								"SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_CONSTRUCTION_SERVANT'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_PUBLISHER'",
								"SELECT * FROM user_role WHERE user_id=1000 AND role='ROLE_CONSTRUCTION_SERVANT'"))
				.build()));
	}

//  _██████╗_███████╗████████╗
//  ██╔════╝_██╔════╝╚══██╔══╝
//  ██║__███╗█████╗_____██║___
//  ██║___██║██╔══╝_____██║___
//  ╚██████╔╝███████╗___██║___
//  _╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testUserGetForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Terin', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1002', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test2@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)"))
				.userTests(List.of(//
						UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/{?project_id,role,free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"firstName\":\"Test\",\"lastName\":\"Admin\",\"gender\":\"MALE\","
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
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/{?project_id,role,free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":2,\"firstName\":\"Test\",\"lastName\":\"Construction_servant\",\"gender\":\"MALE\",\"email\":\"test-construction_servant@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false}]}")
								.build(),
						UserTest.builder().emails(List.of(LOCAL_COORDINATOR_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/{?project_id,role,free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":3,\"firstName\":\"Test\",\"lastName\":\"Local_coordinator\",\"gender\":\"MALE\",\"email\":\"test-local_coordinator@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false}]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testUserGetOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Terin', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1002', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test2@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/{?project_id,role,free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"firstName\":\"Test\",\"lastName\":\"Admin\",\"gender\":\"MALE\","
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
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/{?project_id,role,free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":4,\"firstName\":\"Test\",\"lastName\":\"Attendance\",\"gender\":\"MALE\","
												+ "\"email\":\"test-attendance@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":2,\"firstName\":\"Test\",\"lastName\":\"Construction_servant\",\"gender\":\"MALE\","
												+ "\"email\":\"test-construction_servant@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":7,\"firstName\":\"Test\",\"lastName\":\"Inventory_manager\",\"gender\":\"MALE\","
												+ "\"email\":\"test-inventory_manager@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":3,\"firstName\":\"Test\",\"lastName\":\"Local_coordinator\",\"gender\":\"MALE\","
												+ "\"email\":\"test-local_coordinator@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":5,\"firstName\":\"Test\",\"lastName\":\"Publisher\",\"gender\":\"MALE\","
												+ "\"email\":\"test-publisher@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":6,\"firstName\":\"Test\",\"lastName\":\"Store_keeper\",\"gender\":\"MALE\","
												+ "\"email\":\"test-store_keeper@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Terin\",\"gender\":\"FEMALE\","
												+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
								.build(),
						UserTest.builder().emails(List.of(LOCAL_COORDINATOR_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/{?project_id,role,free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":3,\"firstName\":\"Test\",\"lastName\":\"Local_coordinator\",\"gender\":\"MALE\","
												+ "\"email\":\"test-local_coordinator@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":5,\"firstName\":\"Test\",\"lastName\":\"Publisher\",\"gender\":\"MALE\","
												+ "\"email\":\"test-publisher@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":6,\"firstName\":\"Test\",\"lastName\":\"Store_keeper\",\"gender\":\"MALE\","
												+ "\"email\":\"test-store_keeper@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Terin\",\"gender\":\"FEMALE\","
												+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testUserGetByOwnProjectId() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users?project_id=1").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1002', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test2@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-08-09', '2020-11-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 2,id FROM user",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1003', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test3@lh-tool.de', '541681', '61', NULL, '', NULL)"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?project_id=1{&role,free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"firstName\":\"Test\",\"lastName\":\"Admin\",\"gender\":\"MALE\","
										+ "\"email\":\"test-admin@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":4,\"firstName\":\"Test\",\"lastName\":\"Attendance\",\"gender\":\"MALE\","
										+ "\"email\":\"test-attendance@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":2,\"firstName\":\"Test\",\"lastName\":\"Construction_servant\",\"gender\":\"MALE\","
										+ "\"email\":\"test-construction_servant@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":7,\"firstName\":\"Test\",\"lastName\":\"Inventory_manager\",\"gender\":\"MALE\","
										+ "\"email\":\"test-inventory_manager@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":3,\"firstName\":\"Test\",\"lastName\":\"Local_coordinator\",\"gender\":\"MALE\","
										+ "\"email\":\"test-local_coordinator@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":5,\"firstName\":\"Test\",\"lastName\":\"Publisher\",\"gender\":\"MALE\","
										+ "\"email\":\"test-publisher@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":6,\"firstName\":\"Test\",\"lastName\":\"Store_keeper\",\"gender\":\"MALE\","
										+ "\"email\":\"test-store_keeper@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\","
										+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?project_id=1{&role,free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":4,\"firstName\":\"Test\",\"lastName\":\"Attendance\",\"gender\":\"MALE\","
												+ "\"email\":\"test-attendance@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":2,\"firstName\":\"Test\",\"lastName\":\"Construction_servant\",\"gender\":\"MALE\","
												+ "\"email\":\"test-construction_servant@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":7,\"firstName\":\"Test\",\"lastName\":\"Inventory_manager\",\"gender\":\"MALE\","
												+ "\"email\":\"test-inventory_manager@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":3,\"firstName\":\"Test\",\"lastName\":\"Local_coordinator\",\"gender\":\"MALE\","
												+ "\"email\":\"test-local_coordinator@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":5,\"firstName\":\"Test\",\"lastName\":\"Publisher\",\"gender\":\"MALE\","
												+ "\"email\":\"test-publisher@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":6,\"firstName\":\"Test\",\"lastName\":\"Store_keeper\",\"gender\":\"MALE\","
												+ "\"email\":\"test-store_keeper@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\","
												+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
								.build(),
						UserTest.builder().emails(List.of(LOCAL_COORDINATOR_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?project_id=1{&role,free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":3,\"firstName\":\"Test\",\"lastName\":\"Local_coordinator\",\"gender\":\"MALE\","
												+ "\"email\":\"test-local_coordinator@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":5,\"firstName\":\"Test\",\"lastName\":\"Publisher\",\"gender\":\"MALE\","
												+ "\"email\":\"test-publisher@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":6,\"firstName\":\"Test\",\"lastName\":\"Store_keeper\",\"gender\":\"MALE\","
												+ "\"email\":\"test-store_keeper@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\","
												+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testUserGetForeignProjectId() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
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
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?project_id=2{&role,free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1002,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"MALE\","
										+ "\"email\":\"test2@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Terin\",\"gender\":\"FEMALE\","
										+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testUserGetOwnProjectIdAndRole() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
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
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?project_id=1&role=ROLE_INVENTORY_MANAGER{&free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":7,\"firstName\":\"Test\",\"lastName\":\"Inventory_manager\",\"gender\":\"MALE\","
										+ "\"email\":\"test-inventory_manager@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\","
										+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
						.build(),
						UserTest.builder().emails(List.of(LOCAL_COORDINATOR_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?project_id=1&role=ROLE_INVENTORY_MANAGER{&free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testUserGetForeignProjectIdAndRole() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
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
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?project_id=2&role=ROLE_PUBLISHER{&free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testUserGetByOwnProjectIdAndRoleAndFreeText() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users?project_id=1&role=ROLE_INVENTORY_MANAGER&free_text=fRe3T_eXt")
				.method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-08-09', '2020-11-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1002', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test2@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1002,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO project_user(project_id, user_id) VALUES(2,1002)",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1003', 'ewfRe3T_eXtasda', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test3@lh-tool.de', '541681', '61', NULL, '', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1003,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1004', 'Firstname', 'vewfRe3T_eXtaase', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test4@lh-tool.de', '541681', '61', NULL, '', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1004,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1005', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'fRe3T_eXtf@lh-tool.de', '541681', '61', NULL, '', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1005,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1006', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test6@lh-tool.de', 'ffffre3t_extase', '61', NULL, '', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1006,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1007', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test7@lh-tool.de', '541681', 'fRe3T_eXt', NULL, '', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1007,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1008', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test8@lh-tool.de', '541681', '61', 'basFWfRe3T_eXtdae', '', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1008,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1009', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test9@lh-tool.de', '541681', '61', NULL, 'vasereFRE3T_EXTeaseav', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1009,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1010', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test10@lh-tool.de', '541681', '61', NULL, '', 'ankalsnfRe3T_eXTalsknelk')",
						"INSERT INTO user_role(user_id,role) VALUES(1010,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1011', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test11@lh-tool.de', '541681', '61', NULL, '', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1011,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1012', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test12@lh-tool.de', '541681', '61', NULL, '', NULL)",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1013', 'fRe3T_eXt', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test13@lh-tool.de', '541681', '61', NULL, '', NULL)",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1014', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test14@lh-tool.de', '541681', '61', NULL, '', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1014,'ROLE_INVENTORY_MANAGER')"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?project_id=1&role=ROLE_INVENTORY_MANAGER&free_text=fRe3T_eXt\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1003,\"firstName\":\"ewfRe3T_eXtasda\",\"lastName\":\"Lastname\",\"gender\":\"MALE\","
										+ "\"email\":\"test3@lh-tool.de\",\"telephoneNumber\":\"541681\",\"mobileNumber\":\"61\",\"businessNumber\":null,\"profession\":\"\",\"skills\":null,\"active\":false},{\"id\":1005,\"firstName\":\"Firstname\",\"lastName\":\"Lastname\",\"gender\":\"MALE\","
										+ "\"email\":\"fRe3T_eXtf@lh-tool.de\",\"telephoneNumber\":\"541681\",\"mobileNumber\":\"61\",\"businessNumber\":null,\"profession\":\"\",\"skills\":null,\"active\":false},{\"id\":1006,\"firstName\":\"Firstname\",\"lastName\":\"Lastname\",\"gender\":\"MALE\","
										+ "\"email\":\"test6@lh-tool.de\",\"telephoneNumber\":\"ffffre3t_extase\",\"mobileNumber\":\"61\",\"businessNumber\":null,\"profession\":\"\",\"skills\":null,\"active\":false},{\"id\":1007,\"firstName\":\"Firstname\",\"lastName\":\"Lastname\",\"gender\":\"MALE\","
										+ "\"email\":\"test7@lh-tool.de\",\"telephoneNumber\":\"541681\",\"mobileNumber\":\"fRe3T_eXt\",\"businessNumber\":null,\"profession\":\"\",\"skills\":null,\"active\":false},{\"id\":1008,\"firstName\":\"Firstname\",\"lastName\":\"Lastname\",\"gender\":\"MALE\","
										+ "\"email\":\"test8@lh-tool.de\",\"telephoneNumber\":\"541681\",\"mobileNumber\":\"61\",\"businessNumber\":\"basFWfRe3T_eXtdae\",\"profession\":\"\",\"skills\":null,\"active\":false},{\"id\":1009,\"firstName\":\"Firstname\",\"lastName\":\"Lastname\",\"gender\":\"MALE\","
										+ "\"email\":\"test9@lh-tool.de\",\"telephoneNumber\":\"541681\",\"mobileNumber\":\"61\",\"businessNumber\":null,\"profession\":\"vasereFRE3T_EXTeaseav\",\"skills\":null,\"active\":false},{\"id\":1010,\"firstName\":\"Firstname\",\"lastName\":\"Lastname\",\"gender\":\"MALE\","
										+ "\"email\":\"test10@lh-tool.de\",\"telephoneNumber\":\"541681\",\"mobileNumber\":\"61\",\"businessNumber\":null,\"profession\":\"\",\"skills\":\"ankalsnfRe3T_eXTalsknelk\",\"active\":false},{\"id\":1004,\"firstName\":\"Firstname\",\"lastName\":\"vewfRe3T_eXtaase\",\"gender\":\"MALE\","
										+ "\"email\":\"test4@lh-tool.de\",\"telephoneNumber\":\"541681\",\"mobileNumber\":\"61\",\"businessNumber\":null,\"profession\":\"\",\"skills\":null,\"active\":false}]}")
						.build(),
						UserTest.builder().emails(List.of(LOCAL_COORDINATOR_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?project_id=1&role=ROLE_INVENTORY_MANAGER&free_text=fRe3T_eXt\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testUserGetByRole() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
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
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?role=ROLE_INVENTORY_MANAGER{&project_id,free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":7,\"firstName\":\"Test\",\"lastName\":\"Inventory_manager\",\"gender\":\"MALE\","
										+ "\"email\":\"test-inventory_manager@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\","
										+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false},{\"id\":1002,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"MALE\","
										+ "\"email\":\"test2@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?role=ROLE_INVENTORY_MANAGER{&project_id,free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":7,\"firstName\":\"Test\",\"lastName\":\"Inventory_manager\",\"gender\":\"MALE\","
												+ "\"email\":\"test-inventory_manager@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false},{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\","
												+ "\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false}]}")
								.build(),
						UserTest.builder().emails(List.of(LOCAL_COORDINATOR_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/?role=ROLE_INVENTORY_MANAGER{&project_id,free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testUserGetByIdOwnProject() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
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
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, "test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testUserGetByIdForeignProject() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, "test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"FEMALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":\"123\",\"mobileNumber\":\"456\",\"businessNumber\":null,\"profession\":\"Hartzer\",\"skills\":null,\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testUserGetByIdNonExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.GET).initializationQueries(List.of())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_WRONG_ID_PROVIDED\",\"message\":\"Please provide a valid ID.\",\"httpCode\":400}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testUserRolesGetByIdOwnProject() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000/roles").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-08-09', '2020-11-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_STORE_KEEPER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1002', 'Tes', 'Ter', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test2@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1002,'ROLE_INVENTORY_MANAGER')",
						"INSERT INTO project_user(project_id, user_id) VALUES(2,1002)",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1003', 'Firstname', 'Lastname', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test3@lh-tool.de', '541681', '61', NULL, '', NULL)"))
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000/roles\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":8,\"userId\":1000,\"role\":\"ROLE_PUBLISHER\"},{\"id\":9,\"userId\":1000,\"role\":\"ROLE_STORE_KEEPER\"}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testUserRolesGetByIdForeignProject() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000/roles").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_STORE_KEEPER')",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000/roles\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":9,\"userId\":1000,\"role\":\"ROLE_PUBLISHER\"},{\"id\":8,\"userId\":1000,\"role\":\"ROLE_STORE_KEEPER\"}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testUserProjectsGetOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000/projects").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-08-09', '2020-11-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 2,id FROM user"))
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000/projects\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":8,\"projectId\":1,\"userId\":1000},{\"id\":23,\"projectId\":2,\"userId\":1000}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testUserProjectsGetForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000/projects").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) VALUES (1,1000)",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-08-09', '2020-11-24')",
						"INSERT INTO project_user(project_id, user_id) VALUES (2,1000)"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000/projects\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"projectId\":1,\"userId\":1000},{\"id\":2,\"projectId\":2,\"userId\":1000}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testUserProjectsGetNonExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1002/projects").method(Method.GET)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-08-09', '2020-11-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 2,id FROM user"))
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_USER_ID\",\"message\":\"The provided user id is invalid.\",\"httpCode\":400}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

}
