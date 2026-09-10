package com.example.financetracker.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

val SamsungMint = Color(0xFF01D475)
val SamsungBlue = Color(0xFF29B6F6)

private val DarkColorScheme = darkColorScheme(
    primary = SamsungMint, 
    onPrimary = Color.Black,
    secondary = SamsungBlue, 
    onSecondary = Color.Black,
    tertiary = Color(0xFFFF4081),
    background = Color(0xFF000000),
    surface = Color(0xFF1C1C1E),
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = SamsungMint,
    onPrimary = Color.White,
    secondary = SamsungBlue,
    onSecondary = Color.White,
    tertiary = Color(0xFFFF4081),
    background = Color(0xFFF2F4F7),
    surface = Color(0xFFFFFFFF),
    onBackground = Color.Black,
    onSurface = Color.Black
)

val LocalHapticEnabled = androidx.compose.runtime.staticCompositionLocalOf { true }

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(40.dp)
)

@Composable
fun FinanceTrackerTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  primaryColor: Color? = null,
  // Disabled dynamicColor by default to force our Samsung Health palette
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }


  val finalColorScheme = if (primaryColor != null) {
      colorScheme.copy(primary = primaryColor)
  } else {
      colorScheme
  }

  MaterialTheme(
    colorScheme = finalColorScheme, 
    typography = Typography, 
    shapes = AppShapes,
    content = content
  )
}


fun Modifier.bounceClick(onClick: () -> Unit) = composed {
    val hapticEnabled = LocalHapticEnabled.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed && hapticEnabled) 0.92f else 1f, label = "bounce")
    
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.clickable(
        interactionSource = interactionSource,
        indication = androidx.compose.foundation.LocalIndication.current,
        onClick = onClick
    )
}