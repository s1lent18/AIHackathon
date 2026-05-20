package com.example.aihackaton.data.model

import com.google.gson.annotations.SerializedName

// POST /api/analysis/analyze-signal
data class AnalyzeSignalRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("unstructuredInput") val unstructuredInput: String,
    @SerializedName("documentUrl") val documentUrl: String? = null
)

data class AnalyzeSignalResponse(
    @SerializedName("insightExtraction") val insightExtraction: String,
    @SerializedName("impactAnalysis") val impactAnalysis: String,
    @SerializedName("executionPlan") val executionPlan: ExecutionPlan,
    @SerializedName("tradeResult") val tradeResult: TradeResult
)

data class ExecutionPlan(
    @SerializedName("action") val action: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("reasoning") val reasoning: String
)

data class TradeResult(
    @SerializedName("message") val message: String,
    @SerializedName("portfolio") val portfolio: PortfolioSummary,
    @SerializedName("positions") val positions: List<PositionSummary>
)

data class PortfolioSummary(
    @SerializedName("cashBalance") val cashBalance: Double,
    @SerializedName("totalValue") val totalValue: Double
)

data class PositionSummary(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("shares") val shares: Int,
    @SerializedName("averagePrice") val averagePrice: Double
)

// GET /api/analysis/dashboard/:userId
data class DashboardResponse(
    @SerializedName("portfolio") val portfolio: PortfolioDetail,
    @SerializedName("positions") val positions: List<PositionDetail>,
    @SerializedName("tradeLogs") val tradeLogs: List<TradeLog>,
    @SerializedName("history") val history: List<HistorySnapshot>
)

data class PortfolioDetail(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("cashBalance") val cashBalance: Double,
    @SerializedName("totalValue") val totalValue: Double,
    @SerializedName("riskScore") val riskScore: Double
)

data class PositionDetail(
    @SerializedName("id") val id: String,
    @SerializedName("portfolioId") val portfolioId: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("shares") val shares: Int,
    @SerializedName("averagePrice") val averagePrice: Double,
    @SerializedName("updatedAt") val updatedAt: String,
    var livePrice: Double? = null
)

data class TradeLog(
    @SerializedName("id") val id: String,
    @SerializedName("portfolioId") val portfolioId: String,
    @SerializedName("action") val action: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price") val price: Double,
    @SerializedName("reasoning") val reasoning: String,
    @SerializedName("createdAt") val createdAt: String
)

data class HistorySnapshot(
    @SerializedName("snapshotDate") val snapshotDate: String,
    @SerializedName("totalValue") val totalValue: Double,
    @SerializedName("cashBalance") val cashBalance: Double,
    @SerializedName("assetValue") val assetValue: Double
)

data class LivePriceDto(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("price") val price: Double
)
