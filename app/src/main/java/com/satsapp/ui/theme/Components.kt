package com.satsapp.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Custom button styles and components matching iOS Theme.swift
 * 
 * This file contains Jetpack Compose equivalents of:
 * - PrimaryButtonStyle
 * - SecondaryButtonStyle
 * - CompactButtonStyle
 * - CircularButtonStyle
 * - NumberPadButtonStyle
 * - ThemedCheckbox
 * - ThemedMemoField
 * - ThemedIconButton
 */

// MARK: - Primary Button
/**
 * Primary button matching iOS PrimaryButtonStyle
 * 
 * iOS equivalent:
 * - Orange background (theme.primary)
 * - White text (theme.onPrimary)
 * - 50pt height
 * - 12pt corner radius
 * - Scale animation on press (0.98)
 */
@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    // Track press state for scale animation
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animate scale when pressed (0.98 when pressed, 1.0 otherwise)
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        label = "button_scale"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .scale(scale),
        enabled = enabled,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp),
        content = content
    )
}

// MARK: - Secondary Button
/**
 * Secondary button matching iOS SecondaryButtonStyle
 * 
 * iOS equivalent:
 * - Light gray background (theme.surface)
 * - Black text (theme.onSurface)
 * - 50pt height
 * - 12pt corner radius
 * - Scale animation on press (0.98)
 */
@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    // Track press state for scale animation
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animate scale when pressed
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        label = "button_scale"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .scale(scale),
        enabled = enabled,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp),
        content = content
    )
}

// MARK: - Compact Button
/**
 * Compact square button matching iOS CompactButtonStyle
 * 
 * iOS equivalent:
 * - 50x50pt size
 * - Light gray background (theme.surface)
 * - 8pt corner radius
 * - Scale animation on press (0.95)
 */
@Composable
fun CompactButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    // Track press state for scale animation
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animate scale when pressed (0.95 for compact buttons)
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        label = "button_scale"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier
            .size(50.dp)
            .scale(scale),
        enabled = enabled,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        // Center the content
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

// MARK: - Circular Button
/**
 * Circular button matching iOS CircularButtonStyle
 * 
 * iOS equivalent:
 * - 50x50pt circular shape
 * - Orange background when selected, gray when not (theme.primary/surface)
 * - White/black text accordingly
 * - Scale animation on press (0.95)
 */
@Composable
fun CircularButton(
    onClick: () -> Unit,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    // Track press state for scale animation
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animate scale when pressed
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        label = "button_scale"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier
            .size(50.dp)
            .scale(scale),
        enabled = enabled,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) 
                MaterialTheme.colorScheme.onPrimary 
            else 
                MaterialTheme.colorScheme.onSurface
        ),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp)
    ) {
        // Center the content
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

// MARK: - Number Pad Button
/**
 * Number pad button matching iOS NumberPadButtonStyle
 * 
 * iOS equivalent:
 * - Clear/transparent background
 * - System font size 22, medium weight
 * - Min height 60pt
 * - Scale animation on press (0.95)
 */
@Composable
fun NumberPadButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    // Track press state for scale animation
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animate scale when pressed
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        label = "button_scale"
    )
    
    TextButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .scale(scale),
        enabled = enabled,
        interactionSource = interactionSource,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        // Apply text style matching iOS (size 22, medium weight)
        CompositionLocalProvider(
            LocalTextStyle provides TextStyle(
                fontSize = 22.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            )
        ) {
            content()
        }
    }
}

// MARK: - Themed Checkbox
/**
 * Custom checkbox matching iOS ThemedCheckbox
 * 
 * iOS equivalent:
 * - Checkbox icon (filled when checked, outline when not)
 * - Orange when checked, gray when not
 * - Label text beside checkbox
 */
@Composable
fun ThemedCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox button
        IconButton(onClick = { onCheckedChange(!isChecked) }) {
            Icon(
                imageVector = if (isChecked) 
                    Icons.Filled.CheckBox 
                else 
                    Icons.Outlined.CheckBoxOutlineBlank,
                contentDescription = if (isChecked) "Checked" else "Unchecked",
                tint = if (isChecked) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.secondary
            )
        }
        
        // Label text (body style)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

// MARK: - Themed Memo Field
/**
 * Custom memo/notes text field matching iOS ThemedMemoField
 * 
 * iOS equivalent:
 * - "Memo" section header above
 * - Multi-line text field
 * - Light border (gray with opacity)
 * - 80pt height
 * - 8pt corner radius
 * - Placeholder text when empty
 */
@Composable
fun ThemedMemoField(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section header
        Text(
            text = "Memo",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        // Text field with border
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            ),
            shape = RoundedCornerShape(8.dp),
            maxLines = 3
        )
    }
}

// MARK: - Themed Icon Button
/**
 * Icon button with custom tint matching iOS ThemedIconButton
 * 
 * iOS equivalent:
 * - System icon (SF Symbols in iOS)
 * - Custom tint color (defaults to onBackground)
 */
@Composable
fun ThemedIconButton(
    iconVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tintColor: Color = MaterialTheme.colorScheme.onBackground,
    contentDescription: String? = null
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = iconVector,
            contentDescription = contentDescription,
            tint = tintColor
        )
    }
}

// MARK: - Text Style Helper Comments
/*
 * Example usage of text styles in your composables:
 * 
 * Text("1000", style = MaterialTheme.typography.displayLarge) // Amount style
 * Text("Balance", style = MaterialTheme.typography.headlineLarge) // Title style
 * Text("Transactions", style = MaterialTheme.typography.titleMedium) // Section header
 * Text("Details here", style = MaterialTheme.typography.bodyMedium) // Body style
 * Text("Hint text", style = MaterialTheme.typography.labelSmall) // Caption style
 */


