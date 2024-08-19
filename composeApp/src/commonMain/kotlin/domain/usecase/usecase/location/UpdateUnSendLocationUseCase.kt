package domain.usecase.usecase.location

import database.entity.GeneralLocationEntity
import domain.repository.IGeneralLocationRepository
import domain.usecase.BaseUseCase

class UpdateUnSendLocationUseCase(
    private val iGeneralLocationRepository: IGeneralLocationRepository
): BaseUseCase<Unit, Unit>() {

    override suspend fun run(params: Unit) {
        return iGeneralLocationRepository.updateUnSend()
    }
}