package com.whatssyntax.shared

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: String,
    val chatId: String,
    val senderId: String,
    val text: String,
    val timestamp: String, // ISO-8601
    val delivered: Boolean = false,
    val read: Boolean = false
)

@Serializable
data class SendMessageDto(
    val text: String
)

/** WebSocket envelope — covers all real-time event types */
@Serializable
data class WsEnvelope(
    val type: WsEventType,
    val payload: String // JSON-encoded payload; parse based on `type`
)

@Serializable
enum class WsEventType {
    MESSAGE,
    DELIVERED,
    READ,
    TYPING,
    PRESENCE
}
