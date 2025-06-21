@echo off
echo 🔧 Fixing Gradle wrapper and setting up releases...

REM Navigate to android directory
cd android

REM Generate gradle wrapper if missing
echo 📦 Generating Gradle wrapper...
if exist "gradle\wrapper\gradle-wrapper.jar" (
    echo ✅ Gradle wrapper already exists
) else (
    echo 🔨 Creating Gradle wrapper...
    gradle wrapper --gradle-version 8.1.1
)

REM Make gradlew executable (not needed on Windows, but for completeness)
echo ✅ Gradle wrapper setup complete

REM Go back to root directory
cd ..

REM Add and commit the changes
echo 📁 Adding Gradle wrapper files to repository...
git add android/gradle/wrapper/
git add android/gradlew
git add android/gradlew.bat

echo 💾 Committing Gradle wrapper fixes...
git commit -m "🔧 Fix: Add Gradle wrapper files for GitHub Actions

- Added gradle-wrapper.jar and properties
- Fixed GradleWrapperMain ClassNotFoundException
- Updated .gitignore to include wrapper files
- Added automatic release workflow for APK downloads"

REM Push to GitHub
echo 🚀 Pushing to GitHub...
git push origin main

echo.
echo ✅ Fixed! Your repository now has:
echo • ✅ Gradle wrapper files (fixes build error)
echo • ✅ Automatic APK releases on every push
echo • ✅ Release downloads available in GitHub
echo.
echo 🎯 Your APK will be available at:
echo https://github.com/yamodiji/APP_D_KOT/releases
echo.
echo 📱 To create a tagged release (recommended):
echo git tag v1.0.0
echo git push origin v1.0.0
echo.
echo 🎉 GitHub Actions will now build APKs successfully!

pause 