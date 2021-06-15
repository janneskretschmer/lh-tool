package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import de.lh.tool.domain.dto.ProjectDto;
import de.lh.tool.domain.dto.ProjectHelperTypeDto;
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
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/projects/").method(Method.POST)
				.body(ProjectDto.builder().name("Test").startDate(new Date(1548971153l)).endDate(new Date(1551571200l))
						.build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Test\",\"startDate\":1548971153,\"endDate\":1551571200}")
						.validationQueries(List.of("SELECT * FROM project WHERE name='Test'",
								"SELECT * FROM project_user WHERE project_id=(SELECT id FROM project WHERE name='Test') AND user_id=(SELECT id FROM user WHERE email=:email)"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(
						List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project WHERE name='Test')"))
				.build()));
	}

	@Test
	public void testProjectCreationDuplicate() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
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
				.build()));
	}

	// TODO test missing values

	@Test
	public void testProjectUserCreationForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `email`) VALUES ('1000', 'Tes', 'Ter', 'MALE','tester@lh-tool.de');"))
				.url(REST_URL + "/projects/1/users/1000").method(Method.POST)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":1,\"projectId\":1,\"userId\":1000}")
						.validationQueries(List.of("SELECT * FROM project_user WHERE project_id=1 AND user_id=1000"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_user WHERE project_id=1 AND user_id=1000)"))
				.build()));
	}

	@Test
	public void testProjectUserCreationOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `email`) VALUES ('1000', 'Tes', 'Ter', 'MALE','tester@lh-tool.de');"))
				.url(REST_URL + "/projects/1/users/1000").method(Method.POST)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":" + (IntegrationTestRestService.getDefaultEmails().size() + 1)
								+ ",\"projectId\":1,\"userId\":1000}")
						.validationQueries(List.of("SELECT * FROM project_user WHERE project_id=1 AND user_id=1000"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_user WHERE project_id=1 AND user_id=1000)"))
				.build()));
	}

	// TODO test non existing project and user ids

	@Test
	public void testProjectHelperTypesCreationForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')"))
				.url(REST_URL + "/projects/1/helper_types").method(Method.POST)
				.body(ProjectHelperTypeDto.builder().projectId(1l).helperTypeId(1l).weekday(3).startTime("12:00")
						.endTime("13:00").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"projectId\":1,\"helperTypeId\":1,\"weekday\":3,\"startTime\":\"12:00\",\"endTime\":\"13:00\"}")
						.validationQueries(List.of(
								"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=3 AND start_time='12:00' AND end_time='13:00'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type)"))
				.build()));
	}

	@Test
	public void testProjectHelperTypesCreationOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.url(REST_URL + "/projects/1/helper_types").method(Method.POST)
				.body(ProjectHelperTypeDto.builder().projectId(1l).helperTypeId(1l).weekday(3).startTime("12:00")
						.endTime("13:00").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"projectId\":1,\"helperTypeId\":1,\"weekday\":3,\"startTime\":\"12:00\",\"endTime\":\"13:00\"}")
						.validationQueries(List.of(
								"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=3 AND start_time='12:00' AND end_time='13:00'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type)"))
				.build()));
	}

	@Test
	public void testProjectHelperTypesCreationNoEndTime() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.url(REST_URL + "/projects/1/helper_types").method(Method.POST)
				.body(ProjectHelperTypeDto.builder().projectId(1l).helperTypeId(1l).weekday(3).startTime("12:00")
						.build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"projectId\":1,\"helperTypeId\":1,\"weekday\":3,\"startTime\":\"12:00\",\"endTime\":null}")
						.validationQueries(List.of(
								"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=3 AND start_time='12:00' AND end_time IS NULL"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type)"))
				.build()));
	}

	@Test
	public void testProjectHelperTypesCreationNotExistingProject() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.url(REST_URL + "/projects/1/helper_types").method(Method.POST)
				.body(ProjectHelperTypeDto.builder().projectId(2l).helperTypeId(1l).weekday(3).startTime("12:00")
						.endTime("13:00").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_PROJECT_ID\",\"message\":\"The provided project id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type)"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type)"))
				.build()));
	}

	@Test
	public void testProjectHelperTypesCreationNotExistingHelperType() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.url(REST_URL + "/projects/1/helper_types").method(Method.POST)
				.body(ProjectHelperTypeDto.builder().projectId(1l).helperTypeId(2l).weekday(3).startTime("12:00")
						.endTime("13:00").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_HELPER_TYPE_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type)"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type)"))
				.build()));
	}

//  ██████╗_██╗___██╗████████╗
//  ██╔══██╗██║___██║╚══██╔══╝
//  ██████╔╝██║___██║___██║___
//  ██╔═══╝_██║___██║___██║___
//  ██║_____╚██████╔╝___██║___
//  ╚═╝______╚═════╝____╚═╝___

	@Test
	public void testProjectModificationForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test123', '2020-04-09', '2020-04-24')"))
				.url(REST_URL + "/projects/1").method(Method.PUT)
				.body(ProjectDto.builder().name("Test").startDate(new Date(1548971153000l))
						.endDate(new Date(1551571200000l)).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Test\",\"startDate\":1548971153000,\"endDate\":1551571200000}")
						.validationQueries(List.of(
								"SELECT * FROM project WHERE name='Test' AND start_date='2019-01-31' AND end_date='2019-03-03'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM project WHERE name='Test123' AND start_date='2020-04-09' AND end_date='2020-04-24'"))
				.build()));
	}

	@Test
	public void testProjectModificationOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test123', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.url(REST_URL + "/projects/1").method(Method.PUT)
				.body(ProjectDto.builder().name("Test").startDate(new Date(1548971153000l))
						.endDate(new Date(1551571200000l)).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Test\",\"startDate\":1548971153000,\"endDate\":1551571200000}")
						.validationQueries(List.of(
								"SELECT * FROM project WHERE name='Test' AND start_date='2019-01-31' AND end_date='2019-03-03'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM project WHERE name='Test123' AND start_date='2020-04-09' AND end_date='2020-04-24'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE id NOT IN (SELECT user_id FROM project_user WHERE project_id=1))"))
				.build()));
	}

	@Test
	public void testProjectModificationNotExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test123', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.url(REST_URL + "/projects/2").method(Method.PUT)
				.body(ProjectDto.builder().name("Test").startDate(new Date(1548971153000l))
						.endDate(new Date(1551571200000l)).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_PROJECT_ID\",\"message\":\"The provided project id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project WHERE name='Test')",
								"SELECT * FROM project WHERE name='Test123' AND start_date='2020-04-09' AND end_date='2020-04-24'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project WHERE name='Test')",
						"SELECT * FROM project WHERE name='Test123' AND start_date='2020-04-09' AND end_date='2020-04-24'"))
				.build()));
	}

	@Test
	public void testProjectModificationDuplicate() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
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
				.build()));
	}

	@Test
	public void testProjectHelperTypesModificationForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (3, 'Test3')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '12:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (2, 1, 1, 1, '12:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (3, 2, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (4, 1, 3, 2, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/projects/1/helper_types/1").method(Method.PUT)
				.body(ProjectHelperTypeDto.builder().id(1l).projectId(1l).helperTypeId(1l).weekday(3).startTime("12:00")
						.endTime("13:00").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"projectId\":1,\"helperTypeId\":1,\"weekday\":3,\"startTime\":\"12:00\",\"endTime\":\"13:00\"}")
						.validationQueries(List.of(
								"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=3 AND start_time='12:00' AND end_time='13:00'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND start_time='07:00')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=1 AND start_time='07:00:00' AND end_time='12:00:00'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND end_time='13:00')"))
				.build()));
	}

	@Test
	public void testProjectHelperTypesModificationOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (3, 'Test3')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '12:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (2, 1, 1, 1, '12:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (3, 2, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (4, 1, 3, 2, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/projects/1/helper_types/1").method(Method.PUT)
				.body(ProjectHelperTypeDto.builder().id(1l).projectId(1l).helperTypeId(1l).weekday(3).startTime("12:00")
						.endTime("13:00").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"projectId\":1,\"helperTypeId\":1,\"weekday\":3,\"startTime\":\"12:00\",\"endTime\":\"13:00\"}")
						.validationQueries(List.of(
								"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=3 AND start_time='12:00' AND end_time='13:00'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND start_time='07:00')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=1 AND start_time='07:00:00' AND end_time='12:00:00'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND end_time='13:00')"))
				.build()));
	}

	@Test
	public void testProjectHelperTypesModificationNotExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (3, 'Test3')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '12:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (2, 1, 1, 1, '12:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (3, 2, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (4, 1, 3, 2, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/projects/1/helper_types/5").method(Method.PUT)
				.body(ProjectHelperTypeDto.builder().id(5l).projectId(1l).helperTypeId(1l).weekday(3).startTime("12:00")
						.endTime("13:00").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of(
								"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=1 AND start_time='07:00:00' AND end_time='12:00:00'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND end_time='13:00')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=1 AND start_time='07:00:00' AND end_time='12:00:00'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND end_time='13:00')"))
				.build()));
	}

	@Test
	public void testProjectHelperTypesModificationNotExistingProject() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (3, 'Test3')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '12:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (2, 1, 1, 1, '12:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (3, 2, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (4, 1, 3, 2, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/projects/3/helper_types/1").method(Method.PUT)
				.body(ProjectHelperTypeDto.builder().id(1l).projectId(3l).helperTypeId(2l).weekday(3).startTime("12:00")
						.endTime("13:00").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_PROJECT_ID\",\"message\":\"The provided project id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of(
								"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=1 AND start_time='07:00:00' AND end_time='12:00:00'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND end_time='13:00')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=1 AND start_time='07:00:00' AND end_time='12:00:00'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND end_time='13:00')"))
				.build()));
	}

	@Test
	public void testProjectHelperTypesModificationNotExistingHelperType() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (3, 'Test3')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '12:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (2, 1, 1, 1, '12:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (3, 2, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (4, 1, 3, 2, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/projects/1/helper_types/1").method(Method.PUT)
				.body(ProjectHelperTypeDto.builder().id(1l).projectId(1l).helperTypeId(5l).weekday(3).startTime("12:00")
						.endTime("13:00").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_HELPER_TYPE_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of(
								"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=1 AND start_time='07:00:00' AND end_time='12:00:00'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND end_time='13:00')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=1 AND start_time='07:00:00' AND end_time='12:00:00'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND end_time='13:00')"))
				.build()));
	}

//  ██████╗_███████╗██╗_____███████╗████████╗███████╗
//  ██╔══██╗██╔════╝██║_____██╔════╝╚══██╔══╝██╔════╝
//  ██║__██║█████╗__██║_____█████╗_____██║___█████╗__
//  ██║__██║██╔══╝__██║_____██╔══╝_____██║___██╔══╝__
//  ██████╔╝███████╗███████╗███████╗___██║___███████╗
//  ╚═════╝_╚══════╝╚══════╝╚══════╝___╚═╝___╚══════╝

	@Test
	public void testProjectDeletionForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')"))
				.url(REST_URL + "/projects/1").method(Method.DELETE)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
						.validationQueries(
								List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM project WHERE name='Test')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM project WHERE name='Test'")).build()));
	}

	@Test
	public void testProjectDeletionOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
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
				.validationQueriesForOthers(List.of("SELECT * FROM project WHERE name='Test'")).build()));
	}

	@Test
	public void testProjectDeletionNotExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')"))
				.url(REST_URL + "/projects/2").method(Method.DELETE)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_PROJECT_ID\",\"message\":\"The provided project id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testProjectUserDeletionForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `email`) VALUES ('1000', 'Tes', 'Ter', 'MALE','tester@lh-tool.de')",
						"INSERT INTO project_user(id,project_id,user_id) VALUES (1,1,1000)"))
				.url(REST_URL + "/projects/1/users/1000").method(Method.DELETE)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
						.validationQueries(List.of(
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_user WHERE project_id=1 AND user_id=1000)"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM project_user WHERE project_id=1 AND user_id=1000"))
				.build()));
	}

	@Test
	public void testProjectUserDeletionOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `email`) VALUES ('1000', 'Tes', 'Ter', 'MALE','tester@lh-tool.de');",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.url(REST_URL + "/projects/1/users/1000").method(Method.DELETE)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT).expectedResponse("")
						.validationQueries(List.of(
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_user WHERE project_id=1 AND user_id=1000)"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM project_user WHERE project_id=1 AND user_id=1000"))
				.build()));
	}

	@Test
	public void testProjectHelperTypesDeletionForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '12:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (2, 1, 2, 2, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/projects/1/helper_types/1").method(Method.DELETE)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT)
						.validationQueries(List.of(
								"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=2 AND weekday=2 AND start_time='07:00' AND end_time='17:00'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1)"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=1 AND start_time='07:00:00' AND end_time='12:00:00'",
						"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=2 AND weekday=2 AND start_time='07:00' AND end_time='17:00'"))
				.build()));
	}

	@Test
	public void testProjectHelperTypesDeletionOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '12:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (2, 1, 2, 2, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/projects/1/helper_types/1").method(Method.DELETE)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT)
						.validationQueries(List.of(
								"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=2 AND weekday=2 AND start_time='07:00' AND end_time='17:00'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1)"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=1 AND start_time='07:00:00' AND end_time='12:00:00'",
						"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=2 AND weekday=2 AND start_time='07:00' AND end_time='17:00'"))
				.build()));
	}

	@Test
	public void testProjectHelperTypesDeletionNotExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '12:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (2, 1, 2, 2, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/projects/1/helper_types/3").method(Method.DELETE)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of(
								"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=1 AND start_time='07:00:00' AND end_time='12:00:00'",
								"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=2 AND weekday=2 AND start_time='07:00' AND end_time='17:00'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of(
						"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=1 AND weekday=1 AND start_time='07:00:00' AND end_time='12:00:00'",
						"SELECT * FROM project_helper_type WHERE project_id=1 AND helper_type_id=2 AND weekday=2 AND start_time='07:00' AND end_time='17:00'"))
				.build()));
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
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test123', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.url(REST_URL + "/projects/").method(Method.GET)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"[{\"id\":1,\"name\":\"Test123\",\"startDate\":1586390400000,\"endDate\":1587686400000},{\"id\":2,\"name\":\"Test\",\"startDate\":1586390400000,\"endDate\":1587686400000}]")
						.validationQueries(List.of()).build(),
						UserTest.builder()
								.emails(List.of(CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL, ATTENDANCE_EMAIL,
										PUBLISHER_EMAIL))
								.expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"[{\"id\":1,\"name\":\"Test123\",\"startDate\":1586390400000,\"endDate\":1587686400000}]")
								.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testProjectGetByIdForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')"))
				.url(REST_URL + "/projects/1").method(Method.GET)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Test\",\"startDate\":1586390400000,\"endDate\":1587686400000}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testProjectGetByIdOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.url(REST_URL + "/projects/1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								ATTENDANCE_EMAIL, PUBLISHER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Test\",\"startDate\":1586390400000,\"endDate\":1587686400000}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testProjectGetByIdNotExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test', '2020-04-09', '2020-04-24')"))
				.url(REST_URL + "/projects/2").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								ATTENDANCE_EMAIL, PUBLISHER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_PROJECT_ID\",\"message\":\"The provided project id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testProjectHelperTypesByWeekdayForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (3, 'Test3')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '12:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (2, 1, 1, 1, '12:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (3, 2, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (4, 1, 3, 2, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/projects/1/helper_types?weekday=1&helper_type_id=1").method(Method.GET)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"[{\"id\":1,\"projectId\":1,\"helperTypeId\":1,\"weekday\":1,\"startTime\":\"07:00\",\"endTime\":\"12:00\"},{\"id\":2,\"projectId\":1,\"helperTypeId\":1,\"weekday\":1,\"startTime\":\"12:00\",\"endTime\":\"17:00\"}]")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testProjectHelperTypesByWeekdayOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (3, 'Test3')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '12:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (2, 1, 1, 1, '12:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (3, 2, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (4, 1, 3, 2, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/projects/1/helper_types?weekday=1&helper_type_id=1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								ATTENDANCE_EMAIL, PUBLISHER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"[{\"id\":1,\"projectId\":1,\"helperTypeId\":1,\"weekday\":1,\"startTime\":\"07:00\",\"endTime\":\"12:00\"},{\"id\":2,\"projectId\":1,\"helperTypeId\":1,\"weekday\":1,\"startTime\":\"12:00\",\"endTime\":\"17:00\"}]")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testProjectHelperTypes() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (3, 'Test3')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '12:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (2, 1, 1, 1, '12:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (3, 2, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (4, 1, 3, 2, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/projects/1/helper_types").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								ATTENDANCE_EMAIL, PUBLISHER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"[{\"id\":1,\"projectId\":1,\"helperTypeId\":1,\"weekday\":1,\"startTime\":\"07:00\",\"endTime\":\"12:00\"},"
										+ "{\"id\":2,\"projectId\":1,\"helperTypeId\":1,\"weekday\":1,\"startTime\":\"12:00\",\"endTime\":\"17:00\"},"
										+ "{\"id\":4,\"projectId\":1,\"helperTypeId\":3,\"weekday\":2,\"startTime\":\"07:00\",\"endTime\":\"17:00\"}]")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testProjectHelperTypesNotExistingProject() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (2, 'Test2')",
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (3, 'Test3')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (2, 'Test2', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '12:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (2, 1, 1, 1, '12:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (3, 2, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (4, 1, 3, 2, '07:00:00', '17:00:00')"))
				.url(REST_URL + "/projects/3/helper_types").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL,
								ATTENDANCE_EMAIL, PUBLISHER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_PROJECT_ID\",\"message\":\"The provided project id is invalid.\",\"httpCode\":400}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

}
