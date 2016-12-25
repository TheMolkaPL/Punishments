SELECT * FROM %TABLE_NAME% WHERE `active` = 1 AND `player_id` = ? AND (`expires` = NULL OR `expires` > NOW());
