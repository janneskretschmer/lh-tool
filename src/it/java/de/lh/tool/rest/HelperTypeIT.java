package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import de.lh.tool.domain.dto.HelperTypeDto;
import de.lh.tool.rest.bean.EndpointTest;
import de.lh.tool.rest.bean.UserTest;
import io.restassured.http.Method;

public class HelperTypeIT extends BasicRestIntegrationTest {

//  ██████╗__██████╗_███████╗████████╗
//  ██╔══██╗██╔═══██╗██╔════╝╚══██╔══╝
//  ██████╔╝██║___██║███████╗___██║___
//  ██╔═══╝_██║___██║╚════██║___██║___
//  ██║_____╚██████╔╝███████║___██║___
//  ╚═╝______╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testHelperTypeCreation() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/helper_types/").method(Method.POST).body(HelperTypeDto.builder().name("Test").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Test\",\"links\":[{\"rel\":\"/{id}\",\"href\":\"http://localhost:8080/lh-tool/rest/helper_types/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of("SELECT * FROM helper_type WHERE name='Test'")).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(
						List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM helper_type WHERE name='Test')"))
				.build()));
	}

	@Test
	public void testHelperTypeCreationDuplicate() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test')"))
				.url(REST_URL + "/helper_types/").method(Method.POST).body(HelperTypeDto.builder().name("Test").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.CONFLICT)
						.expectedResponse(
								"{\"key\":\"EX_HELPER_TYPE_ALREADY_EXISTS\",\"message\":\"A helper type with the provided name already exists.\",\"httpCode\":409}")
						.validationQueries(
								List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM helper_type WHERE name='Test')=1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(
						List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM helper_type WHERE name='Test')=1"))
				.build()));
	}

	@Test
	public void testHelperTypeCreationMissingName() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/helper_types/").method(Method.POST).body(HelperTypeDto.builder().build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_NO_NAME\",\"message\":\"The provided name is empty.\",\"httpCode\":400}")
						.validationQueries(
								List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM helper_type WHERE name='Test')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(
						List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM helper_type WHERE name='Test')"))
				.build()));
	}

	@Test
	public void testHelperTypeCreationWithId() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/helper_types/").method(Method.POST)
				.body(HelperTypeDto.builder().id(1l).name("Test").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_ILLEGAL_ID\",\"message\":\"Please don't provide an id for new entities.\",\"httpCode\":400}")
						.validationQueries(
								List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM helper_type WHERE name='Test')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(
						List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM helper_type WHERE name='Test')"))
				.build()));
	}

//  ██████╗_██╗___██╗████████╗
//  ██╔══██╗██║___██║╚══██╔══╝
//  ██████╔╝██║___██║___██║___
//  ██╔═══╝_██║___██║___██║___
//  ██║_____╚██████╔╝___██║___
//  ╚═╝______╚═════╝____╚═╝___

	@Test
	public void testHelperTypeModification() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test')"))
				.url(REST_URL + "/helper_types/1").method(Method.PUT)
				.body(HelperTypeDto.builder().id(1l).name("Changed").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Changed\",\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/helper_types/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of("SELECT * FROM helper_type WHERE name='Changed'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM helper_type WHERE name='Test')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM helper_type WHERE name='Test'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM helper_type WHERE name='Changed')"))
				.build()));
	}

	@Test
	public void testHelperTypeModificationDuplication() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Changed')"))
				.url(REST_URL + "/helper_types/1").method(Method.PUT)
				.body(HelperTypeDto.builder().id(1l).name("Changed").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.CONFLICT)
						.expectedResponse(
								"{\"key\":\"EX_HELPER_TYPE_ALREADY_EXISTS\",\"message\":\"A helper type with the provided name already exists.\",\"httpCode\":409}")
						.validationQueries(List.of("SELECT * FROM helper_type WHERE name='Changed'",
								"SELECT * FROM helper_type WHERE name='Test'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM helper_type WHERE name='Test'",
						"SELECT * FROM helper_type WHERE name='Changed'"))
				.build()));
	}

	@Test
	public void testHelperTypeModificationNotExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test')"))
				.url(REST_URL + "/helper_types/2").method(Method.PUT)
				.body(HelperTypeDto.builder().id(2l).name("Changed").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_HELPER_TYPE_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of("SELECT * FROM helper_type WHERE name='Test'",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM helper_type)=1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM helper_type WHERE name='Test'",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM helper_type)=1"))
				.build()));
	}

//  ██████╗_███████╗██╗_____███████╗████████╗███████╗
//  ██╔══██╗██╔════╝██║_____██╔════╝╚══██╔══╝██╔════╝
//  ██║__██║█████╗__██║_____█████╗_____██║___█████╗__
//  ██║__██║██╔══╝__██║_____██╔══╝_____██║___██╔══╝__
//  ██████╔╝███████╗███████╗███████╗___██║___███████╗
//  ╚═════╝_╚══════╝╚══════╝╚══════╝___╚═╝___╚══════╝

	@Test
	public void testHelperTypeDeletionOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/helper_types/1").method(Method.DELETE)
				.userTests(
						List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.NO_CONTENT)
								.expectedResponse("")
								.validationQueries(List.of(
										"SELECT 1 WHERE NOT EXISTS(SELECT * FROM helper_type WHERE name='Test1')",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM helper_type)=1"))
								.build()))
				.validationQueriesForOthers(List.of("SELECT * FROM helper_type WHERE name='Test1'",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM helper_type)=2"))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testHelperTypeDeletionNotExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/helper_types/3").method(Method.DELETE)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_HELPER_TYPE_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of("SELECT * FROM helper_type WHERE name='Test1'",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM helper_type)=2"))
						.build()))
				.validationQueriesForOthers(List.of("SELECT * FROM helper_type WHERE name='Test1'",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM helper_type)=2"))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

//  _██████╗_███████╗████████╗
//  ██╔════╝_██╔════╝╚══██╔══╝
//  ██║__███╗█████╗_____██║___
//  ██║___██║██╔══╝_____██║___
//  ╚██████╔╝███████╗___██║___
//  _╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testHelperTypeGet() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')"))
				.url(REST_URL + "/helper_types").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								ATTENDANCE_EMAIL, PUBLISHER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/helper_types/{?project_id,weekday}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"name\":\"Test1\"},{\"id\":2,\"name\":\"Test2\"}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testHelperTypeGetByProjectIdForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (3, 'Test3')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (2, 1, 2, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (3, 2, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (4, 1, 3, 2, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/helper_types?project_id=2").method(Method.GET)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/helper_types/?project_id=2{&weekday}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],"
										+ "\"content\":[{\"id\":1,\"name\":\"Test1\"}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testHelperTypeGetByWeekdayForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (3, 'Test3')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (2, 1, 2, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (3, 2, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (4, 1, 3, 2, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/helper_types?weekday=1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								ATTENDANCE_EMAIL, PUBLISHER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_HELPER_TYPE_WEEKDAY_WITHOUT_PROJECT\",\"message\":\"Please provide a valid project id with the weekday query.\",\"httpCode\":400}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testHelperTypeGetByProjectIdAndWeekdayOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (3, 'Test3')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (2, 1, 2, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (3, 2, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (4, 1, 3, 2, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/helper_types?project_id=1&weekday=1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								ATTENDANCE_EMAIL, PUBLISHER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/helper_types/?project_id=1&weekday=1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],"
										+ "\"content\":[{\"id\":1,\"name\":\"Test1\"},{\"id\":2,\"name\":\"Test2\"}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testHelperTypeGetById() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')"))
				.url(REST_URL + "/helper_types/1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								ATTENDANCE_EMAIL, PUBLISHER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Test1\",\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/helper_types/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testHelperTypeGetByIdNotExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')"))
				.url(REST_URL + "/helper_types/3").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								ATTENDANCE_EMAIL, PUBLISHER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_HELPER_TYPE_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

}
