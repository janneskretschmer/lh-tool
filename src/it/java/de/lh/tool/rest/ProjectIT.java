package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import de.lh.tool.domain.dto.ProjectDto;
import de.lh.tool.domain.dto.UserDto;
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
		ProjectDto dto = getRequestSpecWithJwt(jwt).body(testDto).contentType(ContentType.JSON).post(url)
				.as(ProjectDto.class);
		assertNotNull(dto.getId());
		assertEquals("Test1", dto.getName());
		assertEquals(1548971153l, dto.getStartDate().getTime());
		assertEquals(1551571200l, dto.getEndDate().getTime());
		getRequestSpecWithJwt(jwt).delete(url + dto.getId()).then().statusCode(204);

		String constructionServantJwt = getJwtByEmail(CONSTRUCTION_SERVANT_1_EMAIL);
		dto = getRequestSpecWithJwt(constructionServantJwt).body(testDto).contentType(ContentType.JSON).post(url)
				.as(ProjectDto.class);
		assertNotNull(dto.getId());
		assertEquals("Test1", dto.getName());
		assertEquals(1548971153l, dto.getStartDate().getTime());
		assertEquals(1551571200l, dto.getEndDate().getTime());
		getRequestSpecWithJwt(constructionServantJwt).delete(url + dto.getId()).then().statusCode(403);
		getRequestSpecWithJwt(jwt).delete(url + dto.getId()).then().statusCode(204);

		RestAssured.given().body(testDto).contentType(ContentType.JSON).post(url).then().statusCode(401);
		getRequestSpecWithJwtByEmail(LOCAL_COORDINATOR_1_EMAIL).body(testDto).contentType(ContentType.JSON).post(url)
				.then().statusCode(403);
		getRequestSpecWithJwtByEmail(PUBLISHER_1_EMAIL).body(testDto).contentType(ContentType.JSON).post(url).then()
				.statusCode(403);
		getRequestSpecWithJwtByEmail(INVENTORY_MANAGER_1_EMAIL).body(testDto).contentType(ContentType.JSON).post(url)
				.then().statusCode(403);
		getRequestSpecWithJwtByEmail(STORE_KEEPER_1_EMAIL).body(testDto).contentType(ContentType.JSON).post(url).then()
				.statusCode(403);
		deleteTestUsers();
	}

	@Test
	public void testProjectUserCreation() throws Exception {
		createTestUsers();
		String url = REST_URL + "/projects/";

		String jwt = getJwtByEmail(CONSTRUCTION_SERVANT_1_EMAIL);
		ProjectDto dto = getRequestSpecWithJwt(jwt).body(ProjectDto.builder().name("Test1")
				.startDate(new Date(1548971153l)).endDate(new Date(1551571200l)).build()).contentType(ContentType.JSON)
				.post(url).as(ProjectDto.class);

		String adminJwt = getJwtByEmail(ADMIN_EMAIL);
		Long adminId = getRequestSpecWithJwt(adminJwt).get(REST_URL + "/users/current").as(UserDto.class).getId();
		String jwt2 = getJwtByEmail(CONSTRUCTION_SERVANT_2_EMAIL);
		getRequestSpecWithJwt(jwt2).post(url + dto.getId() + "/" + adminId).then().statusCode(403);
		Long constructionServant2Id = getRequestSpecWithJwt(jwt2).get(REST_URL + "/users/current").as(UserDto.class)
				.getId();
		getRequestSpecWithJwt(jwt2).when().get(url).then().body("content", Matchers.iterableWithSize(0));
		getRequestSpecWithJwt(adminJwt).when().get(url).then().body("content", Matchers.iterableWithSize(1));
		getRequestSpecWithJwt(adminJwt).post(url + dto.getId() + "/" + constructionServant2Id).then().statusCode(200);
		getRequestSpecWithJwt(jwt2).when().get(url).then().body("content", Matchers.iterableWithSize(1));
		getRequestSpecWithJwtByEmail(LOCAL_COORDINATOR_1_EMAIL).post(url + dto.getId() + "/" + adminId).then()
				.statusCode(403);
		getRequestSpecWithJwt(getJwtByEmail(PUBLISHER_1_EMAIL)).post(url + dto.getId() + "/" + adminId).then()
				.statusCode(403);
		getRequestSpecWithJwt(getJwtByEmail(INVENTORY_MANAGER_1_EMAIL)).post(url + dto.getId() + "/" + adminId).then()
				.statusCode(403);
		getRequestSpecWithJwt(getJwtByEmail(STORE_KEEPER_1_EMAIL)).post(url + dto.getId() + "/" + adminId).then()
				.statusCode(403);
		RestAssured.given().post(url + dto.getId() + "/" + adminId).then().statusCode(401);
		getRequestSpecWithJwt(jwt2).post(url + dto.getId() + "/" + adminId).then().statusCode(200);

		Long project2Id = getRequestSpecWithJwt(jwt).body(ProjectDto.builder().name("Test2")
				.startDate(new Date(1548971153l)).endDate(new Date(1551571200l)).build()).contentType(ContentType.JSON)
				.post(url).as(ProjectDto.class).getId();
		getRequestSpecWithJwt(jwt2).when().get(url).then().body("content", Matchers.iterableWithSize(1));
		getRequestSpecWithJwt(adminJwt).when().get(url).then().body("content", Matchers.iterableWithSize(2));
		Long project3Id = getRequestSpecWithJwt(jwt2).body(ProjectDto.builder().name("Test3")
				.startDate(new Date(1548971153l)).endDate(new Date(1551571200l)).build()).contentType(ContentType.JSON)
				.post(url).as(ProjectDto.class).getId();
		getRequestSpecWithJwt(jwt2).when().get(url).then().body("content", Matchers.iterableWithSize(2));
		getRequestSpecWithJwt(adminJwt).when().get(url).then().body("content", Matchers.iterableWithSize(3));

		ProjectDto projectDto = getRequestSpecWithJwt(adminJwt).when().get(url + project3Id).as(ProjectDto.class);
		assertNotNull(projectDto.getId());
		assertEquals("Test3", projectDto.getName());
		getRequestSpecWithJwt(jwt2).when().get(url + project3Id).then().statusCode(200);
		getRequestSpecWithJwt(adminJwt).when().get(url + project2Id).then().statusCode(200);
		getRequestSpecWithJwt(jwt2).when().get(url + project2Id).then().statusCode(403);
		RestAssured.when().get(url + project2Id).then().statusCode(401);

		getRequestSpecWithJwtByEmail(LOCAL_COORDINATOR_1_EMAIL).delete(url + dto.getId() + "/" + adminId).then()
				.statusCode(403);
		getRequestSpecWithJwt(getJwtByEmail(PUBLISHER_1_EMAIL)).delete(url + dto.getId() + "/" + adminId).then()
				.statusCode(403);
		getRequestSpecWithJwt(getJwtByEmail(INVENTORY_MANAGER_1_EMAIL)).delete(url + dto.getId() + "/" + adminId).then()
				.statusCode(403);
		getRequestSpecWithJwt(getJwtByEmail(STORE_KEEPER_1_EMAIL)).delete(url + dto.getId() + "/" + adminId).then()
				.statusCode(403);
		RestAssured.given().delete(url + dto.getId() + "/" + adminId).then().statusCode(401);
		getRequestSpecWithJwt(jwt2).delete(url + dto.getId() + "/" + adminId).then().statusCode(204);

		getRequestSpecWithJwt(adminJwt).delete(url + dto.getId()).then().statusCode(204);
		getRequestSpecWithJwt(adminJwt).delete(url + project2Id).then().statusCode(204);
		getRequestSpecWithJwt(adminJwt).delete(url + project3Id).then().statusCode(204);

		deleteTestUsers();
	}
}
