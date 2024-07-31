package domain.usecase.usecase.steps

import domain.repository.IStepPointerRepository
import domain.usecase.BaseUseCase

class CheckForEditedTicketUseCase(private val iStepPointerRepository: IStepPointerRepository) :
    BaseUseCase<Boolean, String>() {
    override suspend fun run(params: String): Boolean {
      return  iStepPointerRepository.checkIfTicketIsEdited(params)
    }
}