package com.whatssyntax.shared

import kotlinx.serialization.Serializable

@Serializable
data class ChatDto(
    val id: String,
    val participants: List<ContactDto>,
    val lastMessage: MessageDto? = null,
    val updatedAt: String, // ISO-8601
    val unreadCount: Int = 0
)

@Serializable
data class CreateChatDto(
    val participantPhone: String
)
