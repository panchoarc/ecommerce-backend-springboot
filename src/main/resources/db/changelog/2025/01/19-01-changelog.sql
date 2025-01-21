-- liquibase formatted sql

-- changeset Francisco:1737312930881-4
ALTER TABLE endpoint
    ADD is_active BOOLEAN;

-- changeset Francisco:1737312930881-5
ALTER TABLE endpoint
    ALTER COLUMN is_active SET NOT NULL;

-- changeset Francisco:1737312930881-1
ALTER TABLE product
    ALTER COLUMN price TYPE DECIMAL USING (price::DECIMAL);

-- changeset Francisco:1737312930881-2
ALTER TABLE order_item
    ALTER COLUMN price_at_purchase TYPE DECIMAL USING (price_at_purchase::DECIMAL);

-- changeset Francisco:1737312930881-3
ALTER TABLE orders
    ALTER COLUMN total_amount TYPE DECIMAL USING (total_amount::DECIMAL);

