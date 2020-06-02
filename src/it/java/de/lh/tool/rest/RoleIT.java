package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import de.lh.tool.rest.bean.EndpointTest;
import de.lh.tool.rest.bean.UserTest;
import io.restassured.http.Method;

public class RoleIT extends BasicRestIntegrationTest {
//  _██████╗_███████╗████████╗
//  ██╔════╝_██╔════╝╚══██╔══╝
//  ██║__███╗█████╗_____██║___
//  ██║___██║██╔══╝_____██║___
//  ╚██████╔╝███████╗___██║___
//  _╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testRolesGet() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/roles").method(Method.GET).userTests(List.of(//
						UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/roles/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"role\":\"ROLE_STORE_KEEPER\"},{\"role\":\"ROLE_INVENTORY_MANAGER\"},{\"role\":\"ROLE_ATTENDANCE\"},{\"role\":\"ROLE_CONSTRUCTION_SERVANT\"},{\"role\":\"ROLE_LOCAL_COORDINATOR\"},{\"role\":\"ROLE_ADMIN\"},{\"role\":\"ROLE_PUBLISHER\"}]}")
								.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/roles/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"role\":\"ROLE_STORE_KEEPER\"},{\"role\":\"ROLE_INVENTORY_MANAGER\"},{\"role\":\"ROLE_ATTENDANCE\"},{\"role\":\"ROLE_CONSTRUCTION_SERVANT\"},{\"role\":\"ROLE_LOCAL_COORDINATOR\"},{\"role\":\"ROLE_PUBLISHER\"}]}")
								.build(),
						UserTest.builder().emails(List.of(LOCAL_COORDINATOR_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/roles/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"role\":\"ROLE_STORE_KEEPER\"},{\"role\":\"ROLE_PUBLISHER\"}]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}
}
