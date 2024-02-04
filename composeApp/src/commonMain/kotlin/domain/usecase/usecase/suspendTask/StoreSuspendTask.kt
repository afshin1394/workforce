package domain.usecase.usecase.suspendTask

import domain.mappers.toSuspendTaskEntity
import domain.models.SuspendTaskDomain
import domain.repository.ISuspendTaskRepository
import domain.usecase.BaseUseCase

class StoreSuspendTask(
    private val suspendTicketRepository: ISuspendTaskRepository
) : BaseUseCase<Unit, SuspendTaskDomain>() {
    override suspend fun run(params: SuspendTaskDomain) {
        suspendTicketRepository.insert(params.toSuspendTaskEntity())
    }
}