package com.whatssyntax.shared

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val phone: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val statusText: String? = null,
    val createdAt: String // ISO-8601
)

@Serializable
data class ContactDto(
    val id: String,
    val displayName: String,
    val phone: String,
    val avatarUrl: String? = null,
    val statusText: String? = null
)

@Serializable
data class UpdateProfileDto(
    val displayName: String? = null,
    val avatarUrl: String? = null,
    val statusText: String? = null
)
