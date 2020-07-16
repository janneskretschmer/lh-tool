package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import de.lh.tool.domain.dto.ItemDto;
import de.lh.tool.rest.bean.EndpointTest;
import de.lh.tool.rest.bean.UserTest;
import io.restassured.http.Method;

public class ItemIT extends BasicRestIntegrationTest {
//  ██████╗__██████╗_███████╗████████╗
//  ██╔══██╗██╔═══██╗██╔════╝╚══██╔══╝
//  ██████╔╝██║___██║███████╗___██║___
//  ██╔═══╝_██║___██║╚════██║___██║___
//  ██║_____╚██████╔╝███████║___██║___
//  ╚═╝______╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testItemCreationOwn() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot', NULL, NULL, NULL, NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items").method(Method.POST)
				.body(ItemDto.builder().broken(false).consumable(true).depth(12.34f).description("description")
						.hasBarcode(false).height(23.45f).identifier("identifier").name("name").outsideQualified(true)
						.pictureUrl("pictureUrl").quantity(100d).slotId(1l).technicalCrewId(1l).unit("unit")
						.width(34.56f).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"identifier\",\"hasBarcode\":false,\"name\":\"name\",\"description\":\"description\",\"quantity\":100.0,\"unit\":\"unit\",\"width\":34.56,\"height\":23.45,\"depth\":12.34,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"pictureUrl\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE slot_id=1 AND identifier='identifier' AND has_barcode=0 AND name='name' AND description='description' AND quantity=100 AND unit='unit' AND width LIKE 34.56 AND height LIKE 23.45 AND depth LIKE 12.34 AND outside_qualified=1 AND consumable=1 AND broken=0 AND picture_url='pictureUrl' AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='CREATED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL)))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"identifier\",\"hasBarcode\":false,\"name\":\"name\",\"description\":\"description\",\"quantity\":100.0,\"unit\":\"unit\",\"width\":34.56,\"height\":23.45,\"depth\":12.34,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"pictureUrl\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE slot_id=1 AND identifier='identifier' AND has_barcode=0 AND name='name' AND description='description' AND quantity=100 AND unit='unit' AND width LIKE 34.56 AND height LIKE 23.45 AND depth LIKE 12.34 AND outside_qualified=1 AND consumable=1 AND broken=0 AND picture_url='pictureUrl' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='CREATED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL)))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"identifier\",\"hasBarcode\":false,\"name\":\"name\",\"description\":\"description\",\"quantity\":100.0,\"unit\":\"unit\",\"width\":34.56,\"height\":23.45,\"depth\":12.34,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"pictureUrl\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE slot_id=1 AND identifier='identifier' AND has_barcode=0 AND name='name' AND description='description' AND quantity=100 AND unit='unit' AND width LIKE 34.56 AND height LIKE 23.45 AND depth LIKE 12.34 AND outside_qualified=1 AND consumable=1 AND broken=0 AND picture_url='pictureUrl' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='CREATED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL)))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=0",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

//  ██████╗_██╗___██╗████████╗
//  ██╔══██╗██║___██║╚══██╔══╝
//  ██████╔╝██║___██║___██║___
//  ██╔═══╝_██║___██║___██║___
//  ██║_____╚██████╔╝___██║___
//  ╚═╝______╚═════╝____╚═╝___

//  ██████╗__█████╗_████████╗_██████╗██╗__██╗
//  ██╔══██╗██╔══██╗╚══██╔══╝██╔════╝██║__██║
//  ██████╔╝███████║___██║___██║_____███████║
//  ██╔═══╝_██╔══██║___██║___██║_____██╔══██║
//  ██║_____██║__██║___██║___╚██████╗██║__██║
//  ╚═╝_____╚═╝__╚═╝___╚═╝____╚═════╝╚═╝__╚═╝

	@Test
	public void testItemBrokenOwn() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot', NULL, NULL, NULL, NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '12', '24', '48', '1', '1', '0', 'Url1', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1").method(Method.PATCH).body(ItemDto.builder().broken(true).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
												+ getUserIdByEmail(STORE_KEEPER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=0",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemBrokenExpired() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot', NULL, NULL, NULL, NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '12', '24', '48', '1', '1', '0', 'Url1', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2020-06-02');"))
				.url(REST_URL + "/items/1").method(Method.PATCH).body(ItemDto.builder().broken(true).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=0",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemBrokenForeign() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot', NULL, NULL, NULL, NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '12', '24', '48', '1', '1', '0', 'Url1', '1')"))
				.url(REST_URL + "/items/1").method(Method.PATCH).body(ItemDto.builder().broken(true).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=0",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemFixedOwn() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot', NULL, NULL, NULL, NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '12', '24', '48', '1', '1', '1', 'Url1', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1").method(Method.PATCH).body(ItemDto.builder().broken(false).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(STORE_KEEPER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemFixedForeign() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot', NULL, NULL, NULL, NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '12', '24', '48', '1', '1', '1', 'Url1', '1')"))
				.url(REST_URL + "/items/1").method(Method.PATCH).body(ItemDto.builder().broken(false).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemSameBrokenState() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot', NULL, NULL, NULL, NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '12', '24', '48', '1', '1', '1', 'Url1', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1").method(Method.PATCH).body(ItemDto.builder().broken(true).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
								.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemBrokenAndSlot() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES (1, '1', 'Slot', NULL, NULL, NULL, NULL, '0')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES (2, 1, 'Slot2', NULL, NULL, NULL, NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '12', '24', '48', '1', '1', '1', 'Url1', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1").method(Method.PATCH)
				.body(ItemDto.builder().broken(false).slotId(2l).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":2,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=2"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":2,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=2"))
								.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(STORE_KEEPER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":2,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND width=12.0 AND height=24.0 AND depth=48.0 AND outside_qualified=1 AND consumable=1 AND picture_url='Url1' AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=2"))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

//  ██████╗_███████╗██╗_____███████╗████████╗███████╗
//  ██╔══██╗██╔════╝██║_____██╔════╝╚══██╔══╝██╔════╝
//  ██║__██║█████╗__██║_____█████╗_____██║___█████╗__
//  ██║__██║██╔══╝__██║_____██╔══╝_____██║___██╔══╝__
//  ██████╔╝███████╗███████╗███████╗___██║___███████╗
//  ╚═════╝_╚══════╝╚══════╝╚══════╝___╚═╝___╚══════╝

//  _██████╗_███████╗████████╗
//  ██╔════╝_██╔════╝╚══██╔══╝
//  ██║__███╗█████╗_____██║___
//  ██║___██║██╔══╝_____██║___
//  ╚██████╔╝███████╗___██║___
//  _╚═════╝_╚══════╝___╚═╝___

	@Test
	public void testItemsGetOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot', NULL, NULL, NULL, NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '12', '24', '48', '1', '1', '0', 'Url1', '1')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '3', '2', '1', '0', '1', '0', 'Picture Url', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, STORE_KEEPER_EMAIL,
								INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1},{\"id\":2,\"slotId\":1,\"identifier\":\"Identifier2\",\"hasBarcode\":true,\"name\":\"Item2\",\"description\":\"Description2\",\"quantity\":100.0,\"unit\":\"Stück\",\"width\":3.0,\"height\":2.0,\"depth\":1.0,\"outsideQualified\":false,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Picture Url\",\"technicalCrewId\":1}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot', NULL, NULL, NULL, NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '12', '24', '48', '1', '1', '0', 'Url1', '1')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '3', '2', '1', '0', '1', '0', 'Picture Url', '1')"))
				.url(REST_URL + "/items").method(Method.GET)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1},{\"id\":2,\"slotId\":1,\"identifier\":\"Identifier2\",\"hasBarcode\":true,\"name\":\"Item2\",\"description\":\"Description2\",\"quantity\":100.0,\"unit\":\"Stück\",\"width\":3.0,\"height\":2.0,\"depth\":1.0,\"outsideQualified\":false,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Picture Url\",\"technicalCrewId\":1}]}")
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
								.expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1},{\"id\":2,\"slotId\":1,\"identifier\":\"Identifier2\",\"hasBarcode\":true,\"name\":\"Item2\",\"description\":\"Description2\",\"quantity\":100.0,\"unit\":\"Stück\",\"width\":3.0,\"height\":2.0,\"depth\":1.0,\"outsideQualified\":false,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Picture Url\",\"technicalCrewId\":1}]}")
								.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetByIdOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot', NULL, NULL, NULL, NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '12', '24', '48', '1', '1', '0', 'Url1', '1')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '3', '2', '1', '0', '1', '0', 'Picture Url', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, STORE_KEEPER_EMAIL,
								INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetByIdForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot', NULL, NULL, NULL, NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '12', '24', '48', '1', '1', '0', 'Url1', '1')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '3', '2', '1', '0', '1', '0', 'Picture Url', '1')"))
				.url(REST_URL + "/items/1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"width\":12.0,\"height\":24.0,\"depth\":48.0,\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"pictureUrl\":\"Url1\",\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetNotesOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot', NULL, NULL, NULL, NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '12', '24', '48', '1', '1', '0', 'Url1', '1')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', '1', 'Notize 1 \\\"Test\\\" <*/>§$1 \\\\\\\\\\\\\\\\\\\"', '2020-07-17 09:51:33'), ('2', '1', '2', 'Blah Blah Blah', '2020-07-14 09:51:33')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '3', '2', '1', '0', '1', '0', 'Picture Url', '1')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('3', '2', '3', 'Notiz 3 ', '2020-07-05 09:51:33'), ('4', '2', '4', 'Servus :)', '2020-07-31 09:51:33')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/notes").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, STORE_KEEPER_EMAIL,
								INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/notes\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"itemId\":1,\"userId\":1,\"note\":\"Notize 1 \\\"Test\\\" <*/>§$1 \\\\\\\\\\\\\\\\\\\"\",\"timestamp\":\"2020-07-17T09:51:33\"},{\"id\":2,\"itemId\":1,\"userId\":2,\"note\":\"Blah Blah Blah\",\"timestamp\":\"2020-07-14T09:51:33\"}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetNotesForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES ('1', '1', 'Slot', NULL, NULL, NULL, NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '12', '24', '48', '1', '1', '0', 'Url1', '1')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', '1', 'Notize 1 \\\"Test\\\" <*/>§$1 \\\\\\\\\\\\\\\\\\\"', '2020-07-17 09:51:33'), ('2', '1', '2', 'Blah Blah Blah', '2020-07-14 09:51:33')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `picture_url`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '3', '2', '1', '0', '1', '0', 'Picture Url', '1')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('3', '2', '3', 'Notiz 3 ', '2020-07-05 09:51:33'), ('4', '2', '4', 'Servus :)', '2020-07-31 09:51:33')"))
				.url(REST_URL + "/items/1/notes").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/notes\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"itemId\":1,\"userId\":1,\"note\":\"Notize 1 \\\"Test\\\" <*/>§$1 \\\\\\\\\\\\\\\\\\\"\",\"timestamp\":\"2020-07-17T09:51:33\"},{\"id\":2,\"itemId\":1,\"userId\":2,\"note\":\"Blah Blah Blah\",\"timestamp\":\"2020-07-14T09:51:33\"}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}
}
