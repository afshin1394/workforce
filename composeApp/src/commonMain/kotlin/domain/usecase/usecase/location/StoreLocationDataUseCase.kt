package domain.usecase.usecase.location

import data.GeneralLocationRepositoryImpl
import database.entity.GeneralLocationEntity
import domain.repository.IGeneralLocationRepository
import domain.usecase.BaseUseCase


class StoreLocationDataUseCase(
    private val iGeneralLocationRepository: IGeneralLocationRepository) :
    BaseUseCase<Unit, GeneralLocationEntity>() {
    override suspend fun run(params: GeneralLocationEntity) {
        iGeneralLocationRepository.insert(params)
    }
}