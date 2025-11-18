package com.example.spenly.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BudgetScreen(onEditBudget: () -> Unit = {}) {
    var showEditDialog by remember { mutableStateOf(false) }
    var budgetInput by remember { mutableStateOf("") }
    
    // Get repository and observe transactions
    val repository = LocalTransactionRepository.current
    val transactionsState = repository.transactionsState
    val transactions by transactionsState
    val budgetState = repository.monthlyBudgetState
    val monthlyBudget by budgetState
    
    // Reset budget input when dialog opens
    LaunchedEffect(showEditDialog) {
        if (showEditDialog) {
            budgetInput = monthlyBudget.toString()
        }
    }
    
    val currentDate = LocalDate.now()
    val currentMonth = currentDate.monthValue
    val currentYear = currentDate.year
    val daysInMonth = currentDate.lengthOfMonth()
    val currentDay = currentDate.dayOfMonth
    
    // Filter transactions for current month (expenses only)
    val monthlyExpenses = remember(transactions, currentMonth, currentYear) {
        transactions.filter { 
            !it.isIncome && 
            it.date.monthValue == currentMonth && 
            it.date.year == currentYear 
        }
    }
    
    // Calculate spent amount
    val spent = remember(monthlyExpenses) {
        monthlyExpenses.sumOf { it.amount }
    }
    
    // Calculate budget metrics
    val left = remember(monthlyBudget, spent) {
        (monthlyBudget - spent).coerceAtLeast(0.0)
    }
    
    val progress = remember(monthlyBudget, spent) {
        if (monthlyBudget > 0) {
            (spent / monthlyBudget).coerceIn(0.0, 1.0).toFloat()
        } else 0.0f
    }
    
    val safeDaily = remember(left, currentDay, daysInMonth) {
        val daysRemaining = daysInMonth - currentDay + 1
        if (daysRemaining > 0) left / daysRemaining else 0.0
    }
    
    // Calculate budget pace
    val shouldHaveSpent = remember(monthlyBudget, currentDay, daysInMonth) {
        if (daysInMonth > 0) (monthlyBudget / daysInMonth) * currentDay else 0.0
    }
    
    val projection = remember(spent, currentDay, daysInMonth) {
        if (currentDay > 0 && daysInMonth > 0 && spent > 0) {
            (spent / currentDay) * daysInMonth
        } else 0.0
    }
    
    // Group expenses by category
    val categorySpending = remember(monthlyExpenses) {
        monthlyExpenses.groupBy { it.category }
            .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
            .take(5) // Top 5 categories
    }
    
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    // When parent passes an onEditBudget, call it as well as show dialog
    val handleEdit: () -> Unit = {
        onEditBudget()
        showEditDialog = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Budget",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Pass action here:
        BudgetSummaryCard(
            onEditClick = handleEdit,
            monthlyBudget = monthlyBudget,
            spent = spent,
            left = left,
            safeDaily = safeDaily,
            progress = progress,
            currencyFormat = currencyFormat
        )

        Spacer(modifier = Modifier.height(20.dp))
        BudgetMedalsCard()

        Spacer(modifier = Modifier.height(20.dp))
        BudgetPaceCard(
            shouldHaveSpent = shouldHaveSpent,
            actuallySpent = spent,
            projection = projection,
            currencyFormat = currencyFormat
        )

        Spacer(modifier = Modifier.height(20.dp))
        CategoryBudgetsSection(categorySpending = categorySpending, currencyFormat = currencyFormat)
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Budget", color = Color.White) },
            text = {
                Column {
                    Text("Enter monthly budget amount:", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = budgetInput,
                        onValueChange = { newValue ->
                            // Allow only numbers and decimal point
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                budgetInput = newValue
                            }
                        },
                        label = { Text("Budget Amount") },
                        placeholder = { Text("Enter amount", color = Color.Gray) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1A1A1E),
                            unfocusedContainerColor = Color(0xFF1A1A1E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            val amount = budgetInput.toDouble()
                            if (amount >= 0) {
                                repository.setMonthlyBudget(amount)
                                showEditDialog = false
                            }
                        } catch (e: NumberFormatException) {
                            // Invalid input, do nothing
                        }
                    }
                ) {
                    Text("Save", color = Color.Cyan)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF1A1A1E)
        )
    }
}

@Composable
fun BudgetSummaryCard(
    onEditClick: () -> Unit = {},
    monthlyBudget: Double,
    spent: Double,
    left: Double,
    safeDaily: Double,
    progress: Float,
    currencyFormat: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color(0xFF1A1A1E))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularBudgetProgress(progress)

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Budget", color = Color.White, fontWeight = FontWeight.Bold)

                    // Clickable Edit text
                    Text(
                        text = "Edit",
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onEditClick() }
                            .padding(4.dp) // slightly larger tap target
                    )
                }

                Text(
                    currencyFormat.format(monthlyBudget), 
                    color = Color.White, 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                InfoChip(label = "Spent: ${currencyFormat.format(spent)}")
                InfoChip(label = "Left: ${currencyFormat.format(left)}")
                InfoChip(label = "Safe today: ${currencyFormat.format(safeDaily)}")
                InfoChip(label = "Monthly")
            }
        }
    }
}

@Composable
fun CircularBudgetProgress(progress: Float) {
    Box(
        modifier = Modifier.size(90.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(90.dp)) {
            drawArc(
                color = Color.DarkGray,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 12f, cap = StrokeCap.Round)
            )
            drawArc(
                brush = Brush.linearGradient(
                    listOf(Color(0xFF00FFB9), Color(0xFF7D7AFF))
                ),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 12f, cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${(progress * 100).toInt()}%\nof budget",
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun InfoChip(label: String) {
    Box(
        modifier = Modifier
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF2A2A2E))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(label, color = Color.White, fontSize = 13.sp)
    }
}

@Composable
fun BudgetMedalsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color(0xFF17171C))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Budget Medals", color = Color.White, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MedalItem("Bronze", Color(0xFFB87333))
                MedalItem("Silver", Color(0xFFC0C0C0))
                MedalItem("Gold", Color(0xFFFFD700))
                MedalItem("Perfect", Color(0xFFB76EFF))
            }
        }
    }
}

@Composable
fun MedalItem(label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(color.copy(alpha = 0.2f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = color)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text("0", color = Color.White, fontWeight = FontWeight.Bold)
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun BudgetPaceCard(
    shouldHaveSpent: Double,
    actuallySpent: Double,
    projection: Double,
    currencyFormat: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color(0xFF17171C))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Budget Pace", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Should Have Spent", color = Color.Gray)
                    Text(
                        currencyFormat.format(shouldHaveSpent), 
                        color = Color.White, 
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Actually Spent", color = Color.Gray)
                    Text(
                        currencyFormat.format(actuallySpent), 
                        color = Color(0xFF00FF84), 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Projection ${currencyFormat.format(projection)}", 
                color = Color.White, 
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CategoryBudgetsSection(
    categorySpending: List<Pair<String, Double>>,
    currencyFormat: NumberFormat
) {
    Text("Category Budgets", color = Color.White, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(12.dp))
    if (categorySpending.isEmpty()) {
        Text(
            "No expenses this month", 
            color = Color.Gray, 
            fontSize = 14.sp
        )
    } else {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(
                items = categorySpending,
                key = { it.first }
            ) { (category, amount) ->
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .height(100.dp),
                    colors = CardDefaults.cardColors(Color(0xFF1C1C21)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            category, 
                            color = Color.White, 
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            currencyFormat.format(amount), 
                            color = Color(0xFF00FF84), 
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBudgetScreen() {
    ProvideTransactionRepository {
        BudgetScreen(onEditBudget = { /* preview action or leave empty */ })
    }
}
