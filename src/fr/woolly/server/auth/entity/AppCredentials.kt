package fr.woolly.server.auth.entity

data class AppCredentials(
    val domain: String,
    val clientId: String,
    val clientSecret: String,
)
