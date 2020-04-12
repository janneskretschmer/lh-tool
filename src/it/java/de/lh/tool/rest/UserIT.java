package de.lh.tool.rest;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import de.lh.tool.domain.dto.UserCreationDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.model.User.Gender;
import de.lh.tool.rest.bean.EndpointTest;
import de.lh.tool.rest.bean.UserTest;
import de.lh.tool.service.rest.testonly.IntegrationTestRestService;
import io.restassured.http.Method;

public class UserIT extends BasicRestIntegrationTest {

//  ██████╗__██████╗_███████╗████████╗
//  ██╔══██╗██╔═══██╗██╔════╝╚══██╔══╝
//  ██████╔╝██║___██║███████╗___██║___
//  ██╔═══╝_██║___██║╚════██║___██║___
//  ██║_____╚██████╔╝███████║___██║___
//  ╚═╝______╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testUserCreation() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/").method(Method.POST)
				.body(UserCreationDto.builder().email("test@lh-tool.de").firstName("Tes").lastName("Ter")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, LOCAL_COORDINATOR_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("{\"id\":" + (IntegrationTestRestService.getDefaultEmails().size() + 1)
								+ ",\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":\"MALE\",\"email\":\"test@lh-tool.de\",\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null},{\"rel\":\"/password\",\"href\":\"http://localhost:8080/lh-tool/rest/users/password\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'")).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
				.build());
	}

	// TODO test missing values
	// TODO test existing email

//  ██████╗_██╗___██╗████████╗
//  ██╔══██╗██║___██║╚══██╔══╝
//  ██████╔╝██║___██║___██║___
//  ██╔═══╝_██║___██║___██║___
//  ██║_____╚██████╔╝___██║___
//  ╚═╝______╚═════╝____╚═╝___

	@Test
	public void testUserModification() throws Exception {
		testEndpoint(EndpointTest.builder()//
				.url(REST_URL + "/users/1000").method(Method.PUT)
				.initializationQueries(List.of(
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', NULL, 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)"))
				.body(UserDto.builder().email("changed@lh-tool.de").firstName("Chan").lastName("Ged")
						.telephoneNumber("987").mobileNumber("").businessNumber("321").profession("KA").skills("nix")
						.gender(Gender.MALE.name()).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Chan\",\"lastName\":\"Ged\",\"gender\":\"MALE\",\"email\":\"changed@lh-tool.de\",\"telephoneNumber\":\"987\",\"mobileNumber\":\"\",\"businessNumber\":\"321\",\"profession\":\"KA\",\"skills\":\"nix\",\"active\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/users/1000\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM user WHERE email='changed@lh-tool.de' AND first_name='Chan' AND last_name='Ged' AND telephone_number='987' AND mobile_number='' AND business_number='321' AND profession='KA' AND skills='nix'",
								"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='test@lh-tool.de')"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM user WHERE email='test@lh-tool.de'",
						"SELECT 1 WHERE NOT EXISTS(SELECT * FROM user WHERE email='changed@lh-tool.de')"))
				.build());
	}

}
