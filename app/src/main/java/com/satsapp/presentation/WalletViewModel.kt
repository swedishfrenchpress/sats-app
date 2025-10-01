package com.satsapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satsapp.domain.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * ViewModel for managing wallet state and operations
 * 
 * NOTE: This is currently using MOCK/FAKE data for UI testing!
 * CDK backend is temporarily disabled to test the UI screens.
 * 
 * TODO: Re-enable CashuWalletRepository when CDK API is updated
 */
class WalletViewModel : ViewModel() {

    private val _walletState = MutableStateFlow(WalletState(
        isInitialized = true,  // Start initialized for UI testing
        balance = 1000u        // Mock balance of 1000 sats
    ))
    val walletState: StateFlow<WalletState> = _walletState.asStateFlow()

    private val _mintState = MutableStateFlow(MintState())
    val mintState: StateFlow<MintState> = _mintState.asStateFlow()

    private val _meltState = MutableStateFlow(MeltState())
    val meltState: StateFlow<MeltState> = _meltState.asStateFlow()

    private val _sendState = MutableStateFlow(TokenTransferState())
    val sendState: StateFlow<TokenTransferState> = _sendState.asStateFlow()

    private val _receiveState = MutableStateFlow(TokenTransferState())
    val receiveState: StateFlow<TokenTransferState> = _receiveState.asStateFlow()

    // ========================================
    // MOCK FUNCTIONS FOR UI TESTING
    // (Backend temporarily disabled)
    // ========================================
    
    /**
     * Initialize the wallet (MOCK VERSION - just simulates loading)
     */
    fun initializeWallet(mnemonic: String? = null) {
        viewModelScope.launch {
            _walletState.update { it.copy(isLoading = true, error = null) }
            
            // Simulate network delay
            delay(2000)
            
            // Set wallet as initialized with mock mnemonic
            _walletState.update {
                it.copy(
                    isLoading = false,
                    isInitialized = true,
                    balance = 1000u,
                    mnemonic = "word1 word2 word3 word4 word5 word6 word7 word8 word9 word10 word11 word12"
                )
            }
        }
    }

    /**
     * Refresh balance (MOCK VERSION - just updates UI)
     */
    fun refreshBalance() {
        viewModelScope.launch {
            _walletState.update { it.copy(isLoading = true) }
            delay(500)
            _walletState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * Create mint quote (MOCK VERSION)
     */
    fun createMintQuote(amount: ULong, description: String = "") {
        viewModelScope.launch {
            _mintState.update { it.copy(isProcessing = true, error = null, amount = amount) }
            delay(1000)
            _mintState.update {
                it.copy(
                    isProcessing = false,
                    quoteId = "mock-quote-123",
                    paymentRequest = "lnbc1000n1..."
                )
            }
        }
    }

    /**
     * Mint tokens (MOCK VERSION)
     */
    fun mintTokens() {
        viewModelScope.launch {
            _mintState.update { it.copy(isProcessing = true) }
            delay(1000)
            _mintState.update { it.copy(isProcessing = false, isCompleted = true) }
            _walletState.update { it.copy(balance = it.balance + 100u) }
        }
    }

    /**
     * Create melt quote (MOCK VERSION)
     */
    fun createMeltQuote(invoice: String) {
        viewModelScope.launch {
            _meltState.update { it.copy(isProcessing = true, error = null, invoice = invoice) }
            delay(1000)
            _meltState.update {
                it.copy(
                    isProcessing = false,
                    quoteId = "mock-melt-quote-456",
                    amount = 100u,
                    fee = 5u
                )
            }
        }
    }

    /**
     * Melt tokens (MOCK VERSION)
     */
    fun meltTokens() {
        viewModelScope.launch {
            _meltState.update { it.copy(isProcessing = true) }
            delay(1000)
            _meltState.update { it.copy(isProcessing = false, isCompleted = true, isPaid = true) }
            _walletState.update { it.copy(balance = if (it.balance > 100u) it.balance - 100u else 0u) }
        }
    }

    /**
     * Send tokens (MOCK VERSION)
     */
    fun sendTokens(amount: ULong) {
        viewModelScope.launch {
            _sendState.update { it.copy(isProcessing = true, error = null, amount = amount) }
            delay(1000)
            _sendState.update {
                it.copy(
                    isProcessing = false,
                    isCompleted = true,
                    token = "cashuAeyJ0eXAiOiJjYXNodSIsICJ..."
                )
            }
            _walletState.update { it.copy(balance = if (it.balance > amount) it.balance - amount else 0u) }
        }
    }

    /**
     * Receive tokens (MOCK VERSION)
     */
    fun receiveTokens(token: String) {
        viewModelScope.launch {
            _receiveState.update { it.copy(isProcessing = true, error = null, token = token) }
            delay(1000)
            val mockAmount = 50u
            _receiveState.update {
                it.copy(
                    isProcessing = false,
                    isCompleted = true,
                    amount = mockAmount.toLong().toULong()
                )
            }
            _walletState.update { it.copy(balance = it.balance + mockAmount) }
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
}
