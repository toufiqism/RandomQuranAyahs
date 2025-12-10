package com.toufiq.randomquranayahs.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.toufiq.randomquranayahs.data.remote.NetworkResult
import com.toufiq.randomquranayahs.data.repository.QuranRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class QuranNotificationWorker @AssistedInject constructor (
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val quranRepository: QuranRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        when (val result = quranRepository.getRandomAyah()) {
            is NetworkResult.Success -> {
                notificationHelper.showNotification(result.data.data)
                Result.success()
            }
            is NetworkResult.Error -> {
                if (result.error.isRetryable) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
            is NetworkResult.Loading -> Result.retry()
        }
    }
} 