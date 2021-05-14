package fr.woolly.server.config.repository

import fr.woolly.server.config.model.ServerConfig

interface ServerConfigRepository {
    fun getServerConfig(): ServerConfig
}