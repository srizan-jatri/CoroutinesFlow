package com.srizan.flowpractice.api

import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T : Any> safeApiCall(
    call: suspend () -> Response<T>
): NetworkResource<T> {
    try {
        val response = call.invoke()
        if (response.isSuccessful) {
            if (response.body() != null) {
                return NetworkResource.Success(response.body())
            }
        }
        return NetworkResource.Error(response.message())
    } catch (e: Exception) {
        return when (e) {
            is ConnectException -> {
                NetworkResource.Error(CONNECT_EXCEPTION)
            }
            is UnknownHostException -> {
                NetworkResource.Error(UNKNOWN_HOST_EXCEPTION)
            }
            is SocketTimeoutException -> {
                NetworkResource.Error(SOCKET_TIME_OUT_EXCEPTION)
            }
            is HttpException -> {
                NetworkResource.Error(UNKNOWN_NETWORK_EXCEPTION)
            }
            else -> {
                NetworkResource.Error(UNKNOWN_NETWORK_EXCEPTION)
            }
        }
    }
}