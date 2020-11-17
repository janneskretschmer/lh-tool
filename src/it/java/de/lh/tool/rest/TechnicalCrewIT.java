package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import de.lh.tool.rest.bean.EndpointTest;
import de.lh.tool.rest.bean.UserTest;
import io.restassured.http.Method;

public class TechnicalCrewIT extends BasicRestIntegrationTest {

//  _██████╗_███████╗████████╗
//  ██╔════╝_██╔════╝╚══██╔══╝
//  ██║__███╗█████╗_____██║___
//  ██║___██║██╔══╝_____██║___
//  ╚██████╔╝███████╗___██║___
//  _╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testTechnicalCrewGet() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `technical_crew` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES (2, 'Test2')"))
				.url(REST_URL + "/technical_crews").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, STORE_KEEPER_EMAIL,
								INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/technical_crews/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"name\":\"Test1\"},{\"id\":2,\"name\":\"Test2\"}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testTechnicalCrewGetById() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `technical_crew` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES (2, 'Test2')"))
				.url(REST_URL + "/technical_crews/1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, STORE_KEEPER_EMAIL,
								INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Test1\",\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/technical_crews/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testTechnicalCrewGetByIdNotExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of("INSERT INTO `technical_crew` (`id`, `name`) VALUES (1, 'Test1')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES (2, 'Test2')"))
				.url(REST_URL + "/technical_crews/3").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, STORE_KEEPER_EMAIL,
								INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_TECHNICAL_CREW_ID\",\"message\":\"The provided technical crew id is invalid.\",\"httpCode\":400}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

}
