package com.ritm.feature.today.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ritm.core.designsystem.RitmTheme
import com.ritm.feature.today.HabitFilter

private val FILTERS = listOf(
    HabitFilter.ALL to "Все",
    HabitFilter.POSITIVE to "Полезные",
    HabitFilter.BOUNDARY to "Ограничения",
)

/** Аналог .chip-row — фильтр "Все / Полезные / Ограничения". */
@Composable
fun FilterChipsRow(
    selected: HabitFilter,
    onSelect: (HabitFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = RitmTheme.colors
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        FILTERS.forEach { (filter, label) ->
            val isSelected = filter == selected
            Box(
                modifier = Modifier
                    .heightIn(min = 44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, if (isSelected) colors.foreground else colors.border, RoundedCornerShape(10.dp))
                    .background(if (isSelected) colors.foreground else Color.Transparent)
                    .clickable { onSelect(filter) }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (isSelected) colors.surface else colors.foreground,
                    fontSize = 12.sp,
                )
            }
        }
    }
}
