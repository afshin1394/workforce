package domain.usecase.usecase.ticket

import arrow.core.Either
import domain.mappers.toTaskDomainList
import domain.mappers.toTaskEntityList
import domain.models.TaskDomain
import domain.repository.ITaskRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay

class UpdateTasksUseCase(
    private val iTaskRepository: ITaskRepository
) : BaseUseCase<List<TaskDomain>, Unit>() {
    override suspend fun run(params: Unit): List<TaskDomain> {
        val tasks = iTaskRepository.fetchWorks()
        iTaskRepository.insertAll(tasks.toTaskEntityList())
        val domainList = iTaskRepository.getAll().toTaskDomainList()
        Napier.log(LogLevel.ASSERT,tag = "domainList", message =  domainList.toString())
        return iTaskRepository.getAll().toTaskDomainList()
    }
}