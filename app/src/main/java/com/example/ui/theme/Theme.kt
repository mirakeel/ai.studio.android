package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AccentIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF312E81), // Deep Indigo
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = AccentIndigoLight,
    onSecondary = Color.Black,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = TextPrimary,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    error = Color(0xFFEF4444),
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = AccentIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = AccentIndigoLight,
    onSecondary = Color.White,
    background = Color(0xFFF8FAFC), // Slate 50
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9), // Slate 100
    onSurfaceVariant = Color(0xFF475569)
)

@Composable
fun SocialDashTheme(
    darkTheme: Boolean = true, // Force Dark Mode by default for SocialDash's aesthetic as requested
    dynamicColor: Boolean = false, // Disable dynamic colors to enforce the custom #4F46E5 branding
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = SocialDashShapes,
        content = content
    )
}

