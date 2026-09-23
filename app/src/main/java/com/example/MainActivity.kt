package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.InvestmentProject
import com.example.data.model.ProjectWithWithdrawals
import com.example.ui.MainTab
import com.example.ui.dialogs.AddEditProjectDialog
import com.example.ui.dialogs.AddWithdrawalDialog
import com.example.ui.dialogs.MarkAsFallenDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MonthlyReportsScreen
import com.example.ui.screens.ProjectDetailScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.InvestmentViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                InverTrackApp()
            }
        }
    }
}

@Composable
fun InverTrackApp(
    viewModel: InvestmentViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(MainTab.DASHBOARD) }
    var selectedProjectId by remember { mutableStateOf<Long?>(null) }

    // Dialog states
    var showAddProjectDialog by remember { mutableStateOf(false) }
    var projectToEdit by remember { mutableStateOf<InvestmentProject?>(null) }
    var withdrawalTargetProject by remember { mutableStateOf<ProjectWithWithdrawals?>(null) }
    var showWithdrawalDialog by remember { mutableStateOf(false) }
    var projectToMarkFallen by remember { mutableStateOf<ProjectWithWithdrawals?>(null) }

    // Intercept back button if in detail view
    BackHandler(enabled = selectedProjectId != null) {
        selectedProjectId = null
    }

    val selectedProjectItem = state.projects.find { it.project.id == selectedProjectId }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
    ) {
        if (selectedProjectItem != null) {
            ProjectDetailScreen(
                projectItem = selectedProjectItem,
                onBackClick = { selectedProjectId = null },
                onEditClick = { project -> projectToEdit = project },
                onDeleteClick = { project ->
                    viewModel.deleteProject(project)
                    selectedProjectId = null
                },
                onAddWithdrawalClick = { item ->
                    withdrawalTargetProject = item
                    showWithdrawalDialog = true
                },
                onDeleteWithdrawalClick = { tx ->
                    viewModel.deleteWithdrawal(tx)
                },
                onMarkAsFallenClick = { item ->
                    projectToMarkFallen = item
                },
                onStatusChangeClick = { project, newStatus ->
                    viewModel.updateProjectStatus(project, newStatus)
                }
            )
        } else {
            Scaffold(
                containerColor = ObsidianBackground,
                bottomBar = {
                    NavigationBar(
                        containerColor = ObsidianSurface,
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        // Inicio / Dashboard
                        NavigationBarItem(
                            selected = currentTab == MainTab.DASHBOARD,
                            onClick = { currentTab = MainTab.DASHBOARD },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Dashboard,
                                    contentDescription = "Inicio"
                                )
                            },
                            label = { Text("Inicio", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldNeon,
                                selectedTextColor = EmeraldNeon,
                                indicatorColor = EmeraldContainer,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextSecondary
                            )
                        )

                        // Proyectos
                        NavigationBarItem(
                            selected = currentTab == MainTab.PROJECTS,
                            onClick = { currentTab = MainTab.PROJECTS },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.FolderSpecial,
                                    contentDescription = "Proyectos"
                                )
                            },
                            label = { Text("Proyectos", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldNeon,
                                selectedTextColor = EmeraldNeon,
                                indicatorColor = EmeraldContainer,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextSecondary
                            )
                        )

                        // Reportes
                        NavigationBarItem(
                            selected = currentTab == MainTab.REPORTS,
                            onClick = { currentTab = MainTab.REPORTS },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.BarChart,
                                    contentDescription = "Reportes"
                                )
                            },
                            label = { Text("Reportes", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldNeon,
                                selectedTextColor = EmeraldNeon,
                                indicatorColor = EmeraldContainer,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextSecondary
                            )
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        MainTab.DASHBOARD -> {
                            DashboardScreen(
                                state = state,
                                onAddProjectClick = { showAddProjectDialog = true },
                                onAddWithdrawalClick = { item ->
                                    withdrawalTargetProject = item
                                    showWithdrawalDialog = true
                                },
                                onProjectClick = { id -> selectedProjectId = id },
                                onViewAllProjectsClick = { currentTab = MainTab.PROJECTS },
                                onViewReportsClick = { currentTab = MainTab.REPORTS }
                            )
                        }

                        MainTab.PROJECTS -> {
                            ProjectsScreen(
                                state = state,
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onStatusFilterSelect = { viewModel.setStatusFilter(it) },
                                onCategoryFilterSelect = { viewModel.setCategoryFilter(it) },
                                onProjectClick = { id -> selectedProjectId = id },
                                onAddProjectClick = { showAddProjectDialog = true },
                                onAddWithdrawalClick = { item ->
                                    withdrawalTargetProject = item
                                    showWithdrawalDialog = true
                                }
                            )
                        }

                        MainTab.REPORTS -> {
                            MonthlyReportsScreen(
                                state = state,
                                onBackClick = { currentTab = MainTab.DASHBOARD }
                            )
                        }
                    }
                }
            }
        }

        // Add Project Dialog
        if (showAddProjectDialog) {
            AddEditProjectDialog(
                projectToEdit = null,
                onDismiss = { showAddProjectDialog = false },
                onConfirm = { name, category, initialCapital, startDate, riskLevel, notes ->
                    viewModel.addProject(
                        name = name,
                        category = category,
                        initialCapital = initialCapital,
                        startDate = startDate,
                        riskLevel = riskLevel,
                        notes = notes
                    )
                    showAddProjectDialog = false
                }
            )
        }

        // Edit Project Dialog
        if (projectToEdit != null) {
            AddEditProjectDialog(
                projectToEdit = projectToEdit,
                onDismiss = { projectToEdit = null },
                onConfirm = { name, category, initialCapital, startDate, riskLevel, notes ->
                    viewModel.updateProject(
                        project = projectToEdit!!,
                        name = name,
                        category = category,
                        initialCapital = initialCapital,
                        riskLevel = riskLevel,
                        notes = notes
                    )
                    projectToEdit = null
                }
            )
        }

        // Add Withdrawal Dialog
        if (showWithdrawalDialog) {
            AddWithdrawalDialog(
                projects = state.projects,
                preselectedProjectId = withdrawalTargetProject?.project?.id,
                onDismiss = {
                    showWithdrawalDialog = false
                    withdrawalTargetProject = null
                },
                onConfirm = { projId, amount, date, note ->
                    viewModel.addWithdrawal(
                        projectId = projId,
                        amount = amount,
                        date = date,
                        note = note
                    )
                    showWithdrawalDialog = false
                    withdrawalTargetProject = null
                }
            )
        }

        // Mark As Fallen Dialog
        if (projectToMarkFallen != null) {
            MarkAsFallenDialog(
                projectItem = projectToMarkFallen!!,
                onDismiss = { projectToMarkFallen = null },
                onConfirm = { project, reason, date ->
                    viewModel.markProjectAsFallen(
                        project = project,
                        reason = reason,
                        date = date
                    )
                    projectToMarkFallen = null
                }
            )
        }
    }
}
