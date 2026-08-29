package com.example

import android.app.Application
import android.util.Log
import androidx.room.Room
import androidx.work.*
import com.example.data.AppLanguageManager
import com.example.data.database.AppDatabase
import com.google.firebase.FirebaseApp
import com.example.workers.PrayerWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class YeshuVerseApplication : Application() {
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        AppLanguageManager.init(this)
        database = Room.databaseBuilder(this, AppDatabase::class.java, "rosary-db").build()
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val app = FirebaseApp.initializeApp(this)
                Log.d("YeshuVerseApp", "Firebase initialized: ${app?.name}")
            } else {
                Log.d("YeshuVerseApp", "Firebase already initialized.")
            }
        } catch (e: Exception) {
            Log.e("YeshuVerseApp", "Failed to initialize Firebase: ${e.message}", e)
        }
        schedulePrayers()
    }

    private fun schedulePrayers() {
        val times = listOf(5, 6, 7, 17, 18, 19)
        val workManager = WorkManager.getInstance(this)

        times.forEach { hour ->
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val now = Calendar.getInstance()
            val nextRun = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(now)) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            
            val delay = nextRun.timeInMillis - now.timeInMillis
            
            val workRequest = PeriodicWorkRequestBuilder<PrayerWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .addTag("prayer_$hour")
                .build()
                
            workManager.enqueueUniquePeriodicWork(
                "prayer_$hour",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
