package domain.usecase.usecase

import data.GeneralLocationRepositoryImpl
import domain.usecase.BaseUseCase
import irancell.nwg.wfm.db.GeneralLocation

class StoreLocationDataUseCase(
    private val generalLocationRepositoryImpl: GeneralLocationRepositoryImpl) :
    BaseUseCase<Unit, GeneralLocation>() {
    override suspend fun run(params: GeneralLocation) {
        generalLocationRepositoryImpl.insert(params)
    }
}