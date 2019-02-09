package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import de.lh.tool.domain.dto.NeedDto;
import de.lh.tool.domain.dto.ProjectDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.model.HelperType;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class NeedIT extends BasicRestIntegrationTest {

	@Test
	public void testNeedWorkflow() throws Exception {
		createTestUsers();
		String jwt = getJwtByEmail(CONSTRUCTION_SERVANT_1_EMAIL);
		String jwt2 = getJwtByEmail(CONSTRUCTION_SERVANT_2_EMAIL);
		String adminJwt = getJwtByEmail(ADMIN_EMAIL);

		Long project1Id = getRequestSpecWithJwt(jwt)
				.body(ProjectDto.builder().name("Test1").startDate(new Date(1548971153l)).endDate(new Date(1551571200l))
						.build())
				.contentType(ContentType.JSON).post(REST_URL + "/projects/").as(ProjectDto.class).getId();
		Long project2Id = getRequestSpecWithJwt(jwt2)
				.body(ProjectDto.builder().name("Test2").startDate(new Date(1548971153l)).endDate(new Date(1551571200l))
						.build())
				.contentType(ContentType.JSON).post(REST_URL + "/projects/").as(ProjectDto.class).getId();

		String url = REST_URL + "/needs/";
		NeedDto testDto = NeedDto.builder().date(new Date(1548971153l)).helperType(HelperType.CONSTRUCTION_WORKER)
				.projectId(project1Id).quantity(20).build();
		// not allowed
		RestAssured.given().body(testDto).contentType(ContentType.JSON).post(url).then().statusCode(401);
		getRequestSpecWithJwtByEmail(INVENTORY_MANAGER_1_EMAIL).body(testDto).contentType(ContentType.JSON).post(url)
				.then().statusCode(403);
		getRequestSpecWithJwtByEmail(PUBLISHER_1_EMAIL).body(testDto).contentType(ContentType.JSON).post(url).then()
				.statusCode(403);
		getRequestSpecWithJwtByEmail(STORE_KEEPER_1_EMAIL).body(testDto).contentType(ContentType.JSON).post(url).then()
				.statusCode(403);
		// foreign project
		getRequestSpecWithJwt(jwt2).body(testDto).contentType(ContentType.JSON).post(url).then().statusCode(403);
		// own project
		NeedDto needDto = getRequestSpecWithJwt(jwt).body(testDto).contentType(ContentType.JSON).post(url)
				.as(NeedDto.class);
		assertNotNull(needDto.getId());
		assertEquals(HelperType.CONSTRUCTION_WORKER, needDto.getHelperType());
		assertEquals(project1Id, needDto.getProjectId());
		assertEquals(Integer.valueOf(20), needDto.getQuantity());

		getRequestSpecWithJwt(jwt).when().get(url).then().body("content", Matchers.iterableWithSize(1));
		getRequestSpecWithJwt(jwt2).when().get(url).then().body("content", Matchers.iterableWithSize(0));
		getRequestSpecWithJwt(adminJwt).when().get(url).then().body("content", Matchers.iterableWithSize(1));

		RestAssured.delete(url + needDto.getId()).then().statusCode(401);
		getRequestSpecWithJwtByEmail(INVENTORY_MANAGER_1_EMAIL).delete(url + needDto.getId()).then().statusCode(403);
		getRequestSpecWithJwtByEmail(PUBLISHER_1_EMAIL).delete(url + needDto.getId()).then().statusCode(403);
		getRequestSpecWithJwtByEmail(STORE_KEEPER_1_EMAIL).delete(url + needDto.getId()).then().statusCode(403);
		getRequestSpecWithJwt(jwt2).delete(url + needDto.getId()).then().statusCode(403);
		getRequestSpecWithJwt(jwt).delete(url + needDto.getId()).then().statusCode(204);

		getRequestSpecWithJwt(jwt).when().get(url).then().body("content", Matchers.iterableWithSize(0));
		getRequestSpecWithJwt(jwt2).when().get(url).then().body("content", Matchers.iterableWithSize(0));
		getRequestSpecWithJwt(adminJwt).when().get(url).then().body("content", Matchers.iterableWithSize(0));

		Long needId = getRequestSpecWithJwt(adminJwt).body(testDto).contentType(ContentType.JSON).post(url)
				.as(NeedDto.class).getId();
		getRequestSpecWithJwt(adminJwt).delete(url + needId).then().statusCode(204);

		String coordinatorJwt = getJwtByEmail(LOCAL_COORDINATOR_1_EMAIL);
		getRequestSpecWithJwt(coordinatorJwt).body(testDto).contentType(ContentType.JSON).post(url).then()
				.statusCode(403);
		getRequestSpecWithJwt(jwt).post(REST_URL + "/projects/" + project1Id + "/"
				+ getRequestSpecWithJwt(coordinatorJwt).get(REST_URL + "/users/current").as(UserDto.class).getId())
				.then().statusCode(200);
		needId = getRequestSpecWithJwt(coordinatorJwt).body(testDto).contentType(ContentType.JSON).post(url)
				.as(NeedDto.class).getId();

		deleteTestUsers();
	}
}
