package com.ritm.feature.today

import com.ritm.core.model.DayProgress
import com.ritm.core.model.Habit
import com.ritm.core.model.HabitKind
import com.ritm.core.mvi.UiEffect
import com.ritm.core.mvi.UiIntent
import com.ritm.core.mvi.UiState
import java.time.DayOfWeek
import java.time.LocalDate

enum class HabitFilter { ALL, POSITIVE, BOUNDARY }

enum class ScheduleChoice(val label: String) {
    DAILY("Каждый день"),
    WEEKDAYS("Будни"),
    CUSTOM("Свои дни"),
}

val UNIT_OPTIONS = listOf("раз", "минут", "стаканов", "порций")

data class NewHabitFormState(
    val name: String = "",
    val nameError: Boolean = false,
    val note: String = "",
    val kind: HabitKind = HabitKind.POSITIVE,
    val schedule: ScheduleChoice = ScheduleChoice.DAILY,
    val selectedWeekdays: Set<DayOfWeek> = emptySet(),
    val weekdayError: String? = null,
    val target: String = "1",
    val unit: String = UNIT_OPTIONS[1],
    val boundaryLimitExpanded: Boolean = false,
    val reminderEnabled: Boolean = false,
    val reminderTime: String = "19:00",
) {
    val isBoundary: Boolean get() = kind == HabitKind.BOUNDARY

    /** Для ограничений поле цели по умолчанию свёрнуто в кнопку — лимит "0" уже валиден сам по себе. */
    val showLimitFields: Boolean get() = !isBoundary || boundaryLimitExpanded
    val targetLabel: String get() = if (isBoundary) "Допустимый максимум" else "Цель на день"
    val targetHelp: String
        get() = if (isBoundary) {
            "Лимит: не больше указанного количества за день."
        } else {
            "Цель: выполнить не меньше указанного количества."
        }
    val kindHelp: String
        get() = if (isBoundary) {
            "Задай безопасный максимум, который не хочешь превышать."
        } else {
            "Добавляй действие, которое хочешь повторять."
        }
}

data class TodayState(
    val habits: List<Habit> = emptyList(),
    val weekStrip: List<DayProgress> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val weekAnchor: LocalDate = LocalDate.now(),
    val filter: HabitFilter = HabitFilter.ALL,
    val isSheetOpen: Boolean = false,
    val form: NewHabitFormState = NewHabitFormState(),
    val habitPendingDeletion: Habit? = null,
    val stepperHabitId: Long? = null,
) : UiState {

    val isViewingToday: Boolean get() = selectedDate == LocalDate.now()

    /** Живая привычка, открытая в степпере — берётся из актуального списка, а не снимком на момент открытия. */
    val stepperHabit: Habit? get() = stepperHabitId?.let { id -> habits.find { it.id == id } }

    val positiveHabits: List<Habit> get() = habits.filter { it.kind == HabitKind.POSITIVE }
    val boundaryHabits: List<Habit> get() = habits.filter { it.kind == HabitKind.BOUNDARY }

    val visiblePositive: List<Habit>
        get() = positiveHabits.takeIf { filter == HabitFilter.ALL || filter == HabitFilter.POSITIVE } ?: emptyList()

    val visibleBoundary: List<Habit>
        get() = boundaryHabits.takeIf { filter == HabitFilter.ALL || filter == HabitFilter.BOUNDARY } ?: emptyList()

    val doneCount: Int get() = habits.count { it.doneToday }
    val totalCount: Int get() = habits.size
    val progress: Float get() = if (totalCount == 0) 0f else doneCount / totalCount.toFloat()

    val progressHeading: String
        get() = when {
            totalCount == 0 -> "Начни с одного шага"
            doneCount == totalCount -> "День закрыт"
            doneCount >= (totalCount + 1) / 2 -> "Половина пути"
            else -> "Начни с одного шага"
        }

    val progressCopy: String
        get() {
            val dayWord = if (isViewingToday) "на сегодня" else "за этот день"
            return when {
                totalCount == 0 -> "Добавь первую привычку, чтобы начать."
                doneCount == totalCount -> "Все выбранные действия $dayWord отмечены."
                else -> "$doneCount ${habitWord(doneCount)} $dayWord."
            }
        }

    val positiveCountLabel: String
        get() = "${positiveHabits.count { it.doneToday }} из ${positiveHabits.size}"

    val boundaryCountLabel: String
        get() = "${boundaryHabits.count { it.doneToday }} из ${boundaryHabits.size}"
}

private fun habitWord(count: Int): String =
    if (count == 1) "привычка отмечена" else "привычки отмечены"

sealed interface TodayIntent : UiIntent {
    data class ToggleHabit(val habitId: Long) : TodayIntent
    data class SelectFilter(val filter: HabitFilter) : TodayIntent
    data class SelectDate(val date: LocalDate) : TodayIntent
    data object PreviousWeek : TodayIntent
    data object NextWeek : TodayIntent
    data object OpenSheet : TodayIntent
    data object CloseSheet : TodayIntent
    data object NavigateToStatistics : TodayIntent

    data class RequestDeleteHabit(val habit: Habit) : TodayIntent
    data object CancelDeleteHabit : TodayIntent
    data object ConfirmDeleteHabit : TodayIntent

    data class OpenStepper(val habitId: Long) : TodayIntent
    data object CloseStepper : TodayIntent
    data class AdjustStepperProgress(val delta: Int) : TodayIntent
    data class SetStepperProgress(val value: Int) : TodayIntent

    data class NameChanged(val value: String) : TodayIntent
    data class NoteChanged(val value: String) : TodayIntent
    data class KindChanged(val kind: HabitKind) : TodayIntent
    data object RevealBoundaryLimit : TodayIntent
    data class ScheduleChanged(val choice: ScheduleChoice) : TodayIntent
    data class WeekdayToggled(val day: DayOfWeek) : TodayIntent
    data class TargetChanged(val value: String) : TodayIntent
    data class UnitChanged(val value: String) : TodayIntent
    data class ReminderToggled(val enabled: Boolean) : TodayIntent
    data class ReminderTimeChanged(val value: String) : TodayIntent
    data object SubmitNewHabit : TodayIntent
}

sealed interface TodayEffect : UiEffect {
    data object NavigateToStatistics : TodayEffect
}
