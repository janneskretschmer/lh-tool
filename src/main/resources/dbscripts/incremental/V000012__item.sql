CREATE TABLE `item` (
  `id` int(11) NOT NULL,
  `slot_id` int(11) NOT NULL,
  `identifier` varchar(100) NOT NULL,
  `has_barcode` tinyint(1) NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL,
  `description` varchar(4000) DEFAULT NULL,
  `quantity` float NOT NULL DEFAULT '1',
  `unit` varchar(50) NOT NULL DEFAULT 'Stück',
  `width` float DEFAULT NULL,
  `height` float DEFAULT NULL,
  `depth` float DEFAULT NULL,
  `outside_qualified` tinyint(1) NOT NULL,
  `consumable` tinyint(1) NOT NULL DEFAULT '0',
  `broken` tinyint(1) NOT NULL DEFAULT '0',
  `picture_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
ALTER TABLE `item`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `identifier` (`identifier`),
  ADD KEY `slot_id` (`slot_id`);
ALTER TABLE `item`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `item`
  ADD FOREIGN KEY (`slot_id`) REFERENCES `slot` (`id`)  ON DELETE CASCADE ON UPDATE CASCADE;
  