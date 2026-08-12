package com.ritm.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Единственная, фиксированная палитра дизайна (не адаптируется под системную тёмную тему —
 * так же, как исходная вёрстка не имела отдельной dark-схемы).
 */
@Immutable
data class RitmColors(
    val background: Color,
    val surface: Color,
    val foreground: Color,
    val muted: Color,
    val border: Color,
    val accent: Color,
    val foregroundSoft: Color,
    val accentSoft: Color,
)

private val ritmColors = RitmColors(
    background = RitmColorTokens.Background,
    surface = RitmColorTokens.Surface,
    foreground = RitmColorTokens.Foreground,
    muted = RitmColorTokens.Muted,
    border = RitmColorTokens.Border,
    accent = RitmColorTokens.Accent,
    foregroundSoft = RitmColorTokens.ForegroundSoft,
    accentSoft = RitmColorTokens.AccentSoft,
)

private val LocalRitmColors = staticCompositionLocalOf { ritmColors }
private val LocalRitmTypography = staticCompositionLocalOf { defaultRitmTypography() }

object RitmTheme {
    val colors: RitmColors
        @Composable get() = LocalRitmColors.current

    val typography: RitmTypography
        @Composable get() = LocalRitmTypography.current

    val shapes: RitmShapes
        get() = RitmShapes
}

@Composable
fun RitmTheme(content: @Composable () -> Unit) {
    val colors = ritmColors
    val typography = defaultRitmTypography()

    // Material3-компоненты (ModalBottomSheet, Switch и т.д.), которые Ritm использует поверх
    // собственных composable, тоже красим в палитру бренда, чтобы не было чужеродного лилового.
    val materialColorScheme = darkColorScheme(
        primary = colors.foreground,
        onPrimary = colors.surface,
        secondary = colors.accent,
        onSecondary = colors.foreground,
        background = colors.background,
        onBackground = colors.foreground,
        surface = colors.surface,
        onSurface = colors.foreground,
        surfaceVariant = colors.foregroundSoft,
        onSurfaceVariant = colors.foreground,
        outline = colors.border,
        scrim = colors.foreground,
    )

    CompositionLocalProvider(
        LocalRitmColors provides colors,
        LocalRitmTypography provides typography,
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = Typography(),
            content = content,
        )
    }
}
