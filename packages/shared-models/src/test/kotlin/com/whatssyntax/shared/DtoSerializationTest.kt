package com.whatssyntax.shared

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class DtoSerializationTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `MessageDto roundtrip serialization`() {
        val msg = MessageDto(
            id = "msg-1",
            chatId = "chat-1",
            senderId = "user-1",
            text = "Hello!",
            timestamp = "2024-01-01T12:00:00Z",
            delivered = true
        )
        val encoded = json.encodeToString(msg)
        val decoded = json.decodeFromString<MessageDto>(encoded)
        assertEquals(msg, decoded)
    }

    @Test
    fun `ContactDto roundtrip serialization`() {
        val contact = ContactDto(
            id = "user-2",
            displayName = "Alice",
            phone = "+49123456789",
            avatarUrl = "https://example.com/avatar.jpg"
        )
        val encoded = json.encodeToString(contact)
        val decoded = json.decodeFromString<ContactDto>(encoded)
        assertEquals(contact, decoded)
    }

    @Test
    fun `AuthResponseDto roundtrip serialization`() {
        val user = UserDto(
            id = "user-1",
            phone = "+49123456789",
            displayName = "Bob",
            createdAt = "2024-01-01T00:00:00Z"
        )
        val auth = AuthResponseDto(
            accessToken = "jwt-access",
            refreshToken = "jwt-refresh",
            user = user
        )
        val encoded = json.encodeToString(auth)
        val decoded = json.decodeFromString<AuthResponseDto>(encoded)
        assertEquals(auth, decoded)
    }

    @Test
    fun `WsEnvelope roundtrip serialization`() {
        val envelope = WsEnvelope(
            type = WsEventType.MESSAGE,
            payload = """{"id":"msg-1"}"""
        )
        val encoded = json.encodeToString(envelope)
        val decoded = json.decodeFromString<WsEnvelope>(encoded)
        assertEquals(envelope, decoded)
    }
}
