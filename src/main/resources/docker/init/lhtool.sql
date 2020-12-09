-- phpMyAdmin SQL Dump
-- version 4.6.6deb5
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Erstellungszeit: 27. Jan 2019 um 17:26
-- Server-Version: 5.7.25-0ubuntu0.18.04.2
-- PHP-Version: 7.2.10-0ubuntu0.18.04.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;


DELIMITER $$
CREATE PROCEDURE `truncate_all`()
    MODIFIES SQL DATA
    DETERMINISTIC
    SQL SECURITY INVOKER
BEGIN
	DECLARE finished INTEGER DEFAULT 0;
    DECLARE tableName VARCHAR(255);
	DEClARE tableCursor 
		CURSOR FOR SELECT table_name FROM information_schema.tables WHERE table_schema='lhtool' AND table_name != 'schema_version';
	DECLARE CONTINUE HANDLER 
        FOR NOT FOUND SET finished = 1;
    
    SET FOREIGN_KEY_CHECKS=0;
    
    OPEN tableCursor;
    trunc: LOOP
    	FETCH tableCursor INTO tableName;
        IF finished=1 THEN 
        	LEAVE trunc;
        END IF;
        SET @query=CONCAT('DELETE FROM ',tableName);
        PREPARE statement FROM @query;
        EXECUTE statement;
        IF ROW_COUNT() > 0 THEN
	        SET @query=CONCAT('ALTER TABLE ',tableName,' AUTO_INCREMENT = 1');
	        PREPARE statement FROM @query;
	        EXECUTE statement;
        END IF;
    END LOOP trunc;
	CLOSE tableCursor;
    
    SET FOREIGN_KEY_CHECKS=0;
    
END$$
DELIMITER ;



--
-- Datenbank: `lhtool`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `password_change_token`
--

CREATE TABLE `password_change_token` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `token` varchar(128) NOT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `schema_version`
--

CREATE TABLE `schema_version` (
  `version_rank` int(11) NOT NULL,
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) NOT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `schema_version`
--

INSERT INTO `schema_version` (`version_rank`, `installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES
(1, 1, '000000', 'users', 'SQL', 'V000000__users.sql', -979291555, 'lh-tool', '2019-01-20 02:35:22', 21, 1),
(2, 2, '000001', 'password change token', 'SQL', 'V000001__password_change_token.sql', 1082974610, 'lh-tool', '2019-01-20 02:35:22', 19, 1),
(3, 3, '000002', 'alter user', 'SQL', 'V000002__alter_user.sql', -1154653996, 'lh-tool', '2019-01-20 02:35:22', 29, 1),
(4, 4, '000003', 'user role', 'SQL', 'V000003__user_role.sql', 876529757, 'lh-tool', '2019-01-20 02:35:23', 17, 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `gender` varchar(6) NOT NULL,
  `password_hash` varchar(60) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `telephone_number` varchar(30) DEFAULT NULL,
  `mobile_number` varchar(30) DEFAULT NULL,
  `business_number` varchar(30) DEFAULT NULL,
  `profession` varchar(250) DEFAULT NULL,
  `skills` varchar(4000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `user`
--

INSERT INTO `user` (`id`, `first_name`, `last_name`, `gender`, `password_hash`, `email`, `telephone_number`, `mobile_number`, `business_number`, `profession`, `skills`) VALUES
(1, 'Ad', 'Min', 'MALE', '$2a$10$SfXYNzO70C1BqSPOIN0oYOwkz2hPWaXWvRc5aWBHuYxNNlpmciE9W', 'test-admin@lh-tool.de', NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user_role`
--

CREATE TABLE `user_role` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `role` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `user_role`
--

INSERT INTO `user_role` (`id`, `user_id`, `role`) VALUES
(1, 1, 'ROLE_ADMIN');

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `password_change_token`
--
ALTER TABLE `password_change_token`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id_UNIQUE` (`user_id`);

--
-- Indizes für die Tabelle `schema_version`
--
ALTER TABLE `schema_version`
  ADD PRIMARY KEY (`version`),
  ADD KEY `schema_version_vr_idx` (`version_rank`),
  ADD KEY `schema_version_ir_idx` (`installed_rank`),
  ADD KEY `schema_version_s_idx` (`success`);

--
-- Indizes für die Tabelle `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email_UNIQUE` (`email`);

--
-- Indizes für die Tabelle `user_role`
--
ALTER TABLE `user_role`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id_UNIQUE` (`user_id`,`role`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `password_change_token`
--
ALTER TABLE `password_change_token`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT für Tabelle `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT für Tabelle `user_role`
--
ALTER TABLE `user_role`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `password_change_token`
--
ALTER TABLE `password_change_token`
  ADD CONSTRAINT `fk_password_change_token_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `user_role`
--
ALTER TABLE `user_role`
  ADD CONSTRAINT `fk_user_role_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
