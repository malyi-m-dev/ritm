package com.ritm.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ritm.core.designsystem.RitmTheme

enum class RitmNavDestination(val label: String) {
    TODAY("Сегодня"),
    STATISTICS("Статистика"),
}

/** Аналог .nav-bar — нижняя навигация с активной "пилюлей" вокруг иконки текущего раздела. */
@Composable
fun RitmBottomNavBar(
    selected: RitmNavDestination,
    onSelect: (RitmNavDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = RitmTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        RitmNavDestination.entries.forEach { destination ->
            val isSelected = destination == selected
            val interactionSource = remember { MutableInteractionSource() }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onSelect(destination) },
                    )
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = if (isSelected) {
                        Modifier
                            .width(44.dp)
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(colors.foregroundSoft)
                    } else {
                        Modifier.size(26.dp)
                    },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = when (destination) {
                            RitmNavDestination.TODAY -> Icons.Outlined.RadioButtonChecked
                            RitmNavDestination.STATISTICS -> Icons.Outlined.BarChart
                        },
                        contentDescription = destination.label,
                        tint = if (isSelected) colors.foreground else colors.muted,
                        modifier = Modifier.size(19.dp),
                    )
                }
                Text(
                    text = destination.label,
                    color = if (isSelected) colors.foreground else colors.muted,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 10.sp,
                )
            }
        }
    }
}
