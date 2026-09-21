package com.example.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.NotificationPayloads
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("alarm_id", 1)
        val channelId = "lawdesk_reminders_silent_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "LawDesk Companion Reminders"
            val descriptionText = "Channel for daily legal readings and alarms"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                setSound(null, null)
                enableVibration(false)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Dynamic notification index based on day of year
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        val payload = com.example.data.NotificationPayloads.getPayloadForId(alarmId, dayOfYear)
        val title = payload.title
        val body = payload.body

        val targetTab = when (alarmId) {
            1 -> "HOME"
            2 -> "LIBRARY"
            else -> "AI"
        }

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_title", title)
            putExtra("notification_body", body)
            putExtra("target_tab", targetTab)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            alarmId,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Post Android System Bar Notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(null)
            .setDefaults(0)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(alarmId, builder.build())

        // Display over other apps (Draw overlay banner if permission is allowed)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && android.provider.Settings.canDrawOverlays(context)) {
            val overlayIntent = Intent(context, OverlayBannerService::class.java).apply {
                putExtra("title", title)
                putExtra("body", body)
                putExtra("target_tab", targetTab)
            }
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(overlayIntent)
                } else {
                    context.startService(overlayIntent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // Fallback: Custom tone plays directly even if overlay is not enabled
            try {
                val prefs = context.getSharedPreferences("lawdesk_prefs", Context.MODE_PRIVATE)
                val toneFile = prefs.getString("selected_tone_file", "universfield-new-notification-03-323602.mp3") ?: "universfield-new-notification-03-323602.mp3"
                val mediaPlayer = android.media.MediaPlayer().apply {
                    val descriptor = context.assets.openFd("sounds/$toneFile")
                    setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
                    descriptor.close()
                    prepare()
                    start()
                }
                mediaPlayer.setOnCompletionListener { mp ->
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
    }
}
