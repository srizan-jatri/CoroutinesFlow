package com.srizan.flowpractice.api

sealed class NetworkResource<T>(
    val data: T? = null,
    val errorMessage: String? = null
) {
    class Success<T>(data: T?) : NetworkResource<T>(data)
    class Error<T>(errorMessage: String?, data: T? = null) : NetworkResource<T>(data, errorMessage)
}