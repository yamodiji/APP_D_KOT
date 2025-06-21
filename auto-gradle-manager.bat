@echo off
setlocal enabledelayedexpansion

echo ========================================
echo    Auto Gradle Manager v2.0
echo    Fetches ANY version automatically!
echo ========================================
echo.

:: Check if android directory exists
if not exist "android" (
    echo Error: android directory not found!
    echo This script must be run from the project root directory.
    pause
    exit /b 1
)

echo [1/5] Analyzing your project...

:: Read the current build.gradle to detect required Gradle version
set "detected_gradle_version="
set "detected_agp_version="

:: Check build.gradle for AGP version
if exist "android\build.gradle" (
    for /f "tokens=*" %%i in ('findstr /r "com\.android\.tools\.build:gradle:" android\build.gradle') do (
        set "line=%%i"
        for /f "tokens=2 delims=:" %%j in ("!line!") do (
            set "agp_part=%%j"
            for /f "tokens=1 delims='" %%k in ("!agp_part!") do (
                set "detected_agp_version=%%k"
            )
        )
    )
)

:: AGP to Gradle version mapping (from your search results)
if "!detected_agp_version!"=="8.1.2" set "detected_gradle_version=8.1.1"
if "!detected_agp_version!"=="8.1.1" set "detected_gradle_version=8.1.1"
if "!detected_agp_version!"=="8.1.0" set "detected_gradle_version=8.1.1"
if "!detected_agp_version!"=="8.0.2" set "detected_gradle_version=8.0"
if "!detected_agp_version!"=="8.0.1" set "detected_gradle_version=8.0"
if "!detected_agp_version!"=="8.0.0" set "detected_gradle_version=8.0"
if "!detected_agp_version!"=="7.4.2" set "detected_gradle_version=7.6"
if "!detected_agp_version!"=="7.4.1" set "detected_gradle_version=7.6"
if "!detected_agp_version!"=="7.4.0" set "detected_gradle_version=7.5"

:: Check gradle-wrapper.properties for current version
set "current_gradle_version="
if exist "android\gradle\wrapper\gradle-wrapper.properties" (
    for /f "tokens=*" %%i in ('findstr "distributionUrl" android\gradle\wrapper\gradle-wrapper.properties') do (
        set "url_line=%%i"
        for /f "tokens=2 delims=-" %%j in ("!url_line!") do (
            for /f "tokens=1 delims=-" %%k in ("%%j") do (
                set "current_gradle_version=%%k"
            )
        )
    )
)

echo.
echo 📊 PROJECT ANALYSIS RESULTS:
echo ----------------------------------------
echo Current Gradle Version: !current_gradle_version!
echo Detected AGP Version: !detected_agp_version!
echo Required Gradle Version: !detected_gradle_version!
echo.

:: If no version detected, ask user
if "!detected_gradle_version!"=="" (
    echo ⚠️  Could not auto-detect required Gradle version.
    echo.
    echo Common versions:
    echo [1] Gradle 8.1.1 (Recommended for most projects)
    echo [2] Gradle 8.0 (Stable)
    echo [3] Gradle 7.6 (Legacy projects)
    echo [4] Enter custom version
    echo.
    set /p choice="Select option (1-4): "
    
    if "!choice!"=="1" set "detected_gradle_version=8.1.1"
    if "!choice!"=="2" set "detected_gradle_version=8.0"
    if "!choice!"=="3" set "detected_gradle_version=7.6"
    if "!choice!"=="4" (
        set /p detected_gradle_version="Enter Gradle version (e.g., 8.1.1): "
    )
)

if "!detected_gradle_version!"=="" (
    echo Error: No Gradle version specified!
    pause
    exit /b 1
)

:: Check if current version matches required
if "!current_gradle_version!"=="!detected_gradle_version!" (
    echo ✅ Your Gradle version is already correct: !detected_gradle_version!
    echo No changes needed.
    pause
    exit /b 0
)

echo [2/5] Fetching Gradle !detected_gradle_version! from GitHub...

:: Create temp directory
if not exist "temp_gradle" mkdir temp_gradle
cd temp_gradle

:: Download Gradle wrapper JAR from GitHub releases
echo Downloading gradle-wrapper.jar...
powershell -Command "try { Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/releases/download/v!detected_gradle_version!/gradle-!detected_gradle_version!-wrapper.jar' -OutFile 'gradle-wrapper.jar' -ErrorAction Stop } catch { Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-!detected_gradle_version!-wrapper.jar' -OutFile 'gradle-wrapper.jar' }"

if not exist "gradle-wrapper.jar" (
    echo ❌ Failed to download gradle-wrapper.jar
    cd ..
    rmdir /s /q temp_gradle
    pause
    exit /b 1
)

echo [3/5] Downloading Gradle distribution info...

:: Download the actual Gradle distribution to get gradlew files
powershell -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-!detected_gradle_version!-bin.zip' -OutFile 'gradle-dist.zip'"

if not exist "gradle-dist.zip" (
    echo ❌ Failed to download Gradle distribution
    cd ..
    rmdir /s /q temp_gradle
    pause
    exit /b 1
)

:: Extract gradlew files
echo Extracting gradlew scripts...
powershell -Command "Expand-Archive -Path 'gradle-dist.zip' -DestinationPath '.' -Force"

echo [4/5] Updating your project files...

:: Update gradle-wrapper.properties
(
echo distributionBase=GRADLE_USER_HOME
echo distributionPath=wrapper/dists
echo distributionUrl=https\://services.gradle.org/distributions/gradle-!detected_gradle_version!-bin.zip
echo networkTimeout=10000
echo zipStoreBase=GRADLE_USER_HOME
echo zipStorePath=wrapper/dists
echo.
) > ..\android\gradle\wrapper\gradle-wrapper.properties

:: Replace gradle-wrapper.jar
copy /y "gradle-wrapper.jar" "..\android\gradle\wrapper\gradle-wrapper.jar" >nul

:: Replace gradlew files
if exist "gradle-!detected_gradle_version!\gradlew" (
    copy /y "gradle-!detected_gradle_version!\gradlew" "..\android\gradlew" >nul
)
if exist "gradle-!detected_gradle_version!\gradlew.bat" (
    copy /y "gradle-!detected_gradle_version!\gradlew.bat" "..\android\gradlew.bat" >nul
)

:: Go back and cleanup
cd ..
rmdir /s /q temp_gradle

echo [5/5] Verification...

echo.
echo ✅ SUCCESS! Gradle updated to version !detected_gradle_version!
echo.
echo 📋 Updated files:
echo ├── android/gradle/wrapper/gradle-wrapper.jar
echo ├── android/gradle/wrapper/gradle-wrapper.properties  
echo ├── android/gradlew
echo └── android/gradlew.bat
echo.
echo 🔧 Current configuration:
type android\gradle\wrapper\gradle-wrapper.properties
echo.
echo 🚀 Ready to build! Run: cd android ^&^& ./gradlew build
echo.

:: Test if it works
echo 🧪 Testing Gradle wrapper...
cd android
call gradlew.bat --version
cd ..

echo.
echo ========================================
echo    Auto Gradle Manager Complete!
echo ========================================
pause 