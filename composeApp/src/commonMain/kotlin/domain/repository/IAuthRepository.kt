package domain.repository

import data.network.request.LoginNetworkRequest
import data.network.request.VerifyNetworkRequest
import data.network.response.LoginNetworkResponse
import data.network.response.VerifyNetworkResponse

interface IAuthRepository {

    suspend fun login(loginNetworkRequest: LoginNetworkRequest) : LoginNetworkResponse

    suspend fun verify(verifyNetworkRequest: VerifyNetworkRequest) : VerifyNetworkResponse
}