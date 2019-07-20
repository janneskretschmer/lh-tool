package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import de.lh.tool.domain.dto.NeedDto;
import de.lh.tool.domain.dto.NeedUserDto;
import de.lh.tool.domain.dto.ProjectDto;
import de.lh.tool.domain.model.HelperType;
import de.lh.tool.domain.model.NeedUserState;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class NeedIT extends BasicRestIntegrationTest {

	@Test
	public void testNeedWorkflow() throws Exception {
		createTestUsers();

		Long project1Id = getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_1_EMAIL)
				.body(ProjectDto.builder().name("Test1").startDate(new Date(1548971153l)).endDate(new Date(1551571200l))
						.build())
				.contentType(ContentType.JSON).post(REST_URL + "/projects/").as(ProjectDto.class).getId();
		getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_2_EMAIL).body(ProjectDto.builder().name("Test2")
				.startDate(new Date(1548971153l)).endDate(new Date(1551571200l)).build()).contentType(ContentType.JSON)
				.post(REST_URL + "/projects/").then().statusCode(200);

		String url = REST_URL + "/needs/";
		NeedDto testDto = NeedDto.builder().date(new Date(1548971153l)).helperType(HelperType.CONSTRUCTION_WORKER)
				.projectId(project1Id).quantity(20).build();
		// not allowed
		RestAssured.given().body(testDto).contentType(ContentType.JSON).post(url).then().statusCode(401);
		testForUsers(r -> r.body(testDto).contentType(ContentType.JSON).post(url).then().statusCode(403),
				STORE_KEEPER_1_EMAIL, PUBLISHER_1_EMAIL, INVENTORY_MANAGER_1_EMAIL);
		// foreign project
		getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_2_EMAIL).body(testDto).contentType(ContentType.JSON).post(url)
				.then().statusCode(403);
		// own project
		NeedDto needDto = getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_1_EMAIL).body(testDto)
				.contentType(ContentType.JSON).post(url).as(NeedDto.class);
		Long needId = needDto.getId();
		assertNotNull(needId);
		assertEquals(HelperType.CONSTRUCTION_WORKER, needDto.getHelperType());
		assertEquals(project1Id, needDto.getProjectId());
		assertEquals(Integer.valueOf(20), needDto.getQuantity());

		// get by id
		needDto = getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_1_EMAIL).get(url + needId).as(NeedDto.class);
		assertEquals(needId, needDto.getId());
		assertEquals(HelperType.CONSTRUCTION_WORKER, needDto.getHelperType());
		assertEquals(project1Id, needDto.getProjectId());
		assertEquals(Integer.valueOf(20), needDto.getQuantity());
		getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_2_EMAIL).get(url + needId).then().statusCode(403);
		getRequestSpecWithJwtByEmail(ADMIN_EMAIL).get(url + needId).then().statusCode(200);

		// TODO specify dates
		// getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_1_EMAIL).when().get(url).then().body("content",
		// Matchers.iterableWithSize(4));
		getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_1_EMAIL).when().get(url).then().body("content",
				Matchers.iterableWithSize(0));
		// getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_2_EMAIL).when().get(url).then().body("content",
		// Matchers.iterableWithSize(4));
		getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_2_EMAIL).when().get(url).then().body("content",
				Matchers.iterableWithSize(0));
		// getRequestSpecWithJwtByEmail(ADMIN_EMAIL).when().get(url).then().body("content",
		// Matchers.iterableWithSize(8));
		getRequestSpecWithJwtByEmail(ADMIN_EMAIL).when().get(url).then().body("content", Matchers.iterableWithSize(0));

		RestAssured.delete(url + needId).then().statusCode(401);
		testForUsers(r -> r.delete(url + needId).then().statusCode(403), INVENTORY_MANAGER_1_EMAIL, PUBLISHER_1_EMAIL,
				STORE_KEEPER_1_EMAIL, CONSTRUCTION_SERVANT_2_EMAIL);
		getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_1_EMAIL).delete(url + needId).then().statusCode(204);

		// TODO specify dates
		getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_1_EMAIL).when().get(url).then().body("content",
				Matchers.iterableWithSize(0));
		getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_2_EMAIL).when().get(url).then().body("content",
				Matchers.iterableWithSize(0));
		getRequestSpecWithJwtByEmail(ADMIN_EMAIL).when().get(url).then().body("content", Matchers.iterableWithSize(0));

		Long need2Id = getRequestSpecWithJwtByEmail(ADMIN_EMAIL).body(testDto).contentType(ContentType.JSON).post(url)
				.as(NeedDto.class).getId();
		getRequestSpecWithJwtByEmail(ADMIN_EMAIL).delete(url + need2Id).then().statusCode(204);

		getRequestSpecWithJwtByEmail(LOCAL_COORDINATOR_1_EMAIL).body(testDto).contentType(ContentType.JSON).post(url)
				.then().statusCode(403);
		getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_1_EMAIL)
				.post(REST_URL + "/projects/" + project1Id + "/" + getUserIdByEmail(LOCAL_COORDINATOR_1_EMAIL)).then()
				.statusCode(200);
		need2Id = getRequestSpecWithJwtByEmail(LOCAL_COORDINATOR_1_EMAIL).body(testDto).contentType(ContentType.JSON)
				.post(url).as(NeedDto.class).getId();

		deleteTestUsers();
	}

	@Test
	public void testNeedUserWorkflow() throws Exception {
		createTestUsers();
		String url = REST_URL + "/needs/";
		Long project1Id = getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_1_EMAIL)
				.body(ProjectDto.builder().name("Test1").startDate(new Date(1548971153l)).endDate(new Date(1551571200l))
						.build())
				.contentType(ContentType.JSON).post(REST_URL + "/projects/").as(ProjectDto.class).getId();
		getRequestSpecWithJwtByEmail(CONSTRUCTION_SERVANT_1_EMAIL)
				.post(REST_URL + "/projects/" + project1Id + "/" + getUserIdByEmail(LOCAL_COORDINATOR_1_EMAIL)).then()
				.statusCode(200);
		Long need1Id = getRequestSpecWithJwtByEmail(LOCAL_COORDINATOR_1_EMAIL)
				.body(NeedDto.builder().date(new Date(1548971153l)).helperType(HelperType.CONSTRUCTION_WORKER)
						.projectId(project1Id).quantity(20).build())
				.contentType(ContentType.JSON).post(url).as(NeedDto.class).getId();

		getRequestSpecWithJwtByEmail(LOCAL_COORDINATOR_1_EMAIL).body(new NeedUserDto(NeedUserState.APPLIED))
				.contentType(ContentType.JSON).put(url + need1Id + "/" + getUserIdByEmail(LOCAL_COORDINATOR_1_EMAIL))
				.then().statusCode(200);
		getRequestSpecWithJwtByEmail(STORE_KEEPER_1_EMAIL).body(new NeedUserDto(NeedUserState.APPLIED))
				.contentType(ContentType.JSON).put(url + need1Id + "/" + getUserIdByEmail(STORE_KEEPER_1_EMAIL)).then()
				.statusCode(403);
		Long publisherId = getUserIdByEmail(PUBLISHER_1_EMAIL);
		getRequestSpecWithJwtByEmail(PUBLISHER_1_EMAIL).body(new NeedUserDto(NeedUserState.APPLIED))
				.contentType(ContentType.JSON).put(url + need1Id + "/" + publisherId).then().statusCode(403);
		getRequestSpecWithJwtByEmail(INVENTORY_MANAGER_1_EMAIL).body(new NeedUserDto(NeedUserState.APPLIED))
				.contentType(ContentType.JSON).put(url + need1Id + "/" + getUserIdByEmail(INVENTORY_MANAGER_1_EMAIL))
				.then().statusCode(403);
		getRequestSpecWithJwtByEmail(PUBLISHER_1_EMAIL).get(url + need1Id + "/" + publisherId).then().statusCode(403);

		getRequestSpecWithJwtByEmail(ADMIN_EMAIL).post(REST_URL + "/projects/" + project1Id + "/" + publisherId).then()
				.statusCode(200);
		assertEquals(NeedUserState.NONE, getRequestSpecWithJwtByEmail(PUBLISHER_1_EMAIL)
				.get(url + need1Id + "/" + publisherId).as(NeedUserDto.class).getState());
		getRequestSpecWithJwtByEmail(PUBLISHER_1_EMAIL).body(new NeedUserDto(NeedUserState.APPLIED))
				.contentType(ContentType.JSON).put(url + need1Id + "/" + publisherId).then().statusCode(200);
		assertEquals(NeedUserState.APPLIED, getRequestSpecWithJwtByEmail(PUBLISHER_1_EMAIL)
				.get(url + need1Id + "/" + publisherId).as(NeedUserDto.class).getState());

		testForUsers(
				r -> r.body(new NeedUserDto(NeedUserState.APPROVED)).contentType(ContentType.JSON)
						.put(url + need1Id + "/" + publisherId).then().statusCode(403),
				STORE_KEEPER_1_EMAIL, PUBLISHER_1_EMAIL, INVENTORY_MANAGER_1_EMAIL);

		getRequestSpecWithJwtByEmail(LOCAL_COORDINATOR_1_EMAIL).body(new NeedUserDto(NeedUserState.APPROVED))
				.contentType(ContentType.JSON).put(url + need1Id + "/" + publisherId).then().statusCode(200);
		assertEquals(NeedUserState.APPROVED, getRequestSpecWithJwtByEmail(PUBLISHER_1_EMAIL)
				.get(url + need1Id + "/" + publisherId).as(NeedUserDto.class).getState());
		getRequestSpecWithJwtByEmail(LOCAL_COORDINATOR_1_EMAIL).body(new NeedUserDto(NeedUserState.NONE))
				.contentType(ContentType.JSON).put(url + need1Id + "/" + publisherId).then().statusCode(200);
		getRequestSpecWithJwtByEmail(LOCAL_COORDINATOR_1_EMAIL).body(new NeedUserDto(NeedUserState.APPLIED))
				.contentType(ContentType.JSON).put(url + need1Id + "/" + publisherId).then().statusCode(200);
		assertEquals(NeedUserState.APPLIED, getRequestSpecWithJwtByEmail(PUBLISHER_1_EMAIL)
				.get(url + need1Id + "/" + publisherId).as(NeedUserDto.class).getState());
		getRequestSpecWithJwtByEmail(PUBLISHER_1_EMAIL).body(new NeedUserDto(NeedUserState.NONE))
				.contentType(ContentType.JSON).put(url + need1Id + "/" + publisherId).then().statusCode(200);
		assertEquals(NeedUserState.NONE, getRequestSpecWithJwtByEmail(PUBLISHER_1_EMAIL)
				.get(url + need1Id + "/" + publisherId).as(NeedUserDto.class).getState());
		getRequestSpecWithJwtByEmail(PUBLISHER_1_EMAIL).body(new NeedUserDto(NeedUserState.APPROVED))
				.contentType(ContentType.JSON).put(url + need1Id + "/" + publisherId).then().statusCode(403);
		deleteTestUsers();
	}
}
