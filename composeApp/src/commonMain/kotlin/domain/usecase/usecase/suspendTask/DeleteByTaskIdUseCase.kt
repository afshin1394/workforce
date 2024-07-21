package domain.usecase.usecase.suspendTask

import domain.repository.ISuspendTaskRepository
import domain.usecase.BaseUseCase

class DeleteByTaskIdUseCase(
    private val iSuspendTaskRepository: ISuspendTaskRepository
) : BaseUseCase<Unit,String>() {
    override suspend fun run(params: String) {
        iSuspendTaskRepository.deleteByTaskId(params)
    }
}