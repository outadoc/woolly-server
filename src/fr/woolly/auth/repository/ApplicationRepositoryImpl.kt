package fr.woolly.auth.fr.woolly.auth.repository

import fr.outadoc.mastodonk.api.entity.request.ApplicationCreate
import fr.outadoc.mastodonk.client.MastodonClient
import fr.woolly.auth.fr.woolly.auth.entity.AppCredentials

class ApplicationRepositoryImpl : ApplicationRepository {

    private val defaultApp = ApplicationCreate(
        clientName = "Woolly",
        website = "https://woolly.app",
        scopes = "read write follow push",
        redirectUris = listOf(
            "urn:ietf:wg:oauth:2.0:oob",
            "woolly://oauth/callback",
            "https://woolly.app/oauth/callback"
        ).joinToString("\n")
    )

    override suspend fun getApplicationForDomain(domain: String): AppCredentials {
        TODO()
    }

    private suspend fun createApplicationForDomain(domain: String): AppCredentials {
        val client = MastodonClient {
            this.domain = domain
        }

        return client.apps.createApplication(defaultApp).let { app ->
            AppCredentials(
                domain = domain,
                clientId = app.clientId!!,
                clientSecret = app.clientSecret!!
            )
        }
    }
}
