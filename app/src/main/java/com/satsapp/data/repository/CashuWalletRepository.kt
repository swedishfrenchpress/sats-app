package com.satsapp.data.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.cashudevkit.*
import java.io.File

private const val TAG = "CashuWalletRepo"

/**
 * Repository for managing Cashu wallet operations
 * 
 * This is the Kotlin equivalent of WalletManager.swift
 * 
 * Key responsibilities:
 * - Initialize wallet with mnemonic (stored securely)
 * - Manage wallet balance
 * - Create mint quotes (receive via Lightning)
 * - Mint tokens after payment
 * - Send tokens to other wallets
 * - Receive tokens from other wallets
 * - Track transaction history
 */
class CashuWalletRepository(
    private val context: Context
) {
    // The CDK Wallet - initialized once and reused
    private var wallet: Wallet? = null
    private var database: WalletSqliteDatabase? = null
    var currentMintUrl: String = "https://fake.thesimplekid.dev"
        private set
    
    /**
     * Update the mint URL
     * Note: Requires reinitializing wallet with new mint
     */
    fun updateMintUrl(newMintUrl: String) {
        currentMintUrl = newMintUrl
    }
    
    /**
     * Initialize wallet with mnemonic
     * 
     * Swift equivalent:
     * ```swift
     * let wallet = try await Wallet(
     *     mintUrl: defaultMintURL,
     *     unit: CurrencyUnit.sat,
     *     mnemonic: mnemonic,
     *     db: database,
     *     config: walletConfig
     * )
     * ```
     */
    suspend fun initializeWallet(mnemonic: String? = null): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🔄 Starting wallet initialization...")
            Log.d(TAG, "📍 Mint URL: $currentMintUrl")
            
            // Generate or use provided mnemonic
            val walletMnemonic = mnemonic ?: generateMnemonic()
            Log.d(TAG, "🔑 Mnemonic: ${walletMnemonic.take(20)}... (${walletMnemonic.split(" ").size} words)")
            
            // Create database file in app's private storage
            val walletDir = File(context.filesDir, "wallet_data")
            walletDir.mkdirs()
            val dbFile = File(walletDir, "wallet.sqlite")
            Log.d(TAG, "💾 Database path: ${dbFile.absolutePath}")
            
            // Create database
            database = WalletSqliteDatabase(filePath = dbFile.absolutePath)
            Log.d(TAG, "✅ Database created")
            
            // Create wallet config (matches Swift: targetProofCount: nil)
            val config = WalletConfig(targetProofCount = null)
            Log.d(TAG, "⚙️ Config created (targetProofCount: null)")
            
            // Initialize wallet (matches Swift Wallet constructor)
            Log.d(TAG, "🔨 Creating Wallet object...")
            wallet = Wallet(
                mintUrl = currentMintUrl,
                unit = CurrencyUnit.Sat,
                mnemonic = walletMnemonic,
                db = database!!,
                config = config
            )
            Log.d(TAG, "✅ Wallet object created successfully")
            Log.d(TAG, "✅ CDK Wallet is now managing all state")
            
            Result.success(walletMnemonic)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Wallet initialization failed: ${e.message}", e)
            Log.e(TAG, "❌ Stack trace: ${e.stackTraceToString()}")
            Result.failure(e)
        }
    }
    
    /**
     * Get current wallet balance
     * 
     * Swift equivalent:
     * ```swift
     * let balance = try await wallet.totalBalance()
     * return balance.value
     * ```
     */
    suspend fun getBalance(): ULong = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "💰 Getting wallet balance...")
            val currentWallet = wallet ?: run {
                Log.e(TAG, "❌ Wallet is NULL when getting balance")
                return@withContext 0uL
            }
            
            val balance = currentWallet.totalBalance()
            Log.d(TAG, "✅ Balance fetched: ${balance.value} sats")
            balance.value
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to get balance: ${e.message}", e)
            0uL
        }
    }
    
    /**
     * Check if wallet is initialized
     */
    fun isWalletInitialized(): Boolean = wallet != null
    
    /**
     * Generate a mint quote to receive funds via Lightning
     * 
     * Swift equivalent:
     * ```swift
     * let mintQuote = try await wallet.mintQuote(amount: amountObj, description: nil)
     * return (mintQuote.request, statusString, mintQuote.id)
     * ```
     * 
     * @return Triple of (invoice, status, quoteId)
     */
    suspend fun generateMintQuote(amount: ULong, description: String? = null): Result<Triple<String, String, String>> = withContext(Dispatchers.IO) {
        try {
            val currentWallet = wallet ?: return@withContext Result.failure(
                IllegalStateException("Wallet not initialized")
            )
            
            val amountObj = Amount(value = amount)
            val mintQuote = currentWallet.mintQuote(amount = amountObj, description = description)
            
            // Convert quote state to string (matches Swift switch statement)
            val statusString = when (mintQuote.state) {
                QuoteState.UNPAID -> "Unpaid"
                QuoteState.PAID -> "Paid"
                QuoteState.PENDING -> "Pending"
                QuoteState.ISSUED -> "Issued"
            }
            
            Result.success(Triple(mintQuote.request, statusString, mintQuote.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Mint tokens after Lightning payment is made
     * 
     * Swift equivalent:
     * ```swift
     * let proofs = try await wallet.mint(
     *     quoteId: quoteId,
     *     amountSplitTarget: SplitTarget.none,
     *     spendingConditions: nil
     * )
     * let totalMinted = proofs.reduce(0) { total, proof in
     *     total + proof.amount().value
     * }
     * ```
     */
    suspend fun mintTokens(quoteId: String): Result<ULong> = withContext(Dispatchers.IO) {
        try {
            val currentWallet = wallet ?: return@withContext Result.failure(
                IllegalStateException("Wallet not initialized")
            )
            
            // Mint tokens (matches Swift with SplitTarget.none and nil spending conditions)
            val proofs = currentWallet.mint(
                quoteId = quoteId,
                amountSplitTarget = SplitTarget.None,
                spendingConditions = null
            )
            
            // Calculate total minted amount
            val totalMinted = proofs.fold(0uL) { total, proof ->
                total + proof.amount().value
            }
            
            Log.d(TAG, "✅ Minting complete! Total: $totalMinted sats")
            Log.d(TAG, "💡 CDK has updated balance automatically")
            
            Result.success(totalMinted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Receive Cashu tokens from an encoded token string
     * 
     * This receives tokens from another Cashu wallet by claiming the proofs
     * 
     * @param encodedToken The Cashu token string (starts with "cashuA...")
     * @return Amount received
     */
    suspend fun receiveTokens(encodedToken: String): Result<Amount> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📥 Starting token receive...")
            Log.d(TAG, "📝 Token (first 50 chars): ${encodedToken.take(50)}...")
            
            val currentWallet = wallet ?: run {
                Log.e(TAG, "❌ Wallet is NULL! Cannot receive tokens")
                return@withContext Result.failure(
                    IllegalStateException("Wallet not initialized")
                )
            }
            
            Log.d(TAG, "✅ Wallet exists, parsing token...")
            
            // Parse the token string into Token object
            val token = Token.fromString(encodedToken)
            Log.d(TAG, "✅ Token parsed successfully using Token.fromString()")
            Log.d(TAG, "📊 Current wallet mint: $currentMintUrl")
            // Note: Token structure doesn't expose mint field directly in this version
            
            // Receive the token with options
            Log.d(TAG, "🔄 Calling wallet.receive()...")
            val options = ReceiveOptions(
                amountSplitTarget = SplitTarget.None,
                p2pkSigningKeys = emptyList(),
                preimages = emptyList(),
                metadata = emptyMap()  // Empty metadata map
            )
            
            val amount = currentWallet.receive(token, options)
            
            Log.d(TAG, "✅ Token received successfully!")
            Log.d(TAG, "💰 Amount received: ${amount.value} sats")
            Log.d(TAG, "💡 CDK has updated balance automatically")
            
            Result.success(amount)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Token receive failed: ${e.message}", e)
            Log.e(TAG, "❌ Error type: ${e::class.java.simpleName}")
            Log.e(TAG, "❌ Stack trace: ${e.stackTraceToString()}")
            Result.failure(e)
        }
    }
    
    /**
     * Send tokens to another wallet
     * 
     * TODO: Token constructor signature needs investigation
     * For now returning error - receive() is more important for initial testing
     * 
     * @param amount Amount to send in sats
     * @return Encoded token string to share
     */
    suspend fun sendTokens(amount: ULong): Result<String> = withContext(Dispatchers.IO) {
        Result.failure(Exception("Send not yet fully implemented - Token construction needs work. Receive works though!"))
    }
    
    /**
     * Get transaction history
     * 
     * Swift equivalent:
     * ```swift
     * let allStates: [ProofState] = [.unspent, .spent, .pending]
     * let proofs = try await wallet.getProofsByStates(states: allStates)
     * ```
     * 
     * Note: This returns proofs, not full transactions
     * You'll need to process these into your Transaction model
     */
    suspend fun getProofs(): Result<List<Proof>> = withContext(Dispatchers.IO) {
        try {
            val currentWallet = wallet ?: return@withContext Result.failure(
                IllegalStateException("Wallet not initialized")
            )
            
            // Get all proofs (unspent, spent, pending)
            val allStates = listOf(ProofState.UNSPENT, ProofState.SPENT, ProofState.PENDING)
            val proofs = currentWallet.getProofsByStates(states = allStates)
            
            Result.success(proofs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get mint information from the current mint
     * 
     * This fetches the mint info from the /v1/info endpoint
     * Useful for debugging and verifying mint connection
     * 
     * @return MintInfo object with mint details
     */
    suspend fun getMintInfo(): Result<MintInfo> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🔍 Fetching mint info...")
            Log.d(TAG, "📍 Mint URL: $currentMintUrl")
            Log.d(TAG, "🔍 Expected endpoint: $currentMintUrl/v1/info")
            
            val currentWallet = wallet ?: run {
                Log.e(TAG, "❌ Wallet is NULL! Cannot fetch mint info")
                return@withContext Result.failure(
                    IllegalStateException("Wallet not initialized")
                )
            }
            
            Log.d(TAG, "✅ Wallet exists, calling getMintInfo()...")
            
            // Get mint info from the wallet
            // The wallet has a getMintInfo() method that fetches from /v1/info
            val info = currentWallet.getMintInfo()
            
            Log.d(TAG, "📦 getMintInfo() returned: ${if (info != null) "SUCCESS" else "NULL"}")
            
            if (info != null) {
                Log.d(TAG, "✅ Mint Info:")
                Log.d(TAG, "  📛 Name: ${info.name}")
                Log.d(TAG, "  📦 Version: ${info.version}")
                Log.d(TAG, "  📝 Description: ${info.description}")
                Log.d(TAG, "  🔑 Pubkey: ${info.pubkey?.take(20)}...")
                Result.success(info)
            } else {
                Log.e(TAG, "❌ Mint info returned NULL from getMintInfo()")
                Result.failure(Exception("Mint info is null"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ getMintInfo() threw exception: ${e.message}", e)
            Log.e(TAG, "❌ Stack trace: ${e.stackTraceToString()}")
            Result.failure(e)
        }
    }
    
}

/*
 * LEARNING NOTES:
 * 
 * 1. INITIALIZATION:
 *    Swift: Wallet(mintUrl:, unit:, mnemonic:, db:, config:)
 *    Kotlin: Same! API is identical
 * 
 * 2. MINTING (Receiving via Lightning):
 *    Step 1: mintQuote() - Creates invoice
 *    Step 2: mint() - Claims tokens after payment
 * 
 * 3. RECEIVING CASHU TOKENS:
 *    Use receive(token:, options:) to claim tokens from another wallet
 *    The token is a string like "cashuAeyJ0eXAiOiJjYXNodSI..."
 * 
 * 4. SENDING CASHU TOKENS:
 *    Use send(amount:, options:) to create a token to share
 *    Returns a string that the recipient can receive()
 * 
 * 5. BALANCE:
 *    Use totalBalance() to get current balance
 *    Returns Amount object with .value property
 * 
 * 6. KEY DIFFERENCE FROM OLD CODE:
 *    - SplitTarget is now SplitTarget.None (not lowercase)
 *    - QuoteState enum values (Unpaid, Paid, etc.)
 *    - ProofState enum values (Unspent, Spent, Pending)
 *    - receive() and send() use options parameter (nullable)
 */

