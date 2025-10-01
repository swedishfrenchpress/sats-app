package com.satsapp.domain.usecase

import com.satsapp.data.repository.CashuWalletRepository

/**
 * Use cases for payment operations
 * Encapsulates business logic for Cashu payment workflows
 */
class PaymentUseCases(private val repository: CashuWalletRepository) {

    /**
     * Complete mint flow: create quote and mint tokens
     */
    suspend fun receiveFunds(
        amount: ULong,
        description: String = ""
    ): Result<ReceiveFundsResult> {
        // Create mint quote
        val quoteResult = repository.createMintQuote(amount, description)
        if (quoteResult.isFailure) {
            return Result.failure(quoteResult.exceptionOrNull()!!)
        }

        val quote = quoteResult.getOrNull()!!
        return Result.success(
            ReceiveFundsResult(
                quoteId = quote.id,
                paymentRequest = quote.request,
                amount = amount
            )
        )
    }

    /**
     * Complete the minting after payment is confirmed
     */
    suspend fun completeMint(quoteId: String): Result<ULong> {
        return repository.mintTokens(quoteId).map { it.value }
    }

    /**
     * Complete send flow: create melt quote and execute payment
     */
    suspend fun sendPayment(invoice: String): Result<SendPaymentResult> {
        // Create melt quote
        val quoteResult = repository.createMeltQuote(invoice)
        if (quoteResult.isFailure) {
            return Result.failure(quoteResult.exceptionOrNull()!!)
        }

        val quote = quoteResult.getOrNull()!!

        // Execute melt
        val meltResult = repository.meltTokens(quote.id)
        if (meltResult.isFailure) {
            return Result.failure(meltResult.exceptionOrNull()!!)
        }

        val response = meltResult.getOrNull()!!
        return Result.success(
            SendPaymentResult(
                isPaid = response.isPaid,
                amount = quote.amount.value,
                fee = quote.feeReserve.value
            )
        )
    }

    /**
     * Send tokens directly to another Cashu wallet
     */
    suspend fun sendTokens(amount: ULong): Result<String> {
        return repository.sendTokens(amount)
    }

    /**
     * Receive tokens from another Cashu wallet
     */
    suspend fun receiveTokens(encodedToken: String): Result<ULong> {
        return repository.receiveTokens(encodedToken).map { it.value }
    }

    /**
     * Check wallet balance
     */
    suspend fun getBalance(): Result<ULong> {
        return repository.refreshBalance()
    }
}

/**
 * Result of receive funds operation
 */
data class ReceiveFundsResult(
    val quoteId: String,
    val paymentRequest: String,
    val amount: ULong
)

/**
 * Result of send payment operation
 */
data class SendPaymentResult(
    val isPaid: Boolean,
    val amount: ULong,
    val fee: ULong
)
