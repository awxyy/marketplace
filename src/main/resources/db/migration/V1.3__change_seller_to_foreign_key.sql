ALTER TABLE products
    ALTER COLUMN seller TYPE BIGINT USING seller::BIGINT,
    ALTER COLUMN seller SET NOT NULL;

ALTER TABLE products
    ADD CONSTRAINT fk_product_seller
        FOREIGN KEY (seller)
            REFERENCES users(id);
