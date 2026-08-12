package com.ritm.core.model

import java.time.LocalDate

enum class StatsPeriod {
    WEEK,
    MONTH,
}

/** Один столбец на графике "Выполнение по дням". */
data class DayProgress(
    val date: LocalDate,
    val shortLabel: String,
    val percent: Int,
    val isToday: Boolean,
)

enum class InsightIcon {
    STREAK,
    CONSISTENCY,
}

data class Insight(
    val icon: InsightIcon,
    val title: String,
    val copy: String,
    val tag: String,
)

/** Всё, что нужно экрану "Статистика" для одного выбранного периода — посчитано из реальных данных Room. */
data class StatisticsSnapshot(
    val period: StatsPeriod,
    val rangeLabel: String,
    val daysInRhythm: Int,
    val totalDays: Int,
    val rhythmCaption: String,
    val bestHabitName: String,
    val bestHabitCaption: String,
    val chartLabel: String,
    val bars: List<DayProgress>,
    val insights: List<Insight>,
)
