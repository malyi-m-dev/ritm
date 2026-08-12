package com.ritm.feature.today.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.ritm.core.designsystem.DisplayFontFamily
import com.ritm.core.designsystem.MonoFontFamily
import com.ritm.core.designsystem.RitmShapes
import com.ritm.core.designsystem.RitmTheme
import com.ritm.core.model.Habit
import com.ritm.core.model.HabitKind

private val ExceededColor = Color(0xFFC1443B)

/**
 * Степпер для количественных привычек: два круга +/- и текущее значение "X из Y единица".
 * Тап по самому числу открывает ручной ввод — удобно для целей вроде "60 минут", где
 * тыкать "+" по одному было бы утомительно. Для ограничений здесь же ведётся счётчик
 * срывов/потраченного — сама привычка "выполняется" не тут, а автоматически в конце дня
 * (см. полночную финализацию), поэтому лишней кнопки "готово" внутри намеренно нет.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressStepperSheet(
    habit: Habit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onSetProgress: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = RitmTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isBoundary = habit.kind == HabitKind.BOUNDARY
    val zeroTolerance = isBoundary && habit.targetAmount == 0
    val exceeded = habit.isExceeded
    val valueColor = if (exceeded) ExceededColor else colors.foreground

    var isEditing by remember(habit.id) { mutableStateOf(false) }
    var editValue by remember(habit.id) { mutableStateOf(habit.progress.toString()) }
    val focusRequester = remember { FocusRequester() }

    fun commitEdit() {
        onSetProgress(editValue.toIntOrNull() ?: habit.progress)
        isEditing = false
    }

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
                .navigationBarsPadding()
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(
                        text = habit.name,
                        fontFamily = DisplayFontFamily,
                        fontSize = 24.sp,
                        letterSpacing = (-0.03).em,
                        color = colors.foreground,
                    )
                    Text(habit.subtitle, color = colors.muted, fontSize = 13.sp, modifier = Modifier.padding(top = 3.dp))
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Закрыть", tint = colors.foreground)
                }
            }

            Spacer(Modifier.height(20.dp))
            if (isBoundary) {
                Text(
                    text = if (exceeded) "Лимит превышен" else "В пределах лимита",
                    color = if (exceeded) ExceededColor else colors.muted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(6.dp))
            }

            if (isEditing) {
                LaunchedEffect(Unit) { focusRequester.requestFocus() }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = editValue,
                        onValueChange = { editValue = it.filter(Char::isDigit).take(4) },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = MonoFontFamily,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.03).em,
                            color = colors.foreground,
                            textAlign = TextAlign.Center,
                        ),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(colors.foreground),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { commitEdit() }),
                        modifier = Modifier
                            .width(120.dp)
                            .focusRequester(focusRequester),
                    )
                    IconButton(onClick = { commitEdit() }) {
                        Icon(Icons.Outlined.Check, contentDescription = "Подтвердить", tint = colors.foreground)
                    }
                }
            } else {
                Text(
                    text = habit.progress.toString(),
                    fontFamily = MonoFontFamily,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.03).em,
                    color = valueColor,
                    modifier = Modifier.clickable {
                        editValue = habit.progress.toString()
                        isEditing = true
                    },
                )
            }
            Text(
                text = if (zeroTolerance) "без исключений" else "из ${habit.targetAmount} ${habit.unit}",
                color = colors.muted,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 2.dp),
            )

            Spacer(Modifier.height(28.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                StepperButton(
                    icon = Icons.Outlined.Remove,
                    contentDescription = "Уменьшить",
                    enabled = habit.progress > 0,
                    onClick = onDecrement,
                )
                StepperButton(
                    icon = Icons.Outlined.Add,
                    contentDescription = "Увеличить",
                    enabled = true,
                    onClick = onIncrement,
                )
            }
            Text(
                text = "Тапни по числу, чтобы ввести значение вручную.",
                color = colors.muted,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 14.dp),
            )

            if (isBoundary) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Эта привычка закрывается сама в конце дня: если не выйдешь за лимит — " +
                        "засчитается выполненной, придёт уведомление с итогом.",
                    color = colors.muted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun StepperButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val colors = RitmTheme.colors
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(if (enabled) colors.foreground else colors.foregroundSoft)
            .border(1.dp, colors.foreground, CircleShape)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) colors.surface else colors.muted,
            modifier = Modifier.size(26.dp),
        )
    }
}
