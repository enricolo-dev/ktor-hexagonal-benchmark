package com.quick.tor.common

import com.quick.tor.common.exceptions.RestMissingRequestParameterException
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.request.uri
import io.ktor.response.respond

internal suspend inline fun <reified T : Any> ApplicationCall.receiveValidated(): T {
    return try {
        receive()
    } catch (e: Exception) {
        // todo handler error
        throw e
    }
}

internal fun ApplicationCall.stringParameter(name: String) =
    this.parameters[name] ?: throw RestMissingRequestParameterException(paramName = name)

internal fun ApplicationCall.uuidParameter(name: String) =
    stringParameter(name).toUUID() ?: throw RestMissingRequestParameterException(paramName = name)
