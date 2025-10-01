package com.satsapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.satsapp.presentation.WalletViewModel
import com.satsapp.ui.theme.PrimaryButton

/**
 * DepositSheet - Bottom sheet for requesting Lightning deposits
 * 
 * Matches the iOS DepositSheetView design:
 * - Cream/beige background
 * - Amount input field with orange border
 * - Orange "Generate Deposit Request" button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositSheet(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    val mintState by viewModel.mintState.collectAsState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFF5F1E8), // Cream/beige background from screenshot
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Title
            Text(
                text = "Enter Deposit Amount",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            
            // Amount Input Field (Orange border, white background)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Amount",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary, // Orange border
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = amount,
                            onValueChange = { newValue ->
                                // Only allow numbers
                                if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                                    amount = newValue
                                }
                            },
                            modifier = Modifier.weight(1f),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                if (amount.isEmpty()) {
                                    Text(
                                        text = "0",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Gray
                                    )
                                }
                                innerTextField()
                            }
                        )
                        
                        Text(
                            text = "sat",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            // Generate Button (Orange, full width)
            PrimaryButton(
                onClick = {
                    val amountValue = amount.toULongOrNull() ?: 0u
                    if (amountValue > 0u) {
                        viewModel.createMintQuote(amountValue)
                        // TODO: Show QR code or invoice after generation
                    }
                },
                enabled = amount.isNotEmpty() && amount.toULongOrNull() != null && !mintState.isProcessing,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (mintState.isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Generate Deposit Request")
                }
            }
            
            // Show error if any
            if (mintState.error != null) {
                Text(
                    text = mintState.error!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            // Show invoice if generated
            if (mintState.paymentRequest != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Lightning Invoice Generated!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // TODO: Add QR code display here
                    
                    Text(
                        text = mintState.paymentRequest!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 3
                    )
                }
            }
        }
    }
}

