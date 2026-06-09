@echo off
REM ============================================================
REM  TradeSphere Pro - One-Click Launcher
REM  Developer: MOHAMMAD SAKIB AHMAD
REM ============================================================
title TradeSphere Pro Launcher

echo.
echo  ========================================
echo    TradeSphere Pro - Virtual Trading
echo    "Learn, Trade, Analyze."
echo  ========================================
echo.

REM Set JDK 21
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

REM Try to find Maven in TEMP or PATH
set MAVEN_DIR=%TEMP%\apache-maven-3.9.9

if not exist "%MAVEN_DIR%\bin\mvn.cmd" (
    echo [INFO] Maven not found. Downloading...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip' -OutFile '%TEMP%\maven.zip' -UseBasicParsing; Expand-Archive '%TEMP%\maven.zip' -DestinationPath '%TEMP%' -Force"
)

echo [INFO] Starting TradeSphere Pro...
echo [INFO] Demo account: username=demo  password=demo123
echo.

"%MAVEN_DIR%\bin\mvn.cmd" javafx:run

pause
