$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$python311 = "D:\Python3.11\python.exe"

if (Test-Path $python311) {
    $python = $python311
} else {
    $pythonCommand = Get-Command python -ErrorAction SilentlyContinue
    if (-not $pythonCommand) {
        Write-Host "Python was not found. Install Python 3.11 or add python to PATH."
        exit 1
    }
    $python = $pythonCommand.Source
}

Push-Location $root
try {
    & $python -m PyInstaller `
        --noconfirm `
        --clean `
        --onefile `
        --windowed `
        --name HuiSuQuickNote `
        huisu_sticky_note.py
} finally {
    Pop-Location
}

Write-Host "Generated: $root\dist\HuiSuQuickNote.exe"
