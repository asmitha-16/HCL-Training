# PowerShell Launcher for Day 8: Collections Framework Part 2
Write-Host "========================================================================" -ForegroundColor Cyan
Write-Host "  Campus Lost and Found - Day 8: Maps, Comparators & Analytics" -ForegroundColor Cyan
Write-Host "========================================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Force -Path "bin" | Out-Null
}

javac -encoding UTF-8 -d "bin" "CampusDirectory.java"
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[SUCCESS] Running CampusDirectory..." -ForegroundColor Green
java -cp "bin" com.campus.lostandfound.CampusDirectory $args
