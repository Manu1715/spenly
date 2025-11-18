package com.example.spenly.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import java.time.LocalDate

// Unified Transaction data class
data class Transaction(
    val id: Int,
    val title: String,
    val category: String,
    val amount: Double,
    val date: LocalDate = LocalDate.now(),
    val isIncome: Boolean,
    val note: String = "",
    val payee: String? = null
)

// Transaction Repository to manage all transactions
class TransactionRepository {
    private val _transactions = mutableStateOf(
        listOf(
            Transaction(1, "Salary", "Income", 8000.0, LocalDate.of(2025, 1, 13), true),
            Transaction(2, "Groceries", "Food", 1200.0, LocalDate.of(2025, 1, 13), false),
            Transaction(3, "Freelance", "Side Hustle", 3000.0, LocalDate.of(2025, 1, 13), true),
            Transaction(4, "Netflix", "Entertainment", 499.0, LocalDate.of(2025, 1, 13), false),
            Transaction(5, "Food & Dining", "Groceries", 45.00, LocalDate.of(2025, 1, 13), false),
            Transaction(6, "Shopping", "Dress", 308.00, LocalDate.of(2025, 1, 13), false),
            Transaction(7, "Freelance", "Project X", 10000.00, LocalDate.of(2025, 1, 13), true),
            Transaction(8, "Shopping", "Snitch Shirt", 180.00, LocalDate.of(2025, 1, 13), false),
            Transaction(9, "Education", "Udemy Course", 50.00, LocalDate.of(2025, 1, 12), false),
            Transaction(10, "Salary", "October Paycheck", 50000.00, LocalDate.of(2025, 1, 11), true),
            Transaction(11, "Travel", "Weekend Trip", 2500.00, LocalDate.of(2025, 1, 11), false),
            Transaction(12, "Entertainment", "Movie Tickets", 600.00, LocalDate.of(2025, 1, 10), false),
            Transaction(13, "Health", "Medicine", 150.00, LocalDate.of(2025, 1, 10), false),
            Transaction(14, "Gifts", "Birthday Present", 1000.00, LocalDate.of(2025, 1, 9), false)
        )
    )
    
    private val _monthlyBudget = mutableStateOf(20000.0)
    
    val transactions: List<Transaction>
        get() = _transactions.value
    
    val transactionsState: State<List<Transaction>>
        get() = _transactions
    
    val monthlyBudget: Double
        get() = _monthlyBudget.value
    
    val monthlyBudgetState: State<Double>
        get() = _monthlyBudget
    
    fun addTransaction(transaction: Transaction) {
        _transactions.value = _transactions.value + transaction
    }
    
    fun removeTransaction(transaction: Transaction) {
        _transactions.value = _transactions.value.filter { it.id != transaction.id }
    }
    
    fun clearAllTransactions() {
        _transactions.value = emptyList()
    }
    
    fun setMonthlyBudget(amount: Double) {
        _monthlyBudget.value = amount
    }
    
    private var nextId = 15
    
    fun getNextId(): Int {
        return nextId++
    }
}

// CompositionLocal for TransactionRepository
val LocalTransactionRepository = compositionLocalOf<TransactionRepository> {
    error("No TransactionRepository provided")
}

// Provide TransactionRepository at the app level
@Composable
fun ProvideTransactionRepository(content: @Composable () -> Unit) {
    val repository = remember { TransactionRepository() }
    CompositionLocalProvider(LocalTransactionRepository provides repository) {
        content()
    }
}

