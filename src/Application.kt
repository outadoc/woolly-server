@file:OptIn(KtorExperimentalLocationsAPI::class)

package fr.woolly.auth

import fr.woolly.auth.fr.woolly.auth.repository.ApplicationRepositoryImpl
import fr.woolly.auth.fr.woolly.auth.route.authorizeRoute
import fr.woolly.auth.fr.woolly.auth.route.revokeRoute
import fr.woolly.auth.fr.woolly.auth.route.tokenRoute
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.serialization.*
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    install(Locations)

    install(ContentNegotiation) {
        json()
    }

    val env = System.getenv()
    Database.connect(
        driver = "com.mysql.jdbc.Driver",
        url = checkNotNull(env["MYSQL_URL"]) { "MYSQL_URL must be set to a JDBC connection string" },
        user = checkNotNull(env["MYSQL_USER"]) { "MYSQL_USER must be set to a MySQL user" },
        password = checkNotNull(env["MYSQL_PWD"]) { "MYSQL_PWD must be set to a MySQL password" }
    )

    val appRepository = ApplicationRepositoryImpl()

    routing {
        authorizeRoute(appRepository)
        tokenRoute(appRepository)
        revokeRoute(appRepository)
    }
}
