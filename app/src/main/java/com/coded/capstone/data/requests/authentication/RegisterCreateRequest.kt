package com.coded.capstone.data.requests.authentication

data class RegisterCreateRequest(
    val username: String,
    val password: String,
    val email: String,
    val civilId: String,
)