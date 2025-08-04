package utils

import data.network.NetworkException
import data.network.NetworkResult
import domain.usecase.ResultStatus
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Utility functions for network operations
 */

/**
 * Convert NetworkResult to AsyncResult for use case layer
 */
fun <T> NetworkResult<T>.toAsyncResult(): utils.AsyncResult<T> {
    return when (this) {
        is NetworkResult.Success -> utils.AsyncResult.Success(this.data, ResultStatus.SUCCESS)
        is NetworkResult.Error -> utils.AsyncResult.Error(
            this.exception.message,
            this.exception.toResultStatus()
        )
        is NetworkResult.Loading -> utils.AsyncResult.Loading(null, true)
    }
}

/**
 * Convert NetworkException to ResultStatus
 */
private fun NetworkException.toResultStatus(): ResultStatus {
    return when (this) {
        is NetworkException.UnauthorizedException -> ResultStatus.CLIENT_EXCEPTION.UNATHORIZED
        is NetworkException.NetworkConnectionException -> ResultStatus.IO_EXCEPTION
        is NetworkException.TimeoutException -> ResultStatus.TIME_OUT
        is NetworkException.ServerException -> when (this.code) {
            500 -> ResultStatus.SERVER_EXCEPTION.INTERNAL_SERVER_ERROR
            502 -> ResultStatus.SERVER_EXCEPTION.BAD_GATEWAY
            503 -> ResultStatus.SERVER_EXCEPTION.SERVICE_UNAVAILABLE
            504 -> ResultStatus.SERVER_EXCEPTION.GATEWAY_TIMEOUT
            else -> ResultStatus.SERVER_EXCEPTION.DEFAULT_SERVER_EXCEPTION
        }
        is NetworkException.ApiException -> when (this.code) {
            400 -> ResultStatus.CLIENT_EXCEPTION.BAD_REQUEST
            401 -> ResultStatus.CLIENT_EXCEPTION.UNATHORIZED
            403 -> ResultStatus.CLIENT_EXCEPTION.FORBIDDEN
            404 -> ResultStatus.CLIENT_EXCEPTION.NOT_FOUND
            429 -> ResultStatus.CLIENT_EXCEPTION.TOO_MANY_REQUESTS
            else -> ResultStatus.CLIENT_EXCEPTION.DEFAULT_CLIENT_EXCEPTION
        }
        else -> ResultStatus.EXCEPTION
    }
}

/**
 * Flow extension to handle network errors gracefully
 */
fun <T> Flow<NetworkResult<T>>.handleNetworkErrors(): Flow<utils.AsyncResult<T>> {
    return this.map { networkResult ->
        networkResult.toAsyncResult()
    }.catch { throwable ->
        Napier.e("Unexpected error in network flow", throwable)
        emit(utils.AsyncResult.Error("Unexpected error: ${throwable.message}", ResultStatus.EXCEPTION))
    }
}

/**
 * Safe API call wrapper for repositories
 */
suspend fun <T> safeNetworkCall(
    operation: String,
    call: suspend () -> NetworkResult<T>
): NetworkResult<T> {
    return try {
        LoggingConfig.logNetwork(operation, "Starting network call")
        call()
    } catch (e: Exception) {
        Napier.e("Unexpected error in $operation", e)
        NetworkResult.Error(
            NetworkException.UnknownException(
                message = "Unexpected error in $operation: ${e.message}",
                throwable = e
            )
        )
    }
}

/**
 * Create a flow that emits network results with proper error handling
 */
fun <T> networkFlow(
    operation: suspend () -> NetworkResult<T>
): Flow<utils.AsyncResult<T>> = flow {
    emit(utils.AsyncResult.Loading(null, true))
    val result = operation()
    emit(result.toAsyncResult())
}