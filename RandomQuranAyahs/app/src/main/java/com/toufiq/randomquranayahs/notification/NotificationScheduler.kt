package com.toufiq.randomquranayahs.notification

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    companion object {
        private const val WORK_NAME = "quran_notification_work"
    }

    fun scheduleNotifications(minutes: Int) {
        val workRequest = PeriodicWorkRequestBuilder<QuranNotificationWorker>(
            minutes.toLong(), TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
} 