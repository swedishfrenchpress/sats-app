# SatsApp - Cashu Wallet for Android

A Cashu wallet built with Kotlin for Android using the [CDK-Kotlin](https://github.com/cashubtc/cdk-kotlin) bindings.

## Project Structure

This project follows clean architecture principles with the backend completely set up for Cashu operations:

```
app/src/main/java/com/satsapp/
├── data/
│   └── repository/
│       └── CashuWalletRepository.kt      # Main repository handling CDK operations
├── domain/
│   ├── model/
│   │   └── WalletState.kt                # State models for wallet operations
│   └── usecase/
│       └── PaymentUseCases.kt            # Business logic for payments
├── presentation/
│   └── WalletViewModel.kt                # ViewModel for state management
├── ui/
│   └── theme/                            # Compose theme files
├── MainActivity.kt                        # Main entry point
└── SatsApplication.kt                     # Application class
```

## Backend Features

The backend is fully implemented with:

### CashuWalletRepository
Core functionality for Cashu operations:
- ✅ Wallet initialization with mnemonic support
- ✅ Mint operations (receive funds via Lightning)
- ✅ Melt operations (send funds via Lightning)
- ✅ Direct token sending/receiving between Cashu wallets
- ✅ Balance management with reactive Flow
- ✅ Automatic state updates

### WalletViewModel
State management with Kotlin Flow:
- ✅ Wallet state (balance, initialization, loading, errors)
- ✅ Mint state tracking
- ✅ Melt state tracking
- ✅ Token transfer state
- ✅ Automatic balance updates

### PaymentUseCases
High-level payment workflows:
- ✅ Complete receive funds flow
- ✅ Complete send payment flow
- ✅ Token transfers
- ✅ Balance checking

## Available Operations

### Initialize Wallet
```kotlin
viewModel.initializeWallet() // Generates new mnemonic
// or
viewModel.initializeWallet(mnemonic = "your existing mnemonic") // Restore
```

### Receive Funds (Mint)
```kotlin
// 1. Create mint quote
viewModel.createMintQuote(amount = 1000UL, description = "Test")
// Get payment request from mintState.paymentRequest

// 2. After payment, complete minting
viewModel.mintTokens()
```

### Send Funds (Melt)
```kotlin
// 1. Create melt quote with Lightning invoice
viewModel.createMeltQuote(invoice = "lnbc...")
// Check amount and fee from meltState

// 2. Execute payment
viewModel.meltTokens()
```

### Direct Token Transfer
```kotlin
// Send tokens
viewModel.sendTokens(amount = 500UL)
// Share the token from sendState.token

// Receive tokens
viewModel.receiveTokens(token = "cashuA...")
```

### Balance
```kotlin
viewModel.refreshBalance()
// Access via walletState.balance
```

## Requirements

- Android SDK (API level 24+)
- Android NDK
- Android Studio

## Setup

1. Clone the repository:
```bash
git clone <your-repo-url>
cd sats-app
```

2. Open in Android Studio

3. Ensure Android NDK is installed:
   - Android Studio → Tools → SDK Manager → SDK Tools → NDK

4. Sync Gradle files

5. Run on device or emulator (API 24+)

## Dependencies

- **CDK-Kotlin** (v0.13.1) - Cashu Development Kit bindings
- **Jetpack Compose** - Modern Android UI
- **Kotlin Coroutines** - Async operations
- **AndroidX Lifecycle** - ViewModel and state management

## Architecture

The app uses **Clean Architecture**:

1. **Data Layer** (`data/repository/`)
   - `CashuWalletRepository`: Direct interface to CDK-Kotlin library
   - Handles all Cashu protocol operations
   - Provides reactive state via Kotlin Flow

2. **Domain Layer** (`domain/`)
   - `model/`: Data models and state representations
   - `usecase/`: Business logic and complex workflows

3. **Presentation Layer** (`presentation/`)
   - `WalletViewModel`: State management and UI logic
   - Coordinates between UI and domain layer

4. **UI Layer** (`ui/` and Compose screens)
   - **Ready for your custom design!**
   - Basic example provided in `MainActivity.kt`

## Next Steps - Frontend Design

The backend is complete! You can now focus on building your UI:

1. **Replace the basic UI** in `MainActivity.kt` with your design
2. **Access wallet state** via `viewModel.walletState.collectAsState()`
3. **Call wallet operations** through `viewModel` methods
4. **Build your screens** using Jetpack Compose

### State Observables

```kotlin
val walletState by viewModel.walletState.collectAsState()
val mintState by viewModel.mintState.collectAsState()
val meltState by viewModel.meltState.collectAsState()
val sendState by viewModel.sendState.collectAsState()
val receiveState by viewModel.receiveState.collectAsState()
```

## Configuration

Default mint URL is set to `https://testmint.cashu.space` (testnet).

To change the mint URL, modify the `CashuWalletRepository` initialization in `WalletViewModel.kt`:

```kotlin
private val repository: CashuWalletRepository = CashuWalletRepository(
    mintUrl = "https://your-mint-url.com"
)
```

## Security Notes

⚠️ **Important**: The mnemonic should be stored securely in production!

Current implementation returns the mnemonic during initialization for backup purposes. In production:
- Use Android Keystore for secure storage
- Implement encrypted SharedPreferences
- Add biometric authentication
- Never log or expose mnemonics

## Testing

The project includes basic UI to test all backend functionality:
1. Initialize wallet
2. View balance
3. Test mint/melt operations (implement UI for these)
4. Test token transfers (implement UI for these)

## License

[Your License Here]

## Resources

- [CDK-Kotlin Repository](https://github.com/cashubtc/cdk-kotlin)
- [Cashu Protocol](https://cashu.space)
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
