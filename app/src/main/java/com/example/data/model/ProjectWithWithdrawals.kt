package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class ProjectWithWithdrawals(
    @Embedded
    val project: InvestmentProject,

    @Relation(
        parentColumn = "id",
        entityColumn = "projectId"
    )
    val withdrawals: List<WithdrawalTransaction> = emptyList()
) {
    val totalWithdrawn: Double
        get() = withdrawals.sumOf { it.amount }

    val totalRecovered: Double
        get() = totalWithdrawn

    val totalInvested: Double
        get() = project.initialCapital

    val netProfitLoss: Double
        get() = totalWithdrawn - project.initialCapital

    val recoveryPercentage: Double
        get() = if (project.initialCapital > 0) {
            (totalWithdrawn / project.initialCapital) * 100.0
        } else 0.0

    val recoveryRate: Double
        get() = recoveryPercentage

    val roiPercentage: Double
        get() = if (project.initialCapital > 0) {
            ((totalWithdrawn - project.initialCapital) / project.initialCapital) * 100.0
        } else 0.0

    val isBreakEven: Boolean
        get() = totalWithdrawn >= project.initialCapital

    val isBreakEvenOrProfit: Boolean
        get() = isBreakEven

    val isProfit: Boolean
        get() = totalWithdrawn > project.initialCapital

    val isFallen: Boolean
        get() = project.status == ProjectStatus.CAIDO

    val capitalRemainingToRecover: Double
        get() = maxOf(0.0, project.initialCapital - totalWithdrawn)

    val remainingToBreakEven: Double
        get() = capitalRemainingToRecover

    val realizedLossIfFallen: Double
        get() = if (isFallen) capitalRemainingToRecover else 0.0

    val netResultWhenFallen: Double
        get() = if (isFallen) {
            if (isProfit) netProfitLoss else -capitalRemainingToRecover
        } else {
            netProfitLoss
        }

    val financialHealthSummary: String
        get() = when {
            isFallen && isBreakEvenOrProfit -> "Caído pero saliste con ganancias previas"
            isFallen -> "Pérdida definitiva: no se recuperó el total"
            recoveryRate >= 200.0 -> "¡Multiplicador extraordinario (+2x)!"
            recoveryRate > 100.0 -> "En ganancia neta (+${String.format("%.1f", recoveryRate - 100)}% extra)"
            recoveryRate == 100.0 -> "Capital 100% recuperado (Break-even)"
            recoveryRate >= 50.0 -> "Más del 50% recuperado"
            totalRecovered > 0.0 -> "Recuperación en curso"
            else -> "Sin retiros registrados aún"
        }
}
