package domain.usecase.usecase.location

import data.GeneralLocationRepositoryImpl
import domain.repository.IGeneralLocationRepository
import domain.usecase.BaseUseCase
import irancell.nwg.wfm.db.GeneralLocationEntity

class StoreLocationDataUseCase(
    private val iGeneralLocationRepository: IGeneralLocationRepository) :
    BaseUseCase<Unit, GeneralLocationEntity>() {
    override suspend fun run(params: GeneralLocationEntity) {
        iGeneralLocationRepository.insert(params)
    }
}