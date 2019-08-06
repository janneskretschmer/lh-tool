CREATE TABLE `item_tag` ( `id` INT NOT NULL AUTO_INCREMENT , `item_id` INT NOT NULL , `tag_id` INT NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;
ALTER TABLE `item_tag` ADD UNIQUE( `item_id`, `tag_id`);
ALTER TABLE `item_tag` ADD FOREIGN KEY (`item_id`) REFERENCES `item`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `item_tag` ADD FOREIGN KEY (`tag_id`) REFERENCES `tag`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;