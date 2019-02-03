package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.junit.jupiter.api.Test;

import de.lh.tool.domain.dto.ProjectDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class ProjectIT extends BasicRestIntegrationTest {

	@Test
	public void testProjectCreation() throws Exception {
		createTestUsers();
		String url = REST_URL + "/projects/";

		String jwt = getJwtByEmail(ADMIN_EMAIL);
		ProjectDto testDto = ProjectDto.builder().name("Test1").startDate(new Date(1548971153l))
				.endDate(new Date(1551571200l)).build();
		ProjectDto dto = getRequestSpecWithJWT(jwt).body(testDto).contentType(ContentType.JSON).post(url)
				.as(ProjectDto.class);
		assertNotNull(dto.getId());
		assertEquals("Test1", dto.getName());
		assertEquals(1548971153l, dto.getStartDate().getTime());
		assertEquals(1551571200l, dto.getEndDate().getTime());
		getRequestSpecWithJWT(jwt).delete(url + dto.getId()).then().statusCode(204);

		String constructionServantJwt = getJwtByEmail(CONSTRUCTION_SERVANT_1_EMAIL);
		dto = getRequestSpecWithJWT(constructionServantJwt).body(testDto).contentType(ContentType.JSON).post(url)
				.as(ProjectDto.class);
		assertNotNull(dto.getId());
		assertEquals("Test1", dto.getName());
		assertEquals(1548971153l, dto.getStartDate().getTime());
		assertEquals(1551571200l, dto.getEndDate().getTime());
		getRequestSpecWithJWT(constructionServantJwt).delete(url + dto.getId()).then().statusCode(403);
		getRequestSpecWithJWT(jwt).delete(url + dto.getId()).then().statusCode(204);

		RestAssured.given().body(testDto).contentType(ContentType.JSON).post(url).then().statusCode(401);
		getRequestSpecWithJWT(getJwtByEmail(LOCAL_COORDINATOR_1_EMAIL)).body(testDto).contentType(ContentType.JSON)
				.post(url).then().statusCode(403);
		getRequestSpecWithJWT(getJwtByEmail(PUBLISHER_1_EMAIL)).body(testDto).contentType(ContentType.JSON).post(url)
				.then().statusCode(403);
		getRequestSpecWithJWT(getJwtByEmail(INVENTORY_MANAGER_1_EMAIL)).body(testDto).contentType(ContentType.JSON)
				.post(url).then().statusCode(403);
		getRequestSpecWithJWT(getJwtByEmail(STORE_KEEPER_1_EMAIL)).body(testDto).contentType(ContentType.JSON).post(url)
				.then().statusCode(403);
		// failen bei anderen, user bei anlegen hinzufügen
		deleteTestUsers();
	}
}
