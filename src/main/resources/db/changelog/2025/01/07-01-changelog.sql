-- liquibase formatted sql

-- changeset Francisco:1736226693671-1
ALTER TABLE product
    ALTER COLUMN price TYPE DECIMAL USING (price::DECIMAL);

-- changeset Francisco:1736226693671-2
ALTER TABLE order_item
    ALTER COLUMN price_at_purchase TYPE DECIMAL USING (price_at_purchase::DECIMAL);

-- changeset Francisco:1736226693671-3
ALTER TABLE orders
    ALTER COLUMN total_amount TYPE DECIMAL USING (total_amount::DECIMAL);

