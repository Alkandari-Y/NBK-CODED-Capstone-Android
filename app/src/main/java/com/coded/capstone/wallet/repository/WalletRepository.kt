package com.coded.capstone.wallet.repository

import android.content.Context
import com.coded.capstone.wallet.data.WalletAccountDisplayModel
import com.coded.capstone.wallet.data.AccountPerkDisplayModel
import com.coded.capstone.wallet.data.PerkType
import com.coded.capstone.data.enums.AccountType
import com.coded.capstone.data.responses.account.AccountResponse
import com.coded.capstone.data.requests.account.TransferCreateRequest
import com.coded.capstone.data.responses.transaction.TransactionResponse
import com.coded.capstone.providers.RetrofitInstance
import com.coded.capstone.managers.TokenManager
import com.coded.capstone.data.enums.TransactionType
import java.math.BigDecimal

interface WalletRepository {
    suspend fun getWalletAccounts(): Result<List<WalletAccountDisplayModel>>
    suspend fun topUpAccount(fromAccountNumber: String, toAccountNumber: String, amount: BigDecimal): Result<TransactionResponse>
    suspend fun transferFunds(fromAccountNumber: String, toAccountNumber: String, amount: BigDecimal): Result<TransactionResponse>
}

class WalletRepositoryImpl(
    private val context: Context
) : WalletRepository {

    private val bankingService = RetrofitInstance.getBankingServiceProvide(context)

    override suspend fun getWalletAccounts(): Result<List<WalletAccountDisplayModel>> {
        return try {
            // Check token and refresh if needed
            if (TokenManager.isAccessTokenExpired(context)) {
                TokenManager.refreshToken(context) ?: return Result.failure(Exception("Authentication failed"))
            }
            val accounts = bankingService.getAllAccounts()
            val displayModels = accounts.map { account ->
                account.toWalletDisplayModel()
            }
            Result.success(displayModels)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun topUpAccount(
        fromAccountNumber: String,
        toAccountNumber: String,
        amount: BigDecimal
    ): Result<TransactionResponse> {
        return try {
            // Check token and refresh if needed
            if (TokenManager.isAccessTokenExpired(context)) {
                TokenManager.refreshToken(context) ?: return Result.failure(Exception("Authentication failed"))
            }

            val transferRequest = TransferCreateRequest(
                sourceAccountNumber = fromAccountNumber,
                destinationAccountNumber = toAccountNumber,
                amount = amount,
                type = TransactionType.TRANSFER
            )
            val result = bankingService.transfer(transferRequest)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun transferFunds(
        fromAccountNumber: String,
        toAccountNumber: String,
        amount: BigDecimal
    ): Result<TransactionResponse> {
        return try {
            // Check token and refresh if needed
            if (TokenManager.isAccessTokenExpired(context)) {
                TokenManager.refreshToken(context) ?: return Result.failure(Exception("Authentication failed"))
            }

            val transferRequest = TransferCreateRequest(
                sourceAccountNumber = fromAccountNumber,
                destinationAccountNumber = toAccountNumber,
                amount = amount,
                type = TransactionType.TRANSFER
            )
            val result = bankingService.transfer(transferRequest)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Extension function to convert AccountResponse to WalletAccountDisplayModel
private fun AccountResponse.toWalletDisplayModel(): WalletAccountDisplayModel {
    return WalletAccountDisplayModel(
        id = this.id,
        accountNumber = this.accountNumber,
        maskedAccountNumber = "**** ${this.accountNumber.takeLast(4)}",
        balance = this.balance,
        formattedBalance = "${this.balance.setScale(3)} KWD",
        accountType = this.accountType,

        // HARDCODED: Backend doesn't provide these fields yet
        creditLimit = if (this.accountType == AccountType.CREDIT) BigDecimal("10000.000") else null,
        formattedCreditLimit = if (this.accountType == AccountType.CREDIT) "10,000.000 KWD" else null,
        holderName = "ACCOUNT HOLDER", // Get from backend when available

        isActive = this.active,
        canTopUp = this.accountType != AccountType.CASHBACK && this.accountType != AccountType.BUSINESS,
        canTransfer = this.active,
        accountProductName = this.name,

        // Generated locally until real perks API is ready
        perks = generateFakePerks(this.accountType)
    )
}

private fun generateFakePerks(accountType: AccountType): List<AccountPerkDisplayModel> {
    return when (accountType) {
        AccountType.CREDIT -> listOf(
            AccountPerkDisplayModel(
                id = 1,
                type = PerkType.CASHBACK,
                title = "Dining Cashback",
                description = "3% cashback on restaurants & cafes",
                value = "3%",
                minPayment = BigDecimal("100.00"),
                rewardsXp = 200,
                perkAmount = BigDecimal("0.03"),
                isTierBased = false
            ),
            AccountPerkDisplayModel(
                id = 2,
                type = PerkType.INSURANCE,
                title = "Travel Insurance",
                description = "Worldwide travel coverage included",
                value = "Free",
                minPayment = BigDecimal("0.00"),
                rewardsXp = 0,
                perkAmount = BigDecimal("0.00"),
                isTierBased = false
            )
        )

        AccountType.CASHBACK, AccountType.BUSINESS -> listOf(
            AccountPerkDisplayModel(
                id = 3,
                type = PerkType.CASHBACK,
                title = "Category Cashback",
                description = "5% cashback on rotating categories",
                value = "5%",
                minPayment = BigDecimal("75.00"),
                rewardsXp = 300,
                perkAmount = BigDecimal("0.05"),
                isTierBased = true
            ),
            AccountPerkDisplayModel(
                id = 4,
                type = PerkType.REWARDS,
                title = "Bonus Rewards",
                description = "2x points on all purchases",
                value = "2x",
                minPayment = BigDecimal("50.00"),
                rewardsXp = 150,
                perkAmount = BigDecimal("0.02"),
                isTierBased = false
            )
        )

        AccountType.DEBIT -> listOf(
            AccountPerkDisplayModel(
                id = 5,
                type = PerkType.CASHBACK,
                title = "Grocery Cashback",
                description = "2% cashback on grocery purchases",
                value = "2%",
                minPayment = BigDecimal("50.00"),
                rewardsXp = 100,
                perkAmount = BigDecimal("0.02"),
                isTierBased = false
            ),
            AccountPerkDisplayModel(
                id = 6,
                type = PerkType.FEE_WAIVER,
                title = "ATM Fee Waiver",
                description = "No fees at any ATM worldwide",
                value = "Free",
                minPayment = BigDecimal("0.00"),
                rewardsXp = 50,
                perkAmount = BigDecimal("0.00"),
                isTierBased = false
            )
        )
    }
}