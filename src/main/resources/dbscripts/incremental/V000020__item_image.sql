ALTER TABLE `item` DROP `picture_url`;

CREATE TABLE `item_image`( `id` INT NOT NULL AUTO_INCREMENT , `item_id` INT NOT NULL , `image` MEDIUMBLOB NOT NULL , `media_type` VARCHAR(127) NOT NULL , PRIMARY KEY (`id`), UNIQUE (`item_id`)) ENGINE = InnoDB;

ALTER TABLE `item_image` ADD CONSTRAINT `item_image_item_id` FOREIGN KEY (`item_id`) REFERENCES `item`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;