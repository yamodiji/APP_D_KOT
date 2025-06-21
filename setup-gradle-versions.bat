@echo off
echo ========================================
echo    Gradle Versions Setup Script
echo ========================================
echo.
echo This script will download and cache multiple Gradle versions
echo for different Android projects to avoid repeated downloads.
echo.

:: Create gradle cache directory
if not exist "gradle-cache" mkdir gradle-cache
cd gradle-cache

echo Downloading commonly used Gradle versions...
echo.

:: Gradle 8.3 (Latest stable)
echo [1/6] Downloading Gradle 8.3...
if not exist "gradle-8.3" (
    powershell -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.3-bin.zip' -OutFile 'gradle-8.3-bin.zip'"
    powershell -Command "Expand-Archive -Path 'gradle-8.3-bin.zip' -DestinationPath '.'"
    ren gradle-8.3 gradle-8.3-extracted
    del gradle-8.3-bin.zip
) else (
    echo Gradle 8.3 already exists, skipping...
)

:: Gradle 8.1.1 (Current project)
echo [2/6] Downloading Gradle 8.1.1...
if not exist "gradle-8.1.1" (
    powershell -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.1.1-bin.zip' -OutFile 'gradle-8.1.1-bin.zip'"
    powershell -Command "Expand-Archive -Path 'gradle-8.1.1-bin.zip' -DestinationPath '.'"
    ren gradle-8.1.1 gradle-8.1.1-extracted
    del gradle-8.1.1-bin.zip
) else (
    echo Gradle 8.1.1 already exists, skipping...
)

:: Gradle 8.0
echo [3/6] Downloading Gradle 8.0...
if not exist "gradle-8.0" (
    powershell -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.0-bin.zip' -OutFile 'gradle-8.0-bin.zip'"
    powershell -Command "Expand-Archive -Path 'gradle-8.0-bin.zip' -DestinationPath '.'"
    ren gradle-8.0 gradle-8.0-extracted
    del gradle-8.0-bin.zip
) else (
    echo Gradle 8.0 already exists, skipping...
)

:: Gradle 7.6 (For older projects)
echo [4/6] Downloading Gradle 7.6...
if not exist "gradle-7.6" (
    powershell -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-7.6-bin.zip' -OutFile 'gradle-7.6-bin.zip'"
    powershell -Command "Expand-Archive -Path 'gradle-7.6-bin.zip' -DestinationPath '.'"
    ren gradle-7.6 gradle-7.6-extracted
    del gradle-7.6-bin.zip
) else (
    echo Gradle 7.6 already exists, skipping...
)

:: Gradle 7.4 (For legacy projects)
echo [5/6] Downloading Gradle 7.4...
if not exist "gradle-7.4" (
    powershell -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-7.4-bin.zip' -OutFile 'gradle-7.4-bin.zip'"
    powershell -Command "Expand-Archive -Path 'gradle-7.4-bin.zip' -DestinationPath '.'"
    ren gradle-7.4 gradle-7.4-extracted
    del gradle-7.4-bin.zip
) else (
    echo Gradle 7.4 already exists, skipping...
)

:: Gradle 6.9 (For very old projects)
echo [6/6] Downloading Gradle 6.9...
if not exist "gradle-6.9" (
    powershell -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-6.9-bin.zip' -OutFile 'gradle-6.9-bin.zip'"
    powershell -Command "Expand-Archive -Path 'gradle-6.9-bin.zip' -DestinationPath '.'"
    ren gradle-6.9 gradle-6.9-extracted
    del gradle-6.9-bin.zip
) else (
    echo Gradle 6.9 already exists, skipping...
)

cd ..

echo.
echo ========================================
echo    Gradle Versions Setup Complete!
echo ========================================
echo.
echo Available Gradle versions in gradle-cache/:
dir gradle-cache /B
echo.
echo To use a specific version, update your gradle-wrapper.properties:
echo distributionUrl=file:///path/to/gradle-cache/gradle-X.X.X-bin.zip
echo.
echo Or use the gradle-switcher.bat script to switch versions easily.
echo.
pause 