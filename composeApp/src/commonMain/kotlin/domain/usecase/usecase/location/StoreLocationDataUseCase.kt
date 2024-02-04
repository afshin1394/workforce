package domain.usecase.usecase.location

import data.GeneralLocationRepositoryImpl
import domain.usecase.BaseUseCase
import irancell.nwg.wfm.db.GeneralLocationEntity

class StoreLocationDataUseCase(
    private val generalLocationRepositoryImpl: GeneralLocationRepositoryImpl) :
    BaseUseCase<Unit, GeneralLocationEntity>() {
    override suspend fun run(params: GeneralLocationEntity) {
        generalLocationRepositoryImpl.insert(params)
    }
}