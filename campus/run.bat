@echo off
setlocal
echo ===================================================
echo   Compiling Campus Lost and Found Platform...
echo ===================================================
if not exist "campus-app\target\classes" mkdir "campus-app\target\classes"

dir /s /b campus-app\src\main\java\*.java > sources.txt
javac -encoding UTF-8 -d campus-app\target\classes @sources.txt
del sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    exit /b %ERRORLEVEL%
)

echo Starting Console Application...
java -cp campus-app\target\classes com.campus.lostandfound.app.ConsoleApp
