package com.example.aihackaton.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aihackaton.data.model.DashboardResponse
import com.example.aihackaton.data.repository.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val data: DashboardResponse) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: MarketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val uiState: StateFlow<DashboardState> = _uiState
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        fetchDashboard()
    }

    fun fetchDashboard(isRefresh: Boolean = false) {
        if (isRefresh) {
            _isRefreshing.value = true
        } else {
            _uiState.value = DashboardState.Loading
        }
        
        viewModelScope.launch {
            val result = repository.getDashboard("aliraza-agent-test", isRefresh)
            result.onSuccess { data ->
                _uiState.value = DashboardState.Success(data)
                _isRefreshing.value = false
                
                // Fetch live prices in parallel to avoid blocking the initial dashboard render
                val livePricesResult = repository.getLivePrices("aliraza-agent-test")
                livePricesResult.onSuccess { livePrices ->
                    val currentState = _uiState.value
                    if (currentState is DashboardState.Success) {
                        val priceMap = livePrices.associateBy { it.symbol }
                        val updatedPositions = currentState.data.positions.map { pos ->
                            pos.copy(livePrice = priceMap[pos.symbol]?.price ?: pos.livePrice)
                        }
                        _uiState.value = DashboardState.Success(currentState.data.copy(positions = updatedPositions))
                    }
                }
            }
            result.onFailure { error ->
                _uiState.value = DashboardState.Error(error.localizedMessage ?: "Unknown error")
                _isRefreshing.value = false
            }
        }
    }
}
