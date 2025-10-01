package com.satsapp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.satsapp.presentation.WalletViewModel
import com.satsapp.ui.theme.SatsAppTheme

/**
 * ContentScreen - Main app container
 * 
 * This is the Kotlin equivalent of ContentView.swift
 * 
 * iOS Code (Swift):
 * ```swift
 * if walletManager.isInitialized {
 *     TabView {
 *         TransactView().tabItem { ... }
 *         ActivityView().tabItem { ... }
 *     }
 * } else {
 *     WalletLoadingView()
 * }
 * ```
 * 
 * Key Differences:
 * - iOS uses TabView → Android uses NavigationBar (bottom navigation)
 * - iOS @EnvironmentObject → Android ViewModel with remember/collectAsState
 * - iOS conditional rendering is same → if/else works the same way!
 */
@Composable
fun ContentScreen(
    viewModel: WalletViewModel = viewModel()
) {
    // Collect wallet state from ViewModel
    // This is like @EnvironmentObject in iOS
    val walletState by viewModel.walletState.collectAsState()
    
    // Show loading screen or main content based on initialization state
    if (walletState.isInitialized) {
        // Main app with bottom navigation
        MainAppWithTabs(viewModel = viewModel)
    } else {
        // Loading screen while wallet initializes
        WalletLoadingScreen(viewModel = viewModel)
    }
}

/**
 * MainAppWithTabs - Tab navigation container
 * 
 * This creates a bottom navigation bar with two tabs:
 * 1. Transact - Send/receive money
 * 2. Activity - Transaction history
 * 
 * In iOS, TabView automatically creates the tab bar.
 * In Android, we use Scaffold with NavigationBar.
 */
@Composable
private fun MainAppWithTabs(
    viewModel: WalletViewModel
) {
    // Track which tab is selected (0 = Transact, 1 = Activity)
    var selectedTab by remember { mutableIntStateOf(0) }
    
    // Scaffold provides the app structure (content + bottom bar)
    Scaffold(
        bottomBar = {
            // Bottom navigation bar (like iOS tabItem)
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                // Transact Tab
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.List, // You can change this icon
                            contentDescription = "Transact"
                        )
                    },
                    label = { Text("Transact") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                )
                
                // Activity Tab
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Activity"
                        )
                    },
                    label = { Text("Activity") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                )
            }
        }
    ) { paddingValues ->
        // Content area - shows different screen based on selected tab
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> TransactScreen(viewModel = viewModel)
                1 -> ActivityScreen(viewModel = viewModel)
            }
        }
    }
}

/*
 * LEARNING NOTES:
 * 
 * 1. STATE MANAGEMENT:
 *    iOS: @EnvironmentObject var walletManager
 *    Android: val walletState by viewModel.walletState.collectAsState()
 *    
 *    Both watch for changes and recompose/redraw when data changes!
 * 
 * 2. TABS:
 *    iOS: TabView { View1().tabItem {...} View2().tabItem {...} }
 *    Android: Scaffold with NavigationBar + when statement for content
 *    
 *    iOS handles tab switching automatically.
 *    Android requires manual state (selectedTab) to track current tab.
 * 
 * 3. CONDITIONAL UI:
 *    Both iOS and Android use if/else the same way!
 *    if (condition) { ShowThis() } else { ShowThat() }
 */

