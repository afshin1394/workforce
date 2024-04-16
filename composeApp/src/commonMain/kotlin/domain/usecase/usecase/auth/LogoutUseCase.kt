package domain.usecase.usecase.auth

import domain.repository.IAuthRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier


class LogoutUseCase(
    private val iAuthRepository: IAuthRepository
) : BaseUseCase<Unit, Unit>() {
    override suspend fun run(params: Unit) {

        Napier.log(LogLevel.ASSERT,"Logout", message = "")
        iAuthRepository.logout()
    }


}