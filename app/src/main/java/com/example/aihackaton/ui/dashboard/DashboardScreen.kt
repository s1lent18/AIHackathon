package com.example.aihackaton.ui.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.aihackaton.ui.components.shimmerBrush
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.example.aihackaton.ui.theme.CircuitTeal
import com.example.aihackaton.ui.theme.Lexend
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    "Executive Portfolio Dashboard",
                    fontFamily = Lexend
                )
            })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                },
                label = "DashboardStateAnimation",
                modifier = Modifier.fillMaxSize()
            ) { state ->
                when (state) {
                    is DashboardState.Loading -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Spacer(modifier = Modifier.weight(1f).height(100.dp).background(shimmerBrush(), shape = RoundedCornerShape(12.dp)))
                                    Spacer(modifier = Modifier.weight(1f).height(100.dp).background(shimmerBrush(), shape = RoundedCornerShape(12.dp)))
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.width(180.dp).height(24.dp).background(shimmerBrush(), shape = RoundedCornerShape(4.dp)))
                                Spacer(modifier = Modifier.height(16.dp))
                                Spacer(modifier = Modifier.fillMaxWidth().height(200.dp).background(shimmerBrush(), shape = RoundedCornerShape(12.dp)))
                            }
                            item {
                                Spacer(modifier = Modifier.width(220.dp).height(24.dp).background(shimmerBrush(), shape = RoundedCornerShape(4.dp)))
                            }
                            items(4) {
                                Spacer(modifier = Modifier.fillMaxWidth().height(80.dp).background(shimmerBrush(), shape = RoundedCornerShape(12.dp)))
                            }
                        }
                    }
                    is DashboardState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            Text(
                                text = "Error: ${state.message}",
                                fontFamily = Lexend,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    is DashboardState.Success -> {
                        val data = state.data
                        
                        // Simple refresh mapping (Normally we'd use PullToRefreshBox from m3, but we can just use a button or assume it's loaded)
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                KPICards(
                                    totalValue = data.portfolio.totalValue,
                                    cashBalance = data.portfolio.cashBalance,
                                    nav = data.portfolio.totalValue - data.portfolio.cashBalance
                                )
                            }
                            
                            item {
                                Text(
                                    "Performance Trend",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontFamily = Lexend
                                )
                                if (data.history.isNotEmpty()) {
                                    // Simple mapping to Vico chart entries
                                    val chartEntryModel = entryModelOf(*data.history.map { it.totalValue.toFloat() }.toTypedArray())
                                    // Map index (x-value) to snapshotDate string
                                    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                                        val index = value.toInt()
                                        if (index >= 0 && index < data.history.size) {
                                            // Take the first 10 characters (YYYY-MM-DD)
                                            data.history[index].snapshotDate.take(10)
                                        } else {
                                            ""
                                        }
                                    }
                                    Chart(
                                        chart = lineChart(
                                            lines = listOf(
                                                com.patrykandpatrick.vico.compose.chart.line.lineSpec(
                                                    lineColor = CircuitTeal
                                                )
                                            )
                                        ),
                                        model = chartEntryModel,
                                        startAxis = rememberStartAxis(),
                                        bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisValueFormatter),
                                    )
                                } else {
                                    Text("No history data available.",
                                        fontFamily = Lexend)
                                }
                            }
                            
                            item {
                                Text(
                                    "Asset Allocation Matrix",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontFamily = Lexend
                                )
                            }
                            
                            items(data.positions) { position ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    PaddingValues(16.dp)
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            position.symbol,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontFamily = Lexend
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                "Shares: ${position.shares}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontFamily = Lexend
                                            )
                                            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                                                Text(
                                                    "Avg Price: ${formatCurrency(position.averagePrice)}",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontFamily = Lexend
                                                )
                                                position.livePrice?.let { livePrice ->
                                                    val color = if (livePrice > position.averagePrice) {
                                                        androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                                    } else if (livePrice < position.averagePrice) {
                                                        androidx.compose.ui.graphics.Color(0xFFF44336)
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurface
                                                    }
                                                    Text(
                                                        text = "Live Price: ${formatCurrency(livePrice)}",
                                                        color = color,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = Lexend
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatCurrency(amount: Double): String {
    val locale = Locale("en", "PK") // Pakistan
    val format = NumberFormat.getCurrencyInstance(locale)
    format.currency = Currency.getInstance("PKR")
    return format.format(amount)
}

@Composable
fun KPICards(totalValue: Double, cashBalance: Double, nav: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        KPICard(title = "Total Value", value = formatCurrency(totalValue), modifier = Modifier.weight(1f))
        KPICard(title = "Cash Balance", value = formatCurrency(cashBalance), modifier = Modifier.weight(1f))
    }
}

@Composable
fun KPICard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = com.example.aihackaton.ui.theme.HorizonNavy,
            contentColor = androidx.compose.ui.graphics.Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
        }
    }
}
