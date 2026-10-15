# PowerShell Launcher for ArrayList vs LinkedList Benchmark
Write-Host "========================================================================" -ForegroundColor Cyan
Write-Host "  Campus Lost and Found - Collections Benchmark Harness" -ForegroundColor Cyan
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

Write-Host "Executing Collections Benchmark (ArrayList vs LinkedList)..." -ForegroundColor Green
java -cp "bin" com.campus.lostandfound.benchmark.CollectionBenchmark
