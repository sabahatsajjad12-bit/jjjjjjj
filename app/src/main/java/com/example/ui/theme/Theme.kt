package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ScholarDarkPrimary,
    onPrimary = ScholarDarkOnPrimary,
    primaryContainer = ScholarDarkPrimaryContainer,
    onPrimaryContainer = ScholarDarkOnPrimaryContainer,
    secondary = ScholarDarkSecondary,
    onSecondary = ScholarDarkOnSecondary,
    secondaryContainer = ScholarDarkSecondaryContainer,
    onSecondaryContainer = ScholarDarkOnSecondaryContainer,
    tertiary = ScholarDarkTertiary,
    background = ScholarDarkBackground,
    surface = ScholarDarkSurface,
    surfaceVariant = ScholarDarkSurfaceVariant,
    outline = ScholarDarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = ScholarPrimary,
    onPrimary = ScholarOnPrimary,
    primaryContainer = ScholarPrimaryContainer,
    onPrimaryContainer = ScholarOnPrimaryContainer,
    secondary = ScholarSecondary,
    onSecondary = ScholarOnSecondary,
    secondaryContainer = ScholarSecondaryContainer,
    onSecondaryContainer = ScholarOnSecondaryContainer,
    tertiary = ScholarTertiary,
    background = ScholarBackground,
    surface = ScholarSurface,
    surfaceVariant = ScholarSurfaceVariant,
    outline = ScholarOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
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
        content = content
    )
}
