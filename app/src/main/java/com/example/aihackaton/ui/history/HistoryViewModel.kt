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

enum class TradeFilter {
    ALL, BUY_ONLY, SELL_ONLY
}

sealed class HistoryState {
    object Loading : HistoryState()
    data class Success(
        val logs: List<TradeLog>,
        val filteredLogs: List<TradeLog>,
        val totalBuyValue: Double,
        val totalSellValue: Double,
        val netMargin: Double
    ) : HistoryState()
    data class Error(val message: String) : HistoryState()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: MarketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryState>(HistoryState.Loading)
    val uiState: StateFlow<HistoryState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _currentFilter = MutableStateFlow(TradeFilter.ALL)
    val currentFilter: StateFlow<TradeFilter> = _currentFilter

    private var allLogs: List<TradeLog> = emptyList()

    init {
        fetchHistory()
    }

    fun fetchHistory() {
        _uiState.value = HistoryState.Loading
        viewModelScope.launch {
            val result = repository.getDashboard("aliraza-agent-test")
            result.onSuccess { data ->
                allLogs = data.tradeLogs
                updateState()
            }
            result.onFailure { error ->
                _uiState.value = HistoryState.Error(error.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        updateState()
    }

    fun updateFilter(filter: TradeFilter) {
        _currentFilter.value = filter
        updateState()
    }

    private fun updateState() {
        val query = _searchQuery.value
        val filter = _currentFilter.value

        var filtered = allLogs
        if (query.isNotBlank()) {
            filtered = filtered.filter { it.symbol.contains(query, ignoreCase = true) }
        }
        
        filtered = when (filter) {
            TradeFilter.BUY_ONLY -> filtered.filter { it.action.equals("BUY", ignoreCase = true) }
            TradeFilter.SELL_ONLY -> filtered.filter { it.action.equals("SELL", ignoreCase = true) }
            TradeFilter.ALL -> filtered
        }

        val totalBuyValue = filtered.filter { it.action.equals("BUY", ignoreCase = true) }.sumOf { it.price * it.quantity }
        val totalSellValue = filtered.filter { it.action.equals("SELL", ignoreCase = true) }.sumOf { it.price * it.quantity }
        val netMargin = totalSellValue - totalBuyValue

        _uiState.value = HistoryState.Success(
            logs = allLogs,
            filteredLogs = filtered,
            totalBuyValue = totalBuyValue,
            totalSellValue = totalSellValue,
            netMargin = netMargin
        )
    }
}
