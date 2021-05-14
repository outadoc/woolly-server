package fr.outadoc.woolly.server.config.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationConfig(

    @SerialName("name")
    val clientName: String,

    @SerialName("website")
    val website: String?,

    @SerialName("redirectUris")
    val redirectUris: List<String>,

    @SerialName("scopes")
    val scopes: List<String>
)
