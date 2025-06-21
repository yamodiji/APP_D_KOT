package com.speedDrawer.speed_drawer.utils

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "speed_drawer_prefs"
        
        // Settings keys
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_AUTO_FOCUS = "auto_focus"
        private const val KEY_VIBRATION = "vibration"
        private const val KEY_SHOW_MOST_USED = "show_most_used"
        private const val KEY_CLEAR_SEARCH_ON_RESUME = "clear_search_on_resume"
        private const val KEY_FINISH_ON_LAUNCH = "finish_on_launch"
        private const val KEY_ICON_SIZE = "icon_size"
        private const val KEY_FUZZY_SEARCH = "fuzzy_search"
        private const val KEY_ANIMATIONS = "animations"
        
        // Usage data keys
        private const val KEY_LAUNCH_COUNT_PREFIX = "launch_count_"
        private const val KEY_LAST_LAUNCH_PREFIX = "last_launch_"
        private const val KEY_FAVORITE_PREFIX = "favorite_"
        
        // Default values
        private const val DEFAULT_ICON_SIZE = 64
    }
    
    // Theme settings
    fun isDarkModeEnabled(): Boolean = prefs.getBoolean(KEY_DARK_MODE, false)
    fun setDarkModeEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    
    // Search settings
    fun isAutoFocusEnabled(): Boolean = prefs.getBoolean(KEY_AUTO_FOCUS, true)
    fun setAutoFocusEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_AUTO_FOCUS, enabled).apply()
    
    fun isFuzzySearchEnabled(): Boolean = prefs.getBoolean(KEY_FUZZY_SEARCH, true)
    fun setFuzzySearchEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_FUZZY_SEARCH, enabled).apply()
    
    fun isClearSearchOnResumeEnabled(): Boolean = prefs.getBoolean(KEY_CLEAR_SEARCH_ON_RESUME, false)
    fun setClearSearchOnResumeEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_CLEAR_SEARCH_ON_RESUME, enabled).apply()
    
    // UI settings
    fun isVibrationEnabled(): Boolean = prefs.getBoolean(KEY_VIBRATION, true)
    fun setVibrationEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply()
    
    fun isAnimationsEnabled(): Boolean = prefs.getBoolean(KEY_ANIMATIONS, true)
    fun setAnimationsEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_ANIMATIONS, enabled).apply()
    
    fun isShowMostUsedEnabled(): Boolean = prefs.getBoolean(KEY_SHOW_MOST_USED, true)
    fun setShowMostUsedEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_SHOW_MOST_USED, enabled).apply()
    
    fun getIconSize(): Int = prefs.getInt(KEY_ICON_SIZE, DEFAULT_ICON_SIZE)
    fun setIconSize(size: Int) = prefs.edit().putInt(KEY_ICON_SIZE, size).apply()
    
    // Behavior settings
    fun isFinishOnLaunchEnabled(): Boolean = prefs.getBoolean(KEY_FINISH_ON_LAUNCH, false)
    fun setFinishOnLaunchEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_FINISH_ON_LAUNCH, enabled).apply()
    
    // Usage data
    fun getAppUsageData(packageName: String): Pair<Int, Long> {
        val launchCount = prefs.getInt(KEY_LAUNCH_COUNT_PREFIX + packageName, 0)
        val lastLaunch = prefs.getLong(KEY_LAST_LAUNCH_PREFIX + packageName, 0L)
        return Pair(launchCount, lastLaunch)
    }
    
    fun saveAppUsageData(packageName: String, launchCount: Int, lastLaunchTime: Long) {
        prefs.edit()
            .putInt(KEY_LAUNCH_COUNT_PREFIX + packageName, launchCount)
            .putLong(KEY_LAST_LAUNCH_PREFIX + packageName, lastLaunchTime)
            .apply()
    }
    
    // Favorites
    fun isFavoriteApp(packageName: String): Boolean = prefs.getBoolean(KEY_FAVORITE_PREFIX + packageName, false)
    fun setFavoriteApp(packageName: String, isFavorite: Boolean) = 
        prefs.edit().putBoolean(KEY_FAVORITE_PREFIX + packageName, isFavorite).apply()
    
    // Bulk operations
    fun getAllFavoriteApps(): Set<String> {
        return prefs.all.entries
            .filter { it.key.startsWith(KEY_FAVORITE_PREFIX) && it.value == true }
            .map { it.key.removePrefix(KEY_FAVORITE_PREFIX) }
            .toSet()
    }
    
    fun clearAllUsageData() {
        val editor = prefs.edit()
        prefs.all.keys
            .filter { it.startsWith(KEY_LAUNCH_COUNT_PREFIX) || it.startsWith(KEY_LAST_LAUNCH_PREFIX) }
            .forEach { editor.remove(it) }
        editor.apply()
    }
    
    fun clearAllFavorites() {
        val editor = prefs.edit()
        prefs.all.keys
            .filter { it.startsWith(KEY_FAVORITE_PREFIX) }
            .forEach { editor.remove(it) }
        editor.apply()
    }
    
    // Export/Import settings
    fun exportSettings(): Map<String, Any> {
        val settings = mutableMapOf<String, Any>()
        prefs.all.forEach { (key, value) ->
            if (!key.startsWith(KEY_LAUNCH_COUNT_PREFIX) && 
                !key.startsWith(KEY_LAST_LAUNCH_PREFIX) && 
                !key.startsWith(KEY_FAVORITE_PREFIX)) {
                value?.let { settings[key] = it }
            }
        }
        return settings
    }
    
    fun importSettings(settings: Map<String, Any>) {
        val editor = prefs.edit()
        settings.forEach { (key, value) ->
            when (value) {
                is Boolean -> editor.putBoolean(key, value)
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Float -> editor.putFloat(key, value)
                is String -> editor.putString(key, value)
            }
        }
        editor.apply()
    }
    
    // Reset all settings to defaults
    fun resetToDefaults() {
        prefs.edit().clear().apply()
    }
} 