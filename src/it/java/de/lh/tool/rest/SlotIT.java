package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import de.lh.tool.domain.dto.SlotDto;
import de.lh.tool.rest.bean.EndpointTest;
import de.lh.tool.rest.bean.UserTest;
import io.restassured.http.Method;

public class SlotIT extends BasicRestIntegrationTest {
//  ██████╗__██████╗_███████╗████████╗
//  ██╔══██╗██╔═══██╗██╔════╝╚══██╔══╝
//  ██████╔╝██║___██║███████╗___██║___
//  ██╔═══╝_██║___██║╚════██║___██║___
//  ██║_____╚██████╔╝███████║___██║___
//  ╚═╝______╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testPost() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');"))
				.url(REST_URL + "/slots").method(Method.POST)
				.body(SlotDto.builder().depth(1f).description("Description").height(12.3f).name("created")
						.outside(Boolean.TRUE).storeId(3l).width(12.3456f).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"storeId\":3,\"name\":\"created\",\"description\":\"Description\",\"width\":12.3456,\"height\":12.3,\"depth\":1.0,\"outside\":true,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/slots/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM slot WHERE id =1 AND store_id=3 AND name='created' AND description='Description' AND width LIKE 12.3456  AND height LIKE 12.3 AND depth LIKE 1 AND outside=1",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM store) = 3",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM slot) = 1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM slot) = 0")).build()));
	}

//  ██████╗_██╗___██╗████████╗
//  ██╔══██╗██║___██║╚══██╔══╝
//  ██████╔╝██║___██║___██║___
//  ██╔═══╝_██║___██║___██║___
//  ██║_____╚██████╔╝___██║___
//  ╚═╝______╚═════╝____╚═╝___

//	@Test
//	public void testPutOwn() throws IOException {
//		assertTrue(testEndpoint(EndpointTest.builder()//
//				.initializationQueries(List.of(
//						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
//						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
//						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
//						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
//								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
//								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');",
//						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot1', 'Description1', '10', '20', '30', '0'), ('2', '1', 'Slot2', 'Description2', NULL, NULL, NULL, '1'), ('3', '3', 'Slot3', 'Description3', '1', '2', '3', '0'), ('4', '3', 'Slot4', 'Description4', '0.5', '0.6', '0.7', '1')"))
//				.url(REST_URL + "/stores/3").method(Method.PUT)
//				.body(StoreDto.builder().id(3l).name("changed").type(StoreType.MAIN).address("changed address").build())
//				.userTests(List.of(UserTest.builder()
//						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
//						.expectedHttpCode(HttpStatus.OK)
//						.expectedResponse(
//								"{\"id\":3,\"type\":\"MAIN\",\"name\":\"changed\",\"address\":\"changed address\",\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/stores/3\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
//						.validationQueries(List.of(
//								"SELECT * FROM store WHERE id = 3 AND name='changed' AND type='MAIN' AND address='changed address'",
//								"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
//								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
//								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
//						.build()))
//				.httpCodeForOthers(HttpStatus.FORBIDDEN)
//				.validationQueriesForOthers(List.of("SELECT * FROM store WHERE id = 3 AND name='InRange'",
//						"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
//						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
//						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
//				.build()));
//	}
//
//	@Test
//	public void testPutForeign() throws IOException {
//		assertTrue(testEndpoint(EndpointTest.builder()//
//				.initializationQueries(List.of(
//						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
//						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
//						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
//						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
//								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
//								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');",
//						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot1', 'Description1', '10', '20', '30', '0'), ('2', '1', 'Slot2', 'Description2', NULL, NULL, NULL, '1'), ('3', '3', 'Slot3', 'Description3', '1', '2', '3', '0'), ('4', '3', 'Slot4', 'Description4', '0.5', '0.6', '0.7', '1')"))
//				.url(REST_URL + "/stores/1").method(Method.PUT)
//				.body(StoreDto.builder().id(1l).name("changed").type(StoreType.MAIN).address("changed address").build())
//				.userTests(List.of(UserTest.builder()
//						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
//						.expectedHttpCode(HttpStatus.OK)
//						.expectedResponse(
//								"{\"id\":1,\"type\":\"MAIN\",\"name\":\"changed\",\"address\":\"changed address\",\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/stores/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
//						.validationQueries(List.of(
//								"SELECT * FROM store WHERE id =1 AND name='changed' AND type='MAIN' AND address='changed address'",
//								"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
//								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
//								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
//						.build()))
//				.httpCodeForOthers(HttpStatus.FORBIDDEN)
//				.validationQueriesForOthers(List.of("SELECT * FROM store WHERE id = 1 AND name='NoProject'",
//						"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
//						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
//						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
//				.build()));
//	}
//
//	@Test
//	public void testPutNotExisting() throws IOException {
//		assertTrue(testEndpoint(EndpointTest.builder()//
//				.initializationQueries(List.of(
//						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
//						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
//						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
//						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '2', '1', '"
//								+ LocalDate.now().minusDays(1) + "', '" + LocalDate.now().minusDays(1)
//								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');",
//						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot1', 'Description1', '10', '20', '30', '0'), ('2', '1', 'Slot2', 'Description2', NULL, NULL, NULL, '1'), ('3', '3', 'Slot3', 'Description3', '1', '2', '3', '0'), ('4', '3', 'Slot4', 'Description4', '0.5', '0.6', '0.7', '1')"))
//				.url(REST_URL + "/stores/4").method(Method.PUT)
//				.body(StoreDto.builder().id(4l).name("changed").type(StoreType.MAIN).address("changed address").build())
//				.userTests(List.of(UserTest.builder()
//						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
//						.expectedHttpCode(HttpStatus.BAD_REQUEST)
//						.expectedResponse(
//								"{\"key\":\"EX_INVALID_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
//						.validationQueries(List.of("SELECT * FROM store WHERE id = 1 AND name='NoProject'",
//								"SELECT * FROM store WHERE id = 2 AND name='Expired'",
//								"SELECT * FROM store WHERE id = 3 AND name='InRange'",
//								"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
//								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
//								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
//						.build()))
//				.httpCodeForOthers(HttpStatus.FORBIDDEN)
//				.validationQueriesForOthers(List.of("SELECT * FROM store WHERE id = 1 AND name='NoProject'",
//						"SELECT * FROM store WHERE id = 2 AND name='Expired'",
//						"SELECT * FROM store WHERE id = 3 AND name='InRange'",
//						"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
//						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 2",
//						"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 4"))
//				.build()));
//	}

//  ██████╗_███████╗██╗_____███████╗████████╗███████╗
//  ██╔══██╗██╔════╝██║_____██╔════╝╚══██╔══╝██╔════╝
//  ██║__██║█████╗__██║_____█████╗_____██║___█████╗__
//  ██║__██║██╔══╝__██║_____██╔══╝_____██║___██╔══╝__
//  ██████╔╝███████╗███████╗███████╗___██║___███████╗
//  ╚═════╝_╚══════╝╚══════╝╚══════╝___╚═╝___╚══════╝

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
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot1', 'Description1', '10', '20', '30', '0'), ('2', '1', 'Slot2', 'Description2', NULL, NULL, NULL, '1'), ('3', '3', 'Slot3', 'Description3', '1', '2', '3', '0'), ('4', '3', 'Slot4', 'Description4', '0.5', '0.6', '0.7', '1')"))
				.url(REST_URL + "/slots/4").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT)
						.validationQueries(List.of("SELECT 1 WHERE NOT EXISTS (SELECT * FROM slot WHERE id = 4)",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot WHERE store_id=3) = 1",
								"SELECT 1 WHERE (SELECT COuNT(*) FROM slot) = 3"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(
						List.of("SELECT * FROM slot WHERE id = 4", "SELECT 1 WHERE (SELECT COuNT(*) FROM store) = 3",
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
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot1', 'Description1', '10', '20', '30', '0'), ('2', '1', 'Slot2', 'Description2', NULL, NULL, NULL, '1'), ('3', '3', 'Slot3', 'Description3', '1', '2', '3', '0'), ('4', '3', 'Slot4', 'Description4', '0.5', '0.6', '0.7', '1')"))
				.url(REST_URL + "/slots/5").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
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
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot1', 'Description1', '10', '20', '30', '0'), ('2', '1', 'Slot2', 'Description2', NULL, NULL, NULL, '1'), ('3', '3', 'Slot3', 'Description3', '1', '2', '3', '0'), ('4', '3', 'Slot4', 'Description4', '0.5', '0.6', '0.7', '1')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'id', '0', 'name', NULL, '1', 'Stück', NULL, NULL, NULL, '0', '0', '0', '1')"))
				.url(REST_URL + "/slots/1").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_SLOT_NOT_EMPTY\",\"message\":\"The slot is not empty.\",\"httpCode\":400}")
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
								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'SlotOfMain', NULL, NULL, NULL, NULL, '0'), ('2', '2', 'SlotOfStandard', 'description', '123', '12.3', '1.23', '0'), ('3', '3', 'SlotOfMobile', '', NULL, NULL, NULL, '1')"))
				.url(REST_URL + "/slots").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/slots/{?free_text,name,description,store_id}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"storeId\":1,"
										+ "\"name\":\"SlotOfMain\",\"description\":null,\"width\":null,\"height\":null,\"depth\":null,\"outside\":false},{\"id\":3,\"storeId\":3,"
										+ "\"name\":\"SlotOfMobile\",\"description\":\"\",\"width\":null,\"height\":null,\"depth\":null,\"outside\":true},{\"id\":2,\"storeId\":2,"
										+ "\"name\":\"SlotOfStandard\",\"description\":\"description\",\"width\":123.0,\"height\":12.3,\"depth\":1.23,\"outside\":false}]}")
						.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/slots/{?free_text,name,description,store_id}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":3,\"storeId\":3,"
												+ "\"name\":\"SlotOfMobile\",\"description\":\"\",\"width\":null,\"height\":null,\"depth\":null,\"outside\":true}]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testGetForeign() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', 'Adresse des\\r\\n1234. Lagers'), ('3', 'MOBILE', 'InRange', 'Adresse des Magazins')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'SlotOfMain', NULL, NULL, NULL, NULL, '0'), ('2', '2', 'SlotOfStandard', 'description', '123', '12.3', '1.23', '0'), ('3', '3', 'SlotOfMobile', '', NULL, NULL, NULL, '1')"))
				.url(REST_URL + "/slots").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/slots/{?free_text,name,description,store_id}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"storeId\":1,"
										+ "\"name\":\"SlotOfMain\",\"description\":null,\"width\":null,\"height\":null,\"depth\":null,\"outside\":false},{\"id\":3,\"storeId\":3,"
										+ "\"name\":\"SlotOfMobile\",\"description\":\"\",\"width\":null,\"height\":null,\"depth\":null,\"outside\":true},{\"id\":2,\"storeId\":2,"
										+ "\"name\":\"SlotOfStandard\",\"description\":\"description\",\"width\":123.0,\"height\":12.3,\"depth\":1.23,\"outside\":false}]}")
						.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/slots/{?free_text,name,description,store_id}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testGetByFreeText() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', '123'), ('3', 'MOBILE', 'Name with 123:)', 'Adresse des Magazins')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '2', 'NoMatch', 'Description without search phrase', 12.3, 2, 3, '0'), ('2', '3', 'InStore', 'description', '1.2', '12.3', '1.23', '0'), ('3', '1', 'InName:123', '', NULL, NULL, NULL, '1'), ('4', '1', 'InDescription', 'Description with 123xD', NULL, NULL, NULL, '1'), ('5', '1', 'InWidth', '', 12345, NULL, NULL, '1'), ('6', '1', 'InHeight', 'desc', NULL, 1231, NULL, '1'), ('7', '1', 'InDepth', '', NULL, NULL, 123.4, '1')"))
				.url(REST_URL + "/slots?free_text=123").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/slots/?free_text=123{&name,description,store_id}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":7,\"storeId\":1,"
										+ "\"name\":\"InDepth\",\"description\":\"\",\"width\":null,\"height\":null,\"depth\":123.4,\"outside\":true},{\"id\":4,\"storeId\":1,"
										+ "\"name\":\"InDescription\",\"description\":\"Description with 123xD\",\"width\":null,\"height\":null,\"depth\":null,\"outside\":true},{\"id\":6,\"storeId\":1,"
										+ "\"name\":\"InHeight\",\"description\":\"desc\",\"width\":null,\"height\":1231.0,\"depth\":null,\"outside\":true},{\"id\":3,\"storeId\":1,"
										+ "\"name\":\"InName:123\",\"description\":\"\",\"width\":null,\"height\":null,\"depth\":null,\"outside\":true},{\"id\":2,\"storeId\":3,"
										+ "\"name\":\"InStore\",\"description\":\"description\",\"width\":1.2,\"height\":12.3,\"depth\":1.23,\"outside\":false},{\"id\":5,\"storeId\":1,"
										+ "\"name\":\"InWidth\",\"description\":\"\",\"width\":12345.0,\"height\":null,\"depth\":null,\"outside\":true}]}")
						.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/slots/?free_text=123{&name,description,store_id}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testGetByName() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', '123'), ('3', 'MOBILE', 'Name with 123:)', 'Adresse des Magazins')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '2', 'In123Name', 'Description without search phrase', 12.3, 2, 3, '0'), ('2', '3', 'InStore', 'description', '1.2', '12.3', '1.23', '0'), ('3', '1', 'InName:123', '', NULL, NULL, NULL, '1'), ('4', '1', 'InDescription', 'Description with 123xD', NULL, NULL, NULL, '1'), ('5', '1', 'InWidth', '', 12345, NULL, NULL, '1'), ('6', '1', 'InHeight', 'desc', NULL, 1231, NULL, '1'), ('7', '1', 'InDepth', '', NULL, NULL, 123.4, '1')"))
				.url(REST_URL + "/slots?name=123").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/slots/?name=123{&free_text,description,store_id}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"storeId\":2,"
										+ "\"name\":\"In123Name\",\"description\":\"Description without search phrase\",\"width\":12.3,\"height\":2.0,\"depth\":3.0,\"outside\":false},{\"id\":3,\"storeId\":1,"
										+ "\"name\":\"InName:123\",\"description\":\"\",\"width\":null,\"height\":null,\"depth\":null,\"outside\":true}]}")
						.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/slots/?name=123{&free_text,description,store_id}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testGetByDescription() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', '123'), ('3', 'MOBILE', 'Name with 123:)', 'Adresse des Magazins')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '2', 'InDescription2', 'Description without 123 search phrase', 12.3, 2, 3, '0'), ('2', '3', 'InStore', 'description', '1.2', '12.3', '1.23', '0'), ('3', '1', 'InName:123', '', NULL, NULL, NULL, '1'), ('4', '1', 'InDescription', 'Description with 123xD', NULL, NULL, NULL, '1'), ('5', '1', 'InWidth', '', 12345, NULL, NULL, '1'), ('6', '1', 'InHeight', 'desc', NULL, 1231, NULL, '1'), ('7', '1', 'InDepth', '', NULL, NULL, 123.4, '1')"))
				.url(REST_URL + "/slots?description=123").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/slots/?description=123{&free_text,name,store_id}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":4,\"storeId\":1,"
										+ "\"name\":\"InDescription\",\"description\":\"Description with 123xD\",\"width\":null,\"height\":null,\"depth\":null,\"outside\":true},{\"id\":1,\"storeId\":2,"
										+ "\"name\":\"InDescription2\",\"description\":\"Description without 123 search phrase\",\"width\":12.3,\"height\":2.0,\"depth\":3.0,\"outside\":false}]}")
						.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/slots/?description=123{&free_text,name,store_id}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}

	@Test
	public void testGetByStoreId() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'MAIN', 'NoProject', 'Adresse des Hauptlagers'), ('2', 'STANDARD', 'Expired', '123'), ('3', 'MOBILE', 'Name with 123:)', 'Adresse des Magazins')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '2', 'InDescription2', 'Description without 123 search phrase', 12.3, 2, 3, '0'), ('2', '3', 'InStore', 'description', '1.2', '12.3', '1.23', '0'), ('3', '1', 'InName:123', '', NULL, NULL, NULL, '1'), ('4', '1', 'InDescription', 'Description with 123xD', NULL, NULL, NULL, '1'), ('5', '1', 'InWidth', '', 12345, NULL, NULL, '1'), ('6', '1', 'InHeight', 'desc', NULL, 1231, NULL, '1'), ('7', '1', 'InDepth', '', NULL, NULL, 123.4, '1')"))
				.url(REST_URL + "/slots?store_id=1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/slots/?store_id=1{&free_text,name,description}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":7,\"storeId\":1,"
										+ "\"name\":\"InDepth\",\"description\":\"\",\"width\":null,\"height\":null,\"depth\":123.4,\"outside\":true},{\"id\":4,\"storeId\":1,"
										+ "\"name\":\"InDescription\",\"description\":\"Description with 123xD\",\"width\":null,\"height\":null,\"depth\":null,\"outside\":true},{\"id\":6,\"storeId\":1,"
										+ "\"name\":\"InHeight\",\"description\":\"desc\",\"width\":null,\"height\":1231.0,\"depth\":null,\"outside\":true},{\"id\":3,\"storeId\":1,"
										+ "\"name\":\"InName:123\",\"description\":\"\",\"width\":null,\"height\":null,\"depth\":null,\"outside\":true},{\"id\":5,\"storeId\":1,"
										+ "\"name\":\"InWidth\",\"description\":\"\",\"width\":12345.0,\"height\":null,\"depth\":null,\"outside\":true}]}")
						.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/slots/?store_id=1{&free_text,name,description}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[]}")
								.build()))
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
								+ "'),('2', '3', '1', '" + LocalDate.now() + "', '" + LocalDate.now() + "');",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '3', 'InDescription2', 'Description without 123 search phrase', 12.3, 2, 3, '0')"))
				.url(REST_URL + "/slots/1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL,
								STORE_KEEPER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"storeId\":3,\"name\":\"InDescription2\",\"description\":\"Description without 123 search phrase\",\"width\":12.3,\"height\":2.0,\"depth\":3.0,\"outside\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/slots/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
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
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '3', 'InDescription2', 'Description without 123 search phrase', 12.3, 2, 3, '0')"))
				.url(REST_URL + "/slots/1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"storeId\":3,\"name\":\"InDescription2\",\"description\":\"Description without 123 search phrase\",\"width\":12.3,\"height\":2.0,\"depth\":3.0,\"outside\":false,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/slots/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
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
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '3', 'InDescription2', 'Description without 123 search phrase', 12.3, 2, 3, '0')"))
				.url(REST_URL + "/slots/2").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL,
								STORE_KEEPER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_INVALID_ID\",\"message\":\"The provided id is invalid.\",\"httpCode\":400}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).build()));
	}
}
