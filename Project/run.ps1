# Campus Lost & Found Platform Launcher
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " Building & Starting Campus Lost & Found Platform..." -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

$sources = Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
New-Item -ItemType Directory -Force -Path "bin" | Out-Null
& "$env:JAVA_HOME\bin\javac.exe" -d "bin" $sources

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit 1
}

Write-Host "Starting Server on http://localhost:8080 ..." -ForegroundColor Green
& "$env:JAVA_HOME\bin\java.exe" -cp "bin" com.campus.lostandfound.LostAndFoundApplication 8080
