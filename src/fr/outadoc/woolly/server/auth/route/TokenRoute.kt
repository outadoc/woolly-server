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

data class TokenParameters(
    val code: String,
    val scope: String? = null,
    val redirect_uri: String = "urn:ietf:wg:oauth:2.0:oob"
)

fun Route.tokenRoute(appRepository: ApplicationRepository) {
    post<TokenRoute> { req ->
        val params = call.receive<TokenParameters>()
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
                    redirectUri = params.redirect_uri,
                    scope = params.scope,
                    code = params.code
                )
            )

            call.respond(HttpStatusCode.OK, token)
        } catch (e: MastodonApiException) {
            call.respondApiError(e)
        }
    }
}
