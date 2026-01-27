-- 1. СПОЧАТКУ створюємо продавців (Users) з ID 5 та 6
INSERT INTO users (id, full_name, login, password, created_at)
VALUES
    (5, 'Apple Reseller', 'seller5', '$2a$10$U5n/ysgNXgo.iRF5CFarN.of.XycCp34SlxWn3Tah7rzu2whbGKsG', NOW()),
    (6, 'Gadget Store', 'seller6', '$2a$10$U5n/ysgNXgo.iRF5CFarN.of.XycCp34SlxWn3Tah7rzu2whbGKsG', NOW())
ON CONFLICT DO NOTHING;

-- 2. ТЕПЕР додаємо продукти, прив'язані до цих ID
INSERT INTO products (name, description, price, status, seller)
VALUES
    ('iPhone 13', 'Apple smartphone A15 Bionic', 999.99, 'AVAILABLE', 5),
    ('iPhone 13 Pro', 'Improved iPhone with better camera', 1199.99, 'AVAILABLE', 6),
    ('iPhone 13 Mini', 'Compact version of iPhone 13', 899.99, 'AVAILABLE', 5),
    ('Samsung Galaxy S22', 'Samsung flagship phone', 899.99, 'AVAILABLE', 6),
    ('Samsung Galaxy S22 Ultra', 'Samsung high-end flagship', 1099.99, 'AVAILABLE', 5),
    ('MacBook Pro 14"', 'Apple laptop with M1 Pro chip', 1999.99, 'AVAILABLE', 6),
    ('MacBook Air M2', 'Lightweight Apple laptop', 1250.00, 'AVAILABLE', 5),
    ('Dell XPS 15', 'High-end Windows ultrabook', 1499.99, 'AVAILABLE', 6),
    ('Dell XPS 13', 'Compact premium laptop', 1299.99, 'AVAILABLE', 5),
    ('Sony WH-1000XM4', 'Noise-cancelling headphones', 349.99, 'AVAILABLE', 6),
    ('Sony WH-1000XM5', 'Latest model with better ANC', 399.99, 'AVAILABLE', 5),
    ('Nike Air Max 270', 'Comfortable running shoes', 129.99, 'AVAILABLE', 6),
    ('Nike Air Max 90', 'Classic style with new design', 139.99, 'AVAILABLE', 5),
    ('Adidas Ultraboost', 'Lightweight sneakers', 159.99, 'AVAILABLE', 6),
    ('Kindle Paperwhite', 'E-reader with backlight', 139.99, 'AVAILABLE', 5),
    ('iPad Air', 'Apple tablet with M1 chip', 599.99, 'AVAILABLE', 6),
    ('PlayStation 5', 'Next-gen gaming console', 499.99, 'AVAILABLE', 5),
    ('Xbox Series X', 'High-performance gaming console', 499.99, 'AVAILABLE', 6),
    ('GoPro Hero 10', 'Action camera 5K video', 499.99, 'AVAILABLE', 5),
    ('JBL Flip 5', 'Portable Bluetooth speaker', 119.99, 'AVAILABLE', 6);