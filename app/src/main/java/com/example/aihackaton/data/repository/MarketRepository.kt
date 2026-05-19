package com.example.aihackaton.data.repository

import com.example.aihackaton.data.api.MarketPulseApi
import com.example.aihackaton.data.model.AnalyzeSignalRequest
import com.example.aihackaton.data.model.AnalyzeSignalResponse
import com.example.aihackaton.data.model.DashboardResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketRepository @Inject constructor(
    private val api: MarketPulseApi
) {
    private var cachedDashboard: DashboardResponse? = null
    suspend fun analyzeSignal(userId: String, input: String): Result<AnalyzeSignalResponse> = withContext(Dispatchers.IO) {
        try {
            val request = AnalyzeSignalRequest(userId = userId, unstructuredInput = input)
            val response = api.analyzeSignal(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    cachedDashboard = null // Invalidate cache on new signal
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDashboard(userId: String, forceRefresh: Boolean = false): Result<DashboardResponse> = withContext(Dispatchers.IO) {
        if (!forceRefresh && cachedDashboard != null) {
            return@withContext Result.success(cachedDashboard!!)
        }

        try {
            val response = api.getDashboard(userId)
            if (response.isSuccessful) {
                response.body()?.let {
                    cachedDashboard = it
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
