package domain.repository

import data.network.request.LoginRequestNetwork

interface IAuthRepository {

    suspend fun login(loginRequestNetwork: LoginRequestNetwork)
}