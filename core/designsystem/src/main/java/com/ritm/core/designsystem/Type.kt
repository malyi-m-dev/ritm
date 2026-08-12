package com.ritm.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

/** Аналог --font-display: 'Iowan Old Style', 'Charter', Georgia — ближайший serif с кириллицей. */
val DisplayFontFamily = FontFamily(
    Font(GoogleFont("PT Serif"), googleFontProvider, FontWeight.Normal),
    Font(GoogleFont("PT Serif"), googleFontProvider, FontWeight.Bold),
)

/** Аналог --font-body: Roboto — системный шрифт Android, дополнительная загрузка не нужна. */
val BodyFontFamily = FontFamily.Default

/** Аналог --font-mono: 'Roboto Mono' — eyebrow-лейблы, даты, табличные цифры. */
val MonoFontFamily = FontFamily(
    Font(GoogleFont("Roboto Mono"), googleFontProvider, FontWeight.Normal),
    Font(GoogleFont("Roboto Mono"), googleFontProvider, FontWeight.Bold),
)

@Immutable
data class RitmTypography(
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
    val overline: TextStyle,
    val monoValue: TextStyle,
    val monoValueSmall: TextStyle,
    val buttonLabel: TextStyle,
)

fun defaultRitmTypography(): RitmTypography = RitmTypography(
    displayLarge = TextStyle(
        fontFamily = DisplayFontFamily,
        fontSize = 44.sp,
        lineHeight = 42.sp,
        letterSpacing = (-0.05).em,
        fontWeight = FontWeight.Normal,
    ),
    displayMedium = TextStyle(
        fontFamily = DisplayFontFamily,
        fontSize = 29.sp,
        lineHeight = 29.sp,
        letterSpacing = (-0.045).em,
        fontWeight = FontWeight.Normal,
    ),
    titleMedium = TextStyle(
        fontFamily = BodyFontFamily,
        fontSize = 17.sp,
        lineHeight = 21.sp,
        letterSpacing = (-0.02).em,
        fontWeight = FontWeight.Bold,
    ),
    titleSmall = TextStyle(
        fontFamily = BodyFontFamily,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = (-0.015).em,
        fontWeight = FontWeight.Bold,
    ),
    bodyMedium = TextStyle(
        fontFamily = BodyFontFamily,
        fontSize = 14.sp,
        lineHeight = 19.sp,
        fontWeight = FontWeight.Normal,
    ),
    bodySmall = TextStyle(
        fontFamily = BodyFontFamily,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
    ),
    overline = TextStyle(
        fontFamily = MonoFontFamily,
        fontSize = 10.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.09.em,
        fontWeight = FontWeight.Bold,
    ),
    monoValue = TextStyle(
        fontFamily = MonoFontFamily,
        fontSize = 15.sp,
        lineHeight = 17.sp,
        letterSpacing = (-0.03).em,
        fontWeight = FontWeight.Bold,
    ),
    monoValueSmall = TextStyle(
        fontFamily = MonoFontFamily,
        fontSize = 10.sp,
        lineHeight = 13.sp,
        fontWeight = FontWeight.Bold,
    ),
    buttonLabel = TextStyle(
        fontFamily = BodyFontFamily,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Bold,
    ),
)
