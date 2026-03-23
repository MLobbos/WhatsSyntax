package com.syntax_institut.whatssyntax.data.remote

import com.whatssyntax.shared.AuthRequestDto
import com.whatssyntax.shared.AuthResponseDto
import com.whatssyntax.shared.OtpVerifyDto
import com.whatssyntax.shared.RefreshTokenDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("v1/auth/request-otp")
    suspend fun requestOtp(@Body body: AuthRequestDto): Map<String, String>

    @POST("v1/auth/verify-otp")
    suspend fun verifyOtp(@Body body: OtpVerifyDto): AuthResponseDto

    @POST("v1/auth/refresh")
    suspend fun refreshToken(@Body body: RefreshTokenDto): Map<String, String>
}
