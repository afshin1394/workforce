package data

import data.network.request.auth.LoginNetworkRequest
import data.network.request.auth.ResendNetworkRequest
import data.network.request.auth.VerifyNetworkRequest
import data.network.response.ObjectID
import data.network.response.auth.LoginNetworkResponse
import data.network.response.auth.VerifyNetworkResponse
import database.AppDatabase
import domain.repository.IAuthRepository
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val httpClientWithToken: HttpClient,
    private val db: AppDatabase
) : IAuthRepository {
    override suspend fun login(loginNetworkRequest: LoginNetworkRequest) : LoginNetworkResponse {
        val response = httpClient.post("auth/token/2fa/login"){
            setBody(loginNetworkRequest)
        }.body<LoginNetworkResponse>()
        Napier.log(LogLevel.ASSERT,"login" ,  message = "sessionId ${response.session_id} phoneNumber: ${response.phone_number}")
        return response
    }

    override suspend fun verify(verifyNetworkRequest: VerifyNetworkRequest) : VerifyNetworkResponse {
        val response = httpClient.post("auth/token/2fa/verify"){
            setBody(verifyNetworkRequest)
        }.body<VerifyNetworkResponse>()
        Napier.log(LogLevel.ASSERT,"login" ,  message = "response${response.auth_token}")
        return response
    }

    override suspend fun resend(resendNetworkRequest: ResendNetworkRequest) {
        httpClient.post("auth/token/2fa/resend/"){
            setBody(resendNetworkRequest)
        }
        Napier.log(LogLevel.ASSERT,"resend" ,  message = "sessionId ${resendNetworkRequest.session_id}")
    }

    override suspend fun logout(): HttpStatusCode {

        val apiCall = httpClientWithToken.get("auth/token/logout")
        return apiCall.status
    }

    override suspend fun deleteAllTableDB() {
        db.deleteAllTableDao().deleteAllData()
    }
}