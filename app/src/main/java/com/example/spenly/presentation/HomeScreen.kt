package com.example.spenly.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
 import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

data class Transaction(
    val id: Int,
    val title: String,
    val category: String,
    val amount: Double,
    val isIncome: Boolean
)

@Composable
fun Homescreen() {
    var selectedRange by remember { mutableStateOf("Month") }

    val currentDate = LocalDate.now()
    var selectedDay by remember { mutableStateOf(currentDate.dayOfMonth) }
    var selectedMonth by remember { mutableStateOf(currentDate.monthValue) }
    var selectedYear by remember { mutableStateOf(currentDate.year) }

    val options = listOf("Day", "Month", "Year")

    var balanceCardHeight by remember { mutableStateOf<Dp?>(null) }
    val density = LocalDensity.current

    // Sample transaction data (modifiable)
    var transactions by remember {
        mutableStateOf(
            mutableListOf(
                Transaction(1, "Salary", "Income", 8000.0, true),
                Transaction(2, "Groceries", "Food", 1200.0, false),
                Transaction(3, "Freelance", "Side Hustle", 3000.0, true),
                Transaction(4, "Netflix", "Entertainment", 499.0, false)
            )
        )
    }

    var showDialog by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    // Entire screen scrollable
    LazyColumn(
        modifier = Modifier
            .background(Color(0xFF0C0C0F))
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Header & selectors
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
                            when (selectedRange) {
                                "Day" -> selectedDay = if (selectedDay > 1) selectedDay - 1 else 31
                                "Month" -> selectedMonth = if (selectedMonth > 1) selectedMonth - 1 else 12
                                "Year" -> selectedYear -= 1
                            }
                        }
                )

                Text(
                    text = when (selectedRange) {
                        "Day" -> "Day $selectedDay"
                        "Month" -> Month.of(selectedMonth).getDisplayName(TextStyle.FULL, Locale.getDefault()) + " $selectedYear"
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
                            when (selectedRange) {
                                "Day" -> selectedDay = if (selectedDay < 31) selectedDay + 1 else 1
                                "Month" -> selectedMonth = if (selectedMonth < 12) selectedMonth + 1 else 1
                                "Year" -> selectedYear += 1
                            }
                        }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Total Balance Card (measure height)
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
                Box(
                    modifier = Modifier
                        .size(300.dp, 89.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.Cyan.copy(alpha = 0.7f), Color.Transparent),
                                center = Offset(500f, 100f),
                                radius = 480f
                            )
                        )
                        .blur(60.dp)
                )

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
                        text = "₹409,966.53",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Income & Expenses row — BOTH cards visible with equal width
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 11.dp)
                    .height(balanceCardHeight ?: 100.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IncomeExpenseCard(
                    title = "Income",
                    amount = "₹10,610.00",
                    glowColor = Color(0xFF00FF80),
                    modifier = Modifier.weight(1f)
                )

                IncomeExpenseCard(
                    title = "Expenses",
                    amount = "₹5,643.47",
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

        // Dynamic list of transactions
        items(transactions, key = { it.id }) { txn ->
            TransactionItem(txn) {
                selectedTransaction = txn
                showDialog = true
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }

    // Dialog for edit/delete on long press
    if (showDialog && selectedTransaction != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Manage Transaction") },
            text = { Text("What would you like to do with '${selectedTransaction?.title}'?") },
            confirmButton = {
                TextButton(onClick = {
                    // TODO: open an edit screen/dialog — placeholder
                    showDialog = false
                }) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Edit")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    // Delete
                    transactions.remove(selectedTransaction)
                    transactions = transactions.toMutableList()
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
fun IncomeExpenseCard(
    title: String,
    amount: String,
    glowColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable { /* optional click */ },
        contentAlignment = Alignment.Center
    ) {
        // Glow background
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(glowColor.copy(alpha = 0.4f), Color.Transparent),
                        radius = 200f
                    )
                )
                .blur(40.dp)
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
fun HomescreenPreview(){
    Homescreen()
}
