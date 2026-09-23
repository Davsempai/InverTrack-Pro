package com.example.data.repository

import com.example.data.local.InvestmentDao
import com.example.data.model.InvestmentProject
import com.example.data.model.MonthlyReportItem
import com.example.data.model.PortfolioSummary
import com.example.data.model.ProjectStatus
import com.example.data.model.ProjectWithWithdrawals
import com.example.data.model.RiskLevel
import com.example.data.model.WithdrawalTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TreeMap

class InvestmentRepository(private val dao: InvestmentDao) {

    val allProjectsWithWithdrawals: Flow<List<ProjectWithWithdrawals>> =
        dao.getAllProjectsWithWithdrawals()

    fun getProject(id: Long): Flow<ProjectWithWithdrawals?> =
        dao.getProjectWithWithdrawals(id)

    suspend fun addProject(project: InvestmentProject): Long =
        dao.insertProject(project)

    suspend fun insertProject(project: InvestmentProject): Long =
        dao.insertProject(project)

    suspend fun updateProject(project: InvestmentProject) =
        dao.updateProject(project)

    suspend fun deleteProject(projectId: Long) =
        dao.deleteProjectById(projectId)

    suspend fun deleteProject(project: InvestmentProject) =
        dao.deleteProject(project)

    suspend fun addWithdrawal(withdrawal: WithdrawalTransaction): Long =
        dao.insertWithdrawal(withdrawal)

    suspend fun addWithdrawal(
        projectId: Long,
        amount: Double,
        date: Long = System.currentTimeMillis(),
        note: String = ""
    ): Long {
        val tx = WithdrawalTransaction(
            projectId = projectId,
            amount = amount,
            date = date,
            note = note
        )
        return dao.insertWithdrawal(tx)
    }

    suspend fun deleteWithdrawal(id: Long) =
        dao.deleteWithdrawalById(id)

    suspend fun deleteWithdrawal(withdrawal: WithdrawalTransaction) =
        dao.deleteWithdrawal(withdrawal)

    suspend fun updateProjectStatus(
        project: InvestmentProject,
        newStatus: ProjectStatus
    ) = updateProjectStatus(project.id, newStatus)

    suspend fun updateProjectStatus(
        projectId: Long,
        newStatus: ProjectStatus,
        reason: String? = null
    ) {
        val currentList = dao.getAllProjectsWithWithdrawals().firstOrNull() ?: emptyList()
        val found = currentList.find { it.project.id == projectId }?.project ?: return
        val updated = if (newStatus == ProjectStatus.CAIDO) {
            found.copy(
                status = ProjectStatus.CAIDO,
                fallenDate = System.currentTimeMillis(),
                fallenReason = reason?.ifBlank { "Proyecto caído" } ?: "Proyecto caído"
            )
        } else {
            found.copy(
                status = newStatus,
                fallenDate = null,
                fallenReason = null
            )
        }
        dao.updateProject(updated)
    }

    suspend fun markProjectAsFallen(
        project: InvestmentProject,
        reason: String,
        date: Long = System.currentTimeMillis()
    ) {
        val updated = project.copy(
            status = ProjectStatus.CAIDO,
            fallenDate = date,
            fallenReason = reason.ifBlank { "Proyecto caído" }
        )
        dao.updateProject(updated)
    }

    fun computePortfolioSummary(projects: List<ProjectWithWithdrawals>): PortfolioSummary {
        var totalInvested = 0.0
        var totalWithdrawn = 0.0
        var activeCapitalAtRisk = 0.0
        var capitalLostInFallen = 0.0
        var activeCount = 0
        var fallenCount = 0
        var profitableCount = 0

        for (item in projects) {
            val invested = item.project.initialCapital
            val withdrawn = item.totalWithdrawn
            totalInvested += invested
            totalWithdrawn += withdrawn

            if (item.project.status == ProjectStatus.ACTIVO) {
                activeCount++
                activeCapitalAtRisk += item.capitalRemainingToRecover
            } else if (item.project.status == ProjectStatus.CAIDO) {
                fallenCount++
                if (withdrawn < invested) {
                    capitalLostInFallen += (invested - withdrawn)
                }
            }

            if (withdrawn >= invested) {
                profitableCount++
            }
        }

        val netProfitLoss = totalWithdrawn - totalInvested
        val overallRoi = if (totalInvested > 0) (netProfitLoss / totalInvested) * 100.0 else 0.0

        return PortfolioSummary(
            totalInvested = totalInvested,
            totalWithdrawn = totalWithdrawn,
            netProfitLoss = netProfitLoss,
            overallRoiPercentage = overallRoi,
            activeCapitalAtRisk = activeCapitalAtRisk,
            totalCapitalLostInFallen = capitalLostInFallen,
            totalProjectsCount = projects.size,
            activeProjectsCount = activeCount,
            fallenProjectsCount = fallenCount,
            profitableProjectsCount = profitableCount
        )
    }

    fun computeMonthlyReports(projects: List<ProjectWithWithdrawals>): List<MonthlyReportItem> {
        val keyFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val displayFormat = SimpleDateFormat("MMM yyyy", Locale("es", "ES"))

        class MonthAggregator {
            var invested: Double = 0.0
            var withdrawn: Double = 0.0
            var withdrawalCount: Int = 0
            var newProjectsCount: Int = 0
            var timestamp: Long = 0L
        }

        val map = TreeMap<String, MonthAggregator>()

        for (item in projects) {
            val key = keyFormat.format(Date(item.project.startDate))
            val agg = map.getOrPut(key) {
                MonthAggregator().apply { timestamp = item.project.startDate }
            }
            agg.invested += item.project.initialCapital
            agg.newProjectsCount++

            for (w in item.withdrawals) {
                val wKey = keyFormat.format(Date(w.date))
                val wAgg = map.getOrPut(wKey) {
                    MonthAggregator().apply { timestamp = w.date }
                }
                wAgg.withdrawn += w.amount
                wAgg.withdrawalCount++
            }
        }

        if (map.isEmpty()) {
            val cal = Calendar.getInstance()
            val currentKey = keyFormat.format(cal.time)
            map[currentKey] = MonthAggregator().apply { timestamp = cal.timeInMillis }
        }

        var runningCumulativeInvested = 0.0
        var runningCumulativeWithdrawn = 0.0
        val results = mutableListOf<MonthlyReportItem>()

        for ((key, agg) in map) {
            runningCumulativeInvested += agg.invested
            runningCumulativeWithdrawn += agg.withdrawn
            val netFlow = agg.withdrawn - agg.invested
            val cumulativeYield = runningCumulativeWithdrawn - runningCumulativeInvested

            val display = try {
                val d = keyFormat.parse(key)
                if (d != null) displayFormat.format(d).replaceFirstChar { it.uppercase() } else key
            } catch (_: Exception) {
                key
            }

            results.add(
                MonthlyReportItem(
                    monthYearKey = key,
                    displayMonth = display,
                    totalInvestedInMonth = agg.invested,
                    totalWithdrawnInMonth = agg.withdrawn,
                    netCashFlow = netFlow,
                    cumulativeNetYield = cumulativeYield,
                    cumulativeInvested = runningCumulativeInvested,
                    cumulativeWithdrawn = runningCumulativeWithdrawn,
                    withdrawalCount = agg.withdrawalCount,
                    newProjectsCount = agg.newProjectsCount
                )
            )
        }

        return results
    }

    suspend fun cleanSampleDataIfPresent() {
        val sampleNames = listOf(
            "Bot Arbitraje Algorítmico",
            "Staking Liquid Validator",
            "Pool Alto Rendimiento MetaYield",
            "Tokenización Inmobiliaria Loft Centro"
        )
        dao.deleteProjectsByNames(sampleNames)
    }

    suspend fun clearAllData() {
        dao.deleteAllWithdrawals()
        dao.deleteAllProjects()
    }
}
