package com.satsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.satsapp.presentation.WalletViewModel
import com.satsapp.ui.theme.SatsAppTheme

/**
 * Main Activity - Entry point for the app
 * Sets up Jetpack Compose UI
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SatsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WalletScreen()
                }
            }
        }
    }
}

/**
 * Basic wallet screen to demonstrate backend integration
 * Replace this with your custom UI design
 */
@Composable
fun WalletScreen(viewModel: WalletViewModel = viewModel()) {
    val walletState by viewModel.walletState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Cashu Wallet",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (walletState.isInitialized) {
            Text(
                text = "Balance: ${walletState.balance} sats",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (walletState.isLoading) {
                CircularProgressIndicator()
            }

            walletState.error?.let { error ->
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { viewModel.refreshBalance() }) {
                Text("Refresh Balance")
            }
        } else {
            Text(
                text = "Wallet not initialized",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.initializeWallet() },
                enabled = !walletState.isLoading
            ) {
                Text("Initialize Wallet")
            }

            if (walletState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            walletState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Display mnemonic after initialization (for backup purposes)
        walletState.mnemonic?.let { mnemonic ->
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Backup your mnemonic:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = mnemonic,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
