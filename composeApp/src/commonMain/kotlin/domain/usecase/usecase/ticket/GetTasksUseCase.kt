package domain.usecase.usecase.ticket

import domain.mappers.toTaskDomainList
import domain.models.task.TaskDomain
import domain.repository.IInitialFormRepository
import domain.repository.ITaskRepository
import domain.usecase.BaseUseCase

class GetTasksUseCase(
    private val iTaskRepository: ITaskRepository,
) : BaseUseCase<List<TaskDomain>, Unit>() {
    override suspend fun run(params: Unit): List<TaskDomain> {
        return iTaskRepository.getAll().toTaskDomainList()
    }
}