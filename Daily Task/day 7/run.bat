@echo off
REM ============================================================================
REM Launcher for Campus Lost and Found - Day 7 Collections Framework
REM ============================================================================
echo [1/2] Compiling Java source files...
if not exist "bin" mkdir "bin"

dir /s /B src\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
del sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    exit /b %ERRORLEVEL%
)

echo [2/2] Launching Campus Console Application...
java -cp bin com.campus.lostandfound.app.CampusConsoleApp %*
