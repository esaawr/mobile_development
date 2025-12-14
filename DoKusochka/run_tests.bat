@echo off
cd /d "%~dp0"

echo ========================================
echo Running DoKusochka Unit Tests
echo ========================================
echo.

REM Check Java version
echo Checking Java version...
java -version
echo.

echo ========================================
echo Note: This project requires Java 11+
echo Current Java version is 8
echo ========================================
echo.
echo Please use Android Studio to run tests:
echo 1. Open project in Android Studio
echo 2. Navigate to app/src/test/java/com/example/dokusochka/
echo 3. Right-click on 'dokusochka' folder
echo 4. Select 'Run Tests in dokusochka'
echo.
echo Alternatively, install JDK 11+ and run:
echo gradlew.bat test
echo.
pause
