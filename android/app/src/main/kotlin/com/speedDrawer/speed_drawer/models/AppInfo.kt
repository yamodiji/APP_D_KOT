package com.speedDrawer.speed_drawer.models

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppInfo(
    val appName: String,
    val packageName: String,
    val displayName: String = appName,
    val versionName: String? = null,
    val versionCode: Long = 0,
    val isSystemApp: Boolean = false,
    val installTime: Long = 0,
    val updateTime: Long = 0,
    val category: String = "Unknown",
    val isEnabled: Boolean = true
) : Parcelable {
    
    // Non-parcelable properties
    @Transient
    var icon: Drawable? = null
    
    // Usage tracking (stored separately in preferences)
    var launchCount: Int = 0
    var lastLaunchTime: Long = 0
    var isFavorite: Boolean = false
    var searchScore: Double = 0.0
    
    /**
     * Check if app matches search query using fuzzy matching
     */
    fun matchesQuery(query: String): Boolean {
        if (query.isBlank()) return true
        
        val lowerQuery = query.lowercase()
        val lowerAppName = appName.lowercase()
        val lowerDisplayName = displayName.lowercase()
        val lowerPackageName = packageName.lowercase()
        
        return lowerAppName.contains(lowerQuery) ||
               lowerDisplayName.contains(lowerQuery) ||
               lowerPackageName.contains(lowerQuery) ||
               fuzzyMatch(lowerAppName, lowerQuery) ||
               fuzzyMatch(lowerDisplayName, lowerQuery)
    }
    
    /**
     * Simple fuzzy matching algorithm
     */
    private fun fuzzyMatch(text: String, query: String): Boolean {
        if (query.length > text.length) return false
        
        var textIndex = 0
        var queryIndex = 0
        
        while (textIndex < text.length && queryIndex < query.length) {
            if (text[textIndex] == query[queryIndex]) {
                queryIndex++
            }
            textIndex++
        }
        
        return queryIndex == query.length
    }
    
    /**
     * Calculate search relevance score
     */
    fun calculateSearchScore(query: String): Double {
        if (query.isBlank()) return 0.0
        
        val lowerQuery = query.lowercase()
        val lowerAppName = appName.lowercase()
        val lowerDisplayName = displayName.lowercase()
        
        var score = 0.0
        
        // Exact match gets highest score
        if (lowerAppName == lowerQuery || lowerDisplayName == lowerQuery) {
            score += 100.0
        }
        
        // Starts with query gets high score
        if (lowerAppName.startsWith(lowerQuery) || lowerDisplayName.startsWith(lowerQuery)) {
            score += 80.0
        }
        
        // Contains query gets medium score
        if (lowerAppName.contains(lowerQuery) || lowerDisplayName.contains(lowerQuery)) {
            score += 60.0
        }
        
        // Fuzzy match gets lower score
        if (fuzzyMatch(lowerAppName, lowerQuery) || fuzzyMatch(lowerDisplayName, lowerQuery)) {
            score += 40.0
        }
        
        // Bonus for frequently used apps
        score += (launchCount * 0.5)
        
        // Bonus for favorites
        if (isFavorite) {
            score += 20.0
        }
        
        // Bonus for recently used apps
        val daysSinceLastLaunch = (System.currentTimeMillis() - lastLaunchTime) / (1000 * 60 * 60 * 24)
        if (daysSinceLastLaunch < 7) {
            score += (7 - daysSinceLastLaunch) * 2
        }
        
        return score
    }
    
    /**
     * Check if this app should be hidden from the list
     */
    fun shouldHide(): Boolean {
        return isSystemApp && (
                packageName.startsWith("com.android.") ||
                packageName.startsWith("com.google.android.") ||
                packageName.contains("packageinstaller") ||
                packageName.contains("wallpaper") ||
                packageName.contains("launcher") ||
                !isEnabled
                )
    }
    
    /**
     * Get app category for grouping
     */
    fun getCategoryDisplayName(): String {
        return when (category.lowercase()) {
            "game" -> "Games"
            "social" -> "Social"
            "productivity" -> "Productivity"
            "communication" -> "Communication"
            "entertainment" -> "Entertainment"
            "photography" -> "Photography"
            "music_and_audio" -> "Music & Audio"
            "video_players" -> "Video Players"
            "tools" -> "Tools"
            "travel_and_local" -> "Travel & Local"
            "shopping" -> "Shopping"
            "news_and_magazines" -> "News & Magazines"
            "books_and_reference" -> "Books & Reference"
            "education" -> "Education"
            "business" -> "Business"
            "lifestyle" -> "Lifestyle"
            "finance" -> "Finance"
            "health_and_fitness" -> "Health & Fitness"
            "sports" -> "Sports"
            "weather" -> "Weather"
            "auto_and_vehicles" -> "Auto & Vehicles"
            "house_and_home" -> "House & Home"
            "parenting" -> "Parenting"
            "dating" -> "Dating"
            "food_and_drink" -> "Food & Drink"
            "maps_and_navigation" -> "Maps & Navigation"
            "medical" -> "Medical"
            else -> "Other"
        }
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppInfo) return false
        return packageName == other.packageName
    }
    
    override fun hashCode(): Int {
        return packageName.hashCode()
    }
    
    override fun toString(): String {
        return "AppInfo(name='$appName', package='$packageName', displayName='$displayName')"
    }
} 