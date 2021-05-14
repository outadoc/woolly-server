@file:OptIn(KtorExperimentalLocationsAPI::class)

package fr.woolly.auth

import fr.woolly.auth.fr.woolly.auth.repository.ApplicationRepositoryImpl
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.Database

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

    val env = System.getenv()
    Database.connect(
        driver = "com.mysql.jdbc.Driver",
        url = env["MYSQL_URL"]!!,
        user = env["MYSQL_USER"]!!,
        password = env["MYSQL_PWD"]!!
    )

    val repository = ApplicationRepositoryImpl()

    routing {
        get<AuthorizeRoute> { req ->
            val app = repository.getApplicationForDomain(req.domain.trim())
            val url = URLBuilder(
                protocol = URLProtocol.HTTPS,
                host = req.domain,
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

        post<TokenRoute> { req ->
            val app = repository.getApplicationForDomain(req.domain.trim())

            // TODO proxy the request to the instance with our added client_id/client_secret and return its response
            call.respondText("Token")
        }
    }
}

@Location("/oauth/authorize")
class AuthorizeRoute(
    val domain: String,
    val scope: String? = null,
    val redirect_uri: String = "urn:ietf:wg:oauth:2.0:oob"
)

@Location("/oauth/token")
class TokenRoute(
    val domain: String,
    val code: String,
    val scope: String? = null,
    val redirect_uri: String = "urn:ietf:wg:oauth:2.0:oob"
)
