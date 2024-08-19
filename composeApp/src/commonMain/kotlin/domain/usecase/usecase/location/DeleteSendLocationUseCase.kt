package domain.usecase.usecase.location

import domain.repository.IGeneralLocationRepository
import domain.usecase.BaseUseCase

class DeleteSendLocationUseCase (
    private val iGeneralLocationRepository: IGeneralLocationRepository
): BaseUseCase<Unit, Unit>() {

    override suspend fun run(params: Unit) {
        return iGeneralLocationRepository.deleteSent()
    }
}