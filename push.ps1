# PowerShell push script for HCL Training
Write-Host "Syncing and pushing HCL Training to GitHub (asmitha-16)..." -ForegroundColor Cyan
git add .
$status = git status --porcelain
if ($status) {
    $date = Get-Date -Format "yyyy-MM-dd HH:mm"
    git commit -m "feat(daily-update): push latest tasks and project updates ($date)"
}
git branch -M main
git push -u origin main
if ($LASTEXITCODE -eq 0) {
    Write-Host "`nSUCCESS: Successfully pushed to https://github.com/asmitha-16/HCL-Training" -ForegroundColor Green
} else {
    Write-Host "`nNOTICE: If remote repository not found, create it first at: https://github.com/new?name=HCL-Training" -ForegroundColor Yellow
}
