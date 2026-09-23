package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PortfolioSummary
import com.example.data.model.ProjectWithWithdrawals
import com.example.ui.components.MonthlyChartCanvas
import com.example.ui.components.ProjectItemCard
import com.example.ui.components.StatMetricCard
import com.example.ui.theme.CrimsonLoss
import com.example.ui.theme.CrimsonLossDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.InvestmentUiState
import java.util.Locale

@Composable
fun DashboardScreen(
    state: InvestmentUiState,
    onAddProjectClick: () -> Unit,
    onAddWithdrawalClick: (ProjectWithWithdrawals?) -> Unit,
    onProjectClick: (Long) -> Unit,
    onViewAllProjectsClick: () -> Unit,
    onViewReportsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val summary = state.portfolioSummary

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        containerColor = ObsidianBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProjectClick,
                containerColor = EmeraldNeon,
                contentColor = ObsidianBackground,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_project_dashboard")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nuevo Proyecto",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Proyecto",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldNeon)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "INVERTRACK PRO",
                                color = EmeraldNeon,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }
                        Text(
                            text = "Control de Inversiones",
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Quick Action: Register withdrawal
                    OutlinedButton(
                        onClick = { onAddWithdrawalClick(null) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldNeon),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldNeon.copy(alpha = 0.5f))
                    ) {
                        Icon(imageVector = Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Retiro", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Portfolio Master Hero Card (Net Profit / Loss Banner)
            item {
                PortfolioHeroCard(summary = summary)
            }

            // Stat Metrics Grid (4 Key indicators)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricCard(
                        title = "Invertido Total",
                        value = summary.totalInvested,
                        currency = summary.currency,
                        icon = Icons.Default.Payments,
                        subtitle = "${summary.totalProjectsCount} proyectos",
                        accentColor = CyanAccent,
                        modifier = Modifier.weight(1f)
                    )

                    StatMetricCard(
                        title = "Retirado Total",
                        value = summary.totalWithdrawn,
                        currency = summary.currency,
                        icon = Icons.Default.TrendingUp,
                        subtitle = "${summary.recoveryPercentage.toInt()}% recuperado",
                        accentColor = EmeraldNeon,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricCard(
                        title = "Capital en Riesgo",
                        value = summary.activeCapitalAtRisk,
                        currency = summary.currency,
                        icon = Icons.Default.Shield,
                        subtitle = "${summary.activeProjectsCount} activos",
                        accentColor = EmeraldMint,
                        modifier = Modifier.weight(1f)
                    )

                    StatMetricCard(
                        title = "Pérdida en Caídos",
                        value = summary.totalCapitalLostInFallen,
                        currency = summary.currency,
                        icon = Icons.Default.Warning,
                        subtitle = "${summary.fallenProjectsCount} caídos",
                        accentColor = CrimsonLoss,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Chart Preview & Button to Full Reports
            if (state.monthlyReport.monthlyMetrics.isNotEmpty()) {
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rendimiento Acumulado",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.clickable { onViewReportsClick() },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Ver Reportes",
                                    color = EmeraldNeon,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = EmeraldNeon,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        MonthlyChartCanvas(
                            metrics = state.monthlyReport.monthlyMetrics,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Section Header: Active & Highlighted Projects
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tus Proyectos",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.clickable { onViewAllProjectsClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ver todos (${state.projects.size})",
                            color = EmeraldNeon,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = EmeraldNeon,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Project Items (show top 5)
            if (state.projects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ObsidianSurfaceElevated)
                            .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Aún no has registrado proyectos.",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onAddProjectClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldNeon,
                                    contentColor = ObsidianBackground
                                )
                            ) {
                                Text("Registrar Primer Proyecto", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                items(state.projects.take(5), key = { it.project.id }) { item ->
                    ProjectItemCard(
                        projectItem = item,
                        onProjectClick = onProjectClick,
                        onAddWithdrawalClick = { onAddWithdrawalClick(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PortfolioHeroCard(summary: PortfolioSummary) {
    val isProfit = summary.isNetProfit
    val net = summary.netProfitLoss

    val gradientColors = if (isProfit) {
        listOf(
            Color(0xFF072B1A),
            Color(0xFF04180E),
            ObsidianSurface
        )
    } else {
        listOf(
            Color(0xFF2E0C0C),
            Color(0xFF190606),
            ObsidianSurface
        )
    }

    val glowBorder = if (isProfit) EmeraldNeon.copy(alpha = 0.5f) else CrimsonLoss.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(gradientColors))
            .border(1.dp, glowBorder, RoundedCornerShape(20.dp))
            .padding(20.dp)
            .testTag("portfolio_hero_card")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BALANCE NETO TOTAL",
                    color = if (isProfit) EmeraldNeon else CrimsonLoss,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                // Status Badge (Ganancia / Pérdida)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isProfit) EmeraldContainer else CrimsonLossDark)
                        .border(
                            0.8.dp,
                            if (isProfit) EmeraldNeon.copy(alpha = 0.6f) else CrimsonLoss.copy(alpha = 0.6f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isProfit) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (isProfit) EmeraldNeon else CrimsonLoss,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isProfit) "EN GANANCIA" else if (net < 0) "EN PÉRDIDA" else "EQUILIBRIO",
                            color = if (isProfit) EmeraldNeon else CrimsonLoss,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Large Net Number
            val prefix = if (net > 0) "+$" else if (net < 0) "-$" else "$"
            Text(
                text = String.format(Locale.US, "%s%,.2f", prefix, kotlin.math.abs(net)),
                color = if (isProfit) EmeraldNeon else if (net < 0) CrimsonLoss else TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Overall ROI %
            Text(
                text = String.format(
                    Locale.US,
                    "Retorno Global: %s%.1f%% sobre capital invertido",
                    if (summary.overallRoiPercentage > 0) "+" else "",
                    summary.overallRoiPercentage
                ),
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Overall Recovery Progress Bar
            val recoveryFraction = (summary.recoveryPercentage / 100.0).coerceIn(0.0, 1.0).toFloat()
            LinearProgressIndicator(
                progress = { recoveryFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isProfit) EmeraldNeon else CrimsonLoss,
                trackColor = ObsidianSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Recuperado: ${String.format(Locale.US, "$%,.0f", summary.totalWithdrawn)}",
                    color = EmeraldNeon,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Invertido: ${String.format(Locale.US, "$%,.0f", summary.totalInvested)}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
