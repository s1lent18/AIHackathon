package com.example.aihackaton.ui.dashboard

import androidx.compose.foundation.layout.*
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
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Executive Portfolio Dashboard") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is DashboardState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                }
                is DashboardState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                    )
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
                            Text("Performance Trend", style = MaterialTheme.typography.titleLarge)
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
                                    chart = lineChart(),
                                    model = chartEntryModel,
                                    startAxis = rememberStartAxis(),
                                    bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisValueFormatter),
                                )
                            } else {
                                Text("No history data available.")
                            }
                        }
                        
                        item {
                            Text("Asset Allocation Matrix", style = MaterialTheme.typography.titleLarge)
                        }
                        
                        items(data.positions) { position ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                PaddingValues(16.dp)
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(position.symbol, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text("Shares: ${position.shares}")
                                        Text("Avg Price: ${formatCurrency(position.averagePrice)}")
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
    val format = NumberFormat.getCurrencyInstance(Locale.US)
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
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}
