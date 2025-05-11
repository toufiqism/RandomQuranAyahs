package com.toufiq.randomquranayahs.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val NOTIFICATION_FREQUENCY = intPreferencesKey("notification_frequency")
        private val DEFAULT_FREQUENCY = 15 // 15 minutes
    }

    val notificationFrequency: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATION_FREQUENCY] ?: DEFAULT_FREQUENCY
        }

    suspend fun updateNotificationFrequency(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_FREQUENCY] = minutes
        }
    }
} 