package com.example.ui.dialogs

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.InvestmentProject
import com.example.data.model.RiskLevel
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AddEditProjectDialog(
    projectToEdit: InvestmentProject? = null,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        category: String,
        initialCapital: Double,
        startDate: Long,
        riskLevel: RiskLevel,
        notes: String
    ) -> Unit
) {
    val isEditMode = projectToEdit != null

    var name by remember { mutableStateOf(projectToEdit?.name ?: "") }
    var category by remember { mutableStateOf(projectToEdit?.category ?: "Crypto & DeFi") }
    var capitalText by remember {
        mutableStateOf(if (projectToEdit != null) projectToEdit.initialCapital.toString() else "")
    }
    var riskLevel by remember { mutableStateOf(projectToEdit?.riskLevel ?: RiskLevel.MEDIO) }
    var notes by remember { mutableStateOf(projectToEdit?.notes ?: "") }

    var nameError by remember { mutableStateOf(false) }
    var capitalError by remember { mutableStateOf(false) }

    val categories = listOf("Crypto & DeFi", "Trading & Bots", "Bienes Raíces", "Startups", "Acciones", "Forex", "Otro")

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
                .border(1.dp, ObsidianBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
                .testTag("dialog_add_edit_project")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditMode) "Editar Proyecto" else "Nuevo Proyecto",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Project Name
                Text(text = "Nombre del Proyecto *", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = it.isBlank()
                    },
                    placeholder = { Text("Ej. Bot Arbitraje USDT, Staking...", fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_project_name"),
                    isError = nameError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianSurface,
                        unfocusedContainerColor = ObsidianSurface,
                        focusedBorderColor = EmeraldNeon,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Capital Invertido
                Text(text = "Capital Invertido ($) *", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = capitalText,
                    onValueChange = {
                        capitalText = it
                        val parsed = it.toDoubleOrNull()
                        capitalError = parsed == null || parsed <= 0.0
                    },
                    placeholder = { Text("0.00", fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_project_capital"),
                    isError = capitalError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianSurface,
                        unfocusedContainerColor = ObsidianSurface,
                        focusedBorderColor = EmeraldNeon,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Chips
                Text(text = "Categoría", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        categories.chunked(3).forEach { rowList ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                rowList.forEach { cat ->
                                    val isSelected = category == cat
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) EmeraldContainer else ObsidianSurface)
                                            .border(
                                                1.dp,
                                                if (isSelected) EmeraldNeon.copy(alpha = 0.6f) else ObsidianBorder,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { category = cat }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = cat,
                                            color = if (isSelected) EmeraldNeon else TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Risk Level
                Text(text = "Nivel de Riesgo", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RiskLevel.entries.forEach { r ->
                        val isSelected = riskLevel == r
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EmeraldContainer else ObsidianSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) EmeraldNeon.copy(alpha = 0.6f) else ObsidianBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { riskLevel = r }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = r.displayName,
                                color = if (isSelected) EmeraldNeon else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notes
                Text(text = "Notas o Detalles (Opcional)", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Estrategia, condiciones de retiro, enlace...", fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .testTag("input_project_notes"),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianSurface,
                        unfocusedContainerColor = ObsidianSurface,
                        focusedBorderColor = EmeraldNeon,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

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
                            val validName = name.isNotBlank()
                            val parsedCapital = capitalText.toDoubleOrNull()
                            val validCapital = parsedCapital != null && parsedCapital > 0.0

                            nameError = !validName
                            capitalError = !validCapital

                            if (validName && validCapital) {
                                onConfirm(
                                    name,
                                    category,
                                    parsedCapital!!,
                                    projectToEdit?.startDate ?: System.currentTimeMillis(),
                                    riskLevel,
                                    notes
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_confirm_project"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldNeon,
                            contentColor = ObsidianBackground
                        )
                    ) {
                        Text(
                            text = if (isEditMode) "Guardar" else "Crear",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
