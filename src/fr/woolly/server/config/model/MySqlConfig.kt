package fr.woolly.server.config.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MySqlConfig(

    @SerialName("connectionString")
    val connectionString: String,

    @SerialName("username")
    val username: String,

    @SerialName("password")
    val password: String
)
