package com.whatssyntax.shared

import kotlinx.serialization.Serializable

@Serializable
data class CallDto(
    val id: String,
    val callerId: String,
    val recipientId: String,
    val caller: ContactDto,
    val recipient: ContactDto,
    val type: CallType,
    val direction: CallDirection,
    val durationSeconds: Int? = null,
    val startedAt: String, // ISO-8601
    val endedAt: String? = null
)

@Serializable
enum class CallType {
    VOICE,
    VIDEO
}

@Serializable
enum class CallDirection {
    INCOMING,
    OUTGOING,
    MISSED
}
