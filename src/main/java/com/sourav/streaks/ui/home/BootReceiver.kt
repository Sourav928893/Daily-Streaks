package com.sourav.streaks.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sourav.streaks.data.StreakRepository
import com.sourav.streaks.utils.NotificationScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule all reminders using WorkManager
            val repository = StreakRepository.getInstance()
            val scheduler = NotificationScheduler(context)
            val streaks = repository.streaks.value ?: emptyList()

            for (streak in streaks) {
                streak.reminder?.let { reminder ->
                    scheduler.scheduleReminder(streak.id, streak.name, reminder)
                }
            }
        }
    }
}
