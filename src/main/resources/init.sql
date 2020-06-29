CREATE TABLE IF NOT EXISTS minecord (
    id TINYINT(1) PRIMARY KEY CHECK (id = 0),
    version TINYINT(2) NOT NULL
);
INSERT INTO minecord (id, version)
    VALUES(0, 1);
CREATE TABLE IF NOT EXISTS guild (
    id BIGINT(20) PRIMARY KEY NOT NULL,
    prefix TEXT(8),
    lang TEXT(5),
    banned TINYINT(1) NOT NULL DEFAULT 0,
    noCooldown TINYINT(1) NOT NULL DEFAULT 0,
    deleteCommands TINYINT(1),
    noMenu TINYINT(1)
);
CREATE TABLE IF NOT EXISTS user (
    id BIGINT(20) PRIMARY KEY NOT NULL,
    elevated TINYINT(1) NOT NULL DEFAULT 0,
    banned TINYINT(1) NOT NULL DEFAULT 0
);