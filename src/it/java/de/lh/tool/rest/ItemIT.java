package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import de.lh.tool.domain.dto.ItemDto;
import de.lh.tool.domain.dto.ItemImageDto;
import de.lh.tool.domain.dto.ItemItemDto;
import de.lh.tool.domain.dto.ItemNoteDto;
import de.lh.tool.domain.dto.ItemTagDto;
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
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items").method(Method.POST)
				.body(ItemDto.builder().broken(false).consumable(true).description("description").hasBarcode(false)
						.identifier("identifier").name("name").outsideQualified(true).quantity(100d).slotId(1l)
						.technicalCrewId(1l).unit("unit").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"identifier\",\"hasBarcode\":false,\"name\":\"name\",\"description\":\"description\",\"quantity\":100.0,\"unit\":\"unit\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE slot_id=1 AND identifier='identifier' AND has_barcode=0 AND name='name' AND description='description' AND quantity=100 AND unit='unit' AND outside_qualified=1 AND consumable=1 AND broken=0 AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='CREATED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL)))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"identifier\",\"hasBarcode\":false,\"name\":\"name\",\"description\":\"description\",\"quantity\":100.0,\"unit\":\"unit\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE slot_id=1 AND identifier='identifier' AND has_barcode=0 AND name='name' AND description='description' AND quantity=100 AND unit='unit' AND outside_qualified=1 AND consumable=1 AND broken=0 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='CREATED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL)))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"identifier\",\"hasBarcode\":false,\"name\":\"name\",\"description\":\"description\",\"quantity\":100.0,\"unit\":\"unit\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE slot_id=1 AND identifier='identifier' AND has_barcode=0 AND name='name' AND description='description' AND quantity=100 AND unit='unit' AND outside_qualified=1 AND consumable=1 AND broken=0 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='CREATED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL)))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=0",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemCreationForeign() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')"))
				.url(REST_URL + "/items").method(Method.POST)
				.body(ItemDto.builder().broken(false).consumable(true).description("description").hasBarcode(false)
						.identifier("identifier").name("name").outsideQualified(true).quantity(100d).slotId(1l)
						.technicalCrewId(1l).unit("unit").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"identifier\",\"hasBarcode\":false,\"name\":\"name\",\"description\":\"description\",\"quantity\":100.0,\"unit\":\"unit\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE slot_id=1 AND identifier='identifier' AND has_barcode=0 AND name='name' AND description='description' AND quantity=100 AND unit='unit' AND outside_qualified=1 AND consumable=1 AND broken=0 AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='CREATED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL)))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"identifier\",\"hasBarcode\":false,\"name\":\"name\",\"description\":\"description\",\"quantity\":100.0,\"unit\":\"unit\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE slot_id=1 AND identifier='identifier' AND has_barcode=0 AND name='name' AND description='description' AND quantity=100 AND unit='unit' AND outside_qualified=1 AND consumable=1 AND broken=0 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='CREATED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL)))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"identifier\",\"hasBarcode\":false,\"name\":\"name\",\"description\":\"description\",\"quantity\":100.0,\"unit\":\"unit\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE slot_id=1 AND identifier='identifier' AND has_barcode=0 AND name='name' AND description='description' AND quantity=100 AND unit='unit' AND outside_qualified=1 AND consumable=1 AND broken=0 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='CREATED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL)))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=0",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemCreationMissingName() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items").method(Method.POST)
				.body(ItemDto.builder().broken(false).consumable(true).description("description").hasBarcode(false)
						.identifier("identifier").name(null).outsideQualified(true).quantity(100d).slotId(1l)
						.technicalCrewId(1l).unit("unit").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_ITEM_NO_NAME\",\"message\":\"The item has no name.\",\"httpCode\":400}")
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=0",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemCreationMissingIdentifier() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items").method(Method.POST)
				.body(ItemDto.builder().broken(false).consumable(true).description("description").hasBarcode(false)
						.identifier(null).name("name").outsideQualified(true).quantity(100d).slotId(1l)
						.technicalCrewId(1l).unit("unit").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_ITEM_NO_IDENTIFIER\",\"message\":\"The item has no identifier.\",\"httpCode\":400}")
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=0",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemCreationMissingSlot() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items").method(Method.POST)
				.body(ItemDto.builder().broken(false).consumable(true).description("description").hasBarcode(false)
						.identifier("identifier").name("name").outsideQualified(true).quantity(100d).slotId(2l)
						.technicalCrewId(1l).unit("unit").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_ITEM_NO_SLOT\",\"message\":\"The item has no slot.\",\"httpCode\":400}")
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=0",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemCreationMissingTechnicalCrew() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items").method(Method.POST)
				.body(ItemDto.builder().broken(false).consumable(true).description("description").hasBarcode(false)
						.identifier("identifier").name("name").outsideQualified(true).quantity(100d).slotId(1l)
						.technicalCrewId(2l).unit("unit").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_ITEM_NO_TECHNICAL_CREW\",\"message\":\"The item has no technical crew.\",\"httpCode\":400}")
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=0",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemCreationDuplicateIdentifier() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'identifier', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items").method(Method.POST)
				.body(ItemDto.builder().broken(false).consumable(true).description("description").hasBarcode(false)
						.identifier("identifier").name("name").outsideQualified(true).quantity(100d).slotId(1l)
						.technicalCrewId(1l).unit("unit").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.CONFLICT)
						.expectedResponse(
								"{\"key\":\"EX_ITEM_IDENTIFIER_ALREADY_IN_USE\",\"message\":\"The identifier is already in use.\",\"httpCode\":409}")
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=1",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemTagCreation() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'identifier', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/tags").method(Method.POST).body(ItemTagDto.builder().name("Tag1").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))

						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Tag1\",\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/tags\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of("SELECT * FROM item_tag WHERE name='Tag1'",
								"SELECT * FROM item_item_tag WHERE item_id=1 AND item_tag_id=1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_tag)=0",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag)=0"))
				.build()));
	}

	@Test
	public void testItemTagCreationExistingTag() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'identifier', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tag1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/tags").method(Method.POST).body(ItemTagDto.builder().name("Tag1").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))

						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"name\":\"Tag1\",\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/tags\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of("SELECT * FROM item_tag WHERE name='Tag1'",
								"SELECT * FROM item_item_tag WHERE item_id=1 AND item_tag_id=1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_tag)=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag)=0"))
				.build()));
	}

	@Test
	public void testItemTagCreationAlreadyExists() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'identifier', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tag1')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '1', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/tags").method(Method.POST).body(ItemTagDto.builder().name("Tag1").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))

						.expectedHttpCode(HttpStatus.CONFLICT)
						.expectedResponse(
								"{\"key\":\"EX_ITEM_ITEM_TAG_ALREADY_EXISTS\",\"message\":\"The item already has the provided tag.\",\"httpCode\":409}")
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=1",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_tag)=1",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag)=1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_tag)=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag)=1"))
				.build()));
	}

	@Test
	public void testItemNoteCreation() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'identifier', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/notes").method(Method.POST)
				.body(ItemNoteDto.builder().itemId(1l).note("Das ist eine Notiz").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponseIsRegex(true)
						.expectedResponse("\\{\"id\":1,\"itemId\":1,\"userId\":" + getUserIdByEmail(ADMIN_EMAIL)
								+ ",\"note\":\"Das ist eine Notiz\",\"timestamp\":\"\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d.\\d*\",\"links\":\\[\\{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/notes\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null\\}\\]\\}")
						.validationQueries(List
								.of("SELECT * FROM item_note WHERE item_id=1 AND note='Das ist eine Notiz' AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL)))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponseIsRegex(true)
								.expectedResponse("\\{\"id\":1,\"itemId\":1,\"userId\":"
										+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL)
										+ ",\"note\":\"Das ist eine Notiz\",\"timestamp\":\"\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d.\\d*\",\"links\":\\[\\{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/notes\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null\\}\\]\\}")
								.validationQueries(List.of(
										"SELECT * FROM item_note WHERE item_id=1 AND note='Das ist eine Notiz' AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL)))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponseIsRegex(true)
								.expectedResponse("\\{\"id\":1,\"itemId\":1,\"userId\":"
										+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL)
										+ ",\"note\":\"Das ist eine Notiz\",\"timestamp\":\"\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d.\\d*\",\"links\":\\[\\{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/notes\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null\\}\\]\\}")
								.validationQueries(List.of(
										"SELECT * FROM item_note WHERE item_id=1 AND note='Das ist eine Notiz' AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL)))
								.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponseIsRegex(true)
								.expectedResponse("\\{\"id\":1,\"itemId\":1,\"userId\":"
										+ getUserIdByEmail(STORE_KEEPER_EMAIL)
										+ ",\"note\":\"Das ist eine Notiz\",\"timestamp\":\"\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d.\\d*\",\"links\":\\[\\{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/notes\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null\\}\\]\\}")
								.validationQueries(List.of(
										"SELECT * FROM item_note WHERE item_id=1 AND note='Das ist eine Notiz' AND user_id="
												+ getUserIdByEmail(STORE_KEEPER_EMAIL)))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note)=0"))
				.build()));
	}

	@Test
	public void testItemImageCreation() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'identifier', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/image").method(Method.POST)
				.body(ItemImageDto.builder().itemId(1l).image(Base64.decodeBase64(
						"iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAABhWlDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AYht+mSotUHOwg4pChOlnwD9FNq1CECqFWaNXB5NIfoUlDkuLiKLgWHPxZrDq4OOvq4CoIgj8gTo5Oii5S4ndJoUWMdxz38N73vtx9Bwj1MtOsjhFA020znUyI2dyKGHpFmGYIo5iWmWXMSlIKvuPrHgG+38V5ln/dn6NbzVsMCIjEM8wwbeJ14slN2+C8TxxlJVklPiceNumCxI9cVzx+41x0WeCZUTOTniOOEovFNlbamJVMjXiCOKZqOuULWY9VzluctXKVNe/JXxjJ68tLXKc1gCQWsAgJIhRUsYEybMRp10mxkKbzhI+/3/VL5FLItQFGjnlUoEF2/eB/8Lu3VmF8zEuKJIDOF8f5GARCu0Cj5jjfx47TOAGCz8CV3vJX6sDUJ+m1lhY7Anq2gYvrlqbsAZc7QN+TIZuyKwVpCYUC8H5G35QDem+BrlWvb81znD4AGepV6gY4OASGipS95vPucHvf/q1p9u8HrApyvnqnxPEAAAAJcEhZcwAALiMAAC4jAXilP3YAAAAHdElNRQfkCAgHMxPBX6JxAAAAGXRFWHRDb21tZW50AENyZWF0ZWQgd2l0aCBHSU1QV4EOFwAAAAxJREFUCNdjYGBgAAAABAABJzQnCgAAAABJRU5ErkJggg=="))
						.mediaType("image/png").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"itemId\":1,\"image\":\"iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAABhWlDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AYht+mSotUHOwg4pChOlnwD9FNq1CECqFWaNXB5NIfoUlDkuLiKLgWHPxZrDq4OOvq4CoIgj8gTo5Oii5S4ndJoUWMdxz38N73vtx9Bwj1MtOsjhFA020znUyI2dyKGHpFmGYIo5iWmWXMSlIKvuPrHgG+38V5ln/dn6NbzVsMCIjEM8wwbeJ14slN2+C8TxxlJVklPiceNumCxI9cVzx+41x0WeCZUTOTniOOEovFNlbamJVMjXiCOKZqOuULWY9VzluctXKVNe/JXxjJ68tLXKc1gCQWsAgJIhRUsYEybMRp10mxkKbzhI+/3/VL5FLItQFGjnlUoEF2/eB/8Lu3VmF8zEuKJIDOF8f5GARCu0Cj5jjfx47TOAGCz8CV3vJX6sDUJ+m1lhY7Anq2gYvrlqbsAZc7QN+TIZuyKwVpCYUC8H5G35QDem+BrlWvb81znD4AGepV6gY4OASGipS95vPucHvf/q1p9u8HrApyvnqnxPEAAAAJcEhZcwAALiMAAC4jAXilP3YAAAAHdElNRQfkCAgHMxPBX6JxAAAAGXRFWHRDb21tZW50AENyZWF0ZWQgd2l0aCBHSU1QV4EOFwAAAAxJREFUCNdjYGBgAAAABAABJzQnCgAAAABJRU5ErkJggg==\","
										+ "\"mediaType\":\"image/png\",\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/image\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(
								// FUTURE: check blob
								List.of("SELECT * FROM item_image WHERE item_id=1 AND media_type='image/png'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_image)=0"))
				.build()));
	}

	@Test
	public void testItemItemCreation() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '0', '1', '0', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/items").method(Method.POST)
				.body(ItemItemDto.builder().item1Id(1l).item2Id(2l).build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"item1Id\":1,\"item2Id\":2,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/items\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of("SELECT * FROM item_item WHERE item1_id=1 AND item2_id")).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=2",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item)=0"))
				.build()));
	}

//  ██████╗_██╗___██╗████████╗
//  ██╔══██╗██║___██║╚══██╔══╝
//  ██████╔╝██║___██║___██║___
//  ██╔═══╝_██║___██║___██║___
//  ██║_____╚██████╔╝___██║___
//  ╚═╝______╚═════╝____╚═╝___

	@Test
	public void testItemModificationOwn() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (1, '1', 'Slot', NULL, '0')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (2, 1, 'Slot2', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew'),(2, 'TK2')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tag1'), ('2', 'Tag2')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '1', '1'), ('2', '1', '2')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', NULL, 'Test Notiz', '2020-07-22 11:37:34'), (2, '1', '2', 'Noch eine Notiz', '2020-07-23 14:37:34')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1").method(Method.PUT)
				.body(ItemDto.builder().broken(false).consumable(false).description("description").hasBarcode(true)
						.identifier("identifier").name("name").outsideQualified(false).quantity(123d).slotId(2l)
						.technicalCrewId(2l).unit("unit").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":2,\"identifier\":\"identifier\",\"hasBarcode\":true,\"name\":\"name\",\"description\":\"description\",\"quantity\":123.0,\"unit\":\"unit\",\"outsideQualified\":false,\"consumable\":false,\"broken\":false,\"technicalCrewId\":2,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='identifier' AND has_barcode=1 AND name='name' AND description='description' AND quantity=123.0 AND unit='unit' AND outside_qualified=0 AND consumable=0 AND technical_crew_id=2",
								"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT * FROM item_history WHERE item_id=1 AND type='QUANTITY_CHANGED' AND data='{\"from\":\"1\",\"to\":\"123\"}' AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT * FROM item_history WHERE item_id=1 AND type='UPDATED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=4",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":2,\"identifier\":\"identifier\",\"hasBarcode\":true,\"name\":\"name\",\"description\":\"description\",\"quantity\":123.0,\"unit\":\"unit\",\"outsideQualified\":false,\"consumable\":false,\"broken\":false,\"technicalCrewId\":2,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='identifier' AND has_barcode=1 AND name='name' AND description='description' AND quantity=123.0 AND unit='unit' AND outside_qualified=0 AND consumable=0 AND technical_crew_id=2",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='QUANTITY_CHANGED' AND data='{\"from\":\"1\",\"to\":\"123\"}' AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='UPDATED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=4",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":2,\"identifier\":\"identifier\",\"hasBarcode\":true,\"name\":\"name\",\"description\":\"description\",\"quantity\":123.0,\"unit\":\"unit\",\"outsideQualified\":false,\"consumable\":false,\"broken\":false,\"technicalCrewId\":2,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='identifier' AND has_barcode=1 AND name='name' AND description='description' AND quantity=123.0 AND unit='unit' AND outside_qualified=0 AND consumable=0 AND technical_crew_id=2",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='QUANTITY_CHANGED' AND data='{\"from\":\"1\",\"to\":\"123\"}' AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='UPDATED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=4",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemModificationBrokenAndSlot() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (1, '1', 'Slot', NULL, '0')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (2, 1, 'Slot2', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew'),(2, 'TK2')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück','1', '1', '1', '1')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tag1'), ('2', 'Tag2')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '1', '1'), ('2', '1', '2')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', NULL, 'Test Notiz', '2020-07-22 11:37:34'), (2, '1', '2', 'Noch eine Notiz', '2020-07-23 14:37:34')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1").method(Method.PUT)
				.body(ItemDto.builder().broken(false).consumable(true).description("Description 1").hasBarcode(false)
						.identifier("Identifier1").name("Item1").outsideQualified(true).quantity(1d).slotId(2l)
						.technicalCrewId(1l).unit("Stück").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":2,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=2",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":2,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=2",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":2,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=2",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemModificationForeign() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (1, '1', 'Slot', NULL, '0')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (2, 1, 'Slot2', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew'),(2, 'TK2')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tag1'), ('2', 'Tag2')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '1', '1'), ('2', '1', '2')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', NULL, 'Test Notiz', '2020-07-22 11:37:34'), (2, '1', '2', 'Noch eine Notiz', '2020-07-23 14:37:34')"))
				.url(REST_URL + "/items/1").method(Method.PUT)
				.body(ItemDto.builder().broken(false).consumable(false).description("description").hasBarcode(true)
						.identifier("identifier").name("name").outsideQualified(false).quantity(123d).slotId(2l)
						.technicalCrewId(2l).unit("unit").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":2,\"identifier\":\"identifier\",\"hasBarcode\":true,\"name\":\"name\",\"description\":\"description\",\"quantity\":123.0,\"unit\":\"unit\",\"outsideQualified\":false,\"consumable\":false,\"broken\":false,\"technicalCrewId\":2,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='identifier' AND has_barcode=1 AND name='name' AND description='description' AND quantity=123.0 AND unit='unit' AND outside_qualified=0 AND consumable=0 AND technical_crew_id=2",
								"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT * FROM item_history WHERE item_id=1 AND type='QUANTITY_CHANGED' AND data='{\"from\":\"1\",\"to\":\"123\"}' AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT * FROM item_history WHERE item_id=1 AND type='UPDATED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=4",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":2,\"identifier\":\"identifier\",\"hasBarcode\":true,\"name\":\"name\",\"description\":\"description\",\"quantity\":123.0,\"unit\":\"unit\",\"outsideQualified\":false,\"consumable\":false,\"broken\":false,\"technicalCrewId\":2,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='identifier' AND has_barcode=1 AND name='name' AND description='description' AND quantity=123.0 AND unit='unit' AND outside_qualified=0 AND consumable=0 AND technical_crew_id=2",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='QUANTITY_CHANGED' AND data='{\"from\":\"1\",\"to\":\"123\"}' AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='UPDATED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=4",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":2,\"identifier\":\"identifier\",\"hasBarcode\":true,\"name\":\"name\",\"description\":\"description\",\"quantity\":123.0,\"unit\":\"unit\",\"outsideQualified\":false,\"consumable\":false,\"broken\":false,\"technicalCrewId\":2,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='identifier' AND has_barcode=1 AND name='name' AND description='description' AND quantity=123.0 AND unit='unit' AND outside_qualified=0 AND consumable=0 AND technical_crew_id=2",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='QUANTITY_CHANGED' AND data='{\"from\":\"1\",\"to\":\"123\"}' AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='UPDATED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=4",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemModificationMissingName() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (1, '1', 'Slot', NULL, '0')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (2, 1, 'Slot2', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew'),(2, 'TK2')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tag1'), ('2', 'Tag2')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '1', '1'), ('2', '1', '2')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', NULL, 'Test Notiz', '2020-07-22 11:37:34'), (2, '1', '2', 'Noch eine Notiz', '2020-07-23 14:37:34')"))
				.url(REST_URL + "/items/1").method(Method.PUT)
				.body(ItemDto.builder().broken(false).consumable(false).description("description").hasBarcode(true)
						.identifier("identifier").name(null).outsideQualified(false).quantity(123d).slotId(2l)
						.technicalCrewId(2l).unit("unit").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_ITEM_NO_NAME\",\"message\":\"The item has no name.\",\"httpCode\":400}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemModificationMissingIdentifier() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (1, '1', 'Slot', NULL, '0')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (2, 1, 'Slot2', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew'),(2, 'TK2')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tag1'), ('2', 'Tag2')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '1', '1'), ('2', '1', '2')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', NULL, 'Test Notiz', '2020-07-22 11:37:34'), (2, '1', '2', 'Noch eine Notiz', '2020-07-23 14:37:34')"))
				.url(REST_URL + "/items/1").method(Method.PUT)
				.body(ItemDto.builder().broken(false).consumable(false).description("description").hasBarcode(true)
						.identifier(null).name("name").outsideQualified(false).quantity(123d).slotId(2l)
						.technicalCrewId(2l).unit("unit").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_ITEM_NO_IDENTIFIER\",\"message\":\"The item has no identifier.\",\"httpCode\":400}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	// TODO test identifier already in use

	@Test
	public void testItemModificationMissingSlot() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (1, '1', 'Slot', NULL, '0')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (2, 1, 'Slot2', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew'),(2, 'TK2')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tag1'), ('2', 'Tag2')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '1', '1'), ('2', '1', '2')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', NULL, 'Test Notiz', '2020-07-22 11:37:34'), (2, '1', '2', 'Noch eine Notiz', '2020-07-23 14:37:34')"))
				.url(REST_URL + "/items/1").method(Method.PUT)
				.body(ItemDto.builder().broken(false).consumable(false).description("description").hasBarcode(true)
						.identifier("identifier").name("name").outsideQualified(false).quantity(123d).slotId(null)
						.technicalCrewId(2l).unit("unit").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_ITEM_NO_SLOT\",\"message\":\"The item has no slot.\",\"httpCode\":400}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemModificationMissingTechnicalCrew() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (1, '1', 'Slot', NULL, '0')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (2, 1, 'Slot2', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew'),(2, 'TK2')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tag1'), ('2', 'Tag2')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '1', '1'), ('2', '1', '2')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', NULL, 'Test Notiz', '2020-07-22 11:37:34'), (2, '1', '2', 'Noch eine Notiz', '2020-07-23 14:37:34')"))
				.url(REST_URL + "/items/1").method(Method.PUT)
				.body(ItemDto.builder().broken(false).consumable(false).description("description").hasBarcode(true)
						.identifier("identifier").name("name").outsideQualified(false).quantity(123d).slotId(2l)
						.technicalCrewId(null).unit("unit").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.BAD_REQUEST)
						.expectedResponse(
								"{\"key\":\"EX_ITEM_NO_TECHNICAL_CREW\",\"message\":\"The item has no technical crew.\",\"httpCode\":400}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemImageModification() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL,  '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'identifier', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO `item_image` (`id`, `item_id`, `image`, `media_type`) VALUES (1, '1', 0x89504e470d0a1a0a0000000d4948445200000001000000010802000000907753de00000185694343504943432070726f66696c65000028917d913d48c3401886dfb69616a93ad841c4214375b22055c451ab50840aa15668d5c1e4d23f68d290a4b8380aae05077f16ab0e2eceba3ab80a82e00f8893a393a28b94f85d526811e31dc73dbcf7bd2f77df01fe6695a966cf04a06a96914925855c7e5508bd224cb31f41242466ea73a29886e7f8ba878fef77719ee55df7e7e8530a26037c02f12cd30d8b7883787ad3d239ef134759595288cf89c70dba20f123d76597df38971cf6f3cca891cdcc134789855217cb5dccca864a3c451c53548df2fd399715ce5b9cd56a9db5efc95f1829682bcb5ca735821416b104110264d451411516e2b46ba498c8d079d2c33fecf84572c9e4aa8091630135a8901c3ff81ffceead599c4cb8499124107cb1ed8f5120b40bb41ab6fd7d6cdbad1320f00c5c691d7fad09cc7c92dee868b12360601bb8b8ee68f21e70b9030c3de9922139528096bf5804decfe89bf2c0e02dd0bbe6f6ad7d8ed307204bbd4adf000787c05889b2d73dde1deeeedbbf35edfefd003e5272927f4756bc000000097048597300002e2300002e230178a53f760000000774494d4507e408080e063220d64b160000001974455874436f6d6d656e74004372656174656420776974682047494d5057810e170000000c4944415408d763f8cfc000000301010018dd8db00000000049454e44ae426082, 'image/png');",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/image/1").method(Method.PUT)
				.body(ItemImageDto.builder().id(1l).itemId(1l).image(Base64.decodeBase64(
						"iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAABhWlDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AYht+mSotUHOwg4pChOlnwD9FNq1CECqFWaNXB5NIfoUlDkuLiKLgWHPxZrDq4OOvq4CoIgj8gTo5Oii5S4ndJoUWMdxz38N73vtx9Bwj1MtOsjhFA020znUyI2dyKGHpFmGYIo5iWmWXMSlIKvuPrHgG+38V5ln/dn6NbzVsMCIjEM8wwbeJ14slN2+C8TxxlJVklPiceNumCxI9cVzx+41x0WeCZUTOTniOOEovFNlbamJVMjXiCOKZqOuULWY9VzluctXKVNe/JXxjJ68tLXKc1gCQWsAgJIhRUsYEybMRp10mxkKbzhI+/3/VL5FLItQFGjnlUoEF2/eB/8Lu3VmF8zEuKJIDOF8f5GARCu0Cj5jjfx47TOAGCz8CV3vJX6sDUJ+m1lhY7Anq2gYvrlqbsAZc7QN+TIZuyKwVpCYUC8H5G35QDem+BrlWvb81znD4AGepV6gY4OASGipS95vPucHvf/q1p9u8HrApyvnqnxPEAAAAJcEhZcwAALiMAAC4jAXilP3YAAAAHdElNRQfkCAgHMxPBX6JxAAAAGXRFWHRDb21tZW50AENyZWF0ZWQgd2l0aCBHSU1QV4EOFwAAAAxJREFUCNdjYGBgAAAABAABJzQnCgAAAABJRU5ErkJggg=="))
						.mediaType("image/png").build())
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"itemId\":1,\"image\":\"iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAABhWlDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AYht+mSotUHOwg4pChOlnwD9FNq1CECqFWaNXB5NIfoUlDkuLiKLgWHPxZrDq4OOvq4CoIgj8gTo5Oii5S4ndJoUWMdxz38N73vtx9Bwj1MtOsjhFA020znUyI2dyKGHpFmGYIo5iWmWXMSlIKvuPrHgG+38V5ln/dn6NbzVsMCIjEM8wwbeJ14slN2+C8TxxlJVklPiceNumCxI9cVzx+41x0WeCZUTOTniOOEovFNlbamJVMjXiCOKZqOuULWY9VzluctXKVNe/JXxjJ68tLXKc1gCQWsAgJIhRUsYEybMRp10mxkKbzhI+/3/VL5FLItQFGjnlUoEF2/eB/8Lu3VmF8zEuKJIDOF8f5GARCu0Cj5jjfx47TOAGCz8CV3vJX6sDUJ+m1lhY7Anq2gYvrlqbsAZc7QN+TIZuyKwVpCYUC8H5G35QDem+BrlWvb81znD4AGepV6gY4OASGipS95vPucHvf/q1p9u8HrApyvnqnxPEAAAAJcEhZcwAALiMAAC4jAXilP3YAAAAHdElNRQfkCAgHMxPBX6JxAAAAGXRFWHRDb21tZW50AENyZWF0ZWQgd2l0aCBHSU1QV4EOFwAAAAxJREFUCNdjYGBgAAAABAABJzQnCgAAAABJRU5ErkJggg==\","
										+ "\"mediaType\":\"image/png\",\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/image/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(
								// FUTURE: check blob
								List.of("SELECT * FROM item_image WHERE item_id=1 AND media_type='image/png'"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=1",
						// FUTURE: check blob
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_image)=1"))
				.build()));
	}

//  ██████╗__█████╗_████████╗_██████╗██╗__██╗
//  ██╔══██╗██╔══██╗╚══██╔══╝██╔════╝██║__██║
//  ██████╔╝███████║___██║___██║_____███████║
//  ██╔═══╝_██╔══██║___██║___██║_____██╔══██║
//  ██║_____██║__██║___██║___╚██████╗██║__██║
//  ╚═╝_____╚═╝__╚═╝___╚═╝____╚═════╝╚═╝__╚═╝

	@Test
	public void testItemPatchBrokenOwn() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1").method(Method.PATCH).body(ItemDto.builder().broken(true).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
												+ getUserIdByEmail(STORE_KEEPER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
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
	public void testItemPatchBrokenExpired() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2020-06-02');"))
				.url(REST_URL + "/items/1").method(Method.PATCH).body(ItemDto.builder().broken(true).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
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
	public void testItemPatchBrokenForeign() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')"))
				.url(REST_URL + "/items/1").method(Method.PATCH).body(ItemDto.builder().broken(true).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='BROKEN' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
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
	public void testItemPatchFixedOwn() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1").method(Method.PATCH).body(ItemDto.builder().broken(false).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(STORE_KEEPER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
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
	public void testItemPatchFixedForeign() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')"))
				.url(REST_URL + "/items/1").method(Method.PATCH).body(ItemDto.builder().broken(false).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
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
	public void testItemPatchSameBrokenState() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1").method(Method.PATCH).body(ItemDto.builder().broken(true).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
								.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":true,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=1 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemPatchBrokenAndSlot() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (1, '1', 'Slot', NULL, '0')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (2, 1, 'Slot2', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1").method(Method.PATCH)
				.body(ItemDto.builder().broken(false).slotId(2l).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":2,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=2"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":2,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=2"))
								.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(STORE_KEEPER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":2,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=1.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
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

	@Test
	public void testItemPatchQuantityOwn() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1").method(Method.PATCH).body(ItemDto.builder().quantity(123.0).build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":123.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=123.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
								"SELECT * FROM item_history WHERE item_id=1 AND type='QUANTITY_CHANGED' AND data ='{\"from\":\"1\",\"to\":\"123\"}' AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":123.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=123.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='QUANTITY_CHANGED' AND data='{\"from\":\"1\",\"to\":\"123\"}' AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":123.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=123.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='QUANTITY_CHANGED' AND data='{\"from\":\"1\",\"to\":\"123\"}' AND user_id="
												+ getUserIdByEmail(STORE_KEEPER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":123.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=123.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='QUANTITY_CHANGED' AND data='{\"from\":\"1\",\"to\":\"123\"}' AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=1"))
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT * FROM item WHERE id=1 AND broken=0",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0"))
				.build()));
	}

	@Test
	public void testItemPatchAll() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (1, '1', 'Slot', NULL, '0')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (2, 1, 'Slot2', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew'),(2, 'TK2')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tag1'), ('2', 'Tag2')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '1', '1'), ('2', '1', '2')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', NULL, 'Test Notiz', '2020-07-22 11:37:34'), (2, '1', '2', 'Noch eine Notiz', '2020-07-23 14:37:34')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1").method(Method.PATCH)
				.body(ItemDto.builder().broken(false).consumable(false).description("description").hasBarcode(true)
						.identifier("identifier").name("name").outsideQualified(false).quantity(123d).slotId(2l)
						.technicalCrewId(2l).unit("unit").build())
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":2,\"identifier\":\"identifier\",\"hasBarcode\":true,\"name\":\"name\",\"description\":\"description\",\"quantity\":123.0,\"unit\":\"unit\",\"outsideQualified\":false,\"consumable\":false,\"broken\":false,\"technicalCrewId\":2,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of(
								"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='identifier' AND has_barcode=1 AND name='name' AND description='description' AND quantity=123.0 AND unit='unit' AND outside_qualified=0 AND consumable=0 AND technical_crew_id=2",
								"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT * FROM item_history WHERE item_id=1 AND type='QUANTITY_CHANGED' AND data='{\"from\":\"1\",\"to\":\"123\"}' AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT * FROM item_history WHERE item_id=1 AND type='UPDATED' AND data IS NULL AND user_id="
										+ getUserIdByEmail(ADMIN_EMAIL),
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=4",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":2,\"identifier\":\"identifier\",\"hasBarcode\":true,\"name\":\"name\",\"description\":\"description\",\"quantity\":123.0,\"unit\":\"unit\",\"outsideQualified\":false,\"consumable\":false,\"broken\":false,\"technicalCrewId\":2,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='identifier' AND has_barcode=1 AND name='name' AND description='description' AND quantity=123.0 AND unit='unit' AND outside_qualified=0 AND consumable=0 AND technical_crew_id=2",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='QUANTITY_CHANGED' AND data='{\"from\":\"1\",\"to\":\"123\"}' AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='UPDATED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(CONSTRUCTION_SERVANT_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=4",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
								.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":123.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=1 AND identifier='Identifier1' AND has_barcode=0 AND name='Item1' AND description='Description 1' AND quantity=123.0 AND unit='Stück' AND outside_qualified=1 AND consumable=1 AND technical_crew_id=1",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(STORE_KEEPER_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='QUANTITY_CHANGED' AND data='{\"from\":\"1\",\"to\":\"123\"}' AND user_id="
												+ getUserIdByEmail(STORE_KEEPER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=2",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
								.build(),
						UserTest.builder().emails(List.of(INVENTORY_MANAGER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"id\":1,\"slotId\":2,\"identifier\":\"identifier\",\"hasBarcode\":true,\"name\":\"name\",\"description\":\"description\",\"quantity\":123.0,\"unit\":\"unit\",\"outsideQualified\":false,\"consumable\":false,\"broken\":false,\"technicalCrewId\":2,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
								.validationQueries(List.of(
										"SELECT * FROM item WHERE id=1 AND broken=0 AND slot_id=2 AND identifier='identifier' AND has_barcode=1 AND name='name' AND description='description' AND quantity=123.0 AND unit='unit' AND outside_qualified=0 AND consumable=0 AND technical_crew_id=2",
										"SELECT * FROM item_history WHERE item_id=1 AND type='FIXED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='MOVED' AND data = '{\"from\":\"Store: Slot\",\"to\":\"Store: Slot2\"}' AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='QUANTITY_CHANGED' AND data='{\"from\":\"1\",\"to\":\"123\"}' AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT * FROM item_history WHERE item_id=1 AND type='UPDATED' AND data IS NULL AND user_id="
												+ getUserIdByEmail(INVENTORY_MANAGER_EMAIL),
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=4",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag WHERE item_id=1)=2",
										"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note WHERE item_id=1)=2"))
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

	@Test
	public void testItemDeletionForeign() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (1, '1', 'Slot', NULL, '0')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (2, 1, 'Slot2', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew'),(2, 'TK2')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tag1'), ('2', 'Tag2')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '1', '1'), ('2', '1', '2')",
						"INSERT INTO `item_history` (`id`, `item_id`, `type`, `user_id`, `timestamp`, `data`) VALUES (1, 1, 'CREATED', 1, '2020-07-25 15:34:13', NULL), (2, 1, 'BROKEN', 1, '2020-07-25 15:34:40', NULL), (3, 1, 'FIXED', 1, '2020-07-25 15:34:42', NULL), (4, 1, 'MOVED', 1, '2020-07-25 15:34:50', '{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}'), (5, 1, 'MOVED', 1, '2020-07-25 15:35:03', '{\\\"from\\\":\\\"Store: Slot2\\\",\\\"to\\\":\\\"Store: Slot\\\"}'), (6, 1, 'BROKEN', 1, '2020-07-25 15:35:06', NULL), (7, 1, 'FIXED', 6, '2020-07-25 15:36:04', NULL);",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', NULL, 'Test Notiz', '2020-07-22 11:37:34'), (2, '1', '2', 'Noch eine Notiz', '2020-07-23 14:37:34')"))
				.url(REST_URL + "/items/1").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT)
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_tag)=2",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM technical_crew)=2",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM slot)=2"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=7",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag)=2",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note)=2",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_tag)=2",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM technical_crew)=2",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM slot)=2"))
				.build()));
	}

	@Test
	public void testItemDeletionOwn() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (1, '1', 'Slot', NULL, '0')",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES (2, 1, 'Slot2', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew'),(2, 'TK2')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '1', '1')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tag1'), ('2', 'Tag2')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '1', '1'), ('2', '1', '2')",
						"INSERT INTO `item_history` (`id`, `item_id`, `type`, `user_id`, `timestamp`, `data`) VALUES (1, 1, 'CREATED', 1, '2020-07-25 15:34:13', NULL), (2, 1, 'BROKEN', 1, '2020-07-25 15:34:40', NULL), (3, 1, 'FIXED', 1, '2020-07-25 15:34:42', NULL), (4, 1, 'MOVED', 1, '2020-07-25 15:34:50', '{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}'), (5, 1, 'MOVED', 1, '2020-07-25 15:35:03', '{\\\"from\\\":\\\"Store: Slot2\\\",\\\"to\\\":\\\"Store: Slot\\\"}'), (6, 1, 'BROKEN', 1, '2020-07-25 15:35:06', NULL), (7, 1, 'FIXED', 6, '2020-07-25 15:36:04', NULL);",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', NULL, 'Test Notiz', '2020-07-22 11:37:34'), (2, '1', '2', 'Noch eine Notiz', '2020-07-23 14:37:34')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT)
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_tag)=2",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM technical_crew)=2",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM slot)=2"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_history)=7",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag)=2",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note)=2",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_tag)=2",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM technical_crew)=2",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM slot)=2"))
				.build()));
	}

	@Test
	public void testItemNoteDeletion() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO user_role(user_id,role) VALUES(1000,'ROLE_STORE_KEEPER')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', '1000', 'Notiz 3 ', '2020-07-05 09:51:33')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/notes/1").method(Method.DELETE)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL, "test@lh-tool.de"))
						.expectedHttpCode(HttpStatus.NO_CONTENT)
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=1",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note)=0"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_note)=1"))
				.build()));
	}

	@Test
	public void testItemTagDeletionOneDependency() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tag1')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '1', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/tags/1").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT)
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=1",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_tag)=0",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag)=0"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_tag)=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag)=1"))
				.build()));
	}

	@Test
	public void testItemTagDeletionTwoDependencies() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tag1')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '1', '1'), ('2', '2', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/tags/1").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT)
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=2",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_tag)=1",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag)=1"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=2",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_tag)=1",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item_tag)=2"))
				.build()));
	}

	@Test
	public void testItemItemDeletion() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InNamem1', '1', '1', 'Identifier1', '0', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InDescription', '2', '1', 'Identifier2', '0', 'Description m1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InIdentifier', '3', '1', 'Identifierm1', '0', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('2', 'Technical Crew m1')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InTechnicalCrew', '4', '1', 'Identifier4', '0', 'Description 1', '1', 'Stück', '1', '1', '0', '2')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InNote', '5', '1', 'Identifier5', '0', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InTag', '6', '1', 'Identifier6', '0', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('NotFound', '100', '1', 'Identifier100', '2', 'Description2', '100', 'Stück', '0', '1', '0', '1')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '5', NULL, 'Test Notiz m1', '2020-07-22 11:37:34'), (2, '100', '2', 'Noch eine Notiz', '2020-07-23 14:37:34')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tagm1'), ('2', 'Tag2')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '6', '1'), ('2', '100', '2')",
						"INSERT INTO `item_item` (`id`, `item1_id`, `item2_id`) VALUES ('1', '1', '2'), ('2', '1', '4'),(3,1,6),(4,2,5),(5,2,6);",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/items/2").method(Method.DELETE)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.NO_CONTENT)
						.validationQueries(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=7",
								"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item)=4"))
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN)
				.validationQueriesForOthers(List.of("SELECT 1 WHERE (SELECT COUNT(*) FROM item)=7",
						"SELECT 1 WHERE (SELECT COUNT(*) FROM item_item)=5"))
				.build()));
	}

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
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '0', '1', '0', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, STORE_KEEPER_EMAIL,
								INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/{?free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1},{\"id\":2,\"slotId\":1,\"identifier\":\"Identifier2\",\"hasBarcode\":true,\"name\":\"Item2\",\"description\":\"Description2\",\"quantity\":100.0,\"unit\":\"Stück\",\"outsideQualified\":false,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetFreetext() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InNamem1', '1', '1', 'Identifier1', '0', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InDescription', '2', '1', 'Identifier2', '0', 'Description m1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InIdentifier', '3', '1', 'Identifierm1', '0', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('2', 'Technical Crew m1')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InTechnicalCrew', '4', '1', 'Identifier4', '0', 'Description 1', '1', 'Stück',  '1', '1', '0', '2')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InNote', '5', '1', 'Identifier5', '0', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InTag', '6', '1', 'Identifier6', '0', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('NotFound', '100', '1', 'Identifier100', '2', 'Description2', '100', 'Stück', '0', '1', '0', '1')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '5', NULL, 'Test Notiz m1', '2020-07-22 11:37:34'), (2, '100', '2', 'Noch eine Notiz', '2020-07-23 14:37:34')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tagm1'), ('2', 'Tag2')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '6', '1'), ('2', '100', '2')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items?free_text=m1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, STORE_KEEPER_EMAIL,
								INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/?free_text=m1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":2,\"slotId\":1,\"identifier\":\"Identifier2\",\"hasBarcode\":false,"
										+ "\"name\":\"InDescription\",\"description\":\"Description m1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1},{\"id\":3,\"slotId\":1,\"identifier\":\"Identifierm1\",\"hasBarcode\":false,"
										+ "\"name\":\"InIdentifier\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1},{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,"
										+ "\"name\":\"InNamem1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1},{\"id\":5,\"slotId\":1,\"identifier\":\"Identifier5\",\"hasBarcode\":false,"
										+ "\"name\":\"InNote\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1},{\"id\":6,\"slotId\":1,\"identifier\":\"Identifier6\",\"hasBarcode\":false,"
										+ "\"name\":\"InTag\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1},{\"id\":4,\"slotId\":1,\"identifier\":\"Identifier4\",\"hasBarcode\":false,"
										+ "\"name\":\"InTechnicalCrew\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":2}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '0', '1', '0', '1')"))
				.url(REST_URL + "/items").method(Method.GET)
				.userTests(List.of(UserTest.builder().emails(List.of(ADMIN_EMAIL)).expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/{?free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1},{\"id\":2,\"slotId\":1,\"identifier\":\"Identifier2\",\"hasBarcode\":true,\"name\":\"Item2\",\"description\":\"Description2\",\"quantity\":100.0,\"unit\":\"Stück\",\"outsideQualified\":false,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1}]}")
						.build(),
						UserTest.builder().emails(List.of(CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
								.expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/{?free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1},{\"id\":2,\"slotId\":1,\"identifier\":\"Identifier2\",\"hasBarcode\":true,\"name\":\"Item2\",\"description\":\"Description2\",\"quantity\":100.0,\"unit\":\"Stück\",\"outsideQualified\":false,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1}]}")
								.build(),
						UserTest.builder().emails(List.of(STORE_KEEPER_EMAIL)).expectedHttpCode(HttpStatus.OK)
								.expectedResponse(
										"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/{?free_text}\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[]}")
								.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetByIdOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '0', '1', '0', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, STORE_KEEPER_EMAIL,
								INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetByIdForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '0', '1', '0', '1')"))
				.url(REST_URL + "/items/1").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"slotId\":1,\"identifier\":\"Identifier1\",\"hasBarcode\":false,\"name\":\"Item1\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetNotesOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', '1', 'Notize 1 \\\"Test\\\" <*/>§$1 \\\\\\\\\\\\\\\\\\\"', '2020-07-17 09:51:33'), ('2', '1', '2', 'Blah Blah Blah', '2020-07-14 09:51:33')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '0', '1', '0', '1')",
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
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', '1', 'Notize 1 \\\"Test\\\" <*/>§$1 \\\\\\\\\\\\\\\\\\\"', '2020-07-17 09:51:33'), ('2', '1', '2', 'Blah Blah Blah', '2020-07-14 09:51:33')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '0', '1', '0', '1')",
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

	@Test
	public void testItemsGetNotesUser() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '1', '1000', 'Notize 1 \\\"Test\\\" <*/>§$1 \\\\\\\\\\\\\\\\\\\"', '2020-07-17 09:51:33'), ('2', '1', '2', 'Blah Blah Blah', '2020-07-14 09:51:33')",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/notes/1/user").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, STORE_KEEPER_EMAIL,
								INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":null,\"email\":null,\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":null,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/notes/1/user\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetHistoryOwn() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '0', '1', '0', '1')",
						"INSERT INTO `item_history` (`id`, `item_id`, `type`, `user_id`, `timestamp`, `data`) VALUES (1, 1, 'CREATED', 1, '2020-07-25 15:34:13', NULL), (2, 2, 'BROKEN', 1, '2020-07-25 15:34:40', NULL), (3, 2, 'FIXED', 1, '2020-07-25 15:34:42', NULL), (4, 1, 'MOVED', 1, '2020-07-25 15:34:50', '{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}'), (5, 1, 'MOVED', 1, '2020-07-25 15:35:03', '{\\\"from\\\":\\\"Store: Slot2\\\",\\\"to\\\":\\\"Store: Slot\\\"}'), (6, 1, 'BROKEN', 1, '2020-07-25 15:35:06', NULL), (7, 1, 'FIXED', 6, '2020-07-25 15:36:04', NULL);",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/history").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, STORE_KEEPER_EMAIL,
								INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/history\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":7,\"itemId\":1,\"userId\":6,\"timestamp\":\"2020-07-25T15:36:04\",\"type\":\"FIXED\",\"data\":null},{\"id\":6,\"itemId\":1,\"userId\":1,\"timestamp\":\"2020-07-25T15:35:06\",\"type\":\"BROKEN\",\"data\":null},{\"id\":5,\"itemId\":1,\"userId\":1,\"timestamp\":\"2020-07-25T15:35:03\",\"type\":\"MOVED\",\"data\":\"{\\\"from\\\":\\\"Store: Slot2\\\",\\\"to\\\":\\\"Store: Slot\\\"}\"},{\"id\":4,\"itemId\":1,\"userId\":1,\"timestamp\":\"2020-07-25T15:34:50\",\"type\":\"MOVED\",\"data\":\"{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}\"},{\"id\":1,\"itemId\":1,\"userId\":1,\"timestamp\":\"2020-07-25T15:34:13\",\"type\":\"CREATED\",\"data\":null}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetHistoryForeign() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '0', '1', '0', '1')",
						"INSERT INTO `item_history` (`id`, `item_id`, `type`, `user_id`, `timestamp`, `data`) VALUES (1, 1, 'CREATED', 1, '2020-07-25 15:34:13', NULL), (2, 2, 'BROKEN', 1, '2020-07-25 15:34:40', NULL), (3, 2, 'FIXED', 1, '2020-07-25 15:34:42', NULL), (4, 1, 'MOVED', 1, '2020-07-25 15:34:50', '{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}'), (5, 1, 'MOVED', 1, '2020-07-25 15:35:03', '{\\\"from\\\":\\\"Store: Slot2\\\",\\\"to\\\":\\\"Store: Slot\\\"}'), (6, 1, 'BROKEN', 1, '2020-07-25 15:35:06', NULL), (7, 1, 'FIXED', 6, '2020-07-25 15:36:04', NULL);"))
				.url(REST_URL + "/items/1/history").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/history\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":[{\"id\":7,\"itemId\":1,\"userId\":6,\"timestamp\":\"2020-07-25T15:36:04\",\"type\":\"FIXED\",\"data\":null},{\"id\":6,\"itemId\":1,\"userId\":1,\"timestamp\":\"2020-07-25T15:35:06\",\"type\":\"BROKEN\",\"data\":null},{\"id\":5,\"itemId\":1,\"userId\":1,\"timestamp\":\"2020-07-25T15:35:03\",\"type\":\"MOVED\",\"data\":\"{\\\"from\\\":\\\"Store: Slot2\\\",\\\"to\\\":\\\"Store: Slot\\\"}\"},{\"id\":4,\"itemId\":1,\"userId\":1,\"timestamp\":\"2020-07-25T15:34:50\",\"type\":\"MOVED\",\"data\":\"{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}\"},{\"id\":1,\"itemId\":1,\"userId\":1,\"timestamp\":\"2020-07-25T15:34:13\",\"type\":\"CREATED\",\"data\":null}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetHistoryUser() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES ('1000', 'Tes', 'Ter', 'FEMALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test@lh-tool.de', '123', '456', NULL, 'Hartzer', NULL)",
						"INSERT INTO `item_history` (`id`, `item_id`, `type`, `user_id`, `timestamp`, `data`) VALUES (1, 1, 'CREATED', 1000, '2020-07-25 15:34:13', NULL), (2, 1, 'BROKEN', 1, '2020-07-25 15:34:40', NULL), (3, 1, 'FIXED', 1, '2020-07-25 15:34:42', NULL), (4, 1, 'MOVED', 1, '2020-07-25 15:34:50', '{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}'), (5, 1, 'MOVED', 1, '2020-07-25 15:35:03', '{\\\"from\\\":\\\"Store: Slot2\\\",\\\"to\\\":\\\"Store: Slot\\\"}'), (6, 1, 'BROKEN', 1, '2020-07-25 15:35:06', NULL), (7, 1, 'FIXED', 6, '2020-07-25 15:36:04', NULL);",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/history/1/user").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, STORE_KEEPER_EMAIL,
								INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1000,\"firstName\":\"Tes\",\"lastName\":\"Ter\",\"gender\":null,\"email\":null,\"telephoneNumber\":null,\"mobileNumber\":null,\"businessNumber\":null,\"profession\":null,\"skills\":null,\"active\":null,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/history/1/user\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetItemImage() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('2', '1', 'Identifier2', '2', 'Item2', 'Description2', '100', 'Stück', '0', '1', '0', '1')",
						"INSERT INTO `item_image` (`id`, `item_id`, `image`, `media_type`) VALUES ('1', '1', 0x89504e470d0a1a0a0000000d4948445200000001000000010802000000907753de00000185694343504943432070726f66696c65000028917d913d48c3401886dfa64a8b541cec20e290a13a59f00fd14dab50840aa15668d5c1e4d21fa1494392e2e228b8161cfc59ac3ab838ebeae02a08823f204e8e4e8a2e52e27749a1458c771cf7f0def7bedc7d0708f532d3ac8e1140d36d339d4c88d9dc8a187a45986608a398969965cc4a520abee3eb1e01bedfc579967fdd9fa35bcd5b0c0888c433cc306de275e2c94ddbe0bc4f1c652559253e271e36e982c48f5c573c7ee35c7459e0995133939e238e128bc53656da98954c8d788238a66a3ae50b598f55ce5b9cb5729535efc95f18c9ebcb4b5ca735802416b00809221454b181326cc469d749b190a6f3848fbfdff54be452c8b501468e7954a04176fde07ff0bbb756617ccc4b8a2480ce17c7f9180442bb40a3e638dfc78ed3380182cfc095def257eac0d427e9b596163b027ab6818beb96a6ec01973b40df93219bb22b0569098502f07e46df94037a6f81ae55af6fcd739c3e0019ea55ea06383804868a94bde6f3ee707bdffead69f6ef07ac0a72be7aa7c4f1000000097048597300002e2300002e230178a53f760000000774494d4507e40808073313c15fa2710000001974455874436f6d6d656e74004372656174656420776974682047494d5057810e170000000c4944415408d7636060600000000400012734270a0000000049454e44ae426082, 'image/png');",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/image").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, STORE_KEEPER_EMAIL,
								INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":1,\"itemId\":1,\"image\":\"iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAABhWlDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AYht+mSotUHOwg4pChOlnwD9FNq1CECqFWaNXB5NIfoUlDkuLiKLgWHPxZrDq4OOvq4CoIgj8gTo5Oii5S4ndJoUWMdxz38N73vtx9Bwj1MtOsjhFA020znUyI2dyKGHpFmGYIo5iWmWXMSlIKvuPrHgG+38V5ln/dn6NbzVsMCIjEM8wwbeJ14slN2+C8TxxlJVklPiceNumCxI9cVzx+41x0WeCZUTOTniOOEovFNlbamJVMjXiCOKZqOuULWY9VzluctXKVNe/JXxjJ68tLXKc1gCQWsAgJIhRUsYEybMRp10mxkKbzhI+/3/VL5FLItQFGjnlUoEF2/eB/8Lu3VmF8zEuKJIDOF8f5GARCu0Cj5jjfx47TOAGCz8CV3vJX6sDUJ+m1lhY7Anq2gYvrlqbsAZc7QN+TIZuyKwVpCYUC8H5G35QDem+BrlWvb81znD4AGepV6gY4OASGipS95vPucHvf/q1p9u8HrApyvnqnxPEAAAAJcEhZcwAALiMAAC4jAXilP3YAAAAHdElNRQfkCAgHMxPBX6JxAAAAGXRFWHRDb21tZW50AENyZWF0ZWQgd2l0aCBHSU1QV4EOFwAAAAxJREFUCNdjYGBgAAAABAABJzQnCgAAAABJRU5ErkJggg==\",\"mediaType\":\"image/png\",\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/image\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetItemImageNotExisting() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES ('1', '1', 'Identifier1', '0', 'Item1', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/image").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, STORE_KEEPER_EMAIL,
								INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"id\":null,\"itemId\":1,\"image\":null,\"mediaType\":null,\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/image\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

	@Test
	public void testItemsGetRelatedItems() throws Exception {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24')",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES ('1', 'STANDARD', 'Store', NULL)",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `outside`) VALUES ('1', '1', 'Slot', NULL, '0')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('1', 'Technical Crew')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InNamem1', '1', '1', 'Identifier1', '0', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InDescription', '2', '1', 'Identifier2', '0', 'Description m1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InIdentifier', '3', '1', 'Identifierm1', '0', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES ('2', 'Technical Crew m1')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InTechnicalCrew', '4', '1', 'Identifier4', '0', 'Description 1', '1', 'Stück', '1', '1', '0', '2')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InNote', '5', '1', 'Identifier5', '0', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('InTag', '6', '1', 'Identifier6', '0', 'Description 1', '1', 'Stück', '1', '1', '0', '1')",
						"INSERT INTO `item` (`name`, `id`, `slot_id`, `identifier`, `has_barcode`, `description`, `quantity`, `unit`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) "
								+ "VALUES ('NotFound', '100', '1', 'Identifier100', '2', 'Description2', '100', 'Stück', '0', '1', '0', '1')",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES ('1', '5', NULL, 'Test Notiz m1', '2020-07-22 11:37:34'), (2, '100', '2', 'Noch eine Notiz', '2020-07-23 14:37:34')",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES ('1', 'Tagm1'), ('2', 'Tag2')",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES ('1', '6', '1'), ('2', '100', '2')",
						"INSERT INTO `item_item` (`id`, `item1_id`, `item2_id`) VALUES ('1', '1', '2'), ('2', '1', '4'),(3,1,6),(4,2,5),(5,2,6);",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES ('1', '1', '1', '2020-06-01', '2030-12-31');"))
				.url(REST_URL + "/items/1/items").method(Method.GET)
				.userTests(List.of(UserTest.builder()
						.emails(List.of(ADMIN_EMAIL, CONSTRUCTION_SERVANT_EMAIL, STORE_KEEPER_EMAIL,
								INVENTORY_MANAGER_EMAIL))
						.expectedHttpCode(HttpStatus.OK)
						.expectedResponse(
								"{\"links\":[{\"rel\":\"self\",\"href\":\"http://localhost:8080/lh-tool/rest/items/1/items\",\"hreflang\":null,\"media\":null,\"title\":null,\"type\":null,\"deprecation\":null}],\"content\":["
										+ "{\"id\":2,\"slotId\":1,\"identifier\":\"Identifier2\",\"hasBarcode\":false,\"name\":\"InDescription\",\"description\":\"Description m1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1},"
										+ "{\"id\":4,\"slotId\":1,\"identifier\":\"Identifier4\",\"hasBarcode\":false,\"name\":\"InTechnicalCrew\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":2},"
										+ "{\"id\":6,\"slotId\":1,\"identifier\":\"Identifier6\",\"hasBarcode\":false,\"name\":\"InTag\",\"description\":\"Description 1\",\"quantity\":1.0,\"unit\":\"Stück\",\"outsideQualified\":true,\"consumable\":true,\"broken\":false,\"technicalCrewId\":1}]}")
						.validationQueries(List.of()).build()))
				.httpCodeForOthers(HttpStatus.FORBIDDEN).validationQueriesForOthers(List.of()).build()));
	}

}
