package com.whatssyntax.api

import com.whatssyntax.api.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureCors()
    configureAuth()
    configureStatusPages()
    configureCallLogging()
    configureWebSockets()
    configureRouting()
}
