package data

import data.network.BaseRepository
import data.network.NetworkResult
import data.network.addAuthHeader
import data.network.addStandardHeaders
import data.network.request.auth.LoginNetworkRequest
import data.network.request.auth.ResendNetworkRequest
import data.network.request.auth.VerifyNetworkRequest
import data.network.response.ObjectID
import data.network.response.auth.LoginNetworkResponse
import data.network.response.auth.VerifyNetworkResponse
import database.AppDatabase
import domain.repository.IAuthRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import utils.LoggingConfig

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val httpClientWithToken: HttpClient,
    private val db: AppDatabase
) : BaseRepository(), IAuthRepository {
    override suspend fun login(loginNetworkRequest: LoginNetworkRequest): LoginNetworkResponse {
        LoggingConfig.logNetwork("login", "Attempting login for phone: ${loginNetworkRequest.username}")
        
        return when (val result = httpClient.postWithResult<LoginNetworkResponse>("auth/token/2fa/login") {
            setBody(loginNetworkRequest)
            addStandardHeaders()
        }) {
            is NetworkResult.Success -> {
                LoggingConfig.logNetwork("login", "Login successful, sessionId: ${result.data.session_id}")
                result.data
            }
            is NetworkResult.Error -> {
                Napier.e("Login failed: ${result.exception.message}", result.exception)
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Unexpected loading state")
        }
    }

    override suspend fun verify(verifyNetworkRequest: VerifyNetworkRequest): VerifyNetworkResponse {
        LoggingConfig.logNetwork("verify", "Verifying session: ${verifyNetworkRequest.session_id}")
        
        return when (val result = httpClient.postWithResult<VerifyNetworkResponse>("auth/token/2fa/verify") {
            setBody(verifyNetworkRequest)
            addStandardHeaders()
        }) {
            is NetworkResult.Success -> {
                LoggingConfig.logNetwork("verify", "Verification successful")
                result.data
            }
            is NetworkResult.Error -> {
                Napier.e("Verification failed: ${result.exception.message}", result.exception)
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Unexpected loading state")
        }
    }

    override suspend fun resend(resendNetworkRequest: ResendNetworkRequest) {
        LoggingConfig.logNetwork("resend", "Resending code for session: ${resendNetworkRequest.session_id}")
        
        when (val result = httpClient.postWithResult<Unit>("auth/token/2fa/resend/") {
            setBody(resendNetworkRequest)
            addStandardHeaders()
        }) {
            is NetworkResult.Success -> {
                LoggingConfig.logNetwork("resend", "Resend successful")
            }
            is NetworkResult.Error -> {
                Napier.e("Resend failed: ${result.exception.message}", result.exception)
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Unexpected loading state")
        }
    }

    override suspend fun logout(): HttpStatusCode {
        LoggingConfig.logNetwork("logout", "Logging out user")
        
        return when (val result = httpClientWithToken.getWithResult<Unit>("auth/token/logout") {
            addAuthHeader()
            addStandardHeaders()
        }) {
            is NetworkResult.Success -> {
                LoggingConfig.logNetwork("logout", "Logout successful")
                HttpStatusCode.OK
            }
            is NetworkResult.Error -> {
                Napier.e("Logout failed: ${result.exception.message}", result.exception)
                // Return the actual status code if available, otherwise default
                HttpStatusCode.fromValue(result.exception.code ?: 500)
            }
            is NetworkResult.Loading -> HttpStatusCode.Processing
        }
    }

    override suspend fun deleteAllTableDB() {
        try {
            LoggingConfig.logUseCase("AuthRepository", "Deleting all database tables")
            db.deleteAllTableDao().deleteAllData()
        } catch (e: Exception) {
            Napier.e("Failed to delete all database tables", e)
            throw e
        }
    }
}