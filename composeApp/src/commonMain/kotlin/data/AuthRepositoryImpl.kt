package data

import data.network.request.LoginRequestNetwork
import domain.repository.IAuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthRepositoryImpl(
    private val httpClient: HttpClient
) : IAuthRepository {
    override suspend fun login(loginRequestNetwork: LoginRequestNetwork)    {
        httpClient.post("auth/token/2fa/login"){
            setBody(loginRequestNetwork)
        }
    }
}