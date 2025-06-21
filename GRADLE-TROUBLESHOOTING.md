# 🔧 Gradle Troubleshooting Guide

## 🎯 **Why Different Apps Need Different Gradle Versions**

Yes, you're absolutely right! Different Android projects require different Gradle versions due to:

1. **Android Gradle Plugin (AGP) compatibility**
2. **Kotlin version requirements**
3. **Java/JDK version dependencies**
4. **Third-party library compatibility**
5. **Legacy project constraints**

## 📊 **Gradle Compatibility Matrix**

| Android Gradle Plugin | Required Gradle | Java Version | Common Issues |
|----------------------|----------------|--------------|---------------|
| 8.2.x | 8.2+ | 17+ | Latest features, may break old code |
| 8.1.x | 8.0 - 8.3 | 17+ | **Current project** ✅ |
| 8.0.x | 8.0 - 8.3 | 17+ | Stable, good compatibility |
| 7.4.x | 7.5+ | 11+ | Older projects, Flutter compatibility |
| 7.3.x | 7.4+ | 11+ | Legacy support |
| 7.2.x | 7.3.3+ | 11+ | Common in older apps |
| 6.9.x | 6.8+ | 8+ | Very old projects |

## 🚀 **Quick Setup Instructions**

### Step 1: Download All Gradle Versions
```bash
# Run this once to cache all common Gradle versions
setup-gradle-versions.bat
```

### Step 2: Switch Versions for Different Projects
```bash
# Use this anytime you need to change Gradle version
gradle-switcher.bat
```

## 🔍 **Common Gradle Errors & Solutions**

### Error 1: "GradleWrapperMain ClassNotFoundException"
**Cause:** Missing `gradle-wrapper.jar`
**Solution:**
```bash
cd android
gradle wrapper --gradle-version 8.1.1
```

### Error 2: "Unsupported Gradle version"
**Cause:** AGP and Gradle version mismatch
**Solution:** Use the compatibility matrix above or run `gradle-switcher.bat`

### Error 3: "Could not resolve all files for configuration"
**Cause:** Network issues or wrong repository
**Solution:**
```gradle
// Add to build.gradle
repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}
```

### Error 4: "Java version incompatibility"
**Cause:** Wrong JDK version
**Solution:**
- Gradle 8.x requires Java 17+
- Gradle 7.x requires Java 11+
- Gradle 6.x requires Java 8+

### Error 5: "Build cache corruption"
**Solution:**
```bash
cd android
./gradlew clean
./gradlew build --refresh-dependencies
```

## 📁 **Local Gradle Cache Structure**

After running `setup-gradle-versions.bat`, you'll have:
```
gradle-cache/
├── gradle-8.3-extracted/
├── gradle-8.1.1-extracted/
├── gradle-8.0-extracted/
├── gradle-7.6-extracted/
├── gradle-7.4-extracted/
└── gradle-6.9-extracted/
```

## 🛠️ **Manual Gradle Version Change**

If you need to manually change Gradle version:

1. **Edit `android/gradle/wrapper/gradle-wrapper.properties`:**
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.1.1-bin.zip
```

2. **Or use local cache:**
```properties
distributionUrl=file:///F:/your-path/gradle-cache/gradle-8.1.1-bin.zip
```

## 🎯 **Project-Specific Recommendations**

### For Flutter-to-Android Conversions:
- Use Gradle 8.1.1 (current setup) ✅
- Android Gradle Plugin 8.1.2
- Kotlin 1.9.10

### For Legacy Android Projects:
- Use Gradle 7.6 or 7.4
- Check the project's original `build.gradle`

### For New Android Projects:
- Use latest Gradle 8.3+
- Latest Android Gradle Plugin
- Latest Kotlin version

## 🚨 **Emergency Fixes**

### Quick Reset to Working State:
```bash
# Delete gradle cache and start fresh
cd android
rmdir /s .gradle
./gradlew wrapper --gradle-version 8.1.1
./gradlew clean build
```

### Force Download Fresh Gradle:
```bash
cd android
./gradlew wrapper --gradle-version 8.1.1 --distribution-type all
```

## 📋 **Troubleshooting Checklist**

- [ ] Check AGP version in `build.gradle`
- [ ] Verify Gradle version in `gradle-wrapper.properties`
- [ ] Confirm Java/JDK version compatibility
- [ ] Clear Gradle cache if corrupted
- [ ] Check internet connection for downloads
- [ ] Verify repository URLs are accessible
- [ ] Run `gradle-switcher.bat` for easy switching

## 💡 **Pro Tips**

1. **Always keep multiple Gradle versions cached locally**
2. **Use `gradle-switcher.bat` instead of manual editing**
3. **Check project documentation for recommended versions**
4. **When in doubt, use the compatibility matrix above**
5. **Keep a backup of working `gradle-wrapper.properties`**

## 🔗 **Useful Commands**

```bash
# Check current Gradle version
cd android && ./gradlew --version

# Force refresh dependencies
cd android && ./gradlew build --refresh-dependencies

# Clean and rebuild
cd android && ./gradlew clean build

# Check project structure
cd android && ./gradlew projects

# Debug build issues
cd android && ./gradlew build --debug --stacktrace
```

---

**Remember:** The scripts I created (`setup-gradle-versions.bat` and `gradle-switcher.bat`) will handle most of these issues automatically! 🎉 