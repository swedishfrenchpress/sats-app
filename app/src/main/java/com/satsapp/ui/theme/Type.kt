package com.satsapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography definitions matching iOS Theme.swift text styles
 * 
 * These text styles correspond to the iOS app's text extensions:
 * - displayLarge: Amount style (48sp, light weight) - for large numbers
 * - headlineLarge: Title style (bold, 22sp) - for screen titles
 * - headlineMedium: Balance style (medium weight, 17sp) - for balance displays
 * - titleMedium: Section header style (medium weight, 17sp) - for section headers
 * - bodyMedium: Body style (15sp) - for regular body text
 * - labelSmall: Caption style (12sp) - for captions and hints
 */
val Typography = Typography(
    // Amount style - Large display numbers (e.g., balance amounts)
    // iOS: .font(.system(size: 48, weight: .light))
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = 0.sp
    ),
    
    // Title style - Screen titles and headers
    // iOS: .font(.title2).bold()
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    
    // Balance style - Balance displays
    // iOS: .font(.headline).fontWeight(.medium)
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    
    // Section header style - Section headers
    // iOS: .font(.headline).fontWeight(.medium)
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    
    // Body style - Regular body text
    // iOS: .font(.subheadline)
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    
    // Body large - Also used for body text
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    
    // Caption style - Small hints and captions
    // iOS: .font(.caption)
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),
    
    // Headline (button text) - Used for button labels
    // iOS: .font(.headline)
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    )
)
