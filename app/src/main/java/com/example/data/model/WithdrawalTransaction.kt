package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "withdrawals",
    foreignKeys = [
        ForeignKey(
            entity = InvestmentProject::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"]), Index(value = ["date"])]
)
data class WithdrawalTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val note: String = ""
)
