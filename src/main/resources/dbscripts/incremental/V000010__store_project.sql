CREATE TABLE `store_project` (
  `id` int(11) NOT NULL,
  `store_id` int(11) NOT NULL,
  `project_id` int(11) NOT NULL,
  `start` date NOT NULL,
  `end` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
ALTER TABLE `store_project` ADD PRIMARY KEY(`id`);
ALTER TABLE `store_project` CHANGE `id` `id` INT(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `store_project` ADD UNIQUE( `store_id`, `project_id`, `start`);
ALTER TABLE `store_project` ADD FOREIGN KEY (`store_id`) REFERENCES `store`(`id`) ON DELETE CASCADE ON UPDATE CASCADE; 
ALTER TABLE `store_project` ADD FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;

