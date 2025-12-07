package com.example.spenly.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val category: String,
    val amount: Double,
    val date: LocalDate,
    val isIncome: Boolean,
    val note: String = "",
    val payee: String? = null,
    val receiptUri: String? = null
)


