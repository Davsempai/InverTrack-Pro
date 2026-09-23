package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.ProjectStatus
import com.example.data.model.RiskLevel

class Converters {
    @TypeConverter
    fun fromProjectStatus(status: ProjectStatus): String {
        return status.name
    }

    @TypeConverter
    fun toProjectStatus(value: String): ProjectStatus {
        return try {
            ProjectStatus.valueOf(value)
        } catch (_: Exception) {
            ProjectStatus.ACTIVO
        }
    }

    @TypeConverter
    fun fromRiskLevel(risk: RiskLevel): String {
        return risk.name
    }

    @TypeConverter
    fun toRiskLevel(value: String): RiskLevel {
        return try {
            RiskLevel.valueOf(value)
        } catch (_: Exception) {
            RiskLevel.MEDIO
        }
    }
}
