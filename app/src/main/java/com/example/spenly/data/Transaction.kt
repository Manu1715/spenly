package com.example.spenly.data

import java.time.LocalDate

// Domain model for Transaction
data class Transaction(
    val id: Int,
    val title: String,
    val category: String,
    val amount: Double,
    val date: LocalDate = LocalDate.now(),
    val isIncome: Boolean,
    val note: String = "",
    val payee: String? = null,
    val receiptUri: String? = null
)

// Extension functions to convert between Entity and Domain model
fun TransactionEntity.toTransaction(): Transaction {
    return Transaction(
        id = id,
        title = title,
        category = category,
        amount = amount,
        date = date,
        isIncome = isIncome,
        note = note,
        payee = payee,
        receiptUri = receiptUri
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        title = title,
        category = category,
        amount = amount,
        date = date,
        isIncome = isIncome,
        note = note,
        payee = payee,
        receiptUri = receiptUri
    )
}


