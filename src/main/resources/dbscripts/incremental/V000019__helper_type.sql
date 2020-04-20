CREATE TABLE `helper_type` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(100) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;
INSERT INTO helper_type(id,name) VALUES 
(1, 'Bauhelfer'),
(2, 'Küche'),
(3, 'Magaziner'),
(4, 'Stadtfahrer'),
(5, 'Pforte'),
(6, 'Putzen'),
(7, 'Tagwächter'),
(8, 'Nachtwächter');

CREATE TABLE `project_helper_type` ( `id` INT NOT NULL AUTO_INCREMENT , `project_id` INT NOT NULL , `helper_type_id` INT NOT NULL , `weekday` INT NOT NULL , `start_time` TIME NOT NULL , `end_time` TIME, PRIMARY KEY (`id`)) ENGINE = InnoDB;
ALTER TABLE `project_helper_type` ADD FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `project_helper_type` ADD FOREIGN KEY (`helper_type_id`) REFERENCES `helper_type`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `project_helper_type` ADD UNIQUE( `project_id`, `helper_type_id`, `weekday`, `start_time`);
ALTER TABLE `project_helper_type` ADD UNIQUE( `project_id`, `helper_type_id`, `weekday`, `end_time`);
-- Construction workers
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,1,2,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,1,3,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,1,4,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,1,5,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,1,6,'07:00:00','17:00:00' FROM project;
-- Kitchen helpers
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,2,2,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,2,3,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,2,4,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,2,5,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,2,6,'07:00:00','17:00:00' FROM project;
-- Store keepers
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,3,2,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,3,3,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,3,4,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,3,5,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,3,6,'07:00:00','17:00:00' FROM project;
-- Drivers
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,4,2,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,4,3,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,4,4,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,4,5,'07:00:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,4,6,'07:00:00','17:00:00' FROM project;
-- Gate keepers
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,5,2,'07:00:00','12:30:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,5,3,'07:00:00','12:30:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,5,4,'07:00:00','12:30:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,5,5,'07:00:00','12:30:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,5,6,'07:00:00','12:30:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,5,2,'12:30:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,5,3,'12:30:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,5,4,'12:30:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,5,5,'12:30:00','17:00:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time, end_time) 
	SELECT id,5,6,'12:30:00','17:00:00' FROM project;
-- Cleaners
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time) 
	SELECT id,6,2,'17:05:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time) 
	SELECT id,6,3,'17:05:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time) 
	SELECT id,6,4,'17:05:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time) 
	SELECT id,6,5,'17:05:00' FROM project;
INSERT INTO project_helper_type (project_id, helper_type_id, weekday, start_time) 
	SELECT id,6,6,'17:05:00' FROM project;

ALTER TABLE `need` ADD `project_helper_type_id` INT NOT NULL AFTER `helper_type`;
UPDATE need n SET project_helper_type_id = (SELECT id FROM project_helper_type p WHERE p.project_id=n.project_id AND p.weekday=WEEKDAY(n.date)+1 
	AND IF(n.helper_type='GATEKEEPER_AFTERNOON',
		p.helper_type_id=5 AND p.start_time='12:30:00',
		n.helper_type= ELT(p.helper_type_id,'CONSTRUCTION_WORKER','KITCHEN_HELPER','STORE_KEEPER','DRIVER','GATEKEEPER_MORNING','CLEANER') AND p.start_time='07:00:00'
	));
	
ALTER TABLE `need` ADD FOREIGN KEY (`project_helper_type_id`) REFERENCES `project_helper_type`(`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE need DROP FOREIGN KEY `need_ibfk_1`;
ALTER TABLE `need` DROP INDEX `date`;
ALTER TABLE `need` DROP INDEX `project_id`;
ALTER TABLE `need` ADD UNIQUE( `date`, `project_helper_type_id`);
ALTER TABLE `need` DROP `helper_type`;
ALTER TABLE `need` DROP `project_id`;

