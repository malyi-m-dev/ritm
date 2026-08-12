package com.ritm.core.model

import java.time.DayOfWeek

/** Правило повторения привычки — зеркалирует choice-row из формы добавления привычки. */
sealed class Schedule {

    abstract val displayLabel: String

    data object Daily : Schedule() {
        override val displayLabel: String = "Каждый день"
    }

    data object Weekdays : Schedule() {
        override val displayLabel: String = "По будням"
    }

    data class Custom(val days: Set<DayOfWeek>) : Schedule() {
        override val displayLabel: String
            get() = days.sortedBy { it.value }.joinToString(", ") { it.shortRuLabel() }
    }

    companion object {
        fun shortWeekdayLabel(day: DayOfWeek): String = day.shortRuLabel()
    }
}

private fun DayOfWeek.shortRuLabel(): String = when (this) {
    DayOfWeek.MONDAY -> "Пн"
    DayOfWeek.TUESDAY -> "Вт"
    DayOfWeek.WEDNESDAY -> "Ср"
    DayOfWeek.THURSDAY -> "Чт"
    DayOfWeek.FRIDAY -> "Пт"
    DayOfWeek.SATURDAY -> "Сб"
    DayOfWeek.SUNDAY -> "Вс"
}
