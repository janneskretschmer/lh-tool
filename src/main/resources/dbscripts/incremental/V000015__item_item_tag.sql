CREATE TABLE `item_item_tag` ( `id` INT NOT NULL AUTO_INCREMENT , `item_id` INT NOT NULL , `item_tag_id` INT NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;
ALTER TABLE `item_item_tag` ADD UNIQUE( `item_id`, `item_tag_id`);
ALTER TABLE `item_item_tag` ADD FOREIGN KEY (`item_id`) REFERENCES `item`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `item_item_tag` ADD FOREIGN KEY (`item_tag_id`) REFERENCES `item_tag`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;