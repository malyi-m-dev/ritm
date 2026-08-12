package com.ritm.feature.today.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ritm.core.designsystem.MonoFontFamily
import com.ritm.core.designsystem.RitmTheme
import com.ritm.core.model.DayProgress
import java.time.LocalDate

private const val SwipeThresholdPx = 64f

/**
 * Аналог .date-strip/.date-indicator — 7 дней недели (Пн–Вс). Акцентным цветом отмечен выбранный
 * для просмотра день (по умолчанию — сегодня), тонкой обводкой — реальное "сегодня",
 * если сейчас просматривается другой день. Тап по прошедшему/сегодняшнему дню переключает
 * список привычек на этот день; будущие дни не кликабельны. Горизонтальный свайп по полосе
 * листает недели: влево — вперёд (не дальше текущей), вправо — назад.
 */
@Composable
fun WeekStrip(
    days: List<DayProgress>,
    selectedDate: LocalDate,
    onSelect: (LocalDate) -> Unit,
    onSwipePreviousWeek: () -> Unit,
    onSwipeNextWeek: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = RitmTheme.colors
    val today = LocalDate.now()
    var dragAccum by remember { mutableStateOf(0f) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(onSwipePreviousWeek, onSwipeNextWeek) {
                detectHorizontalDragGestures(
                    onDragStart = { dragAccum = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        dragAccum += dragAmount
                        change.consume()
                    },
                    onDragEnd = {
                        when {
                            dragAccum <= -SwipeThresholdPx -> onSwipeNextWeek()
                            dragAccum >= SwipeThresholdPx -> onSwipePreviousWeek()
                        }
                        dragAccum = 0f
                    },
                )
            },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        days.forEach { day ->
            val isSelected = day.date == selectedDate
            val isRealToday = day.date == today
            val isDone = !isSelected && day.percent >= 50
            val isFuture = day.date.isAfter(today)
            Column(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .clip(CircleShape)
                    .then(if (isFuture) Modifier else Modifier.clickable { onSelect(day.date) })
                    .padding(vertical = 1.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = day.shortLabel.uppercase(),
                    fontFamily = MonoFontFamily,
                    fontSize = 9.sp,
                    color = colors.muted,
                )
                Box(
                    modifier = Modifier
                        .size(27.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isSelected -> colors.accent
                                isDone -> colors.foreground
                                else -> Color.Transparent
                            },
                        )
                        .then(
                            when {
                                isSelected -> Modifier.border(1.dp, colors.foreground, CircleShape)
                                isRealToday -> Modifier.border(1.dp, colors.accent, CircleShape)
                                else -> Modifier
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = day.date.dayOfMonth.toString(),
                        fontSize = 12.sp,
                        color = when {
                            isSelected -> colors.foreground
                            isDone -> colors.surface
                            isFuture -> colors.muted
                            else -> colors.foreground
                        },
                    )
                }
            }
        }
    }
}
