-- liquibase formatted sql

-- changeset Francisco:1745468542550-1
ALTER TABLE address
    ADD alias VARCHAR(255);

