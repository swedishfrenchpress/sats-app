package com.satsapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.satsapp.ui.theme.PrimaryButton
import kotlinx.coroutines.launch

/**
 * SignUpScreen - User registration form
 * 
 * This is the Kotlin equivalent of SignUpView.swift
 * 
 * iOS Code (Swift):
 * ```swift
 * VStack {
 *     Text("Sign Up for SatsApp")
 *     TextField("Enter your email", text: $email)
 *     TextField("Enter your username", text: $username)
 *     Button("Sign Up with Passkey") { ... }
 * }
 * ```
 * 
 * Key Differences:
 * - iOS @State var email → Android var email by remember { mutableStateOf("") }
 * - iOS TextField → Android OutlinedTextField
 * - iOS Task { await ... } → Android LaunchedEffect or rememberCoroutineScope
 */
@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel
) {
    // Local state for form inputs
    // iOS: @State private var email = ""
    // Android: var email by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    
    // Collect auth state from ViewModel
    val authState by authViewModel.authState.collectAsState()
    
    // Coroutine scope for async operations
    // iOS uses Task { await ... }
    // Android uses rememberCoroutineScope
    val scope = rememberCoroutineScope()
    
    // Main container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f)) // Push content down
        
        // HEADER SECTION
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title
            // iOS: .font(.largeTitle).fontWeight(.bold)
            Text(
                text = "Sign Up for SatsApp",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            // Subtitle
            // iOS: .font(.body).foregroundColor(.secondary)
            Text(
                text = "Create your account to get started",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }
        
        // FORM SECTION
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Email Field
            FormField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                placeholder = "Enter your email",
                keyboardType = KeyboardType.Email
            )
            
            // Username Field
            FormField(
                label = "Username",
                value = username,
                onValueChange = { username = it },
                placeholder = "Enter your username",
                keyboardType = KeyboardType.Text
            )
        }
        
        // ERROR MESSAGE (if any)
        authState.error?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        
        // SIGN UP BUTTON
        // iOS: Button with ProgressView when loading
        // Android: PrimaryButton with CircularProgressIndicator
        PrimaryButton(
            onClick = {
                // Launch coroutine for async operation
                // iOS: Task { await authManager.signUpWithPasskey(...) }
                scope.launch {
                    authViewModel.signUpWithPasskey(email, username)
                }
            },
            enabled = !authState.isLoading && email.isNotEmpty() && username.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            if (authState.isLoading) {
                // Show spinner when loading
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Sign Up with Passkey",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f)) // Push content up
    }
}

/**
 * FormField - Reusable labeled text input
 * 
 * Creates a label + text field combo used in forms
 * 
 * iOS equivalent:
 * ```swift
 * VStack(alignment: .leading, spacing: 8) {
 *     Text("Email").font(.headline)
 *     TextField("Enter your email", text: $email)
 *         .textFieldStyle(RoundedBorderTextFieldStyle())
 * }
 * ```
 */
@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Label
        // iOS: .font(.headline).foregroundColor(.primary)
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        // Text Field
        // iOS: TextField(...).textFieldStyle(RoundedBorderTextFieldStyle())
        // Android: OutlinedTextField
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                autoCorrect = false
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            )
        )
    }
}

/*
 * LEARNING NOTES:
 * 
 * 1. STATE IN COMPOSABLES:
 *    iOS: @State private var email = ""
 *    Android: var email by remember { mutableStateOf("") }
 *    
 *    - "remember" keeps the value across recompositions
 *    - "mutableStateOf" makes it observable (UI updates when it changes)
 *    - "by" is a Kotlin delegate that lets us use email directly (not email.value)
 * 
 * 2. TEXT FIELDS:
 *    iOS: TextField("placeholder", text: $email)
 *    Android: OutlinedTextField(value = email, onValueChange = { email = it })
 *    
 *    iOS uses $ for two-way binding
 *    Android explicitly passes value and onChange callback
 * 
 * 3. KEYBOARD TYPES:
 *    iOS: .keyboardType(.emailAddress)
 *    Android: keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
 *    
 *    Same concept, different API!
 * 
 * 4. ASYNC OPERATIONS:
 *    iOS:
 *    ```swift
 *    Task {
 *        await authManager.signUpWithPasskey(...)
 *    }
 *    ```
 *    
 *    Android:
 *    ```kotlin
 *    scope.launch {
 *        authViewModel.signUpWithPasskey(...)
 *    }
 *    ```
 *    
 *    Both run code asynchronously without blocking the UI!
 * 
 * 5. OPTIONAL/NULL HANDLING:
 *    iOS: if let error = authManager.error { Text(error) }
 *    Android: authState.error?.let { error -> Text(error) }
 *    
 *    iOS "if let" unwraps optionals
 *    Android "?.let" does the same thing!
 */

