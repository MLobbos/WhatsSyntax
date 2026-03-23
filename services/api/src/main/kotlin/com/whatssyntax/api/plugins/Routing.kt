package com.whatssyntax.api.plugins

import com.whatssyntax.api.domain.auth.JwtService
import com.whatssyntax.api.domain.auth.UserRepository
import com.whatssyntax.api.routing.authRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userRepository: UserRepository,
    jwtService: JwtService
) {
    routing {
        get("/health") {
            call.respondText("OK")
        }

        route("/v1") {
            authRoutes(userRepository, jwtService)
            // Phase 2+: chatRoutes(), messageRoutes(), contactRoutes(), statusRoutes()
        }
    }
}
