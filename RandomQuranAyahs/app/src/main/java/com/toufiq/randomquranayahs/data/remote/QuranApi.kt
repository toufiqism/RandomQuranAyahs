package com.toufiq.randomquranayahs.data.remote

import com.toufiq.randomquranayahs.data.model.QuranResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApi {
    @GET("ayah/{number}/en.asad")
    suspend fun getRandomAyah(@Path("number") number: Int): QuranResponse
} 