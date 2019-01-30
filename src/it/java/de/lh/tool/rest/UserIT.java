package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.lh.tool.IntegrationTestUtil;
import de.lh.tool.domain.dto.JwtAuthenticationDto;
import de.lh.tool.domain.dto.UserCreationDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.model.User.Gender;
import io.restassured.http.ContentType;

public class UserIT {

	@BeforeAll
	public static void waitForServer() {
		IntegrationTestUtil.waitForLocalTomcat();
	}

	@Test
	public void testUserCreation() throws Exception {
		String url = IntegrationTestUtil.REST_URL + "/login/";
		JwtAuthenticationDto authenticationDto = IntegrationTestUtil.getRequestSpecWithAdminLogin().when().post(url)
				.as(JwtAuthenticationDto.class);
		assertNotNull(authenticationDto.getAccessToken());
		UserCreationDto userCreationDto = new UserCreationDto();
		userCreationDto.setEmail("test-construction-servant@lh-tool.de");
		userCreationDto.setFirstName("construction");
		userCreationDto.setLastName("servant");
		userCreationDto.setGender(Gender.MALE.name());
		url = IntegrationTestUtil.REST_URL + "/users/";
		UserDto userDto = IntegrationTestUtil.getRequestSpecWithJWT(authenticationDto.getAccessToken()).body(userCreationDto)
				.contentType(ContentType.JSON).post(url).as(UserDto.class);
		assertNull(userDto.getBusinessNumber());
		assertEquals("test-construction-servant@lh-tool.de", userDto.getEmail());
		assertEquals("construction", userDto.getFirstName());
		assertEquals("servant", userDto.getLastName());
		assertEquals(Gender.MALE.name(), userDto.getGender());
		assertNull(userDto.getMobileNumber());
		assertNull(userDto.getProfession());
		assertNull(userDto.getSkills());
		assertNull(userDto.getTelephoneNumber());
		IntegrationTestUtil.getRequestSpecWithJWT(authenticationDto.getAccessToken()).when().get(url).then().body("content",
				Matchers.iterableWithSize(2));
		IntegrationTestUtil.getRequestSpecWithJWT(authenticationDto.getAccessToken()).body(userDto).contentType(ContentType.JSON)
				.delete(url).then().statusCode(200);
		IntegrationTestUtil.getRequestSpecWithJWT(authenticationDto.getAccessToken()).when().get(url).then().body("content",
				Matchers.iterableWithSize(1));
	}
}
