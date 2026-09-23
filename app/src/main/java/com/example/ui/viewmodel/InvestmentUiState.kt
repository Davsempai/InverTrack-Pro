package com.example.ui.viewmodel

import com.example.data.model.MonthlyReportSummary
import com.example.data.model.PortfolioSummary
import com.example.data.model.ProjectWithWithdrawals

enum class FilterStatus(val label: String) {
    TODOS("Todos"),
    ACTIVOS("Activos"),
    CAIDOS("Caídos"),
    EN_GANANCIA("En Ganancia"),
    EN_PERDIDA("En Pérdida")
}

data class InvestmentUiState(
    val projects: List<ProjectWithWithdrawals> = emptyList(),
    val filteredProjects: List<ProjectWithWithdrawals> = emptyList(),
    val selectedProject: ProjectWithWithdrawals? = null,
    val portfolioSummary: PortfolioSummary = PortfolioSummary(),
    val monthlyReport: MonthlyReportSummary = MonthlyReportSummary(),
    val searchQuery: String = "",
    val selectedStatusFilter: FilterStatus = FilterStatus.TODOS,
    val selectedCategoryFilter: String = "Todas",
    val availableCategories: List<String> = listOf("Todas"),
    val isLoading: Boolean = false
)
