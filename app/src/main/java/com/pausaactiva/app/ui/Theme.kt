package com.pausaactiva.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.util.Locale

/** Paleta pastel tipo Notion, igual que la versión web. */
@Immutable
class Pal(
    val bg: Color,
    val surface: Color,
    val ink: Color,
    val text2: Color,
    val text3: Color,
    val line: Color,
    val gray: Color,
    val green: Color,
    val blue: Color,
    val yellow: Color,
    val pink: Color,
    val purple: Color,
    val orange: Color,
    val red: Color,
    val sGreen: Color,
    val sBlue: Color,
    val sOrange: Color,
    val accent: Color,
    val onAccent: Color,
) {
    /** Colores de fondo de las figuras, en el mismo orden que la versión web. */
    val tones: List<Color> get() = listOf(green, blue, yellow, pink, purple, orange)
}

val LightPal = Pal(
    bg = Color(0xFFFBFBFA), surface = Color(0xFFFFFFFF), ink = Color(0xFF37352F),
    text2 = Color(0xA837352F), text3 = Color(0x6B37352F), line = Color(0x2137352F),
    gray = Color(0xFFF1F1EF), green = Color(0xFFEDF3EC), blue = Color(0xFFE7F3F8), yellow = Color(0xFFFBF3DB),
    pink = Color(0xFFFAF1F5), purple = Color(0xFFF6F3F9), orange = Color(0xFFFBECDD), red = Color(0xFFFDEBEC),
    sGreen = Color(0xFF2B6E4F), sBlue = Color(0xFF2B6C8F), sOrange = Color(0xFFB85C0B),
    accent = Color(0xFF2B6E4F), onAccent = Color(0xFFFFFFFF),
)

val DarkPal = Pal(
    bg = Color(0xFF191919), surface = Color(0xFF202020), ink = Color(0xFFEDEDEC),
    text2 = Color(0x9EFFFFFF), text3 = Color(0x61FFFFFF), line = Color(0x21FFFFFF),
    gray = Color(0xFF2B2B2B), green = Color(0xFF1F3A2D), blue = Color(0xFF1D3A4A), yellow = Color(0xFF463C22),
    pink = Color(0xFF43293A), purple = Color(0xFF35294A), orange = Color(0xFF4A3626), red = Color(0xFF4A2B2D),
    sGreen = Color(0xFF6FCF9F), sBlue = Color(0xFF7CC2E8), sOrange = Color(0xFFF0A463),
    accent = Color(0xFF6FCF9F), onAccent = Color(0xFF0F2218),
)

val LocalPal = staticCompositionLocalOf { LightPal }

fun Color.hex6(): String = String.format(Locale.US, "%06x", toArgb() and 0xFFFFFF)

@Composable
fun PausaTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val p = if (dark) DarkPal else LightPal
    val scheme = if (dark) {
        darkColorScheme(
            primary = p.accent, onPrimary = p.onAccent, background = p.bg, onBackground = p.ink,
            surface = p.surface, onSurface = p.ink, surfaceVariant = p.gray, onSurfaceVariant = p.text2,
            outline = p.line, secondaryContainer = p.blue, onSecondaryContainer = p.ink,
        )
    } else {
        lightColorScheme(
            primary = p.accent, onPrimary = p.onAccent, background = p.bg, onBackground = p.ink,
            surface = p.surface, onSurface = p.ink, surfaceVariant = p.gray, onSurfaceVariant = p.text2,
            outline = p.line, secondaryContainer = p.blue, onSecondaryContainer = p.ink,
        )
    }
    CompositionLocalProvider(LocalPal provides p) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}
