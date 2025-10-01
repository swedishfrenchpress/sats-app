package com.satsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.satsapp.presentation.screens.ContentScreen
import com.satsapp.ui.theme.SatsAppTheme

/**
 * Main Activity - Entry point for the app
 * 
 * This is where your Android app starts!
 * 
 * What happens here:
 * 1. onCreate() is called when the app launches
 * 2. setContent { } sets up the Compose UI
 * 3. SatsAppTheme applies your custom orange/gray theme
 * 4. ContentScreen shows the main app (tabs, screens, etc.)
 * 
 * ContentScreen handles:
 * - Checking if wallet is initialized
 * - Showing loading screen if not ready
 * - Showing main app with tabs if ready
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set up Compose UI
        setContent {
            // Apply your custom theme (orange primary, gray secondary)
            SatsAppTheme {
                // Full-screen surface with themed background
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Show the main app!
                    // ContentScreen handles:
                    // - WalletLoadingScreen (if wallet not initialized)
                    // - Main app with tabs (if wallet ready)
                    ContentScreen()
                }
            }
        }
    }
}

/*
 * WHAT CHANGED?
 * 
 * BEFORE:
 * - Simple WalletScreen() with basic buttons
 * - Manual initialization and refresh
 * - No navigation or tabs
 * 
 * NOW:
 * - ContentScreen() - Full app from iOS conversion!
 * - Automatic loading screen
 * - Tab navigation (Transact & Activity)
 * - All converted iOS screens available
 * 
 * YOUR APP NOW HAS:
 * ✅ Tab navigation (Transact, Activity)
 * ✅ Loading screen with error handling
 * ✅ Transaction screen with number pad
 * ✅ Activity/history screen
 * ✅ Auth screens (sign up, confirmation)
 * ✅ Custom theme matching iOS
 * 
 * TO TEST DIFFERENT SCREENS:
 * Replace ContentScreen() with:
 * - TransactScreen() - Just the transaction screen
 * - ActivityScreen() - Just the activity list
 * - AuthScreen() - Just the auth flow
 * - SignUpScreen(AuthViewModel()) - Just sign up
 */
