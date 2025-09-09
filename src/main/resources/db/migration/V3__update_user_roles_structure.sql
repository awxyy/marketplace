ALTER TABLE users DROP COLUMN IF EXISTS role;

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO roles (name) VALUES ('USER'), ('SELLER');

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE r.name = 'USER';

-- Insert new users with both USER and SELLER roles
INSERT INTO users (login, password, full_name, created_at) VALUES
('seller1', '$2a$10$YourHashedPasswordHere', 'Seller One', NOW()),
('seller2', '$2a$10$YourHashedPasswordHere', 'Seller Two', NOW());

-- Assign USER role to new sellers
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.login IN ('seller1', 'seller2') AND r.name = 'USER';

-- Assign SELLER role to new sellers
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.login IN ('seller1', 'seller2') AND r.name = 'SELLER';