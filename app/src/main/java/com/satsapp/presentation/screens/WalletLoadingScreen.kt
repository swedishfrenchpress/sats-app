package com.satsapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.satsapp.presentation.WalletViewModel
import com.satsapp.ui.theme.PrimaryButton

/**
 * WalletLoadingScreen - Shows wallet initialization progress
 * 
 * This is the Kotlin equivalent of WalletLoadingView.swift
 * 
 * iOS Code (Swift):
 * ```swift
 * if walletManager.isLoading {
 *     ProgressView()
 *     Text("Initializing Wallet...")
 * } else if let error = walletManager.initializationError {
 *     Image(systemName: "exclamationmark.triangle.fill")
 *     Text("Failed to Initialize Wallet")
 *     Button("Retry") { walletManager.retryInitialization() }
 * }
 * ```
 * 
 * Key Differences:
 * - iOS .scaleEffect(1.5) → Android Modifier.size() to control size
 * - iOS .progressViewStyle(CircularProgressViewStyle(tint: .orange))
 *   → Android CircularProgressIndicator with color parameter
 * - iOS if let error = ... → Android walletState.error?.let { error -> }
 */
@Composable
fun WalletLoadingScreen(
    viewModel: WalletViewModel
) {
    // Collect wallet state (isLoading, error, etc.)
    val walletState by viewModel.walletState.collectAsState()
    
    // Center everything on the screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        // Show loading or error based on state
        if (walletState.isLoading) {
            // LOADING STATE
            LoadingContent(mintUrl = "Default Mint") // TODO: Get actual mint URL
        } else if (walletState.error != null) {
            // ERROR STATE
            ErrorContent(
                error = walletState.error!!,
                onRetry = { viewModel.initializeWallet() }
            )
        }
    }
}

/**
 * LoadingContent - Spinning indicator with text
 * 
 * Shows a progress indicator while wallet initializes
 */
@Composable
private fun LoadingContent(mintUrl: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Circular progress indicator (iOS ProgressView)
        // .scaleEffect(1.5) in iOS → size(60.dp) in Android
        CircularProgressIndicator(
            modifier = Modifier.size(60.dp),
            color = MaterialTheme.colorScheme.primary, // Orange color
            strokeWidth = 4.dp
        )
        
        // "Initializing Wallet..." text
        // iOS: .font(.headline)
        Text(
            text = "Initializing Wallet...",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        // Mint URL text
        // iOS: .font(.caption).foregroundColor(.secondary)
        Text(
            text = "Connecting to $mintUrl",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

/**
 * ErrorContent - Error display with retry button
 * 
 * Shows when wallet initialization fails
 */
@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Error icon
        // iOS: Image(systemName: "exclamationmark.triangle.fill")
        // Android: Use Material Icons
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(50.dp),
            tint = Color.Red
        )
        
        // Error title
        // iOS: .font(.headline)
        Text(
            text = "Failed to Initialize Wallet",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        // Error message
        // iOS: .font(.subheadline).foregroundColor(.secondary)
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        
        // Retry button
        // iOS: Button with fixed width/height and orange background
        // Android: Use our custom PrimaryButton component!
        PrimaryButton(
            onClick = onRetry,
            modifier = Modifier.width(120.dp)
        ) {
            Text("Retry")
        }
    }
}

/*
 * LEARNING NOTES:
 * 
 * 1. OPTIONAL HANDLING:
 *    iOS: if let error = walletManager.initializationError { ... }
 *    Android: if (walletState.error != null) { ... }
 *    
 *    iOS uses "optional binding" (if let)
 *    Android checks for null explicitly
 * 
 * 2. PROGRESS INDICATORS:
 *    iOS: ProgressView().scaleEffect(1.5)
 *    Android: CircularProgressIndicator(modifier = Modifier.size(60.dp))
 *    
 *    Both show a spinning circle, but sized differently
 * 
 * 3. SF SYMBOLS vs MATERIAL ICONS:
 *    iOS: Image(systemName: "exclamationmark.triangle.fill")
 *    Android: Icon(imageVector = Icons.Default.Warning)
 *    
 *    iOS uses SF Symbols (Apple's icon system)
 *    Android uses Material Icons (Google's icon system)
 * 
 * 4. SPACING:
 *    iOS: VStack(spacing: 16) { ... }
 *    Android: Column(verticalArrangement = Arrangement.spacedBy(16.dp)) { ... }
 *    
 *    Same concept, different syntax!
 */

