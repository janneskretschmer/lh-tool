package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import de.lh.tool.domain.dto.StoreDto;
import de.lh.tool.domain.model.StoreType;
import de.lh.tool.rest.bean.EndpointTest;
import de.lh.tool.rest.bean.UserTest;
import io.restassured.http.Method;

public class StoreIT extends BasicRestIntegrationTest {
//  ██████╗__██████╗_███████╗████████╗
//  ██╔══██╗██╔═══██╗██╔════╝╚══██╔══╝
//  ██████╔╝██║___██║███████╗___██║___
//  ██╔═══╝_██║___██║╚════██║___██║___
//  ██║_____╚██████╔╝███████║___██║___
//  ╚═╝______╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testPost() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of()).url(REST_URL + "/stores").method(Method.POST)
				.body(StoreDto.builder().name("created").type(StoreType.MAIN).address("created address").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"type\":\"MAIN\",\"name\":\"created\",\"address\":\"created address\"}")
						.validationQueries(List.of(
								"SELECT * FROM store WHERE id =1 AND name='created' AND type='MAIN' AND address='created address'",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM store) = 1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM store) = 0")).build()));
	}

//  ██████╗_██╗___██╗████████╗
//  ██╔══██╗██║___██║╚══██╔══╝
//  ██████╔╝██║___██║___██║___
//  ██╔═══╝_██║___██║___██║___
//  ██║_____╚██████╔╝___██║___
//  ╚═╝______╚═════╝____╚═╝___

	@Test
	public void testPutOwn() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot1', 'Description1', '0'), ('2', '1', 'Slot2', 'Description2', '1'), ('3', '3', 'Slot3', 'Description3', '0'), ('4', '3', 'Slot4', 'Description4', '1')"))
				.url(REST_URL + "/stores/3").method(Method.PUT)
				.body(StoreDto.builder().id(3l).name("changed").type(StoreType.MAIN).address("changed address").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":3,\"type\":\"MAIN\",\"name\":\"changed\",\"address\":\"changed address\"}")
						.validationQueries(List.of(
								"SELECT * FROM store WHERE id = 3 AND name='changed' AND type='MAIN' AND address='changed address'",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM store WHERE id = 3 AND name='InRange'",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
				.build()));
	}

	@Test
	public void testPutForeign() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot1', 'Description1', '0'), ('2', '1', 'Slot2', 'Description2', '1'), ('3', '3', 'Slot3', 'Description3', '0'), ('4', '3', 'Slot4', 'Description4', '1')"))
				.url(REST_URL + "/stores/1").method(Method.PUT)
				.body(StoreDto.builder().id(1l).name("changed").type(StoreType.MAIN).address("changed address").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"type\":\"MAIN\",\"name\":\"changed\",\"address\":\"changed address\"}")
						.validationQueries(List.of(
								"SELECT * FROM store WHERE id =1 AND name='changed' AND type='MAIN' AND address='changed address'",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM store WHERE id = 1 AND name='NoProject'",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
				.build()));
	}

	@Test
	public void testPutNotExisting() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot1', 'Description1', '0'), ('2', '1', 'Slot2', 'Description2', '1'), ('3', '3', 'Slot3', 'Description3', '0'), ('4', '3', 'Slot4', 'Description4', '1')"))
				.url(REST_URL + "/stores/4").method(Method.PUT)
				.body(StoreDto.builder().id(4l).name("changed").type(StoreType.MAIN).address("changed address").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_STORE_ID\",\"message\":\"The provided store id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of("SELECT * FROM store WHERE id = 1 AND name='NoProject'",
								"SELECT * FROM store WHERE id = 2 AND name='Expired'",
								"SELECT * FROM store WHERE id = 3 AND name='InRange'",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM store WHERE id = 1 AND name='NoProject'",
						"SELECT * FROM store WHERE id = 2 AND name='Expired'",
						"SELECT * FROM store WHERE id = 3 AND name='InRange'",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
				.build()));
	}

//  ██████╗_███████╗██╗_____███████╗████████╗███████╗
//  ██╔══██╗██╔════╝██║_____██╔════╝╚══██╔══╝██╔════╝
//  ██║__██║█████╗__██║_____█████╗_____██║___█████╗__
//  ██║__██║██╔══╝__██║_____██╔══╝_____██║___██╔══╝__
//  ██████╔╝███████╗███████╗███████╗___██║___███████╗
//  ╚═════╝_╚══════╝╚══════╝╚══════╝___╚═╝___╚══════╝

	@Test
	public void testDeleteForeign() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot1', 'Description1', '0'), ('2', '1', 'Slot2', 'Description2', '1'), ('3', '2', 'Slot3', 'Description3', '0'), ('4', '2', 'Slot4', 'Description4', '1')"))
				.url(REST_URL + "/stores/1").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT)
						.validationQueries(List.of("SELECT 1 WHERE NOT EXISTS (SELECT * FROM store WHERE id = 1)",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 2",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=1) = 0",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 2"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM store WHERE id = 1 AND name='NoProject'",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=1) = 2",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
				.build()));
	}

	@Test
	public void testDeleteOwn() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot1', 'Description1', '0'), ('2', '1', 'Slot2', 'Description2', '1'), ('3', '3', 'Slot3', 'Description3', '0'), ('4', '3', 'Slot4', 'Description4', '1')"))
				.url(REST_URL + "/stores/3").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT)
						.validationQueries(List.of("SELECT 1 WHERE NOT EXISTS (SELECT * FROM store WHERE id = 3)",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 2",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 0",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 2"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM store WHERE id = 3 AND name='InRange'",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
				.build()));
	}

	@Test
	public void testDeleteNotExisting() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot1', 'Description1', '0'), ('2', '1', 'Slot2', 'Description2', '1'), ('3', '3', 'Slot3', 'Description3', '0'), ('4', '3', 'Slot4', 'Description4', '1')"))
				.url(REST_URL + "/stores/4").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_STORE_ID\",\"message\":\"The provided store id is invalid.\",\"httpCode\":400}")
						.validationQueries(List.of("SELECT * FROM store WHERE id = 3 AND name='InRange'",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM store WHERE id = 3 AND name='InRange'",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
				.build()));
	}

	@Test
	public void testDeleteNotEmpty() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot1', 'Description1', '0'), ('2', '1', 'Slot2', 'Description2', '1'), ('3', '3', 'Slot3', 'Description3', '0'), ('4', '3', 'Slot4', 'Description4', '1')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'id', '0', 'name', NULL, '1', 'Stück', '0', '0', '0', '1')"))
				.url(REST_URL + "/stores/1").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_STORE_NOT_EMPTY\",\"message\":\"The store is not empty.\",\"httpCode\":400}")
						.validationQueries(List.of("SELECT * FROM store WHERE id = 3 AND name='InRange'",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM store WHERE id = 3 AND name='InRange'",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
				.build()));
	}

//  _██████╗_███████╗████████╗
//  ██╔════╝_██╔════╝╚══██╔══╝
//  ██║__███╗█████╗_____██║___
//  ██║___██║██╔══╝_____██║___
//  ╚██████╔╝███████╗___██║___
//  _╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testGetOwn() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');"))
				.url(REST_URL + "/stores").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("[{\"id\":1,\"type\":\"MAIN\","
								+ "\"name\":\"NoProject\",\"address\":\"Adresse des Hauptlagers\"},{\"id\":2,\"type\":\"STANDARD\","
								+ "\"name\":\"Expired\",\"address\":\"Adresse des\\r\\n1234. Lagers\"},{\"id\":3,\"type\":\"MOBILE\","
								+ "\"name\":\"InRange\",\"address\":\"Adresse des Magazins\"}]")
						.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"[{\"id\":3,\"type\":\"MOBILE\",\"name\":\"InRange\",\"address\":\"Adresse des Magazins\"}]")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testGetForeign() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');"))
				.url(REST_URL + "/stores").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse("[{\"id\":1,\"type\":\"MAIN\","
								+ "\"name\":\"NoProject\",\"address\":\"Adresse des Hauptlagers\"},{\"id\":2,\"type\":\"STANDARD\","
								+ "\"name\":\"Expired\",\"address\":\"Adresse des\\r\\n1234. Lagers\"},{\"id\":3,\"type\":\"MOBILE\","
								+ "\"name\":\"InRange\",\"address\":\"Adresse des Magazins\"}]")
						.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse("[]").build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testGetByIdOwn() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');"))
				.url(REST_URL + "/stores/3").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL,
								STORE_KEEPER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":3,\"type\":\"MOBILE\",\"name\":\"InRange\",\"address\":\"Adresse des Magazins\"}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testGetByIdForeign() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');"))
				.url(REST_URL + "/stores/1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"type\":\"MAIN\",\"name\":\"NoProject\",\"address\":\"Adresse des Hauptlagers\"}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testGetByIdNotExisting() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');"))
				.url(REST_URL + "/stores/4").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL,
								STORE_KEEPER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_STORE_ID\",\"message\":\"The provided store id is invalid.\",\"httpCode\":400}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}
}
