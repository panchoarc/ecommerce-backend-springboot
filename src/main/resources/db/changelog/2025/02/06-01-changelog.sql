-- liquibase formatted sql

-- changeset Francisco:1738870435920-1
ALTER TABLE endpoint
    ADD base_path VARCHAR(255);
ALTER TABLE endpoint
    ADD dynamic_path VARCHAR(255);

-- changeset Francisco:1738870435920-2
ALTER TABLE endpoint
    ALTER COLUMN base_path SET NOT NULL;

