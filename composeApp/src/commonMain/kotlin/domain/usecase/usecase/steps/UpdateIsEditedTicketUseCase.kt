package domain.usecase.usecase.steps

import domain.repository.IStepPointerRepository
import domain.usecase.BaseUseCase

class UpdateIsEditedTicketUseCase(private val iStepPointerRepository: IStepPointerRepository) : BaseUseCase<Unit, Pair<String, Boolean>>() {
    override suspend fun run(params: Pair<String, Boolean>) {
        iStepPointerRepository.updateIsEdited(params.first,params.second)
    }
}