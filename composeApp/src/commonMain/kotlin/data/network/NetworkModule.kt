package data.network

import io.github.aakira.napier.Napier
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import irancell.nwg.wfm.getSharedPref
import utils.Token
import kotlinx.datetime.Clock
import com.benasher44.uuid.uuid4

/**
 * Network extensions for better API call handling
 */

fun HttpRequestBuilder.addStandardHeaders() {
    header("X-Request-ID", uuid4().toString())
    header("X-Timestamp", Clock.System.now().epochSeconds.toString())
    header(HttpHeaders.UserAgent, "WorkforceApp/1.0")
}

fun HttpRequestBuilder.addAuthHeader() {
    val token = getSharedPref().getString(Token)
    if (!token.isNullOrEmpty()) {
        header(HttpHeaders.Authorization, "Token $token")
    }
}

/**
 * Logging configuration for Ktor
 */
object KtorLogger : Logger {
    override fun log(message: String) {
        Napier.d(message, tag = "HTTP")
    }
}

/**
 * Enhanced HTTP client configuration
 */
fun io.ktor.client.HttpClientConfig<*>.configureNetworking() {
    install(Logging) {
        logger = KtorLogger
        level = LogLevel.INFO
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
    }
    
    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 2)
        retryOnException(maxRetries = 2, retryOnTimeout = true)
        exponentialDelay()
        modifyRequest { request ->
            Napier.w("Retrying request to ${request.url}")
        }
    }
}

/**
 * Response wrappers for consistency
 */
data class ApiResponse<T>(
    val data: T?,
    val message: String = "",
    val success: Boolean = true,
    val statusCode: Int = 200
)

/**
 * Standard error response
 */
data class ErrorResponse(
    val error: String,
    val detail: String? = null,
    val timestamp: Long = Clock.System.now().epochSeconds
)