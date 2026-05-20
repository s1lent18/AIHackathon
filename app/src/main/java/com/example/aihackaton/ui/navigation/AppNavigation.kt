package com.example.aihackaton.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aihackaton.ui.dashboard.DashboardScreen
import com.example.aihackaton.ui.history.HistoryScreen
import com.example.aihackaton.ui.terminal.TerminalScreen

sealed class Screen(val route: String, val title: String, val icon: @Composable () -> Unit) {
    object Terminal : Screen("terminal", "Terminal", { Icon(Icons.Filled.Terminal, contentDescription = "Terminal") })
    object Dashboard : Screen("dashboard", "Dashboard", { Icon(Icons.Filled.Dashboard, contentDescription = "Dashboard") })
    object History : Screen("history", "History", { Icon(Icons.Filled.History, contentDescription = "History") })
}

val items = listOf(
    Screen.Terminal,
    Screen.Dashboard,
    Screen.History
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = com.example.aihackaton.ui.theme.HorizonNavy
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = screen.icon,
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = androidx.compose.ui.graphics.Color.White,
                            unselectedIconColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f),
                            selectedTextColor = androidx.compose.ui.graphics.Color.White,
                            unselectedTextColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f),
                            indicatorColor = com.example.aihackaton.ui.theme.CircuitTeal.copy(alpha = 0.3f)
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Terminal.route, Modifier.padding(innerPadding)) {
            composable(Screen.Terminal.route) { TerminalScreen() }
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.History.route) { HistoryScreen() }
        }
    }
}
