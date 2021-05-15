@file:OptIn(KtorExperimentalLocationsAPI::class)

package fr.outadoc.woolly.server.auth.route

import fr.outadoc.mastodonk.api.entity.GrantType
import fr.outadoc.mastodonk.api.entity.request.TokenGet
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

@Location("/oauth/{domain}/token")
class TokenRoute(
    val domain: String
)

fun Route.tokenRoute(appRepository: ApplicationRepository) {
    post<TokenRoute> { req ->
        val params = call.receiveParameters()

        val code = params["code"]
        if (code == null) {
            call.respond(HttpStatusCode.BadRequest, "Code is required")
            return@post
        }

        val app = appRepository.getAppCredentialsForDomain(req.domain.trim())
        val client = MastodonClient {
            domain = req.domain.trim()
        }

        try {
            val token = client.oauth.getToken(
                TokenGet(
                    clientId = app.clientId,
                    clientSecret = app.clientSecret,
                    grantType = GrantType.AuthorizationCode,
                    redirectUri = params["redirect_uri"] ?: "urn:ietf:wg:oauth:2.0:oob",
                    scope = params["scope"],
                    code = code
                )
            )

            call.respond(HttpStatusCode.OK, token)
        } catch (e: MastodonApiException) {
            call.respondApiError(e)
        }
    }
}
