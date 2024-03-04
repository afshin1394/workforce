package domain.usecase.usecase.suspendTask

import domain.repository.ISuspendTaskRepository
import domain.usecase.BaseUseCase

class DeleteByTaskIdUseCase(
    private val iSuspendTaskRepository: ISuspendTaskRepository
) : BaseUseCase<Unit,Long>() {
    override suspend fun run(params: Long) {
        iSuspendTaskRepository.deleteByTaskId(params)
    }
}