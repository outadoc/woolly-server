@file:OptIn(KtorExperimentalLocationsAPI::class)

package fr.woolly.auth.fr.woolly.auth.route

import fr.outadoc.mastodonk.api.entity.GrantType
import fr.outadoc.mastodonk.api.entity.request.TokenGet
import fr.outadoc.mastodonk.client.MastodonApiException
import fr.outadoc.mastodonk.client.MastodonClient
import fr.woolly.auth.fr.woolly.auth.repository.ApplicationRepository
import fr.woolly.auth.fr.woolly.auth.respondApiError
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/oauth/{domain}/token")
class TokenRoute(
    val domain: String,
    val code: String,
    val scope: String? = null,
    val redirect_uri: String = "urn:ietf:wg:oauth:2.0:oob"
)

fun Route.tokenRoute(appRepository: ApplicationRepository) {
    post<TokenRoute> { req ->
        val app = appRepository.getAppCredentialsForDomain(req.domain.trim())

        val client = MastodonClient {
            domain = req.domain.trim()
        }

        try {
            val token = client.oauth.getToken(
                TokenGet(
                    clientId = app.clientId,
                    clientSecret = app.clientSecret,
                    redirectUri = req.redirect_uri,
                    grantType = GrantType.AuthorizationCode,
                    scope = req.scope,
                    code = req.code
                )
            )

            call.respond(HttpStatusCode.OK, token)
        } catch (e: MastodonApiException) {
            call.respondApiError(e)
        }
    }
}
