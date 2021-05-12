@file:OptIn(KtorExperimentalLocationsAPI::class)

package fr.woolly.auth

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

fun Application.module() {
    install(Locations) {
    }

    install(Compression) {
        gzip {
            priority = 1.0
        }

        deflate {
            priority = 10.0
            minimumSize(1024)
        }
    }

    routing {
        get<AuthorizeRoute> { req ->
            // TODO get the right client_id for this instance
            // aka create it persist it in db if it doesn't exist, otherwise fetch it
            call.respondRedirect(permanent = false) {
                takeFrom(Url(req.instance))
                encodedPath = "oauth/authorize"
                with(parameters) {
                    remove("instance")
                    append("client_id", "TODO")
                }
            }
        }

        post<TokenRoute> { req ->
            // TODO proxy the request to the instance with our added client_id/client_secret and return its response
            call.respondText("Token")
        }
    }
}

@Location("/oauth/authorize")
class AuthorizeRoute(
    val instance: String,
    val scope: String,
    val redirect_uri: String = "urn:ietf:wg:oauth:2.0:oob",
    val response_type: String = "code"
)

@Location("/oauth/token")
class TokenRoute(
    val instance: String,
    val code: String,
    val scope: String,
    val redirect_uri: String = "urn:ietf:wg:oauth:2.0:oob",
    val grant_type: String = "authorization_code"
)
