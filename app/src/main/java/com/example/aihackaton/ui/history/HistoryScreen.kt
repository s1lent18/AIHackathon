package com.example.aihackaton.ui.history

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.aihackaton.ui.components.shimmerBrush
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aihackaton.data.model.TradeLog
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.ui.unit.sp
import com.example.aihackaton.ui.theme.HorizonNavy
import com.example.aihackaton.ui.theme.CircuitTeal
import com.example.aihackaton.ui.theme.Lexend
import java.util.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(
                "Trade History Explorer",
                fontFamily = Lexend
            ) })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                },
                label = "HistoryStateAnimation",
                modifier = Modifier.fillMaxSize()
            ) { state ->
                when (state) {
                    is HistoryState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                repeat(3) {
                                    Spacer(modifier = Modifier.weight(1f).height(80.dp).background(shimmerBrush(), shape = RoundedCornerShape(12.dp)))
                                }
                            }
                            
                            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    repeat(3) {
                                         Spacer(modifier = Modifier.width(80.dp).height(32.dp).background(shimmerBrush(), shape = RoundedCornerShape(16.dp)))
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Spacer(modifier = Modifier.fillMaxWidth().height(56.dp).background(shimmerBrush(), shape = RoundedCornerShape(12.dp)))
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(5) {
                                    Spacer(modifier = Modifier.fillMaxWidth().height(100.dp).background(shimmerBrush(), shape = RoundedCornerShape(12.dp)))
                                }
                            }
                        }
                    }
                    is HistoryState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Error: ${state.message}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    is HistoryState.Success -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            PerformanceHeader(state)
                            
                            InteractiveControls(
                                searchQuery = searchQuery,
                                onSearchQueryChange = viewModel::updateSearchQuery,
                                currentFilter = currentFilter,
                                onFilterChange = viewModel::updateFilter
                            )

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.filteredLogs) { log ->
                                    TradeLogCard(log)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PerformanceHeader(state: HistoryState.Success) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PerformanceCard(title = "Total Buy", value = state.totalBuyValue, modifier = Modifier.weight(1f))
        PerformanceCard(title = "Total Sell", value = state.totalSellValue, modifier = Modifier.weight(1f))
        PerformanceCard(title = "Net Margin", value = state.netMargin, modifier = Modifier.weight(1f))
    }
}

@Composable
fun PerformanceCard(title: String, value: Double, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = HorizonNavy,
            contentColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f),
                fontFamily = Lexend)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = formatCurrency(value), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 8.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveControls(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    currentFilter: TradeFilter,
    onFilterChange: (TradeFilter) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChipItem("All Trades", currentFilter == TradeFilter.ALL) { onFilterChange(TradeFilter.ALL) }
            FilterChipItem("Buys", currentFilter == TradeFilter.BUY_ONLY) { onFilterChange(TradeFilter.BUY_ONLY) }
            FilterChipItem("Sells", currentFilter == TradeFilter.SELL_ONLY) { onFilterChange(TradeFilter.SELL_ONLY) }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by Symbol...") },
            trailingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CircuitTeal,
                unfocusedBorderColor = HorizonNavy.copy(alpha = 0.5f)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipItem(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label,
            fontFamily = Lexend) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = CircuitTeal,
            selectedLabelColor = Color.White,
        ),
        border = if (!selected) FilterChipDefaults.filterChipBorder(borderColor = HorizonNavy, enabled = true, selected = false) else null
    )
}

@Composable
fun TradeLogCard(log: TradeLog) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, HorizonNavy)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val actionColor = if (log.action == "BUY") Color(0xFF4CAF50) else Color(0xFFF44336)
                    Text(
                        text = log.action,
                        color = actionColor,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = log.symbol, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = HorizonNavy)
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = HorizonNavy
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Quantity: ${log.quantity}", style = MaterialTheme.typography.bodyMedium, color = Color.Black,
                    fontFamily = Lexend)
                Text("Price: ${formatCurrency(log.price)}", style = MaterialTheme.typography.bodyMedium, color = Color.Black,
                    fontFamily = Lexend)
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = HorizonNavy.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Agent Reasoning:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = HorizonNavy,
                        fontFamily = Lexend)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = log.reasoning, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray,
                        fontFamily = Lexend)
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
