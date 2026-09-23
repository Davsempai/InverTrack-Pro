package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MonthlyMetric
import com.example.data.model.PortfolioSummary
import com.example.ui.components.MonthlyChartCanvas
import com.example.ui.theme.CrimsonLoss
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.InvestmentUiState
import java.util.Locale

@Composable
fun MonthlyReportsScreen(
    state: InvestmentUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val report = state.monthlyReport
    val summary = state.portfolioSummary
    val reversedMetrics = remember(report.monthlyMetrics) { report.monthlyMetrics.reversed() }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("monthly_reports_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Reportes de Rendimiento",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            ),
                            color = TextPrimary
                        )
                        Text(
                            text = "Rendimiento mensual acumulado y flujos",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                            color = TextSecondary
                        )
                    }
                }
            }

            // Interactive Chart
            item {
                MonthlyChartCanvas(
                    metrics = report.monthlyMetrics,
                    modifier = Modifier.testTag("reports_chart")
                )
            }

            // Highlights Row (Best month / Average)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Best month
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ObsidianSurfaceElevated)
                            .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = EmeraldNeon,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Mejor Mes",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = report.bestMonth?.displayLabel ?: "Sin datos",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            if (report.bestMonth != null) {
                                Text(
                                    text = "Flujo: +$${String.format(Locale.US, "%.2f", report.bestMonth.netFlowInMonth)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                                    color = EmeraldNeon
                                )
                            }
                        }
                    }

                    // Average Monthly Withdrawn
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ObsidianSurfaceElevated)
                            .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = EmeraldMint,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Promedio Cobros",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$${String.format(Locale.US, "%,.2f", report.averageMonthlyWithdrawn)}",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldNeon
                            )
                            Text(
                                text = "${report.totalMonthsTracked} periodos registrados",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            // Capital Allocation Card
            item {
                CapitalDistributionCard(summary = summary)
            }

            // Month-by-month Breakdown Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = EmeraldNeon,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Historial Mes a Mes",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }

                    Text(
                        text = "${report.monthlyMetrics.size} meses",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                        color = TextSecondary
                    )
                }
            }

            // Monthly rows in reverse order (newest first)
            items(reversedMetrics, key = { it.yearMonth }) { metric ->
                MonthlyMetricItemCard(metric = metric)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CapitalDistributionCard(
    summary: PortfolioSummary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ObsidianSurfaceElevated)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PieChart,
                    contentDescription = null,
                    tint = EmeraldNeon,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Distribución del Capital Total",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }
            Text(
                text = "$${String.format(Locale.US, "%,.2f", summary.totalInvested)}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        DistributionLine(
            label = "Capital Recuperado / Retirado",
            amount = summary.totalWithdrawn,
            color = EmeraldNeon,
            percent = if (summary.totalInvested > 0) (summary.totalWithdrawn / summary.totalInvested) * 100.0 else 0.0
        )

        Spacer(modifier = Modifier.height(10.dp))

        DistributionLine(
            label = "Capital Activo en Juego",
            amount = summary.activeCapitalAtRisk,
            color = CyanAccent,
            percent = if (summary.totalInvested > 0) (summary.activeCapitalAtRisk / summary.totalInvested) * 100.0 else 0.0
        )

        if (summary.totalCapitalLostInFallen > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            DistributionLine(
                label = "Capital Perdido en Caídos",
                amount = summary.totalCapitalLostInFallen,
                color = CrimsonLoss,
                percent = if (summary.totalInvested > 0) (summary.totalCapitalLostInFallen / summary.totalInvested) * 100.0 else 0.0
            )
        }
    }
}

@Composable
private fun DistributionLine(
    label: String,
    amount: Double,
    color: Color,
    percent: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = TextSecondary
            )
        }
        Text(
            text = "$${String.format(Locale.US, "%,.2f", amount)} (${String.format(Locale.US, "%.1f", percent)}%)",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = TextPrimary
        )
    }
}

@Composable
private fun MonthlyMetricItemCard(
    metric: MonthlyMetric,
    modifier: Modifier = Modifier
) {
    val isNetPositive = metric.netFlowInMonth >= 0
    val netColor = if (isNetPositive) EmeraldNeon else CrimsonLoss
    val cumColor = if (metric.cumulativeNetPerformance >= 0) EmeraldNeon else CrimsonLoss

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ObsidianSurfaceElevated)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = metric.displayLabel,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isNetPositive) EmeraldContainer else Color(0xFF2C0F12))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Flujo: ${if (isNetPositive) "+" else ""}$${String.format(Locale.US, "%,.2f", metric.netFlowInMonth)}",
                    color = netColor,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Invertido",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = TextSecondary
                )
                Text(
                    text = if (metric.investedInMonth > 0) "-$${String.format(Locale.US, "%,.2f", metric.investedInMonth)}" else "$0.00",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (metric.investedInMonth > 0) CrimsonLoss else TextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Retirado",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = TextSecondary
                )
                Text(
                    text = if (metric.withdrawnInMonth > 0) "+$${String.format(Locale.US, "%,.2f", metric.withdrawnInMonth)}" else "$0.00",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (metric.withdrawnInMonth > 0) EmeraldNeon else TextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Rend. Acumulado",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = TextSecondary
                )
                Text(
                    text = "${if (metric.cumulativeNetPerformance >= 0) "+" else ""}$${String.format(Locale.US, "%,.2f", metric.cumulativeNetPerformance)}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = cumColor
                    )
                )
            }
        }

        if (metric.withdrawalsCount > 0 || metric.projectsStartedCount > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${metric.withdrawalsCount} retiros realizados",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                    color = TextMuted
                )
                if (metric.projectsStartedCount > 0) {
                    Text(
                        text = "${metric.projectsStartedCount} nuevos proyectos",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                        color = TextMuted
                    )
                }
            }
        }
    }
}
