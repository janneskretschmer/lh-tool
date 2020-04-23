package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import de.lh.tool.domain.dto.NeedDto;
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
								"{\"id\":1,\"projectHelperTypeId\":1,\"date\":1587600000000,\"quantity\":123,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/needs/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
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
				.url(REST_URL + "/needs/").method(Method.POST)
				.body(NeedDto.builder().projectHelperTypeId(1l).date(Date.valueOf("2020-04-23")).quantity(123).build())
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')"))
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"projectHelperTypeId\":1,\"date\":1587600000000,\"quantity\":123,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/needs/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
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
				.url(REST_URL + "/needs/").method(Method.POST)
				.body(NeedDto.builder().projectHelperTypeId(1l).date(Date.valueOf("2020-04-23")).quantity(123).build())
				.initializationQueries(List.of("INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO project_helper_type (id, project_id, helper_type_id, weekday, start_time, end_time) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00')",
						"INSERT INTO need (id, project_helper_type_id, quantity, date) VALUES (1, 1, 42, '2020-04-23')"))
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.CONFLICT)
						.expectedResponse(
								"{\"key\":\"EX_NEED_ALREADY_EXISTS\",\"message\":\"A need with the provided date and project helper type already exists.\",\"httpCode\":409}")
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM need)=1")).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM need)=1")).build()));
	}
}
