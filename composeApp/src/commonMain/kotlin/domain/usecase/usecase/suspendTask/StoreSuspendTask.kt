package domain.usecase.usecase.suspendTask

import domain.mappers.toSuspendTaskEntity
import domain.models.SuspendTaskDomain
import domain.repository.ISuspendTaskRepository
import domain.usecase.BaseUseCase

class StoreSuspendTask(
    private val iSuspendTaskRepository: ISuspendTaskRepository
) : BaseUseCase<Unit, SuspendTaskDomain>() {
    override suspend fun run(params: SuspendTaskDomain) {
        iSuspendTaskRepository.insert(params.toSuspendTaskEntity())
    }
}