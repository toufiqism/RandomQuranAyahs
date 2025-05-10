package com.toufiq.randomquranayahs.data.model

data class QuranResponse(
    val code: Int,
    val status: String,
    val data: QuranData
)

data class QuranData(
    val number: Int,
    val text: String,
    val edition: Edition,
    val surah: Surah,
    val numberInSurah: Int,
    val juz: Int,
    val manzil: Int,
    val page: Int,
    val ruku: Int,
    val hizbQuarter: Int,
    val sajda: Boolean
)

data class Edition(
    val identifier: String,
    val language: String,
    val name: String,
    val englishName: String,
    val format: String,
    val type: String,
    val direction: String
)

data class Surah(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val numberOfAyahs: Int,
    val revelationType: String
) 