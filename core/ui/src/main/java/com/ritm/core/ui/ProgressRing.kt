package com.ritm.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ritm.core.designsystem.RitmTheme

/** Аналог .meter — кольцо дневного прогресса с числом в центре (SVG circle + stroke-dashoffset в вёрстке). */
@Composable
fun ProgressRing(
    progress: Float,
    valueText: String,
    caption: String,
    modifier: Modifier = Modifier,
    diameter: Dp = 98.dp,
) {
    val colors = RitmTheme.colors
    Box(modifier = modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(diameter)) {
            val strokeWidthPx = 7.dp.toPx()
            val arcDiameter = size.minDimension - strokeWidthPx
            val topLeft = Offset(
                (size.width - arcDiameter) / 2f,
                (size.height - arcDiameter) / 2f,
            )
            val arcSize = Size(arcDiameter, arcDiameter)
            drawArc(
                color = colors.foregroundSoft,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            )
            drawArc(
                color = colors.foreground,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = valueText, style = RitmTheme.typography.monoValue, color = colors.foreground)
            Text(text = caption, style = RitmTheme.typography.monoValueSmall, color = colors.muted)
        }
    }
}
