# Navigate to project directory
Set-Location $PSScriptRoot

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "DoKusochka Unit Tests" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check Java version
Write-Host "Checking Java version..." -ForegroundColor Yellow
java -version 2>&1 | Select-Object -First 1
Write-Host ""

# Check if gradlew.bat exists
if (Test-Path ".\gradlew.bat") {
    $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_ -replace '.*"(\d+)\..*', '$1' }
    
    if ([int]$javaVersion -lt 11) {
        Write-Host "========================================" -ForegroundColor Red
        Write-Host "ERROR: Java 11+ required!" -ForegroundColor Red
        Write-Host "Current version: Java $javaVersion" -ForegroundColor Red
        Write-Host "========================================" -ForegroundColor Red
        Write-Host ""
        Write-Host "Please use one of these methods:" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "METHOD 1: Android Studio (Recommended)" -ForegroundColor Green
        Write-Host "  1. Open project in Android Studio"
        Write-Host "  2. Navigate to: app/src/test/java/com/example/dokusochka/"
        Write-Host "  3. Right-click 'dokusochka' folder"
        Write-Host "  4. Select 'Run Tests in dokusochka'"
        Write-Host ""
        Write-Host "METHOD 2: Upgrade Java" -ForegroundColor Green
        Write-Host "  1. Download JDK 11+ from: https://adoptium.net/"
        Write-Host "  2. Install and set JAVA_HOME"
        Write-Host "  3. Run this script again"
        Write-Host ""
    } else {
        Write-Host "========================================" -ForegroundColor Green
        Write-Host "Running Unit Tests..." -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
        Write-Host ""
        
        .\gradlew.bat test --console=plain
        
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host "Test Results" -ForegroundColor Cyan
        Write-Host "========================================" -ForegroundColor Cyan
        
        $reportPath = "app\build\reports\tests\testDebugUnitTest\index.html"
        if (Test-Path $reportPath) {
            Write-Host "Opening test report..." -ForegroundColor Green
            Start-Process $reportPath
        } else {
            Write-Host "Report location: $reportPath" -ForegroundColor Yellow
        }
    }
} else {
    Write-Host "ERROR: gradlew.bat not found!" -ForegroundColor Red
    Write-Host "Please run from project root directory." -ForegroundColor Red
}

Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
