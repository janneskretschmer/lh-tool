CREATE TABLE `store_type` ( `id` INT NOT NULL AUTO_INCREMENT , `key` VARCHAR(10) NOT NULL , `name` VARCHAR(50) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;
ALTER TABLE `store_type` ADD UNIQUE( `key`);
INSERT INTO store_type(`key`,name) VALUES ('MAIN','Hauptlager'),('STANDARD','Lager'),('MOBILE','Magazin');
