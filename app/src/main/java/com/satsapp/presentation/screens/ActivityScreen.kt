package com.satsapp.presentation.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.satsapp.presentation.WalletViewModel
import java.text.NumberFormat
import java.util.*

/**
 * ActivityScreen - Transaction history list
 * 
 * This is the Kotlin equivalent of ActivityView.swift
 * 
 * Shows a list of all transactions (sent, received, pending)
 * 
 * iOS Code (Swift):
 * ```swift
 * NavigationView {
 *     List {
 *         ForEach(transactions) { transaction in
 *             TransactionRowView(transaction: transaction)
 *         }
 *     }
 *     .refreshable { await loadData() }
 * }
 * ```
 * 
 * Key Differences:
 * - iOS List + ForEach → Android LazyColumn + items
 * - iOS .refreshable → Android PullRefresh (custom modifier)
 * - iOS .task → Android LaunchedEffect
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    viewModel: WalletViewModel
) {
    // Sample transactions (TODO: Replace with actual data from viewModel)
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Load transactions when screen appears
    // iOS: .task { await loadData() }
    // Android: LaunchedEffect(Unit) { ... }
    LaunchedEffect(Unit) {
        isLoading = true
        // Load transactions from CDK wallet
        viewModel.loadTransactions { loadedTransactions ->
            transactions = loadedTransactions
            isLoading = false
        }
    }
    
    // Main content (no top bar needed - main screen has it)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading) {
            // Loading state
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else if (transactions.isEmpty()) {
            // Empty state
            Text(
                text = "No transactions yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            // Transaction list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
            ) {
                items(transactions) { transaction ->
                    TransactionRow(transaction = transaction)
                    Divider()
                }
            }
        }
    }
}

/**
 * TransactionRow - Single transaction item
 * 
 * This is the Kotlin equivalent of TransactionRowView in Swift
 * 
 * Shows transaction icon, details, and amount
 */
@Composable
private fun TransactionRow(
    transaction: Transaction
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ICON (left side)
        Icon(
            imageVector = getTransactionIcon(transaction.type),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = getIconColor(transaction.type, transaction.status)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // DETAILS (middle)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Title (memo or "Sent"/"Received")
            Text(
                text = transaction.memo ?: when (transaction.type) {
                    TransactionType.SENT -> "Sent"
                    TransactionType.RECEIVED -> "Received"
                    TransactionType.REQUEST -> "Request"
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            // Date and status
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = formatDate(transaction.date),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                if (transaction.status != TransactionStatus.COMPLETED) {
                    Text(
                        text = "• ${getStatusText(transaction.status)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = getStatusColor(transaction.status)
                    )
                }
            }
        }
        
        // AMOUNT (right side)
        Text(
            text = formatAmount(transaction.amount, transaction.type),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = getAmountColor(transaction.type, transaction.status)
        )
    }
}

// MARK: - Transaction Data Models

/**
 * Transaction - Represents a transaction
 * 
 * iOS equivalent: struct Transaction
 */
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val type: TransactionType,
    val amount: Int,
    val description: String,
    val memo: String?,
    val date: Date,
    val status: TransactionStatus
)

/**
 * TransactionType - Type of transaction
 */
enum class TransactionType {
    SENT,
    RECEIVED,
    REQUEST
}

/**
 * TransactionStatus - Status of transaction
 */
enum class TransactionStatus {
    COMPLETED,
    PENDING,
    FAILED
}

// MARK: - Helper Functions

private fun getTransactionIcon(type: TransactionType) = when (type) {
    TransactionType.RECEIVED -> Icons.Default.ArrowDownward
    TransactionType.SENT -> Icons.Default.ArrowUpward
    TransactionType.REQUEST -> Icons.Default.AccessTime
}

private fun getIconColor(type: TransactionType, status: TransactionStatus): Color {
    return when (status) {
        TransactionStatus.COMPLETED -> when (type) {
            TransactionType.RECEIVED -> Color(0xFF4CAF50) // Green
            TransactionType.SENT -> Color(0xFF2196F3) // Blue
            TransactionType.REQUEST -> Color(0xFFFF9500) // Orange
        }
        TransactionStatus.PENDING -> Color(0xFFFF9500) // Orange
        TransactionStatus.FAILED -> Color(0xFFF44336) // Red
    }
}

private fun getAmountColor(type: TransactionType, status: TransactionStatus): Color {
    return when (status) {
        TransactionStatus.COMPLETED -> 
            if (type == TransactionType.RECEIVED) Color(0xFF4CAF50) else Color.Black
        TransactionStatus.PENDING -> Color(0xFFFF9500)
        TransactionStatus.FAILED -> Color(0xFFF44336)
    }
}

private fun getStatusText(status: TransactionStatus) = when (status) {
    TransactionStatus.COMPLETED -> "Completed"
    TransactionStatus.PENDING -> "Pending"
    TransactionStatus.FAILED -> "Failed"
}

private fun getStatusColor(status: TransactionStatus) = when (status) {
    TransactionStatus.COMPLETED -> Color(0xFF4CAF50)
    TransactionStatus.PENDING -> Color(0xFFFF9500)
    TransactionStatus.FAILED -> Color(0xFFF44336)
}

private fun formatAmount(amount: Int, type: TransactionType): String {
    val prefix = if (type == TransactionType.RECEIVED) "+" else "-"
    val formatter = NumberFormat.getInstance()
    return "$prefix${formatter.format(amount)} sat"
}

/**
 * Format date as relative time (e.g., "5m ago", "2h ago", "3d ago")
 * 
 * iOS does this with custom date formatting
 * Android can use same logic!
 */
private fun formatDate(date: Date): String {
    val now = Date()
    val timeInterval = (now.time - date.time) / 1000 // seconds
    
    return when {
        timeInterval < 60 -> "Just now"
        timeInterval < 3600 -> "${timeInterval / 60}m ago"
        timeInterval < 86400 -> "${timeInterval / 3600}h ago"
        timeInterval < 604800 -> "${timeInterval / 86400}d ago"
        else -> {
            val calendar = Calendar.getInstance()
            calendar.time = date
            "${calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())} ${calendar.get(Calendar.DAY_OF_MONTH)}"
        }
    }
}

/*
 * LEARNING NOTES:
 * 
 * 1. LISTS:
 *    iOS: List { ForEach(items) { item in ... } }
 *    Android: LazyColumn { items(items) { item -> ... } }
 *    
 *    LazyColumn is like RecyclerView in old Android - it only renders visible items!
 * 
 * 2. ENUMS:
 *    iOS: enum TransactionType { case sent, received, request }
 *    Android: enum class TransactionType { SENT, RECEIVED, REQUEST }
 *    
 *    Almost identical! Android convention uses UPPER_CASE for enum values.
 * 
 * 3. COMPUTED PROPERTIES:
 *    iOS: var iconName: String { switch self { ... } }
 *    Android: fun getIcon(type: Type) = when (type) { ... }
 *    
 *    iOS uses computed properties on enums
 *    Android typically uses separate functions
 * 
 * 4. LAZY LOADING:
 *    iOS: .task { await loadData() }
 *    Android: LaunchedEffect(Unit) { loadData() }
 *    
 *    Both run code when the screen appears!
 *    LaunchedEffect(Unit) runs once when composable enters composition.
 * 
 * 5. PULL TO REFRESH:
 *    iOS: .refreshable { await refresh() }
 *    Android: Requires PullRefreshIndicator (more complex)
 *    
 *    iOS has built-in pull-to-refresh.
 *    Android requires additional code (I simplified it for now).
 */

