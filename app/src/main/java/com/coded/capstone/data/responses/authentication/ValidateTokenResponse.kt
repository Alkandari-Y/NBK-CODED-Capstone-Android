package com.coded.capstone.data.responses.authentication

data class ValidateTokenResponse (
    val userId: Long,
    val isActive: Boolean,
    val roles: List<String>,
    val email: String,
    val username: String
)

fun ValidateTokenResponse.toUserInfoDto() = UserInfoDto(
    userId = userId,
    isActive = isActive,
    email = email,
    username = username
)

data class JwtContents (
    val userId: Long,
    val isActive: Boolean,
    val roles: List<String>,
    val type: String
)
