package com.speedDrawer.speed_drawer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.speedDrawer.speed_drawer.adapters.AppAdapter
import com.speedDrawer.speed_drawer.databinding.ActivityMainBinding
import com.speedDrawer.speed_drawer.models.AppInfo
import com.speedDrawer.speed_drawer.viewmodels.AppViewModel
import com.speedDrawer.speed_drawer.utils.AppPreferences
import com.speedDrawer.speed_drawer.utils.Constants
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var appAdapter: AppAdapter
    private lateinit var preferences: AppPreferences
    private val appViewModel: AppViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize preferences
        preferences = AppPreferences(this)
        applyTheme()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        setupRecyclerView()
        setupSearch()
        setupObservers()
        
        // Load apps
        appViewModel.loadApps()
        
        // Auto-focus search if enabled
        if (preferences.isAutoFocusEnabled()) {
            showKeyboardWithDelay()
        }
    }
    
    override fun onResume() {
        super.onResume()
        
        // Clear search if setting is enabled
        if (preferences.isClearSearchOnResumeEnabled()) {
            binding.searchEditText.text?.clear()
        }
        
        // Auto-focus if enabled
        if (preferences.isAutoFocusEnabled() && binding.searchEditText.text.isNullOrEmpty()) {
            showKeyboardWithDelay()
        }
        
        // Refresh app list in case new apps were installed
        appViewModel.refreshApps()
    }
    
    private fun applyTheme() {
        val isDarkMode = preferences.isDarkModeEnabled()
        if (isDarkMode) {
            setTheme(R.style.Theme_SpeedDrawer_Dark)
        } else {
            setTheme(R.style.Theme_SpeedDrawer_Light)
        }
    }
    
    private fun setupUI() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        
        // Setup settings button
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        // Setup clear button
        binding.clearButton.setOnClickListener {
            binding.searchEditText.text?.clear()
            if (preferences.isAutoFocusEnabled()) {
                binding.searchEditText.requestFocus()
                showKeyboard()
            }
        }
        
        // Setup most used apps visibility
        updateMostUsedVisibility()
    }
    
    private fun setupRecyclerView() {
        val spanCount = calculateSpanCount()
        binding.recyclerView.layoutManager = GridLayoutManager(this, spanCount)
        
        appAdapter = AppAdapter(
            iconSize = preferences.getIconSize(),
            onAppClick = { app -> launchApp(app) },
            onAppLongClick = { app -> showAppOptions(app) }
        )
        
        binding.recyclerView.adapter = appAdapter
    }
    
    private fun calculateSpanCount(): Int {
        val iconSize = preferences.getIconSize()
        val screenWidth = resources.displayMetrics.widthPixels
        val padding = resources.getDimensionPixelSize(R.dimen.recycler_padding) * 2
        val itemWidth = iconSize + resources.getDimensionPixelSize(R.dimen.item_padding) * 2
        
        return ((screenWidth - padding) / itemWidth).coerceAtLeast(3)
    }
    
    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                appViewModel.searchApps(query)
                
                // Show/hide clear button
                binding.clearButton.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                
                // Update most used apps visibility
                updateMostUsedVisibility()
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun setupObservers() {
        appViewModel.apps.observe(this) { apps ->
            appAdapter.submitList(apps)
            
            // Show/hide empty state
            binding.emptyStateLayout.visibility = if (apps.isEmpty()) View.VISIBLE else View.GONE
        }
        
        appViewModel.mostUsedApps.observe(this) { mostUsedApps ->
            updateMostUsedApps(mostUsedApps)
        }
        
        appViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
    
    private fun updateMostUsedApps(mostUsedApps: List<AppInfo>) {
        if (!preferences.isShowMostUsedEnabled() || binding.searchEditText.text?.isNotEmpty() == true) {
            binding.mostUsedLayout.visibility = View.GONE
            return
        }
        
        if (mostUsedApps.isNotEmpty()) {
            binding.mostUsedLayout.visibility = View.VISIBLE
            // Setup most used apps horizontal list
            // Implementation details...
        } else {
            binding.mostUsedLayout.visibility = View.GONE
        }
    }
    
    private fun updateMostUsedVisibility() {
        val showMostUsed = preferences.isShowMostUsedEnabled() && binding.searchEditText.text.isNullOrEmpty()
        binding.mostUsedLayout.visibility = if (showMostUsed) View.VISIBLE else View.GONE
    }
    
    private fun launchApp(app: AppInfo) {
        try {
            // Provide haptic feedback if enabled
            if (preferences.isVibrationEnabled()) {
                binding.root.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            }
            
            val intent = packageManager.getLaunchIntentForPackage(app.packageName)
            if (intent != null) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                
                // Update launch count
                appViewModel.incrementLaunchCount(app)
                
                // Finish activity if setting is enabled
                if (preferences.isFinishOnLaunchEnabled()) {
                    finish()
                }
            } else {
                showToast("Cannot launch ${app.displayName}")
            }
        } catch (e: Exception) {
            showToast("Failed to launch ${app.displayName}")
        }
    }
    
    private fun showAppOptions(app: AppInfo) {
        // Provide haptic feedback if enabled
        if (preferences.isVibrationEnabled()) {
            binding.root.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
        }
        
        val intent = Intent(this, AppOptionsActivity::class.java)
        intent.putExtra(Constants.EXTRA_APP_INFO, app)
        startActivity(intent)
    }
    
    private fun showKeyboardWithDelay() {
        binding.searchEditText.postDelayed({
            binding.searchEditText.requestFocus()
            showKeyboard()
        }, 100)
    }
    
    private fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    override fun onBackPressed() {
        if (binding.searchEditText.text?.isNotEmpty() == true) {
            binding.searchEditText.text?.clear()
        } else {
            super.onBackPressed()
        }
    }
} 