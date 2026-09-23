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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InvestmentProject
import com.example.data.model.ProjectStatus
import com.example.data.model.ProjectWithWithdrawals
import com.example.data.model.WithdrawalTransaction
import com.example.ui.components.RiskChip
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CrimsonLoss
import com.example.ui.theme.CrimsonLossBorder
import com.example.ui.theme.CrimsonLossDark
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectItem: ProjectWithWithdrawals,
    onBackClick: () -> Unit,
    onEditClick: (InvestmentProject) -> Unit,
    onDeleteClick: (InvestmentProject) -> Unit,
    onAddWithdrawalClick: (ProjectWithWithdrawals) -> Unit,
    onDeleteWithdrawalClick: (WithdrawalTransaction) -> Unit,
    onMarkAsFallenClick: (ProjectWithWithdrawals) -> Unit,
    onStatusChangeClick: (InvestmentProject, ProjectStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val project = projectItem.project
    val isFallen = project.status == ProjectStatus.CAIDO
    val isProfit = projectItem.isProfit
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES")) }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var withdrawalToDelete by remember { mutableStateOf<WithdrawalTransaction?>(null) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("project_detail_screen"),
        containerColor = ObsidianBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = project.name,
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = project.category,
                            color = EmeraldMint,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEditClick(project) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = TextSecondary
                        )
                    }
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = CrimsonLoss
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fallen Project Alert Banner (if Fallen)
            if (isFallen) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CrimsonLossDark)
                            .border(1.dp, CrimsonLossBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = CrimsonLoss,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PROYECTO CAÍDO / CERRADO",
                                    color = CrimsonLoss,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            if (!project.fallenReason.isNullOrBlank()) {
                                Text(
                                    text = "Causa: ${project.fallenReason}",
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                )
                            }
                            if (project.fallenDate != null) {
                                Text(
                                    text = "Fecha de caída: ${dateFormat.format(Date(project.fallenDate))}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Reactivate Button
                            OutlinedButton(
                                onClick = { onStatusChangeClick(project, ProjectStatus.ACTIVO) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldNeon),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldNeon.copy(alpha = 0.5f))
                            ) {
                                Text("Reactivar Proyecto", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Hero Metric Card: Net Result & Recovery
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    ObsidianSurfaceElevated,
                                    ObsidianSurface
                                )
                            )
                        )
                        .border(
                            1.dp,
                            if (isProfit) EmeraldNeon.copy(alpha = 0.5f) else ObsidianBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusBadge(status = project.status)
                            RiskChip(riskLevel = project.riskLevel)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isFallen) "RESULTADO FINAL" else "BALANCE NETO",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        val net = projectItem.netProfitLoss
                        val prefix = if (net > 0) "+$" else if (net < 0) "-$" else "$"
                        Text(
                            text = String.format(Locale.US, "%s%,.2f", prefix, kotlin.math.abs(net)),
                            color = if (net > 0) EmeraldNeon else if (net < 0) CrimsonLoss else TextPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )

                        Text(
                            text = String.format(
                                Locale.US,
                                "ROI: %s%.1f%% sobre inversión inicial",
                                if (projectItem.roiPercentage > 0) "+" else "",
                                projectItem.roiPercentage
                            ),
                            color = if (isProfit) EmeraldNeon else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Bar
                        val fraction = (projectItem.recoveryPercentage / 100.0).coerceIn(0.0, 1.0).toFloat()
                        LinearProgressIndicator(
                            progress = { fraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (isProfit) EmeraldNeon else if (isFallen) CrimsonLoss else EmeraldMint,
                            trackColor = ObsidianSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${projectItem.recoveryPercentage.toInt()}% Recuperado",
                                color = if (isProfit) EmeraldNeon else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isProfit) {
                                Text(
                                    text = "¡100% de capital recuperado + ganancias!",
                                    color = EmeraldNeon,
                                    fontSize = 11.sp
                                )
                            } else {
                                Text(
                                    text = String.format(
                                        Locale.US,
                                        "Faltan $%,.2f",
                                        projectItem.capitalRemainingToRecover
                                    ),
                                    color = if (isFallen) CrimsonLoss else TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Quick Status Controls & Mark As Fallen Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isFallen) {
                        Button(
                            onClick = { onMarkAsFallenClick(projectItem) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CrimsonLossDark,
                                contentColor = CrimsonLoss
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonLossBorder)
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Marcar Caído", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = { onAddWithdrawalClick(projectItem) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldContainer,
                            contentColor = EmeraldNeon
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldNeon.copy(alpha = 0.5f))
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Nuevo Retiro", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // Project Info Details
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ObsidianSurfaceElevated)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "DETALLES DEL PROYECTO",
                            color = EmeraldMint,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Capital Inicial Invertido", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = String.format(Locale.US, "$%,.2f", project.initialCapital),
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Retirado", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = String.format(Locale.US, "$%,.2f", projectItem.totalWithdrawn),
                                color = EmeraldNeon,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Fecha de Inicio", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = dateFormat.format(Date(project.startDate)),
                                color = TextPrimary,
                                fontSize = 13.sp
                            )
                        }

                        if (project.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Notas:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = project.notes,
                                color = TextPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Withdrawal History Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Historial de Retiros (${projectItem.withdrawals.size})",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total: ${String.format(Locale.US, "$%,.2f", projectItem.totalWithdrawn)}",
                        color = EmeraldNeon,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (projectItem.withdrawals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ObsidianSurfaceElevated)
                            .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Aún no has registrado retiros para este proyecto.",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { onAddWithdrawalClick(projectItem) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldNeon,
                                    contentColor = ObsidianBackground
                                )
                            ) {
                                Text("Registrar Primer Retiro", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                items(projectItem.withdrawals.sortedByDescending { it.date }, key = { it.id }) { tx ->
                    WithdrawalItemRow(
                        transaction = tx,
                        dateFormat = dateFormat,
                        onDeleteClick = { withdrawalToDelete = tx }
                    )
                }
            }
        }
    }

    // Delete Project Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("¿Eliminar Proyecto?", color = TextPrimary) },
            text = {
                Text(
                    "Se eliminará el proyecto '${project.name}' y todos sus registros de retiros asociados.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteClick(project)
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonLoss)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancelar")
                }
            },
            containerColor = ObsidianSurfaceElevated
        )
    }

    // Delete Withdrawal Confirmation Dialog
    if (withdrawalToDelete != null) {
        AlertDialog(
            onDismissRequest = { withdrawalToDelete = null },
            title = { Text("¿Eliminar Retiro?", color = TextPrimary) },
            text = {
                Text(
                    "Se revertirá el retiro de ${String.format(Locale.US, "$%,.2f", withdrawalToDelete!!.amount)}.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteWithdrawalClick(withdrawalToDelete!!)
                        withdrawalToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonLoss)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { withdrawalToDelete = null }) {
                    Text("Cancelar")
                }
            },
            containerColor = ObsidianSurfaceElevated
        )
    }
}

@Composable
private fun WithdrawalItemRow(
    transaction: WithdrawalTransaction,
    dateFormat: SimpleDateFormat,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ObsidianSurfaceElevated)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(EmeraldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = EmeraldNeon,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (transaction.note.isNotBlank()) transaction.note else "Retiro de Fondos",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = dateFormat.format(Date(transaction.date)),
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = String.format(Locale.US, "+$%,.2f", transaction.amount),
                    color = EmeraldNeon,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar retiro",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
