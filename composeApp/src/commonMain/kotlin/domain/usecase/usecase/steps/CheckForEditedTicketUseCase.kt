package domain.usecase.usecase.steps

import domain.repository.IStepPointerRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlin.math.tan

class CheckForEditedTicketUseCase(private val iStepPointerRepository: IStepPointerRepository) :
    BaseUseCase<Boolean, String>() {
    override suspend fun run(params: String): Boolean {
      val result = iStepPointerRepository.checkIfTicketIsEdited(params)
      Napier.log(LogLevel.ASSERT, tag = "CheckForEditedTicketUseCase", message = "$result")
      return  result
    }
}