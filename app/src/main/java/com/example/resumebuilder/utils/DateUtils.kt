package com.example.resumebuilder.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateFormatter {
    private val monthYearFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    private val fullDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val yearOnlyFormat = SimpleDateFormat("yyyy", Locale.getDefault())

    fun formatMonthYear(date: Date?): String = date?.let { monthYearFormat.format(it) } ?: ""
    fun formatFullDate(date: Date?): String = date?.let { fullDateFormat.format(it) } ?: ""
    fun formatYear(date: Date?): String = date?.let { yearOnlyFormat.format(it) } ?: ""

    fun parseDateString(dateString: String): Date? {
        return try {
            when {
                dateString.matches(Regex("\\w{3} \\d{4}")) -> monthYearFormat.parse(dateString)
                dateString.matches(Regex("\\d{4}")) -> yearOnlyFormat.parse(dateString)
                else -> fullDateFormat.parse(dateString)
            }
        } catch (e: Exception) {
            null
        }
    }
}
// Extension Functions for Date Handling
fun Date.toMonthYear(): String = DateFormatter.formatMonthYear(this)
fun Date.toYear(): String = DateFormatter.formatYear(this)
fun Date.isInFuture(): Boolean = this.after(Date())
fun Date.monthsUntilNow(): Int {
    val now = Calendar.getInstance()
    val then = Calendar.getInstance().apply { time = this@monthsUntilNow }
    return (now.get(Calendar.YEAR) - then.get(Calendar.YEAR)) * 12 +
            (now.get(Calendar.MONTH) - then.get(Calendar.MONTH))
}