package utils

import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier

object LoggingConfig {
    var isDebugMode: Boolean = true
    var logLevel: LogLevel = LogLevel.DEBUG
    
    fun logUseCase(
        useCaseName: String,
        message: String,
        level: LogLevel = LogLevel.DEBUG,
        throwable: Throwable? = null
    ) {
        if (isDebugMode || level >= LogLevel.WARNING) {
            Napier.log(
                priority = level,
                tag = "UseCase",
                throwable = throwable,
                message = "[$useCaseName] $message"
            )
        }
    }
    
    fun logNetwork(
        endpoint: String,
        message: String,
        level: LogLevel = LogLevel.DEBUG,
        throwable: Throwable? = null
    ) {
        if (isDebugMode || level >= LogLevel.WARNING) {
            Napier.log(
                priority = level,
                tag = "Network",
                throwable = throwable,
                message = "[$endpoint] $message"
            )
        }
    }
}