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
						"INSERT INTO `helper_type` (`id`, `name`) VALUES (1,'Bauhelfer'),(2,'Küche'),(3,'Magaziner'),(4,'Stadtfahrer'),(5,'Pforte'),(6,'Putzen'),(7,'Tagwächter'),(8,'Nachtwächter');",
						"INSERT INTO `item_tag` (`id`, `name`) VALUES (4,'bioib'),(8,'DAS'),(10,'ein'),(15,'generieren'),(9,'ist'),(3,'jkj'),(5,'jkjbl'),(6,'pb'),(1,'Tag1'),(2,'Tag2'),(7,'tag3'),(13,'Tags'),(12,'um'),(14,'zu');",
						"INSERT INTO `project` (`id`, `name`, `start_date`, `end_date`) VALUES (1,'Test1','2020-04-09','2020-04-24'),(2,'Test2','2020-08-22','2020-12-18');",
						"INSERT INTO `project_helper_type` (`id`, `project_id`, `helper_type_id`, `weekday`, `start_time`, `end_time`) VALUES (1,1,4,2,'07:00:00','17:00:00'),(2,1,1,3,'07:00:00','17:00:00'),(3,1,1,2,'07:00:00','17:00:00'),(4,1,6,2,'07:00:00',NULL),(5,1,3,2,'07:00:00','17:00:00'),(6,1,2,2,'07:00:00','17:00:00'),(7,1,2,3,'07:00:00','17:00:00'),(8,1,2,4,'07:00:00','17:00:00'),(9,1,6,3,'07:00:00',NULL),(10,1,3,3,'07:00:00','17:00:00'),(11,1,4,3,'07:00:00','17:00:00'),(12,1,1,4,'07:00:00','17:00:00'),(13,1,4,4,'07:00:00','17:00:00'),(14,1,2,5,'07:00:00','17:00:00'),(15,1,3,4,'07:00:00','17:00:00'),(16,1,6,4,'07:00:00',NULL),(17,1,3,5,'07:00:00','17:00:00'),(18,1,1,5,'07:00:00','17:00:00'),(19,1,4,5,'07:00:00','17:00:00'),(20,1,6,5,'07:00:00',NULL),(21,1,3,6,'07:00:00','17:00:00'),(22,1,2,6,'07:00:00','17:00:00'),(23,1,1,6,'07:00:00','17:00:00'),(24,1,4,6,'07:00:00','17:00:00'),(25,1,6,6,'07:00:00',NULL),(26,2,7,1,'07:00:00','10:00:00'),(27,2,7,1,'14:00:00','17:00:00'),(28,2,8,1,'22:00:00','02:00:00'),(29,2,8,1,'02:00:00','07:00:00'),(30,2,8,1,'17:00:00','22:00:00'),(31,2,7,1,'10:00:00','14:00:00'),(32,2,8,2,'02:00:00','07:00:00'),(33,2,8,2,'22:00:00','02:00:00'),(34,2,8,2,'17:00:00','22:00:00'),(35,2,5,2,'12:30:00','17:00:00'),(36,2,5,2,'07:00:00','12:30:00'),(37,2,5,3,'07:00:00','12:30:00'),(38,2,5,3,'12:30:00','17:00:00'),(39,2,8,3,'17:00:00','22:00:00'),(40,2,8,3,'02:00:00','07:00:00'),(41,2,5,4,'12:30:00','17:00:00'),(42,2,8,3,'22:00:00','02:00:00'),(43,2,5,4,'07:00:00','12:30:00'),(44,2,8,4,'22:00:00','02:00:00'),(45,2,5,5,'07:00:00','12:30:00'),(46,2,8,4,'02:00:00','07:00:00'),(47,2,8,4,'17:00:00','22:00:00'),(48,2,5,5,'12:30:00','17:00:00'),(49,2,8,5,'02:00:00','07:00:00'),(50,2,8,5,'22:00:00','02:00:00'),(51,2,8,5,'17:00:00','22:00:00'),(52,2,5,6,'07:00:00','12:30:00'),(53,2,5,6,'12:30:00','17:00:00'),(54,2,8,6,'02:00:00','07:00:00'),(55,2,8,6,'22:00:00','02:00:00'),(56,2,7,7,'14:00:00','17:00:00'),(57,2,7,7,'07:00:00','10:00:00'),(58,2,7,7,'10:00:00','14:00:00'),(59,2,8,6,'17:00:00','22:00:00'),(60,2,8,7,'02:00:00','07:00:00'),(61,2,8,7,'22:00:00','02:00:00'),(62,2,8,7,'17:00:00','22:00:00');",
						"INSERT INTO `project_user` (`id`, `project_id`, `user_id`) VALUES (1,2,1);",
						"INSERT INTO `store` (`id`, `type`, `name`, `address`) VALUES (1,'STANDARD','Store',NULL);",
						"INSERT INTO `technical_crew` (`id`, `name`) VALUES (1,'Technical Crew'),(2,'TK2');",
						"INSERT INTO `need` (`id`, `date`, `quantity`, `project_helper_type_id`) VALUES (1,'2020-04-09',34,12),(2,'2020-04-09',23,8),(3,'2020-04-09',1,15),(4,'2020-04-09',43,13),(5,'2020-04-09',12,16),(6,'2020-04-10',3,18),(7,'2020-04-10',2,14),(8,'2020-04-10',12,17),(9,'2020-04-10',2,19),(10,'2020-04-10',1,20),(11,'2020-04-11',1,22);",
						"INSERT INTO `need_user` (`id`, `need_id`, `user_id`, `state`) VALUES (1,11,1,'APPROVED'),(2,4,1,'REJECTED'),(3,3,1,'APPROVED');",
						"INSERT INTO `slot` (`id`, `store_id`, `name`, `description`, `width`, `height`, `depth`, `outside`) VALUES (1,1,'Slot',NULL,NULL,NULL,NULL,0),(2,1,'Slot2',NULL,NULL,NULL,NULL,0);",
						"INSERT INTO `item` (`id`, `slot_id`, `identifier`, `has_barcode`, `name`, `description`, `quantity`, `unit`, `width`, `height`, `depth`, `outside_qualified`, `consumable`, `broken`, `technical_crew_id`) VALUES (7,1,'HzoabqcJJ',1,'Kehlheim','jo',901,'Stück',1,3,5,0,1,0,2),(8,2,'bUrghFbe5',0,'Test3','',1,'Stück',5,5,5,0,0,0,1),(9,2,'1mc79etHL',1,'Test123','klm mlk klm mlk klm',3,'Stück',NULL,NULL,NULL,0,0,0,1),(10,2,'rFuPvhMhE',0,'Lager','',1,'Stück',3,1,NULL,0,0,0,1),(11,1,'K9HV3gHsB',0,'Jannes Kretschmer','',1,'Stück',NULL,NULL,NULL,0,0,1,2),(12,1,'gvdkrcjyA',0,'Test4','',2,'m',1,200,4,1,0,0,1),(13,1,'NXDh9aEka',0,'Kehlhei','',1,'Stück',NULL,NULL,NULL,0,0,0,1),(14,1,'ap04BIIJa',0,'Kehlhei','',1,'Stück',NULL,NULL,NULL,0,0,1,1),(15,2,'Xo0X0l3M2',0,'Test2','',1,'Stück',NULL,NULL,NULL,0,0,0,1),(16,1,'EgRj6UEtA',0,'Test5','getDerivedStateFromProps is invoked right before calling the render method, both on the initial mount and on subsequent updates. It should return an object to update the state, or null to update nothing.\n\nThis method exists for rare use cases where the state depends on changes in props over time. For example, it might be handy for implementing a <Transition> component that compares its previous and next children to decide which of them to animate in and out.',1,'Stück',1,3,1,0,1,0,1),(17,1,'DU1X9N0Ac',0,'Test6','',1,'Stück',NULL,NULL,NULL,1,0,0,2),(18,2,'nH52wd4iZ',1,'Test7','lknnklmnnß',1,'Stück',3,1,3,0,0,0,1),(19,2,'LU2z8YlzM',0,'Test7','lknnklmnnß',1,'Stück',3,1,3,0,0,0,1);",
						"INSERT INTO `item_history` (`id`, `item_id`, `type`, `user_id`, `timestamp`, `data`) VALUES (13,7,'CREATED',1,'2020-08-02 17:12:53',NULL),(14,8,'CREATED',1,'2020-08-03 13:11:13',NULL),(15,9,'CREATED',1,'2020-08-03 13:11:29',NULL),(16,9,'QUANTITY_CHANGED',1,'2020-08-03 18:15:50','{\\\"from\\\":\\\"1\\\",\\\"to\\\":\\\"3\\\"}'),(17,8,'UPDATED',1,'2020-08-04 05:14:30',NULL),(18,8,'UPDATED',1,'2020-08-04 05:15:40',NULL),(19,10,'CREATED',1,'2020-08-05 17:49:05',NULL),(20,11,'CREATED',1,'2020-08-05 17:58:55',NULL),(21,12,'CREATED',1,'2020-08-05 18:01:12',NULL),(22,13,'CREATED',1,'2020-08-05 18:02:47',NULL),(23,14,'CREATED',1,'2020-08-05 18:04:59',NULL),(24,15,'CREATED',1,'2020-08-05 18:07:26',NULL),(25,7,'UPDATED',1,'2020-08-05 18:09:33',NULL),(26,7,'QUANTITY_CHANGED',1,'2020-08-05 18:10:29','{\\\"from\\\":\\\"1\\\",\\\"to\\\":\\\"901\\\"}'),(27,7,'BROKEN',1,'2020-08-05 18:10:59',NULL),(28,7,'FIXED',1,'2020-08-05 18:11:00',NULL),(29,7,'BROKEN',1,'2020-08-05 18:11:01',NULL),(30,7,'FIXED',1,'2020-08-05 18:11:02',NULL),(31,9,'UPDATED',1,'2020-08-05 18:13:14',NULL),(32,11,'MOVED',1,'2020-08-05 18:15:36','{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}'),(33,10,'MOVED',1,'2020-08-05 18:15:36','{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}'),(34,7,'MOVED',1,'2020-08-05 18:15:36','{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}'),(35,14,'MOVED',1,'2020-08-05 18:15:36','{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}'),(36,9,'MOVED',1,'2020-08-05 18:15:36','{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}'),(37,13,'MOVED',1,'2020-08-05 18:15:36','{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}'),(38,8,'MOVED',1,'2020-08-05 18:15:36','{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}'),(39,12,'MOVED',1,'2020-08-05 18:15:36','{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}'),(40,15,'MOVED',1,'2020-08-05 18:15:36','{\\\"from\\\":\\\"Store: Slot\\\",\\\"to\\\":\\\"Store: Slot2\\\"}'),(41,12,'MOVED',1,'2020-08-05 18:16:02','{\\\"from\\\":\\\"Store: Slot2\\\",\\\"to\\\":\\\"Store: Slot\\\"}'),(42,14,'MOVED',1,'2020-08-05 18:16:02','{\\\"from\\\":\\\"Store: Slot2\\\",\\\"to\\\":\\\"Store: Slot\\\"}'),(43,11,'MOVED',1,'2020-08-05 18:16:02','{\\\"from\\\":\\\"Store: Slot2\\\",\\\"to\\\":\\\"Store: Slot\\\"}'),(44,7,'MOVED',1,'2020-08-05 18:16:02','{\\\"from\\\":\\\"Store: Slot2\\\",\\\"to\\\":\\\"Store: Slot\\\"}'),(45,13,'MOVED',1,'2020-08-05 18:16:02','{\\\"from\\\":\\\"Store: Slot2\\\",\\\"to\\\":\\\"Store: Slot\\\"}'),(46,11,'BROKEN',1,'2020-08-05 18:16:14',NULL),(47,14,'BROKEN',1,'2020-08-05 18:16:14',NULL),(48,12,'QUANTITY_CHANGED',1,'2020-08-05 18:17:02','{\\\"from\\\":\\\"1\\\",\\\"to\\\":\\\"2\\\"}'),(49,12,'UPDATED',1,'2020-08-05 18:17:02',NULL),(50,8,'UPDATED',1,'2020-08-05 18:20:03',NULL),(51,12,'UPDATED',1,'2020-08-05 18:20:57',NULL),(52,16,'CREATED',1,'2020-08-05 18:22:24',NULL),(53,17,'CREATED',1,'2020-08-05 18:24:38',NULL),(54,18,'CREATED',1,'2020-08-06 05:09:29',NULL),(55,19,'CREATED',1,'2020-08-06 05:34:21',NULL);",
						"INSERT INTO `item_item` (`id`, `item1_id`, `item2_id`) VALUES (16,7,9),(15,7,10),(14,7,11),(21,8,7),(24,8,10),(20,8,11),(22,8,12),(23,8,13),(19,8,14),(25,8,15),(1,9,8),(18,9,14),(17,9,15),(26,12,14),(11,15,10),(9,15,11),(27,17,9),(29,17,11),(30,17,12),(28,17,13),(31,18,7),(32,18,12),(33,18,15),(34,18,17),(37,19,7),(36,19,12),(35,19,15),(38,19,17);",
						"INSERT INTO `item_item_tag` (`id`, `item_id`, `item_tag_id`) VALUES (3,7,1),(8,7,2),(4,7,3),(5,7,4),(6,7,5),(7,7,6),(11,8,3),(12,8,7),(13,8,8),(14,8,9),(15,8,10),(17,8,12),(18,8,13),(19,8,14),(20,8,15),(9,9,2),(10,9,5),(23,17,3),(21,17,9),(22,17,15);",
						"INSERT INTO `item_note` (`id`, `item_id`, `user_id`, `note`, `timestamp`) VALUES (3,7,1,'jo jo jo','2020-08-05 18:10:55'),(4,8,1,'1. Notiz','2020-08-05 18:17:58'),(5,8,1,'2. Notiz','2020-08-05 18:18:05'),(6,8,1,'3. Notiz','2020-08-05 18:18:14'),(7,8,1,'4. Notiz etwas länger: getDerivedStateFromProps is invoked right before calling the render method, both on the initial mount and on subsequent updates. It should return an object to update the state, or null to update nothing.\n\nThis method exists for rare use cases where the state depends on changes in props over time. For example, it might be handy for implementing a <Transition> component that compares its previous and next children to decide which of them to animate in and out.','2020-08-05 18:19:16');"))
				.method(Method.GET).url(REST_URL + "/info/heartbeat")
				.userTests(List.of(UserTest.builder().emails(List.of()).build())).httpCodeForOthers(HttpStatus.OK)
				.build()));
	}
}
