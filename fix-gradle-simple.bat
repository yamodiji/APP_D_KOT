@echo off
echo ========================================
echo     Simple Gradle Fix Script
echo ========================================
echo.

:: Check if android directory exists
if not exist "android" (
    echo Error: android directory not found!
    echo This script must be run from the project root directory.
    pause
    exit /b 1
)

echo What's the issue you're experiencing?
echo.
echo [1] GradleWrapperMain ClassNotFoundException
echo [2] Unsupported Gradle version
echo [3] Build cache corruption
echo [4] Network/download issues
echo [5] Change Gradle version (enter manually)
echo [6] Reset to default working version
echo [0] Exit
echo.

set /p choice="Select fix (0-6): "

if "%choice%"=="0" exit /b 0
if "%choice%"=="1" goto fix_wrapper
if "%choice%"=="2" goto version_mismatch
if "%choice%"=="3" goto clear_cache
if "%choice%"=="4" goto network_fix
if "%choice%"=="5" goto manual_version
if "%choice%"=="6" goto reset_default

echo Invalid choice!
pause
exit /b 1

:fix_wrapper
echo.
echo Fixing missing gradle-wrapper.jar...
cd android
gradle wrapper --gradle-version 8.1.1
echo ✅ Gradle wrapper fixed!
goto end

:version_mismatch
echo.
echo Common working combinations:
echo - Gradle 8.1.1 + AGP 8.1.2 (recommended)
echo - Gradle 8.0 + AGP 8.0.x
echo - Gradle 7.6 + AGP 7.4.x
echo.
echo Current gradle-wrapper.properties:
type android\gradle\wrapper\gradle-wrapper.properties
echo.
echo Do you want to use the recommended version? (y/n)
set /p confirm=
if /i "%confirm%"=="y" goto reset_default
goto end

:clear_cache
echo.
echo Clearing Gradle cache...
cd android
if exist ".gradle" rmdir /s /q .gradle
echo ✅ Gradle cache cleared!
echo Now run: cd android && ./gradlew clean build
goto end

:network_fix
echo.
echo Network troubleshooting:
echo 1. Check internet connection
echo 2. Try different network (mobile hotspot)
echo 3. Check if corporate firewall blocks gradle.org
echo 4. Use VPN if needed
echo.
echo Testing connection to Gradle servers...
ping services.gradle.org
goto end

:manual_version
echo.
echo Enter Gradle version (e.g., 8.1.1, 8.0, 7.6):
set /p gradle_ver=
if "%gradle_ver%"=="" (
    echo No version entered!
    goto end
)

echo Updating to Gradle %gradle_ver%...
(
echo distributionBase=GRADLE_USER_HOME
echo distributionPath=wrapper/dists
echo distributionUrl=https\://services.gradle.org/distributions/gradle-%gradle_ver%-bin.zip
echo networkTimeout=10000
echo zipStoreBase=GRADLE_USER_HOME
echo zipStorePath=wrapper/dists
echo.
) > android\gradle\wrapper\gradle-wrapper.properties

echo ✅ Updated to Gradle %gradle_ver%!
goto end

:reset_default
echo.
echo Resetting to known working configuration...
(
echo distributionBase=GRADLE_USER_HOME
echo distributionPath=wrapper/dists
echo distributionUrl=https\://services.gradle.org/distributions/gradle-8.1.1-bin.zip
echo networkTimeout=10000
echo zipStoreBase=GRADLE_USER_HOME
echo zipStorePath=wrapper/dists
echo.
) > android\gradle\wrapper\gradle-wrapper.properties

echo ✅ Reset to Gradle 8.1.1 (recommended)!

:end
echo.
echo ----------------------------------------
echo Current configuration:
type android\gradle\wrapper\gradle-wrapper.properties
echo ----------------------------------------
echo.
echo Next steps:
echo 1. cd android
echo 2. ./gradlew clean build
echo.
pause 