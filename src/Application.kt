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

fun Application.module() {
    install(Locations)

    install(ContentNegotiation) {
        json()
    }

    val env = System.getenv()
    Database.connect(
        driver = "com.mysql.jdbc.Driver",
        url = env["MYSQL_URL"]!!,
        user = env["MYSQL_USER"]!!,
        password = env["MYSQL_PWD"]!!
    )

    val appRepository = ApplicationRepositoryImpl()

    routing {
        authorizeRoute(appRepository)
        tokenRoute(appRepository)
        revokeRoute(appRepository)
    }
}
