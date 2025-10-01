package com.satsapp.presentation.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.satsapp.presentation.WalletViewModel
import com.satsapp.ui.theme.CompactButton
import com.satsapp.ui.theme.NumberPadButton
import com.satsapp.ui.theme.PrimaryButton

/**
 * TransactScreen - Main transaction screen with number pad
 * 
 * This is the Kotlin equivalent of TransactView.swift
 * 
 * Features:
 * - Large amount display
 * - Custom number pad
 * - Request and Pay buttons
 * - QR code scanner button
 * 
 * iOS Code (Swift):
 * ```swift
 * VStack {
 *     Text("\(amount) sat")
 *     NumberPadView(amount: $amount)
 *     HStack {
 *         Button("Request") { ... }
 *         Button(QR Icon) { ... }
 *         Button("Pay") { ... }
 *     }
 * }
 * .sheet(isPresented: $showingTransactSheet) { ... }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactScreen(
    viewModel: WalletViewModel
) {
    // Local state for amount input
    var amount by remember { mutableStateOf("0") }
    var showDepositSheet by remember { mutableStateOf(false) }
    var showQRScanner by remember { mutableStateOf(false) }
    var transactMode by remember { mutableStateOf(TransactMode.PAY) }
    
    // Load balance when screen appears
    LaunchedEffect(Unit) {
        viewModel.refreshBalance()
    }
    
    // Show QR Scanner if requested
    if (showQRScanner) {
        QRScannerScreen(
            viewModel = viewModel,
            onNavigateBack = { showQRScanner = false }
        )
        return
    }
    
    // Main container without top bar (balance is shown in main header)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        // AMOUNT DISPLAY
        // Large text showing the amount user is entering
        // iOS: .font(.system(size: 48, weight: .light))
        Text(
            text = "$amount sat",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // NUMBER PAD
        NumberPad(
            amount = amount,
            onAmountChange = { amount = it }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // ACTION BUTTONS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // REQUEST BUTTON - Opens deposit sheet
            PrimaryButton(
                onClick = { showDepositSheet = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Request")
            }
            
            // QR CODE SCANNER BUTTON (compact square button)
            CompactButton(
                onClick = { showQRScanner = true }
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scan QR Code",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // PAY BUTTON - TODO: Implement send flow
            PrimaryButton(
                onClick = {
                    // TODO: Show send sheet when send() is implemented
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Pay")
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
    
    // DEPOSIT SHEET (Request Bitcoin via Lightning)
    if (showDepositSheet) {
        DepositSheet(
            viewModel = viewModel,
            onDismiss = { showDepositSheet = false }
        )
    }
}

/**
 * NumberPad - Custom number pad for entering amounts
 * 
 * This is the Kotlin equivalent of NumberPadView in Swift
 * 
 * Creates a 4x3 grid of buttons: 1-9, 0, and backspace
 */
@Composable
private fun NumberPad(
    amount: String,
    onAmountChange: (String) -> Unit
) {
    // Button grid layout
    val buttons = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "⌫") // Empty, 0, backspace
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { button ->
                    // Each button takes equal space
                    Box(modifier = Modifier.weight(1f)) {
                        if (button.isNotEmpty()) {
                            NumberPadButton(
                                onClick = {
                                    onAmountChange(handleButtonPress(amount, button))
                                }
                            ) {
                                Text(button)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Handle number pad button press
 * 
 * Logic for adding digits or deleting
 * This matches the Swift handleButtonPress function
 */
private fun handleButtonPress(currentAmount: String, button: String): String {
    return when (button) {
        "⌫" -> {
            // Backspace - remove last digit
            if (currentAmount.isNotEmpty() && currentAmount != "0") {
                val newAmount = currentAmount.dropLast(1)
                if (newAmount.isEmpty()) "0" else newAmount
            } else {
                currentAmount
            }
        }
        "0" -> {
            // Don't allow leading zeros
            if (currentAmount != "0") {
                currentAmount + button
            } else {
                currentAmount
            }
        }
        else -> {
            // Regular digit
            if (currentAmount == "0") {
                button
            } else {
                currentAmount + button
            }
        }
    }
}

/**
 * TransactMode - Pay or Request mode
 * 
 * iOS equivalent: enum TransactMode
 */
enum class TransactMode {
    PAY,
    REQUEST;
    
    val buttonTitle: String
        get() = when (this) {
            PAY -> "Pay Bitcoin"
            REQUEST -> "Request Bitcoin"
        }
    
    val successMessage: String
        get() = when (this) {
            PAY -> "Payment sent!"
            REQUEST -> "Request sent!"
        }
}

// TransactSheet removed - now using DepositSheet for requests
// and QRScannerScreen for scanning tokens

/*
 * LEARNING NOTES:
 * 
 * 1. CUSTOM LAYOUTS:
 *    iOS and Android handle grids differently:
 *    
 *    iOS:
 *    ForEach(buttons) { row in
 *        HStack { ForEach(row) { button in ... } }
 *    }
 *    
 *    Android:
 *    buttons.forEach { row ->
 *        Row { row.forEach { button -> ... } }
 *    }
 *    
 *    Same nested loop structure!
 * 
 * 2. SHEET/MODAL PRESENTATION:
 *    iOS: .sheet(isPresented: $showingSheet) { SheetView() }
 *    Android: if (showSheet) { ModalBottomSheet { ... } }
 *    
 *    iOS uses modifier, Android uses conditional composable.
 * 
 * 3. BUTTON GRIDS:
 *    Creating a number pad requires:
 *    - Nested loops for rows/columns
 *    - Equal weight distribution (Modifier.weight(1f))
 *    - Handling empty cells
 *    
 *    Both platforms use similar approaches!
 * 
 * 4. STRING MANIPULATION:
 *    iOS: String(amount.dropLast())
 *    Android: currentAmount.dropLast(1)
 *    
 *    Kotlin has great string functions built-in!
 * 
 * 5. ENUMS WITH PROPERTIES:
 *    iOS:
 *    enum Mode {
 *        case pay
 *        var buttonTitle: String {
 *            switch self { case .pay: return "Pay" }
 *        }
 *    }
 *    
 *    Android:
 *    enum class Mode {
 *        PAY;
 *        val buttonTitle: String
 *            get() = when (this) { PAY -> "Pay" }
 *    }
 *    
 *    Very similar patterns!
 */

