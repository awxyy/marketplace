CREATE TABLE reservations (
                              id BIGSERIAL PRIMARY KEY,
                              user_id BIGINT NOT NULL REFERENCES users(id),
                              product_id BIGINT NOT NULL REFERENCES products(id),
                              quantity INTEGER NOT NULL CHECK (quantity > 0),
                              reserved_at TIMESTAMP NOT NULL,
                              expires_at TIMESTAMP NOT NULL,
                              status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
                              UNIQUE(user_id, product_id)
);

CREATE INDEX idx_reservations_expires_at ON reservations(expires_at);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_reservations_user_product ON reservations(user_id, product_id);