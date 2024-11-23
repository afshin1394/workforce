package domain.usecase.usecase.auth

import domain.repository.IAuthRepository
import domain.usecase.BaseUseCase
import irancell.nwg.wfm.BackgroundServiceApp
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.getSharedPref
import irancell.nwg.wfm.provideAppContext
import utils.Language
import utils.UpdateTaskListTypes
import utils.UpdateType

class AutoLogoutUseCase(private val iAuthRepository: IAuthRepository) : BaseUseCase<Unit, Unit>() {
    override suspend fun run(params: Unit) {
        val language= getSharedPref().getString(Language)
        val typeUpdate= getSharedPref().getString(UpdateType)
        getSharedPref().deleteAll()
        getSharedPref().put(Language,language?:"en")
        getSharedPref().put(UpdateType,typeUpdate?: UpdateTaskListTypes.Auto)
        iAuthRepository.deleteAllTableDB()
        InternalStorage.clearCache(provideAppContext())
    }
}