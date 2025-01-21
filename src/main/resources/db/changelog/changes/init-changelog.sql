-- liquibase formatted sql

-- changeset alexander:1737501857700-1
CREATE TABLE cards (id VARCHAR(255) NOT NULL, background_color VARCHAR(255), created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL, image_url VARCHAR(255), text_color VARCHAR(255), translated_text_color VARCHAR(255), translation_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT "cardsPK" PRIMARY KEY (id));

-- changeset alexander:1737501857700-2
CREATE TABLE permissions (id VARCHAR(255) NOT NULL, type VARCHAR(255) NOT NULL, CONSTRAINT "permissionsPK" PRIMARY KEY (id));

-- changeset alexander:1737501857700-3
CREATE TABLE refresh_tokens (id VARCHAR(255) NOT NULL, expiry_date TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL, token VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT "refresh_tokensPK" PRIMARY KEY (id));

-- changeset alexander:1737501857700-4
CREATE TABLE role_permissions (role_id VARCHAR(255) NOT NULL, permission_id VARCHAR(255) NOT NULL, CONSTRAINT "role_permissionsPK" PRIMARY KEY (role_id, permission_id));

-- changeset alexander:1737501857700-5
CREATE TABLE roles (id VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL, CONSTRAINT "rolesPK" PRIMARY KEY (id));

-- changeset alexander:1737501857700-6
CREATE TABLE translations (id VARCHAR(255) NOT NULL, source_language VARCHAR(255) NOT NULL, target_language VARCHAR(255) NOT NULL, text VARCHAR(255) NOT NULL, translated_text VARCHAR(255) NOT NULL, CONSTRAINT "translationsPK" PRIMARY KEY (id));

-- changeset alexander:1737501857700-7
CREATE TABLE users (id VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL, first_name VARCHAR(255) NOT NULL, last_name VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, role_id VARCHAR(255) NOT NULL, CONSTRAINT "usersPK" PRIMARY KEY (id));

