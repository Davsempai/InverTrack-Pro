package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.InvestmentProject
import com.example.data.model.ProjectWithWithdrawals
import com.example.ui.theme.CrimsonLoss
import com.example.ui.theme.CrimsonLossBorder
import com.example.ui.theme.CrimsonLossDark
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun MarkAsFallenDialog(
    projectItem: ProjectWithWithdrawals,
    onDismiss: () -> Unit,
    onConfirm: (project: InvestmentProject, reason: String, date: Long) -> Unit
) {
    val project = projectItem.project
    val unrecovered = projectItem.capitalRemainingToRecover
    val isProfit = projectItem.isProfit

    var reason by remember { mutableStateOf("") }
    val reasonsSuggestions = listOf(
        "Página cerrada / Cesó pagos",
        "Rugpull / Estafa comprobada",
        "Empresa en quiebra / Bancarrota",
        "Bloqueo de fondos indefinido",
        "Salida voluntaria con pérdidas"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(ObsidianSurfaceElevated)
                .border(1.dp, CrimsonLossBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
                .testTag("dialog_mark_as_fallen")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header with Warning Icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CrimsonLossDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = CrimsonLoss,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Declarar Proyecto Caído",
                            color = CrimsonLoss,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = project.name,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Financial Impact Warning Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CrimsonLossDark)
                        .border(1.dp, CrimsonLossBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        if (isProfit) {
                            Text(
                                text = "¡Saliste a tiempo en ganancia!",
                                color = EmeraldNeon,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "A pesar de que el proyecto cayó, ya habías retirado $${String.format(Locale.US, "%,.2f", projectItem.totalWithdrawn)} (ganancia neta de +$${String.format(Locale.US, "%,.2f", projectItem.netProfitLoss)}).",
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        } else {
                            Text(
                                text = "Impacto del Cierre",
                                color = CrimsonLoss,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "El capital no recuperado de $${String.format(Locale.US, "%,.2f", unrecovered)} se registrará como pérdida definitiva en tu portafolio global.",
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Motivo de caída
                Text(
                    text = "Motivo de la caída *",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = { Text("Indica la causa del cierre...", fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_fallen_reason"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianSurface,
                        unfocusedContainerColor = ObsidianSurface,
                        focusedBorderColor = CrimsonLoss,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Suggestions
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    reasonsSuggestions.take(3).forEach { suggestion ->
                        OutlinedButton(
                            onClick = { reason = suggestion },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextSecondary
                            )
                        ) {
                            Text(text = suggestion, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val finalReason = reason.ifBlank { "Proyecto caído / cesó operaciones" }
                            onConfirm(project, finalReason, System.currentTimeMillis())
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_confirm_mark_fallen"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CrimsonLoss,
                            contentColor = TextPrimary
                        )
                    ) {
                        Text("Confirmar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
