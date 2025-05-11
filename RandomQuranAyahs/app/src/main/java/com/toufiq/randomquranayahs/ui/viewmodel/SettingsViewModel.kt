package com.toufiq.randomquranayahs.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toufiq.randomquranayahs.data.repository.SettingsRepository
import com.toufiq.randomquranayahs.notification.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.getNotificationFrequency(),
                settingsRepository.getNotificationStyle(),
                settingsRepository.getNotificationPermissionState(),
                settingsRepository.getNotificationAppearance()
            ) { frequency: Int, style: NotificationStyle, permissionState: Boolean, appearance: NotificationAppearance ->
                _state.value = SettingsState(
                    frequency = frequency,
                    notificationStyle = style,
                    hasNotificationPermission = permissionState,
                    isDarkModePreview = false,
                    appearance = appearance
                )
            }.collect()
        }
    }

    fun updateFrequency(frequency: Int) {
        viewModelScope.launch {
            settingsRepository.setNotificationFrequency(frequency)
            notificationScheduler.scheduleNotifications(frequency)
        }
    }

    fun updateNotificationStyle(style: NotificationStyle) {
        viewModelScope.launch {
            settingsRepository.setNotificationStyle(style)
        }
    }

    fun toggleDarkModePreview() {
        _state.value = _state.value.copy(
            isDarkModePreview = !_state.value.isDarkModePreview
        )
    }

    fun requestNotificationPermission() {
        viewModelScope.launch {
            settingsRepository.requestNotificationPermission()
        }
    }

    fun updateAppearance(appearance: NotificationAppearance) {
        viewModelScope.launch {
            settingsRepository.setNotificationAppearance(appearance)
        }
    }

    fun toggleShowArabicText() {
        _state.value = _state.value.copy(
            appearance = _state.value.appearance.copy(
                showArabicText = !_state.value.appearance.showArabicText
            )
        )
        updateAppearance(_state.value.appearance)
    }

    fun toggleShowTranslation() {
        _state.value = _state.value.copy(
            appearance = _state.value.appearance.copy(
                showTranslation = !_state.value.appearance.showTranslation
            )
        )
        updateAppearance(_state.value.appearance)
    }

    fun toggleShowSurahInfo() {
        _state.value = _state.value.copy(
            appearance = _state.value.appearance.copy(
                showSurahInfo = !_state.value.appearance.showSurahInfo
            )
        )
        updateAppearance(_state.value.appearance)
    }

    fun toggleShowTranslatorInfo() {
        _state.value = _state.value.copy(
            appearance = _state.value.appearance.copy(
                showTranslatorInfo = !_state.value.appearance.showTranslatorInfo
            )
        )
        updateAppearance(_state.value.appearance)
    }

    fun updateFontSize(size: Int) {
        _state.value = _state.value.copy(
            appearance = _state.value.appearance.copy(
                fontSize = size
            )
        )
        updateAppearance(_state.value.appearance)
    }

    fun toggleCustomFont() {
        _state.value = _state.value.copy(
            appearance = _state.value.appearance.copy(
                useCustomFont = !_state.value.appearance.useCustomFont
            )
        )
        updateAppearance(_state.value.appearance)
    }

    fun updateCustomFont(fontName: String) {
        _state.value = _state.value.copy(
            appearance = _state.value.appearance.copy(
                customFontName = fontName
            )
        )
        updateAppearance(_state.value.appearance)
    }
}

data class SettingsState(
    val frequency: Int = 15,
    val notificationStyle: NotificationStyle = NotificationStyle.DEFAULT,
    val hasNotificationPermission: Boolean = false,
    val isDarkModePreview: Boolean = false,
    val appearance: NotificationAppearance = NotificationAppearance()
)

enum class NotificationStyle {
    DEFAULT,
    COMPACT,
    EXPANDED,
    CUSTOM
}

data class NotificationAppearance(
    val showArabicText: Boolean = true,
    val showTranslation: Boolean = true,
    val showSurahInfo: Boolean = true,
    val showTranslatorInfo: Boolean = true,
    val fontSize: Int = 16,
    val useCustomFont: Boolean = false,
    val customFontName: String = "Poppins",
    val backgroundColor: Color = Color.Transparent,
    val textColor: Color = Color.Transparent,
    val accentColor: Color = Color.Transparent
) 