package com.whatssyntax.api.data.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object Users : Table("users") {
    val id          = text("id")
    val phone       = text("phone")
    val displayName = text("display_name")
    val avatarUrl   = text("avatar_url").nullable()
    val statusText  = text("status_text").nullable()
    val createdAt   = timestampWithTimeZone("created_at")
    val updatedAt   = timestampWithTimeZone("updated_at")

    override val primaryKey = PrimaryKey(id)
}

object OtpCodes : Table("otp_codes") {
    val phone     = text("phone")
    val code      = text("code")
    val expiresAt = timestampWithTimeZone("expires_at")

    override val primaryKey = PrimaryKey(phone)
}

object RefreshTokens : Table("refresh_tokens") {
    val id        = text("id")
    val userId    = text("user_id").references(Users.id)
    val tokenHash = text("token_hash")
    val expiresAt = timestampWithTimeZone("expires_at")
    val createdAt = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(id)
}
