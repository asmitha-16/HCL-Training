@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
echo Building and running Campus Lost and Found Platform...
if not exist bin mkdir bin
dir /s /b src\main\java\*.java > sources.txt
"%JAVA_HOME%\bin\javac.exe" -d bin @sources.txt
del sources.txt
"%JAVA_HOME%\bin\java.exe" -cp bin com.campus.lostandfound.LostAndFoundApplication 8080
pause
