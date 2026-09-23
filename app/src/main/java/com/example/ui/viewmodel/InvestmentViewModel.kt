package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.InvestmentProject
import com.example.data.model.MonthlyMetric
import com.example.data.model.MonthlyReportSummary
import com.example.data.model.PortfolioSummary
import com.example.data.model.ProjectStatus
import com.example.data.model.ProjectWithWithdrawals
import com.example.data.model.RiskLevel
import com.example.data.model.WithdrawalTransaction
import com.example.data.repository.InvestmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TreeMap

class InvestmentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InvestmentRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = InvestmentRepository(db.investmentDao())
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    private val _searchQuery = MutableStateFlow("")
    private val _selectedStatusFilter = MutableStateFlow(FilterStatus.TODOS)
    private val _selectedCategoryFilter = MutableStateFlow("Todas")
    private val _selectedProjectId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<InvestmentUiState> = combine(
        repository.allProjectsWithWithdrawals,
        _searchQuery,
        _selectedStatusFilter,
        _selectedCategoryFilter,
        _selectedProjectId
    ) { projectsList, query, statusFilter, categoryFilter, selectedId ->
        // Distinct categories
        val categories = mutableListOf("Todas")
        categories.addAll(projectsList.map { it.project.category }.distinct().sorted())

        // Calculate Portfolio Summary
        val totalInvested = projectsList.sumOf { it.project.initialCapital }
        val totalWithdrawn = projectsList.sumOf { it.totalWithdrawn }
        val netProfitLoss = totalWithdrawn - totalInvested
        val overallRoi = if (totalInvested > 0) (netProfitLoss / totalInvested) * 100.0 else 0.0

        val activeProjects = projectsList.filter { it.project.status == ProjectStatus.ACTIVO || it.project.status == ProjectStatus.PAUSADO }
        val fallenProjects = projectsList.filter { it.project.status == ProjectStatus.CAIDO }

        val activeCapitalAtRisk = activeProjects.sumOf { it.capitalRemainingToRecover }
        val totalCapitalLostInFallen = fallenProjects.sumOf { it.capitalRemainingToRecover }
        val profitableCount = projectsList.count { it.isProfit }

        val summary = PortfolioSummary(
            totalInvested = totalInvested,
            totalWithdrawn = totalWithdrawn,
            netProfitLoss = netProfitLoss,
            overallRoiPercentage = overallRoi,
            activeCapitalAtRisk = activeCapitalAtRisk,
            totalCapitalLostInFallen = totalCapitalLostInFallen,
            totalProjectsCount = projectsList.size,
            activeProjectsCount = activeProjects.size,
            fallenProjectsCount = fallenProjects.size,
            profitableProjectsCount = profitableCount
        )

        // Calculate Monthly Metrics
        val monthlyReport = computeMonthlyReport(projectsList)

        // Apply filters
        val filtered = projectsList.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.project.name.contains(query, ignoreCase = true) ||
                    item.project.category.contains(query, ignoreCase = true) ||
                    item.project.notes.contains(query, ignoreCase = true) ||
                    (item.project.fallenReason?.contains(query, ignoreCase = true) == true)

            val matchesStatus = when (statusFilter) {
                FilterStatus.TODOS -> true
                FilterStatus.ACTIVOS -> item.project.status == ProjectStatus.ACTIVO
                FilterStatus.CAIDOS -> item.project.status == ProjectStatus.CAIDO
                FilterStatus.EN_GANANCIA -> item.isProfit
                FilterStatus.EN_PERDIDA -> !item.isBreakEven
            }

            val matchesCategory = categoryFilter == "Todas" || item.project.category.equals(categoryFilter, ignoreCase = true)

            matchesQuery && matchesStatus && matchesCategory
        }

        val selectedProjectItem = if (selectedId != null) {
            projectsList.firstOrNull { it.project.id == selectedId }
        } else null

        InvestmentUiState(
            projects = projectsList,
            filteredProjects = filtered,
            selectedProject = selectedProjectItem,
            portfolioSummary = summary,
            monthlyReport = monthlyReport,
            searchQuery = query,
            selectedStatusFilter = statusFilter,
            selectedCategoryFilter = categoryFilter,
            availableCategories = categories,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InvestmentUiState(isLoading = true)
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(filter: FilterStatus) {
        _selectedStatusFilter.value = filter
    }

    fun setFilter(filter: FilterStatus) {
        _selectedStatusFilter.value = filter
    }

    fun setCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun selectProject(projectId: Long?) {
        _selectedProjectId.value = projectId
    }

    fun addProject(
        name: String,
        category: String,
        initialCapital: Double,
        startDate: Long = System.currentTimeMillis(),
        riskLevel: RiskLevel = RiskLevel.MEDIO,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val newProject = InvestmentProject(
                name = name.trim(),
                category = category.trim(),
                initialCapital = initialCapital,
                startDate = startDate,
                riskLevel = riskLevel,
                notes = notes.trim()
            )
            val newId = repository.insertProject(newProject)
            _selectedProjectId.value = newId
        }
    }

    fun updateProject(
        project: InvestmentProject,
        name: String,
        category: String,
        initialCapital: Double,
        riskLevel: RiskLevel,
        notes: String
    ) {
        viewModelScope.launch {
            val updated = project.copy(
                name = name.trim(),
                category = category.trim(),
                initialCapital = initialCapital,
                riskLevel = riskLevel,
                notes = notes.trim()
            )
            repository.updateProject(updated)
        }
    }

    fun deleteProject(project: InvestmentProject) {
        viewModelScope.launch {
            repository.deleteProject(project)
            if (_selectedProjectId.value == project.id) {
                _selectedProjectId.value = null
            }
        }
    }

    fun addWithdrawal(
        projectId: Long,
        amount: Double,
        date: Long = System.currentTimeMillis(),
        note: String = ""
    ) {
        viewModelScope.launch {
            repository.addWithdrawal(
                projectId = projectId,
                amount = amount,
                date = date,
                note = note.trim()
            )
        }
    }

    fun deleteWithdrawal(transaction: WithdrawalTransaction) {
        viewModelScope.launch {
            repository.deleteWithdrawal(transaction)
        }
    }

    fun markProjectAsFallen(
        project: InvestmentProject,
        reason: String,
        date: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            repository.markProjectAsFallen(project, reason, date)
        }
    }

    fun updateProjectStatus(project: InvestmentProject, newStatus: ProjectStatus) {
        viewModelScope.launch {
            repository.updateProjectStatus(project, newStatus)
        }
    }

    private fun computeMonthlyReport(projects: List<ProjectWithWithdrawals>): MonthlyReportSummary {
        val ymFormat = SimpleDateFormat("yyyy-MM", Locale.US)
        val displayFormat = SimpleDateFormat("MMM yyyy", Locale("es", "ES"))

        val monthsMap = TreeMap<String, MonthAccumulator>()

        for (item in projects) {
            val p = item.project
            val ym = ymFormat.format(Date(p.startDate))
            val acc = monthsMap.getOrPut(ym) { MonthAccumulator(ym) }
            acc.invested += p.initialCapital
            acc.projectsStarted++

            for (w in item.withdrawals) {
                val wym = ymFormat.format(Date(w.date))
                val wAcc = monthsMap.getOrPut(wym) { MonthAccumulator(wym) }
                wAcc.withdrawn += w.amount
                wAcc.withdrawalsCount++
            }
        }

        var runningInvested = 0.0
        var runningWithdrawn = 0.0
        val metricList = mutableListOf<MonthlyMetric>()

        for ((ym, acc) in monthsMap) {
            runningInvested += acc.invested
            runningWithdrawn += acc.withdrawn
            val runningNet = runningWithdrawn - runningInvested

            val displayLabel = try {
                val parsed = ymFormat.parse(ym)
                if (parsed != null) displayFormat.format(parsed).replaceFirstChar { it.uppercase() } else ym
            } catch (e: Exception) {
                ym
            }

            metricList.add(
                MonthlyMetric(
                    yearMonth = ym,
                    displayLabel = displayLabel,
                    investedInMonth = acc.invested,
                    withdrawnInMonth = acc.withdrawn,
                    netFlowInMonth = acc.withdrawn - acc.invested,
                    cumulativeInvested = runningInvested,
                    cumulativeWithdrawn = runningWithdrawn,
                    cumulativeNetPerformance = runningNet,
                    withdrawalsCount = acc.withdrawalsCount,
                    projectsStartedCount = acc.projectsStarted
                )
            )
        }

        val best = metricList.maxByOrNull { it.netFlowInMonth }
        val worst = metricList.minByOrNull { it.netFlowInMonth }
        val totalMonths = metricList.size
        val avgWithdrawn = if (totalMonths > 0) metricList.map { it.withdrawnInMonth }.average() else 0.0

        return MonthlyReportSummary(
            monthlyMetrics = metricList,
            bestMonth = best,
            worstMonth = worst,
            totalMonthsTracked = totalMonths,
            averageMonthlyWithdrawn = avgWithdrawn
        )
    }

    private class MonthAccumulator(val yearMonth: String) {
        var invested: Double = 0.0
        var withdrawn: Double = 0.0
        var projectsStarted: Int = 0
        var withdrawalsCount: Int = 0
    }
}
