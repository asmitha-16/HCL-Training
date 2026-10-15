@echo off
REM ============================================================================
REM Launcher for Day 8: Collections Framework Part 2 (Maps & Comparators)
REM ============================================================================
if not exist "bin" mkdir "bin"

javac -encoding UTF-8 -d bin CampusDirectory.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    exit /b %ERRORLEVEL%
)

java -cp bin com.campus.lostandfound.CampusDirectory %*
