package domain.usecase


import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.util.reflect.Type
import io.ktor.utils.io.errors.IOException
import irancell.nwg.wfm.SentryLog
import kotlinx.coroutines.flow.flow
import utils.AsyncResult

abstract class BaseUseCase<out Type, in Params> {
    abstract suspend fun run(params: Params) : Type
    suspend operator fun invoke(params: Params) = flow {
        emit(AsyncResult.Loading(null, isLoading = true))
        try {
            val result = run(params)
            Napier.log(LogLevel.INFO, "BaseUseCase", message = "done")
            emit(AsyncResult.Success(result, ResultStatus.SUCCESS))
        } catch (exception: Exception) {
            exception.message?.let {
                Napier.log(LogLevel.INFO, "BaseUseCase", message = it)
                val resultStatus = handleError(exception)
                Napier.log(LogLevel.INFO, "BaseUseCase", message = resultStatus.toString())
                emit(AsyncResult.Error(it, resultStatus))
                SentryLog(exception.stackTraceToString())
            } ?: run {
                emit(AsyncResult.Error("no message", handleError(exception)))
            }
        }
    }

    private fun handleError(exception: Exception): ResultStatus {
        when (exception) {
            is RedirectResponseException -> {
                return ResultStatus.REDIRECT_EXCEPTION
            }

            is ClientRequestException -> {
                return ResultStatus.CLIENT_EXCEPTION
            }

            is ServerResponseException -> {
                return ResultStatus.SERVER_EXCEPTION
            }

            is ConnectTimeoutException -> {
                return ResultStatus.TIME_OUT
            }

            is SocketTimeoutException -> {
                return ResultStatus.TIME_OUT
            }

            is HttpRequestTimeoutException -> {
                return ResultStatus.TIME_OUT
            }

            is IOException -> {
                return ResultStatus.IO_EXCEPTION
            }

            else -> {
                return ResultStatus.EXCEPTION
            }
        }

    }
}

sealed class ResultStatus() {
    data object SUCCESS : ResultStatus()
    data object REDIRECT_EXCEPTION : ResultStatus()
    data object SERVER_EXCEPTION : ResultStatus()
    data object CLIENT_EXCEPTION : ResultStatus()
    data object TIME_OUT : ResultStatus()
    data object IO_EXCEPTION : ResultStatus()
    data object EXCEPTION : ResultStatus()

}