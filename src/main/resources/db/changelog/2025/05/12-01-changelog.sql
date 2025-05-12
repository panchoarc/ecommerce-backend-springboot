-- liquibase formatted sql

-- changeset Francisco:1747083396732-1
ALTER TABLE review
    ADD created_at TIMESTAMP WITHOUT TIME ZONE;

