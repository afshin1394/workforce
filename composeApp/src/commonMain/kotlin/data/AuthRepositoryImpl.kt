package data

import data.network.LoginRequestNetwork
import data.network.response.LoginResponseNetwork
import domain.repository.IAuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import presentation.screens.auth.viewmodel.LoginScreenVM

class AuthRepositoryImpl(
    private val httpClient: HttpClient
) : IAuthRepository {
    override suspend fun login(loginRequestNetwork: LoginRequestNetwork)    {
        httpClient.post("auth/token/2fa/login"){
            setBody(loginRequestNetwork)
        }
    }
}