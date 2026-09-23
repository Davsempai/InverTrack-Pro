package com.example.data.model

enum class RiskLevel(val displayName: String) {
    BAJO("Riesgo Bajo"),
    MEDIO("Riesgo Medio"),
    ALTO("Riesgo Alto"),
    ESPECULATIVO("Muy Alto / Degen");

    val label: String
        get() = displayName

    companion object {
        fun fromString(value: String?): RiskLevel {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MEDIO
        }
    }
}
