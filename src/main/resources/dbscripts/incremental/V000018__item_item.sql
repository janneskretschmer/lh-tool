CREATE TABLE `item_item` ( `id` INT NOT NULL AUTO_INCREMENT , `item1_id` INT NOT NULL , `item2_id` INT NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;
ALTER TABLE `item_item` ADD UNIQUE( `item1_id`, `item2_id`);
ALTER TABLE `item_item` ADD FOREIGN KEY (`item1_id`) REFERENCES `item`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `item_item` ADD FOREIGN KEY (`item2_id`) REFERENCES `item`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
