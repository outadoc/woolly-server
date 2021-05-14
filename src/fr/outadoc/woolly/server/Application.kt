package fr.outadoc.woolly.server

import fr.outadoc.woolly.server.auth.repository.ApplicationRepositoryImpl
import fr.outadoc.woolly.server.auth.route.authorizeRoute
import fr.outadoc.woolly.server.auth.route.revokeRoute
import fr.outadoc.woolly.server.auth.route.statusRoute
import fr.outadoc.woolly.server.auth.route.tokenRoute
import fr.outadoc.woolly.server.config.repository.ServerConfigRepositoryImpl
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.serialization.*
import org.jetbrains.exposed.sql.Database
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {

    install(Locations)
    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }

    val env = System.getenv()
    val configPath = env["WOOLLY_CONFIG_PATH"] ?: "/etc/woolly/server_config.json"
    val configRepo = ServerConfigRepositoryImpl(File(configPath))
    val config = configRepo.getServerConfig()

    val appRepository = ApplicationRepositoryImpl(config.applicationConfig)

    Database.connect(
        driver = "com.mysql.jdbc.Driver",
        url = config.mySqlConfig.connectionString,
        user = config.mySqlConfig.username,
        password = config.mySqlConfig.password
    )

    routing {
        statusRoute()
        authorizeRoute(appRepository)
        tokenRoute(appRepository)
        revokeRoute(appRepository)
    }
}
