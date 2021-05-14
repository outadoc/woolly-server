@file:OptIn(KtorExperimentalLocationsAPI::class)

package fr.woolly.auth.fr.woolly.auth.route

import fr.outadoc.mastodonk.api.entity.request.TokenRevoke
import fr.outadoc.mastodonk.client.MastodonApiException
import fr.outadoc.mastodonk.client.MastodonClient
import fr.woolly.auth.fr.woolly.auth.repository.ApplicationRepository
import fr.woolly.auth.fr.woolly.auth.respondApiError
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/oauth/{domain}/revoke")
class RevokeRoute(
    val domain: String,
    val token: String
)

fun Route.revokeRoute(appRepository: ApplicationRepository) {
    post<RevokeRoute> { req ->
        val app = appRepository.getAppCredentialsForDomain(req.domain.trim())

        val client = MastodonClient {
            domain = req.domain.trim()
        }

        try {
            client.oauth.revokeToken(
                TokenRevoke(
                    clientId = app.clientId,
                    clientSecret = app.clientSecret,
                    token = req.token
                )
            )

            call.respond(HttpStatusCode.OK)
        } catch (e: MastodonApiException) {
            call.respondApiError(e)
        }
    }
}
