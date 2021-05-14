package fr.outadoc.woolly.server.config.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerConfig(

    @SerialName("mysql")
    val mySqlConfig: MySqlConfig,

    @SerialName("application")
    val applicationConfig: ApplicationConfig
)
