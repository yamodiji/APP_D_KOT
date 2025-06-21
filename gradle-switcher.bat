@echo off
setlocal enabledelayedexpansion

echo ========================================
echo       Gradle Version Switcher
echo ========================================
echo.

:: Check if gradle-cache exists
if not exist "gradle-cache" (
    echo Error: gradle-cache directory not found!
    echo Please run setup-gradle-versions.bat first.
    echo.
    pause
    exit /b 1
)

:: Check if android directory exists
if not exist "android" (
    echo Error: android directory not found!
    echo This script must be run from the project root directory.
    echo.
    pause
    exit /b 1
)

echo Available Gradle versions:
echo.
echo [1] Gradle 8.3 (Latest stable)
echo [2] Gradle 8.1.1 (Current project default)
echo [3] Gradle 8.0
echo [4] Gradle 7.6 (For older projects)
echo [5] Gradle 7.4 (For legacy projects)
echo [6] Gradle 6.9 (For very old projects)
echo [7] Custom URL (enter manually)
echo [0] Exit
echo.

set /p choice="Select Gradle version (0-7): "

if "%choice%"=="0" exit /b 0
if "%choice%"=="1" set gradle_version=8.3
if "%choice%"=="2" set gradle_version=8.1.1
if "%choice%"=="3" set gradle_version=8.0
if "%choice%"=="4" set gradle_version=7.6
if "%choice%"=="5" set gradle_version=7.4
if "%choice%"=="6" set gradle_version=6.9
if "%choice%"=="7" goto custom_url

if "%gradle_version%"=="" (
    echo Invalid choice!
    pause
    exit /b 1
)

:: Check if the selected version exists
if not exist "gradle-cache\gradle-%gradle_version%-extracted" (
    echo Error: Gradle %gradle_version% not found in cache!
    echo Please run setup-gradle-versions.bat to download it.
    echo.
    pause
    exit /b 1
)

:: Update gradle-wrapper.properties
set "current_dir=%CD%"
set "gradle_url=file:///%current_dir:\=/%/gradle-cache/gradle-%gradle_version%-bin.zip"

echo.
echo Updating gradle-wrapper.properties...
echo distributionUrl=%gradle_url%

:: Create temp file with new content
(
echo distributionBase=GRADLE_USER_HOME
echo distributionPath=wrapper/dists
echo distributionUrl=%gradle_url%
echo networkTimeout=10000
echo zipStoreBase=GRADLE_USER_HOME
echo zipStorePath=wrapper/dists
echo.
) > android\gradle\wrapper\gradle-wrapper.properties.tmp

:: Replace original file
move android\gradle\wrapper\gradle-wrapper.properties.tmp android\gradle\wrapper\gradle-wrapper.properties

echo.
echo ✅ Successfully switched to Gradle %gradle_version%!
echo.
goto end

:custom_url
echo.
set /p custom_gradle_url="Enter Gradle distribution URL: "
if "%custom_gradle_url%"=="" (
    echo No URL provided!
    pause
    exit /b 1
)

:: Create temp file with custom URL
(
echo distributionBase=GRADLE_USER_HOME
echo distributionPath=wrapper/dists
echo distributionUrl=%custom_gradle_url%
echo networkTimeout=10000
echo zipStoreBase=GRADLE_USER_HOME
echo zipStorePath=wrapper/dists
echo.
) > android\gradle\wrapper\gradle-wrapper.properties.tmp

:: Replace original file
move android\gradle\wrapper\gradle-wrapper.properties.tmp android\gradle\wrapper\gradle-wrapper.properties

echo.
echo ✅ Successfully updated Gradle URL to: %custom_gradle_url%
echo.

:end
echo Current gradle-wrapper.properties:
echo ----------------------------------------
type android\gradle\wrapper\gradle-wrapper.properties
echo ----------------------------------------
echo.
echo You can now run: cd android && ./gradlew build
echo.
pause 