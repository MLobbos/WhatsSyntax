package com.syntax_institut.whatssyntax.repository

import com.syntax_institut.whatssyntax.data.TokenDataStore
import com.syntax_institut.whatssyntax.data.remote.ApiService
import com.whatssyntax.shared.AuthRequestDto
import com.whatssyntax.shared.AuthResponseDto
import com.whatssyntax.shared.OtpVerifyDto
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val tokenDataStore: TokenDataStore
) {

    suspend fun requestOtp(phone: String): AuthResult<Unit> = runCatching {
        api.requestOtp(AuthRequestDto(phone))
    }.fold(
        onSuccess = { AuthResult.Success(Unit) },
        onFailure = { AuthResult.Error(it.message ?: "Failed to send OTP") }
    )

    suspend fun verifyOtp(phone: String, code: String): AuthResult<AuthResponseDto> = runCatching {
        api.verifyOtp(OtpVerifyDto(phone, code))
    }.fold(
        onSuccess = { response ->
            tokenDataStore.saveTokens(
                accessToken  = response.accessToken,
                refreshToken = response.refreshToken,
                userId       = response.user.id,
                phone        = response.user.phone
            )
            AuthResult.Success(response)
        },
        onFailure = { AuthResult.Error(it.message ?: "Invalid OTP code") }
    )

    suspend fun isLoggedIn(): Boolean {
        var result = false
        tokenDataStore.isLoggedIn.collect { result = it; return@collect }
        return result
    }

    suspend fun logout() {
        tokenDataStore.clear()
    }
}
