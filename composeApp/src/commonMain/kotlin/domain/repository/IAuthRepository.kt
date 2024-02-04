package domain.repository

import data.network.LoginRequestNetwork
import data.network.response.LoginResponseNetwork

interface IAuthRepository {

    suspend fun login(loginRequestNetwork: LoginRequestNetwork)
}