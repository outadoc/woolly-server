package fr.outadoc.woolly.server.auth.repository

import fr.outadoc.mastodonk.api.entity.request.ApplicationCreate
import fr.outadoc.mastodonk.client.MastodonClient
import fr.outadoc.woolly.server.auth.entity.AppCredentials
import fr.outadoc.woolly.server.auth.entity.AppCredentialsTable
import fr.outadoc.woolly.server.config.model.ApplicationConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ApplicationRepositoryImpl(applicationConfig: ApplicationConfig) : ApplicationRepository {

    init {
        SchemaUtils.create(AppCredentialsTable)
    }

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
        return transaction {
            AppCredentialsTable
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
