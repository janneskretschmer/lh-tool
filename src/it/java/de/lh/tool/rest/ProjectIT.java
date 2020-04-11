package de.lh.tool.rest;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import de.lh.tool.domain.dto.ProjectDto;
import de.lh.tool.rest.bean.EndpointTest;
import de.lh.tool.rest.bean.UserTest;
import de.lh.tool.service.rest.testonly.IntegrationTestRestService;
import io.restassured.http.Method;

public class ProjectIT extends BasicRestIntegrationTest {

//  ██████╗__██████╗_███████╗████████╗
//  ██╔══██╗██╔═══██╗██╔════╝╚══██╔══╝
//  ██████╔╝██║___██║███████╗___██║___
//  ██╔═══╝_██║___██║╚════██║___██║___
//  ██║_____╚██████╔╝███████║___██║___
//  ╚═╝______╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testProjectCreation() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/projects/").method(Method.POST)
				.body(ProjectDto.builder().name("Test").startDate(new Date(1548971153l)).endDate(new Date(1551571200l))
						.build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Test\",\"startDate\":1548971153,\"endDate\":1551571200,\"links\":[{\"rel\":\"/{id}\",\"href\":\"http://localhost:8080/lh-tool/rest/projects/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of("SELECT * FROM project WHERE name='Test'",
								"SELECT * FROM project_user WHERE project_id=(SELECT id FROM project WHERE name='Test') AND user_id=(SELECT id FROM user WHERE email=:email)"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(
						List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project WHERE name='Test')"))
				.build());
	}

	@Test
	public void testProjectCreationDuplicate() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (NULL, 'Test', '2020-04-09', '2020-04-24')"))
				.url(REST_URL + "/projects/").method(Method.POST)
				.body(ProjectDto.builder().name("Test").startDate(new Date(1548971153l)).endDate(new Date(1551571200l))
						.build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.CONFLICT)
						.expectedResponse(
								"{\"key\":\"EX_PROJECT_NAME_ALREADY_EXISTS\",\"message\":\"A project with the provided name already exists.\",\"httpCode\":409}")
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM project WHERE name='Test')=1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(
						List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM project WHERE name='Test')=1"))
				.build());
	}

	// TODO test missing values

	@Test
	public void testProjectUserCreationForeign() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `email`) VALUES ('1000', 'Tes', 'Ter', 'MALE','tester@lh-tool.de');"))
				.url(REST_URL + "/projects/1/1000").method(Method.POST)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"projectId\":1,\"userId\":1000,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/projects/1/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of("SELECT * FROM project_user WHERE project_id=1 AND user_id=1000"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_user WHERE project_id=1 AND user_id=1000)"))
				.build());
	}

	@Test
	public void testProjectUserCreationOwn() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `email`) VALUES ('1000', 'Tes', 'Ter', 'MALE','tester@lh-tool.de');"))
				.url(REST_URL + "/projects/1/1000").method(Method.POST)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":" + (IntegrationTestRestService.getDefaultEmails().size() + 1)
								+ ",\"projectId\":1,\"userId\":1000,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/projects/1/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of("SELECT * FROM project_user WHERE project_id=1 AND user_id=1000"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_user WHERE project_id=1 AND user_id=1000)"))
				.build());
	}

	// TODO test non existing project and user ids

//  ██████╗_██╗___██╗████████╗
//  ██╔══██╗██║___██║╚══██╔══╝
//  ██████╔╝██║___██║___██║___
//  ██╔═══╝_██║___██║___██║___
//  ██║_____╚██████╔╝___██║___
//  ╚═╝______╚═════╝____╚═╝___

	@Test
	public void testProjectModificationForeign() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test123', '2020-04-09', '2020-04-24')"))
				.url(REST_URL + "/projects/1").method(Method.PUT)
				.body(ProjectDto.builder().name("Test").startDate(new Date(1548971153000l))
						.endDate(new Date(1551571200000l)).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Test\",\"startDate\":1548971153000,\"endDate\":1551571200000,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/projects/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM project WHERE name='Test' AND start_date='2019-01-31' AND end_date='2019-03-03'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM project WHERE name='Test123' AND start_date='2020-04-09' AND end_date='2020-04-24'"))
				.build());
	}

	@Test
	public void testProjectModificationOwn() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test123', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.url(REST_URL + "/projects/1").method(Method.PUT)
				.body(ProjectDto.builder().name("Test").startDate(new Date(1548971153000l))
						.endDate(new Date(1551571200000l)).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Test\",\"startDate\":1548971153000,\"endDate\":1551571200000,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/projects/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM project WHERE name='Test' AND start_date='2019-01-31' AND end_date='2019-03-03'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM project WHERE name='Test123' AND start_date='2020-04-09' AND end_date='2020-04-24'"))
				.build());
	}

	@Test
	public void testProjectModificationNotExisting() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test123', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.url(REST_URL + "/projects/2").method(Method.PUT)
				.body(ProjectDto.builder().name("Test").startDate(new Date(1548971153000l))
						.endDate(new Date(1551571200000l)).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project WHERE name='Test')",
								"SELECT * FROM project WHERE name='Test123' AND start_date='2020-04-09' AND end_date='2020-04-24'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project WHERE name='Test')",
						"SELECT * FROM project WHERE name='Test123' AND start_date='2020-04-09' AND end_date='2020-04-24'"))
				.build());
	}

	@Test
	public void testProjectModificationDuplicate() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test123', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test', '2020-04-09', '2020-04-24')"))
				.url(REST_URL + "/projects/1").method(Method.PUT)
				.body(ProjectDto.builder().name("Test").startDate(new Date(1548971153000l))
						.endDate(new Date(1551571200000l)).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.CONFLICT)
						.expectedResponse(
								"{\"key\":\"EX_PROJECT_NAME_ALREADY_EXISTS\",\"message\":\"A project with the provided name already exists.\",\"httpCode\":409}")
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM project WHERE name='Test')=1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(
						List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM project WHERE name='Test')=1"))
				.build());
	}

//  ██████╗_███████╗██╗_____███████╗████████╗███████╗
//  ██╔══██╗██╔════╝██║_____██╔════╝╚══██╔══╝██╔════╝
//  ██║__██║█████╗__██║_____█████╗_____██║___█████╗__
//  ██║__██║██╔══╝__██║_____██╔══╝_____██║___██╔══╝__
//  ██████╔╝███████╗███████╗███████╗___██║___███████╗
//  ╚═════╝_╚══════╝╚══════╝╚══════╝___╚═╝___╚══════╝

	@Test
	public void testProjectDeletionForeign() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')"))
				.url(REST_URL + "/projects/1").method(Method.DELETE)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
						.validationQueries(
								List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project WHERE name='Test')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM project WHERE name='Test'")).build());
	}

	@Test
	public void testProjectDeletionOwn() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.url(REST_URL + "/projects/1").method(Method.DELETE)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
						.validationQueries(
								List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project WHERE name='Test')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM project WHERE name='Test'")).build());
	}

	@Test
	public void testProjectDeletionNotExisting() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')"))
				.url(REST_URL + "/projects/2").method(Method.DELETE)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build());
	}

	@Test
	public void testProjectUserDeletionForeign() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `email`) VALUES ('1000', 'Tes', 'Ter', 'MALE','tester@lh-tool.de')",
						"INSERT INTO project_user(id,project_id,user_id) VALUES (1,1,1000)"))
				.url(REST_URL + "/projects/1/1000").method(Method.DELETE)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
						.validationQueries(List.of(
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_user WHERE project_id=1 AND user_id=1000)"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM project_user WHERE project_id=1 AND user_id=1000"))
				.build());
	}

	@Test
	public void testProjectUserDeletionOwn() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `email`) VALUES ('1000', 'Tes', 'Ter', 'MALE','tester@lh-tool.de');",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.url(REST_URL + "/projects/1/1000").method(Method.DELETE)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
						.validationQueries(List.of(
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_user WHERE project_id=1 AND user_id=1000)"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM project_user WHERE project_id=1 AND user_id=1000"))
				.build());
	}

	// TODO test deleting not existing project_user references

//  _██████╗_███████╗████████╗
//  ██╔════╝_██╔════╝╚══██╔══╝
//  ██║__███╗█████╗_____██║___
//  ██║___██║██╔══╝_____██║___
//  ╚██████╔╝███████╗___██║___
//  _╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testProjectGet() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test123', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.url(REST_URL + "/projects/").method(Method.GET)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/projects/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"name\":\"Test123\",\"startDate\":1586390400000,\"endDate\":1587686400000},{\"id\":2,\"name\":\"Test\",\"startDate\":1586390400000,\"endDate\":1587686400000}]}")
						.validationQueries(List.of()).build(),
						UserTest.builder()
								.emails(List.of(CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL, ATTENDANCE_EMAIL,
										PUBLISHER_EMAIL))
								.expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/projects/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"name\":\"Test123\",\"startDate\":1586390400000,\"endDate\":1587686400000}]}")
								.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build());
	}

	@Test
	public void testProjectGetByIdForeign() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')"))
				.url(REST_URL + "/projects/1").method(Method.GET)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Test\",\"startDate\":1586390400000,\"endDate\":1587686400000,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/projects/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build());
	}

	@Test
	public void testProjectGetByIdOwn() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.url(REST_URL + "/projects/1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List
								.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL, ATTENDANCE_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Test\",\"startDate\":1586390400000,\"endDate\":1587686400000,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/projects/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build());
	}

	@Test
	public void testProjectGetByIdNotExisting() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')"))
				.url(REST_URL + "/projects/2").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List
								.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL, ATTENDANCE_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build());
	}

}
