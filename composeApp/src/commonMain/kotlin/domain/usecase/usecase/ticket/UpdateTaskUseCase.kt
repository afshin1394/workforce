package domain.usecase.usecase.ticket

import data.network.response.task.FormStruct
import database.entity.InitialFormEntity
import domain.mappers.toTaskDomainList
import domain.mappers.toTaskEntityList
import domain.repository.IInitialFormRepository
import domain.repository.ITaskRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json

class UpdateTaskUseCase(
    private val iTaskRepository: ITaskRepository,
    private val iInitialFormRepository: IInitialFormRepository
) : BaseUseCase<Unit, Unit>() {
    override suspend fun run(params: Unit) {
        val tasks = iTaskRepository.fetchWorks()
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = tasks.toString())
        val initialTasks = arrayListOf<InitialFormEntity>()
        tasks.details.forEach {
            it.initial_form?.let { initialForm ->
                initialTasks.add(
                    InitialFormEntity(
                        it.basic_info.ticket_number ?: "",
                        it.initial_form
                    )
                )
            }
        }
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = initialTasks.toString())

        iInitialFormRepository.deleteAll()
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "deleteAll")

        iInitialFormRepository.resetEntitySequence()
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "resetEntitySequence")

        iInitialFormRepository.insertAll(initialTasks)

        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "insertAll")

        iTaskRepository.deleteAll()
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "deleteAll")

        iTaskRepository.resetEntitySequence()
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "resetEntitySequence")

        iTaskRepository.insertAll(tasks.details.toTaskEntityList())
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "insertAll")

        val domainList = iTaskRepository.getAll().toTaskDomainList()
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "domainList")

        Napier.log(LogLevel.ASSERT, tag = "domainList", message = domainList.toString())
    }
}