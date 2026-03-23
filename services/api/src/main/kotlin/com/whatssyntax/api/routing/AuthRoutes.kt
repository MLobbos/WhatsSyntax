package com.whatssyntax.api.routing

import com.whatssyntax.api.domain.auth.JwtService
import com.whatssyntax.api.domain.auth.UserRepository
import com.whatssyntax.api.plugins.BadRequestException
import com.whatssyntax.api.plugins.UnauthorizedException
import com.whatssyntax.shared.AuthRequestDto
import com.whatssyntax.shared.AuthResponseDto
import com.whatssyntax.shared.OtpVerifyDto
import com.whatssyntax.shared.RefreshTokenDto
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(
    userRepository: UserRepository,
    jwtService: JwtService
) {
    route("/auth") {

        /**
         * POST /v1/auth/request-otp
         * Sends a 6-digit OTP to the given phone number.
         * In dev: set env DEV_OTP_BYPASS=123456 to skip SMS.
         */
        post("/request-otp") {
            val body = call.receive<AuthRequestDto>()
            if (body.phone.isBlank()) throw BadRequestException("phone is required")

            val devBypass = System.getenv("DEV_OTP_BYPASS")
            val code = devBypass ?: (100000..999999).random().toString()

            userRepository.saveOtp(body.phone, code)

            if (devBypass != null) {
                call.application.environment.log.info("DEV OTP for ${body.phone}: $code")
            } else {
                // TODO: replace with real Twilio call
                call.application.environment.log.info("STUB SMS to ${body.phone}: $code")
            }

            call.respond(mapOf("message" to "OTP sent"))
        }

        /**
         * POST /v1/auth/verify-otp
         * Validates OTP, creates or retrieves user, returns JWT pair.
         */
        post("/verify-otp") {
            val body = call.receive<OtpVerifyDto>()

            val valid = userRepository.consumeOtp(body.phone, body.code)
            if (!valid) throw UnauthorizedException("Invalid or expired OTP code")

            val user   = userRepository.findOrCreateUser(body.phone)
            val tokens = jwtService.generateTokenPair(user.id)

            userRepository.saveRefreshToken(
                id         = tokens.refreshTokenId,
                userId     = user.id,
                tokenHash  = jwtService.hashToken(tokens.refreshToken),
                expiresAt  = jwtService.refreshTokenExpiryDate()
            )

            call.respond(
                AuthResponseDto(
                    accessToken  = tokens.accessToken,
                    refreshToken = tokens.refreshToken,
                    user         = user
                )
            )
        }

        /**
         * POST /v1/auth/refresh
         * Exchange a valid refresh token for a new access token.
         */
        post("/refresh") {
            val body = call.receive<RefreshTokenDto>()
            val hash   = jwtService.hashToken(body.refreshToken)
            val userId = userRepository.findUserByRefreshTokenHash(hash)
                ?: throw UnauthorizedException("Invalid or expired refresh token")

            // Rotate refresh token
            userRepository.deleteRefreshToken(hash)
            val tokens = jwtService.generateTokenPair(userId)
            userRepository.saveRefreshToken(
                id        = tokens.refreshTokenId,
                userId    = userId,
                tokenHash = jwtService.hashToken(tokens.refreshToken),
                expiresAt = jwtService.refreshTokenExpiryDate()
            )

            call.respond(
                mapOf(
                    "accessToken"  to tokens.accessToken,
                    "refreshToken" to tokens.refreshToken
                )
            )
        }
    }
}
