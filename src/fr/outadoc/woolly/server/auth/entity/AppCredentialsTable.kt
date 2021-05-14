package fr.outadoc.woolly.server.auth.entity

import org.jetbrains.exposed.sql.Table

object AppCredentialsTable : Table() {
    val domain = varchar("domain", 512)
    val clientId = varchar("client_id", 512)
    val clientSecret = varchar("client_id", 512)
    override val primaryKey = PrimaryKey(domain)
}
