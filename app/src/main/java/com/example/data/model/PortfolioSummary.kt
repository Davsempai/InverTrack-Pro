package com.example.data.model

data class PortfolioSummary(
    val totalInvested: Double = 0.0,
    val totalWithdrawn: Double = 0.0,
    val netProfitLoss: Double = 0.0,
    val overallRoiPercentage: Double = 0.0,
    val activeCapitalAtRisk: Double = 0.0,
    val totalCapitalLostInFallen: Double = 0.0,
    val totalProjectsCount: Int = 0,
    val activeProjectsCount: Int = 0,
    val fallenProjectsCount: Int = 0,
    val profitableProjectsCount: Int = 0,
    val currency: String = "$"
) {
    val totalRecovered: Double
        get() = totalWithdrawn

    val globalRoiPercentage: Double
        get() = overallRoiPercentage

    val capitalLostInFallen: Double
        get() = totalCapitalLostInFallen

    val winRatePercentage: Double
        get() = if (totalProjectsCount > 0) (profitableProjectsCount.toDouble() / totalProjectsCount) * 100.0 else 0.0

    val isNetProfit: Boolean
        get() = netProfitLoss > 0

    val isBreakEven: Boolean
        get() = netProfitLoss >= 0

    val recoveryPercentage: Double
        get() = if (totalInvested > 0) (totalWithdrawn / totalInvested) * 100.0 else 0.0
}
