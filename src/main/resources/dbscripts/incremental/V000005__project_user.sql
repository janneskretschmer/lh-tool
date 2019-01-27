CREATE TABLE `project_user` ( `id` INT NOT NULL AUTO_INCREMENT , `project_id` INT NOT NULL , `user_id` INT NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;
ALTER TABLE `project_user` ADD UNIQUE( `project_id`, `user_id`);
ALTER TABLE `project_user` ADD FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `project_user` ADD FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
