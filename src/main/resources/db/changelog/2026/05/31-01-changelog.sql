-- liquibase formatted sql

-- changeset panch:1780214997970-5
CREATE UNIQUE INDEX "IX_pk_role_permissions" ON role_permissions (role_id, permission_id);

-- changeset panch:1780214997970-1
ALTER TABLE orders
    ALTER COLUMN order_number DROP NOT NULL;

-- changeset panch:1780214997970-2
ALTER TABLE product
    ALTER COLUMN price TYPE DECIMAL USING (price::DECIMAL);

-- changeset panch:1780214997970-3
ALTER TABLE order_item
    ALTER COLUMN price_at_purchase TYPE DECIMAL USING (price_at_purchase::DECIMAL);

-- changeset panch:1780214997970-4
ALTER TABLE orders
    ALTER COLUMN total_amount TYPE DECIMAL USING (total_amount::DECIMAL);

