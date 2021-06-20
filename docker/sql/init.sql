DROP TABLE IF EXISTS board_response_users;
DROP TABLE IF EXISTS board_responses;
DROP TABLE IF EXISTS board_users;
DROP TABLE IF EXISTS board_categories;
DROP TABLE IF EXISTS user_images;
DROP TABLE IF EXISTS user_confirmations;
DROP TABLE IF EXISTS user_forgotemails;
DROP TABLE IF EXISTS boards;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS ip_strings;

CREATE TABLE roles (
	id BIGSERIAL NOT NULL ,
	is_admin BOOLEAN NOT NULL DEFAULT false, 
	power_level INT NOT NULL,
	name VARCHAR(10) NOT NULL,
	is_deleted BOOLEAN NOT NULL DEFAULT false, 
	PRIMARY KEY (id)
);

INSERT INTO roles (is_admin, power_level, name)
VALUES (true, 1, 'ADMIN');

INSERT INTO roles (is_admin, power_level, name)
VALUES (false, 0, 'USER');

CREATE TABLE users (
	id BIGSERIAL NOT NULL UNIQUE ,
	name VARCHAR(50) NOT NULL UNIQUE,  
	password VARCHAR(128),
	role_id BIGINT , 
	email VARCHAR(255) NOT NULL UNIQUE,
	fail_times INT NOT NULL DEFAULT 0,
	is_deleted BOOLEAN NOT NULL DEFAULT false,
	is_verified BOOLEAN NOT NULL DEFAULT false,
	is_banned BOOLEAN NOT NULL DEFAULT false,
	updated TIMESTAMP WITH TIME ZONE,
	created TIMESTAMP WITH TIME ZONE,
	PRIMARY KEY (id),
	FOREIGN KEY (role_id) REFERENCES roles(id)
);
CREATE INDEX idx_users_role_id ON users (role_id);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_is_deleted ON users (is_deleted);
CREATE INDEX idx_created ON users (created);

CREATE TABLE user_confirmations (
	confirmation_token CHAR(73) NOT NULL UNIQUE,
	user_id BIGINT NOT NULL, 
	is_verified BOOLEAN NOT NULL DEFAULT false,
	updated TIMESTAMP WITH TIME ZONE,
	created TIMESTAMP WITH TIME ZONE,
	PRIMARY KEY (confirmation_token)
);
CREATE INDEX idx_user_confirmations_user_id ON user_confirmations (user_id);
CREATE INDEX idx_user_confirmations_confirmation_token ON user_confirmations (confirmation_token);
CREATE INDEX idx_user_confirmations_created ON user_confirmations (created);

CREATE TABLE user_forgotemails (
	confirmation_token CHAR(73) NOT NULL UNIQUE,
	user_id BIGINT NOT NULL, 
	is_used BOOLEAN NOT NULL DEFAULT false,
	updated TIMESTAMP WITH TIME ZONE,
	created TIMESTAMP WITH TIME ZONE,
	PRIMARY KEY (confirmation_token)
);
CREATE INDEX idx_user_forgotemails_user_id ON user_forgotemails (user_id);
CREATE INDEX idx_user_forgotemails_confirmation_token ON user_forgotemails (confirmation_token);
CREATE INDEX idx_user_forgotemails_created ON user_forgotemails (created);

CREATE TABLE user_images (
	id BIGSERIAL NOT NULL ,
	user_id BIGINT NOT NULL,	
	name VARCHAR NOT NULL,
	path VARCHAR(500),
    detail VARCHAR(300),
	is_main BOOLEAN NOT NULL DEFAULT false, 
	is_deleted BOOLEAN NOT NULL DEFAULT false, 
	updated TIMESTAMP WITH TIME ZONE,
	created TIMESTAMP WITH TIME ZONE,
	PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_user_images_user_id ON user_images (user_id);

CREATE TABLE boards (
	id BIGSERIAL NOT NULL,
	user_id BIGINT  NOT NULL,
	name VARCHAR(150) NOT NULL,
	table_url_name CHAR(73) NOT NULL UNIQUE,
	detail VARCHAR(700), 
	is_private BOOLEAN NOT NULL DEFAULT false,
	is_deleted BOOLEAN NOT NULL DEFAULT false,
	is_deleted_by_admin BOOLEAN NOT NULL DEFAULT false,
	updated TIMESTAMP WITH TIME ZONE,
	created TIMESTAMP WITH TIME ZONE,
	PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_boards_url_name ON boards (table_url_name);
CREATE INDEX idx_boards_is_deleted ON boards (is_deleted);
CREATE INDEX idx_user_id ON boards (user_id);

CREATE TABLE categories (
	id BIGSERIAL NOT NULL,
	url_name VARCHAR(50) NOT NULL UNIQUE,
	name VARCHAR(50) NOT NULL, 
	icon VARCHAR(50) NOT NULL,
	detail VARCHAR(500), 
	is_deleted BOOLEAN NOT NULL DEFAULT false, 
	updated TIMESTAMP WITH TIME ZONE,
	created TIMESTAMP WITH TIME ZONE,
	PRIMARY KEY (id)
);

INSERT INTO categories (url_name, name, icon, detail, is_deleted, updated, created)
VALUES ('free', 'Free', 'cil-chat-bubble', 'This a category where you can talk about anything.', false, NOW(), NOW());

INSERT INTO categories (url_name, name, icon, detail, is_deleted, updated, created)
VALUES ('hobby', 'Hobby', 'cil-bike','This a category where you can talk about hobbies like games, cinemas...', false, NOW(), NOW());

INSERT INTO categories (url_name, name, icon, detail, is_deleted, updated, created)
VALUES ('science', 'Science', 'cil-balance-scale', 'This a category where you can talk about science: math, propgramming, etc.', false, NOW(), NOW());

INSERT INTO categories (url_name, name, icon, detail, is_deleted, updated, created)
VALUES ('other', 'Other', 'cil-asterisk-circle', 'This a category where you can talk about anything that does not belong to other categories.', false, NOW(), NOW());

CREATE INDEX idx_categories_is_deleted ON categories (is_deleted);
CREATE INDEX idx_categories_url_name ON categories (url_name);

CREATE TABLE board_categories (
	id BIGSERIAL NOT NULL,
	board_id BIGINT NOT NULL,
	category_id BIGINT NOT NULL,
	is_deleted BOOLEAN NOT NULL DEFAULT false, 
	updated TIMESTAMP WITH TIME ZONE,
	created TIMESTAMP WITH TIME ZONE,
	FOREIGN KEY (board_id) REFERENCES boards(id),
	FOREIGN KEY (category_id) REFERENCES categories(id),
	PRIMARY KEY (id)
);

CREATE INDEX idx_board_categories_board_id ON board_categories (board_id);
CREATE INDEX idx_board_categories_category_id ON board_categories (category_id);
CREATE INDEX idx_board_categories_is_deleted ON board_categories (is_deleted);

CREATE TABLE board_users (
	id BIGSERIAL NOT NULL ,
	user_id BIGINT NOT NULL,
	board_id BIGINT NOT NULL,
	is_deleted BOOLEAN NOT NULL DEFAULT false, 
	updated TIMESTAMP WITH TIME ZONE,
	created TIMESTAMP WITH TIME ZONE,
	FOREIGN KEY (user_id) REFERENCES users(id),
	FOREIGN KEY (board_id) REFERENCES boards(id),
	PRIMARY KEY (id)
);

DROP TYPE IF EXISTS validMsgType;
CREATE TYPE validMsgType AS ENUM ('CHAT', 'JOIN', 'LEAVE', 'ERROR');

CREATE INDEX idx_board_users_user_id ON board_users (user_id);
CREATE INDEX idx_board_users_board_id ON board_users (board_id);
CREATE INDEX idx_board_users_is_deleted ON board_users (is_deleted);

CREATE TABLE board_responses (
	id BIGSERIAL NOT NULL,
	response VARCHAR(700),
	msg_type validMsgType,
	board_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL,
	ip_address VARCHAR(45) NOT NULL,
	is_first BOOLEAN NOT NULL DEFAULT false,
	res_number INT NOT NULL,
	is_deleted BOOLEAN NOT NULL DEFAULT false,
	is_deleted_by_admin BOOLEAN NOT NULL DEFAULT false,
	updated TIMESTAMP WITH TIME ZONE,
	created TIMESTAMP WITH TIME ZONE,
	FOREIGN KEY (board_id) REFERENCES boards(id),
	FOREIGN KEY (user_id) REFERENCES users(id),
	PRIMARY KEY (id)
);
CREATE INDEX idx_board_responses_board_id ON board_responses (board_id);
CREATE INDEX idx_board_responses_user_id ON board_responses (user_id);
CREATE INDEX idx_board_responses_updated ON board_responses (updated);
CREATE INDEX idx_board_responses_created ON board_responses (created);

INSERT INTO users(name, password, role_id, email, is_verified, updated, created)
VALUES ('Anonymous guest', 'Anonymous guest', 2, 'Anonymous guest', true, Now(), Now());

DROP TABLE IF EXISTS ip_strings;
CREATE TABLE ip_strings (
	id BIGSERIAL NOT NULL,
    ip_address VARCHAR(45) NOT NULL UNIQUE,
    string_id CHAR(8) NOT NULL,
	PRIMARY KEY (id)
);

CREATE INDEX idx_ip_string_ids_ip_address ON ip_strings (ip_address);

ALTER TABLE board_responses ADD COLUMN ip_string_id BIGINT;
ALTER TABLE board_responses ADD COLUMN string_id CHAR(8);

INSERT INTO categories (url_name, name, icon, detail, is_deleted, updated, created)
VALUES ('news', 'News', 'cil-book', 'This a category for news.', false, NOW(), NOW());
INSERT INTO categories (url_name, name, icon, detail, is_deleted, updated, created)
VALUES ('economy', 'Finance/Economy', 'cil-bank', 'This a category for Economy/Finance.', false, NOW(), NOW());
INSERT INTO categories (url_name, name, icon, detail, is_deleted, updated, created)
VALUES ('anime', 'Anime/Manga', 'cil-face', 'This a category for Anime.', false, NOW(), NOW());
INSERT INTO categories (url_name, name, icon, detail, is_deleted, updated, created)
VALUES ('game', 'Video Games', 'cil-gamepad', 'This a category for Video Games.', false, NOW(), NOW());

UPDATE categories SET url_name = 'lounge', name = 'Lounge' WHERE url_name = 'free';

ALTER TABLE categories ADD COLUMN display_order INT;

UPDATE categories SET display_order = 1 WHERE url_name = 'lounge';
UPDATE categories SET display_order = 2 WHERE url_name = 'hobby';
UPDATE categories SET display_order = 3 WHERE url_name = 'science';
UPDATE categories SET display_order = 4 WHERE url_name = 'news';
UPDATE categories SET display_order = 5 WHERE url_name = 'economy';
UPDATE categories SET display_order = 6 WHERE url_name = 'anime';
UPDATE categories SET display_order = 7 WHERE url_name = 'game';
UPDATE categories SET display_order = 8 WHERE url_name = 'other';
