package com.toufiq.randomquranayahs.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject
    lateinit var workManager: WorkManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            schedulePeriodicWork()
        }
    }

    private fun schedulePeriodicWork() {
        val workRequest = PeriodicWorkRequestBuilder<QuranNotificationWorker>(
            15, TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "quran_notification_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
} 