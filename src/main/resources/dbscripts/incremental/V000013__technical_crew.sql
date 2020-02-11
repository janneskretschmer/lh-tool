CREATE TABLE `technical_crew` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(100) NOT NULL , PRIMARY KEY (`id`), UNIQUE(name)) ENGINE = InnoDB;
ALTER TABLE `item` ADD `technical_crew_id` INT NOT NULL AFTER `picture_url`;
ALTER TABLE `item` ADD FOREIGN KEY (`technical_crew_id`) REFERENCES `technical_crew`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;


