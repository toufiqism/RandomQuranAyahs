package com.toufiq.randomquranayahs.ui.state

import com.toufiq.randomquranayahs.data.model.QuranData

sealed class QuranUiState {
    data object Initial : QuranUiState()
    data object Loading : QuranUiState()
    data class Success(val data: QuranData) : QuranUiState()
    data class Error(val message: String) : QuranUiState()
}

data class QuranState(
    val uiState: QuranUiState = QuranUiState.Initial,
    val isLoading: Boolean = false,
    val error: String? = null,
    val ayah: QuranData? = null
) 