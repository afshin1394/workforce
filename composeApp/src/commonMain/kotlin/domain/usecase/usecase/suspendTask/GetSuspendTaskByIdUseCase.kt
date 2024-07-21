package domain.usecase.usecase.suspendTask

import domain.mappers.toSuspendTaskDomain
import domain.models.SuspendTaskDomain
import domain.repository.ISuspendTaskRepository
import domain.usecase.BaseUseCase

class GetSuspendTaskByIdUseCase(
    private val iSuspendTaskRepository: ISuspendTaskRepository
) : BaseUseCase<SuspendTaskDomain,String>() {
    override suspend fun run(params: String): SuspendTaskDomain {
       return iSuspendTaskRepository.selectByTaskId(params).toSuspendTaskDomain()
    }
}