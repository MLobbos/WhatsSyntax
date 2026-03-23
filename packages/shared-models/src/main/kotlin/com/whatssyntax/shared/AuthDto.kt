package com.whatssyntax.shared

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequestDto(
    val phone: String
)

@Serializable
data class OtpVerifyDto(
    val phone: String,
    val code: String
)

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)

@Serializable
data class RefreshTokenDto(
    val refreshToken: String
)
