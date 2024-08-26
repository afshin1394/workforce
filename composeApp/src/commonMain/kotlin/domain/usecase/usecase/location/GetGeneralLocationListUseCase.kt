package domain.usecase.usecase.location

import database.entity.GeneralLocationEntity
import domain.repository.IGeneralLocationRepository
import domain.usecase.BaseUseCase


class GetGeneralLocationListUseCase(
   private val iGeneralLocationRepository: IGeneralLocationRepository
): BaseUseCase<List<GeneralLocationEntity>, Unit>() {

    override suspend fun run(params: Unit): List<GeneralLocationEntity> {
        return iGeneralLocationRepository.selectUnSend()
    }
}