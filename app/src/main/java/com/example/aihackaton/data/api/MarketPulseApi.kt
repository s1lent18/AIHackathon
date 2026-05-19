package com.example.aihackaton.data.api

import com.example.aihackaton.data.model.AnalyzeSignalRequest
import com.example.aihackaton.data.model.AnalyzeSignalResponse
import com.example.aihackaton.data.model.DashboardResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MarketPulseApi {

    @POST("/api/analysis/analyze-signal")
    suspend fun analyzeSignal(
        @Body request: AnalyzeSignalRequest
    ): Response<AnalyzeSignalResponse>

    @GET("/api/analysis/dashboard/{userId}")
    suspend fun getDashboard(
        @Path("userId") userId: String
    ): Response<DashboardResponse>
}
