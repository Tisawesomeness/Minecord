CREATE TABLE IF NOT EXISTS minecord (
    id      TINYINT(1) PRIMARY KEY CHECK (id = 0),
    version TINYINT(2) NOT NULL
);
INSERT INTO minecord (id, version)
    VALUES(0, 1);
CREATE TABLE IF NOT EXISTS guild (
    id       BIGINT(20) PRIMARY KEY NOT NULL,
    banned   TINYINT(1) NOT NULL DEFAULT 0,
    prefix   TEXT(8),
    lang     TEXT(5),
    use_menu TINYINT(1)
);
CREATE TABLE IF NOT EXISTS channel (
    id       BIGINT(20) PRIMARY KEY NOT NULL,
    guild_id BIGINT(20) NOT NULL UNIQUE,
    prefix   TEXT(8),
    lang     TEXT(5),
    use_menu TINYINT(1)
);
CREATE TABLE IF NOT EXISTS user (
    id       BIGINT(20) PRIMARY KEY NOT NULL,
    banned   TINYINT(1) NOT NULL DEFAULT 0,
    elevated TINYINT(1) NOT NULL DEFAULT 0,
    prefix   TEXT(8),
    lang     TEXT(5)
);