package fr.woolly.server.config.repository

import fr.woolly.server.config.model.ServerConfig
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class ServerConfigRepositoryImpl(private val configFile: File) : ServerConfigRepository {

    private val json = Json {}

    override fun getServerConfig(): ServerConfig {
        val configText = configFile.bufferedReader().readText()
        return json.decodeFromString(configText)
    }
}