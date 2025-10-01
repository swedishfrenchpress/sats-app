package com.satsapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// MARK: - App Colors (matching iOS Theme.swift)
// These colors match the iOS app's AppTheme

// Primary color - Orange (like iOS Color.orange)
val AppOrange = Color(0xFFFF9500)

// Secondary color - Gray
val AppGray = Color(0xFF8E8E93)

// Background colors
val AppWhite = Color(0xFFFFFFFF)
val AppBlack = Color(0xFF000000)

// Surface color - Gray with opacity (0.1 in iOS)
val AppSurfaceLight = Color(0xFFF2F2F7) // Light gray surface
val AppSurfaceDark = Color(0xFF1C1C1E) // Dark gray surface

// MARK: - Light Color Scheme
// Matches iOS light mode colors
private val LightColorScheme = lightColorScheme(
    primary = AppOrange,           // primary color (orange)
    onPrimary = AppWhite,          // text on primary (white)
    secondary = AppGray,           // secondary color (gray)
    onSecondary = AppBlack,        // text on secondary (black)
    background = AppWhite,         // background (white)
    onBackground = AppBlack,       // text on background (black)
    surface = AppSurfaceLight,     // surface (light gray)
    onSurface = AppBlack,          // text on surface (black)
    error = Color(0xFFFF3B30),     // iOS red for errors
    onError = AppWhite
)

// MARK: - Dark Color Scheme
// Matches iOS dark mode colors (if needed in the future)
private val DarkColorScheme = darkColorScheme(
    primary = AppOrange,           // primary color (orange)
    onPrimary = AppWhite,          // text on primary (white)
    secondary = AppGray,           // secondary color (gray)
    onSecondary = AppWhite,        // text on secondary (white in dark mode)
    background = AppBlack,         // background (black)
    onBackground = AppWhite,       // text on background (white)
    surface = AppSurfaceDark,      // surface (dark gray)
    onSurface = AppWhite,          // text on surface (white)
    error = Color(0xFFFF3B30),     // iOS red for errors
    onError = AppWhite
)

/**
 * SatsAppTheme - Main theme composable
 * 
 * This theme matches the iOS app's color scheme and design system.
 * It supports both light and dark themes, though the iOS app 
 * currently only uses light theme.
 * 
 * @param darkTheme Whether to use dark theme (defaults to system setting)
 * @param content The composable content to wrap with the theme
 */
@Composable
fun SatsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Select color scheme based on theme
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Apply Material3 theme with our custom colors and typography
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
