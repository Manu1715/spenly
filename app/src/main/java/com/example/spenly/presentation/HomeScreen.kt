package com.example.spenly.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Homescreen() {
    val repository = LocalTransactionRepository.current
    val scope = rememberCoroutineScope()
    var selectedRange by remember { mutableStateOf("Month") }

    // Use a single source of truth for the date
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // Derived states for month and year views
    val selectedYearMonth = YearMonth.from(selectedDate)
    val selectedYear = selectedDate.year

    val options = remember { listOf("Day", "Month", "Year") }

    var balanceCardHeight by remember { mutableStateOf<Dp?>(null) }
    val density = LocalDensity.current

    // Observe repository transactions from Flow
    val allTransactions by repository.transactions.collectAsState(initial = emptyList())

    // Get current transactions from repository - newest first (by date, then by ID)
    val transactions by remember(allTransactions) {
        derivedStateOf {
            allTransactions.sortedWith(
                compareByDescending<Transaction> { it.date }
                    .thenByDescending { it.id }
            ).take(4) // Show recent 4
        }
    }

    // Calculate totals
    val totalBalance by remember(allTransactions) {
        derivedStateOf {
            allTransactions.sumOf { if (it.isIncome) it.amount else -it.amount }
        }
    }

    val totalIncome by remember(allTransactions) {
        derivedStateOf {
            allTransactions.filter { it.isIncome }.sumOf { it.amount }
        }
    }

    val totalExpenses by remember(allTransactions) {
        derivedStateOf {
            allTransactions.filter { !it.isIncome }.sumOf { it.amount }
        }
    }

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    var showDialog by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Home",
                    fontSize = 35.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    options.forEach { option ->
                        val isSelected = option == selectedRange
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color.Cyan.copy(alpha = 0.8f) else Color.Transparent)
                                .clickable { selectedRange = option }
                                .padding(horizontal = 17.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = option,
                                color = if (isSelected) Color.White else Color.Gray,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(11.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIos,
                        contentDescription = "Previous",
                        tint = Color.LightGray,
                        modifier = Modifier
                            .size(17.dp)
                            .clickable {
                                // --- FIX 1: Use a single date source to avoid conflicts ---
                                selectedDate = when (selectedRange) {
                                    "Day" -> selectedDate.minusDays(1)
                                    "Month" -> selectedDate.minusMonths(1)
                                    "Year" -> selectedDate.minusYears(1)
                                    else -> selectedDate // Should not happen
                                }
                            }
                    )

                    Text(
                        text = when (selectedRange) {
                            // --- FIX 2: All text is derived from the single selectedDate state ---
                            "Day" -> "${selectedDate.dayOfMonth} ${selectedYearMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${selectedDate.year}"
                            "Month" -> "${selectedYearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${selectedDate.year}"
                            else -> selectedYear.toString()
                        },
                        color = Color.LightGray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "Next",
                        tint = Color.LightGray,
                        modifier = Modifier
                            .size(17.dp)
                            .clickable {
                                selectedDate = when (selectedRange) {
                                    "Day" -> selectedDate.plusDays(1)
                                    "Month" -> selectedDate.plusMonths(1)
                                    "Year" -> selectedDate.plusYears(1)
                                    else -> selectedDate
                                }
                            }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- BETTER GLOW BEHIND TOTAL BALANCE BOX ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 11.dp)
                        .onSizeChanged {
                            if (balanceCardHeight == null) {
                                balanceCardHeight = with(density) { it.height.toDp() }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Cyan glow behind the card
                    val glowBrush = remember {
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Cyan.copy(alpha = 0.6f),
                                Color.Transparent
                            ),
                            center = Offset(500f, 100f), // Center glow under the box
                            radius = 500f
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .size(200.dp,89.dp)
                            .background(
                                brush = glowBrush,
                                shape = RoundedCornerShape(24.dp)
                            )
                    )

                    // Main Total Balance card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF111113), RoundedCornerShape(24.dp))
                            .border(1.dp, Color.DarkGray.copy(0.3f), RoundedCornerShape(24.dp))
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Balance",
                            color = Color(0xFFB0B0B0),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = currencyFormat.format(totalBalance),
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 11.dp)
                        .height(balanceCardHeight ?: 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IncomeExpenseCard(
                        title = "Income",
                        amount = currencyFormat.format(totalIncome),
                        glowColor = Color(0xFF00FF80),
                        modifier = Modifier.weight(1f)
                    )

                    IncomeExpenseCard(
                        title = "Expenses",
                        amount = currencyFormat.format(totalExpenses),
                        glowColor = Color(0xFFFF4D4D),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(thickness = 1.5.dp, color = Color.DarkGray.copy(alpha = 0.8f))

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Recent Transactions",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(
                items = transactions,
                key = { it.id }
            ) { txn ->
                TransactionItem(
                    transaction = txn,
                    onLongPress = {
                        selectedTransaction = txn
                        showDialog = true
                    }
                )
            }

            // --- NEW CALENDAR SECTION ---
            item {
                Spacer(modifier = Modifier.height(24.dp))

                // Horizontal Divider separating Recent Transactions and Calendar
                HorizontalDivider(thickness = 1.5.dp, color = Color.DarkGray.copy(alpha = 0.8f))

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Calendar",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                CalendarSection(
                    currentDate = selectedDate,
                    onDateSelected = { newDate ->
                        selectedDate = newDate
                        // If user clicks a date, switch to "Day" view to show transactions for that specific day
                        selectedRange = "Day"
                    }
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showDialog && selectedTransaction != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Manage Transaction") },
            text = { Text("What would you like to do with '${selectedTransaction?.title}'?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    // TODO: Navigate to an edit screen
                }) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Edit")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedTransaction?.let { transaction ->
                        scope.launch {
                            repository.removeTransaction(transaction)
                        }
                    }
                    showDialog = false
                }) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Delete")
                }
            }
        )
    }
}

@Composable
fun CalendarSection(
    currentDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val yearMonth = YearMonth.from(currentDate)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek.value // 1 (Mon) to 7 (Sun)

    // Calculate offset: We assume Monday start (value 1).
    // Grid needs to know how many empty slots before the 1st of the month.
    val offset = firstDayOfMonth - 1

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF111113))
            .border(1.dp, Color.DarkGray.copy(0.3f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        // Month and Year Header within the Calendar Card
        Text(
            text = "${yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${yearMonth.year}",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // Days of Week Header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Text(
                    text = day,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Calendar Grid
        val totalSlots = daysInMonth + offset
        val rows = (totalSlots + 6) / 7

        for (i in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (j in 0 until 7) {
                    val dayIndex = (i * 7) + j - offset + 1
                    if (dayIndex in 1..daysInMonth) {
                        val date = yearMonth.atDay(dayIndex)
                        val isSelected = date == currentDate

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.Cyan else Color.Transparent)
                                .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayIndex.toString(),
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        // Empty spacer for offset days
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun IncomeExpenseCard(
    title: String,
    amount: String,
    glowColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(glowColor.copy(alpha = 0.08f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF111113), RoundedCornerShape(24.dp))
                .border(1.dp, Color.DarkGray.copy(0.3f), RoundedCornerShape(24.dp))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, color = Color(0xFFB0B0B0), fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = amount, color = glowColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onLongPress: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF111113))
            .border(1.dp, Color.DarkGray.copy(0.4f), RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { onLongPress() })
            }
            .padding(14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(transaction.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(transaction.category, color = Color.Gray, fontSize = 13.sp)
            }
            Text(
                text = (if (transaction.isIncome) "+" else "-") + "₹" + transaction.amount,
                color = if (transaction.isIncome) Color(0xFF00FF80) else Color(0xFFFF4D4D),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    Homescreen()
}
