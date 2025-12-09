package com.toufiq.randomquranayahs.data.remote

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * A sealed class representing the result of a network operation.
 * Handles all network-related errors and null pointer scenarios.
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val error: NetworkError) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = (this as? Success)?.data

    inline fun <R> map(transform: (T) -> R): NetworkResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> Loading
    }

    inline fun onSuccess(action: (T) -> Unit): NetworkResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (NetworkError) -> Unit): NetworkResult<T> {
        if (this is Error) action(error)
        return this
    }

    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (NetworkError) -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(error)
        is Loading -> throw IllegalStateException("Cannot fold Loading state")
    }
}

/**
 * Sealed class representing different types of network errors.
 */
sealed class NetworkError(
    open val message: String,
    open val cause: Throwable? = null
) {
    /** No internet connection available */
    data class NoConnection(
        override val message: String = "No internet connection. Please check your network settings.",
        override val cause: Throwable? = null
    ) : NetworkError(message, cause)

    /** Request timed out */
    data class Timeout(
        override val message: String = "Request timed out. Please try again.",
        override val cause: Throwable? = null
    ) : NetworkError(message, cause)

    /** Server returned an error response */
    data class ServerError(
        val code: Int,
        override val message: String = "Server error occurred. Please try again later.",
        override val cause: Throwable? = null
    ) : NetworkError(message, cause)

    /** Response body was null or empty */
    data class EmptyResponse(
        override val message: String = "Empty response received from server.",
        override val cause: Throwable? = null
    ) : NetworkError(message, cause)

    /** Failed to parse the response */
    data class ParseError(
        override val message: String = "Failed to parse server response.",
        override val cause: Throwable? = null
    ) : NetworkError(message, cause)

    /** Unknown or unexpected error */
    data class Unknown(
        override val message: String = "An unexpected error occurred.",
        override val cause: Throwable? = null
    ) : NetworkError(message, cause)

    val isRetryable: Boolean
        get() = when (this) {
            is NoConnection, is Timeout, is ServerError -> true
            is EmptyResponse, is ParseError, is Unknown -> false
        }
}

/**
 * Safely executes a network call and wraps the result in NetworkResult.
 * Handles all common network exceptions and null responses.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T?): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response != null) {
            NetworkResult.Success(response)
        } else {
            NetworkResult.Error(NetworkError.EmptyResponse())
        }
    } catch (e: Exception) {
        NetworkResult.Error(e.toNetworkError())
    }
}

/**
 * Extension function to convert exceptions to NetworkError.
 */
fun Throwable.toNetworkError(): NetworkError = when (this) {
    is UnknownHostException -> NetworkError.NoConnection(cause = this)
    is IOException -> {
        if (message?.contains("Unable to resolve host") == true) {
            NetworkError.NoConnection(cause = this)
        } else {
            NetworkError.Unknown(message = message ?: "Network error occurred", cause = this)
        }
    }
    is SocketTimeoutException -> NetworkError.Timeout(cause = this)
    is HttpException -> {
        val errorMessage = when (code()) {
            400 -> "Bad request"
            401 -> "Unauthorized"
            403 -> "Forbidden"
            404 -> "Resource not found"
            500 -> "Internal server error"
            502 -> "Bad gateway"
            503 -> "Service unavailable"
            else -> "Server error (${code()})"
        }
        NetworkError.ServerError(code = code(), message = errorMessage, cause = this)
    }
    is NullPointerException -> NetworkError.EmptyResponse(
        message = "Received null data from server",
        cause = this
    )
    is kotlinx.serialization.SerializationException,
    is com.google.gson.JsonSyntaxException,
    is com.google.gson.JsonParseException -> NetworkError.ParseError(cause = this)
    else -> NetworkError.Unknown(message = message ?: "Unknown error occurred", cause = this)
}
