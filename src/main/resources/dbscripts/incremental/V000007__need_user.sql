CREATE TABLE `need_user` (
  `id` int(11) NOT NULL,
  `need_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `state` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `need_user`
--
ALTER TABLE `need_user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `need_id` (`need_id`,`user_id`),
  ADD KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `need_user`
--
ALTER TABLE `need_user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `need_user`
--
ALTER TABLE `need_user`
  ADD CONSTRAINT `need_user_ibfk_1` FOREIGN KEY (`need_id`) REFERENCES `need` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `need_user_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
