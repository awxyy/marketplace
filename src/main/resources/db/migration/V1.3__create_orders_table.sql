CREATE TABLE orders(
    id BIGINT PRIMARY KEY ,
    user_id BIGINT NOT NULL ,
    status VARCHAR(255) ,
    total_price NUMERIC(10, 2) NOT NULL ,
    created_at TIMESTAMP  NOT NULL DEFAULT NOW()
);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id)
            REFERENCES users(id);