package com.satsapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.satsapp.presentation.WalletViewModel
import com.satsapp.ui.theme.PrimaryButton
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.common.BitMatrix

/**
 * Generate QR code bitmap from text
 */
private fun generateQRCodeBitmap(text: String, size: Int = 512): Bitmap {
    val writer = QRCodeWriter()
    val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
    val bitMatrix: BitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints)
    
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(
                x, y,
                if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            )
        }
    }
    
    return bitmap
}

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
@OptIn(ExperimentalMaterial3Api::class)
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
    
    // Main payment screen with proper top app bar
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "61 sat",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Navigate to settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        
        // SEND ICON (32x32 as requested)
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
        
        // MEMO FIELD (custom with custom keyboard)
        CustomMemoField(
            text = memo,
            onTextChange = { memo = it },
            placeholder = "Add a note...",
            modifier = Modifier.fillMaxWidth()
        )
        
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
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
            
            // Generate and display QR code from token
            val qrCodeBitmap = remember(sendState.token) {
                sendState.token?.let { token ->
                    generateQRCodeBitmap(token, 256)
                }
            }
            
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (qrCodeBitmap != null) {
                    Image(
                        bitmap = qrCodeBitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(180.dp)
                    )
                } else {
                    Text(
                        text = "Generating QR Code...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
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

/**
 * CustomMemoField - Memo field with custom keyboard
 * 
 * Uses the same styling as ThemedMemoField but with custom keyboard support
 */
@Composable
private fun CustomMemoField(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var showCustomKeyboard by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section header
        Text(
            text = "Memo",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        // Text field with border (clickable to show custom keyboard)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { showCustomKeyboard = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.TopStart
        ) {
            if (text.isEmpty()) {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Custom keyboard (when shown)
        if (showCustomKeyboard) {
            CustomMemoKeyboard(
                text = text,
                onTextChange = onTextChange,
                onDismiss = { showCustomKeyboard = false }
            )
        }
    }
}

/**
 * CustomMemoKeyboard - Custom keyboard for memo input
 */
@Composable
private fun CustomMemoKeyboard(
    text: String,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Keyboard buttons
        val rows = listOf(
            listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
            listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
            listOf("Z", "X", "C", "V", "B", "N", "M"),
            listOf("Space", "Backspace", "Done")
        )
        
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    Box(modifier = Modifier.weight(1f)) {
                        when (key) {
                            "Space" -> {
                                Button(
                                    onClick = { onTextChange(text + " ") },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Space")
                                }
                            }
                            "Backspace" -> {
                                Button(
                                    onClick = { 
                                        if (text.isNotEmpty()) {
                                            onTextChange(text.dropLast(1))
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Backspace, contentDescription = "Backspace")
                                }
                            }
                            "Done" -> {
                                Button(
                                    onClick = onDismiss,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Done")
                                }
                            }
                            else -> {
                                Button(
                                    onClick = { onTextChange(text + key) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(key)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
