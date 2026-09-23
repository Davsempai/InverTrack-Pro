package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MonthlyMetric
import com.example.ui.theme.CrimsonLoss
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianBorderGlow
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

enum class ChartType(val label: String) {
    CUMULATIVE_PERFORMANCE("Rendimiento Acumulado"),
    MONTHLY_FLOWS("Invertido vs Retirado")
}

@Composable
fun MonthlyChartCanvas(
    metrics: List<MonthlyMetric>,
    modifier: Modifier = Modifier
) {
    var selectedChartType by remember { mutableStateOf(ChartType.CUMULATIVE_PERFORMANCE) }
    var selectedIndex by remember { mutableIntStateOf(if (metrics.isNotEmpty()) metrics.size - 1 else -1) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ObsidianSurfaceElevated)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Chart Selector Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GRÁFICA DE RENDIMIENTO",
                color = EmeraldMint,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = selectedChartType == ChartType.CUMULATIVE_PERFORMANCE,
                    onClick = { selectedChartType = ChartType.CUMULATIVE_PERFORMANCE },
                    label = { Text("Acumulado", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldContainer,
                        selectedLabelColor = EmeraldNeon,
                        containerColor = ObsidianSurface,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedChartType == ChartType.CUMULATIVE_PERFORMANCE,
                        borderColor = ObsidianBorder,
                        selectedBorderColor = EmeraldNeon.copy(alpha = 0.5f)
                    )
                )

                FilterChip(
                    selected = selectedChartType == ChartType.MONTHLY_FLOWS,
                    onClick = { selectedChartType = ChartType.MONTHLY_FLOWS },
                    label = { Text("Flujos", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldContainer,
                        selectedLabelColor = EmeraldNeon,
                        containerColor = ObsidianSurface,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedChartType == ChartType.MONTHLY_FLOWS,
                        borderColor = ObsidianBorder,
                        selectedBorderColor = EmeraldNeon.copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (metrics.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay datos de inversiones o retiros todavía.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        } else {
            // Interactive tooltip of selected month
            val selectedMetric = metrics.getOrNull(selectedIndex) ?: metrics.lastOrNull()

            AnimatedVisibility(
                visible = selectedMetric != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (selectedMetric != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ObsidianSurface)
                            .border(0.8.dp, ObsidianBorderGlow, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = selectedMetric.displayLabel,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Invertido: ${String.format(Locale.US, "$%,.0f", selectedMetric.investedInMonth)}  |  Retirado: ${String.format(Locale.US, "$%,.0f", selectedMetric.withdrawnInMonth)}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                val netCumulative = selectedMetric.cumulativeNetPerformance
                                val isPos = netCumulative >= 0
                                Text(
                                    text = if (selectedChartType == ChartType.CUMULATIVE_PERFORMANCE) "Neto Acumulado" else "Flujo Mes",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                                val displayValue = if (selectedChartType == ChartType.CUMULATIVE_PERFORMANCE) netCumulative else selectedMetric.netFlowInMonth
                                val isValPos = displayValue >= 0
                                Text(
                                    text = String.format(
                                        Locale.US,
                                        "%s$%,.2f",
                                        if (isValPos) "+$" else "-$",
                                        kotlin.math.abs(displayValue)
                                    ),
                                    color = if (isValPos) EmeraldNeon else CrimsonLoss,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Canvas
            val count = metrics.size
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .pointerInput(count) {
                            detectTapGestures { offset ->
                                val step = size.width / count
                                val index = (offset.x / step).toInt().coerceIn(0, count - 1)
                                selectedIndex = index
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height
                    val bottomPadding = 35f
                    val topPadding = 20f
                    val chartHeight = h - bottomPadding - topPadding

                    // Draw subtle grid lines
                    val gridSteps = 3
                    for (i in 0..gridSteps) {
                        val y = topPadding + (chartHeight / gridSteps) * i
                        drawLine(
                            color = ObsidianBorder.copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                        )
                    }

                    if (selectedChartType == ChartType.CUMULATIVE_PERFORMANCE) {
                        // CUMULATIVE LINE CHART
                        val values = metrics.map { it.cumulativeNetPerformance }
                        val minVal = minOf(0.0, values.minOrNull() ?: 0.0)
                        val maxVal = maxOf(100.0, values.maxOrNull() ?: 100.0)
                        val range = (maxVal - minVal).coerceAtLeast(1.0)

                        val zeroY = topPadding + (chartHeight * (1f - ((0.0 - minVal) / range).toFloat()))

                        // Draw zero baseline
                        drawLine(
                            color = TextMuted.copy(alpha = 0.7f),
                            start = Offset(0f, zeroY),
                            end = Offset(w, zeroY),
                            strokeWidth = 1.2f
                        )

                        val points = mutableListOf<Offset>()
                        val stepX = if (count > 1) w / (count - 1) else w / 2

                        metrics.forEachIndexed { i, m ->
                            val x = if (count > 1) i * stepX else w / 2
                            val norm = ((m.cumulativeNetPerformance - minVal) / range).toFloat()
                            val y = topPadding + (chartHeight * (1f - norm))
                            points.add(Offset(x, y))
                        }

                        // Gradient fill path
                        if (points.isNotEmpty()) {
                            val fillPath = Path()
                            fillPath.moveTo(points.first().x, zeroY)
                            points.forEach { fillPath.lineTo(it.x, it.y) }
                            fillPath.lineTo(points.last().x, zeroY)
                            fillPath.close()

                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        EmeraldNeon.copy(alpha = 0.25f),
                                        EmeraldNeon.copy(alpha = 0.02f)
                                    ),
                                    startY = topPadding,
                                    endY = zeroY.coerceAtLeast(topPadding + 10f)
                                )
                            )

                            // Stroke line path
                            val strokePath = Path()
                            strokePath.moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                val prev = points[i - 1]
                                val curr = points[i]
                                val cx = (prev.x + curr.x) / 2
                                strokePath.cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                            }

                            drawPath(
                                path = strokePath,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(EmeraldMint, EmeraldNeon)
                                ),
                                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Draw point circles
                            points.forEachIndexed { idx, p ->
                                val isSelected = idx == selectedIndex
                                val pointColor = if (metrics[idx].cumulativeNetPerformance >= 0) EmeraldNeon else CrimsonLoss
                                drawCircle(
                                    color = if (isSelected) Color.White else pointColor,
                                    radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                                    center = p
                                )
                                drawCircle(
                                    color = ObsidianSurface,
                                    radius = if (isSelected) 3.5.dp.toPx() else 2.dp.toPx(),
                                    center = p
                                )
                            }
                        }

                    } else {
                        // MONTHLY FLOWS DUAL BAR CHART
                        val maxFlow = maxOf(
                            100.0,
                            metrics.maxOfOrNull { maxOf(it.investedInMonth, it.withdrawnInMonth) } ?: 100.0
                        )

                        val colWidth = w / count
                        val barWidth = (colWidth * 0.28f).coerceAtMost(16.dp.toPx())

                        metrics.forEachIndexed { idx, m ->
                            val cx = idx * colWidth + (colWidth / 2)
                            val isSelected = idx == selectedIndex

                            // Bar 1: Invested (Dark Amber/Slate)
                            val invHeight = ((m.investedInMonth / maxFlow) * chartHeight).toFloat()
                            val invY = topPadding + (chartHeight - invHeight)
                            val invX = cx - barWidth - 2f

                            drawRoundRect(
                                color = if (isSelected) Color(0xFFFFA726) else Color(0x99FFA726),
                                topLeft = Offset(invX, invY),
                                size = Size(barWidth, invHeight),
                                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                            )

                            // Bar 2: Withdrawn (Vivid Emerald Neon)
                            val withHeight = ((m.withdrawnInMonth / maxFlow) * chartHeight).toFloat()
                            val withY = topPadding + (chartHeight - withHeight)
                            val withX = cx + 2f

                            drawRoundRect(
                                color = if (isSelected) EmeraldNeon else EmeraldMint.copy(alpha = 0.85f),
                                topLeft = Offset(withX, withY),
                                size = Size(barWidth, withHeight),
                                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Fast Month X-Axis labels in native Compose Row (zero Canvas text measuring overhead)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val labelStep = if (count > 6) 2 else 1
                metrics.forEachIndexed { idx, m ->
                    if (idx % labelStep == 0 || idx == count - 1) {
                        Text(
                            text = m.displayLabel.take(3),
                            color = if (idx == selectedIndex) EmeraldNeon else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = if (idx == selectedIndex) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedChartType == ChartType.MONTHLY_FLOWS) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFA726)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Invertido", color = TextSecondary, fontSize = 11.sp)

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldNeon))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Retirado", color = TextSecondary, fontSize = 11.sp)
                } else {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldNeon))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rendimiento Neto Acumulado (Ganancia / Pérdida)", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}
