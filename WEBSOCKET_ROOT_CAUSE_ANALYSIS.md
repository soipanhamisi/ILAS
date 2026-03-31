# WebSocket Connection - Root Cause Analysis & Fix

## Problem Statement
Still getting: "Unable to connect via WebSocket or HTTP" error

## Root Cause Analysis

Based on test cases, the most likely causes are:

### 1. ❌ No Admin in Database
The most likely issue is that **there is NO admin user with ID=1 in the database**

**How to verify:**
```sql
-- Connect to your database
SELECT * FROM admin;
SELECT * FROM admin WHERE admin_id = 1;
```

**If no results:** This is the problem! The WebSocket handler correctly rejects invalid admin IDs.

### 2. ❌ Wrong Admin ID in Frontend
The frontend might be sending a different admin ID than what exists in the database

**Frontend code:**
```javascript
useMonitoringWebSocket(authStore.userId)  // Is this the correct ID?
```

**To verify:**
1. Open DevTools → Console
2. Check: What is `authStore.userId`?
3. Query database: Does that admin exist?

### 3. ❌ Database Connection Issue
The Spring application might not be able to connect to the database

**How to verify:**
- Check application logs for connection errors
- Test with a simple endpoint that queries the database
- Verify `application.properties` has correct database credentials

---

## Solution Steps

### Step 1: Verify Admin Exists in Database

**If using MySQL:**
```sql
-- Login to your database
mysql -u root -p

-- Use the correct database (usually 'ilas' or similar)
USE ilas;

-- Check all admins
SELECT admin_id, name, username, email FROM admin;

-- Check specific admin
SELECT * FROM admin WHERE admin_id = 1;
```

**Expected output:**
```
| admin_id | name       | username | email           |
|----------|------------|----------|-----------------|
| 1        | Admin User | admin    | admin@email.com |
```

### Step 2: Get the Correct Admin ID

From the query above, note the `admin_id` value. Let's call it `YOUR_ADMIN_ID`.

### Step 3: Test with Correct Admin ID

**In AdminDashboard.vue**, check what adminId is being passed:

```javascript
// Check the actual value
console.log('Admin ID from auth:', authStore.userId)

// Use the correct ID from database
useMonitoringWebSocket(authStore.userId)  // Should match database admin_id
```

### Step 4: Create an Admin if None Exists

If no admin exists, you need to create one first:

**SQL:**
```sql
INSERT INTO admin (admin_id, name, username, email, password, role)
VALUES (1, 'Admin User', 'admin', 'admin@email.com', 'hashed_password', 'ADMIN');
```

**OR through the app:**
1. If there's an admin signup/creation endpoint, use it
2. Or use the database directly if you have credentials

### Step 5: Verify Login and Auth Flow

Make sure:
1. You're logged in as an admin (not student/instructor)
2. The `authStore.userId` returns a valid admin ID
3. That ID exists in the database

---

## Detailed Diagnostic Checklist

### ✓ Backend Diagnostics

- [ ] **Database Connection Working**
  - Can you access other endpoints that query the database?
  - Check logs for connection errors

- [ ] **Admin Exists in Database**
  - Run: `SELECT * FROM admin;`
  - At least one admin should exist

- [ ] **WebSocket Endpoint Registered**
  - Check logs for: `[WebSocket] Connection attempt...`
  - Verify no 404 errors for `/ws/monitoring`

- [ ] **Admin ID Format Correct**
  - Admin ID must be a positive integer
  - Check: Is it `1`, `5`, `100`? (not `"1"` or `admin` or `null`)

### ✓ Frontend Diagnostics

- [ ] **Auth Store Has Admin ID**
  - Open DevTools Console
  - Type: `console.log(authStore.userId)`
  - Should return a number like `1`

- [ ] **WebSocket URL Correct**
  - Should be: `ws://localhost:8080/ws/monitoring?adminId=1`
  - NOT: `ws://localhost:8080/ws/monitoring?adminId=null`

- [ ] **Logged In As Admin**
  - Are you logged in with an ADMIN account?
  - Or are you logged in as student/instructor?

- [ ] **Network Connected**
  - Check DevTools Network tab
  - Is WebSocket upgrade attempt being made?
  - What's the response code? (Should be 101)

### ✓ Integration Diagnostics

- [ ] **Check Browser Console**
  - Look for `[WebSocket]` logs
  - Do they show successful connection or failure?

- [ ] **Check Backend Console**
  - Look for `[WebSocket] Connection attempt...`
  - Does it show admin ID extraction?
  - Does it say "Admin not found"?

---

## Most Likely Solution

**99% chance the issue is:** No admin in database with the ID being sent

**Fix:**
1. Query: `SELECT * FROM admin;`
2. If empty: Create an admin
3. If exists: Use that admin_id in frontend
4. Restart backend
5. Test again

---

## Quick Test

### Step 1: Start Everything
```bash
# Terminal 1: Backend
mvn spring-boot:run

# Terminal 2: Frontend  
npm run dev
```

### Step 2: Check Database
Open your database tool and run:
```sql
SELECT * FROM admin;
```

### Step 3: Check Auth
Login and open DevTools Console:
```javascript
console.log('User ID:', authStore.userId)
console.log('User Type:', authStore.userType)
```

### Step 4: Check Logs

**Backend console should show:**
```
[WebSocket] Connection attempt. URI: ws://localhost:8080/ws/monitoring?adminId=X
[WebSocket] Query string: adminId=X
[WebSocket] Extracted adminId: X
[WebSocket] Validating admin with ID: X
[WebSocket] Admin not found with ID: X  ← IF THIS APPEARS, ADMIN DOESN'T EXIST
```

**OR:**
```
[WebSocket] ✓ Connection established for admin: X  ← SUCCESS
```

---

## If Admin Doesn't Exist

Create one using SQL (adjust values as needed):

```sql
-- For MySQL
INSERT INTO admin (admin_id, name, username, email, password, role, created_at)
VALUES (
  1,
  'System Administrator',
  'admin',
  'admin@example.com',
  'bcrypt_hashed_password_here',  -- Use a real bcrypt hash
  'ADMIN',
  NOW()
);
```

Or if you can login to the app, try creating via the signup endpoint.

---

## Next Steps

1. **Check Database:** Does admin exist?
2. **Check Auth:** What is `authStore.userId`?
3. **Check Logs:** What does backend show?
4. **Fix Issue:** Create admin or use correct ID
5. **Test Again:** Should work now!

---

## Expected Success Behavior

Once fixed, you should see:

**Backend Console:**
```
[WebSocket] Connection attempt. URI: ws://localhost:8080/ws/monitoring?adminId=1
[WebSocket] Query string: adminId=1
[WebSocket] Extracted adminId: 1
[WebSocket] Validating admin with ID: 1
[WebSocket] ✓ Connection established for admin: 1
```

**Frontend Console:**
```
[WebSocket] Component mounted, connecting with adminId: 1
[WebSocket] ✓ Connected successfully for admin: 1
[WebSocket] Message sent: subscribe
[WebSocket] Starting heartbeat
```

**Dashboard:**
- No error message
- Metrics displaying
- Real-time updates happening

✅ Success!

