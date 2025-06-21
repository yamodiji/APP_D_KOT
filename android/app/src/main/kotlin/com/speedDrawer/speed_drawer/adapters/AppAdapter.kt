package com.speedDrawer.speed_drawer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.speedDrawer.speed_drawer.databinding.ItemAppBinding
import com.speedDrawer.speed_drawer.models.AppInfo

class AppAdapter(
    private val iconSize: Int,
    private val onAppClick: (AppInfo) -> Unit,
    private val onAppLongClick: (AppInfo) -> Unit
) : ListAdapter<AppInfo, AppAdapter.AppViewHolder>(AppDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class AppViewHolder(private val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(app: AppInfo) {
            binding.apply {
                // Set app name
                appName.text = app.displayName
                
                // Set app icon
                app.icon?.let { icon ->
                    appIcon.setImageDrawable(icon)
                } ?: run {
                    // Use default icon if no icon is available
                    appIcon.setImageResource(android.R.drawable.sym_def_app_icon)
                }
                
                // Set icon size
                val layoutParams = appIcon.layoutParams
                layoutParams.width = iconSize
                layoutParams.height = iconSize
                appIcon.layoutParams = layoutParams
                
                // Show favorite indicator
                favoriteIcon.visibility = if (app.isFavorite) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }
                
                // Set click listeners
                root.setOnClickListener {
                    onAppClick(app)
                }
                
                root.setOnLongClickListener {
                    onAppLongClick(app)
                    true
                }
                
                // Add subtle animation on bind
                root.alpha = 0.8f
                root.animate()
                    .alpha(1.0f)
                    .setDuration(150)
                    .start()
            }
        }
    }
    
    private class AppDiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.packageName == newItem.packageName
        }
        
        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem == newItem && 
                   oldItem.isFavorite == newItem.isFavorite &&
                   oldItem.launchCount == newItem.launchCount &&
                   oldItem.searchScore == newItem.searchScore
        }
    }
} 