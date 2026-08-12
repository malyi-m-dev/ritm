package com.ritm.core.model

import java.time.LocalDate

/** Отметка выполнения одной привычки за один день. */
data class HabitCompletion(
    val habitId: Long,
    val date: LocalDate,
    val done: Boolean,
)
