# TradeSphere Pro — Installation & Setup Guide

**Developer:** MOHAMMAD SAKIB AHMAD  
**Version:** 1.0 | **Year:** 2026

---

## System Requirements

| Component | Minimum | Recommended |
|-----------|---------|-------------|
| OS | Windows 10 | Windows 11 |
| Java JDK | 17+ | 21 (Temurin) |
| Maven | 3.8+ | 3.9.9 |
| RAM | 2 GB | 4 GB |
| Storage | 200 MB | 500 MB |
| Display | 1280×720 | 1920×1080 |

---

## Step 1 — Install Java JDK 21

1. Download **Eclipse Temurin JDK 21** from:  
   https://adoptium.net/temurin/releases/?version=21

2. Run the installer and select **"Add to PATH"** and **"Set JAVA_HOME"**.

3. Verify installation:
   ```cmd
   java -version
   ```
   Expected output: `openjdk version "21.x.x"`

---

## Step 2 — Install Apache Maven (Optional)

> **Skip this step** — the `run.bat` script auto-downloads Maven if not found.

Manual install:
1. Download from https://maven.apache.org/download.cgi
2. Extract to `C:\maven`
3. Add `C:\maven\bin` to your system `PATH`
4. Verify: `mvn -version`

---

## Step 3 — Get the Project

```cmd
cd "C:\Users\YourName\Desktop\CODE ALPHA\TASK02"
```

Or unzip the project archive to any folder.

---

## Step 4 — Run the Application

### Option A — Double-click (Easiest)
Double-click **`run.bat`** in the project root.

### Option B — PowerShell / Command Prompt
```powershell
# Navigate to project folder
cd "TradeSphereP ro"

# Set Java 21
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot"
$mavenDir = "$env:TEMP\apache-maven-3.9.9"
$env:PATH = "$env:JAVA_HOME\bin;$mavenDir\bin;$env:PATH"

# Launch
& "$mavenDir\bin\mvn.cmd" javafx:run
```

### Option C — IntelliJ IDEA
1. Open IntelliJ IDEA → **File → Open** → select project folder
2. Wait for Maven sync
3. Run `App.java` main method directly

### Option D — Eclipse IDE
1. File → Import → Existing Maven Projects
2. Select project root folder
3. Right-click `App.java` → Run As → Java Application

---

## Step 5 — First Login

On first launch:
- A **demo account** is auto-created:
  - Username: `demo`
  - Password: `demo123`
- Or click **Register** to create your own account

---

## Build Fat JAR (Portable)

```cmd
& "$mavenDir\bin\mvn.cmd" clean package -DskipTests
```

Output: `target\tradesphere-pro-1.0.0.jar`

Run anywhere with Java 21:
```cmd
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar tradesphere-pro-1.0.0.jar
```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `java: command not found` | Reinstall JDK 21 and add to PATH |
| Black screen on startup | Ensure JavaFX 21 compatible JDK |
| Database locked error | Close other instances of the app |
| Font rendering issues | Install Segoe UI font (default on Windows) |
| `OutOfMemoryError` | Add `-Xmx512m` to JVM args in `pom.xml` |

---

## Data Location

All persistent data is stored in the project folder:

| File | Purpose |
|------|---------|
| `tradesphere.db` | SQLite database (users, trades, alerts) |
| `reports/` | Generated CSV, TXT, PDF reports |
| `data/` | JSON backup files |

---

## Uninstall

Simply delete the project folder. No registry entries or system files are modified.

---

*© 2026 TradeSphere Pro — Educational Use Only*
