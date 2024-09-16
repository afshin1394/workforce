package domain.usecase.usecase.auth

import data.AuthRepositoryImpl
import domain.mappers.toLoginNetworkRequest
import domain.mappers.toLoginResponseDomain
import domain.models.LoginRequestDomain
import domain.repository.IAuthRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.getSharedPref
import utils.Password
import utils.PhoneNumber
import utils.SessionId
import utils.UserName

class LoginUseCase(
    private val iAuthRepository: IAuthRepository
) : BaseUseCase<Unit, LoginRequestDomain>() {
    override suspend fun run(params: LoginRequestDomain) {
        Napier.log(LogLevel.ASSERT, "email & password", message = params.toString())
        getSharedPref().put(UserName, params.username)
        getSharedPref().put(Password, params.password)

        val loginDomain =
            iAuthRepository.login(params.toLoginNetworkRequest()).toLoginResponseDomain()

        Napier.log(LogLevel.ASSERT, "email & password", message = loginDomain.toString())
        getSharedPref().put(SessionId, loginDomain.session_id)
        loginDomain.phone_number?.let {
            getSharedPref().put(PhoneNumber, loginDomain.phone_number)
        }
    }
}