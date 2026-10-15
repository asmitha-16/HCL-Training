@echo off
REM ============================================================================
REM Launcher for ArrayList vs LinkedList Performance Benchmark
REM ============================================================================
echo Compiling benchmark harness...
if not exist "bin" mkdir "bin"

dir /s /B src\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
del sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    exit /b %ERRORLEVEL%
)

echo Executing Benchmark Suite...
java -cp bin com.campus.lostandfound.benchmark.CollectionBenchmark
