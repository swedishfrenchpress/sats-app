package com.satsapp.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * MOCK Repository for UI Testing
 * 
 * This is a fake/stub version of CashuWalletRepository that returns
 * mock data instead of calling the real CashuDevKit library.
 * 
 * Use this temporarily to test the UI screens without backend complexity.
 * 
 * TODO: Replace with real CashuWalletRepository once CDK API is updated
 */
class MockCashuWalletRepository {
    
    private val _balance = MutableStateFlow(1000uL)
    val balance: StateFlow<ULong> = _balance.asStateFlow()
    
    private val _isInitialized = MutableStateFlow(true)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()
    
    /**
     * Mock: Initialize wallet
     */
    suspend fun initializeWallet(mnemonic: String? = null): Result<String> {
        // Simulate initialization
        kotlinx.coroutines.delay(1000)
        _isInitialized.value = true
        _balance.value = 1000u
        return Result.success("word1 word2 word3 word4 word5 word6 word7 word8 word9 word10 word11 word12")
    }
    
    /**
     * Mock: Refresh balance
     */
    suspend fun refreshBalance() {
        // Balance is already set in the flow
    }
    
    /**
     * Mock: Close wallet
     */
    suspend fun closeWallet() {
        // Nothing to close in mock
    }
}

