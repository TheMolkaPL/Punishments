SELECT * FROM %TABLE_NAME% WHERE `active` = 1 AND `punisher_id` = ? AND (`expires` = NULL OR `expires` > NOW());
