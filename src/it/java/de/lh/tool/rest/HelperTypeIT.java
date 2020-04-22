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
						.validationQueries(List.of("SELECT * FROM helper_type WHERE name='TEST'")).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(
						List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM helper_type WHERE name='TEST')"))
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

	// TODO test missing values

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
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM helper_type WHERE name='TEST')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM helper_type WHERE name='Test'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM helper_type WHERE name='Changed')"))
				.build()));
	}

	@Test
	public void testHelperTypeDuplication() throws Exception {
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
	public void testHelperTypeNotExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test')"))
				.url(REST_URL + "/helper_types/2").method(Method.PUT)
				.body(HelperTypeDto.builder().id(2l).name("Changed").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
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

//	@Test
//	public void testProjectDeletionForeign() throws Exception {
//		assertTrue(testEndpoint(EndpointTest.builder()//
//				.initializationQueries(List.of(
//						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')"))
//				.url(REST_URL + "/projects/1").method(Method.DELETE)
//				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
//						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
//						.validationQueries(
//								List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project WHERE name='Test')"))
//						.build()))
//				.httpCodeForOthers(HttpStatus.FORBIDDEN)
//				.validationQueriesForOthers(List.of("SELECT * FROM project WHERE name='Test'")).build()));
//	}
//
//	@Test
//	public void testProjectDeletionOwn() throws Exception {
//		assertTrue(testEndpoint(EndpointTest.builder()//
//				.initializationQueries(List.of(
//						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
//						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
//				.url(REST_URL + "/projects/1").method(Method.DELETE)
//				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
//						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
//						.validationQueries(
//								List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project WHERE name='Test')"))
//						.build()))
//				.httpCodeForOthers(HttpStatus.FORBIDDEN)
//				.validationQueriesForOthers(List.of("SELECT * FROM project WHERE name='Test'")).build()));
//	}
//
//	@Test
//	public void testProjectDeletionNotExisting() throws Exception {
//		assertTrue(testEndpoint(EndpointTest.builder()//
//				.initializationQueries(List.of(
//						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')"))
//				.url(REST_URL + "/projects/2").method(Method.DELETE)
//				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
//						.expectedHttpCode(HttpStatus.BAD_REQUEST)
//						.expectedResponse(
//								"{\"key\":\"EX_INVALID_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
//						.validationQueries(List.of()).build()))
//				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
//	}
//
//	@Test
//	public void testProjectUserDeletionForeign() throws Exception {
//		assertTrue(testEndpoint(EndpointTest.builder()//
//				.initializationQueries(List.of(
//						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
//						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `email`) VALUES ('1000', 'Tes', 'Ter', 'MALE','tester@lh-tool.de')",
//						"INSERT INTO project_user(id,project_id,user_id) VALUES (1,1,1000)"))
//				.url(REST_URL + "/projects/1/users/1000").method(Method.DELETE)
//				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
//						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
//						.validationQueries(List.of(
//								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_user WHERE project_id=1 AND user_id=1000)"))
//						.build()))
//				.httpCodeForOthers(HttpStatus.FORBIDDEN)
//				.validationQueriesForOthers(List.of("SELECT * FROM project_user WHERE project_id=1 AND user_id=1000"))
//				.build()));
//	}
//
//	@Test
//	public void testProjectUserDeletionOwn() throws Exception {
//		assertTrue(testEndpoint(EndpointTest.builder()//
//				.initializationQueries(List.of(
//						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
//						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `email`) VALUES ('1000', 'Tes', 'Ter', 'MALE','tester@lh-tool.de');",
//						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
//				.url(REST_URL + "/projects/1/users/1000").method(Method.DELETE)
//				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
//						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
//						.validationQueries(List.of(
//								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_user WHERE project_id=1 AND user_id=1000)"))
//						.build()))
//				.httpCodeForOthers(HttpStatus.FORBIDDEN)
//				.validationQueriesForOthers(List.of("SELECT * FROM project_user WHERE project_id=1 AND user_id=1000"))
//				.build()));
//	}

	// TODO test deleting not existing project_user references

//  _██████╗_███████╗████████╗
//  ██╔════╝_██╔════╝╚══██╔══╝
//  ██║__███╗█████╗_____██║___
//  ██║___██║██╔══╝_____██║___
//  ╚██████╔╝███████╗___██║___
//  _╚═════╝_╚══════╝___╚═╝___

//	@Test
//	public void testProjectGet() throws Exception {
//		assertTrue(testEndpoint(EndpointTest.builder()//
//				.initializationQueries(List.of(
//						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test123', '2020-04-09', '2020-04-24')",
//						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test', '2020-04-09', '2020-04-24')",
//						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
//				.url(REST_URL + "/projects/").method(Method.GET)
//				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
//						.expectedResponse(
//								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/projects/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"name\":\"Test123\",\"startDate\":1586390400000,\"endDate\":1587686400000},{\"id\":2,\"name\":\"Test\",\"startDate\":1586390400000,\"endDate\":1587686400000}]}")
//						.validationQueries(List.of()).build(),
//						UserTest.builder()
//								.emails(List.of(CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL, ATTENDANCE_EMAIL,
//										PUBLISHER_EMAIL))
//								.expectedHttpCode(HttpStatus.OK)
//								.expectedResponse(
//										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/projects/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"name\":\"Test123\",\"startDate\":1586390400000,\"endDate\":1587686400000}]}")
//								.validationQueries(List.of()).build()))
//				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
//	}
//
//	@Test
//	public void testProjectGetByIdForeign() throws Exception {
//		assertTrue(testEndpoint(EndpointTest.builder()//
//				.initializationQueries(List.of(
//						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')"))
//				.url(REST_URL + "/projects/1").method(Method.GET)
//				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
//						.expectedResponse(
//								"{\"id\":1,\"name\":\"Test\",\"startDate\":1586390400000,\"endDate\":1587686400000,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/projects/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
//						.validationQueries(List.of()).build()))
//				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
//	}
//
//	@Test
//	public void testProjectGetByIdOwn() throws Exception {
//		assertTrue(testEndpoint(EndpointTest.builder()//
//				.initializationQueries(List.of(
//						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
//						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
//				.url(REST_URL + "/projects/1").method(Method.GET)
//				.userTests(List.of(UserTest.builder()
//						.emails(List
//								.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL, ATTENDANCE_EMAIL))
//						.expectedHttpCode(HttpStatus.OK)
//						.expectedResponse(
//								"{\"id\":1,\"name\":\"Test\",\"startDate\":1586390400000,\"endDate\":1587686400000,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/projects/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
//						.validationQueries(List.of()).build()))
//				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
//	}
//
//	@Test
//	public void testProjectGetByIdNotExisting() throws Exception {
//		assertTrue(testEndpoint(EndpointTest.builder()//
//				.initializationQueries(List.of(
//						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')"))
//				.url(REST_URL + "/projects/2").method(Method.GET)
//				.userTests(List.of(UserTest.builder()
//						.emails(List
//								.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL, ATTENDANCE_EMAIL))
//						.expectedHttpCode(HttpStatus.BAD_REQUEST)
//						.expectedResponse(
//								"{\"key\":\"EX_INVALID_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
//						.validationQueries(List.of()).build()))
//				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
//	}

}
