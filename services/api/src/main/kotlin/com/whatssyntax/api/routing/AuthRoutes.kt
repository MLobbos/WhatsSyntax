package com.whatssyntax.api.routing

import com.whatssyntax.api.plugins.BadRequestException
import com.whatssyntax.api.plugins.UnauthorizedException
import com.whatssyntax.shared.AuthRequestDto
import com.whatssyntax.shared.AuthResponseDto
import com.whatssyntax.shared.OtpVerifyDto
import com.whatssyntax.shared.UserDto
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// Placeholder in-memory OTP store — replaced by Redis in Phase 1
private val otpStore = mutableMapOf<String, String>()

fun Route.authRoutes() {
    route("/auth") {

        /**
         * POST /v1/auth/request-otp
         * Body: { "phone": "+49123456789" }
         * Sends OTP via SMS (or logs to console if DEV_OTP_BYPASS is set).
         */
        post("/request-otp") {
            val body = call.receive<AuthRequestDto>()
            if (body.phone.isBlank()) throw BadRequestException("phone is required")

            val devBypass = System.getenv("DEV_OTP_BYPASS")
            if (devBypass != null) {
                // Dev mode: store fixed code, skip SMS
                otpStore[body.phone] = devBypass
                call.application.environment.log.info("DEV OTP for ${body.phone}: $devBypass")
            } else {
                // TODO Phase 1: send real SMS via Twilio
                val code = (100000..999999).random().toString()
                otpStore[body.phone] = code
                call.application.environment.log.info("OTP for ${body.phone}: $code (STUB — wire Twilio in Phase 1)")
            }

            call.respond(mapOf("message" to "OTP sent"))
        }

        /**
         * POST /v1/auth/verify-otp
         * Body: { "phone": "+49123456789", "code": "123456" }
         * Returns JWT access + refresh tokens.
         */
        post("/verify-otp") {
            val body = call.receive<OtpVerifyDto>()
            val expected = otpStore[body.phone]
                ?: throw UnauthorizedException("No OTP requested for this phone")
            if (body.code != expected) throw UnauthorizedException("Invalid OTP code")

            otpStore.remove(body.phone)

            // TODO Phase 1: look up or create user in Postgres, issue real JWT
            val stubUser = UserDto(
                id = "stub-user-id",
                phone = body.phone,
                displayName = "New User",
                createdAt = "2024-01-01T00:00:00Z"
            )
            val stubResponse = AuthResponseDto(
                accessToken = "stub-access-token",
                refreshToken = "stub-refresh-token",
                user = stubUser
            )
            call.respond(stubResponse)
        }
    }
}
