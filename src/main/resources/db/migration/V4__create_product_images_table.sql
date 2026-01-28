CREATE TABLE product_images (
                                id BIGSERIAL PRIMARY KEY,
                                product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
                                file_key VARCHAR(255) NOT NULL,
                                url VARCHAR(512) NOT NULL,
                                is_main BOOLEAN DEFAULT FALSE,
                                sort_order INT DEFAULT 0,
                                content_type VARCHAR(50),
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX unique_main_image_per_product
    ON product_images (product_id)
    WHERE is_main = TRUE;