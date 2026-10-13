# PowerShell Launcher for Campus Lost and Found Console Application
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "  Compiling Campus Lost and Found Platform..." -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

if (-not (Test-Path "campus-app\target\classes")) {
    New-Item -ItemType Directory -Force -Path "campus-app\target\classes" | Out-Null
}

$sources = Get-ChildItem -Path "campus-app\src\main\java" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d "campus-app\target\classes" $sources

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Java compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "Compilation successful! Launching interactive Console App..." -ForegroundColor Green
java -cp "campus-app\target\classes" com.campus.lostandfound.app.ConsoleApp
