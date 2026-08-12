package com.ritm.feature.today.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ritm.core.designsystem.RitmTheme

/** Аналог .extended-fab — плавающая кнопка "Новая привычка". */
@Composable
fun ExtendedFabButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = RitmTheme.colors
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.accent)
            .border(1.dp, colors.foreground, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 17.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Icon(Icons.Outlined.Add, contentDescription = null, tint = colors.foreground, modifier = Modifier.size(18.dp))
        Text(text = text, color = colors.foreground, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
