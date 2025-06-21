package com.speedDrawer.speed_drawer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.speedDrawer.speed_drawer.databinding.ActivityAppOptionsBinding
import com.speedDrawer.speed_drawer.models.AppInfo
import com.speedDrawer.speed_drawer.utils.Constants

class AppOptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppOptionsBinding
    private lateinit var appInfo: AppInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get app info from intent
        appInfo = intent.getParcelableExtra(Constants.EXTRA_APP_INFO) ?: run {
            finish()
            return
        }

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = appInfo.displayName

        // Load app icon
        binding.appIcon.setImageDrawable(appInfo.icon)
        binding.appName.text = appInfo.displayName
        binding.packageName.text = appInfo.packageName
    }

    private fun setupClickListeners() {
        binding.launchButton.setOnClickListener {
            launchApp()
        }

        binding.appInfoButton.setOnClickListener {
            openAppInfo()
        }

        binding.uninstallButton.setOnClickListener {
            uninstallApp()
        }

        binding.playStoreButton.setOnClickListener {
            openInPlayStore()
        }
    }

    private fun launchApp() {
        try {
            val intent = packageManager.getLaunchIntentForPackage(appInfo.packageName)
            if (intent != null) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        } catch (e: Exception) {
            // Handle error
        }
    }

    private fun openAppInfo() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${appInfo.packageName}")
            startActivity(intent)
        } catch (e: Exception) {
            // Handle error
        }
    }

    private fun uninstallApp() {
        try {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:${appInfo.packageName}")
            startActivity(intent)
        } catch (e: Exception) {
            // Handle error
        }
    }

    private fun openInPlayStore() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${appInfo.packageName}"))
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to web browser
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${appInfo.packageName}"))
            startActivity(webIntent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 