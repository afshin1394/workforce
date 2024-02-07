package domain.usecase.usecase.auth

import data.AuthRepositoryImpl
import domain.mappers.toVerifyNetworkRequest
import domain.mappers.toVerifyResponseDomain
import domain.models.VerifyRequestDomain
import domain.models.VerifyResponseDomain
import domain.usecase.BaseUseCase
import irancell.nwg.wfm.getSharedPref
import utils.SessionId
import utils.Token

class VerifyUseCase(
    private val authRepositoryImpl: AuthRepositoryImpl
) : BaseUseCase<Unit, String>() {
    override suspend fun run(otpCode: String) {
        val verifyResponseDomain = authRepositoryImpl.verify(
            VerifyRequestDomain(
                session_id = getSharedPref().getString(
                    SessionId
                ).toString(), code = otpCode
            ).toVerifyNetworkRequest()
        ).toVerifyResponseDomain()
        getSharedPref().put(Token, verifyResponseDomain.auth_token)
    }

}