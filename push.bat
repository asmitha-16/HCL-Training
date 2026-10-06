@echo off
echo Syncing and pushing HCL Training to GitHub (asmitha-16)...
git add .
git status --porcelain > nul 2>&1
git commit -m "feat(daily-update): push latest tasks and project updates"
git branch -M main
git push -u origin main
pause
