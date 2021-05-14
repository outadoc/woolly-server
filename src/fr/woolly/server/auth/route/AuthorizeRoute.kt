@file:OptIn(KtorExperimentalLocationsAPI::class)

package fr.woolly.server.auth.route

import fr.woolly.server.auth.repository.ApplicationRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/oauth/{domain}/authorize")
class AuthorizeRoute(
    val domain: String,
    val scope: String? = null,
    val redirect_uri: String = "urn:ietf:wg:oauth:2.0:oob"
)

fun Route.authorizeRoute(appRepository: ApplicationRepository) {
    get<AuthorizeRoute> { req ->
        val app = appRepository.getAppCredentialsForDomain(req.domain.trim())

        val url = URLBuilder(
            protocol = URLProtocol.HTTPS,
            host = req.domain.trim(),
            encodedPath = "oauth/authorize",
            parameters = ParametersBuilder().apply {
                append("client_id", app.clientId)
                append("redirect_uri", req.redirect_uri)
                append("response_type", "code")
                req.scope?.let { scope -> append("scope", scope) }
            }
        ).buildString()

        call.respondRedirect(url, permanent = false)
    }
}
