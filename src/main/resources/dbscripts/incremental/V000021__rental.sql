CREATE TABLE `rental` ( `id` INT NOT NULL AUTO_INCREMENT , `item_id` INT NOT NULL , `user_id` INT NOT NULL , `store_keeper_id` INT NOT NULL , `start` DATETIME NOT NULL , `end` DATETIME NULL, PRIMARY KEY (`id`)) ENGINE = InnoDB;

ALTER TABLE `rental` ADD CONSTRAINT `rental_item_id` FOREIGN KEY (`item_id`) REFERENCES `item`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `rental` ADD CONSTRAINT `rental_user_id` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `rental` ADD CONSTRAINT `rental_store_keeper_id` FOREIGN KEY (`store_keeper_id`) REFERENCES `user`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
