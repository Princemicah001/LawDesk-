package com.example.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
            intent.action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            val prefs = context.getSharedPreferences("lawdesk_prefs", Context.MODE_PRIVATE)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            for (alarmId in 1..3) {
                val enabled = prefs.getBoolean("alarm${alarmId}_enabled", true)
                val hour = prefs.getInt("alarm${alarmId}_hour", when (alarmId) {
                    1 -> 8
                    2 -> 13
                    else -> 18
                })
                val minute = prefs.getInt("alarm${alarmId}_minute", 0)

                if (enabled) {
                    val alarmIntent = Intent(context, ReminderReceiver::class.java).apply {
                        putExtra("alarm_id", alarmId)
                    }
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        alarmId,
                        alarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = System.currentTimeMillis()
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                        if (timeInMillis <= System.currentTimeMillis()) {
                            add(Calendar.DAY_OF_YEAR, 1)
                        }
                    }

                    try {
                        alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            AlarmManager.INTERVAL_DAY,
                            pendingIntent
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
