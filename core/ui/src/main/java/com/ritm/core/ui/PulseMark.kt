package com.ritm.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.ritm.core.designsystem.RitmTheme

/**
 * Фирменный знак "Пульс" — тот же путь, что в android-splash.html / app-icon-options.html:
 * viewBox 0 0 160 160, path "M28 84h25l16-28 25 54 16-26h22" + окружность (80,80,r=55).
 */
@Composable
fun PulseMark(
    modifier: Modifier = Modifier,
    strokeColor: Color = RitmTheme.colors.surface,
    backgroundColor: Color = RitmTheme.colors.foreground,
    cornerPercent: Int = 28,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = cornerPercent))
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize(0.65f)) {
            val scale = (size.width / 160f + size.height / 160f) / 2f
            val path = Path().apply {
                moveTo(28f * scale, 84f * scale)
                lineTo(53f * scale, 84f * scale)
                lineTo(69f * scale, 56f * scale)
                lineTo(94f * scale, 110f * scale)
                lineTo(110f * scale, 84f * scale)
                lineTo(132f * scale, 84f * scale)
            }
            val strokeWidth = 10f * scale
            val strokeStyle = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            drawPath(path = path, color = strokeColor, style = strokeStyle)
            drawCircle(
                color = strokeColor,
                radius = 55f * scale,
                center = Offset(80f * scale, 80f * scale),
                style = strokeStyle,
            )
        }
    }
}
