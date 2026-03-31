-- WebSocket Connection Fix - Admin Database Setup
-- Run this SQL if you're getting "Unable to connect via WebSocket" error

-- First, check if any admins exist:
SELECT COUNT(*) as admin_count FROM admin;

-- If count is 0, run this to create a test admin:
-- (Adjust the password hash as needed - shown is a bcrypt hash for "admin123")

INSERT INTO admin (admin_id, name, username, email, password, role, created_at, updated_at)
VALUES (
  1,
  'System Administrator',
  'admin',
  'admin@localhost.com',
  '$2a$10$slYQmyNdGzin7olVN3p1OPST9/PgBkqquzi8Ay0IQI7tEf5ZtxK6m',  -- bcrypt: admin123
  'ADMIN',
  NOW(),
  NOW()
);

-- Verify the admin was created:
SELECT * FROM admin WHERE admin_id = 1;

-- If you need to login with username/password:
-- Username: admin
-- Password: admin123

-- If you already have another admin, note its admin_id:
SELECT admin_id, name, username FROM admin;

-- When logging in, use that admin account
-- The WebSocket will automatically use the logged-in admin's ID

