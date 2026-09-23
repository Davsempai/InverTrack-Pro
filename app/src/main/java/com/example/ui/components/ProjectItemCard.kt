package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.model.ProjectStatus
import com.example.data.model.ProjectWithWithdrawals
import com.example.ui.theme.CrimsonLoss
import com.example.ui.theme.CrimsonLossBorder
import com.example.ui.theme.CrimsonLossDark
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

private val ItemCardShape = RoundedCornerShape(16.dp)
private val ItemCardGradient = Brush.verticalGradient(
    colors = listOf(
        ObsidianSurfaceElevated,
        ObsidianSurface
    )
)

@Composable
fun ProjectItemCard(
    projectItem: ProjectWithWithdrawals,
    onProjectClick: (Long) -> Unit,
    onAddWithdrawalClick: (ProjectWithWithdrawals) -> Unit,
    modifier: Modifier = Modifier
) {
    val project = projectItem.project
    val isFallen = project.status == ProjectStatus.CAIDO
    val isProfit = projectItem.isProfit
    val isBreakEven = projectItem.isBreakEven

    val borderColor = when {
        isFallen -> CrimsonLossBorder
        isProfit -> EmeraldNeon.copy(alpha = 0.4f)
        else -> ObsidianBorder
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(ItemCardShape)
            .background(ItemCardGradient)
            .border(1.dp, borderColor, ItemCardShape)
            .clickable { onProjectClick(project.id) }
            .padding(16.dp)
            .testTag("project_item_${project.id}")
    ) {
        Column {
            // Header Row: Category + Name + StatusBadge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.category.uppercase(),
                        color = EmeraldMint,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = project.name,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                StatusBadge(status = project.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Financial Metrics Grid: Invertido | Retirado | Balance Neto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Invertido
                Column {
                    Text(
                        text = "Invertido",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = String.format(Locale.US, "$%,.2f", project.initialCapital),
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Recuperado / Retirado
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Recuperado",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = String.format(Locale.US, "$%,.2f", projectItem.totalWithdrawn),
                        color = EmeraldNeon,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Net Profit / Loss
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isFallen) "Resultado Final" else "Balance Neto",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    val net = projectItem.netProfitLoss
                    val netColor = if (net > 0) EmeraldNeon else if (net < 0) CrimsonLoss else TextPrimary
                    val netPrefix = if (net > 0) "+$" else if (net < 0) "-$" else "$"
                    Text(
                        text = String.format(Locale.US, "$netPrefix%,.2f", kotlin.math.abs(net)),
                        color = netColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Recovery Progress Bar
            val recoveryFraction = (projectItem.recoveryPercentage / 100.0).coerceIn(0.0, 1.0).toFloat()
            LinearProgressIndicator(
                progress = { recoveryFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isProfit) EmeraldNeon else if (isFallen) CrimsonLoss else EmeraldMint,
                trackColor = ObsidianSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${projectItem.recoveryPercentage.toInt()}% Recuperado",
                    color = if (isProfit) EmeraldNeon else TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                if (isProfit) {
                    Text(
                        text = String.format(Locale.US, "+%.1f%% ROI", projectItem.roiPercentage),
                        color = EmeraldNeon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (isFallen) {
                    Text(
                        text = String.format(Locale.US, "Pérdida: -$%,.2f", projectItem.capitalRemainingToRecover),
                        color = CrimsonLoss,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = String.format(Locale.US, "Faltan $%,.2f", projectItem.capitalRemainingToRecover),
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Fallen Alert Banner inside card
            if (isFallen && !project.fallenReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CrimsonLossDark)
                        .border(0.8.dp, CrimsonLossBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = CrimsonLoss,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Caído: ${project.fallenReason}",
                            color = CrimsonLoss,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onAddWithdrawalClick(projectItem) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldContainer,
                        contentColor = EmeraldNeon
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Retiro", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.clickable { onProjectClick(project.id) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detalles",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
