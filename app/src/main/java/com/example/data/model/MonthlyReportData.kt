package com.example.data.model

data class MonthlyReportItem(
    val monthYearKey: String, // "2026-03"
    val displayMonth: String, // "Marzo 2026"
    val totalInvestedInMonth: Double = 0.0,
    val totalWithdrawnInMonth: Double = 0.0,
    val netCashFlow: Double = 0.0, // totalWithdrawnInMonth - totalInvestedInMonth
    val cumulativeNetYield: Double = 0.0,
    val cumulativeInvested: Double = 0.0,
    val cumulativeWithdrawn: Double = 0.0,
    val withdrawalCount: Int = 0,
    val newProjectsCount: Int = 0
)

data class MonthlyMetric(
    val yearMonth: String,
    val displayLabel: String,
    val investedInMonth: Double,
    val withdrawnInMonth: Double,
    val netFlowInMonth: Double,
    val cumulativeInvested: Double,
    val cumulativeWithdrawn: Double,
    val cumulativeNetPerformance: Double,
    val withdrawalsCount: Int,
    val projectsStartedCount: Int
)

data class MonthlyReportSummary(
    val monthlyMetrics: List<MonthlyMetric> = emptyList(),
    val bestMonth: MonthlyMetric? = null,
    val worstMonth: MonthlyMetric? = null,
    val totalMonthsTracked: Int = 0,
    val averageMonthlyWithdrawn: Double = 0.0
)
