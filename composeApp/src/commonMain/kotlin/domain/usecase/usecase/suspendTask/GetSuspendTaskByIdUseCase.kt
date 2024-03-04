package domain.usecase.usecase.suspendTask

import domain.mappers.toSuspendTaskDomain
import domain.models.SuspendTaskDomain
import domain.repository.ISuspendTaskRepository
import domain.usecase.BaseUseCase

class GetSuspendTaskByIdUseCase(
    private val iSuspendTaskRepository: ISuspendTaskRepository
) : BaseUseCase<SuspendTaskDomain,Long>() {
    override suspend fun run(params: Long): SuspendTaskDomain {
       return iSuspendTaskRepository.selectByTaskId(params).toSuspendTaskDomain()
    }
}