package com.whatssyntax.api.domain.auth

import com.whatssyntax.api.data.tables.OtpCodes
import com.whatssyntax.api.data.tables.RefreshTokens
import com.whatssyntax.api.data.tables.Users
import com.whatssyntax.shared.UserDto
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.OffsetDateTime
import java.util.*

class UserRepository {

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    // ── OTP ──────────────────────────────────────────────────────────────────

    suspend fun saveOtp(phone: String, code: String, ttlMinutes: Long = 10) = dbQuery {
        val expiresAt = OffsetDateTime.now().plusMinutes(ttlMinutes)
        OtpCodes.upsert {
            it[OtpCodes.phone] = phone
            it[OtpCodes.code] = code
            it[OtpCodes.expiresAt] = expiresAt
        }
    }

    suspend fun consumeOtp(phone: String, code: String): Boolean = dbQuery {
        val row = OtpCodes.selectAll()
            .where { OtpCodes.phone eq phone }
            .singleOrNull() ?: return@dbQuery false

        val expired = row[OtpCodes.expiresAt].isBefore(OffsetDateTime.now())
        val match   = row[OtpCodes.code] == code

        if (!expired && match) {
            OtpCodes.deleteWhere { OtpCodes.phone eq phone }
            true
        } else {
            false
        }
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    suspend fun findOrCreateUser(phone: String): UserDto = dbQuery {
        val existing = Users.selectAll()
            .where { Users.phone eq phone }
            .singleOrNull()

        if (existing != null) {
            existing.toUserDto()
        } else {
            val newId = UUID.randomUUID().toString()
            val now = OffsetDateTime.now()
            Users.insert {
                it[id] = newId
                it[Users.phone] = phone
                it[displayName] = "New User"
                it[createdAt] = now
                it[updatedAt] = now
            }
            Users.selectAll()
                .where { Users.id eq newId }
                .single()
                .toUserDto()
        }
    }

    private fun ResultRow.toUserDto() = UserDto(
        id          = this[Users.id],
        phone       = this[Users.phone],
        displayName = this[Users.displayName],
        avatarUrl   = this[Users.avatarUrl],
        statusText  = this[Users.statusText],
        createdAt   = this[Users.createdAt].toString()
    )

    // ── Refresh tokens ────────────────────────────────────────────────────────

    suspend fun saveRefreshToken(
        id: String,
        userId: String,
        tokenHash: String,
        expiresAt: java.util.Date
    ) = dbQuery {
        RefreshTokens.insert {
            it[RefreshTokens.id] = id
            it[RefreshTokens.userId] = userId
            it[RefreshTokens.tokenHash] = tokenHash
            it[RefreshTokens.expiresAt] = expiresAt.toInstant()
                .atOffset(java.time.ZoneOffset.UTC)
            it[RefreshTokens.createdAt] = OffsetDateTime.now()
        }
    }

    suspend fun findUserByRefreshTokenHash(hash: String): String? = dbQuery {
        val row = RefreshTokens.selectAll()
            .where { RefreshTokens.tokenHash eq hash }
            .singleOrNull() ?: return@dbQuery null

        val expired = row[RefreshTokens.expiresAt].isBefore(OffsetDateTime.now())
        if (expired) null else row[RefreshTokens.userId]
    }

    suspend fun deleteRefreshToken(hash: String) = dbQuery {
        RefreshTokens.deleteWhere { RefreshTokens.tokenHash eq hash }
    }
}
