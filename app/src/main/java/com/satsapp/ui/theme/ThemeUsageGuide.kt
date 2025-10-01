package com.satsapp.ui.theme

/**
 * THEME USAGE GUIDE
 * 
 * This file provides examples of how to use the themed components
 * that match the iOS app's design system.
 * 
 * ==========================================
 * COLORS
 * ==========================================
 * 
 * The app uses the following color scheme:
 * - Primary: Orange (MaterialTheme.colorScheme.primary)
 * - Secondary: Gray (MaterialTheme.colorScheme.secondary)
 * - Background: White (MaterialTheme.colorScheme.background)
 * - Surface: Light Gray (MaterialTheme.colorScheme.surface)
 * 
 * Access colors in your composables:
 * 
 * @Composable
 * fun MyScreen() {
 *     Box(
 *         modifier = Modifier
 *             .fillMaxSize()
 *             .background(MaterialTheme.colorScheme.background)
 *     ) {
 *         Text(
 *             text = "Hello",
 *             color = MaterialTheme.colorScheme.onBackground
 *         )
 *     }
 * }
 * 
 * ==========================================
 * TEXT STYLES
 * ==========================================
 * 
 * Use these typography styles to match iOS:
 * 
 * // Large numbers (balance, amounts)
 * Text("1000", style = MaterialTheme.typography.displayLarge)
 * 
 * // Screen titles
 * Text("Wallet", style = MaterialTheme.typography.headlineLarge)
 * 
 * // Balance displays
 * Text("Balance: 500 sats", style = MaterialTheme.typography.headlineMedium)
 * 
 * // Section headers
 * Text("Recent Transactions", style = MaterialTheme.typography.titleMedium)
 * 
 * // Body text
 * Text("Transaction details", style = MaterialTheme.typography.bodyMedium)
 * 
 * // Captions and hints
 * Text("Updated 2 mins ago", style = MaterialTheme.typography.labelSmall)
 * 
 * ==========================================
 * BUTTONS
 * ==========================================
 * 
 * // Primary button (orange, full-width)
 * PrimaryButton(onClick = { /* action */ }) {
 *     Text("Send Payment")
 * }
 * 
 * // Secondary button (gray, full-width)
 * SecondaryButton(onClick = { /* action */ }) {
 *     Text("Cancel")
 * }
 * 
 * // Compact square button (50x50dp)
 * CompactButton(onClick = { /* action */ }) {
 *     Icon(Icons.Default.Add, contentDescription = "Add")
 * }
 * 
 * // Circular button (selected/unselected states)
 * CircularButton(
 *     onClick = { /* action */ },
 *     isSelected = true
 * ) {
 *     Text("1")
 * }
 * 
 * // Number pad button (for keypad)
 * NumberPadButton(onClick = { /* action */ }) {
 *     Text("5")
 * }
 * 
 * ==========================================
 * CUSTOM COMPONENTS
 * ==========================================
 * 
 * // Themed checkbox with label
 * var checked by remember { mutableStateOf(false) }
 * ThemedCheckbox(
 *     isChecked = checked,
 *     onCheckedChange = { checked = it },
 *     label = "Save for later"
 * )
 * 
 * // Themed memo field (multi-line text input)
 * var memo by remember { mutableStateOf("") }
 * ThemedMemoField(
 *     text = memo,
 *     onTextChange = { memo = it },
 *     placeholder = "Add a note..."
 * )
 * 
 * // Themed icon button
 * ThemedIconButton(
 *     iconVector = Icons.Default.Settings,
 *     onClick = { /* action */ },
 *     tintColor = MaterialTheme.colorScheme.primary,
 *     contentDescription = "Settings"
 * )
 * 
 * ==========================================
 * COMPLETE EXAMPLE SCREEN
 * ==========================================
 * 
 * @Composable
 * fun ExampleScreen() {
 *     Column(
 *         modifier = Modifier
 *             .fillMaxSize()
 *             .background(MaterialTheme.colorScheme.background)
 *             .padding(16.dp),
 *         verticalArrangement = Arrangement.spacedBy(16.dp)
 *     ) {
 *         // Title
 *         Text(
 *             text = "Wallet",
 *             style = MaterialTheme.typography.headlineLarge
 *         )
 *         
 *         // Balance display
 *         Text(
 *             text = "1,000",
 *             style = MaterialTheme.typography.displayLarge
 *         )
 *         
 *         Text(
 *             text = "sats",
 *             style = MaterialTheme.typography.labelSmall
 *         )
 *         
 *         Spacer(modifier = Modifier.height(24.dp))
 *         
 *         // Primary action
 *         PrimaryButton(onClick = { /* send */ }) {
 *             Text("Send Payment")
 *         }
 *         
 *         // Secondary action
 *         SecondaryButton(onClick = { /* receive */ }) {
 *             Text("Receive")
 *         }
 *     }
 * }
 * 
 * ==========================================
 * KEY DIFFERENCES: iOS vs Android
 * ==========================================
 * 
 * iOS SwiftUI                  →  Android Jetpack Compose
 * ─────────────────────────────────────────────────────────
 * Color.orange                 →  MaterialTheme.colorScheme.primary
 * .font(.system(size: 48))     →  MaterialTheme.typography.displayLarge
 * .cornerRadius(12)            →  RoundedCornerShape(12.dp)
 * .frame(height: 50)           →  .height(50.dp)
 * .scaleEffect(0.98)           →  .scale(0.98f)
 * @State var text = ""         →  var text by remember { mutableStateOf("") }
 * Text("Hello").titleStyle()   →  Text("Hello", style = MaterialTheme.typography.headlineLarge)
 * 
 * ==========================================
 */

// This is a documentation file - no actual code to run


