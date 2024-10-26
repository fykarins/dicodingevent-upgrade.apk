package com.example.dicodingevent.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dicodingevent.R
import com.example.dicodingevent.di.Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val repository = Injection.provideRepository(context)
            val result = repository.getUpcomingEvent(limit = 1)

            result?.listEvents?.firstOrNull()?.let { event ->
                showNotification(event.name, event.beginTime)
                Result.success()
            } ?: Result.failure()
        }
    }

    private fun showNotification(eventName: String, eventTime: String) {
        val channelId = "event_reminder_channel"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Event Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminder for upcoming event"
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("Upcoming Event: $eventName")
            .setContentText("Scheduled at: $eventTime")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Check permission before showing notification if Android version >= 33 (Android 13/Tiramisu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(context).notify(notificationId, notification)
            }
        } else {
            // Show notification without permission check if Android version < 13
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }
}
