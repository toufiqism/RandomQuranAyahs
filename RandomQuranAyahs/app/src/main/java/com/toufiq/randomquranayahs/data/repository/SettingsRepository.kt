package com.toufiq.randomquranayahs.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.toufiq.randomquranayahs.ui.viewmodel.NotificationAppearance
import com.toufiq.randomquranayahs.ui.viewmodel.NotificationStyle
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val NOTIFICATION_FREQUENCY = intPreferencesKey("notification_frequency")
        val NOTIFICATION_STYLE = stringPreferencesKey("notification_style")
        
        // Appearance settings
        val SHOW_ARABIC_TEXT = booleanPreferencesKey("show_arabic_text")
        val SHOW_TRANSLATION = booleanPreferencesKey("show_translation")
        val SHOW_SURAH_INFO = booleanPreferencesKey("show_surah_info")
        val SHOW_TRANSLATOR_INFO = booleanPreferencesKey("show_translator_info")
        val FONT_SIZE = intPreferencesKey("font_size")
        val USE_CUSTOM_FONT = booleanPreferencesKey("use_custom_font")
        val CUSTOM_FONT_NAME = stringPreferencesKey("custom_font_name")
        val BACKGROUND_COLOR = longPreferencesKey("background_color")
        val TEXT_COLOR = longPreferencesKey("text_color")
        val ACCENT_COLOR = longPreferencesKey("accent_color")
    }

    fun getNotificationFrequency(): Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_FREQUENCY] ?: 15
        }

    fun getNotificationStyle(): Flow<NotificationStyle> = context.dataStore.data
        .map { preferences ->
            val styleString = preferences[PreferencesKeys.NOTIFICATION_STYLE] ?: NotificationStyle.DEFAULT.name
            try {
                NotificationStyle.valueOf(styleString)
            } catch (e: IllegalArgumentException) {
                NotificationStyle.DEFAULT
            }
        }

    fun getNotificationPermissionState(): Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_FREQUENCY] != null
        }

    fun getNotificationAppearance(): Flow<NotificationAppearance> = context.dataStore.data
        .map { preferences ->
            NotificationAppearance(
                showArabicText = preferences[PreferencesKeys.SHOW_ARABIC_TEXT] ?: true,
                showTranslation = preferences[PreferencesKeys.SHOW_TRANSLATION] ?: true,
                showSurahInfo = preferences[PreferencesKeys.SHOW_SURAH_INFO] ?: true,
                showTranslatorInfo = preferences[PreferencesKeys.SHOW_TRANSLATOR_INFO] ?: true,
                fontSize = preferences[PreferencesKeys.FONT_SIZE] ?: 16,
                useCustomFont = preferences[PreferencesKeys.USE_CUSTOM_FONT] ?: false,
                customFontName = preferences[PreferencesKeys.CUSTOM_FONT_NAME] ?: "Poppins",
                backgroundColor = Color(preferences[PreferencesKeys.BACKGROUND_COLOR]?.toInt() ?: Color.Transparent.toArgb()),
                textColor = Color(preferences[PreferencesKeys.TEXT_COLOR]?.toInt() ?: Color.Transparent.toArgb()),
                accentColor = Color(preferences[PreferencesKeys.ACCENT_COLOR]?.toInt() ?: Color.Transparent.toArgb())
            )
        }

    suspend fun setNotificationFrequency(frequency: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_FREQUENCY] = frequency
        }
    }

    suspend fun setNotificationStyle(style: NotificationStyle) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_STYLE] = style.name
        }
    }

    suspend fun setNotificationAppearance(appearance: NotificationAppearance) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_ARABIC_TEXT] = appearance.showArabicText
            preferences[PreferencesKeys.SHOW_TRANSLATION] = appearance.showTranslation
            preferences[PreferencesKeys.SHOW_SURAH_INFO] = appearance.showSurahInfo
            preferences[PreferencesKeys.SHOW_TRANSLATOR_INFO] = appearance.showTranslatorInfo
            preferences[PreferencesKeys.FONT_SIZE] = appearance.fontSize
            preferences[PreferencesKeys.USE_CUSTOM_FONT] = appearance.useCustomFont
            preferences[PreferencesKeys.CUSTOM_FONT_NAME] = appearance.customFontName
            preferences[PreferencesKeys.BACKGROUND_COLOR] = appearance.backgroundColor.toArgb().toLong()
            preferences[PreferencesKeys.TEXT_COLOR] = appearance.textColor.toArgb().toLong()
            preferences[PreferencesKeys.ACCENT_COLOR] = appearance.accentColor.toArgb().toLong()
        }
    }

    fun requestNotificationPermission() {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", context.packageName, null)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
} 