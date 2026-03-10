# 🚀 Running Vue.js Frontend in IntelliJ IDEA - Complete Guide

## Method 1: npm Run Configuration (BEST for IntelliJ)

This is the cleanest and most integrated approach with IntelliJ IDEA.

### Step-by-Step Setup:

1. **Open Run/Debug Configurations:**
   - Click `Run` menu → `Edit Configurations...`
   - Or press `Alt + Shift + D` (Windows)
   - Or click the dropdown next to the run button (top right)

2. **Create New npm Configuration:**
   - Click the `+` button (Add New Configuration)
   - Select `npm` from the list
   - If `npm` is not visible, scroll down in the dropdown

3. **Fill in Configuration Details:**
   ```
   Name:                    Frontend Dev Server
   Package.json:            C:\Users\Admin\Documents\ILAS\frontend\package.json
   Command:                 run
   Scripts:                 dev
   Node interpreter:        Leave as default (or select your Node.js)
   npm package:             Leave as default
   Working directory:       C:\Users\Admin\Documents\ILAS\frontend
   Environment variables:   (Leave empty)
   Run in Terminal:         CHECK this box
   ```

4. **Apply Configuration:**
   - Click `Apply`
   - Click `OK`

5. **Run the Frontend:**
   - In the top right dropdown, select `Frontend Dev Server`
   - Click the green Run button (▶)
   - Or press `Shift + F10` (Windows)

---

## Method 2: Shell Script Configuration

If npm doesn't appear in your list, use Shell Script.

1. **Open Run/Debug Configurations:**
   - Click `Run` → `Edit Configurations...`

2. **Create New Shell Script:**
   - Click `+` button
   - Select `Shell Script`

3. **Configure:**
   ```
   Name:                Frontend Dev Server
   Execute:             Script text
   Script text:         cd frontend && npm run dev
   Working directory:   C:\Users\Admin\Documents\ILAS
   Run in Terminal:     CHECK this box
   ```

4. **Save and Run:**
   - Click `OK`
   - Select configuration and click Run

---

## Method 3: Compound Configuration (Run Backend + Frontend Together)

Run both servers with one click.

### Prerequisites:
- You already have a `Spring Boot` run configuration for the backend

### Steps:

1. **Open Run/Debug Configurations:**
   - Click `Run` → `Edit Configurations...`

2. **Create Compound Configuration:**
   - Click `+` button
   - Select `Compound`

3. **Configure:**
   ```
   Name:        Full Stack (Frontend + Backend)
   ```

4. **Add Configurations:**
   - Click `+` button in the configurations list
   - Select your Spring Boot configuration (backend)
   - Click `+` again
   - Select `Frontend Dev Server` (npm configuration)
   - Make sure both are checked

5. **Run Both:**
   - Select `Full Stack (Frontend + Backend)` from dropdown
   - Click Run
   - Both servers will start in separate terminal tabs

---

## Detailed Screenshots Guide

### Step 1: Open Edit Configurations
```
[Run] menu → [Edit Configurations...]
           ↓
Window opens showing current configurations
```

### Step 2: Add New Configuration
```
Click the [+] button in top left
           ↓
Dropdown menu appears with options:
  - npm ← SELECT THIS
  - Shell Script
  - Python
  - etc.
```

### Step 3: Fill npm Details
```
┌─────────────────────────────────────┐
│ npm Configuration                   │
├─────────────────────────────────────┤
│ Name:           Frontend Dev Server │
│ Package.json:   [Browse button]     │
│                 Select: ...frontend/│
│                 package.json        │
│ Command:        run                 │
│ Scripts:        dev                 │
│ Node interp:    [Auto]              │
│ Working dir:    [Browse button]     │
│                 Select: ...frontend/│
│ Run in Tminal:  [✓] Checked        │
├─────────────────────────────────────┤
│ [Apply] [OK] [Cancel]              │
└─────────────────────────────────────┘
```

---

## Quick Access Buttons

After setup, you can access your run configuration from:

1. **Top Right Dropdown:**
   ```
   [Frontend Dev Server ▼] [▶] [🐞]
    ↑ Click to select     ↑Run ↑Debug
   ```

2. **Keyboard Shortcuts:**
   - Run: `Shift + F10` (Windows)
   - Debug: `Shift + F9` (Windows)
   - Run with Config: `Alt + Shift + F10` (Windows)

3. **Menu:**
   - `Run` → `Run 'Frontend Dev Server'`
   - `Run` → `Debug 'Frontend Dev Server'`

---

## Troubleshooting

### npm Not Found in Configuration List
- Check if Node.js is installed: `node --version` in terminal
- Go to `File` → `Settings` → `Languages & Frameworks` → `Node.js`
- Click `Enable Node Package Manager (npm)`

### Script Running but No Output
- Make sure `Run in Terminal` is CHECKED
- Check that working directory is correct
- Verify npm packages are installed: `npm install` in frontend folder

### Port 3000 Already in Use
- Check: `netstat -ano | findstr :3000` (Windows)
- Kill process: `taskkill /PID <PID> /F`
- Or change port in `vite.config.js`

### Command Not Found Error
- Ensure you're in the right working directory
- Verify `package.json` exists in that directory
- Check that npm is installed: `npm --version`

---

## Recommended Configuration

### Best Practice Setup:

```
┌─ RUN CONFIGURATIONS ─────────────────────┐
│                                          │
│  1. Backend (Spring Boot)               │
│     Run: IlasApplication                │
│     Port: 8080                          │
│                                          │
│  2. Frontend Dev Server (npm)           │
│     Command: npm run dev                │
│     Port: 3000                          │
│                                          │
│  3. Full Stack (Compound)               │
│     Runs: Backend + Frontend together   │
│                                          │
└──────────────────────────────────────────┘
```

This gives you:
- ✅ Individual control over each server
- ✅ Ability to run both together
- ✅ Easy switching between configurations
- ✅ Clean IntelliJ integration

---

## Next Steps After Setup

1. **Select Configuration:**
   - Click the dropdown showing current config
   - Select `Frontend Dev Server`

2. **Run It:**
   - Click the green ▶ button
   - Or press `Shift + F10`

3. **View Output:**
   - Terminal appears showing Vite output
   - Watch for: `Local: http://localhost:3000`

4. **Open Application:**
   - Click the URL in terminal
   - Or manually go to: `http://localhost:3000`

---

## Alternative: Use Built-in Terminal

If you prefer not to use run configurations:

1. Open Terminal in IntelliJ: `Alt + F12`
2. Navigate: `cd frontend`
3. Run: `npm run dev`
4. Vite starts automatically on http://localhost:3000

---

## Running Backend + Frontend Side-by-Side

### Setup Both Servers:

**Terminal 1 (Backend):**
```bash
# Use Spring Boot run configuration or:
.\mvnw.cmd spring-boot:run
```

**Terminal 2 (Frontend):**
```bash
cd frontend
npm run dev
```

### Or use Compound Configuration:
- One click to start both
- Both run in separate terminal tabs
- Easy to monitor both

---

## Keyboard Shortcuts Summary

| Action | Shortcut |
|--------|----------|
| Open Configurations | `Alt + Shift + D` |
| Run Configuration | `Shift + F10` |
| Debug Configuration | `Shift + F9` |
| Select Config & Run | `Alt + Shift + F10` |
| Stop Running Process | `Ctrl + F2` |
| Rerun Last Config | `Ctrl + F5` |

---

## Video Steps (Text Version)

1. `Run` → `Edit Configurations...` *(Opens config window)*
2. Click `+` button → Select `npm` *(Adds npm config)*
3. Enter `Name`: `Frontend Dev Server`
4. Browse `Package.json` → Select `frontend\package.json`
5. Set `Command` to `run`
6. Set `Scripts` to `dev`
7. Browse `Working directory` → Select `frontend` folder
8. CHECK `Run in Terminal`
9. Click `Apply` then `OK`
10. Select `Frontend Dev Server` from dropdown
11. Click green `▶` Run button
12. Frontend starts at `http://localhost:3000`

---

## Final Checklist

Before running, verify:
- [ ] Node.js installed (`node --version`)
- [ ] npm installed (`npm --version`)
- [ ] `package.json` exists in frontend folder
- [ ] `node_modules` exists (run `npm install` if not)
- [ ] Port 3000 is available
- [ ] Backend running (optional, but recommended)

✅ **Done! Your frontend is ready to run from IntelliJ!** 🚀

---

**Quick Reference:**
- **Configuration Type:** npm
- **Command:** run
- **Script:** dev
- **Working Directory:** C:\Users\Admin\Documents\ILAS\frontend
- **Start URL:** http://localhost:3000


