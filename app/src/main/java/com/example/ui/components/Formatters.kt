package com.example.ui.components

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object Formatters {
    private val currencyFormat = DecimalFormat("$#,##0.00", DecimalFormatSymbols(Locale.US))
    private val compactCurrencyFormat = DecimalFormat("$#,##0", DecimalFormatSymbols(Locale.US))
    private val percentFormat = DecimalFormat("#,##0.0%", DecimalFormatSymbols(Locale.US))
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))

    fun formatCurrency(amount: Double, showPlusSign: Boolean = false): String {
        val sign = if (amount > 0 && showPlusSign) "+" else if (amount < 0) "-" else ""
        return "$sign${currencyFormat.format(abs(amount))}"
    }

    fun formatCompactCurrency(amount: Double): String {
        return compactCurrencyFormat.format(amount)
    }

    fun formatPercentage(percent: Double, showPlusSign: Boolean = true): String {
        val sign = if (percent > 0 && showPlusSign) "+" else if (percent < 0) "-" else ""
        val formatted = String.format(Locale.US, "%.1f%%", abs(percent))
        return "$sign$formatted"
    }

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatMonthYear(timestamp: Long): String {
        return monthYearFormat.format(Date(timestamp)).replaceFirstChar { it.uppercase() }
    }
}
