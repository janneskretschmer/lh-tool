CREATE TABLE `need` ( `id` INT NOT NULL AUTO_INCREMENT , `project_id` INT NOT NULL , `date` DATE NOT NULL , `quantity` INT NOT NULL , `helper_type` VARCHAR(20) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;
ALTER TABLE `need` ADD UNIQUE( `date`, `helper_type`);
ALTER TABLE `need` ADD FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
