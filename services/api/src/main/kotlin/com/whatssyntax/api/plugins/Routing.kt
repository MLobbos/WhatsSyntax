package com.whatssyntax.api.plugins

import com.whatssyntax.api.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/health") {
            call.respondText("OK")
        }

        route("/v1") {
            authRoutes()
            // Phase 2+: uncomment as implemented
            // chatRoutes()
            // messageRoutes()
            // contactRoutes()
            // statusRoutes()
            // callRoutes()
        }
    }
}
