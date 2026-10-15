# PowerShell Launcher for Day 9: Generics & I/O
Write-Host "========================================================================" -ForegroundColor Cyan
Write-Host "  Campus Lost and Found - Day 9: Generics & try-with-resources" -ForegroundColor Cyan
Write-Host "========================================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Force -Path "bin" | Out-Null
}

javac -encoding UTF-8 -d "bin" "CampusGenericsApp.java"
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[SUCCESS] Running CampusGenericsApp..." -ForegroundColor Green
java -cp "bin" com.campus.lostandfound.CampusGenericsApp $args
