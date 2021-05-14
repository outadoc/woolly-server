package fr.woolly.server.auth.repository

import fr.woolly.server.auth.entity.AppCredentials

interface ApplicationRepository {
    suspend fun getAppCredentialsForDomain(domain: String): AppCredentials
}
