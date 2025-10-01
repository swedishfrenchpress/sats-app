package com.satsapp.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.satsapp.data.repository.CashuWalletRepository
import com.satsapp.domain.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for managing wallet state and operations
 * 
 * Now using the REAL CashuWalletRepository with CDK!
 * 
 * This connects your UI to the Cashu wallet backend.
 */
class WalletViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CashuWalletRepository(context = application.applicationContext)

    private val _walletState = MutableStateFlow(WalletState())
    val walletState: StateFlow<WalletState> = _walletState.asStateFlow()

    private val _mintState = MutableStateFlow(MintState())
    val mintState: StateFlow<MintState> = _mintState.asStateFlow()

    private val _meltState = MutableStateFlow(MeltState())
    val meltState: StateFlow<MeltState> = _meltState.asStateFlow()

    private val _sendState = MutableStateFlow(TokenTransferState())
    val sendState: StateFlow<TokenTransferState> = _sendState.asStateFlow()

    private val _receiveState = MutableStateFlow(TokenTransferState())
    val receiveState: StateFlow<TokenTransferState> = _receiveState.asStateFlow()
    
    private val _mintInfo = MutableStateFlow<MintInfoState>(MintInfoState())
    val mintInfo: StateFlow<MintInfoState> = _mintInfo.asStateFlow()
    
    init {
        // Initialize wallet once on startup (CDK handles the rest)
        initializeWallet()
    }

    // ========================================
    // REAL WALLET OPERATIONS (CDK Backend)
    // ========================================
    
    /**
     * Initialize wallet with mnemonic
     * 
     * Called once on startup - CDK handles everything else
     */
    fun initializeWallet(mnemonic: String? = null) {
        viewModelScope.launch {
            _walletState.update { it.copy(isLoading = true, error = null) }
            
            repository.initializeWallet(mnemonic)
                .onSuccess { generatedMnemonic ->
                    // Get initial balance from CDK
                    val balance = repository.getBalance()
                    
                    _walletState.update {
                        it.copy(
                            isLoading = false,
                            isInitialized = true,
                            balance = balance,
                            mnemonic = generatedMnemonic
                        )
                    }
                }
                .onFailure { error ->
                    _walletState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to initialize wallet"
                        )
                    }
                }
        }
    }

    /**
     * Refresh wallet balance from CDK
     */
    fun refreshBalance() {
        viewModelScope.launch {
            val balance = repository.getBalance()
            _walletState.update { it.copy(balance = balance) }
        }
    }

    /**
     * Create mint quote to receive funds via Lightning
     * 
     * Matches Swift WalletManager.generateMintQuote()
     * 
     * @return Triple of (invoice, status, quoteId)
     */
    fun createMintQuote(amount: ULong, description: String = "") {
        viewModelScope.launch {
            _mintState.update { it.copy(isProcessing = true, error = null, amount = amount) }
            
            repository.generateMintQuote(amount, description)
                .onSuccess { (invoice, status, quoteId) ->
                    _mintState.update {
                        it.copy(
                            isProcessing = false,
                            quoteId = quoteId,
                            paymentRequest = invoice
                            // Note: status is available but not stored in state
                        )
                    }
                }
                .onFailure { error ->
                    _mintState.update {
                        it.copy(
                            isProcessing = false,
                            error = error.message ?: "Failed to create mint quote"
                        )
                    }
                }
        }
    }

    /**
     * Mint tokens after Lightning payment is made
     * 
     * Matches Swift WalletManager.mintTokens()
     */
    fun mintTokens() {
        val quoteId = _mintState.value.quoteId ?: return
        
        viewModelScope.launch {
            _mintState.update { it.copy(isProcessing = true, error = null) }
            
            repository.mintTokens(quoteId)
                .onSuccess { amount ->
                    _mintState.update {
                        it.copy(
                            isProcessing = false,
                            isCompleted = true
                        )
                    }
                    refreshBalance()
                }
                .onFailure { error ->
                    _mintState.update {
                        it.copy(
                            isProcessing = false,
                            error = error.message ?: "Failed to mint tokens"
                        )
                    }
                }
        }
    }

    /**
     * Send tokens to another wallet
     * 
     * @param amount Amount in sats to send
     * @return Encoded token string to share
     */
    fun sendTokens(amount: ULong) {
        viewModelScope.launch {
            _sendState.update { it.copy(isProcessing = true, error = null, amount = amount) }
            
            repository.sendTokens(amount)
                .onSuccess { token ->
                    _sendState.update {
                        it.copy(
                            isProcessing = false,
                            isCompleted = true,
                            token = token
                        )
                    }
                    refreshBalance()
                }
                .onFailure { error ->
                    _sendState.update {
                        it.copy(
                            isProcessing = false,
                            error = error.message ?: "Failed to send tokens"
                        )
                    }
                }
        }
    }

    /**
     * Receive Cashu tokens from another wallet
     * 
     * THIS IS THE KEY FUNCTION for scanning tokens!
     * 
     * @param token Encoded Cashu token string (starts with "cashuA...")
     */
    fun receiveTokens(token: String) {
        viewModelScope.launch {
            _receiveState.update { it.copy(isProcessing = true, error = null, token = token) }
            
            repository.receiveTokens(token)
                .onSuccess { amount ->
                    _receiveState.update {
                        it.copy(
                            isProcessing = false,
                            isCompleted = true,
                            amount = amount.value
                        )
                    }
                    refreshBalance()
                }
                .onFailure { error ->
                    _receiveState.update {
                        it.copy(
                            isProcessing = false,
                            error = error.message ?: "Failed to receive tokens"
                        )
                    }
                }
        }
    }

    /**
     * Reset mint state
     */
    fun resetMintState() {
        _mintState.value = MintState()
    }

    /**
     * Reset melt state
     */
    fun resetMeltState() {
        _meltState.value = MeltState()
    }

    /**
     * Reset send state
     */
    fun resetSendState() {
        _sendState.value = TokenTransferState()
    }

    /**
     * Reset receive state
     */
    fun resetReceiveState() {
        _receiveState.value = TokenTransferState()
    }
    
    /**
     * Update mint URL and reinitialize wallet
     * 
     * Note: This creates a new wallet with the new mint
     */
    fun updateMintUrl(newMintUrl: String) {
        viewModelScope.launch {
            // Update mint URL in repository
            repository.updateMintUrl(newMintUrl)
            
            // Reinitialize wallet with new mint
            // CDK will handle the new connection
            initializeWallet()
        }
    }
    
    /**
     * Fetch mint information from the current mint
     * 
     * Calls /v1/info via CDK's getMintInfo()
     */
    fun fetchMintInfo() {
        viewModelScope.launch {
            _mintInfo.update { it.copy(isLoading = true, error = null) }
            
            repository.getMintInfo()
                .onSuccess { info ->
                    _mintInfo.update {
                        it.copy(
                            isLoading = false,
                            info = info,
                            mintUrl = repository.currentMintUrl
                        )
                    }
                }
                .onFailure { error ->
                    _mintInfo.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to fetch mint info"
                        )
                    }
                }
        }
    }
    
    /**
     * Load transaction history from CDK
     * 
     * This converts proofs into transactions for display
     */
    fun loadTransactions(onLoaded: (List<com.satsapp.presentation.screens.Transaction>) -> Unit) {
        viewModelScope.launch {
            repository.getProofs()
                .onSuccess { proofs ->
                    // Convert proofs to transactions
                    // For now, create simple transaction representations
                    val transactions = proofs.mapIndexed { index, proof ->
                        com.satsapp.presentation.screens.Transaction(
                            type = if (index % 2 == 0) 
                                com.satsapp.presentation.screens.TransactionType.RECEIVED 
                            else 
                                com.satsapp.presentation.screens.TransactionType.SENT,
                            amount = proof.amount().value.toInt(),
                            description = "Transaction",
                            memo = null,
                            date = java.util.Date(),
                            status = com.satsapp.presentation.screens.TransactionStatus.COMPLETED
                        )
                    }
                    onLoaded(transactions)
                }
                .onFailure {
                    onLoaded(emptyList())
                }
        }
    }
}

/**
 * State for mint information
 */
data class MintInfoState(
    val isLoading: Boolean = false,
    val info: org.cashudevkit.MintInfo? = null,
    val mintUrl: String? = null,
    val error: String? = null
)
