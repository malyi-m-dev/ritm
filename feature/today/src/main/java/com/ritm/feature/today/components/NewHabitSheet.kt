package com.ritm.feature.today.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.ritm.core.designsystem.DisplayFontFamily
import com.ritm.core.designsystem.MonoFontFamily
import com.ritm.core.designsystem.RitmShapes
import com.ritm.core.designsystem.RitmTheme
import com.ritm.core.model.HabitKind
import com.ritm.core.model.Schedule
import com.ritm.feature.today.NewHabitFormState
import com.ritm.feature.today.ScheduleChoice
import com.ritm.feature.today.UNIT_OPTIONS
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewHabitSheet(
    form: NewHabitFormState,
    onDismiss: () -> Unit,
    onNameChanged: (String) -> Unit,
    onNoteChanged: (String) -> Unit,
    onKindChanged: (HabitKind) -> Unit,
    onRevealLimit: () -> Unit,
    onScheduleChanged: (ScheduleChoice) -> Unit,
    onWeekdayToggled: (DayOfWeek) -> Unit,
    onTargetChanged: (String) -> Unit,
    onUnitChanged: (String) -> Unit,
    onReminderToggled: (Boolean) -> Unit,
    onReminderTimeChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val colors = RitmTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        contentColor = colors.foreground,
        shape = RitmShapes.sheetTop,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(
                        text = "Новая привычка",
                        fontFamily = DisplayFontFamily,
                        fontSize = 29.sp,
                        letterSpacing = (-0.045).em,
                        color = colors.foreground,
                    )
                    Text(
                        text = "Задай правило, которое легко соблюдать.",
                        color = colors.muted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 3.dp),
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Закрыть", tint = colors.foreground)
                }
            }

            Spacer(Modifier.height(16.dp))
            FieldLabel("Название")
            OutlinedTextField(
                value = form.name,
                onValueChange = onNameChanged,
                placeholder = { Text("Например, растяжка") },
                isError = form.nameError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = ritmFieldColors(),
                shape = RitmShapes.field,
            )
            if (form.nameError) {
                Text("Название обязательно.", color = colors.muted, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(Modifier.height(12.dp))
            FieldLabel("Заметка")
            OutlinedTextField(
                value = form.note,
                onValueChange = onNoteChanged,
                placeholder = { Text("Например, вечерний режим") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = ritmFieldColors(),
                shape = RitmShapes.field,
            )
            Text(
                text = "Необязательно. Если оставить пустым, подпись соберётся из расписания и цели.",
                color = colors.muted,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp),
            )

            Spacer(Modifier.height(12.dp))
            FieldLabel("Тип")
            ChoiceRow(
                options = listOf(HabitKind.POSITIVE to "Полезная привычка", HabitKind.BOUNDARY to "Ограничение"),
                selected = form.kind,
                onSelect = onKindChanged,
            )
            Text(form.kindHelp, color = colors.muted, fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))

            Spacer(Modifier.height(12.dp))
            FieldLabel("Повторение")
            ChoiceRow(
                options = listOf(
                    ScheduleChoice.DAILY to ScheduleChoice.DAILY.label,
                    ScheduleChoice.WEEKDAYS to ScheduleChoice.WEEKDAYS.label,
                    ScheduleChoice.CUSTOM to ScheduleChoice.CUSTOM.label,
                ),
                selected = form.schedule,
                onSelect = onScheduleChanged,
            )
            if (form.schedule == ScheduleChoice.CUSTOM) {
                Spacer(Modifier.height(8.dp))
                WeekdayPicker(selected = form.selectedWeekdays, onToggle = onWeekdayToggled)
            }
            if (form.weekdayError != null) {
                Text(form.weekdayError, color = colors.muted, fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
            }

            Spacer(Modifier.height(12.dp))
            if (form.showLimitFields) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel(form.targetLabel)
                        OutlinedTextField(
                            value = form.target,
                            onValueChange = onTargetChanged,
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ritmFieldColors(),
                            shape = RitmShapes.field,
                        )
                    }
                    Column(modifier = Modifier.weight(1.25f)) {
                        FieldLabel("Единица")
                        UnitDropdown(selected = form.unit, onSelect = onUnitChanged)
                    }
                }
                Text(form.targetHelp, color = colors.muted, fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
            } else {
                FieldLabel(form.targetLabel)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RitmShapes.field)
                        .border(1.dp, colors.border, RitmShapes.field)
                        .clickable(onClick = onRevealLimit),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Задать лимит", color = colors.foreground, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Text(
                    text = "Сейчас: без исключений (0). Нажми, чтобы задать число и единицу.",
                    color = colors.muted,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }

            Spacer(Modifier.height(15.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RitmShapes.field)
                    .border(1.dp, colors.border, RitmShapes.field)
                    .padding(horizontal = 12.dp, vertical = 11.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Напомнить", color = colors.foreground, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Switch(
                    checked = form.reminderEnabled,
                    onCheckedChange = onReminderToggled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.surface,
                        checkedTrackColor = colors.foreground,
                        uncheckedThumbColor = colors.muted,
                        uncheckedTrackColor = colors.surface,
                        uncheckedBorderColor = colors.border,
                    ),
                )
            }

            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp)
                    .clip(RitmShapes.field)
                    .background(colors.foreground)
                    .clickable(onClick = onSubmit),
                contentAlignment = Alignment.Center,
            ) {
                Text("Создать привычку", color = colors.surface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = RitmTheme.colors.muted,
        fontFamily = MonoFontFamily,
        fontSize = 10.sp,
        modifier = Modifier.padding(bottom = 6.dp),
    )
}

@Composable
private fun <T> ChoiceRow(
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit,
) {
    val colors = RitmTheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        options.forEach { (value, label) ->
            val isSelected = value == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp)
                    .clip(RitmShapes.field)
                    .border(1.dp, if (isSelected) colors.foreground else colors.border, RitmShapes.field)
                    .background(if (isSelected) colors.foreground else Color.Transparent)
                    .clickable { onSelect(value) }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (isSelected) colors.surface else colors.muted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun WeekdayPicker(selected: Set<DayOfWeek>, onToggle: (DayOfWeek) -> Unit) {
    val colors = RitmTheme.colors
    val days = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY,
    )
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()) {
        days.forEach { day ->
            val isSelected = day in selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .border(1.dp, if (isSelected) colors.foreground else colors.border, CircleShape)
                    .background(if (isSelected) colors.foreground else Color.Transparent)
                    .clickable { onToggle(day) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = Schedule.shortWeekdayLabel(day),
                    color = if (isSelected) colors.surface else colors.muted,
                    fontFamily = MonoFontFamily,
                    fontSize = 9.sp,
                )
            }
        }
    }
}

@Composable
private fun UnitDropdown(selected: String, onSelect: (String) -> Unit) {
    val colors = RitmTheme.colors
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RitmShapes.field)
                .border(1.dp, colors.border, RitmShapes.field)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(selected, color = colors.foreground, fontSize = 14.sp)
            Icon(Icons.Outlined.ExpandMore, contentDescription = null, tint = colors.foreground)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = colors.surface,
        ) {
            UNIT_OPTIONS.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit, color = colors.foreground) },
                    onClick = { onSelect(unit); expanded = false },
                    colors = MenuDefaults.itemColors(textColor = colors.foreground),
                )
            }
        }
    }
}

@Composable
private fun ritmFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = RitmTheme.colors.foreground,
    unfocusedBorderColor = RitmTheme.colors.border,
    focusedTextColor = RitmTheme.colors.foreground,
    unfocusedTextColor = RitmTheme.colors.foreground,
    cursorColor = RitmTheme.colors.foreground,
    focusedContainerColor = RitmTheme.colors.surface,
    unfocusedContainerColor = RitmTheme.colors.surface,
)
