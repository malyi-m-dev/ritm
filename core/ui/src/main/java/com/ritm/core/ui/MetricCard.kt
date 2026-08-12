package com.ritm.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.ritm.core.designsystem.RitmTheme

/** Аналог .metric — карточка "Дни в ритме" / "Лучше всего" на экране статистики. */
@Composable
fun MetricCard(
    label: String,
    value: String,
    caption: String,
    modifier: Modifier = Modifier,
) {
    val colors = RitmTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(18.dp))
            .padding(16.dp),
    ) {
        Text(
            text = label,
            color = colors.muted,
            style = RitmTheme.typography.overline.copy(fontWeight = FontWeight.Normal),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = value,
            color = colors.foreground,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.045).em,
        )
        Spacer(Modifier.height(4.dp))
        Text(text = caption, color = colors.muted, fontSize = 11.sp)
    }
}
