package domain.usecase.usecase.auth

import data.AuthRepositoryImpl
import domain.mappers.toVerifyNetworkRequest
import domain.mappers.toVerifyResponseDomain
import domain.models.VerifyRequestDomain
import domain.models.VerifyResponseDomain
import domain.repository.IAuthRepository
import domain.usecase.BaseUseCase
import irancell.nwg.wfm.getSharedPref
import utils.SessionId
import utils.Token

class VerifyUseCase(
    private val iAuthRepository: IAuthRepository
) : BaseUseCase<String, String>() {
    override suspend fun run(otpCode: String) : String {
        val verifyResponseDomain = iAuthRepository.verify(
            VerifyRequestDomain(
                session_id = getSharedPref().getString(
                    SessionId
                ).toString(), code = otpCode
            ).toVerifyNetworkRequest()
        ).toVerifyResponseDomain()
        getSharedPref().put(Token, verifyResponseDomain.auth_token)
        return verifyResponseDomain.auth_token
    }

}