package data.network

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T, val statusCode: Int = 200) : NetworkResult<T>()
    data class Error(val exception: NetworkException) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

sealed class NetworkException(
    override val message: String,
    val code: Int? = null,
    val throwable: Throwable? = null
) : Exception(message, throwable) {
    
    class ApiException(
        message: String,
        code: Int,
        val responseBody: String? = null
    ) : NetworkException(message, code)
    
    class NetworkConnectionException(
        message: String = "No internet connection",
        throwable: Throwable? = null
    ) : NetworkException(message, throwable = throwable)
    
    class TimeoutException(
        message: String = "Request timed out",
        throwable: Throwable? = null
    ) : NetworkException(message, throwable = throwable)
    
    class UnauthorizedException(
        message: String = "Unauthorized access",
        code: Int = 401
    ) : NetworkException(message, code)
    
    class ServerException(
        message: String = "Server error",
        code: Int = 500,
        throwable: Throwable? = null
    ) : NetworkException(message, code, throwable)
    
    class UnknownException(
        message: String = "Unknown error occurred",
        throwable: Throwable? = null
    ) : NetworkException(message, throwable = throwable)
}