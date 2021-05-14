package fr.woolly.server.auth.repository

import fr.outadoc.mastodonk.api.entity.request.ApplicationCreate
import fr.outadoc.mastodonk.client.MastodonClient
import fr.woolly.server.auth.entity.AppCredentials
import fr.woolly.server.auth.entity.AppCredentialsTable
import fr.woolly.server.config.model.ApplicationConfig
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class ApplicationRepositoryImpl(applicationConfig: ApplicationConfig) : ApplicationRepository {

    private val defaultApp = with(applicationConfig) {
        ApplicationCreate(
            clientName = clientName,
            website = website,
            redirectUris = redirectUris.joinToString("\n"),
            scopes = scopes.joinToString(" ")
        )
    }

    override suspend fun getAppCredentialsForDomain(domain: String): AppCredentials {
        return getExistingAppCredentialsForDomainOrNull(domain)
            ?: createAndSaveApplicationForDomain(domain)
    }

    private fun getExistingAppCredentialsForDomainOrNull(domain: String): AppCredentials? {
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

    private suspend fun createAndSaveApplicationForDomain(domain: String): AppCredentials {
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