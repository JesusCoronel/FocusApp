package com.kenproject.focusapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FocusDarkColorScheme = darkColorScheme(
    primary = Color(0xFF82AAFF),
    onPrimary = Color(0xFF003066),
    primaryContainer = Color(0xFF1A4A8A),
    onPrimaryContainer = Color(0xFFD6E3FF),
    secondary = Color(0xFF9ECAFF),
    onSecondary = Color(0xFF003353),
    background = Color(0xFF0F1520),
    onBackground = Color(0xFFE2E8F5),
    surface = Color(0xFF161D2E),
    onSurface = Color(0xFFE2E8F5),
    surfaceVariant = Color(0xFF1E2940),
    error = Color(0xFFFF8A80),
    onError = Color(0xFF690005),
)

private val FocusLightColorScheme = lightColorScheme(
    primary = Color(0xFF1A4A8A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD6E3FF),
    onPrimaryContainer = Color(0xFF001946),
    secondary = Color(0xFF2E5FA3),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF5F8FF),
    onBackground = Color(0xFF0F1520),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F1520),
    surfaceVariant = Color(0xFFE8EEFF),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
)

@Composable
fun FocusAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FocusDarkColorScheme else FocusLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}