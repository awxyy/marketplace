CREATE TABLE cart_items(
    id BIGINT PRIMARY KEY ,
    user_id BIGINT NOT NULL ,
    product_id BIGINT NOT NULL ,
    quantity INT CHECK (quantity > 0),
    added_at TIMESTAMP  NOT NULL DEFAULT NOW()

);
ALTER TABLE cart_items
    ADD CONSTRAINT fk_cart_items_user
        FOREIGN KEY (user_id)
            REFERENCES users(id);

ALTER TABLE cart_items
    ADD CONSTRAINT fk_cart_items_product
        FOREIGN KEY (product_id)
            REFERENCES products(id);