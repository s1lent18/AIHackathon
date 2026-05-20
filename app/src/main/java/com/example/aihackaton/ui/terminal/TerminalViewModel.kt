package com.example.aihackaton.ui.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihackaton.data.model.AnalyzeSignalResponse
import com.example.aihackaton.data.repository.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TerminalState {
    object Idle : TerminalState()
    object Loading : TerminalState()
    data class Success(val response: AnalyzeSignalResponse) : TerminalState()
    data class Error(val message: String) : TerminalState()
}

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val repository: MarketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TerminalState>(TerminalState.Idle)
    val uiState: StateFlow<TerminalState> = _uiState
    
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText
    
    private val _documentUrl = MutableStateFlow("")
    val documentUrl: StateFlow<String> = _documentUrl

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun updateDocumentUrl(url: String) {
        _documentUrl.value = url
    }

    fun analyzeSignal() {
        val input = _inputText.value
        val url = _documentUrl.value
        if (input.isBlank() && url.isBlank()) return
        
        _uiState.value = TerminalState.Loading
        viewModelScope.launch {
            val result = repository.analyzeSignal("aliraza-agent-test", input, url.takeIf { it.isNotBlank() })
            result.onSuccess { response ->
                _uiState.value = TerminalState.Success(response)
            }
            result.onFailure { error ->
                _uiState.value = TerminalState.Error(error.localizedMessage ?: "Unknown error")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = TerminalState.Idle
        _inputText.value = ""
        _documentUrl.value = ""
    }
}
