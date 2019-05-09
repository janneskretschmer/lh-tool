ALTER TABLE `need` DROP INDEX `date`;
ALTER TABLE `need` ADD UNIQUE INDEX (
         `date`, 
         `helper_type`, 
         `project_id`);
