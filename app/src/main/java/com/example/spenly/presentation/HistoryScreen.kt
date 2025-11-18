package com.example.spenly.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spenly.presentation.LocalTransactionRepository
import com.example.spenly.presentation.Transaction
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// CompositionLocal for triggering clear dialog from HistoryTopBar
val LocalClearDialogTrigger = compositionLocalOf<MutableState<Boolean>> {
    error("No ClearDialogTrigger provided")
}

// --- Colors (Defined for clarity) ---
val BackgroundColor = Color.Black
val TextWhite = Color.White
val AccentRed = Color(0xFFFF4D4D)
val SubTextGray = Color.Gray
val SurfaceColor = Color(0xFF1A1A1F) // Color for TextField background
val AccentGreen = Color(0xFF00FF80) // Color for income

private val historyDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

// This reusable TopAppBar can be kept for other screens.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardTopBar(
    title: String,
    onClearClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        color = TextWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Button(
                    onClick = onClearClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = AccentRed
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, AccentRed),
                    modifier = Modifier.height(35.dp)
                ) {
                    Text("Clear All", fontSize = 14.sp)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundColor
        )
    )
}

// --- Final Merged History Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }

    // Get the trigger from CompositionLocal if available
    val clearTrigger = LocalClearDialogTrigger.current
    LaunchedEffect(key1 = clearTrigger.value) {
        if (clearTrigger.value) {
            showClearDialog = true
            // Reset after a brief delay to allow state to update
            clearTrigger.value = false
        }
    }

    // Get transactions from repository - observe state directly
    val repository = LocalTransactionRepository.current
    val transactionsState = repository.transactionsState
    val transactions by transactionsState

    // Filter transactions based on search query and group them by date - Optimized with remember
    val filteredTransactions by remember(transactions, searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                transactions.groupBy { it.date }
            } else {
                transactions.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                            it.category.contains(searchQuery, ignoreCase = true)
                }.groupBy { it.date }
            }
        }
    }

    // Convert map entries to list for better performance in LazyColumn - moved outside LazyColumn
    val transactionEntries by remember(filteredTransactions) {
        derivedStateOf {
            filteredTransactions.entries.sortedByDescending { it.key }
        }
    }

    // --- Confirmation Dialog for Clearing History ---
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear History", color = TextWhite) },
            text = { Text("Are you sure you want to permanently delete all transactions?", color = SubTextGray) },
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.clearAllTransactions()
                        showClearDialog = false
                    }
                ) {
                    Text("Clear All", color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = TextWhite)
                }
            },
            containerColor = SurfaceColor
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {
            // --- Static Header Content ---
            item {
                Text(
                    text = "History",
                    color = Color.White,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 1.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar using BasicTextField for perfect alignment
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    textStyle = TextStyle(color = TextWhite, fontSize = 16.sp),
                    cursorBrush = SolidColor(Color.White),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(SurfaceColor, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = SubTextGray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (searchQuery.isEmpty()) {
                                    Text("Search Transactions", color = SubTextGray, fontSize = 16.sp)
                                }
                                innerTextField()
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(17.dp))
            }

            // --- Dynamic Transaction List --- Optimized for performance
            if (filteredTransactions.isEmpty()) {
                item {
                    Text(
                        text = if (searchQuery.isBlank()) "No transaction history." else "No results for \"$searchQuery\"",
                        color = SubTextGray,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 17.dp)
                    )
                }
            } else {
                // Use pre-computed transaction entries
                transactionEntries.forEach { (date, transactionsOnDate) ->
                    // Date Header
                    item(key = "date_$date") {
                        Text(
                            text = date.format(historyDateFormatter),
                            color = SubTextGray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    // List of transactions for that date - newest first (by ID)
                    items(
                        items = transactionsOnDate.sortedByDescending { it.id },
                        key = { it.id }
                    ) { transaction ->
                        TransactionItem(transaction = transaction)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    val glowColor = remember(transaction.isIncome) {
        if (transaction.isIncome) AccentGreen else AccentRed
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background Glow Effect - Removed blur for maximum performance
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = glowColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                )
        )

        // Main Content Row
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = transaction.category,
                    color = SubTextGray,
                    fontSize = 13.sp
                )
            }
            Text(
                text = currencyFormat.format(transaction.amount),
                color = glowColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


