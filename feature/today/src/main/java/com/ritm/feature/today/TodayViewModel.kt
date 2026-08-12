package com.ritm.feature.today

import androidx.lifecycle.viewModelScope
import com.ritm.core.data.HabitRepository
import com.ritm.core.model.HabitKind
import com.ritm.core.model.Schedule
import com.ritm.core.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TodayViewModel @Inject constructor(
    private val repository: HabitRepository,
) : MviViewModel<TodayState, TodayIntent, TodayEffect>(TodayState()) {

    private val selectedDate = MutableStateFlow(LocalDate.now())

    /** Всегда понедельник отображаемой недели. */
    private val weekAnchor = MutableStateFlow(currentWeekMonday())

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
        viewModelScope.launch {
            combine(selectedDate, weekAnchor) { date, anchor -> date to anchor }
                .flatMapLatest { (date, anchor) ->
                    combine(repository.observeHabits(date), repository.observeWeekStrip(anchor)) { habits, week ->
                        Triple(habits, week, date to anchor)
                    }
                }
                .collect { (habits, week, dateAndAnchor) ->
                    val (date, anchor) = dateAndAnchor
                    setState { copy(habits = habits, weekStrip = week, selectedDate = date, weekAnchor = anchor) }
                }
        }
    }

    override fun onIntent(intent: TodayIntent) {
        when (intent) {
            is TodayIntent.ToggleHabit -> viewModelScope.launch {
                repository.toggleHabit(intent.habitId, currentState.selectedDate)
            }

            is TodayIntent.SelectFilter -> setState { copy(filter = intent.filter) }

            is TodayIntent.SelectDate -> {
                if (!intent.date.isAfter(LocalDate.now())) selectedDate.value = intent.date
            }

            TodayIntent.PreviousWeek -> {
                weekAnchor.value = weekAnchor.value.minusWeeks(1)
            }

            TodayIntent.NextWeek -> {
                val next = weekAnchor.value.plusWeeks(1)
                val thisWeek = currentWeekMonday()
                weekAnchor.value = if (next.isAfter(thisWeek)) thisWeek else next
            }

            TodayIntent.OpenSheet -> setState { copy(isSheetOpen = true, form = NewHabitFormState()) }

            TodayIntent.CloseSheet -> setState { copy(isSheetOpen = false) }

            TodayIntent.NavigateToStatistics -> sendEffect { TodayEffect.NavigateToStatistics }

            is TodayIntent.RequestDeleteHabit -> setState { copy(habitPendingDeletion = intent.habit) }

            TodayIntent.CancelDeleteHabit -> setState { copy(habitPendingDeletion = null) }

            TodayIntent.ConfirmDeleteHabit -> {
                val habit = currentState.habitPendingDeletion ?: return
                setState { copy(habitPendingDeletion = null) }
                viewModelScope.launch { repository.deleteHabit(habit.id) }
            }

            is TodayIntent.OpenStepper -> setState { copy(stepperHabitId = intent.habitId) }

            TodayIntent.CloseStepper -> setState { copy(stepperHabitId = null) }

            is TodayIntent.AdjustStepperProgress -> {
                val habitId = currentState.stepperHabitId ?: return
                viewModelScope.launch { repository.adjustProgress(habitId, currentState.selectedDate, intent.delta) }
            }

            is TodayIntent.SetStepperProgress -> {
                val habitId = currentState.stepperHabitId ?: return
                viewModelScope.launch { repository.setProgress(habitId, currentState.selectedDate, intent.value) }
            }

            is TodayIntent.NameChanged -> setState {
                copy(form = form.copy(name = intent.value, nameError = false))
            }

            is TodayIntent.NoteChanged -> setState { copy(form = form.copy(note = intent.value)) }

            is TodayIntent.KindChanged -> setState {
                val isBoundary = intent.kind == HabitKind.BOUNDARY
                copy(
                    form = form.copy(
                        kind = intent.kind,
                        target = if (isBoundary) "0" else "1",
                        boundaryLimitExpanded = false,
                    ),
                )
            }

            TodayIntent.RevealBoundaryLimit -> setState { copy(form = form.copy(boundaryLimitExpanded = true)) }

            is TodayIntent.ScheduleChanged -> setState {
                copy(form = form.copy(schedule = intent.choice, weekdayError = null))
            }

            is TodayIntent.WeekdayToggled -> setState {
                val days = form.selectedWeekdays.toMutableSet()
                if (!days.remove(intent.day)) days.add(intent.day)
                copy(form = form.copy(selectedWeekdays = days, weekdayError = null))
            }

            is TodayIntent.TargetChanged -> setState {
                copy(form = form.copy(target = intent.value.filter(Char::isDigit).take(2)))
            }

            is TodayIntent.UnitChanged -> setState { copy(form = form.copy(unit = intent.value)) }

            is TodayIntent.ReminderToggled -> setState { copy(form = form.copy(reminderEnabled = intent.enabled)) }

            is TodayIntent.ReminderTimeChanged -> setState { copy(form = form.copy(reminderTime = intent.value)) }

            TodayIntent.SubmitNewHabit -> submitNewHabit()
        }
    }

    private fun submitNewHabit() {
        val form = currentState.form
        val trimmedName = form.name.trim()
        val targetValue = form.target.toIntOrNull()

        val nameInvalid = trimmedName.isEmpty()
        val minTarget = if (form.kind == HabitKind.BOUNDARY) 0 else 1
        val targetInvalid = targetValue == null || targetValue < minTarget || targetValue > 99
        val weekdaysInvalid = form.schedule == ScheduleChoice.CUSTOM && form.selectedWeekdays.isEmpty()

        if (nameInvalid || targetInvalid || weekdaysInvalid) {
            setState {
                copy(
                    form = this.form.copy(
                        nameError = nameInvalid,
                        weekdayError = if (weekdaysInvalid) "Выберите хотя бы один день." else null,
                    ),
                )
            }
            return
        }

        val schedule: Schedule = when (form.schedule) {
            ScheduleChoice.DAILY -> Schedule.Daily
            ScheduleChoice.WEEKDAYS -> Schedule.Weekdays
            ScheduleChoice.CUSTOM -> Schedule.Custom(form.selectedWeekdays)
        }

        viewModelScope.launch {
            repository.addHabit(
                name = trimmedName,
                kind = form.kind,
                schedule = schedule,
                targetAmount = requireNotNull(targetValue),
                unit = form.unit,
                note = form.note,
            )
            setState { copy(isSheetOpen = false, form = NewHabitFormState()) }
        }
    }

    private fun currentWeekMonday(): LocalDate =
        LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
}
