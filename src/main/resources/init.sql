CREATE TABLE IF NOT EXISTS `guild` (
    `id` BIGINT(20) NOT NULL,
    `prefix` TEXT(8),
    `lang` TEXT(5),
    `banned` TINYINT(1) NOT NULL DEFAULT 0,
    `noCooldown` TINYINT(1) NOT NULL DEFAULT 0,
    `deleteCommands` TINYINT(1),
    `noMenu` TINYINT(1),
    PRIMARY KEY (`id`)
);
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT(20) NOT NULL,
    `elevated` TINYINT(1) NOT NULL DEFAULT 0,
    `banned` TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
);