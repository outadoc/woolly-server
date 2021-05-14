package fr.woolly.auth.fr.woolly.auth

import fr.outadoc.mastodonk.client.MastodonApiException
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun ApplicationCall.respondApiError(e: MastodonApiException) {
    val status = HttpStatusCode.fromValue(e.errorCode)
    when (val errorBody = e.apiError) {
        null -> respond(status)
        else -> respond(status, errorBody)
    }
}
