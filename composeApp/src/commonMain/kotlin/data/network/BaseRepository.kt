package data.network

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.json.Json
import utils.LoggingConfig

abstract class BaseRepository {
    
    protected val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
    
    protected suspend inline fun <reified T> safeApiCall(
        apiCall: () -> HttpResponse
    ): NetworkResult<T> {
        return try {
            val response = apiCall()
            val statusCode = response.status.value
            
            LoggingConfig.logNetwork(
                endpoint = response.request.url.toString(),
                message = "Response: $statusCode"
            )
            
            when {
                response.status.isSuccess() -> {
                    val body = response.body<T>()
                    NetworkResult.Success(body, statusCode)
                }
                else -> {
                    val errorBody = response.bodyAsText()
                    NetworkResult.Error(
                        NetworkException.ApiException(
                            message = parseErrorMessage(errorBody),
                            code = statusCode,
                            responseBody = errorBody
                        )
                    )
                }
            }
        } catch (e: ClientRequestException) {
            handleClientException(e)
        } catch (e: ServerResponseException) {
            handleServerException(e)
        } catch (e: IOException) {
            NetworkResult.Error(
                NetworkException.NetworkConnectionException(
                    throwable = e
                )
            )
        } catch (e: TimeoutCancellationException) {
            NetworkResult.Error(
                NetworkException.TimeoutException(
                    throwable = e
                )
            )
        } catch (e: Exception) {
            Napier.e("Unknown error", e)
            NetworkResult.Error(
                NetworkException.UnknownException(
                    message = e.message ?: "Unknown error",
                    throwable = e
                )
            )
        }
    }
    
    protected suspend inline fun <reified T> HttpClient.getWithResult(
        urlString: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): NetworkResult<T> = safeApiCall {
        get(urlString) {
            contentType(ContentType.Application.Json)
            block()
        }
    }
    
    protected suspend inline fun <reified T> HttpClient.postWithResult(
        urlString: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): NetworkResult<T> = safeApiCall {
        post(urlString) {
            contentType(ContentType.Application.Json)
            block()
        }
    }
    
    protected suspend inline fun <reified T> HttpClient.putWithResult(
        urlString: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): NetworkResult<T> = safeApiCall {
        put(urlString) {
            contentType(ContentType.Application.Json)
            block()
        }
    }
    
    protected suspend inline fun <reified T> HttpClient.deleteWithResult(
        urlString: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): NetworkResult<T> = safeApiCall {
        delete(urlString) {
            contentType(ContentType.Application.Json)
            block()
        }
    }
    
    protected suspend fun handleClientException(e: ClientRequestException): NetworkResult.Error {
        return when (e.response.status) {
            HttpStatusCode.Unauthorized -> NetworkResult.Error(
                NetworkException.UnauthorizedException()
            )
            HttpStatusCode.BadRequest -> {
                val errorBody = try {
                    e.response.bodyAsText()
                } catch (ex: Exception) {
                    "Unable to read error response"
                }
                NetworkResult.Error(
                    NetworkException.ApiException(
                        message = parseErrorMessage(errorBody),
                        code = 400,
                        responseBody = errorBody
                    )
                )
            }
            else -> {
                val errorBody = try {
                    e.response.bodyAsText()
                } catch (ex: Exception) {
                    null
                }
                NetworkResult.Error(
                    NetworkException.ApiException(
                        message = errorBody?.let { parseErrorMessage(it) } ?: e.message,
                        code = e.response.status.value,
                        responseBody = errorBody
                    )
                )
            }
        }
    }
    
    protected suspend fun handleServerException(e: ServerResponseException): NetworkResult.Error {
        val errorBody = try {
            e.response.bodyAsText()
        } catch (ex: Exception) {
            "Unable to read error response"
        }
        
        return NetworkResult.Error(
            NetworkException.ServerException(
                message = parseErrorMessage(errorBody).takeIf { it != "Request failed" } 
                    ?: "Server error: ${e.response.status.description}",
                code = e.response.status.value,
                throwable = e
            )
        )
    }
    
    protected fun parseErrorMessage(errorBody: String): String {
        return try {
            val regex = """"detail"\s*:\s*"([^"]+)"""".toRegex()
            regex.find(errorBody)?.groupValues?.get(1) ?: "Request failed"
        } catch (e: Exception) {
            "Request failed"
        }
    }
}

// Extension function to check if status is successful
fun HttpStatusCode.isSuccess(): Boolean = value in 200..299