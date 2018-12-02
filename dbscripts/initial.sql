-- Please copy and execute the following commands manually as root:

CREATE DATABASE IF NOT EXISTS lhtool;

CREATE USER IF NOT EXISTS '<name>'@'localhost' IDENTIFIED BY '<password>';

GRANT ALL ON lhtool.* TO '<name>'@'localhost';

USE lhtool;

CREATE TABLE `lhtool`.`dbscript` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `checksum` VARCHAR(32) NOT NULL,
  `timestamp` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC));
