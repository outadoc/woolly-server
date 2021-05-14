package fr.woolly.auth.fr.woolly.auth.repository

import fr.outadoc.mastodonk.api.entity.request.ApplicationCreate
import fr.outadoc.mastodonk.client.MastodonClient
import fr.woolly.auth.fr.woolly.auth.entity.AppCredentials
import fr.woolly.auth.fr.woolly.auth.entity.AppCredentialsTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

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
        return getExistingCredentialsForDomain(domain)
            ?: createApplicationForDomain(domain)
    }

    private fun getExistingCredentialsForDomain(domain: String): AppCredentials? {
        return AppCredentialsTable
            .select { AppCredentialsTable.domain eq domain }
            .map {
                AppCredentials(
                    domain = it[AppCredentialsTable.domain],
                    clientId = it[AppCredentialsTable.clientId],
                    clientSecret = it[AppCredentialsTable.clientSecret]
                )
            }
            .firstOrNull()
    }

    private suspend fun createApplicationForDomain(domain: String): AppCredentials {
        val client = MastodonClient {
            this.domain = domain
        }

        val app = client.apps.createApplication(defaultApp).let { app ->
            AppCredentials(
                domain = domain,
                clientId = app.clientId!!,
                clientSecret = app.clientSecret!!
            )
        }

        AppCredentialsTable.insert {
            it[this.domain] = app.domain
            it[this.clientId] = app.clientId
            it[this.clientSecret] = app.clientSecret
        }

        return app
    }
}
