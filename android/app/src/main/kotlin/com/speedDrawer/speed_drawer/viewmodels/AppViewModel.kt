package com.speedDrawer.speed_drawer.viewmodels

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.speedDrawer.speed_drawer.models.AppInfo
import com.speedDrawer.speed_drawer.utils.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppViewModel(application: Application) : AndroidViewModel(application) {
    
    private val packageManager = application.packageManager
    private val preferences = AppPreferences(application)
    
    private val _apps = MutableLiveData<List<AppInfo>>()
    val apps: LiveData<List<AppInfo>> = _apps
    
    private val _mostUsedApps = MutableLiveData<List<AppInfo>>()
    val mostUsedApps: LiveData<List<AppInfo>> = _mostUsedApps
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private var allApps: List<AppInfo> = emptyList()
    private var currentQuery: String = ""
    
    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val installedApps = withContext(Dispatchers.IO) {
                    getInstalledApps()
                }
                
                allApps = installedApps
                loadUsageData()
                
                // Initial display - show all apps sorted by usage
                updateDisplayedApps("")
                updateMostUsedApps()
                
            } catch (e: Exception) {
                // Handle error
                _apps.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshApps() {
        if (!_isLoading.value!!) {
            loadApps()
        }
    }
    
    fun searchApps(query: String) {
        currentQuery = query
        updateDisplayedApps(query)
    }
    
    fun incrementLaunchCount(app: AppInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                app.launchCount++
                app.lastLaunchTime = System.currentTimeMillis()
                saveUsageData(app)
            }
            
            // Update the displayed list to reflect new usage stats
            updateMostUsedApps()
            if (currentQuery.isBlank()) {
                updateDisplayedApps("")
            }
        }
    }
    
    fun toggleFavorite(app: AppInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                app.isFavorite = !app.isFavorite
                saveFavoriteStatus(app)
            }
            
            // Update the displayed list
            updateDisplayedApps(currentQuery)
            updateMostUsedApps()
        }
    }
    
    private suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val apps = mutableListOf<AppInfo>()
        
        val installedPackages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        
        for (packageInfo in installedPackages) {
            try {
                // Skip system apps if they should be hidden
                if (shouldHideApp(packageInfo)) continue
                
                val appName = packageManager.getApplicationLabel(packageInfo).toString()
                val packageName = packageInfo.packageName
                
                // Get additional info
                val versionName = try {
                    packageManager.getPackageInfo(packageName, 0).versionName ?: "Unknown"
                } catch (e: Exception) {
                    "Unknown"
                }
                
                val versionCode = try {
                    packageManager.getPackageInfo(packageName, 0).longVersionCode
                } catch (e: Exception) {
                    0L
                }
                
                val isSystemApp = (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val installTime = try {
                    packageManager.getPackageInfo(packageName, 0).firstInstallTime
                } catch (e: Exception) {
                    0L
                }
                
                val updateTime = try {
                    packageManager.getPackageInfo(packageName, 0).lastUpdateTime
                } catch (e: Exception) {
                    0L
                }
                
                val appInfo = AppInfo(
                    appName = appName,
                    packageName = packageName,
                    displayName = appName,
                    versionName = versionName,
                    versionCode = versionCode,
                    isSystemApp = isSystemApp,
                    installTime = installTime,
                    updateTime = updateTime,
                    category = getAppCategory(packageInfo),
                    isEnabled = packageInfo.enabled
                )
                
                // Load app icon
                try {
                    appInfo.icon = packageManager.getApplicationIcon(packageInfo)
                } catch (e: Exception) {
                    // Use default icon if loading fails
                }
                
                apps.add(appInfo)
                
            } catch (e: Exception) {
                // Skip apps that cause errors
                continue
            }
        }
        
        apps
    }
    
    private fun shouldHideApp(packageInfo: ApplicationInfo): Boolean {
        val packageName = packageInfo.packageName
        
        // Always hide these packages
        val hidePackages = listOf(
            "com.android.launcher",
            "com.google.android.launcher",
            "com.sec.android.app.launcher",
            "com.miui.home",
            "com.oneplus.launcher",
            "com.android.settings",
            "com.android.packageinstaller",
            "com.android.systemui",
            "com.android.inputmethod"
        )
        
        if (hidePackages.any { packageName.contains(it) }) {
            return true
        }
        
        // Hide system apps that don't have launcher intents
        if ((packageInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent == null) {
                return true
            }
        }
        
        return false
    }
    
    private fun getAppCategory(packageInfo: ApplicationInfo): String {
        return try {
            when (packageInfo.category) {
                ApplicationInfo.CATEGORY_GAME -> "Games"
                ApplicationInfo.CATEGORY_SOCIAL -> "Social"
                ApplicationInfo.CATEGORY_PRODUCTIVITY -> "Productivity"
                ApplicationInfo.CATEGORY_AUDIO -> "Music & Audio"
                ApplicationInfo.CATEGORY_VIDEO -> "Video Players"
                ApplicationInfo.CATEGORY_IMAGE -> "Photography"
                ApplicationInfo.CATEGORY_NEWS -> "News & Magazines"
                ApplicationInfo.CATEGORY_MAPS -> "Maps & Navigation"
                else -> "Other"
            }
        } catch (e: Exception) {
            "Other"
        }
    }
    
    private fun loadUsageData() {
        for (app in allApps) {
            val usageData = preferences.getAppUsageData(app.packageName)
            app.launchCount = usageData.first
            app.lastLaunchTime = usageData.second
            app.isFavorite = preferences.isFavoriteApp(app.packageName)
        }
    }
    
    private fun saveUsageData(app: AppInfo) {
        preferences.saveAppUsageData(app.packageName, app.launchCount, app.lastLaunchTime)
    }
    
    private fun saveFavoriteStatus(app: AppInfo) {
        preferences.setFavoriteApp(app.packageName, app.isFavorite)
    }
    
    private fun updateDisplayedApps(query: String) {
        val filteredApps = if (query.isBlank()) {
            allApps
        } else {
            allApps.filter { it.matchesQuery(query) }
                .map { app ->
                    app.searchScore = app.calculateSearchScore(query)
                    app
                }
        }
        
        val sortedApps = filteredApps.sortedWith(compareByDescending<AppInfo> { it.isFavorite }
            .thenByDescending { it.searchScore }
            .thenByDescending { it.launchCount }
            .thenByDescending { it.lastLaunchTime }
            .thenBy { it.displayName.lowercase() })
        
        _apps.value = sortedApps
    }
    
    private fun updateMostUsedApps() {
        val mostUsed = allApps
            .filter { it.launchCount > 0 }
            .sortedByDescending { it.launchCount }
            .take(8) // Show top 8 most used apps
        
        _mostUsedApps.value = mostUsed
    }
} 