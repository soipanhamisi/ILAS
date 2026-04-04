-- Demo Admin Seed (legacy/manual fallback)
-- The app startup seeder now creates the demo admin automatically.

-- First, check if any admins exist:
SELECT COUNT(*) as admin_count FROM admin;

-- If count is 0, run this to create the demo admin used by the monitoring tests:
-- (Auth currently compares plaintext passwords, so this helper inserts password123 directly.)

INSERT INTO admin (admin_id, name, username, email, password, role, created_at, updated_at)
VALUES (
  1,
  'System Administrator',
  'admin',
  'admin@localhost.com',
  'password123',
  'ADMIN',
  NOW(),
  NOW()
);

-- Verify the admin was created:
SELECT * FROM admin WHERE admin_id = 1;

-- If you need to login with username/password:
-- Username: admin
-- Password: password123

-- If you already have another admin, note its admin_id:
SELECT admin_id, name, username FROM admin;

-- When logging in, use that admin account.


