CREATE TABLE `project` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(150) NOT NULL , `start_date` DATE NOT NULL , `end_date` DATE NOT NULL , PRIMARY KEY (`id`), UNIQUE (`name`));
