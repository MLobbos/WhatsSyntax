package com.whatssyntax.api.domain.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import java.security.MessageDigest
import java.util.*

data class TokenPair(val accessToken: String, val refreshToken: String, val refreshTokenId: String)

class JwtService(application: Application) {

    private val config = application.environment.config
    private val secret = config.property("jwt.secret").getString()
    private val issuer = config.property("jwt.issuer").getString()
    private val audience = config.property("jwt.audience").getString()
    private val accessExpiryMinutes = config.propertyOrNull("jwt.accessTokenExpiryMinutes")
        ?.getString()?.toLongOrNull() ?: 15L
    private val refreshExpiryDays = config.propertyOrNull("jwt.refreshTokenExpiryDays")
        ?.getString()?.toLongOrNull() ?: 30L

    private val algorithm = Algorithm.HMAC256(secret)

    fun generateTokenPair(userId: String): TokenPair {
        val now = Date()

        val accessToken = JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withIssuedAt(now)
            .withExpiresAt(Date(now.time + accessExpiryMinutes * 60 * 1000))
            .sign(algorithm)

        val rawRefresh = UUID.randomUUID().toString()
        val refreshId = UUID.randomUUID().toString()

        return TokenPair(
            accessToken = accessToken,
            refreshToken = rawRefresh,
            refreshTokenId = refreshId
        )
    }

    fun refreshTokenExpiryDate(): Date =
        Date(System.currentTimeMillis() + refreshExpiryDays * 24 * 60 * 60 * 1000)

    fun hashToken(raw: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(raw.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
