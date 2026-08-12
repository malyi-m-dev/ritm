package com.ritm.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * Токены дизайна "Ритм", перенесённые 1:1 из CSS custom properties веб-прототипа
 * (конвертация OKLCH → sRGB, см. mobile-android.html / android-statistics.html).
 */
object RitmColorTokens {
    val Background = Color(0xFFA6AC98)
    val Surface = Color(0xFFBEC4B1)
    val Foreground = Color(0xFF191E12)
    val Muted = Color(0xFF505646)
    val Border = Color(0xFF33382A)
    val Accent = Color(0xFFE1CD51)

    /** Аналог `color-mix(in oklch, var(--fg) 9%, var(--surface))` — фон hover/активных элементов. */
    val ForegroundSoft = lerp(Surface, Foreground, 0.09f)

    /** Аналог `color-mix(in oklch, var(--accent) 20%, var(--surface))`. */
    val AccentSoft = lerp(Surface, Accent, 0.20f)
}
