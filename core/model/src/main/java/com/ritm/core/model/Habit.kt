package com.ritm.core.model

import java.time.LocalTime

/**
 * Доменная модель привычки. [progress]/[doneToday] — производные поля, собранные
 * репозиторием из таблицы отметок за просматриваемую дату, а не хранимые на самой привычке:
 * [progress] — сколько уже накоплено за день, [doneToday] — финальный статус (для полезных
 * привычек считается живо как progress >= targetAmount; для ограничений выставляется только
 * ночным джобом по итогам дня).
 */
data class Habit(
    val id: Long,
    val name: String,
    val kind: HabitKind,
    val schedule: Schedule,
    val targetAmount: Int,
    val unit: String,
    val subtitle: String,
    val reminderEnabled: Boolean,
    val reminderTime: LocalTime?,
    val sortOrder: Int,
    val progress: Int,
    val doneToday: Boolean,
) {
    /** Привычка с целью больше 1 — заполняется степпером, а не одним тапом. */
    val isQuantified: Boolean get() = targetAmount > 1

    /** Только для ограничений: текущий прогресс уже больше допустимого максимума. */
    val isExceeded: Boolean get() = kind == HabitKind.BOUNDARY && progress > targetAmount
}
