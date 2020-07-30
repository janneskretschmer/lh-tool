package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import de.lh.tool.rest.bean.EndpointTest;
import de.lh.tool.rest.bean.UserTest;
import io.restassured.http.Method;

public class ManualTestInitializer extends BasicRestIntegrationTest {
	@Test
	public void initializeDatabaseForManualTests() throws IOException {
		assertTrue(testEndpoint(EndpointTest.builder()//
				.initializationQueries(List.of(
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (1, 'Bauhelfer'), (2, 'Küche'), (3, 'Magaziner'), (4, 'Stadtfahrer'), (5, 'Pforte'), (6, 'Putzen'), (7, 'Tagwächter'), (8, 'Nachtwächter');",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES (1, 'STANDARD', 'Store', NULL);",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES (1, 1, 'Slot', NULL, NULL, NULL, NULL, 0), (2, 1, 'Slot2', NULL, NULL, NULL, NULL, 0);",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES (1, 'Technical Crew'), (2, 'TK2');",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES (1, 1, 'Identifier1', 0, 'Item1', 'Description 1', 1, 'Stück', 12, 24, 48, 1, 1, 1, 1), (2, 1, 'GJYRS2YDV', 1, 'Test2', 'Der zweite Testgegenstand, z.B. Schrauben oder sowas. Halt irgendwas, was man verbrauchen kann und eine etwas längere Beschreibung hat, damit man mal sieht, wie das aussieht. Deswegen besteht sie aus drei Sätzen, auch wenn es eigentlich viel kürzer gehen würde.', 100, 'Stück', 10, 25, 30, 0, 1, 0, 2);",
						"INSERT INTO `item_history` (`id`, `item_id`, `type`, `user_id`, `timestamp`, `data`) VALUES (1, 2, 'CREATED', 1, '2020-07-25 15:34:13', NULL), (2, 2, 'BROKEN', 1, '2020-07-25 15:34:40', NULL), (3, 2, 'FIXED', 1, '2020-07-25 15:34:42', NULL), (4, 2, 'MOVED', 1, '2020-07-25 15:34:50', '{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}'), (5, 2, 'MOVED', 1, '2020-07-25 15:35:03', '{\\\"from\\\":\\\"Store: Slot2\\\",\\\"to\\\":\\\"Store: Slot\\\"}'), (6, 2, 'BROKEN', 1, '2020-07-25 15:35:06', NULL), (7, 2, 'FIXED', 6, '2020-07-25 15:36:04', NULL);",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES (1, 'Tag1'), (2, 'Tag2');",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES (1, 1, 1), (2, 1, 2);",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES (1, 1, NULL, 'Test Notiz', '2020-07-22 09:37:34'), (2, 1, 2, 'Noch eine Notiz', '2020-07-23 12:37:34'), (3, 2, 1, 'Das ist ne Notiz ;)', '2020-07-25 15:35:24'), (4, 2, 6, 'Der Magaziner will auch was sagen', '2020-07-25 15:35:59');",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1, 'Test1', '2020-04-09', '2020-04-24'), (2, 'Test2', '2020-04-09', '2020-04-24');",
						"INSERT INTO `project_helper_type` (`id`, `project_id`, `helper_type_id`, `weekday`, `start_time`, `end_time`) VALUES (1, 1, 1, 1, '07:00:00', '17:00:00'), (2, 1, 2, 1, '07:00:00', '17:00:00'), (3, 2, 1, 1, '07:00:00', '17:00:00'), (4, 1, 3, 2, '07:00:00', '17:00:00');",
						"INSERT INTO `store_project` (`id`, `store_id`, `project_id`, `start`, `end`) VALUES (1, 1, 1, '2020-06-01', '2030-12-31');",
						"INSERT INTO project_user(project_id, user_id) SELECT 1,id FROM user"))
				.method(Method.GET).url(REST_URL + "/info/heartbeat")
				.userTests(List.of(UserTest.builder().emails(List.of()).build())).httpCodeForOthers(HttpStatus.OK)
				.build()));
	}
}
