package com.ritm.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ritm.core.designsystem.RitmTheme
import com.ritm.core.model.DayProgress

/** Аналог .chart/.bar/.bar-fill — столбчатая диаграмма выполнения по дням недели. */
@Composable
fun BarChart(
    bars: List<DayProgress>,
    modifier: Modifier = Modifier,
) {
    val colors = RitmTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(212.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        bars.forEach { bar ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fraction = (bar.percent / 100f).coerceIn(0.03f, 1f))
                            .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                            .background(if (bar.isToday) colors.accent else colors.foreground)
                            .then(
                                if (bar.isToday) {
                                    Modifier.border(
                                        1.dp,
                                        colors.foreground,
                                        RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 2.dp, bottomEnd = 2.dp),
                                    )
                                } else {
                                    Modifier
                                },
                            ),
                    )
                }
                Text(
                    text = bar.shortLabel,
                    color = colors.muted,
                    fontFamily = com.ritm.core.designsystem.MonoFontFamily,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(top = 9.dp),
                )
            }
        }
    }
}
