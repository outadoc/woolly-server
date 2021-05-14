package fr.woolly.auth.fr.woolly.auth.repository

import fr.woolly.auth.fr.woolly.auth.entity.AppCredentials

interface ApplicationRepository {
    suspend fun getApplicationForDomain(domain: String): AppCredentials
}