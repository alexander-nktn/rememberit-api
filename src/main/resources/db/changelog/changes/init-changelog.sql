-- liquibase formatted sql

-- changeset alexander:1735527490024-1
CREATE TABLE cards (id VARCHAR(255) NOT NULL, "backgroundColor" VARCHAR(255), "createdAt" TIMESTAMP(6) WITHOUT TIME ZONE, "imageUrl" VARCHAR(255), "textColor" VARCHAR(255), "translatedTextColor" VARCHAR(255), translation_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, CONSTRAINT "cardsPK" PRIMARY KEY (id));

-- changeset alexander:1735527490024-2
CREATE TABLE permissions (id VARCHAR(255) NOT NULL, type VARCHAR(255) NOT NULL, CONSTRAINT "permissionsPK" PRIMARY KEY (id));

-- changeset alexander:1735527490024-3
CREATE TABLE refresh_tokens (id VARCHAR(255) NOT NULL, "expiryDate" TIMESTAMP(6) WITHOUT TIME ZONE, token VARCHAR(255), user_id VARCHAR(255) NOT NULL, CONSTRAINT "refresh_tokensPK" PRIMARY KEY (id));

-- changeset alexander:1735527490024-4
CREATE TABLE role_permissions (role_id VARCHAR(255) NOT NULL, permission_id VARCHAR(255) NOT NULL, CONSTRAINT "role_permissionsPK" PRIMARY KEY (role_id, permission_id));

-- changeset alexander:1735527490024-5
CREATE TABLE roles (id VARCHAR(255) NOT NULL, name VARCHAR(255), CONSTRAINT "rolesPK" PRIMARY KEY (id));

-- changeset alexander:1735527490024-6
CREATE TABLE translations (id VARCHAR(255) NOT NULL, source_language VARCHAR(255), target_language VARCHAR(255), text VARCHAR(255) NOT NULL, "translatedText" VARCHAR(255) NOT NULL, CONSTRAINT "translationsPK" PRIMARY KEY (id));

-- changeset alexander:1735527490024-7
CREATE TABLE users (id VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL, "firstName" VARCHAR(255) NOT NULL, "helloWorld" VARCHAR(255) NOT NULL, "lastName" VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, role_id VARCHAR(255) NOT NULL, CONSTRAINT "usersPK" PRIMARY KEY (id));

