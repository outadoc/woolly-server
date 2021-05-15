@file:OptIn(KtorExperimentalLocationsAPI::class)

package fr.outadoc.woolly.server.auth.route

import fr.outadoc.mastodonk.api.entity.request.TokenRevoke
import fr.outadoc.mastodonk.client.MastodonApiException
import fr.outadoc.mastodonk.client.MastodonClient
import fr.outadoc.woolly.server.auth.repository.ApplicationRepository
import fr.outadoc.woolly.server.auth.respondApiError
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/oauth/{domain}/revoke")
class RevokeRoute(val domain: String)

fun Route.revokeRoute(appRepository: ApplicationRepository) {
    post<RevokeRoute> { req ->
        val params = call.receiveParameters()

        val token = params["token"]
        if (token == null) {
            call.respond(HttpStatusCode.BadRequest, "Token is required")
            return@post
        }

        val app = appRepository.getAppCredentialsForDomain(req.domain.trim())
        val client = MastodonClient {
            domain = req.domain.trim()
        }

        try {
            client.oauth.revokeToken(
                TokenRevoke(
                    clientId = app.clientId,
                    clientSecret = app.clientSecret,
                    token = token
                )
            )

            call.respond(HttpStatusCode.OK)
        } catch (e: MastodonApiException) {
            call.respondApiError(e)
        }
    }
}
