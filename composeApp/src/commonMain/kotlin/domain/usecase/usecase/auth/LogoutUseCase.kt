package domain.usecase.usecase.auth

import domain.repository.IAuthRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.ktor.http.HttpStatusCode
import irancell.nwg.wfm.getSharedPref


class LogoutUseCase(
    private val iAuthRepository: IAuthRepository
) : BaseUseCase<Unit, Unit>() {
    override suspend fun run(params: Unit) {
        Napier.log(LogLevel.ASSERT,"Logout", message = "")

       val pair =  iAuthRepository.logout()
        if(pair.first == HttpStatusCode.OK){
            iAuthRepository.deleteAllTableDB()
            getSharedPref().deleteAll()

        }

    }


}