package com.example.financetracker.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// ── Samsung One UI Inspired 4-Color Palette System ──

data class ColorPalette(
    val name: String,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val accent: Color
)

val PaletteMint = ColorPalette("Mint",       Color(0xFF01D475), Color(0xFF29B6F6), Color(0xFF00BFA5), Color(0xFF69F0AE))
val PaletteOcean = ColorPalette("Ocean",     Color(0xFF2979FF), Color(0xFF00B0FF), Color(0xFF448AFF), Color(0xFF82B1FF))
val PaletteSunset = ColorPalette("Sunset",   Color(0xFFFF6D00), Color(0xFFFF9100), Color(0xFFFFAB40), Color(0xFFFFD740))
val PaletteRose = ColorPalette("Rose",       Color(0xFFE91E63), Color(0xFFFF4081), Color(0xFFF48FB1), Color(0xFFFF80AB))
val PaletteLavender = ColorPalette("Lavender", Color(0xFF7C4DFF), Color(0xFFB388FF), Color(0xFF651FFF), Color(0xFFEA80FC))

val PresetPalettes = listOf(PaletteMint, PaletteOcean, PaletteSunset, PaletteRose, PaletteLavender)

val LocalHapticEnabled = androidx.compose.runtime.staticCompositionLocalOf { true }

private fun buildDarkScheme(p: ColorPalette) = darkColorScheme(
    primary = p.primary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1A2C2A),
    onPrimaryContainer = p.primary,
    secondary = p.secondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1A2630),
    onSecondaryContainer = p.secondary,
    tertiary = p.tertiary,
    onTertiary = Color.Black,
    background = Color(0xFF0D0D0D),
    onBackground = Color(0xFFE8E8E8),
    surface = Color(0xFF161618),
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = Color(0xFF1E1E22),
    onSurfaceVariant = Color(0xFFCACACA),
    outline = Color(0xFF3A3A3E),
    outlineVariant = Color(0xFF2A2A2E)
)

private fun buildLightScheme(p: ColorPalette) = lightColorScheme(
    primary = p.primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F5E9),
    onPrimaryContainer = Color(0xFF002204),
    secondary = p.secondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE1F5FE),
    onSecondaryContainer = Color(0xFF001F2A),
    tertiary = p.tertiary,
    onTertiary = Color.White,
    background = Color(0xFFF7F7FA),
    onBackground = Color(0xFF1A1A1C),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1C),
    surfaceVariant = Color(0xFFF0F0F5),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFFD0D0D8),
    outlineVariant = Color(0xFFE0E0E8)
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun FinanceTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    palette: ColorPalette = PaletteMint,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) buildDarkScheme(palette) else buildLightScheme(palette)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}


@OptIn(ExperimentalFoundationApi::class)
fun Modifier.bounceClick(onLongClick: (() -> Unit)? = null, onClick: () -> Unit) = composed {
    val hapticEnabled = LocalHapticEnabled.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed && hapticEnabled) 0.94f else 1f, label = "bounce")

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.combinedClickable(
        interactionSource = interactionSource,
        indication = androidx.compose.foundation.LocalIndication.current,
        onClick = onClick,
        onLongClick = onLongClick
    )
}