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
 * ConfirmEmailScreen - Email confirmation with 6-digit code
 * 
 * This is the Kotlin equivalent of ConfirmEmailView.swift
 * 
 * Shows after sign up to confirm the user's email address
 * 
 * iOS Code (Swift):
 * ```swift
 * VStack {
 *     Text("Check Your Email")
 *     Text("We've sent a 6-digit confirmation code to")
 *     Text(authManager.userEmail!)
 *     TextField("Enter 6-digit code", text: $authManager.confirmationCode)
 *     Button("Confirm") { ... }
 * }
 * ```
 */
@Composable
fun ConfirmEmailScreen(
    authViewModel: AuthViewModel
) {
    // Collect auth state
    val authState by authViewModel.authState.collectAsState()
    
    // Coroutine scope for async operations
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
        Spacer(modifier = Modifier.weight(1f))
        
        // HEADER SECTION
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title
            Text(
                text = "Check Your Email",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            // Instructions
            Text(
                text = "We've sent a 6-digit confirmation code to",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
            
            // User's email (highlighted)
            // iOS: if let email = authManager.userEmail { Text(email) }
            // Android: authState.userEmail?.let { email -> Text(email) }
            authState.userEmail?.let { email ->
                Text(
                    text = email,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
            
            // More instructions
            Text(
                text = "Enter the code below to complete your sign up",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }
        
        // CODE INPUT SECTION
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Label
            Text(
                text = "Confirmation Code",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            // 6-digit code input field
            // iOS: TextField with .keyboardType(.numberPad)
            // Android: OutlinedTextField with KeyboardType.Number
            OutlinedTextField(
                value = authState.confirmationCode,
                onValueChange = { code ->
                    // Only allow numbers and max 6 digits
                    if (code.length <= 6 && code.all { it.isDigit() }) {
                        authViewModel.updateConfirmationCode(code)
                    }
                },
                placeholder = {
                    Text("Enter 6-digit code")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                )
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
        
        // CONFIRM BUTTON
        PrimaryButton(
            onClick = {
                scope.launch {
                    authViewModel.confirmSignUp()
                }
            },
            // Disable button if loading or code is not 6 digits
            enabled = !authState.isLoading && authState.confirmationCode.length == 6,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Confirm",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

/*
 * LEARNING NOTES:
 * 
 * 1. INPUT VALIDATION:
 *    In iOS:
 *    ```swift
 *    TextField("...", text: $authManager.confirmationCode)
 *        .keyboardType(.numberPad)
 *    ```
 *    iOS doesn't prevent non-numeric input automatically
 *    
 *    In Android:
 *    ```kotlin
 *    OutlinedTextField(
 *        onValueChange = { code ->
 *            if (code.length <= 6 && code.all { it.isDigit() }) {
 *                authViewModel.updateConfirmationCode(code)
 *            }
 *        }
 *    )
 *    ```
 *    We explicitly validate in the onValueChange callback
 *    
 *    Android gives you more control but requires more code!
 * 
 * 2. BUTTON ENABLE/DISABLE:
 *    iOS: .disabled(authManager.isLoading || authManager.confirmationCode.count != 6)
 *    Android: enabled = !authState.isLoading && authState.confirmationCode.length == 6
 *    
 *    iOS uses .disabled() modifier
 *    Android uses enabled parameter
 *    
 *    Same concept, different approach!
 * 
 * 3. TEXT STYLING:
 *    iOS: .font(.title2).multilineTextAlignment(.center)
 *    Android: textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center)
 *    
 *    Android uses .copy() to modify existing text styles
 * 
 * 4. REACTIVE UI:
 *    Both iOS and Android automatically update the UI when:
 *    - authState.confirmationCode changes
 *    - authState.isLoading changes
 *    - authState.error changes
 *    
 *    This is the power of reactive/declarative UI frameworks!
 */

