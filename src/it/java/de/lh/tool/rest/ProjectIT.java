package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.junit.jupiter.api.Test;

import de.lh.tool.domain.dto.ProjectDto;
import io.restassured.http.ContentType;

public class ProjectIT extends BasicRestIntegrationTest {

	@Test
	public void testProjectCreation() throws Exception {
		createTestUsers();
		String url = REST_URL + "/projects/";
		String jwt = getJwtByEmail(ADMIN_EMAIL);
		ProjectDto dto = getRequestSpecWithJWT(jwt).body(ProjectDto.builder().name("Test1")
				.startDate(new Date(1548971153l)).endDate(new Date(1551571200l)).build()).contentType(ContentType.JSON)
				.post(url).as(ProjectDto.class);
		assertNotNull(dto.getId());
		assertEquals("Test1", dto.getName());
		assertEquals(1548971153l, dto.getStartDate().getTime());
		assertEquals(1551571200l, dto.getEndDate().getTime());
		getRequestSpecWithJWT(jwt).delete(url + dto.getId()).then().statusCode(204);
		deleteTestUsers();
	}
}
