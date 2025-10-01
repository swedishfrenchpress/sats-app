package com.satsapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import com.satsapp.presentation.WalletViewModel
import java.text.NumberFormat
import java.util.*

/**
 * ActivityScreen - Transaction history matching iOS design
 * 
 * This shows Payment Requests and Transactions sections like the iOS app
 * 
 * Features:
 * - Payment Requests section
 * - Transactions section with real data
 * - Clean list-based layout
 * - Proper icons and styling
 */
@Composable
fun ActivityScreen(
    viewModel: WalletViewModel
) {
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Load transactions when screen appears
    LaunchedEffect(Unit) {
        viewModel.loadTransactions { loadedTransactions ->
            transactions = loadedTransactions
            isLoading = false
        }
    }
    
    // Main container with list layout
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // PAYMENT REQUESTS SECTION
        item {
            PaymentRequestsSection()
        }
        
        // TRANSACTIONS SECTION
        item {
            TransactionsSection(
                transactions = transactions,
                isLoading = isLoading
            )
        }
    }
}

/**
 * PaymentRequestsSection - Shows payment request options
 */
@Composable
private fun PaymentRequestsSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section title
        Text(
            text = "Payment Requests",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        
        // Request Link item
        PaymentRequestItem(
            icon = Icons.Default.Link,
            title = "Request Link",
            amount = "5 sat"
        )
    }
}

/**
 * TransactionsSection - Shows transaction history
 */
@Composable
private fun TransactionsSection(
    transactions: List<Transaction>,
    isLoading: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section title
        Text(
            text = "Transactions",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        
        // Transaction list
        if (isLoading) {
            // Loading state
            repeat(3) {
                TransactionItemSkeleton()
            }
        } else {
            // Real transactions
            transactions.forEach { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
    }
}

/**
 * PaymentRequestItem - Individual payment request row
 */
@Composable
private fun PaymentRequestItem(
    icon: ImageVector,
    title: String,
    amount: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0D9CC)), // Light beige background
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        
        // Amount
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/**
 * TransactionItem - Individual transaction row
 */
@Composable
private fun TransactionItem(
    transaction: Transaction
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with background and status indicator
        Box(
            modifier = Modifier.size(40.dp)
        ) {
            // Main icon background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0D9CC)), // Light beige background
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.type == TransactionType.SENT) 
                        Icons.Default.ArrowUpward 
                    else 
                        Icons.Default.ArrowDownward,
                    contentDescription = transaction.type.name,
                    tint = if (transaction.type == TransactionType.SENT) 
                        Color(0xFFE57373) // Orange-red for sent
                    else 
                        Color(0xFF81C784), // Green for received
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Status indicator (hourglass)
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        if (transaction.type == TransactionType.SENT) 
                            Color(0xFFE57373) 
                        else 
                            Color(0xFF81C784)
                    )
                    .align(Alignment.BottomEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Status",
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Transaction details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = transaction.type.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = formatTimeAgo(transaction.date),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Amount
        Text(
            text = "${transaction.amount} sat",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/**
 * TransactionItemSkeleton - Loading placeholder
 */
@Composable
private fun TransactionItemSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Skeleton icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0D9CC))
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Skeleton text
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(16.dp)
                    .background(Color(0xFFE0D9CC))
            )
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(12.dp)
                    .background(Color(0xFFE0D9CC))
            )
        }
        
        // Skeleton amount
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(16.dp)
                .background(Color(0xFFE0D9CC))
        )
    }
}

/**
 * Format time ago string
 */
private fun formatTimeAgo(date: Date): String {
    val now = Date()
    val diff = now.time - date.time
    val minutes = diff / (1000 * 60)
    
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes minutes ago"
        else -> "${minutes / 60} hours ago"
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

