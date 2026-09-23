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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TreeMap

class InvestmentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InvestmentRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = InvestmentRepository(db.investmentDao())
        // Clean out any sample/test data immediately so the app is 100% clean and fresh
        viewModelScope.launch(Dispatchers.IO) {
            repository.cleanSampleDataIfPresent()
        }
    }

    private data class CachedPortfolioData(
        val projects: List<ProjectWithWithdrawals> = emptyList(),
        val recentProjects: List<ProjectWithWithdrawals> = emptyList(),
        val summary: PortfolioSummary = PortfolioSummary(),
        val monthlyReport: MonthlyReportSummary = MonthlyReportSummary(emptyList(), null, null, 0, 0.0),
        val categories: List<String> = listOf("Todas")
    )

    private val cachedDataFlow: StateFlow<CachedPortfolioData> = repository.allProjectsWithWithdrawals
        .map { projectsList ->
            val categories = mutableListOf("Todas")
            categories.addAll(projectsList.map { it.project.category }.distinct().sorted())
            val summary = repository.computePortfolioSummary(projectsList)
            val monthlyReport = computeMonthlyReport(projectsList)
            CachedPortfolioData(
                projects = projectsList,
                recentProjects = projectsList.take(5),
                summary = summary,
                monthlyReport = monthlyReport,
                categories = categories
            )
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = CachedPortfolioData()
        )

    private val _searchQuery = MutableStateFlow("")
    private val _selectedStatusFilter = MutableStateFlow(FilterStatus.TODOS)
    private val _selectedCategoryFilter = MutableStateFlow("Todas")
    private val _selectedProjectId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<InvestmentUiState> = combine(
        cachedDataFlow,
        _searchQuery,
        _selectedStatusFilter,
        _selectedCategoryFilter,
        _selectedProjectId
    ) { cachedData, query, statusFilter, categoryFilter, selectedId ->
        // Apply filters fast in memory
        val filtered = if (query.isBlank() && statusFilter == FilterStatus.TODOS && categoryFilter == "Todas") {
            cachedData.projects
        } else {
            cachedData.projects.filter { item ->
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
        }

        val selectedProjectItem = if (selectedId != null) {
            cachedData.projects.firstOrNull { it.project.id == selectedId }
        } else null

        InvestmentUiState(
            projects = cachedData.projects,
            filteredProjects = filtered,
            selectedProject = selectedProjectItem,
            portfolioSummary = cachedData.summary,
            monthlyReport = cachedData.monthlyReport,
            searchQuery = query,
            selectedStatusFilter = statusFilter,
            selectedCategoryFilter = categoryFilter,
            availableCategories = cachedData.categories,
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

    fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllData()
            _selectedProjectId.value = null
        }
    }

    fun addProject(
        name: String,
        category: String,
        initialCapital: Double,
        startDate: Long = System.currentTimeMillis(),
        riskLevel: RiskLevel = RiskLevel.MEDIO,
        notes: String = ""
    ) {
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
            repository.addWithdrawal(
                projectId = projectId,
                amount = amount,
                date = date,
                note = note.trim()
            )
        }
    }

    fun deleteWithdrawal(transaction: WithdrawalTransaction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWithdrawal(transaction)
        }
    }

    fun markProjectAsFallen(
        project: InvestmentProject,
        reason: String,
        date: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markProjectAsFallen(project, reason, date)
        }
    }

    fun updateProjectStatus(project: InvestmentProject, newStatus: ProjectStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProjectStatus(project, newStatus)
        }
    }

    private fun computeMonthlyReport(projects: List<ProjectWithWithdrawals>): MonthlyReportSummary {
        if (projects.isEmpty()) {
            return MonthlyReportSummary(emptyList(), null, null, 0, 0.0)
        }

        val monthsMap = TreeMap<String, MonthAccumulator>()

        for (item in projects) {
            val p = item.project
            val ym = synchronized(lock) { ymFormat.format(Date(p.startDate)) }
            val acc = monthsMap.getOrPut(ym) { MonthAccumulator(ym) }
            acc.invested += p.initialCapital
            acc.projectsStarted++

            for (w in item.withdrawals) {
                val wym = synchronized(lock) { ymFormat.format(Date(w.date)) }
                val wAcc = monthsMap.getOrPut(wym) { MonthAccumulator(wym) }
                wAcc.withdrawn += w.amount
                wAcc.withdrawalsCount++
            }
        }

        var runningInvested = 0.0
        var runningWithdrawn = 0.0
        val metricList = ArrayList<MonthlyMetric>(monthsMap.size)

        for ((ym, acc) in monthsMap) {
            runningInvested += acc.invested
            runningWithdrawn += acc.withdrawn
            val runningNet = runningWithdrawn - runningInvested

            val displayLabel = try {
                val parsed = synchronized(lock) { ymFormat.parse(ym) }
                if (parsed != null) {
                    synchronized(lock) { displayFormat.format(parsed) }.replaceFirstChar { it.uppercase() }
                } else ym
            } catch (_: Exception) {
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

    companion object {
        private val lock = Any()
        private val ymFormat = SimpleDateFormat("yyyy-MM", Locale.US)
        private val displayFormat = SimpleDateFormat("MMM yyyy", Locale("es", "ES"))
    }
}
