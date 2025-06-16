package com.coded.capstone.formstates.authentication

import com.coded.capstone.data.responses.errors.ValidationError


data class RegisterFormState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val email: String = "",


    val usernameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val emailError: String? = null,

) {
    val formIsValid: Boolean
        get() = listOfNotNull(
            usernameError, passwordError,
            confirmPasswordError, emailError,
        ).isEmpty()

    fun validate(): RegisterFormState {
        return this.copy(
            usernameError = when {
                username.isBlank() -> "Username is required"
                username.length < 3 -> "Username must be at least 3 characters"
                else -> null
            },
            passwordError = when {
                password.isBlank() -> "Password is required"
                password.length < 6 -> "Password must be at least 6 characters"
                !Regex("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])").containsMatchIn(password) ->
                    "Password must contain uppercase, lowercase and a digit"
                else -> null
            },
            confirmPasswordError = when {
                confirmPassword != password -> "Passwords do not match"
                else -> null
            },
            emailError = when {
                email.isBlank() -> "Email is required"
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email"
                else -> null
            },
        )
    }

    fun applyServerErrors(fieldErrors: List<ValidationError>): RegisterFormState {
        return this.copy(
            usernameError = fieldErrors.find { it.field == "username" }?.message ?: usernameError,
            emailError = fieldErrors.find { it.field == "email" }?.message ?: emailError,
            passwordError = fieldErrors.find { it.field == "password" }?.message ?: passwordError,
//            civilIdError = fieldErrors.find { it.field == "civilId" }?.message ?: civilIdError
        )
    }

}
