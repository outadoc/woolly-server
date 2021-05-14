package fr.outadoc.woolly.server.auth.repository

import fr.outadoc.woolly.server.auth.entity.AppCredentials

interface ApplicationRepository {
    suspend fun getAppCredentialsForDomain(domain: String): AppCredentials
}
