package com.satsapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.satsapp.presentation.WalletViewModel
import com.satsapp.ui.theme.PrimaryButton

/**
 * PayScreen - Payment screen matching iOS design
 * 
 * Shows payment options and allows user to send tokens
 * 
 * Features:
 * - Large send icon and amount display
 * - Pay via options (Link, Username, QR Code, NFC)
 * - Memo field
 * - Viewable by recipient checkbox
 * - Pay Bitcoin button
 */
@Composable
fun PayScreen(
    amount: ULong,
    viewModel: WalletViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.QR_CODE) }
    var memo by remember { mutableStateOf("") }
    var viewableByRecipient by remember { mutableStateOf(false) }
    var showQRCode by remember { mutableStateOf(false) }
    
    // Show QR code if user taps Pay Bitcoin
    if (showQRCode) {
        PayQRCodeScreen(
            amount = amount,
            memo = memo,
            viewModel = viewModel,
            onNavigateBack = { showQRCode = false }
        )
        return
    }
    
    // Main payment screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // SEND ICON
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )
        }
        
        // AMOUNT DISPLAY
        Text(
            text = "$amount sat",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        
        // PAY VIA SECTION
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Pay via",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Start)
            )
            
            // Payment method options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PaymentMethodOption(
                    method = PaymentMethod.LINK,
                    isSelected = selectedPaymentMethod == PaymentMethod.LINK,
                    onClick = { selectedPaymentMethod = PaymentMethod.LINK }
                )
                
                PaymentMethodOption(
                    method = PaymentMethod.USERNAME,
                    isSelected = selectedPaymentMethod == PaymentMethod.USERNAME,
                    onClick = { selectedPaymentMethod = PaymentMethod.USERNAME }
                )
                
                PaymentMethodOption(
                    method = PaymentMethod.QR_CODE,
                    isSelected = selectedPaymentMethod == PaymentMethod.QR_CODE,
                    onClick = { selectedPaymentMethod = PaymentMethod.QR_CODE }
                )
                
                PaymentMethodOption(
                    method = PaymentMethod.NFC,
                    isSelected = selectedPaymentMethod == PaymentMethod.NFC,
                    onClick = { selectedPaymentMethod = PaymentMethod.NFC }
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // MEMO FIELD
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Memo",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            BasicTextField(
                value = memo,
                onValueChange = { memo = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                decorationBox = { innerTextField ->
                    if (memo.isEmpty()) {
                        Text(
                            text = "Add a note...",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            )
        }
        
        // VIEWABLE BY RECIPIENT CHECKBOX
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = viewableByRecipient,
                onCheckedChange = { viewableByRecipient = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Viewable by recipient",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        // PAY BITCOIN BUTTON
        PrimaryButton(
            onClick = { 
                if (selectedPaymentMethod == PaymentMethod.QR_CODE) {
                    showQRCode = true
                }
                // TODO: Handle other payment methods
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Pay Bitcoin",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

/**
 * PaymentMethodOption - Individual payment method button
 */
@Composable
private fun PaymentMethodOption(
    method: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Icon button
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = method.icon,
                contentDescription = method.displayName,
                tint = if (isSelected) Color.White else Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Label
        Text(
            text = method.displayName,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * PayQRCodeScreen - Shows QR code for payment
 */
@Composable
private fun PayQRCodeScreen(
    amount: ULong,
    memo: String,
    viewModel: WalletViewModel,
    onNavigateBack: () -> Unit
) {
    val sendState by viewModel.sendState.collectAsState()
    
    // Generate Cashu token when screen appears
    LaunchedEffect(Unit) {
        viewModel.sendTokens(amount)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (sendState.isProcessing) {
            // Loading state
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Generating payment...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else if (sendState.error != null) {
            // Error state
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Error: ${sendState.error}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
        } else if (sendState.isCompleted && sendState.token != null) {
            // QR Code display
            Text(
                text = "Scan to receive $amount sat",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            
            // TODO: Generate and display QR code from token
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "QR Code\n${sendState.token}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
            
            Text(
                text = "Share this QR code with the recipient",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

/**
 * PaymentMethod - Available payment methods
 */
enum class PaymentMethod(
    val displayName: String,
    val icon: ImageVector
) {
    LINK("Link", Icons.Default.Link),
    USERNAME("Username", Icons.Default.Person),
    QR_CODE("QR Code", Icons.Default.QrCode),
    NFC("NFC", Icons.Default.Nfc)
}
