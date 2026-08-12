package com.ritm.feature.today

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ritm.core.designsystem.RitmShapes
import com.ritm.core.designsystem.RitmTheme
import com.ritm.core.model.Habit
import com.ritm.core.model.HabitKind
import com.ritm.core.ui.ProgressRing
import com.ritm.core.ui.RitmBottomNavBar
import com.ritm.core.ui.RitmDividedList
import com.ritm.core.ui.RitmNavDestination
import com.ritm.core.ui.TopBrandBar
import com.ritm.feature.today.components.ExtendedFabButton
import com.ritm.feature.today.components.FilterChipsRow
import com.ritm.feature.today.components.HabitRow
import com.ritm.feature.today.components.NewHabitSheet
import com.ritm.feature.today.components.ProgressStepperSheet
import com.ritm.feature.today.components.WeekStrip
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

@Composable
fun TodayRoute(
    onNavigateToStatistics: () -> Unit,
    viewModel: TodayViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TodayEffect.NavigateToStatistics -> onNavigateToStatistics()
            }
        }
    }

    TodayScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
private fun TodayScreen(state: TodayState, onIntent: (TodayIntent) -> Unit) {
    val colors = RitmTheme.colors

    Scaffold(
        containerColor = colors.surface,
        topBar = { TopBrandBar() },
        bottomBar = {
            RitmBottomNavBar(
                selected = RitmNavDestination.TODAY,
                onSelect = { destination ->
                    if (destination == RitmNavDestination.STATISTICS) onIntent(TodayIntent.NavigateToStatistics)
                },
            )
        },
        floatingActionButton = {
            ExtendedFabButton(text = "Новая привычка", onClick = { onIntent(TodayIntent.OpenSheet) })
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.surface)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding() + 96.dp),
        ) {
            item { Headline(state.selectedDate) }
            item {
                ProgressCard(state)
                Spacer(Modifier.height(8.dp))
            }
            item {
                SectionHeader(title = "Неделя", trailing = monthLabelFor(state.weekAnchor))
                WeekStrip(
                    days = state.weekStrip,
                    selectedDate = state.selectedDate,
                    onSelect = { onIntent(TodayIntent.SelectDate(it)) },
                    onSwipePreviousWeek = { onIntent(TodayIntent.PreviousWeek) },
                    onSwipeNextWeek = { onIntent(TodayIntent.NextWeek) },
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            item {
                FilterChipsRow(
                    selected = state.filter,
                    onSelect = { onIntent(TodayIntent.SelectFilter(it)) },
                    modifier = Modifier.padding(bottom = 7.dp),
                )
            }
            if (state.visiblePositive.isNotEmpty()) {
                item {
                    SectionHeader(title = "Полезные", trailing = state.positiveCountLabel)
                    RitmDividedList(
                        items = state.visiblePositive,
                        modifier = Modifier.padding(bottom = 9.dp),
                        itemKey = { it.id },
                    ) { habit ->
                        HabitRow(
                            habit = habit,
                            onCheckClick = { onIntent(habitCheckIntent(habit)) },
                            onRequestDelete = { onIntent(TodayIntent.RequestDeleteHabit(habit)) },
                        )
                    }
                }
            }
            if (state.visibleBoundary.isNotEmpty()) {
                item {
                    SectionHeader(title = "Ограничения", trailing = state.boundaryCountLabel)
                    RitmDividedList(
                        items = state.visibleBoundary,
                        modifier = Modifier.padding(bottom = 9.dp),
                        itemKey = { it.id },
                    ) { habit ->
                        HabitRow(
                            habit = habit,
                            onCheckClick = { onIntent(habitCheckIntent(habit)) },
                            onRequestDelete = { onIntent(TodayIntent.RequestDeleteHabit(habit)) },
                        )
                    }
                }
            }
        }
    }

    state.habitPendingDeletion?.let { habit ->
        DeleteHabitDialog(
            habitName = habit.name,
            onConfirm = { onIntent(TodayIntent.ConfirmDeleteHabit) },
            onDismiss = { onIntent(TodayIntent.CancelDeleteHabit) },
        )
    }

    state.stepperHabit?.let { habit ->
        ProgressStepperSheet(
            habit = habit,
            onIncrement = { onIntent(TodayIntent.AdjustStepperProgress(1)) },
            onDecrement = { onIntent(TodayIntent.AdjustStepperProgress(-1)) },
            onSetProgress = { onIntent(TodayIntent.SetStepperProgress(it)) },
            onDismiss = { onIntent(TodayIntent.CloseStepper) },
        )
    }

    if (state.isSheetOpen) {
        NewHabitSheet(
            form = state.form,
            onDismiss = { onIntent(TodayIntent.CloseSheet) },
            onNameChanged = { onIntent(TodayIntent.NameChanged(it)) },
            onNoteChanged = { onIntent(TodayIntent.NoteChanged(it)) },
            onKindChanged = { onIntent(TodayIntent.KindChanged(it)) },
            onRevealLimit = { onIntent(TodayIntent.RevealBoundaryLimit) },
            onScheduleChanged = { onIntent(TodayIntent.ScheduleChanged(it)) },
            onWeekdayToggled = { onIntent(TodayIntent.WeekdayToggled(it)) },
            onTargetChanged = { onIntent(TodayIntent.TargetChanged(it)) },
            onUnitChanged = { onIntent(TodayIntent.UnitChanged(it)) },
            onReminderToggled = { onIntent(TodayIntent.ReminderToggled(it)) },
            onReminderTimeChanged = { onIntent(TodayIntent.ReminderTimeChanged(it)) },
            onSubmit = { onIntent(TodayIntent.SubmitNewHabit) },
        )
    }
}

@Composable
private fun DeleteHabitDialog(habitName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val colors = RitmTheme.colors
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        titleContentColor = colors.foreground,
        textContentColor = colors.muted,
        title = { Text("Удалить привычку?") },
        text = { Text("«$habitName» будет удалена без возможности восстановления.") },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onConfirm) {
                Text("Удалить", color = colors.foreground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Отмена", color = colors.muted)
            }
        },
    )
}

@Composable
private fun Headline(selectedDate: java.time.LocalDate) {
    val colors = RitmTheme.colors
    // Крупный заголовок "Твой ритм" убран по просьбе пользователя — занимал много места
    // и не нёс полезной информации; дата и подсказка остаются.
    Column(modifier = Modifier.padding(top = 7.dp, bottom = 8.dp)) {
        Text(
            text = todayOverline(selectedDate),
            color = colors.muted,
            fontFamily = com.ritm.core.designsystem.MonoFontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.09.em,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(text = "Выбери следующий простой шаг.", color = colors.muted, fontSize = 14.sp)
    }
}

@Composable
private fun ProgressCard(state: TodayState) {
    val colors = RitmTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RitmShapes.card)
            .background(colors.surface)
            .border(1.dp, colors.border, RitmShapes.card)
            .padding(horizontal = 17.dp, vertical = 8.5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(state.progressHeading, color = colors.foreground, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Text(
                text = state.progressCopy,
                color = colors.muted,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        ProgressRing(
            progress = state.progress,
            valueText = "${state.doneCount}/${state.totalCount}",
            caption = "сделано",
        )
    }
}

@Composable
private fun SectionHeader(title: String, trailing: String) {
    val colors = RitmTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 11.5.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(title, color = colors.foreground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(trailing, color = colors.muted, fontFamily = com.ritm.core.designsystem.MonoFontFamily, fontSize = 10.sp)
    }
}

/** Ограничения и количественные привычки открывают степпер; простые "цель = 1" — мгновенный тоггл. */
private fun habitCheckIntent(habit: Habit): TodayIntent =
    if (habit.kind == HabitKind.BOUNDARY || habit.isQuantified) {
        TodayIntent.OpenStepper(habit.id)
    } else {
        TodayIntent.ToggleHabit(habit.id)
    }

private fun todayOverline(date: java.time.LocalDate): String {
    val weekday = date.dayOfWeek.getDisplayName(JavaTextStyle.FULL, Locale("ru")).replaceFirstChar { it.uppercase() }
    val month = date.format(DateTimeFormatter.ofPattern("d MMMM", Locale("ru")))
    return "$weekday · $month"
}

private fun monthLabelFor(anchorDate: java.time.LocalDate): String =
    anchorDate.format(DateTimeFormatter.ofPattern("LLLL", Locale("ru")))

@Preview
@Composable
private fun TodayScreenPreview() {
    RitmTheme {
        TodayScreen(
            state = TodayState(habits = listOf<Habit>()),
            onIntent = {},
        )
    }
}
