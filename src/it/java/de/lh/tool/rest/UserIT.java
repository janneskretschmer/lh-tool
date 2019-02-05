package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import de.lh.tool.domain.dto.UserCreationDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.model.User.Gender;
import io.restassured.http.ContentType;

public class UserIT extends BasicRestIntegrationTest {

	@Test
	public void testUserCreation() throws Exception {
		String jwt = getJwtByEmail(ADMIN_EMAIL);
		assertNotNull(jwt);
		UserCreationDto userCreationDto = new UserCreationDto();
		userCreationDto.setEmail("test-construction-servant@lh-tool.de");
		userCreationDto.setFirstName("construction");
		userCreationDto.setLastName("servant");
		userCreationDto.setGender(Gender.MALE.name());
		String url = REST_URL + "/users/";
		UserDto userDto = getRequestSpecWithJwt(jwt).body(userCreationDto).contentType(ContentType.JSON).post(url)
				.as(UserDto.class);
		assertNull(userDto.getBusinessNumber());
		assertEquals("test-construction-servant@lh-tool.de", userDto.getEmail());
		assertEquals("construction", userDto.getFirstName());
		assertEquals("servant", userDto.getLastName());
		assertEquals(Gender.MALE.name(), userDto.getGender());
		assertNull(userDto.getMobileNumber());
		assertNull(userDto.getProfession());
		assertNull(userDto.getSkills());
		assertNull(userDto.getTelephoneNumber());
		getRequestSpecWithJwt(jwt).when().get(url).then().body("content", Matchers.iterableWithSize(2));
		getRequestSpecWithJwt(jwt).delete(url + userDto.getId()).then().statusCode(204);
		getRequestSpecWithJwt(jwt).when().get(url).then().body("content", Matchers.iterableWithSize(1));
	}
}
