@echo off
REM Day 1 Launcher: Java Platform Basics - Campus Lost and Found
echo ===============================================================================
echo Compiling and Running Day 1: PlatformInfo.java
echo Project: JFS-40 Campus Lost and Found Portal
echo ===============================================================================

if not exist bin mkdir bin

javac -d bin PlatformInfo.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    exit /b %ERRORLEVEL%
)

echo [SUCCESS] Compilation complete. Running PlatformInfo...
echo.
java -cp bin PlatformInfo
echo.
echo ===============================================================================
echo Done! Output logged.
echo ===============================================================================
pause
