package com.coded.capstone.formstates.authentication

data class LoginFormState(
    var password: String = "",
    var username: String = "",
    var passwordError: String? = null,
    var usernameError: String? = null
) {
    val formIsValid: Boolean
        get() = passwordIsValid && usernameIsValid

    val passwordIsValid: Boolean
        get() = password.length in 6..50

    val usernameIsValid: Boolean
        get() = username.isNotBlank() && username.length <= 50

    fun validate(): LoginFormState {
        return this.copy(
            passwordError = when {
                password.isBlank() -> "Password cannot be empty"
                password.length < 6 -> "Password must be at least 6 characters"
                password.length > 50 -> "Password must not exceed 50 characters"
                else -> null
            },
            usernameError = when {
                username.isBlank() -> "Username cannot be empty"
                username.length > 50 -> "Username must not exceed 50 characters"
                else -> null
            }
        )
    }
}