package com.ritm.core.data

import com.ritm.core.model.DayFinalizeSummary
import com.ritm.core.model.DayProgress
import com.ritm.core.model.Habit
import com.ritm.core.model.HabitKind
import com.ritm.core.model.Schedule
import com.ritm.core.model.StatisticsSnapshot
import com.ritm.core.model.StatsPeriod
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface HabitRepository {

    /** Привычки на выбранный день, вместе с прогрессом/отметкой выполнения за этот день. */
    fun observeHabits(date: LocalDate): Flow<List<Habit>>

    /** Календарная неделя (Пн–Вс), содержащая [anchorDate], с реальным % выполнения за день. */
    fun observeWeekStrip(anchorDate: LocalDate): Flow<List<DayProgress>>

    /** Быстрый тап по привычке с целью 1 — переключает прогресс между 0 и 1. */
    suspend fun toggleHabit(habitId: Long, date: LocalDate)

    /** Степпер +/-: изменяет накопленный прогресс за день, не давая уйти в минус. */
    suspend fun adjustProgress(habitId: Long, date: LocalDate, delta: Int)

    /** Ручной ввод точного значения прогресса (тап по числу в степпере). */
    suspend fun setProgress(habitId: Long, date: LocalDate, value: Int)

    /** [note] — свободный комментарий вместо авто-текста расписания (как у стартовых привычек, например "Вечерний режим"). */
    suspend fun addHabit(
        name: String,
        kind: HabitKind,
        schedule: Schedule,
        targetAmount: Int,
        unit: String,
        note: String? = null,
    )

    suspend fun deleteHabit(habitId: Long)

    suspend fun statisticsSnapshot(period: StatsPeriod): StatisticsSnapshot

    /**
     * Ночная финализация ограничений за прошедший день: если прогресс не превысил лимит —
     * засчитывает привычку выполненной, иначе оставляет невыполненной. Возвращает сводку
     * для пуш-уведомления. Полезные привычки не трогает — их статус уже живой (progress >= target).
     */
    suspend fun finalizeBoundaryHabits(date: LocalDate): DayFinalizeSummary
}
