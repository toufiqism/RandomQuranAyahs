package com.toufiq.randomquranayahs.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.toufiq.randomquranayahs.data.repository.QuranRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class QuranNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val quranRepository: QuranRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            quranRepository.getRandomAyah().fold(
                onSuccess = { response ->
                    response.data.let { quranData ->
                        notificationHelper.showNotification(quranData)
                        Result.success()
                    }
                },
                onFailure = { error ->
                    Result.retry()
                }
            )
        } catch (e: Exception) {
            Result.retry()
        }
    }
} 