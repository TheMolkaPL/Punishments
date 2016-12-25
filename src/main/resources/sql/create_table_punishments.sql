CREATE TABLE IF NOT EXISTS %TABLE_NAME% (
        `_id` INT AUTO_INCREMENT,
        `active` BOOL NOT NULL DEFAULT TRUE,
        `creation` TIMESTAMP NOT NULL DEFAULT NOW(),
        `expires` TIMESTAMP DEFAULT NULL,
        `player_id` INT NOT NULL,
        `punisher_id` INT NOT NULL,
        `reason` VARCHAR(255) DEFAULT NULL,
        `server_name` VARCHAR(64),
        `type` INT NOT NULL,
        PRIMARY KEY (`_id`)
);
