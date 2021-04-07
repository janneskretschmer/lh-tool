package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import de.lh.tool.domain.dto.NeedDto;
import de.lh.tool.domain.dto.NeedUserDto;
import de.lh.tool.domain.model.NeedUserState;
import de.lh.tool.rest.bean.EmailTest;
import de.lh.tool.rest.bean.EndpointTest;
import de.lh.tool.rest.bean.UserTest;
import io.restassured.http.Method;

public class NeedIT extends BasicRestIntegrationTest {
//  ██████╗__██████╗_███████╗████████╗
//  ██╔══██╗██╔═══██╗██╔════╝╚══██╔══╝
//  ██████╔╝██║___██║███████╗___██║___
//  ██╔═══╝_██║___██║╚════██║___██║___
//  ██║_____╚██████╔╝███████║___██║___
//  ╚═╝______╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testNeedCreationForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/needs/").method(Method.POST)
				.body(NeedDto.builder().projectHelperTypeId(1l).date(Date.valueOf("2020-04-23")).quantity(123).build())
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')"))
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"projectHelperTypeId\":1,\"date\":1587600000000,\"quantity\":123}")
						.validationQueries(List.of(
								"SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=123 AND date='2020-04-23'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=123 AND date='2020-04-23')"))
				.build()));
	}

	@Test
	public void testNeedCreationOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/needs/").method(Method.POST)
				.body(NeedDto.builder().projectHelperTypeId(1l).date(Date.valueOf("2020-04-23")).quantity(123).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"projectHelperTypeId\":1,\"date\":1587600000000,\"quantity\":123}")
						.validationQueries(List.of(
								"SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=123 AND date='2020-04-23'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=123 AND date='2020-04-23')"))
				.build()));
	}

	@Test
	public void testNeedCreationDuplicate() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')"))
				.url(REST_URL + "/needs/").method(Method.POST)
				.body(NeedDto.builder().projectHelperTypeId(1l).date(Date.valueOf("2020-04-23")).quantity(123).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.CONFLICT)
						.expectedResponse(
								"{\"key\":\"EX_NEED_ALREADY_EXISTS\",\"message\":\"A need with the provided date and project helper type already exists.\",\"httpCode\":409}")
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM need)=1")).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM need)=1")).build()));
	}

	// TODO test missing values

//  ██████╗_██╗___██╗████████╗
//  ██╔══██╗██║___██║╚══██╔══╝
//  ██████╔╝██║___██║___██║___
//  ██╔═══╝_██║___██║___██║___
//  ██║_____╚██████╔╝___██║___
//  ╚═╝______╚═════╝____╚═╝___

	@Test
	public void testNeedModificationForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')"))
				.url(REST_URL + "/needs/1").method(Method.PUT)
				.body(NeedDto.builder().id(1l).projectHelperTypeId(1l).date(Date.valueOf("2020-04-24")).quantity(123)
						.build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"projectHelperTypeId\":1,\"date\":1587686400000,\"quantity\":123}")
						.validationQueries(List.of(
								"SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=123 AND date='2020-04-24'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List
						.of("SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=42 AND date='2020-04-23'"))
				.build()));
	}

	@Test
	public void testNeedModificationOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')"))
				.url(REST_URL + "/needs/1").method(Method.PUT)
				.body(NeedDto.builder().id(1l).projectHelperTypeId(1l).date(Date.valueOf("2020-04-24")).quantity(123)
						.build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"projectHelperTypeId\":1,\"date\":1587686400000,\"quantity\":123}")
						.validationQueries(List.of(
								"SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=123 AND date='2020-04-24'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List
						.of("SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=42 AND date='2020-04-23'"))
				.build()));
	}

	@Test
	public void testNeedQuantityModificationOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1000, 1, 42, '2020-04-23')"))
				.url(REST_URL + "/needs/1000").method(Method.PUT)
				.body(NeedDto.builder().id(1000l).projectHelperTypeId(1l).date(Date.valueOf("2020-04-23")).quantity(123)
						.build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"projectHelperTypeId\":1,\"date\":1587600000000,\"quantity\":123}")
						.validationQueries(List.of(
								"SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=123 AND date='2020-04-23'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List
						.of("SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=42 AND date='2020-04-23'"))
				.build()));
	}

	@Test
	public void testNeedModificationDuplicate() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (2, 1, 42, '2020-04-24')"))
				.url(REST_URL + "/needs/1").method(Method.PUT)
				.body(NeedDto.builder().id(1l).projectHelperTypeId(1l).date(Date.valueOf("2020-04-24")).quantity(123)
						.build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.CONFLICT)
						.expectedResponse(
								"{\"key\":\"EX_NEED_ALREADY_EXISTS\",\"message\":\"A need with the provided date and project helper type already exists.\",\"httpCode\":409}")
						.validationQueries(
								List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM need WHERE date='2020-04-24')=1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(
						List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM need WHERE date='2020-04-24')=1"))
				.build()));
	}

	@Test
	public void testNeedModificationNotExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')"))
				.url(REST_URL + "/needs/2").method(Method.PUT)
				.body(NeedDto.builder().id(2l).projectHelperTypeId(1l).date(Date.valueOf("2020-04-24")).quantity(123)
						.build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of(
								"SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=42 AND date='2020-04-23'",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM need)=1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=42 AND date='2020-04-23'",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM need)=1"))
				.build()));
	}

	@Test
	public void testNeedUserModificationCancelAppliedOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (1, 1, 1000, 'APPLIED')"))
				.url(REST_URL + "/needs/1/users/1000").method(Method.PUT)
				.body(NeedUserDto.builder().needId(1l).userId(1000l).state(NeedUserState.NONE).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, LOCAL_COORDINATOR_EMAIL, "test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":null,\"needId\":1,\"userId\":1000,\"state\":\"NONE\"}")
						.validationQueries(List.of(
								"SELECT 1 WHERE NOT EXISTS (SELECT * FROM need_user WHERE need_id=1 AND user_id=1000)"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT * FROM need_user WHERE need_id=1 AND user_id=1000 AND state='APPLIED'"))
				.build()));
	}

	@Test
	public void testNeedUserModificationApplyNewOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')"))
				.url(REST_URL + "/needs/1/users/1000").method(Method.PUT)
				.body(NeedUserDto.builder().needId(1l).userId(1000l).state(NeedUserState.APPLIED).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, LOCAL_COORDINATOR_EMAIL, "test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":1,\"needId\":1,\"userId\":1000,\"state\":\"APPLIED\"}")
						.validationQueries(
								List.of("SELECT * FROM need_user WHERE need_id=1 AND user_id=1000 AND state='APPLIED'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT 1 WHERE NOT EXISTS (SELECT * FROM need_user WHERE need_id=1 AND user_id=1000)"))
				.build()));
	}

	@Test
	public void testNeedUserModificationApplyApprovedOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (1, 1, 1000, 'APPROVED')"))
				.url(REST_URL + "/needs/1/users/1000").method(Method.PUT)
				.body(NeedUserDto.builder().needId(1l).userId(1000l).state(NeedUserState.APPLIED).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":1,\"needId\":1,\"userId\":1000,\"state\":\"APPLIED\"}")
						.validationQueries(
								List.of("SELECT * FROM need_user WHERE need_id=1 AND user_id=1000 AND state='APPLIED'"))
						.expectedEmails(List.of(EmailTest.builder().recipient("test@lh-tool.de")
								.subjectRegex("Schicht am 23\\.04\\.2020 nicht genehmigt")
								.contentRegex("Liebe Schwester Ter,\n" + "\n"
										+ "deine Schicht am 23\\.04\\.2020 von 07:00 Uhr bis 17:00 Uhr als Test1 wurde nicht genehmigt\\.\n"
										+ "\n" + "Viele Grüße\n" + "LDC Baugruppe\n" + "\n"
										+ "p\\.s\\. Das ist eine automatisch generierte Mail, bitte antworte nicht darauf\\. Bei Fragen wende dich bitte an den zuständigen Helferkoordinator\\.\n"
										+ "")
								.build()))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT * FROM need_user WHERE need_id=1 AND user_id=1000 AND state='APPROVED'"))
				.build()));
	}

	@Test
	public void testNeedUserModificationApproveAppliedOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (1, 1, 1000, 'APPLIED')"))
				.url(REST_URL + "/needs/1/users/1000").method(Method.PUT)
				.body(NeedUserDto.builder().needId(1l).userId(1000l).state(NeedUserState.APPROVED).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":1,\"needId\":1,\"userId\":1000,\"state\":\"APPROVED\"}")
						.validationQueries(List
								.of("SELECT * FROM need_user WHERE need_id=1 AND user_id=1000 AND state='APPROVED'"))
						.expectedEmails(List.of(EmailTest.builder().recipient("test@lh-tool.de")
								.subjectRegex("Schicht am 23\\.04\\.2020 genehmigt")
								.contentRegex("Liebe Schwester Ter,\n" + "\n"
										+ "deine Schicht am 23\\.04\\.2020 von 07:00 Uhr bis 17:00 Uhr als Test1 wurde genehmigt\\.\n"
										+ "\n" + "Viele Grüße\n" + "LDC Baugruppe\n" + "\n"
										+ "p\\.s\\. Das ist eine automatisch generierte Mail, bitte antworte nicht darauf\\. Bei Fragen wende dich bitte an den zuständigen Helferkoordinator\\.\n"
										+ "")
								.build()))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT * FROM need_user WHERE need_id=1 AND user_id=1000 AND state='APPLIED'"))
				.build()));
	}

	@Test
	public void testNeedUserModificationApproveRejectedOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (1, 1, 1000, 'REJECTED')"))
				.url(REST_URL + "/needs/1/users/1000").method(Method.PUT)
				.body(NeedUserDto.builder().needId(1l).userId(1000l).state(NeedUserState.APPROVED).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":1,\"needId\":1,\"userId\":1000,\"state\":\"APPROVED\"}")
						.validationQueries(List
								.of("SELECT * FROM need_user WHERE need_id=1 AND user_id=1000 AND state='APPROVED'"))
						.expectedEmails(List.of(EmailTest.builder().recipient("test@lh-tool.de")
								.subjectRegex("Schicht am 23\\.04\\.2020 genehmigt")
								.contentRegex("Liebe Schwester Ter,\n" + "\n"
										+ "deine Schicht am 23\\.04\\.2020 von 07:00 Uhr bis 17:00 Uhr als Test1 wurde genehmigt\\.\n"
										+ "\n" + "Viele Grüße\n" + "LDC Baugruppe\n" + "\n"
										+ "p\\.s\\. Das ist eine automatisch generierte Mail, bitte antworte nicht darauf\\. Bei Fragen wende dich bitte an den zuständigen Helferkoordinator\\.\n"
										+ "")
								.build()))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT * FROM need_user WHERE need_id=1 AND user_id=1000 AND state='REJECTED'"))
				.build()));
	}

	@Test
	public void testNeedUserModificationRejectApprovedOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (1, 1, 1000, 'APPROVED')"))
				.url(REST_URL + "/needs/1/users/1000").method(Method.PUT)
				.body(NeedUserDto.builder().needId(1l).userId(1000l).state(NeedUserState.REJECTED).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":1,\"needId\":1,\"userId\":1000,\"state\":\"REJECTED\"}")
						.validationQueries(List
								.of("SELECT * FROM need_user WHERE need_id=1 AND user_id=1000 AND state='REJECTED'"))
						.expectedEmails(List.of(EmailTest.builder().recipient("test@lh-tool.de")
								.subjectRegex("Schicht am 23\\.04\\.2020 abgelehnt")
								.contentRegex("Liebe Schwester Ter,\n" + "\n"
										+ "deine Schicht am 23\\.04\\.2020 von 07:00 Uhr bis 17:00 Uhr als Test1 wurde abgelehnt\\.\n"
										+ "\n" + "Viele Grüße\n" + "LDC Baugruppe\n" + "\n"
										+ "p\\.s\\. Das ist eine automatisch generierte Mail, bitte antworte nicht darauf\\. Bei Fragen wende dich bitte an den zuständigen Helferkoordinator\\.\n")
								.build()))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT * FROM need_user WHERE need_id=1 AND user_id=1000 AND state='APPROVED'"))
				.build()));
	}

	@Test
	public void testNeedUserModificationCancelApprovedOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (1, 1, 1000, 'APPROVED')"))
				.url(REST_URL + "/needs/1/users/1000").method(Method.PUT)
				.body(NeedUserDto.builder().needId(1l).userId(1000l).state(NeedUserState.NONE).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, LOCAL_COORDINATOR_EMAIL, "test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":null,\"needId\":1,\"userId\":1000,\"state\":\"NONE\"}")
						.validationQueries(List.of(
								"SELECT 1 WHERE NOT EXISTS (SELECT * FROM need_user WHERE need_id=1 AND user_id=1000)"))
						.expectedEmails(List.of(EmailTest.builder().recipient("test-local_coordinator@lh-tool.de")
								.subjectRegex("Schicht am 23\\.04\\.2020 zurückgezogen")
								.contentRegex("Lieber Bruder Local_coordinator,\n" + "\n"
										+ "die Bewerbung von Tes Ter für den 23\\.04\\.2020 wurde zurückgezogen\\.\n"
										+ "\n"
										+ "Bitte prüfe, ob an diesem Tag genügend Helfer zur Verfügung stehen\\.\n"
										+ "\n" + "Viele Grüße\n" + "LDC Baugruppe\n" + "\n"
										+ "p\\.s\\. Das ist eine automatisch generierte Mail, bitte antworte nicht darauf\\. Bei Fragen wende dich bitte an den zuständigen Helferkoordinator\\.\n")
								.build()))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT * FROM need_user WHERE need_id=1 AND user_id=1000 AND state='APPROVED'"))
				.build()));
	}

	@Test
	public void testNeedUserModificationSameStateOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (1, 1, 1000, 'APPROVED')"))
				.url(REST_URL + "/needs/1/users/1000").method(Method.PUT)
				.body(NeedUserDto.builder().needId(1l).userId(1000l).state(NeedUserState.APPROVED).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								"test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":1,\"needId\":1,\"userId\":1000,\"state\":\"APPROVED\"}")
						.validationQueries(List
								.of("SELECT * FROM need_user WHERE need_id=1 AND user_id=1000 AND state='APPROVED'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT * FROM need_user WHERE need_id=1 AND user_id=1000 AND state='APPROVED'"))
				.build()));
	}

	@Test
	public void testNeedUserModificationIllegal() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')"))
				.url(REST_URL + "/needs/1/users/1000").method(Method.PUT)
				.body(NeedUserDto.builder().needId(1l).userId(1000l).state(NeedUserState.REJECTED).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								"test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_NEED_USER_INVALID_STATE\",\"message\":\"The provided state is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of(
								"SELECT 1 WHERE NOT EXISTS (SELECT * FROM need_user WHERE need_id=1 AND user_id=1000)"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT 1 WHERE NOT EXISTS (SELECT * FROM need_user WHERE need_id=1 AND user_id=1000)"))
				.build()));
	}

	// TODO test missing values

//  ██████╗_███████╗██╗_____███████╗████████╗███████╗
//  ██╔══██╗██╔════╝██║_____██╔════╝╚══██╔══╝██╔════╝
//  ██║__██║█████╗__██║_____█████╗_____██║___█████╗__
//  ██║__██║██╔══╝__██║_____██╔══╝_____██║___██╔══╝__
//  ██████╔╝███████╗███████╗███████╗___██║___███████╗
//  ╚═════╝_╚══════╝╚══════╝╚══════╝___╚═╝___╚══════╝

	@Test
	public void testNeedDeletionForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')"))
				.url(REST_URL + "/needs/1").method(Method.DELETE)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
						.validationQueries(List.of(
								"SELECT 1 WHERE NOT EXISTS (SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=42 AND date='2020-04-23')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List
						.of("SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=42 AND date='2020-04-23'"))
				.build()));
	}

	@Test
	public void testNeedDeletionOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')"))
				.url(REST_URL + "/needs/1").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
						.validationQueries(List.of(
								"SELECT 1 WHERE NOT EXISTS (SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=42 AND date='2020-04-23')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List
						.of("SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=42 AND date='2020-04-23'"))
				.build()));
	}

	@Test
	public void testNeedDeletionNotExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')"))
				.url(REST_URL + "/needs/2").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of(
								"SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=42 AND date='2020-04-23'",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM need)=1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM need WHERE project_helper_type_id=1 AND quantity=42 AND date='2020-04-23'",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM need)=1"))
				.build()));
	}

//  _██████╗_███████╗████████╗
//  ██╔════╝_██╔════╝╚══██╔══╝
//  ██║__███╗█████╗_____██║___
//  ██║___██║██╔══╝_____██║___
//  ╚██████╔╝███████╗___██║___
//  _╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testNeedGetOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')"))
				.url(REST_URL + "/needs?project_helper_type_id=1&date=2020-04-23").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								ATTENDANCE_EMAIL, PUBLISHER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":1,\"projectHelperTypeId\":1,\"date\":1587600000000,\"quantity\":42}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testNeedGetForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')"))
				.url(REST_URL + "/needs?project_helper_type_id=1&date=2020-04-23").method(Method.GET)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":1,\"projectHelperTypeId\":1,\"date\":1587600000000,\"quantity\":42}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testNeedGetByIdOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')"))
				.url(REST_URL + "/needs/1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								ATTENDANCE_EMAIL, PUBLISHER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"projectHelperTypeId\":1,\"date\":1587600000000,\"quantity\":42}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testNeedGetByIdForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')"))
				.url(REST_URL + "/needs/1").method(Method.GET)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":1,\"projectHelperTypeId\":1,\"date\":1587600000000,\"quantity\":42}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testNeedUserGetByUserIdForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (1, 1, 1000, 'APPROVED')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (2, 1, 1, 'APPLIED')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (3, 1, 2, 'REJECTED')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (2, 1, 21, '2020-04-24')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (4, 2, 2, 'APPROVED')"))
				.url(REST_URL + "/needs/1/users/1000").method(Method.GET)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":1,\"needId\":1,\"userId\":1000,\"state\":\"APPROVED\"}").build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testNeedUserGetByUserIdOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (1, 1, 1000, 'APPROVED')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (2, 1, 1, 'APPLIED')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (3, 1, 2, 'REJECTED')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (2, 1, 21, '2020-04-24')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (4, 2, 2, 'APPROVED')"))
				.url(REST_URL + "/needs/1/users/1000").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								ATTENDANCE_EMAIL, "test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":1,\"needId\":1,\"userId\":1000,\"state\":\"APPROVED\"}").build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testNeedUserGetForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (1, 1, 1000, 'APPROVED')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (2, 1, 1, 'APPLIED')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (3, 1, 2, 'REJECTED')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (2, 1, 21, '2020-04-24')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (4, 2, 2, 'APPROVED')"))
				.url(REST_URL + "/needs/1/users").method(Method.GET)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"[{\"id\":2,\"needId\":1,\"userId\":1,\"state\":\"APPLIED\"},{\"id\":3,\"needId\":1,\"userId\":2,\"state\":\"REJECTED\"},{\"id\":1,\"needId\":1,\"userId\":1000,\"state\":\"APPROVED\"}]")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testNeedUserGetOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_PUBLISHER')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (1, 1, 1000, 'APPROVED')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (2, 1, 1, 'APPLIED')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (3, 1, 2, 'REJECTED')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (2, 1, 21, '2020-04-24')",
						"INSERT INTO need_user (id, need_id, user_id, state) VALUES (4, 2, 2, 'APPROVED')"))
				.url(REST_URL + "/needs/1/users").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List
								.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL, ATTENDANCE_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"[{\"id\":2,\"needId\":1,\"userId\":1,\"state\":\"APPLIED\"},{\"id\":3,\"needId\":1,\"userId\":2,\"state\":\"REJECTED\"},{\"id\":1,\"needId\":1,\"userId\":1000,\"state\":\"APPROVED\"}]")
						.build(),
						UserTest.builder().emails(List.of("test@lh-tool.de")).expectedHttpCode(HttpStatus.OK)
								.expectedResponse("[{\"id\":2,\"needId\":1,\"userId\":null,\"state\":\"APPLIED\"},"
										+ "{\"id\":3,\"needId\":1,\"userId\":null,\"state\":\"REJECTED\"},"
										+ "{\"id\":1,\"needId\":1,\"userId\":1000,\"state\":\"APPROVED\"}]")
								.build(),
						UserTest.builder().emails(List.of(PUBLISHER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse("[{\"id\":2,\"needId\":1,\"userId\":null,\"state\":\"APPLIED\"},"
										+ "{\"id\":3,\"needId\":1,\"userId\":null,\"state\":\"REJECTED\"},"
										+ "{\"id\":1,\"needId\":1,\"userId\":null,\"state\":\"APPROVED\"}]")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

}
