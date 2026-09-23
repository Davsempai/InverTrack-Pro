package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectStatus
import com.example.data.model.RiskLevel
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SignalCyan
import com.example.ui.theme.SignalRed
import com.example.ui.theme.SignalYellow

private data class StatusStyle(
    val bgColor: Color,
    val textColor: Color,
    val borderColor: Color,
    val icon: ImageVector
)

@Composable
fun StatusBadge(
    status: ProjectStatus,
    modifier: Modifier = Modifier
) {
    val style = when (status) {
        ProjectStatus.ACTIVO -> StatusStyle(
            bgColor = Color(0xFF092716),
            textColor = NeonGreen,
            borderColor = Color(0xFF1B5935),
            icon = Icons.Default.CheckCircle
        )
        ProjectStatus.CAIDO -> StatusStyle(
            bgColor = Color(0xFF2E0C10),
            textColor = SignalRed,
            borderColor = Color(0xFF5E1B22),
            icon = Icons.Default.Warning
        )
        ProjectStatus.PAUSADO -> StatusStyle(
            bgColor = Color(0xFF291E08),
            textColor = SignalYellow,
            borderColor = Color(0xFF563D10),
            icon = Icons.Default.PauseCircle
        )
        ProjectStatus.FINALIZADO -> StatusStyle(
            bgColor = Color(0xFF06222B),
            textColor = SignalCyan,
            borderColor = Color(0xFF0E4759),
            icon = Icons.Default.CheckCircle
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(style.bgColor)
            .border(1.dp, style.borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = style.icon,
            contentDescription = status.displayName,
            tint = style.textColor,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = status.displayName.uppercase(),
            color = style.textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun RiskBadge(
    risk: RiskLevel,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (risk) {
        RiskLevel.BAJO -> Pair(NeonGreen, "Riesgo Bajo")
        RiskLevel.MEDIO -> Pair(SignalYellow, "Riesgo Medio")
        RiskLevel.ALTO -> Pair(SignalRed, "Riesgo Alto")
        RiskLevel.ESPECULATIVO -> Pair(Color(0xFFFF1744), "Especulativo")
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .border(0.8.dp, color.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun RiskChip(
    riskLevel: RiskLevel,
    modifier: Modifier = Modifier
) {
    RiskBadge(risk = riskLevel, modifier = modifier)
}
