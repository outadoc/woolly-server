package fr.outadoc.woolly.server.config.repository

import fr.outadoc.woolly.server.config.model.ServerConfig

interface ServerConfigRepository {
    fun getServerConfig(): ServerConfig
}