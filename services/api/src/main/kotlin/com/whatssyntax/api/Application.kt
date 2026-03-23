package com.whatssyntax.api

import com.whatssyntax.api.data.DatabaseFactory
import com.whatssyntax.api.domain.auth.JwtService
import com.whatssyntax.api.domain.auth.UserRepository
import com.whatssyntax.api.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    // Infrastructure
    DatabaseFactory.init(this)

    // Domain services
    val userRepository = UserRepository()
    val jwtService     = JwtService(this)

    // Ktor plugins
    configureSerialization()
    configureCors()
    configureAuth()
    configureStatusPages()
    configureCallLogging()
    configureWebSockets()
    configureRouting(userRepository, jwtService)
}
