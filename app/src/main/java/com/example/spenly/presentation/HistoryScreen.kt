package com.example.spenly.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import android.net.Uri
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spenly.data.LocalTransactionRepository
import com.example.spenly.data.Transaction
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
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
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showActionDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Get the trigger from CompositionLocal if available
    val clearTrigger = LocalClearDialogTrigger.current
    LaunchedEffect(key1 = clearTrigger.value) {
        if (clearTrigger.value) {
            showClearDialog = true
            // Reset after a brief delay to allow state to update
            clearTrigger.value = false
        }
    }

    // Get transactions from repository - observe Flow
    val repository = LocalTransactionRepository.current
    val transactions by repository.transactions.collectAsState(initial = emptyList())

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
                        scope.launch {
                            repository.clearAllTransactions()
                        }
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

    // --- Action Dialog (Delete or View Details) ---
    if (showActionDialog && selectedTransaction != null) {
        AlertDialog(
            onDismissRequest = { 
                showActionDialog = false
                selectedTransaction = null
            },
            title = { 
                Text(
                    "Transaction Options", 
                    color = TextWhite, 
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = {
                Column {
                    Text(
                        "What would you like to do with this transaction?",
                        color = SubTextGray,
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showActionDialog = false
                        showDetailsDialog = true
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = AccentGreen)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Details")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        selectedTransaction?.let { transaction ->
                            scope.launch {
                                repository.removeTransaction(transaction)
                            }
                        }
                        showActionDialog = false
                        selectedTransaction = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = AccentRed)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            },
            containerColor = SurfaceColor
        )
    }

    // --- Transaction Details Dialog ---
    if (showDetailsDialog && selectedTransaction != null) {
        TransactionDetailsDialog(
            transaction = selectedTransaction!!,
            onDismiss = {
                showDetailsDialog = false
                selectedTransaction = null
            },
            onDelete = {
                scope.launch {
                    repository.removeTransaction(selectedTransaction!!)
                }
                showDetailsDialog = false
                selectedTransaction = null
            }
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
                        TransactionItem(
                            transaction = transaction,
                            onLongPress = {
                                selectedTransaction = transaction
                                showActionDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onLongPress: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    val glowColor = remember(transaction.isIncome) {
        if (transaction.isIncome) AccentGreen else AccentRed
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() }
                )
            },
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

@Composable
fun TransactionDetailsDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    var showReceiptFullScreen by remember { mutableStateOf(false) }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMMM yyyy") }
    val glowColor = if (transaction.isIncome) AccentGreen else AccentRed
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Transaction Details",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Amount
                DetailRow(
                    label = "Amount",
                    value = currencyFormat.format(transaction.amount),
                    valueColor = glowColor
                )

                // Type
                DetailRow(
                    label = "Type",
                    value = if (transaction.isIncome) "Income" else "Expense",
                    valueColor = if (transaction.isIncome) AccentGreen else AccentRed
                )

                // Category
                DetailRow(
                    label = "Category",
                    value = transaction.category
                )

                // Date
                DetailRow(
                    label = "Date",
                    value = transaction.date.format(dateFormatter)
                )

                // Payee (if available) - More prominent
                if (transaction.payee != null && transaction.payee.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    PayeeSection(payee = transaction.payee)
                }

                // Receipt (if available)
                if (transaction.receiptUri != null && transaction.receiptUri.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    ReceiptSection(
                        receiptUri = transaction.receiptUri,
                        onViewReceipt = { showReceiptFullScreen = true }
                    )
                }

                // Note (if available)
                if (transaction.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    NoteSection(note = transaction.note)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = AccentGreen)
            ) {
                Text("Close")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDelete()
                },
                colors = ButtonDefaults.textButtonColors(contentColor = AccentRed)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Delete")
            }
        },
        containerColor = SurfaceColor,
        shape = RoundedCornerShape(20.dp)
    )

    // Full screen receipt viewer
    if (showReceiptFullScreen && transaction.receiptUri != null) {
        ReceiptFullScreenViewer(
            receiptUri = transaction.receiptUri,
            onDismiss = { showReceiptFullScreen = false }
        )
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = TextWhite
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = SubTextGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun PayeeSection(payee: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A2A2E), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Payee",
                tint = AccentGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Payee",
                    color = SubTextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = payee,
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ReceiptSection(
    receiptUri: String,
    onViewReceipt: () -> Unit
) {
    val context = LocalContext.current
    
    // Safely parse URI using remember
    val uri = remember(receiptUri) {
        try {
            Uri.parse(receiptUri)
        } catch (e: Exception) {
            null
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A2A2E), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Receipt",
                tint = AccentGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Receipt",
                    color = SubTextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Tap to view receipt",
                    color = AccentGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onViewReceipt() }
                )
            }
        }
        
        // Receipt thumbnail
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
                .clickable { onViewReceipt() }
        ) {
            if (uri != null) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(uri)
                            .build()
                    ),
                    contentDescription = "Receipt thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // If URI parsing fails, show placeholder
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Receipt unavailable",
                        color = SubTextGray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NoteSection(note: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2A2A2E), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = "Note",
            color = SubTextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = note,
            color = TextWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun ReceiptFullScreenViewer(
    receiptUri: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
    // Safely parse URI using remember
    val uri = remember(receiptUri) {
        try {
            Uri.parse(receiptUri)
        } catch (e: Exception) {
            null
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Receipt",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextWhite
                    )
                }
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
            ) {
                if (uri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(uri)
                                .build()
                        ),
                        contentDescription = "Receipt",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // If URI parsing fails, show error message
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = SubTextGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Receipt unavailable",
                                color = SubTextGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = AccentGreen)
            ) {
                Text("Close")
            }
        },
        containerColor = SurfaceColor,
        shape = RoundedCornerShape(20.dp)
    )
}


