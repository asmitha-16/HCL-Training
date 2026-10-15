# PowerShell Launcher for Campus Lost and Found - Day 7 Collections Framework
Write-Host "========================================================================" -ForegroundColor Cyan
Write-Host "  Campus Lost and Found Portal - Day 7: Collections Framework Launcher" -ForegroundColor Cyan
Write-Host "========================================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Force -Path "bin" | Out-Null
}

$sources = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d "bin" $sources

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Java compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[SUCCESS] Compilation completed! Launching Console Application..." -ForegroundColor Green
java -cp "bin" com.campus.lostandfound.app.CampusConsoleApp $args
