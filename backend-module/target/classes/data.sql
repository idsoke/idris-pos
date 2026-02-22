-- Initial Data for Warungku POS
-- Note: This data is for development/testing purposes.

-- Outlets
INSERT INTO outlets (id, name, address, phone, email, currency, timezone, is_active) 
VALUES (1, 'Warungku Pusat', 'Jl. Malioboro No. 123, Yogyakarta', '081234567890', 'admin@warungku.com', 'IDR', 'Asia/Jakarta', true)
ON DUPLICATE KEY UPDATE name=name;

-- Users
-- Password is 'password' (bcrypt hash)
INSERT INTO users (id, tenant_id, name, email, password, role, is_active) 
VALUES (1, 1, 'Admin Warung', 'admin@warungku.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'ADMIN', true)
ON DUPLICATE KEY UPDATE name=name;

-- Categories
INSERT INTO categories (id, tenant_id, name, sort_order, is_active) VALUES 
(1, 1, 'Makanan', 1, true),
(2, 1, 'Minuman', 2, true)
ON DUPLICATE KEY UPDATE name=name;

-- Restaurant Tables
INSERT INTO restaurant_tables (id, tenant_id, name, capacity, location, status) VALUES 
(1, 1, 'Meja 01', 4, 'Indoor', 'AVAILABLE'),
(2, 1, 'Meja 02', 4, 'Indoor', 'OCCUPIED'),
(3, 1, 'Meja 03', 2, 'Indoor', 'AVAILABLE'),
(4, 1, 'Meja 04', 6, 'Outdoor', 'DIRTY'),
(5, 1, 'VIP 01', 10, 'VIP Room', 'RESERVED')
ON DUPLICATE KEY UPDATE name=name;
