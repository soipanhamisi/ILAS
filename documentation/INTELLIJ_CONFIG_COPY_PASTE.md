# IntelliJ Frontend Run Configuration - COPY & PASTE VALUES

## Follow These Steps in IntelliJ

### Step 1: Open Edit Configurations
- Click `Run` menu
- Click `Edit Configurations...`

### Step 2: Add npm Configuration
- Click the `+` button (top left)
- Select `npm` from the list

### Step 3: Copy These Exact Values

#### Field 1: Name
```
Frontend Dev Server
```

#### Field 2: Package.json
```
C:\Users\Admin\Documents\ILAS\frontend\package.json
```
*Or use Browse button to select the file*

#### Field 3: Command
```
run
```
*(Should be default)*

#### Field 4: Scripts
```
dev
```

#### Field 5: Working directory
```
C:\Users\Admin\Documents\ILAS\frontend
```
*Or use Browse button to select the folder*

#### Field 6: Run in Terminal
```
CHECK the checkbox ☑
```

---

## Step 4: Save Configuration
- Click `Apply`
- Click `OK`

---

## Step 5: Run Frontend

### Option A: Click Run Button
```
1. Select "Frontend Dev Server" from dropdown (top right)
2. Click green ▶ button
```

### Option B: Keyboard Shortcut
```
Press: Shift + F10
```

### Option C: Run Menu
```
Click: Run → Run 'Frontend Dev Server'
```

---

## Step 6: Access Frontend

Terminal will show:
```
➜  Local:   http://localhost:3000/
```

Click the link or open `http://localhost:3000` in your browser

---

## Troubleshooting

### npm: command not found
- Install Node.js from nodejs.org
- Restart IntelliJ after installing

### Port 3000 in use
- Run in terminal: `netstat -ano | findstr :3000`
- Then: `taskkill /PID [number] /F`

### Working directory error
- Use Browse button instead of typing
- Make sure you select the `frontend` folder

---

## Done! ✅

Your Vue.js frontend is now ready to run from IntelliJ! 🚀

**Next time:** Just select `Frontend Dev Server` and click Run ▶

