package com.example.spenly.data

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(private val database: AppDatabase) {
    private val dao = database.transactionDao()
    
    val transactions: Flow<List<Transaction>> = dao.getAllTransactions().map { entities ->
        entities.map { it.toTransaction() }
    }
    
    suspend fun addTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction.toEntity())
    }
    
    suspend fun removeTransaction(transaction: Transaction) {
        val entity = dao.getTransactionById(transaction.id)
        entity?.let { dao.deleteTransaction(it) }
    }
    
    suspend fun clearAllTransactions() {
        dao.deleteAllTransactions()
    }
    
    suspend fun getTransactionById(id: Int): Transaction? {
        return dao.getTransactionById(id)?.toTransaction()
    }
}

// CompositionLocal for TransactionRepository
val LocalTransactionRepository = compositionLocalOf<TransactionRepository> {
    error("No TransactionRepository provided")
}

// Provide TransactionRepository at the app level
@Composable
fun ProvideTransactionRepository(
    context: Context,
    content: @Composable () -> Unit
) {
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { TransactionRepository(database) }
    CompositionLocalProvider(LocalTransactionRepository provides repository) {
        content()
    }
}


