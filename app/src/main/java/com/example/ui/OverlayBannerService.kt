package com.example.ui

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.MainActivity

class OverlayBannerService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayLayout: FrameLayout? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val title = intent?.getStringExtra("title") ?: "Daily Law Desk Companion"
        val body = intent?.getStringExtra("body") ?: "Time for your daily legal update and case study."
        val targetTab = intent?.getStringExtra("target_tab") ?: "HOME"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "lawdesk_overlay_channel"
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            val channel = android.app.NotificationChannel(
                channelId,
                "Overlay Active Reminder",
                android.app.NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Required notification for showing dynamic law updates over applications."
                setSound(null, null)
                enableVibration(false)
            }
            notificationManager.createNotificationChannel(channel)
            
            val pendingIntent = android.app.PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                android.app.PendingIntent.FLAG_IMMUTABLE
            )
            
            val notification = androidx.core.app.NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_MIN)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            
            try {
                startForeground(1005, notification)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        showOverlayBanner(title, body, targetTab)
        playNotificationTone()

        return START_NOT_STICKY
    }

    private fun showOverlayBanner(title: String, body: String, targetTab: String) {
        // If there's already an active overlay, remove it first
        removeOverlay()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val density = resources.displayMetrics.density
        fun dpToPx(dp: Int): Int = (dp * density).toInt()

        // Root layout to hold the card and add padding/margins
        overlayLayout = FrameLayout(this).apply {
            setPadding(dpToPx(16), dpToPx(48), dpToPx(16), dpToPx(16))
        }

        // Card Container background
        val cardBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(24).toFloat()
            setColor(Color.parseColor("#0C0C0E"))
            setStroke(dpToPx(2), Color.parseColor("#007AFF"))
        }

        // Inner vertical container
        val innerContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardBackground
            setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20))
            elevation = dpToPx(8).toFloat()
        }

        // Row 1: Bell icon, Title Header, Close button
        val headerLayout = RelativeLayout(this).apply {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, dpToPx(12))
            layoutParams = params
        }

        // Bell Icon & Header text together
        val headerTextLayout = LinearLayout(this).apply {
            id = View.generateViewId()
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            layoutParams = params
        }

        val bellIcon = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_popup_reminder)
            setColorFilter(Color.parseColor("#007AFF"))
            val params = LinearLayout.LayoutParams(dpToPx(18), dpToPx(18))
            params.setMargins(0, 0, dpToPx(8), 0)
            layoutParams = params
        }

        val headerTitle = TextView(this).apply {
            text = "LAWDESK DAILY COMPANION"
            setTextColor(Color.parseColor("#007AFF"))
            textSize = 11f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            letterSpacing = 0.12f
        }

        headerTextLayout.addView(bellIcon)
        headerTextLayout.addView(headerTitle)

        // Close Button 'X'
        val closeButton = TextView(this).apply {
            text = "✕"
            setTextColor(Color.parseColor("#8E8E93"))
            textSize = 16f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4))
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            params.addRule(RelativeLayout.CENTER_VERTICAL)
            layoutParams = params
            setOnClickListener {
                dismissService()
            }
        }

        headerLayout.addView(headerTextLayout)
        headerLayout.addView(closeButton)

        // Line 2: Main Title
        val titleText = TextView(this).apply {
            text = title
            setTextColor(Color.parseColor("#F2F2F7"))
            textSize = 18f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, dpToPx(6))
            layoutParams = params
        }

        // Line 3: Body text
        val bodyText = TextView(this).apply {
            text = body
            setTextColor(Color.parseColor("#AEAEB2"))
            textSize = 14f
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            maxLines = 3
            ellipsize = TextUtils.TruncateAt.END
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, dpToPx(16))
            layoutParams = params
        }

        // Row 4: Action Buttons Row (DISMISS & OPEN STUDY)
        val buttonsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = params
        }

        val dismissBtn = TextView(this).apply {
            text = "DISMISS"
            setTextColor(Color.parseColor("#F2F2F7"))
            textSize = 12f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12))
            isClickable = true
            isFocusable = true
            gravity = Gravity.CENTER
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, dpToPx(12), 0)
            layoutParams = params
            setOnClickListener {
                dismissService()
            }
        }

        val openStudyBtnBgc = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(12).toFloat()
            setColor(Color.parseColor("#007AFF"))
        }

        val openStudyBtn = TextView(this).apply {
            text = "OPEN STUDY"
            setTextColor(Color.parseColor("#FFFFFF"))
            textSize = 12f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            background = openStudyBtnBgc
            setPadding(dpToPx(24), dpToPx(12), dpToPx(24), dpToPx(12))
            isClickable = true
            isFocusable = true
            gravity = Gravity.CENTER
            setOnClickListener {
                val appIntent = Intent(this@OverlayBannerService, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    putExtra("notification_title", title)
                    putExtra("notification_body", body)
                    putExtra("target_tab", targetTab)
                }
                startActivity(appIntent)
                dismissService()
            }
        }

        buttonsLayout.addView(dismissBtn)
        buttonsLayout.addView(openStudyBtn)

        innerContainer.addView(headerLayout)
        innerContainer.addView(titleText)
        innerContainer.addView(bodyText)
        innerContainer.addView(buttonsLayout)

        overlayLayout?.addView(innerContainer)

        // Type Overlay settings
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP
            y = dpToPx(12) // Top margin/offset below the status bar area
        }

        try {
            windowManager?.addView(overlayLayout, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playNotificationTone() {
        try {
            mediaPlayer?.release()
        } catch (e: Exception) {
            // ignore
        }
        try {
            val prefs = getSharedPreferences("lawdesk_prefs", Context.MODE_PRIVATE)
            val toneFile = prefs.getString("selected_tone_file", "universfield-new-notification-03-323602.mp3") ?: "universfield-new-notification-03-323602.mp3"

            mediaPlayer = MediaPlayer().apply {
                val descriptor = assets.openFd("sounds/$toneFile")
                setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
                descriptor.close()
                prepare()
                start()
            }
            mediaPlayer?.setOnCompletionListener { mp ->
                try {
                    mp.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dismissService() {
        removeOverlay()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        stopSelf()
    }

    private fun removeOverlay() {
        try {
            if (overlayLayout != null && windowManager != null) {
                windowManager?.removeView(overlayLayout)
                overlayLayout = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeOverlay()
        try {
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
