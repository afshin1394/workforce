package domain.usecase.usecase.auth

import domain.repository.IAuthRepository
import domain.usecase.BaseUseCase
import irancell.nwg.wfm.BackgroundServiceApp
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.getSharedPref
import irancell.nwg.wfm.provideAppContext

class AutoLogoutUseCase(private val iAuthRepository: IAuthRepository) : BaseUseCase<Unit, Unit>() {
    override suspend fun run(params: Unit) {
        getSharedPref().deleteAll()
        iAuthRepository.deleteAllTableDB()
        InternalStorage.clearCache(provideAppContext())
    }
}