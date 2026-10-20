# Day 1 PowerShell Launcher: Java Platform Basics - Campus Lost and Found
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "Compiling and Running Day 1: PlatformInfo.java" -ForegroundColor Yellow
Write-Host "Project: JFS-40 Campus Lost and Found Portal" -ForegroundColor Green
Write-Host "===============================================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

Write-Host "Compiling PlatformInfo.java..." -ForegroundColor Gray
javac -d bin PlatformInfo.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[SUCCESS] Compilation successful. Running PlatformInfo..." -ForegroundColor Green
Write-Host ""
java -cp bin PlatformInfo
Write-Host ""
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "Execution completed successfully!" -ForegroundColor Green
Write-Host "===============================================================================" -ForegroundColor Cyan
