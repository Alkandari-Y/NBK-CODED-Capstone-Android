package com.coded.capstone.formstates.accounts



import java.math.BigDecimal

data class AccountCreateForm(
    val name: String = "",
    val initialBalance: String = "",
    var nameError: String? = null,
    var balanceError: String? = null
) {
    fun validate(): Boolean {
        var valid = true

        if (name.isBlank()) {
            nameError = "Account name cannot be blank"
            valid = false
        }

        val parsedBalance = initialBalance.toBigDecimalOrNull()
        if (parsedBalance == null || parsedBalance < BigDecimal("100.00")) {
            balanceError = "Initial balance must be at least 100.00"
            valid = false
        }

        return valid
    }
}