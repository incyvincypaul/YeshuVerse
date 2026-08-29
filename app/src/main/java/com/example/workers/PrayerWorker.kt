package com.example.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.R
import com.example.data.FirebaseSyncRepository
import com.example.model.RosarySchedule
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeoutOrNull

class PrayerWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = FirebaseSyncRepository()
        val schedule = withTimeoutOrNull(3000) {
            repository.getSchedule().firstOrNull()
        } ?: RosarySchedule()

        val activeSession = schedule.getCurrentActiveSession()
        // Only notify if a session is ACTUALLY active right now
        if (activeSession != null) {
            val host = schedule.getEffectiveHostForSession(activeSession.name)
            val contentText = "$host is leading the Live Rosary right now. Join in prayer!"
            showPrayerNotification(contentText)
        }
        return Result.success()
    }

    private fun showPrayerNotification(contentText: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "prayer_channel"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Prayer Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Need a better icon eventually
            .setContentTitle("Live Rosary - Join in Prayer")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
            
        notificationManager.notify(1, notification)
    }
}
