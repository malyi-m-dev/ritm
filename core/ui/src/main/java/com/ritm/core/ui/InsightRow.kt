package com.ritm.core.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ritm.core.designsystem.MonoFontFamily
import com.ritm.core.designsystem.RitmTheme
import com.ritm.core.model.Insight
import com.ritm.core.model.InsightIcon

/** Аналог .insight — строка "наблюдения" с иконкой, заголовком, описанием и тегом справа. */
@Composable
fun InsightRow(
    insight: Insight,
    modifier: Modifier = Modifier,
) {
    val colors = RitmTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .border(1.dp, colors.border, RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = when (insight.icon) {
                    InsightIcon.STREAK -> Icons.Outlined.Schedule
                    InsightIcon.CONSISTENCY -> Icons.Outlined.TrackChanges
                },
                contentDescription = null,
                tint = colors.foreground,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
        ) {
            Text(text = insight.title, color = colors.foreground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = insight.copy, color = colors.muted, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
        }
        Text(
            text = insight.tag,
            color = colors.muted,
            fontFamily = MonoFontFamily,
            fontSize = 10.sp,
            modifier = Modifier
                .width(56.dp)
                .padding(start = 8.dp),
        )
    }
}
