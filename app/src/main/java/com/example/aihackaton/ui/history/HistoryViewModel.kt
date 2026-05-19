package com.example.aihackaton.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihackaton.data.model.TradeLog
import com.example.aihackaton.data.repository.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HistoryState {
    object Loading : HistoryState()
    data class Success(val logs: List<TradeLog>) : HistoryState()
    data class Error(val message: String) : HistoryState()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: MarketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryState>(HistoryState.Loading)
    val uiState: StateFlow<HistoryState> = _uiState

    init {
        fetchHistory()
    }

    fun fetchHistory() {
        _uiState.value = HistoryState.Loading
        viewModelScope.launch {
            val result = repository.getDashboard("aliraza-agent-test")
            result.onSuccess { data ->
                _uiState.value = HistoryState.Success(data.tradeLogs)
            }
            result.onFailure { error ->
                _uiState.value = HistoryState.Error(error.localizedMessage ?: "Unknown error")
            }
        }
    }
}
