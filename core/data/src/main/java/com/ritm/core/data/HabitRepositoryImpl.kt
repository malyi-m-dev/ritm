package com.ritm.core.data

import com.ritm.core.database.HabitCompletionDao
import com.ritm.core.database.HabitCompletionEntity
import com.ritm.core.database.HabitDao
import com.ritm.core.database.HabitEntity
import com.ritm.core.model.DayFinalizeSummary
import com.ritm.core.model.DayProgress
import com.ritm.core.model.Habit
import com.ritm.core.model.HabitKind
import com.ritm.core.model.Insight
import com.ritm.core.model.InsightIcon
import com.ritm.core.model.Schedule
import com.ritm.core.model.StatisticsSnapshot
import com.ritm.core.model.StatsPeriod
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao,
) : HabitRepository {

    private val iso = DateTimeFormatter.ISO_LOCAL_DATE

    override fun observeHabits(date: LocalDate): Flow<List<Habit>> {
        val dateString = date.format(iso)
        return combine(habitDao.observeAll(), completionDao.observeForDate(dateString)) { habits, completions ->
            val byHabit = completions.associateBy { it.habitId }
            habits.map { entity ->
                val completion = byHabit[entity.id]
                entity.toDomain(progress = completion?.progress ?: 0, doneToday = completion?.done ?: false)
            }
        }
    }

    override fun observeWeekStrip(anchorDate: LocalDate): Flow<List<DayProgress>> {
        val today = LocalDate.now()
        val start = anchorDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return combine(
            habitDao.observeAll(),
            completionDao.observeRange(start.format(iso), start.plusDays(6).format(iso)),
        ) { habits, completions ->
            val total = habits.size.coerceAtLeast(1)
            val doneByDate = completions.filter { it.done }.groupBy { it.date }
            (0..6L).map { offset ->
                val date = start.plusDays(offset)
                val count = doneByDate[date.format(iso)]?.size ?: 0
                DayProgress(
                    date = date,
                    shortLabel = Schedule.shortWeekdayLabel(date.dayOfWeek),
                    percent = ((count * 100) / total).coerceIn(0, 100),
                    isToday = date == today,
                )
            }
        }
    }

    override suspend fun toggleHabit(habitId: Long, date: LocalDate) {
        val dateString = date.format(iso)
        val current = completionDao.getProgress(habitId, dateString) ?: 0
        val next = if (current > 0) 0 else 1
        completionDao.upsert(
            HabitCompletionEntity(habitId = habitId, date = dateString, progress = next, done = next >= 1),
        )
    }

    override suspend fun adjustProgress(habitId: Long, date: LocalDate, delta: Int) {
        val dateString = date.format(iso)
        val current = completionDao.getProgress(habitId, dateString) ?: 0
        applyProgress(habitId, dateString, current + delta)
    }

    override suspend fun setProgress(habitId: Long, date: LocalDate, value: Int) {
        applyProgress(habitId, date.format(iso), value)
    }

    private suspend fun applyProgress(habitId: Long, dateString: String, rawValue: Int) {
        val habit = habitDao.getAllOnce().find { it.id == habitId } ?: return
        val next = rawValue.coerceAtLeast(0)
        val done = if (habit.kind == HabitKind.BOUNDARY.name) {
            // Ограничения не завершаются вручную — их итог за день выставляет только ночная финализация.
            completionDao.getDone(habitId, dateString) ?: false
        } else {
            next >= habit.targetAmount
        }
        completionDao.upsert(HabitCompletionEntity(habitId = habitId, date = dateString, progress = next, done = done))
    }

    override suspend fun addHabit(
        name: String,
        kind: HabitKind,
        schedule: Schedule,
        targetAmount: Int,
        unit: String,
        note: String?,
    ) {
        val rule = if (kind == HabitKind.BOUNDARY) "лимит $targetAmount $unit" else "цель $targetAmount $unit"
        val subtitle = note?.trim()?.takeIf { it.isNotEmpty() } ?: "${schedule.displayLabel} · $rule"
        val existing = habitDao.getAllOnce()
        val entity = HabitEntity(
            name = name,
            kind = kind.name,
            scheduleType = schedule.toType(),
            scheduleDays = schedule.toDaysString(),
            targetAmount = targetAmount,
            unit = unit,
            subtitle = subtitle,
            reminderEnabled = false,
            reminderTime = null,
            sortOrder = existing.size,
        )
        habitDao.insert(entity)
    }

    override suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteById(habitId)
    }

    override suspend fun finalizeBoundaryHabits(date: LocalDate): DayFinalizeSummary {
        val dateString = date.format(iso)
        val boundaryHabits = habitDao.getAllOnce().filter { it.kind == HabitKind.BOUNDARY.name }
        val kept = mutableListOf<String>()
        val exceeded = mutableListOf<String>()
        boundaryHabits.forEach { habit ->
            val progress = completionDao.getProgress(habit.id, dateString) ?: 0
            val success = progress <= habit.targetAmount
            completionDao.upsert(
                HabitCompletionEntity(habitId = habit.id, date = dateString, progress = progress, done = success),
            )
            if (success) kept.add(habit.name) else exceeded.add(habit.name)
        }
        return DayFinalizeSummary(keptNames = kept, exceededNames = exceeded)
    }

    override suspend fun statisticsSnapshot(period: StatsPeriod): StatisticsSnapshot {
        val today = LocalDate.now()
        val habits = habitDao.getAllOnce()
        val totalHabits = habits.size.coerceAtLeast(1)
        val idToName = habits.associate { it.id to it.name }

        val windowStart = today.minusDays(34)
        val completions = completionDao.getRange(windowStart.format(iso), today.format(iso))
        val doneByDate: Map<String, List<HabitCompletionEntity>> =
            completions.filter { it.done }.groupBy { it.date }

        fun doneCountOn(date: LocalDate): Int = doneByDate[date.format(iso)]?.size ?: 0
        fun percentOn(date: LocalDate): Int = ((doneCountOn(date) * 100) / totalHabits).coerceIn(0, 100)

        val streakDays = run {
            var cursor = if (doneCountOn(today) == 0) today.minusDays(1) else today
            var streak = 0
            while (doneCountOn(cursor) > 0) {
                streak++
                cursor = cursor.minusDays(1)
            }
            streak
        }

        val bestHabit: Pair<String, Int>? = completions
            .filter { it.done }
            .groupBy { it.habitId }
            .mapValues { (_, list) -> list.size }
            .maxByOrNull { it.value }
            ?.let { (habitId, count) -> (idToName[habitId] ?: return@let null) to count }

        val insights = buildInsights(streakDays, bestHabit)

        return when (period) {
            StatsPeriod.WEEK -> {
                val start = today.minusDays(6)
                val bars = (0..6L).map { offset ->
                    val date = start.plusDays(offset)
                    DayProgress(
                        date = date,
                        shortLabel = Schedule.shortWeekdayLabel(date.dayOfWeek),
                        percent = percentOn(date),
                        isToday = date == today,
                    )
                }
                StatisticsSnapshot(
                    period = StatsPeriod.WEEK,
                    rangeLabel = "${start.dayOfMonth}–${today.dayOfMonth} ${ruMonthGenitive(today.monthValue)}",
                    daysInRhythm = bars.count { it.percent >= 50 },
                    totalDays = 7,
                    rhythmCaption = "за 7 дней",
                    bestHabitName = bestHabit?.first ?: "—",
                    bestHabitCaption = if (bestHabit != null) "чаще всего отмечена" else "пока нет данных",
                    chartLabel = "${start.dayOfMonth}–${today.dayOfMonth} ${ruMonthShort(today.monthValue)}",
                    bars = bars,
                    insights = insights,
                )
            }

            StatsPeriod.MONTH -> {
                val monthStart = today.minusDays(29)
                val byWeekday = DayOfWeek.entries.associateWith { weekday ->
                    var sum = 0
                    var count = 0
                    var cursor = monthStart
                    while (!cursor.isAfter(today)) {
                        if (cursor.dayOfWeek == weekday) {
                            sum += percentOn(cursor)
                            count++
                        }
                        cursor = cursor.plusDays(1)
                    }
                    if (count > 0) sum / count else 0
                }
                val bars = DayOfWeek.entries.map { weekday ->
                    DayProgress(
                        date = today,
                        shortLabel = Schedule.shortWeekdayLabel(weekday),
                        percent = byWeekday[weekday] ?: 0,
                        isToday = weekday == today.dayOfWeek,
                    )
                }
                StatisticsSnapshot(
                    period = StatsPeriod.MONTH,
                    rangeLabel = ruMonthNominative(today.monthValue).replaceFirstChar { it.uppercase() },
                    daysInRhythm = (monthStart.datesUntilInclusive(today)).count { percentOn(it) >= 50 },
                    totalDays = monthStart.datesUntilInclusive(today).count(),
                    rhythmCaption = "за 30 дней",
                    bestHabitName = bestHabit?.first ?: "—",
                    bestHabitCaption = if (bestHabit != null) "чаще всего отмечена" else "пока нет данных",
                    chartLabel = ruMonthNominative(today.monthValue),
                    bars = bars,
                    insights = insights,
                )
            }
        }
    }

    private fun buildInsights(streakDays: Int, bestHabit: Pair<String, Int>?): List<Insight> {
        val streakInsight = Insight(
            icon = InsightIcon.STREAK,
            title = if (streakDays > 0) "Твоя серия" else "Серия ещё не началась",
            copy = if (streakDays > 0) {
                "Привычки отмечались $streakDays ${daysWord(streakDays)} подряд."
            } else {
                "Отметь хотя бы одну привычку сегодня, чтобы начать серию."
            },
            tag = if (streakDays > 0) "$streakDays дн." else "0",
        )
        val consistencyInsight = Insight(
            icon = InsightIcon.CONSISTENCY,
            title = bestHabit?.let { "Стабильнее всего: ${it.first}" } ?: "Пока не за что зацепиться",
            copy = if (bestHabit != null) {
                "Эта привычка отмечалась чаще остальных за последний период."
            } else {
                "Отмечай привычки несколько дней, чтобы увидеть лидера."
            },
            tag = bestHabit?.let { "${it.second}×" } ?: "—",
        )
        return listOf(streakInsight, consistencyInsight)
    }
}

private fun HabitEntity.toDomain(progress: Int, doneToday: Boolean): Habit = Habit(
    id = id,
    name = name,
    kind = HabitKind.valueOf(kind),
    schedule = toSchedule(),
    targetAmount = targetAmount,
    unit = unit,
    subtitle = subtitle,
    reminderEnabled = reminderEnabled,
    reminderTime = reminderTime?.let { java.time.LocalTime.parse(it) },
    sortOrder = sortOrder,
    progress = progress,
    doneToday = doneToday,
)

private fun HabitEntity.toSchedule(): Schedule = when (scheduleType) {
    "WEEKDAYS" -> Schedule.Weekdays
    "CUSTOM" -> Schedule.Custom(
        scheduleDays.orEmpty()
            .split(",")
            .filter { it.isNotBlank() }
            .map { DayOfWeek.valueOf(it) }
            .toSet(),
    )
    else -> Schedule.Daily
}

private fun Schedule.toType(): String = when (this) {
    is Schedule.Daily -> "DAILY"
    is Schedule.Weekdays -> "WEEKDAYS"
    is Schedule.Custom -> "CUSTOM"
}

private fun Schedule.toDaysString(): String? =
    (this as? Schedule.Custom)?.days?.joinToString(",") { it.name }

private fun daysWord(n: Int): String {
    val mod100 = n % 100
    val mod10 = n % 10
    return when {
        mod100 in 11..14 -> "дней"
        mod10 == 1 -> "день"
        mod10 in 2..4 -> "дня"
        else -> "дней"
    }
}

private val MONTHS_GENITIVE = arrayOf(
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря",
)
private val MONTHS_NOMINATIVE = arrayOf(
    "январь", "февраль", "март", "апрель", "май", "июнь",
    "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь",
)

private fun ruMonthGenitive(month: Int): String = MONTHS_GENITIVE[month - 1]
private fun ruMonthNominative(month: Int): String = MONTHS_NOMINATIVE[month - 1]
private fun ruMonthShort(month: Int): String = MONTHS_GENITIVE[month - 1].take(3) + "."

private fun LocalDate.datesUntilInclusive(end: LocalDate): List<LocalDate> {
    val result = mutableListOf<LocalDate>()
    var cursor = this
    while (!cursor.isAfter(end)) {
        result.add(cursor)
        cursor = cursor.plusDays(1)
    }
    return result
}
