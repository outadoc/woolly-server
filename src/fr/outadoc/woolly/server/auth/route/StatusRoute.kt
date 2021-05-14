@file:OptIn(KtorExperimentalLocationsAPI::class)

package fr.outadoc.woolly.server.auth.route

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/status")
class StatusRoute()

fun Route.statusRoute() {
    get<StatusRoute> {
        call.respondText("OK", ContentType.Text.Plain)
    }
}
