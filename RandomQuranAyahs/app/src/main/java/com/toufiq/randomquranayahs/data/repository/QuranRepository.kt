package com.toufiq.randomquranayahs.data.repository

import com.toufiq.randomquranayahs.data.model.QuranResponse
import com.toufiq.randomquranayahs.data.remote.NetworkResult
import com.toufiq.randomquranayahs.data.remote.QuranApi
import com.toufiq.randomquranayahs.data.remote.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranRepository @Inject constructor(
    private val api: QuranApi
) {
    suspend fun getRandomAyah(): NetworkResult<QuranResponse> = withContext(Dispatchers.IO) {
        val randomNumber = (1..6236).random()
        safeApiCall { api.getRandomAyah(randomNumber) }
    }
} 