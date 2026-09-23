package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class InvestmentProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val initialCapital: Double,
    val startDate: Long = System.currentTimeMillis(),
    val status: ProjectStatus = ProjectStatus.ACTIVO,
    val riskLevel: RiskLevel = RiskLevel.MEDIO,
    val notes: String = "",
    val currency: String = "$",
    val fallenDate: Long? = null,
    val fallenReason: String? = null
)
