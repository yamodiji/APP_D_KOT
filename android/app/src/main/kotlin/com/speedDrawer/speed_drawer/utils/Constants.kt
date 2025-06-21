package com.speedDrawer.speed_drawer.utils

object Constants {
    
    // Intent extras
    const val EXTRA_APP_INFO = "extra_app_info"
    
    // Icon sizes
    const val ICON_SIZE_SMALL = 48
    const val ICON_SIZE_MEDIUM = 64
    const val ICON_SIZE_LARGE = 80
    const val ICON_SIZE_EXTRA_LARGE = 96
    
    // Search
    const val SEARCH_DEBOUNCE_DELAY = 300L
    const val MIN_SEARCH_QUERY_LENGTH = 1
    const val MAX_SEARCH_RESULTS = 50
    
    // UI
    const val ANIMATION_DURATION_SHORT = 150L
    const val ANIMATION_DURATION_MEDIUM = 300L
    const val ANIMATION_DURATION_LONG = 500L
    
    // Grid layout
    const val MIN_SPAN_COUNT = 3
    const val MAX_SPAN_COUNT = 8
    
    // Most used apps
    const val MAX_MOST_USED_APPS = 8
    const val MIN_LAUNCH_COUNT_FOR_MOST_USED = 1
    
    // Performance
    const val APP_LOAD_BATCH_SIZE = 20
    const val ICON_CACHE_SIZE = 100
    
    // Backup/Restore
    const val BACKUP_FILE_NAME = "speed_drawer_backup.json"
    const val BACKUP_VERSION = 1
    
    // Permissions
    const val REQUEST_CODE_OVERLAY_PERMISSION = 1001
    const val REQUEST_CODE_STORAGE_PERMISSION = 1002
    
    // Themes
    const val THEME_LIGHT = "light"
    const val THEME_DARK = "dark"
    const val THEME_SYSTEM = "system"
    
    // Categories
    val APP_CATEGORIES = mapOf(
        "Games" to "game",
        "Social" to "social",
        "Productivity" to "productivity",
        "Communication" to "communication",
        "Entertainment" to "entertainment",
        "Photography" to "photography",
        "Music & Audio" to "music_and_audio",
        "Video Players" to "video_players",
        "Tools" to "tools",
        "Travel & Local" to "travel_and_local",
        "Shopping" to "shopping",
        "News & Magazines" to "news_and_magazines",
        "Books & Reference" to "books_and_reference",
        "Education" to "education",
        "Business" to "business",
        "Lifestyle" to "lifestyle",
        "Finance" to "finance",
        "Health & Fitness" to "health_and_fitness",
        "Sports" to "sports",
        "Weather" to "weather",
        "Auto & Vehicles" to "auto_and_vehicles",
        "House & Home" to "house_and_home",
        "Parenting" to "parenting",
        "Dating" to "dating",
        "Food & Drink" to "food_and_drink",
        "Maps & Navigation" to "maps_and_navigation",
        "Medical" to "medical",
        "Other" to "other"
    )
    
    // System packages to hide
    val HIDDEN_SYSTEM_PACKAGES = setOf(
        "com.android.launcher",
        "com.android.launcher2",
        "com.android.launcher3",
        "com.google.android.launcher",
        "com.sec.android.app.launcher",
        "com.miui.home",
        "com.oneplus.launcher",
        "com.huawei.android.launcher",
        "com.android.settings",
        "com.android.packageinstaller",
        "com.android.systemui",
        "com.android.inputmethod",
        "com.android.vending",
        "com.google.android.gms",
        "com.google.android.gsf",
        "com.android.phone",
        "com.android.contacts",
        "com.android.providers.contacts",
        "com.android.providers.calendar",
        "com.android.calendar",
        "com.android.deskclock",
        "com.android.calculator2"
    )
    
    // Performance optimization
    const val ENABLE_STRICT_MODE = false
    const val ENABLE_LEAK_CANARY = false
    
    // Logging
    const val LOG_TAG = "SpeedDrawer"
    const val ENABLE_DEBUG_LOGGING = true
    
    // App info
    const val APP_NAME = "Speed Drawer"
    const val APP_VERSION = "1.0.0"
    const val APP_PACKAGE = "com.speedDrawer.speed_drawer"
    
    // URLs
    const val GITHUB_URL = "https://github.com/yourusername/speed-drawer"
    const val PRIVACY_POLICY_URL = "https://yourdomain.com/privacy"
    const val TERMS_OF_SERVICE_URL = "https://yourdomain.com/terms"
    
    // Support
    const val SUPPORT_EMAIL = "support@yourdomain.com"
    const val FEEDBACK_EMAIL = "feedback@yourdomain.com"
} 