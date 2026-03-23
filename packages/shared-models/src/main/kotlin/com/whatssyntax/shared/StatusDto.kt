package com.whatssyntax.shared

import kotlinx.serialization.Serializable

@Serializable
data class StatusDto(
    val id: String,
    val userId: String,
    val user: ContactDto,
    val contentType: StatusContentType,
    val text: String? = null,
    val mediaUrl: String? = null,
    val expiresAt: String, // ISO-8601, 24h from creation
    val viewedBy: List<String> = emptyList() // user IDs
)

@Serializable
data class CreateStatusDto(
    val contentType: StatusContentType,
    val text: String? = null,
    val mediaUrl: String? = null
)

@Serializable
enum class StatusContentType {
    TEXT,
    IMAGE,
    VIDEO
}
