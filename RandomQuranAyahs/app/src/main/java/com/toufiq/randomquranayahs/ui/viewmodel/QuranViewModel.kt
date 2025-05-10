package com.toufiq.randomquranayahs.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toufiq.randomquranayahs.data.repository.QuranRepository
import com.toufiq.randomquranayahs.ui.state.QuranState
import com.toufiq.randomquranayahs.ui.state.QuranUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val repository: QuranRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuranState())
    val state: StateFlow<QuranState> = _state.asStateFlow()

    fun loadRandomAyah() {
        viewModelScope.launch {
            _state.update { it.copy(uiState = QuranUiState.Loading, isLoading = true) }
            
            repository.getRandomAyah()
                .onSuccess { response ->
                    _state.update { 
                        it.copy(
                            uiState = QuranUiState.Success(response.data),
                            isLoading = false,
                            ayah = response.data,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { 
                        it.copy(
                            uiState = QuranUiState.Error(error.message ?: "Unknown error occurred"),
                            isLoading = false,
                            error = error.message ?: "Unknown error occurred"
                        )
                    }
                }
        }
    }

    fun resetState() {
        _state.value = QuranState()
    }
} 