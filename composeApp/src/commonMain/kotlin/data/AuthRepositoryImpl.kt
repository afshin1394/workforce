package data

import data.network.request.LoginNetworkRequest
import data.network.request.VerifyNetworkRequest
import data.network.response.LoginNetworkResponse
import data.network.response.VerifyNetworkResponse
import domain.repository.IAuthRepository
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthRepositoryImpl(
    private val httpClient: HttpClient
) : IAuthRepository {
    override suspend fun login(loginNetworkRequest: LoginNetworkRequest) : LoginNetworkResponse    {
      val response = httpClient.post("auth/token/2fa/login"){
            setBody(loginNetworkRequest)
        }.body<LoginNetworkResponse>()
        Napier.log(LogLevel.ASSERT,"login" ,  message = "sessionId ${response.session_id} phoneNumber: ${response.phone_number}")
      return response
    }

    override suspend fun verify(verifyNetworkRequest: VerifyNetworkRequest ) : VerifyNetworkResponse  {
      val response = httpClient.post("auth/token/2fa/verify"){
            setBody(verifyNetworkRequest)
        }.body<VerifyNetworkResponse>()
        Napier.log(LogLevel.ASSERT,"login" ,  message = "response${response.auth_token}")
        return response
    }
}