package com.example

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder
import com.example.ui.MainScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Request notification permission dynamically on Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
      }
    }
    
    // Initialize Coil global ImageLoader with SvgDecoder support
    val imageLoader = ImageLoader.Builder(this)
      .components {
        add(SvgDecoder.Factory())
      }
      .build()
    Coil.setImageLoader(imageLoader)
    
    val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    processIntent(intent, viewModel)
    
    setContent {
      val isDark by viewModel.isDarkTheme.collectAsState()
      MyApplicationTheme(darkTheme = isDark) {
        MainScreen(viewModel = viewModel)
      }
    }
  }

  override fun onNewIntent(intent: android.content.Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    processIntent(intent, viewModel)
  }

  private fun processIntent(intent: android.content.Intent?, viewModel: MainViewModel) {
    val title = intent?.getStringExtra("notification_title")
    val body = intent?.getStringExtra("notification_body")
    val targetTab = intent?.getStringExtra("target_tab")

    if (targetTab != null) {
      try {
        val tab = com.example.ui.AppTab.valueOf(targetTab)
        viewModel.selectTab(tab)
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }

    if (title != null && body != null) {
      viewModel.showDynamicNotificationBanner(title, body)
    }
  }
}
