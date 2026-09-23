package com.example.data.model

enum class ProjectStatus(val displayName: String, val shortName: String) {
    ACTIVO("Activo", "Activo"),
    CAIDO("Caído / Scam", "Caído"),
    PAUSADO("Pausado", "Pausado"),
    FINALIZADO("Finalizado", "Finalizado");

    companion object {
        fun fromString(value: String?): ProjectStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: ACTIVO
        }
    }
}
