package com.example.aihackaton.ui.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aihackaton.ui.theme.CircuitTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(viewModel: TerminalViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val documentUrl by viewModel.documentUrl.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Autonomous Ingestion Terminal", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = inputText,
            onValueChange = viewModel::updateInputText,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = { Text("Paste raw PSX news or economic text here...") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CircuitTeal,
                unfocusedBorderColor = CircuitTeal.copy(alpha = 0.5f),
                focusedTextColor = CircuitTeal,
                unfocusedTextColor = CircuitTeal
            )
        )

        OutlinedTextField(
            value = documentUrl,
            onValueChange = viewModel::updateDocumentUrl,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Document URL (e.g., PSX News Link)") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CircuitTeal,
                unfocusedBorderColor = CircuitTeal.copy(alpha = 0.5f),
                focusedTextColor = CircuitTeal,
                unfocusedTextColor = CircuitTeal
            )
        )

        Button(
            onClick = { viewModel.analyzeSignal() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = uiState !is TerminalState.Loading && (inputText.isNotBlank() || documentUrl.isNotBlank())
        ) {
            if (uiState is TerminalState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Execute Autonomous Agents")
            }
        }

        when (val state = uiState) {
            is TerminalState.Success -> {
                TerminalOutput(state)
            }
            is TerminalState.Error -> {
                Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}

@Composable
fun TerminalOutput(state: TerminalState.Success) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(">> AGENT 1: Insight Extraction", color = CircuitTeal, fontFamily = FontFamily.Monospace)
        Text(state.response.insightExtraction, color = Color.White, fontFamily = FontFamily.Monospace)
        
        HorizontalDivider(color = Color.DarkGray)
        
        Text(">> AGENT 2: Impact Analysis", color = CircuitTeal, fontFamily = FontFamily.Monospace)
        Text(state.response.impactAnalysis, color = Color.White, fontFamily = FontFamily.Monospace)
        
        HorizontalDivider(color = Color.DarkGray)
        
        Text(">> AGENT 3: Execution Plan", color = CircuitTeal, fontFamily = FontFamily.Monospace)
        Text("Action: ${state.response.executionPlan.action} ${state.response.executionPlan.quantity} ${state.response.executionPlan.symbol}", color = Color.White, fontFamily = FontFamily.Monospace)
        Text("Reasoning: ${state.response.executionPlan.reasoning}", color = Color.White, fontFamily = FontFamily.Monospace)
        
        HorizontalDivider(color = Color.DarkGray)
        
        Text(">> SYSTEM: Trade Result", color = CircuitTeal, fontFamily = FontFamily.Monospace)
        Text(state.response.tradeResult.message, color = Color.White, fontFamily = FontFamily.Monospace)
    }
}
