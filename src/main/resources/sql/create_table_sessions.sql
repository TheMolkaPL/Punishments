CREATE TABLE IF NOT EXISTS %TABLE_NAME% (
        `_id` INT PRIMARY KEY AUTO_INCREMENT,
        `creation` TIMESTAMP NOT NULL DEFAULT NOW(),
        `client_id` INT NOT NULL,
        `username` VARCHAR(16) NOT NULL,
        `username_lower` VARCHAR(16) NOT NULL,
        `server_name` VARCHAR(64),
        PRIMARY KEY (`_id`)
);
