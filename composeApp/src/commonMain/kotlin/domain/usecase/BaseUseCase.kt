package domain.usecase


import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.BadGateway
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.GatewayTimeout
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.MethodNotAllowed
import io.ktor.http.HttpStatusCode.Companion.NotAcceptable
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.NotImplemented
import io.ktor.http.HttpStatusCode.Companion.PaymentRequired
import io.ktor.http.HttpStatusCode.Companion.ServiceUnavailable
import io.ktor.http.HttpStatusCode.Companion.TooManyRequests
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.utils.io.errors.IOException
import irancell.nwg.wfm.SentryLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import utils.AsyncResult
import utils.BASE_USECASE
import utils.BASE_USECASE.INITIAL_RETRY_DELAY
import utils.BASE_USECASE.MAX_RETRY_COUNT

abstract class BaseUseCase<out Type, in Params> {

    abstract suspend fun run(params: Params): Type
    suspend operator fun invoke(params: Params) = flow {
        emit(AsyncResult.Loading(null, isLoading = true))

        try {
            val result = run(params)
            println("stepsYO   ${"start"}")

            if (result is List<*> && result.isEmpty()) {
                Napier.log(
                    LogLevel.ASSERT,
                    tag = "BaseUseCase",
                    message = "BaseUseCase ${(result as List<*>).size}"
                )
                emit(AsyncResult.Empty(null, false))
            } else {
                println("stepsYO   ${"Success"}")
                emit(AsyncResult.Success(result, ResultStatus.SUCCESS))
            }
        } catch (exception: Exception) {
            throw exception // Rethrow exception to be caught by retry or catch
        }
    }
        .retry(retries = MAX_RETRY_COUNT.toLong()) { cause ->
            println("stepsYO   ${"retry"}")
            if (cause is Exception) {
                Napier.log(LogLevel.ASSERT, tag = "UnitOfWork", message = "ERROR ${cause.message}")
                delay(INITIAL_RETRY_DELAY) // Wait for 4000 ms before retrying
                true // Continue retrying
            } else {
                false // Stop retrying if it's not an exception
            }
        }
        .catch { exception ->
            println("stepsYO   ${"catch"}")
            val resultStatus = (exception as? Exception)?.handleError() ?: ResultStatus.EXCEPTION
            emit(AsyncResult.Error(exception.message ?: "no message", resultStatus))
            SentryLog(exception.stackTraceToString() ?: "no stack trace")
        }



    private fun Exception.handleError(): ResultStatus {
        when (this) {
            is RedirectResponseException -> {
                return ResultStatus.REDIRECT_EXCEPTION
            }

            is ClientRequestException -> {

                return this.handle()
            }

            is ServerResponseException -> {
                return this.handle()
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

private fun ClientRequestException.handle(): ResultStatus {
    when (this.response.status) {
        BadRequest -> {
         return ResultStatus.CLIENT_EXCEPTION.BAD_REQUEST
        }

        Unauthorized -> {
          return ResultStatus.CLIENT_EXCEPTION.UNATHORIZED
        }

        PaymentRequired -> {
          return ResultStatus.CLIENT_EXCEPTION.PAYMENTREQUIRED
        }

        Forbidden -> {
          return ResultStatus.CLIENT_EXCEPTION.FORBIDDEN
        }

        NotFound -> {
            return ResultStatus.CLIENT_EXCEPTION.NOT_FOUND
        }

        MethodNotAllowed -> {
            return ResultStatus.CLIENT_EXCEPTION.METHOD_NOT_ALLOWED

        }

        NotAcceptable -> {
            return ResultStatus.CLIENT_EXCEPTION.NOT_ACCEPTABLE

        }
        TooManyRequests->{
            return ResultStatus.CLIENT_EXCEPTION.TOO_MANY_REQUESTS
        }
        else->{
            return ResultStatus.CLIENT_EXCEPTION.DEFAULT_CLIENT_EXCEPTION
        }


    }
}

private fun ServerResponseException.handle() : ResultStatus{
    when(this.response.status) {


        InternalServerError -> {
            return ResultStatus.SERVER_EXCEPTION.INTERNAL_SERVER_ERROR
        }

        NotImplemented -> {
            return ResultStatus.SERVER_EXCEPTION.NOT_IMPLEMENTED
        }

        BadGateway -> {
            return ResultStatus.SERVER_EXCEPTION.BAD_GATEWAY
        }

        ServiceUnavailable -> {
            return ResultStatus.SERVER_EXCEPTION.SERVICE_UNAVAILABLE
        }

        GatewayTimeout -> {
            return ResultStatus.SERVER_EXCEPTION.GATEWAY_TIMEOUT

        }


        else -> {
            return ResultStatus.SERVER_EXCEPTION.DEFAULT_SERVER_EXCEPTION
        }
    }
}
sealed class ResultStatus() {
    data object SUCCESS : ResultStatus()
    data object REDIRECT_EXCEPTION : ResultStatus()
    sealed class SERVER_EXCEPTION : ResultStatus(){
        data object  INTERNAL_SERVER_ERROR : SERVER_EXCEPTION()
        data object  NOT_IMPLEMENTED : SERVER_EXCEPTION()
        data object  BAD_GATEWAY : SERVER_EXCEPTION()
        data object  SERVICE_UNAVAILABLE : SERVER_EXCEPTION()
        data object  GATEWAY_TIMEOUT : SERVER_EXCEPTION()
        data object  DEFAULT_SERVER_EXCEPTION : SERVER_EXCEPTION()

    }
    sealed class CLIENT_EXCEPTION : ResultStatus(){
        data object  BAD_REQUEST : CLIENT_EXCEPTION()
        data object  UNATHORIZED : CLIENT_EXCEPTION()
        data object  PAYMENTREQUIRED : CLIENT_EXCEPTION()
        data object  FORBIDDEN : CLIENT_EXCEPTION()
        data object  NOT_FOUND : CLIENT_EXCEPTION()
        data object  METHOD_NOT_ALLOWED : CLIENT_EXCEPTION()
        data object  NOT_ACCEPTABLE : CLIENT_EXCEPTION()
        data object  TOO_MANY_REQUESTS : CLIENT_EXCEPTION()
        data object DEFAULT_CLIENT_EXCEPTION :  CLIENT_EXCEPTION()
    }

    data object TIME_OUT : ResultStatus()
    data object IO_EXCEPTION : ResultStatus()
    data object EXCEPTION : ResultStatus()

}