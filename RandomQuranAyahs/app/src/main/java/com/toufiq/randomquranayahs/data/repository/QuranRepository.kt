package com.toufiq.randomquranayahs.data.repository

import com.toufiq.randomquranayahs.data.model.QuranResponse
import com.toufiq.randomquranayahs.data.remote.QuranApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranRepository @Inject constructor(
    private val api: QuranApi
) {
    suspend fun getRandomAyah(): Result<QuranResponse> = withContext(Dispatchers.IO) {
        try {
            val randomNumber = (1..6236).random()
            val response = api.getRandomAyah(randomNumber)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 