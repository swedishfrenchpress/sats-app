package com.satsapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.satsapp.presentation.WalletViewModel
import com.satsapp.ui.theme.PrimaryButton
import com.satsapp.ui.theme.SecondaryButton

/**
 * SettingsScreen - Configure app settings
 * 
 * Features:
 * - View/Edit mint URL
 * - View wallet mnemonic (for backup)
 * - App version info
 * - Clear wallet data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: WalletViewModel,
    onNavigateBack: () -> Unit
) {
    val walletState by viewModel.walletState.collectAsState()
    var mintUrl by remember { mutableStateOf("https://fake.thesimplekid.dev") }
    var showMnemonic by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // MINT URL SECTION
            SettingsSection(title = "Mint Configuration") {
                Text(
                    text = "Mint URL",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                OutlinedTextField(
                    value = mintUrl,
                    onValueChange = { mintUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("https://mint.example.com") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                    )
                )
                
                Text(
                    text = "Current: $mintUrl",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                PrimaryButton(
                    onClick = {
                        // TODO: Update mint URL in repository
                        viewModel.updateMintUrl(mintUrl)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Mint URL")
                }
            }
            
            // WALLET BACKUP SECTION
            SettingsSection(title = "Wallet Backup") {
                Text(
                    text = "Recovery Phrase",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "Save this phrase to recover your wallet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                if (showMnemonic) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = walletState.mnemonic ?: "Not available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                
                SecondaryButton(
                    onClick = { showMnemonic = !showMnemonic },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (showMnemonic) "Hide Recovery Phrase" else "Show Recovery Phrase")
                }
            }
            
            // WALLET INFO SECTION
            SettingsSection(title = "Wallet Info") {
                InfoRow("Balance", "${walletState.balance} sat")
                InfoRow("Status", if (walletState.isInitialized) "Initialized" else "Not initialized")
                InfoRow("Mint", mintUrl)
            }
            
            // MINT INFO SECTION (Debug/Verification)
            MintInfoSection(viewModel = viewModel)
            
            // DANGER ZONE
            SettingsSection(
                title = "Danger Zone",
                color = MaterialTheme.colorScheme.error
            ) {
                Text(
                    text = "This will delete all wallet data",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                
                SecondaryButton(
                    onClick = {
                        // TODO: Implement wallet reset
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Reset Wallet",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            // VERSION INFO
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "Sats App v1.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

/**
 * SettingsSection - Reusable section container
 */
@Composable
private fun SettingsSection(
    title: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = color
        )
        
        content()
    }
}

/**
 * InfoRow - Key-value display row
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/**
 * MintInfoSection - Displays mint information from /v1/info endpoint
 * 
 * Shows mint name, version, pubkey, and description to verify connection
 */
@Composable
private fun MintInfoSection(viewModel: WalletViewModel) {
    val mintInfoState by viewModel.mintInfo.collectAsState()
    
    SettingsSection(title = "Mint Information") {
        Text(
            text = "Verify mint connection and details",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        
        PrimaryButton(
            onClick = { viewModel.fetchMintInfo() },
            enabled = !mintInfoState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (mintInfoState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Fetch Mint Info")
            }
        }
        
        // Show error if any
        if (mintInfoState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Error: ${mintInfoState.error}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        
        // Show mint info if loaded
        if (mintInfoState.info != null) {
            Spacer(modifier = Modifier.height(16.dp))
            
            val info = mintInfoState.info!!
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mint URL
                    if (mintInfoState.mintUrl != null) {
                        Text(
                            text = "URL: ${mintInfoState.mintUrl}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Mint Name
                    if (info.name != null) {
                        InfoRow("Name", info.name!!)
                    }
                    
                    // Version
                    if (info.version != null) {
                        InfoRow("Version", info.version.toString())
                    }
                    
                    // Description
                    if (info.description != null) {
                        Text(
                            text = "Description:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = info.description!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    
                    // Pubkey (shortened)
                    if (info.pubkey != null) {
                        Text(
                            text = "Pubkey:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "${info.pubkey!!.take(20)}...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    
                    // Connection Status
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✅ Connected to mint successfully!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4CAF50) // Green
                    )
                }
            }
        }
    }
}

