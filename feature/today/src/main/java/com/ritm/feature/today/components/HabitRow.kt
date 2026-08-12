package com.ritm.feature.today.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ritm.core.designsystem.MonoFontFamily
import com.ritm.core.designsystem.RitmTheme
import com.ritm.core.model.Habit
import com.ritm.core.model.HabitKind

private val DeleteColor = Color(0xFFC1443B)
private val ExceededColor = Color(0xFFC1443B)

/**
 * Аналог .habit-row — строка привычки. Свайп влево открывает подтверждение удаления.
 * [onCheckClick] решает вызывающая сторона: для привычек с целью 1 это мгновенный тоггл,
 * для количественных и для ограничений — открытие степпера.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitRow(
    habit: Habit,
    onCheckClick: () -> Unit,
    onRequestDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onRequestDelete()
                false
            } else {
                true
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.65f },
    )

    // Чек-бокс лежит внутри свайпаемой области строки: обычный тап по нему иногда
    // захватывается жестом свайпа и оставляет строку "приоткрытой". Каждое изменение
    // done-статуса (то есть каждый тап по чек-боксу) принудительно возвращает свайп в покой.
    LaunchedEffect(habit.doneToday) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            dismissState.reset()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = { DeleteSwipeBackground() },
    ) {
        HabitRowContent(habit = habit, onCheckClick = onCheckClick)
    }
}

@Composable
private fun DeleteSwipeBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeleteColor)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Удалить", tint = Color.White)
    }
}

@Composable
private fun HabitRowContent(habit: Habit, onCheckClick: () -> Unit) {
    val colors = RitmTheme.colors
    val exceeded = habit.isExceeded
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .background(
                when {
                    exceeded -> ExceededColor.copy(alpha = 0.10f)
                    habit.doneToday -> colors.foreground.copy(alpha = 0.04f)
                    else -> Color.Transparent
                },
            )
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when {
                        habit.doneToday -> colors.foreground
                        exceeded -> ExceededColor
                        else -> Color.Transparent
                    },
                )
                .border(1.dp, if (exceeded) ExceededColor else colors.border, RoundedCornerShape(12.dp))
                .clickable(onClick = onCheckClick),
            contentAlignment = Alignment.Center,
        ) {
            when {
                habit.doneToday -> Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = "Открыть привычку ${habit.name}",
                    tint = colors.surface,
                    modifier = Modifier.size(18.dp),
                )
                exceeded -> Icon(
                    imageVector = Icons.Outlined.PriorityHigh,
                    contentDescription = "Лимит превышен: ${habit.name}",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = habit.name, color = colors.foreground, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(
                text = habit.subtitle,
                color = colors.muted,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 1.5.dp),
            )
        }
        val zeroTolerance = habit.kind == HabitKind.BOUNDARY && habit.targetAmount == 0
        Column(horizontalAlignment = Alignment.End) {
            Text(
                // "0/0" для лимита "без исключений" нечитаемо — показываем просто счётчик срывов.
                text = if (zeroTolerance) habit.progress.toString() else "${habit.progress}/${habit.targetAmount}",
                color = if (exceeded) ExceededColor else colors.foreground,
                fontFamily = MonoFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = when {
                    exceeded -> "превышено"
                    zeroTolerance -> "без исключений"
                    else -> habit.unit
                },
                color = if (exceeded) ExceededColor else colors.muted,
                fontFamily = MonoFontFamily,
                fontSize = 10.sp,
            )
        }
    }
}
