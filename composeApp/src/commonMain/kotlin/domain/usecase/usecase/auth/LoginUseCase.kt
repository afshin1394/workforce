package domain.usecase.usecase.auth

import data.AuthRepositoryImpl
import domain.mappers.toLoginRequestNetwork
import domain.models.LoginRequestDomain
import domain.usecase.BaseUseCase

class LoginUseCase(
  private val authRepositoryImpl: AuthRepositoryImpl
) : BaseUseCase<Unit, LoginRequestDomain>() {
    override suspend fun run(params: LoginRequestDomain) {
        return authRepositoryImpl.login(params.toLoginRequestNetwork())
    }
}