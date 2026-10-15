@echo off
REM ============================================================================
REM Launcher for Day 9: Generics & I/O
REM ============================================================================
if not exist "bin" mkdir "bin"

javac -encoding UTF-8 -d bin CampusGenericsApp.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    exit /b %ERRORLEVEL%
)

java -cp bin com.campus.lostandfound.CampusGenericsApp %*
