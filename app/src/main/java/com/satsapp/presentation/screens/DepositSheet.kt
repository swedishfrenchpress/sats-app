package com.satsapp.presentation.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
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
        onDismissRequest = {
            onDismiss()
            viewModel.resetMintState() // Clear state when dismissed
        },
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
            // Show QR code if invoice was generated, otherwise show input form
            if (mintState.paymentRequest != null) {
                // QR CODE DISPLAY (matches screenshot)
                DepositQRCodeView(
                    amount = mintState.amount,
                    invoice = mintState.paymentRequest!!,
                    viewModel = viewModel
                )
            } else {
                // AMOUNT INPUT FORM
                DepositInputForm(
                    amount = amount,
                    onAmountChange = { amount = it },
                    onGenerate = {
                        val amountValue = amount.toULongOrNull() ?: 0u
                        if (amountValue > 0u) {
                            viewModel.createMintQuote(amountValue)
                        }
                    },
                    isProcessing = mintState.isProcessing,
                    error = mintState.error
                )
            }
        }
    }
}

/**
 * DepositInputForm - Amount input before generating invoice
 */
@Composable
private fun DepositInputForm(
    amount: String,
    onAmountChange: (String) -> Unit,
    onGenerate: () -> Unit,
    isProcessing: Boolean,
    error: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
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
                        color = MaterialTheme.colorScheme.primary,
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
                            if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                                onAmountChange(newValue)
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
        
        // Generate Button
        PrimaryButton(
            onClick = onGenerate,
            enabled = amount.isNotEmpty() && !isProcessing,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Generate Deposit Request")
            }
        }
        
        // Show error
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * DepositQRCodeView - Shows QR code after invoice generation (matches screenshot)
 */
@Composable
private fun DepositQRCodeView(
    amount: ULong,
    invoice: String,
    viewModel: WalletViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Orange circle with + icon (matches screenshot)
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Deposit",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        
        // "Deposit X sat" title
        Text(
            text = "Deposit $amount sat",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        // QR Code
        val qrBitmap = remember(invoice) { generateQRCode(invoice) }
        if (qrBitmap != null) {
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "Lightning Invoice QR Code",
                modifier = Modifier
                    .size(280.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            )
        }
        
        // "Deposit Pending" status (matches screenshot)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            // Loading dots indicator
            Text(
                text = "●●●",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Deposit Pending",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        
        // Invoice string (at bottom, gray text)
        Text(
            text = invoice,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

/**
 * Generate QR code bitmap from string
 */
private fun generateQRCode(content: String, size: Int = 512): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x, y,
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK
                    else android.graphics.Color.WHITE
                )
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}

