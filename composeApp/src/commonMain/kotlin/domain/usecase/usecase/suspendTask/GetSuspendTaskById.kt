package domain.usecase.usecase.suspendTask

import domain.mappers.toSuspendTaskDomain
import domain.models.SuspendTaskDomain
import domain.repository.ISuspendTaskRepository
import domain.usecase.BaseUseCase

class GetSuspendTaskById(
    private val suspendTaskRepository: ISuspendTaskRepository
) : BaseUseCase<SuspendTaskDomain,Long>() {
    override suspend fun run(params: Long): SuspendTaskDomain {
       return suspendTaskRepository.selectByTaskId(params).toSuspendTaskDomain()
    }
}