package domain.usecase.usecase.auth

import data.network.request.auth.ResendNetworkRequest
import domain.repository.IAuthRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.getSharedPref
import utils.SessionId

class ResendUseCase(
    private val iAuthRepository: IAuthRepository
) : BaseUseCase<Unit,Unit>() {
    override suspend fun run(params: Unit) {
        Napier.log(LogLevel.ASSERT,"ResendUseCase", message = getSharedPref().getString(SessionId).toString())
        iAuthRepository.resend(ResendNetworkRequest(getSharedPref().getString(SessionId).toString()))
    }
}