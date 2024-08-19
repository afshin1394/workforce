package domain.repository

import data.network.request.auth.LoginNetworkRequest
import data.network.request.auth.ResendNetworkRequest
import data.network.request.auth.VerifyNetworkRequest
import data.network.response.auth.LoginNetworkResponse
import data.network.response.auth.VerifyNetworkResponse
import io.ktor.http.HttpStatusCode

interface IAuthRepository {

    suspend fun login(loginNetworkRequest: LoginNetworkRequest) : LoginNetworkResponse

    suspend fun verify(verifyNetworkRequest: VerifyNetworkRequest) : VerifyNetworkResponse

    suspend fun resend(resendNetworkRequest: ResendNetworkRequest)
    suspend fun logout(): HttpStatusCode
    suspend fun deleteAllTableDB()
}